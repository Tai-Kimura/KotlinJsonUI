# frozen_string_literal: true

require 'optparse'
require 'json'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'
require_relative '../../core/logger'

module KjuiTools
  module CLI
    module Commands
      class Build
        def run(args)
          options = parse_options(args)
          
          # Detect mode
          mode = options[:mode] || Core::ConfigManager.get('mode') || 'compose'
          
          case mode
          when 'xml', 'all'
            build_xml(options)
          end
          
          if mode == 'compose' || mode == 'all'
            build_compose(options)
          end
        end

        private

        def parse_options(args)
          options = {}
          
          OptionParser.new do |opts|
            opts.banner = "Usage: kjui build [options]"
            
            opts.on('--mode MODE', ['all', 'xml', 'compose'], 
                    'Build mode (all, xml, compose)') do |mode|
              options[:mode] = mode
            end
            
            opts.on('--clean', 'Clean cache before building') do
              options[:clean] = true
            end
            
            opts.on('-h', '--help', 'Show this help message') do
              puts opts
              exit
            end
          end.parse!(args)
          
          options
        end

        def build_xml(options = {})
          Core::Logger.info "Building XML View files..."
          
          # Setup project paths
          Core::ProjectFinder.setup_paths
          
          require_relative '../../xml/xml_builder'
          builder = Xml::XmlBuilder.new
          builder.build(options)
          
          Core::Logger.success "XML build completed!"
        end

        def build_compose(options = {})
          Core::Logger.info "Building Compose files..."
          
          # Setup project paths
          Core::ProjectFinder.setup_paths
          
          require_relative '../../compose/compose_builder'
          require_relative '../../compose/build_cache_manager'
          
          config = Core::ConfigManager.load_config
          source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
          layouts_dir = File.join(source_path, config['layouts_directory'] || 'assets/Layouts')
          
          # Initialize cache manager
          cache_manager = Compose::BuildCacheManager.new(source_path)
          
          # Clean cache if --clean option is specified
          if options[:clean]
            Core::Logger.info "Cleaning build cache..."
            cache_manager.clean_cache
          end
          
          last_updated = cache_manager.load_last_updated
          last_including_files = cache_manager.load_last_including_files
          style_dependencies = cache_manager.load_style_dependencies
          
          # Process all JSON files in Layouts directory
          json_files = Dir.glob(File.join(layouts_dir, '**/*.json'))
          
          if json_files.empty?
            Core::Logger.warn "No JSON files found in #{layouts_dir}"
            return
          end
          
          # Track new includes and style dependencies
          new_including_files = {}
          new_style_dependencies = {}
          
          # Filter files that need update
          files_to_update = []
          json_files.each do |json_file|
            file_name = File.basename(json_file, '.json')
            
            # Check if file needs update
            if cache_manager.needs_update?(json_file, last_updated, layouts_dir, last_including_files, style_dependencies)
              files_to_update << json_file
            else
              # Keep existing includes and style dependencies for unchanged files
              new_including_files[file_name] = last_including_files[file_name] if last_including_files[file_name]
              new_style_dependencies[file_name] = style_dependencies[file_name] if style_dependencies[file_name]
            end
          end
          
          if files_to_update.empty?
            Core::Logger.info "No files need updating (all cached)"
            return
          end
          
          Core::Logger.info "Updating #{files_to_update.length} of #{json_files.length} files..."
          
          builder = Compose::ComposeBuilder.new
          
          files_to_update.each do |json_file|
            relative_path = Pathname.new(json_file).relative_path_from(Pathname.new(layouts_dir)).to_s
            file_name = File.basename(json_file, '.json')
            
            begin
              # Read and parse JSON
              json_content = File.read(json_file)
              json_data = JSON.parse(json_content)
              
              # Extract includes and styles for cache tracking
              includes = cache_manager.extract_includes(json_data)
              styles = cache_manager.extract_styles(json_data)
              
              new_including_files[file_name] = includes if includes.any?
              new_style_dependencies[file_name] = styles if styles.any?
              
              # Build Compose file
              Core::Logger.info "Processing: #{relative_path}"
              builder.build_file(json_file)
              
            rescue JSON::ParserError => e
              Core::Logger.error "Failed to parse #{json_file}: #{e.message}"
            rescue => e
              Core::Logger.error "Failed to process #{json_file}: #{e.message}"
            end
          end
          
          # Save cache for next build
          cache_manager.save_cache(new_including_files, new_style_dependencies)
          
          Core::Logger.success "Compose build completed!"
        end
      end
    end
  end
end