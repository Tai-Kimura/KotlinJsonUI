# frozen_string_literal: true

require 'optparse'
require 'fileutils'
require 'json'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module CLI
    module Commands
      class Init
        def run(args)
          options = parse_options(args)
          
          # Check if MODE file exists (set by installer)
          installer_mode = nil
          mode_file = File.join(File.dirname(__FILE__), '../../../../MODE')
          if File.exist?(mode_file)
            installer_mode = File.read(mode_file).strip
          end
          
          # Detect or use specified mode
          mode = options[:mode] || installer_mode || Core::ConfigManager.detect_mode
          
          puts "Initializing KotlinJsonUI project in #{mode} mode..."
          
          # Create config file
          create_config_file(mode)
          
          # Create directory structure based on mode
          case mode
          when 'xml'
            create_xml_structure
          when 'compose'
            create_compose_structure
          when 'all'
            create_xml_structure
            create_compose_structure
          end
          
          puts "Initialization complete!"
          puts
          if mode == 'compose'
            puts "Compose mode initialized. Use Compose-specific commands for your project."
          else
            puts "Next steps:"
            puts "  1. Run 'kjui setup' to install dependencies and base files"
            puts "  2. Run 'kjui g view HomeView' to generate your first view"
          end
        end

        private

        def parse_options(args)
          options = {}
          
          OptionParser.new do |opts|
            opts.banner = "Usage: kjui init [options]"
            
            opts.on('--mode MODE', ['all', 'xml', 'compose'], 
                    'Initialize mode (all, xml, compose)') do |mode|
              options[:mode] = mode
            end
            
            opts.on('-h', '--help', 'Show this help message') do
              puts opts
              exit
            end
          end.parse!(args)
          
          options
        end

        def create_config_file(mode)
          config_file = 'kjui.config.json'
          
          if File.exist?(config_file)
            puts "Config file already exists: #{config_file}"
            # Check if source_directory needs to be updated
            existing_config = JSON.parse(File.read(config_file))
            if existing_config['source_directory'].to_s.empty?
              Core::ProjectFinder.setup_paths
              # Auto-detect source directory without checking config
              project_dir = Core::ProjectFinder.project_dir
              
              # If project_dir is nil, fallback to finding gradle files
              if project_dir.nil?
                gradle_file = Dir.glob('build.gradle*').first || Dir.glob('../build.gradle*').first
                project_dir = gradle_file ? File.dirname(File.expand_path(gradle_file)) : Dir.pwd
              end
              
              common_names = ['app/src/main', 'src/main', 'src', File.basename(project_dir)]
              
              source_dir = nil
              common_names.each do |name|
                path = File.join(project_dir, name)
                if Dir.exist?(path)
                  source_dir = name
                  break
                end
              end
              
              if source_dir && !source_dir.empty?
                existing_config['source_directory'] = source_dir
                File.write(config_file, JSON.pretty_generate(existing_config))
                puts "Updated source_directory to: #{source_dir}"
              end
            end
            return
          end
          
          # Find project info
          Core::ProjectFinder.setup_paths
          
          # Get project name from settings.gradle or current directory
          project_name = get_project_name_from_gradle || File.basename(Dir.pwd)
          
          # Create base config based on mode
          if mode == 'compose'
            # Detect package name
            package_name = Core::ProjectFinder.package_name
            
            # Compose-specific config with appropriate defaults
            # Detect if we're in a module or main app
            source_dir = if Dir.exist?('src/main')
                          'src/main'
                        elsif Dir.exist?('app/src/main')
                          'app/src/main'
                        else
                          Core::ProjectFinder.find_source_directory || 'src/main'
                        end
            
            config = {
              'mode' => mode,
              'project_name' => project_name,
              'source_directory' => source_dir,
              'layouts_directory' => 'assets/Layouts',
              'styles_directory' => 'assets/Styles',
              'data_directory' => "kotlin/#{package_name.gsub('.', '/')}/data",
              'viewmodel_directory' => "kotlin/#{package_name.gsub('.', '/')}/viewmodels",
              'view_directory' => "kotlin/#{package_name.gsub('.', '/')}/views",
              'extension_directory' => "kotlin/#{package_name.gsub('.', '/')}/extensions",
              'adapter_directory' => "kotlin/#{package_name.gsub('.', '/')}/adapters",
              'package_name' => package_name,
              'use_network' => true,  # Compose mode can use network for API calls
              'hotloader' => {
                'ip' => '127.0.0.1',
                'port' => 8081,
                'watch_directories' => ['assets/Layouts', 'assets/Styles']
              }
            }
          else
            # XML mode or all mode config
            config = {
              'mode' => mode,
              'project_name' => project_name,
              'project_file_name' => project_name,
              'source_directory' => Core::ProjectFinder.find_source_directory || 'app/src/main',
              'layouts_directory' => 'res/raw/layouts',
              'styles_directory' => 'res/raw/styles',
              'view_directory' => 'java/com/example/app/ui',
              'data_directory' => 'java/com/example/app/data',
              'viewmodel_directory' => 'java/com/example/app/viewmodel',
              'bindings_directory' => 'java/com/example/app/bindings',
              'extension_directory' => 'java/com/example/app/extensions',
              'adapter_directory' => 'java/com/example/app/adapters',
              'use_network' => true,
              'hotloader' => {
                'ip' => '127.0.0.1',
                'port' => 8081,
                'watch_directories' => ['res/raw/layouts', 'res/raw/styles']
              }
            }
            
            # Add Compose config if mode is 'all'
            if mode == 'all'
              config['compose'] = {
                'output_directory' => 'java/com/example/app/generated'
              }
            end
          end
          
          File.write(config_file, JSON.pretty_generate(config))
          puts "Created config file: #{config_file}"
        end

        def create_xml_structure
          directories = %w[
            res/raw/layouts
            res/raw/styles
            java/com/example/app/ui
            java/com/example/app/ui/activities
            java/com/example/app/ui/fragments
            java/com/example/app/data
            java/com/example/app/viewmodel
            java/com/example/app/bindings
            java/com/example/app/core
            java/com/example/app/core/base
          ]
          
          create_directories(directories)
        end

        def create_compose_structure
          # Read config to get directory names
          config = Core::ConfigManager.load_config
          source_dir = config['source_directory'] || 'app/src/main'
          
          directories = [
            File.join(source_dir, config['layouts_directory'] || 'assets/Layouts'),
            File.join(source_dir, config['styles_directory'] || 'assets/Styles')
          ]
          
          # Add data directory if configured
          if config['data_directory']
            directories << File.join(source_dir, config['data_directory'])
          end
          
          # Add viewmodel directory if configured
          if config['viewmodel_directory']
            directories << File.join(source_dir, config['viewmodel_directory'])
          end
          
          # Add view directory if configured
          if config['view_directory']
            directories << File.join(source_dir, config['view_directory'])
          end
          
          create_directories(directories)
        end

        def create_directories(directories)
          directories.each do |dir|
            unless Dir.exist?(dir)
              FileUtils.mkdir_p(dir)
              puts "Created directory: #{dir}"
            end
          end
        end
        
        def get_project_name_from_gradle
          # Try settings.gradle.kts first
          if File.exist?('settings.gradle.kts')
            content = File.read('settings.gradle.kts')
            if content =~ /rootProject\.name\s*=\s*["']([^"']+)["']/
              return $1
            end
          end
          
          # Try settings.gradle
          if File.exist?('settings.gradle')
            content = File.read('settings.gradle')
            if content =~ /rootProject\.name\s*=\s*["']([^"']+)["']/
              return $1
            end
          end
          
          nil
        end
      end
    end
  end
end