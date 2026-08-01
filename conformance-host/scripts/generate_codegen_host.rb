#!/usr/bin/env ruby
# frozen_string_literal: true

#
# generate_codegen_host.rb — build the CODEGEN side of the conformance host.
#
# The dynamic host renders fixture layouts through DynamicView at runtime;
# production apps ship kjui-GENERATED Compose code. This script makes the
# generated pipeline hostable so `jui conformance parity` can prove
# dynamic ≡ codegen per fixture (same shape as the SwiftJsonUI host's
# scripts/generate_codegen_host.rb and the web host's generate.mjs):
#
#   1. Stages every android-applicable VISUAL fixture layout as fx_NNNN.json
#      in a NEUTRAL tmp dir (stable synthetic names — fixture ids collide as
#      Kotlin type names, indices never do).
#   2. Runs `kjui build` (kjui_tools — the exact production Compose codegen
#      path) over it.
#   3. Copies the generated kotlin into src/codegen/kotlin/ (gitignored;
#      build.gradle.kts prefers it over src/fallback/kotlin/) and emits
#      CodegenFixtureEntries.kt — fixture id → generated composable — plus
#      codegen-map.json for diagnostics.
#
# Environment:
#   CONFORMANCE_DIR   conformance directory (manifest.json, fixtures/) [required]
#   KJUI_TOOLS_PATH   kjui_tools checkout (default: $CONFORMANCE_DIR/../kjui_tools)
#

require 'fileutils'
require 'json'

host_dir = File.expand_path('..', __dir__)
conformance_dir = ENV['CONFORMANCE_DIR'] or abort 'error: CONFORMANCE_DIR is not set'
kjui_tools = ENV['KJUI_TOOLS_PATH'] || File.expand_path('../kjui_tools', conformance_dir)
kjui_bin = File.join(kjui_tools, 'bin', 'kjui')
abort "error: kjui_tools not found: #{kjui_bin} (set KJUI_TOOLS_PATH)" unless File.file?(kjui_bin)

manifest_path = File.join(conformance_dir, 'manifest.json')
abort "error: manifest not found: #{manifest_path}" unless File.file?(manifest_path)
manifest = JSON.parse(File.read(manifest_path))

build_dir = ENV['CONFORMANCE_CODEGEN_BUILD_DIR'] || '/tmp/jsonui-codegen-android-staging'
codegen_src = File.join(host_dir, 'src', 'codegen', 'kotlin')
FileUtils.rm_rf(build_dir)
FileUtils.rm_rf(codegen_src)
layouts_dir = File.join(build_dir, 'src', 'Layouts')
FileUtils.mkdir_p(layouts_dir)

# Host namespace: generated code references R.drawable.* (fixture images),
# and R lives in the app's namespace — a distinct generated package produced
# 'Unresolved reference R' across every Image fixture.
PACKAGE = 'com.kotlinjsonui.conformance'

# ---------------------------------------------------------------- selection
entries = []
skipped = []
manifest.fetch('fixtures', []).each do |fixture|
  next unless (fixture['platforms'] || []).include?('android')
  next unless fixture['class'] == 'visual'
  mode = fixture['mode']
  mode_values =
    case mode
    when String then [mode]
    when Hash then mode.values.flatten
    else []
    end
  if !mode_values.empty? && !mode_values.include?('compose')
    skipped << { 'id' => fixture['id'], 'reason' => "mode #{mode_values.join(',')} not hosted" }
    next
  end
  if (fixture['companions'] || []).any?
    skipped << { 'id' => fixture['id'], 'reason' => 'embed-companion resolution not hosted in codegen yet' }
    next
  end
  entries << fixture
end

entries.each_with_index do |fixture, i|
  FileUtils.cp(
    File.join(conformance_dir, fixture['layout']),
    File.join(layouts_dir, format('fx_%04d.json', i + 1))
  )
end
puts "[codegen-host] staged #{entries.size} visual fixture layout(s), skipped #{skipped.size}"

# ---------------------------------------------------------------- kjui build
File.write(File.join(build_dir, 'kjui.config.json'), JSON.pretty_generate(
  'mode' => 'compose',
  'project_name' => 'ConformanceCodegen',
  'package_name' => PACKAGE,
  'source_directory' => 'src',
  'layouts_directory' => 'Layouts',
  'styles_directory' => 'Styles',
  'view_directory' => 'kotlin/views',
  'data_directory' => 'kotlin/data',
  'viewmodel_directory' => 'kotlin/viewmodels',
  'compose' => { 'output_directory' => 'kotlin/generated' }
) + "\n")

puts "[codegen-host] running kjui build (#{kjui_tools})"
build_log = File.join(host_dir, 'codegen-build.log')
ok = system({ 'PWD' => build_dir }, RbConfig.ruby, kjui_bin, 'build',
            chdir: build_dir, out: build_log, err: %i[child out])
abort "error: kjui build failed — see #{build_log}" unless ok

# Copy the whole generated kotlin tree (views/data/viewmodels/generated).
FileUtils.mkdir_p(codegen_src)
src_kotlin = File.join(build_dir, 'src', 'kotlin')
abort "error: kjui build produced no kotlin output" unless File.directory?(src_kotlin)
FileUtils.cp_r(File.join(src_kotlin, '.'), codegen_src)

# Generated resources (extracted strings/colors): the generated views
# reference R.string.* / R.color.*, so the staged res tree rides along
# (build.gradle.kts adds src/codegen/res when present).
src_res = File.join(build_dir, 'src', 'res')
if File.directory?(src_res)
  codegen_res = File.join(host_dir, 'src', 'codegen', 'res')
  FileUtils.rm_rf(codegen_res)
  FileUtils.mkdir_p(codegen_res)
  FileUtils.cp_r(File.join(src_res, '.'), codegen_res)
  # The generated views reference no R.string.* (strings render inline);
  # the extractor's strings.xml keys are layout-path-derived and aapt
  # rejects the dots ('.._.._Layouts_…'), so shipping it only breaks the
  # merge. Colors (bg-<key> etc.) are the resources the views DO use.
  FileUtils.rm_f(File.join(codegen_res, 'values', 'strings.xml'))
end

# ---------------------------------------------------------------- registry
generated = 0
imports = []
pairs = []
entries.each_with_index do |fixture, i|
  name = format('Fx%04d', i + 1)
  dir_name = format('fx_%04d', i + 1)
  view_file = File.join(codegen_src, 'views', dir_name, "#{name}View.kt")
  has_view = File.file?(view_file)
  fixture['_codegen'] = { 'component' => name, 'generated' => has_view }
  next unless has_view

  generated += 1
  imports << "import #{PACKAGE}.views.#{dir_name.delete('_')}.#{name}View"
  pairs << [fixture['id'], "#{name}View"]
end

# mapOf with ~500 composable-lambda pairs risks the JVM method-size limit —
# chunk the initializers.
chunks = pairs.each_slice(80).to_a
chunk_funcs = chunks.each_with_index.map do |chunk, ci|
  body = chunk.map { |id, view| "        #{id.inspect} to { #{view}() }," }.join("\n")
  <<~KOTLIN
    private fun chunk#{ci}(): Map<String, @Composable () -> Unit> = mapOf(
    #{body}
    )
  KOTLIN
end

registry = +"// @generated by scripts/generate_codegen_host.rb — DO NOT EDIT\n"
registry << "// Fixture id → kjui-generated Compose view (production codegen pipeline).\n"
registry << "// Compiled in place of src/fallback/kotlin's empty registry (build.gradle.kts).\n\n"
registry << "package com.kotlinjsonui.conformance\n\n"
registry << "import androidx.compose.runtime.Composable\n"
registry << imports.sort.join("\n")
registry << "\n\nobject CodegenFixtureEntries {\n"
registry << "    val map: Map<String, @Composable () -> Unit> =\n"
registry << "        " + (0...chunks.size).map { |ci| "chunk#{ci}()" }.join(" +\n        ") + "\n\n"
registry << chunk_funcs.join("\n").gsub(/^/, '    ').gsub(/^    $/, '')
registry << "}\n"

registry_dir = File.join(codegen_src, 'registry', 'com', 'kotlinjsonui', 'conformance')
FileUtils.mkdir_p(File.dirname(File.join(codegen_src, 'CodegenFixtureEntries.kt')))
File.write(File.join(codegen_src, 'CodegenFixtureEntries.kt'), registry)

map = {
  'generated' => generated,
  'staged' => entries.size,
  'skipped' => skipped,
  'fixtures' => entries.map { |f| [f['id'], f['_codegen']] }.to_h
}
File.write(File.join(host_dir, 'codegen-map.json'), JSON.pretty_generate(map) + "\n")

missing = entries.reject { |f| f.dig('_codegen', 'generated') }
puts "[codegen-host] #{generated}/#{entries.size} generated views; registry written"
unless missing.empty?
  puts "[codegen-host] fixtures WITHOUT a generated view (parity reports them as missing):"
  missing.each { |f| puts "  - #{f['id']}" }
end
puts "[codegen-host] next: HOST_MODE=codegen scripts/run_conformance.sh"
