#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative 'xml_generator'

module KjuiTools
  module Xml
    class XmlBuilder
      def initialize(config = nil)
        @config = config || Core::ConfigManager.load_config
        Core::ProjectFinder.setup_paths
        @project_path = @config['project_path'] || Core::ProjectFinder.project_dir || Dir.pwd
        @layouts_dir = File.join(@project_path, @config['source_directory'] || 'src/main', 'assets/Layouts')
        @output_dir = File.join(@project_path, 'src/main/res/layout')
        @generated_count = 0
        @failed_count = 0
        @skipped_count = 0
      end

      def build(options = {})
        puts "🔨 Building XML View files..."
        puts "📁 Project: #{@project_path}"
        puts "📂 Layouts: #{@layouts_dir}"
        puts "📂 Output: #{@output_dir}"
        puts "-" * 60
        
        unless Dir.exist?(@layouts_dir)
          puts "❌ Layouts directory not found: #{@layouts_dir}"
          return false
        end
        
        # Clean output directory if requested
        if options[:clean]
          clean_output_directory
        end
        
        # Ensure output directory exists
        FileUtils.mkdir_p(@output_dir)
        
        # Get all JSON files
        json_files = Dir.glob(File.join(@layouts_dir, '*.json'))
        
        if json_files.empty?
          puts "⚠️  No JSON files found in #{@layouts_dir}"
          return true
        end
        
        puts "📄 Found #{json_files.length} JSON files"
        puts "-" * 60
        
        # Process each file
        json_files.each do |json_file|
          process_layout(json_file)
        end
        
        # Print summary
        puts "-" * 60
        puts "✅ Build Complete!"
        puts "   Generated: #{@generated_count} files"
        puts "   Failed: #{@failed_count} files" if @failed_count > 0
        puts "   Skipped: #{@skipped_count} files" if @skipped_count > 0
        
        @failed_count == 0
      end
      
      private
      
      def clean_output_directory
        puts "🧹 Cleaning output directory..."
        
        if Dir.exist?(@output_dir)
          # Only remove generated XML files (those with our comment marker)
          Dir.glob(File.join(@output_dir, '*.xml')).each do |file|
            content = File.read(file)
            if content.include?('<!-- Generated from') && content.include?('.json')
              puts "   Removing: #{File.basename(file)}"
              File.delete(file)
            end
          end
        end
      end
      
      def process_layout(json_file)
        layout_name = File.basename(json_file, '.json')
        
        # Skip partial/included files (convention: starts with underscore)
        if layout_name.start_with?('_')
          puts "   ⏭️  Skipping partial: #{layout_name}"
          @skipped_count += 1
          return
        end
        
        # Skip cell templates (they're used in collections)
        if layout_name.end_with?('_cell') || layout_name.include?('cell')
          puts "   ⏭️  Skipping cell template: #{layout_name}"
          @skipped_count += 1
          return
        end
        
        # Skip included files (used by include mechanism)
        if layout_name.start_with?('included')
          puts "   ⏭️  Skipping include file: #{layout_name}"
          @skipped_count += 1
          return
        end
        
        print "   📝 Processing: #{layout_name}..."
        
        begin
          # Ensure project_path is set in config
          config_with_path = @config.merge('project_path' => @project_path)
          
          # Generate XML using the existing generator
          generator = XmlGenerator::Generator.new(layout_name, config_with_path)
          if generator.generate
            @generated_count += 1
            puts " ✅"
          else
            @failed_count += 1
            puts " ❌"
          end
        rescue => e
          @failed_count += 1
          puts " ❌"
          puts "      Error: #{e.message}"
          puts e.backtrace.first(5).map { |line| "        #{line}" }.join("\n")
        end
      end
    end
  end
end

# Allow running directly
if __FILE__ == $0
  require_relative '../core/config_manager'
  
  config = KjuiTools::Core::ConfigManager.load_config
  builder = KjuiTools::Xml::XmlBuilder.new(config)
  
  options = {}
  ARGV.each do |arg|
    case arg
    when '--clean', '-c'
      options[:clean] = true
    when '--debug', '-d'
      config['debug'] = true
    end
  end
  
  builder.build(options)
end