# frozen_string_literal: true

require 'rexml/document'
require 'json'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module Compose
    module Helpers
      class ResourceResolver
        class << self
          # Don't cache - just load each time to avoid issues
          def cached_config
            Core::ConfigManager.load_config
          end
          
          def cached_source_path
            Core::ProjectFinder.get_full_source_path || Dir.pwd
          end
          
          # Process text with data binding and resource resolution
          def process_text(text, required_imports = nil)
            return quote(text) unless text.is_a?(String)
            
            # Handle data binding expressions
            if text.match(/@\{([^}]+)\}/)
              variable = $1
              if variable.include?(' ?? ')
                parts = variable.split(' ?? ')
                var_name = parts[0].strip
                return "\"\${data.#{var_name}}\""
              else
                return "\"\${data.#{variable}}\""
              end
            end
            
            # Skip resource resolution if we're in the extraction phase
            # (Resources directory doesn't exist yet)
            source_directory = cached_config['source_directory'] || 'src/main'
            layouts_dir = File.join(cached_source_path, source_directory, cached_config['layouts_directory'] || 'assets/Layouts')
            resources_dir = File.join(layouts_dir, 'Resources')
            
            # If Resources directory doesn't exist, we're in extraction phase
            # Just return quoted text
            return quote(text) unless File.exist?(resources_dir)
            
            # Try to resolve as a string resource
            resolved = resolve_string(text, cached_config, cached_source_path)
            if resolved.include?('stringResource')
              required_imports&.add(:string_resource)
              required_imports&.add(:r_class)
            end
            resolved
          end
          
          # Process color with resource resolution
          def process_color(color, required_imports = nil)
            return nil unless color.is_a?(String)
            
            # Handle data binding expressions
            if color.start_with?('@{') || color.start_with?('${}')
              return "Color(android.graphics.Color.parseColor(#{quote(color)}))"
            end
            
            # Skip resource resolution if we're in the extraction phase
            # (Resources directory doesn't exist yet)
            source_directory = cached_config['source_directory'] || 'src/main'
            layouts_dir = File.join(cached_source_path, source_directory, cached_config['layouts_directory'] || 'assets/Layouts')
            resources_dir = File.join(layouts_dir, 'Resources')
            
            # If Resources directory doesn't exist, we're in extraction phase
            # Just return standard color parsing
            unless File.exist?(resources_dir)
              return "Color(android.graphics.Color.parseColor(#{quote(color)}))"
            end
            
            resolved = resolve_color(color, cached_config, cached_source_path)
            if resolved&.include?('colorResource')
              required_imports&.add(:color_resource)
              required_imports&.add(:r_class)
            end
            resolved
          end
          
          private
          
          # Check if a string resource exists in strings.xml
          def resolve_string(text, config, source_path)
            return quote(text) unless text.is_a?(String)
            
            # Skip if it's a data binding expression
            return quote(text) if text.start_with?('@{') || text.start_with?('${')
            
            # Try to find the string in strings.xml
            string_key = find_string_key(text, config, source_path)
            
            if string_key
              # Return stringResource reference
              "stringResource(R.string.#{string_key})"
            else
              # Return quoted string
              quote(text)
            end
          end
          
          # Check if a color resource exists
          def resolve_color(color, config, source_path)
            return nil unless color.is_a?(String)

            # Skip if it's a data binding expression
            return "Color(android.graphics.Color.parseColor(#{quote(color)}))" if color.start_with?('@{') || color.start_with?('${')

            # Try to find the color in colors.json
            color_key = find_color_key(color, config, source_path)

            if color_key
              # Return colorResource reference
              "colorResource(R.color.#{color_key})"
            else
              # Return Color.parseColor
              "Color(android.graphics.Color.parseColor(#{quote(color)}))"
            end
          end
          
          private
          
          def cached_strings_data
            source_directory = cached_config['source_directory'] || 'src/main'
            layouts_dir = File.join(cached_source_path, source_directory, cached_config['layouts_directory'] || 'assets/Layouts')
            strings_file = File.join(layouts_dir, 'Resources', 'strings.json')
            
            return {} unless File.exist?(strings_file)
            
            begin
              JSON.parse(File.read(strings_file))
            rescue JSON::ParserError
              {}
            end
          end
          
          def cached_colors_data
            source_directory = cached_config['source_directory'] || 'src/main'
            layouts_dir = File.join(cached_source_path, source_directory, cached_config['layouts_directory'] || 'assets/Layouts')
            colors_file = File.join(layouts_dir, 'Resources', 'colors.json')
            
            return {} unless File.exist?(colors_file)
            
            begin
              JSON.parse(File.read(colors_file))
            rescue JSON::ParserError
              {}
            end
          end
          
          def find_string_key(text, config, source_path)
            strings_data = cached_strings_data

            # First, check if the text itself is a resource key (snake_case like "login_password")
            # This handles the case where JSON has text: "login_password" which should resolve to R.string.login_password
            if text.match?(/^[a-z]+(_[a-z0-9]+)+$/)
              strings_data.each do |file_prefix, file_strings|
                next unless file_strings.is_a?(Hash)

                file_strings.each do |key, _value|
                  full_key = "#{file_prefix}_#{key}"
                  if full_key == text
                    # Text matches an existing resource key directly
                    return text
                  end
                end
              end
            end

            # Search through all file prefixes for matching values
            strings_data.each do |file_prefix, file_strings|
              next unless file_strings.is_a?(Hash)

              file_strings.each do |key, value|
                if value == text
                  # Return the full key with prefix
                  return "#{file_prefix}_#{key}"
                end
              end
            end

            nil
          end
          
          def find_color_key(color, config, source_path)
            colors_data = cached_colors_data
            
            # First check if the color itself is a key in colors.json
            if colors_data.has_key?(color)
              return color
            end
            
            # If it's a hex color, normalize and search by value
            if color.match?(/^#?[A-Fa-f0-9]{6,8}$/)
              normalized_color = normalize_color(color)
              
              # Search through colors by value
              colors_data.each do |key, value|
                if normalize_color(value) == normalized_color
                  return key
                end
              end
            end
            
            # Also check colors.xml for predefined Android colors
            # These are colors that might be defined in colors.xml but not in colors.json
            colors_xml_path = File.join(source_path, config['source_directory'] || 'src/main', 'res/values/colors.xml')
            if File.exist?(colors_xml_path)
              # Quick check - if the color name exists in colors.xml
              # we'll assume it's available (proper check would parse XML)
              xml_content = File.read(colors_xml_path)
              if xml_content.include?("name='#{color}'") || xml_content.include?("name=\"#{color}\"")
                return color
              end
            end
            
            nil
          end
          
          def normalize_color(color)
            return nil unless color.is_a?(String)
            
            # Remove # if present and convert to lowercase
            color.sub(/^#/, '').downcase
          end
          
          def quote(text)
            # Escape special characters properly
            escaped = text.to_s.gsub('\\', '\\\\\\\\')
                              .gsub('"', '\\"')
                              .gsub("\n", '\\n')
                              .gsub("\r", '\\r')
                              .gsub("\t", '\\t')
            "\"#{escaped}\""
          end
        end
      end
    end
  end
end