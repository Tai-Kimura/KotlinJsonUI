# frozen_string_literal: true

require 'fileutils'
require 'tmpdir'

# Where generate_codegen_host.rb runs `kjui build`: a directory of this run's
# own.
#
# It was one fixed path, /tmp/jsonui-codegen-android-staging, emptied at the
# start of every run — so two generations at once (two lanes, two worktrees)
# emptied each other's layouts mid-build. The iOS host had the same shape
# and was measured (SwiftJsonUI 956ffde, 2026-09-26): 4 of 10 concurrent runs
# failed and 3 of the 6 that exited 0 wrote output unlike a run alone's.
# Ticket conformance-host-codegen-staging-dir-collides-across-runs.
#
# Now: a per-run directory under /tmp (where it always was), named with the
# run's pid, removed when the run ends, and first a sweep of what killed
# runs left: this family's per-run directories whose pid is dead. Nothing
# else is touched: not a live run's, not another family's, not the old
# fixed-name directory. A directory the caller names in
# CONFORMANCE_CODEGEN_BUILD_DIR is theirs: emptied first, as before, and
# left in place.
module CodegenBuildDir
  FAMILY = 'jsonui-codegen-android-staging'
  PARENT = '/tmp'

  module_function

  # [directory, whether this run owns it (and so removes it)]
  def for_run(env = ENV, parent: PARENT)
    named = env['CONFORMANCE_CODEGEN_BUILD_DIR']
    if named && !named.empty?
      FileUtils.rm_rf(named)
      FileUtils.mkdir_p(named)
      return [named, false]
    end

    sweep(parent)
    [Dir.mktmpdir("#{FAMILY}-#{Process.pid}-", parent), true]
  end

  # The per-run directories of this family whose run is gone.
  def sweep(parent = PARENT)
    Dir.glob(File.join(parent, "#{FAMILY}-*")).each do |dir|
      pid = run_pid(dir)
      FileUtils.rm_rf(dir) if pid && !alive?(pid)
    end
  end

  def run_pid(dir)
    File.basename(dir)[/\A#{Regexp.escape(FAMILY)}-(\d+)-/o, 1]&.to_i
  end

  def alive?(pid)
    Process.kill(0, pid)
    true
  rescue Errno::ESRCH
    false
  rescue Errno::EPERM
    true # someone else's process: alive, and not ours to judge
  end
end
