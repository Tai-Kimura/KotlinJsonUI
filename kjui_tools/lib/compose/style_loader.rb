# frozen_string_literal: true

require 'json'
require_relative '../core/config_manager'
require_relative '../core/project_finder'

module KjuiTools
  module Compose
    class StyleLoader
      class << self
        def load_and_merge(json_data)
          return json_data unless json_data.is_a?(Hash)
          
          # Load style if specified
          if json_data['style']
            style_data = load_style(json_data['style'])
            if style_data
              # Merge style data with component data
              # Component data takes precedence over style data
              merged_data = style_data.merge(json_data)
              # Remove the style key from the merged data
              merged_data.delete('style')
              json_data = merged_data
            end
          end
          
          # Process children recursively
          if json_data['child']
            if json_data['child'].is_a?(Array)
              json_data['child'] = json_data['child'].map { |child| load_and_merge(child) }
            else
              json_data['child'] = load_and_merge(json_data['child'])
            end
          end
          
          # Process includes
          if json_data['include']
            json_data = process_include(json_data)
          end
          
          json_data
        end
        
        private
        
        def load_style(style_name)
          config = Core::ConfigManager.load_config
          project_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
          source_dir = config['source_directory'] || 'src/main'
          source_path = File.join(project_path, source_dir)
          styles_dir = File.join(source_path, config['styles_directory'] || 'assets/Styles')
          
          style_file = File.join(styles_dir, "#{style_name}.json")
          
          return nil unless File.exist?(style_file)
          
          begin
            style_content = File.read(style_file)
            style_data = JSON.parse(style_content)
            
            # Recursively load and merge styles in the style file
            load_and_merge(style_data)
          rescue JSON::ParserError => e
            puts "Warning: Failed to parse style file #{style_file}: #{e.message}"
            nil
          end
        end
        
        def process_include(json_data)
          include_name = json_data['include']
          config = Core::ConfigManager.load_config
          project_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
          source_dir = config['source_directory'] || 'src/main'
          source_path = File.join(project_path, source_dir)
          layouts_dir = File.join(source_path, config['layouts_directory'] || 'assets/Layouts')
          
          include_file = File.join(layouts_dir, "#{include_name}.json")
          
          if File.exist?(include_file)
            begin
              include_content = File.read(include_file)
              include_data = JSON.parse(include_content)
              
              # Recursively process the included file
              include_data = load_and_merge(include_data)
              
              # Handle data passing to includes
              if json_data['data']
                # Pass data to the included component
                include_data['passed_data'] = json_data['data']
              end
              
              if json_data['shared_data']
                # Share data with the included component
                include_data['shared_data'] = json_data['shared_data']
              end
              
              # Merge any other attributes from the include directive
              # (except 'include', 'data', and 'shared_data')
              json_data.each do |key, value|
                next if ['include', 'data', 'shared_data'].include?(key)
                include_data[key] = value unless include_data.key?(key)
              end
              
              include_data
            rescue JSON::ParserError => e
              puts "Warning: Failed to parse include file #{include_file}: #{e.message}"
              json_data
            end
          else
            puts "Warning: Include file not found: #{include_file}"
            json_data
          end
        end
      end
    end
  end
end