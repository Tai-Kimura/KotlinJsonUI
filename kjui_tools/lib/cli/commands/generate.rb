# frozen_string_literal: true

require 'optparse'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module CLI
    module Commands
      class Generate
        SUBCOMMANDS = {
          'view' => 'Generate a new view with JSON and binding',
          'partial' => 'Generate a partial view', 
          'collection' => 'Generate a collection view',
          'cell' => 'Generate a collection cell view',
          'binding' => 'Generate binding file'
        }.freeze

        def run(args)
          subcommand = args.shift
          
          if subcommand.nil? || subcommand == 'help'
            show_help
            return
          end
          
          unless SUBCOMMANDS.key?(subcommand)
            puts "Unknown generate command: #{subcommand}"
            show_help
            exit 1
          end
          
          # Load config to get mode
          config = Core::ConfigManager.load_config
          mode = config['mode'] || 'compose'
          
          case subcommand
          when 'view'
            generate_view(args, mode)
          when 'partial'
            generate_partial(args, mode)
          when 'collection'
            generate_collection(args, mode)
          when 'cell'
            generate_cell(args, mode)
          when 'binding'
            generate_binding(args, mode)
          end
        end

        private

        def generate_view(args, mode)
          options = parse_view_options(args)
          name = args.shift
          
          if name.nil? || name.empty?
            puts "Error: View name is required"
            puts "Usage: kjui generate view <name> [options]"
            exit 1
          end
          
          # Setup project paths
          Core::ProjectFinder.setup_paths
          
          case mode
          when 'xml'
            require_relative '../../xml/generators/view_generator'
            generator = KjuiTools::Xml::Generators::ViewGenerator.new(name, options)
            generator.generate
          when 'compose'
            require_relative '../../compose/generators/view_generator'
            generator = KjuiTools::Compose::Generators::ViewGenerator.new(name, options)
            generator.generate
          else
            puts "Error: Unknown mode: #{mode}"
            exit 1
          end
        end

        def generate_partial(args, mode)
          name = args.shift
          
          if name.nil? || name.empty?
            puts "Error: Partial name is required"
            puts "Usage: kjui generate partial <name>"
            exit 1
          end
          
          case mode
          when 'xml'
            require_relative '../../xml/generators/partial_generator'
            generator = KjuiTools::Xml::Generators::PartialGenerator.new(name)
            generator.generate
          when 'compose'
            require_relative '../../compose/generators/partial_generator'
            generator = KjuiTools::Compose::Generators::PartialGenerator.new(name)
            generator.generate
          end
        end

        def generate_collection(args, mode)
          name = args.shift
          
          if name.nil? || name.empty?
            puts "Error: Collection name is required"
            puts "Usage: kjui generate collection <name>"
            exit 1
          end
          
          # Setup project paths
          Core::ProjectFinder.setup_paths
          
          case mode
          when 'xml'
            require_relative '../../xml/generators/collection_generator'
            generator = KjuiTools::Xml::Generators::CollectionGenerator.new(name)
            generator.generate
          when 'compose'
            require_relative '../../compose/generators/collection_generator'
            generator = KjuiTools::Compose::Generators::CollectionGenerator.new(name)
            generator.generate
          else
            puts "Error: Unknown mode: #{mode}"
            exit 1
          end
        end

        def generate_cell(args, mode)
          name = args.shift
          
          if name.nil? || name.empty?
            puts "Error: Cell name is required"
            puts "Usage: kjui generate cell <name>"
            exit 1
          end
          
          # Setup project paths
          Core::ProjectFinder.setup_paths
          
          case mode
          when 'xml'
            puts "Cell generation is not available in XML mode"
            exit 1
          when 'compose'
            require_relative '../../compose/generators/cell_generator'
            generator = KjuiTools::Compose::Generators::CellGenerator.new(name)
            generator.generate
          else
            puts "Error: Unknown mode: #{mode}"
            exit 1
          end
        end

        def generate_binding(args, mode)
          name = args.shift
          
          if name.nil? || name.empty?
            puts "Error: Binding name is required"
            puts "Usage: kjui generate binding <name>"
            exit 1
          end
          
          if mode != 'xml'
            puts "Binding generation is only available in XML mode"
            exit 1
          end
          
          require_relative '../../xml/generators/binding_generator'
          generator = KjuiTools::Xml::Generators::BindingGenerator.new(name)
          generator.generate
        end

        def parse_view_options(args)
          options = {
            root: false,
            mode: nil
          }
          
          OptionParser.new do |opts|
            opts.on('--root', 'Generate root view/activity') do
              options[:root] = true
            end
            
            opts.on('--mode MODE', 'Override mode (xml, compose)') do |mode|
              options[:mode] = mode
            end
          end.parse!(args)
          
          options
        end

        def show_help
          puts "Usage: kjui generate SUBCOMMAND [options]"
          puts
          puts "Subcommands:"
          SUBCOMMANDS.each do |cmd, desc|
            puts "  #{cmd.ljust(12)} #{desc}"
          end
          puts
          puts "Examples:"
          puts "  kjui g view HomeView           # Generate a view"
          puts "  kjui g view RootView --root    # Generate root view"
          puts "  kjui g partial Header          # Generate a partial"
          puts "  kjui g collection Post/Cell    # Generate collection cell"
          puts "  kjui g binding CustomBinding   # Generate binding file (XML mode only)"
        end
      end
    end
  end
end