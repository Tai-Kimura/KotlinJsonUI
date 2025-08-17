# frozen_string_literal: true

require 'json'
require 'pathname'

module KjuiTools
  module Core
    class ConfigManager
      CONFIG_FILE = 'kjui.config.json'
      
      class << self
        def load_config
          return {} unless File.exist?(CONFIG_FILE)
          
          JSON.parse(File.read(CONFIG_FILE))
        rescue JSON::ParserError => e
          puts "Error parsing config file: #{e.message}"
          {}
        end
        
        def save_config(config)
          File.write(CONFIG_FILE, JSON.pretty_generate(config))
        end
        
        def config_exists?
          File.exist?(CONFIG_FILE)
        end
        
        def get(key, default = nil)
          config = load_config
          keys = key.split('.')
          
          value = config
          keys.each do |k|
            value = value[k] if value.is_a?(Hash)
          end
          
          value || default
        end
        
        def set(key, value)
          config = load_config
          keys = key.split('.')
          
          current = config
          keys[0...-1].each do |k|
            current[k] ||= {}
            current = current[k]
          end
          
          current[keys.last] = value
          save_config(config)
        end
        
        def detect_mode
          # Check for Android project files
          gradle_files = Dir.glob('build.gradle*')
          settings_gradle = Dir.glob('settings.gradle*')
          
          if gradle_files.any? || settings_gradle.any?
            # Check if it's a Compose project
            build_file = gradle_files.first
            if build_file && File.exist?(build_file)
              content = File.read(build_file)
              if content.include?('compose') || content.include?('androidx.compose')
                return 'compose'
              end
            end
            
            # Default to XML for Android projects
            return 'xml'
          end
          
          # Default mode
          'all'
        end
        
        def project_type
          mode = get('mode', detect_mode)
          
          case mode
          when 'compose'
            'Jetpack Compose'
          when 'xml'
            'Android XML'
          when 'all'
            'Android (XML + Compose)'
          else
            'Unknown'
          end
        end
        
        def source_path
          get('source_directory', 'app/src/main')
        end
        
        def layouts_path
          Pathname.new(source_path).join(get('layouts_directory', 'assets/Layouts'))
        end
        
        def styles_path
          Pathname.new(source_path).join(get('styles_directory', 'assets/Styles'))
        end
        
        def view_path
          Pathname.new(source_path).join(get('view_directory', 'java/com/example/app/ui'))
        end
        
        def data_path
          Pathname.new(source_path).join(get('data_directory', 'java/com/example/app/data'))
        end
        
        def viewmodel_path
          Pathname.new(source_path).join(get('viewmodel_directory', 'java/com/example/app/viewmodel'))
        end
        
        def generated_path
          if get('mode') == 'compose'
            compose_config = get('compose', {})
            Pathname.new(source_path).join(compose_config['output_directory'] || 'java/com/example/app/generated')
          else
            Pathname.new(source_path).join(get('bindings_directory', 'java/com/example/app/bindings'))
          end
        end
      end
    end
  end
end