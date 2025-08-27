# frozen_string_literal: true

require 'fileutils'
require 'json'
require_relative '../../core/logger'
require_relative 'kotlin_component_generator'
require_relative 'dynamic_component_generator'

module KjuiTools
  module Compose
    module Generators
      class ConverterGenerator
        def initialize(name, options = {})
          @name = name
          # Keep original PascalCase name for component
          @component_pascal_case = name  # e.g., MyTestCard
          @component_snake_case = to_snake_case(name)  # e.g., my_test_card
          @class_name = name + "Component"  # e.g., MyTestCardComponent
          @options = options
          @logger = Core::Logger
        end

        def generate
          @logger.info "Generating custom converter: #{@class_name}"
          
          # Create converter file for static generation
          create_converter_file
          
          # Update component mappings file
          update_mappings_file
          
          # Create Kotlin component file using separate generator
          kotlin_generator = KotlinComponentGenerator.new(@name, @options)
          kotlin_generator.generate
          
          # Generate dynamic component file
          dynamic_generator = DynamicComponentGenerator.new(@name, @options)
          dynamic_generator.generate
          
          @logger.success "Successfully generated converter: #{@class_name}"
          @logger.info "Converter file created at: kjui_tools/lib/compose/components/extensions/#{@component_snake_case}_component.rb"
          @logger.info "Mappings file updated with '#{@component_pascal_case}' => '#{@class_name}'"
        end

        private

        def create_converter_file
          # Get the path relative to this generator file
          generator_dir = File.dirname(__FILE__)
          # Go up to lib/compose/components/extensions
          extensions_dir = File.join(generator_dir, '..', 'components', 'extensions')
          extensions_dir = File.expand_path(extensions_dir)
          FileUtils.mkdir_p(extensions_dir)
          
          file_path = File.join(extensions_dir, "#{@component_snake_case}_component.rb")
          
          if File.exist?(file_path)
            @logger.warn "Converter file already exists: #{file_path}"
            print "Overwrite? (y/n): "
            response = gets.chomp.downcase
            return unless response == 'y'
          end
          
          File.write(file_path, converter_template)
          @logger.info "Created converter file: #{file_path}"
        end
        
        def update_mappings_file
          # Get the path relative to this generator file
          generator_dir = File.dirname(__FILE__)
          mappings_file = File.join(generator_dir, '..', 'components', 'extensions', 'component_mappings.rb')
          mappings_file = File.expand_path(mappings_file)
          
          # Create new mappings file if it doesn't exist
          if !File.exist?(mappings_file)
            create_initial_mappings_file
            return
          end
          
          # Read existing mappings
          content = File.read(mappings_file)
          
          # Check if mapping already exists
          if content.include?("'#{@component_pascal_case}' =>")
            @logger.warn "Mapping for '#{@component_pascal_case}' already exists in component_mappings.rb"
            return
          end
          
          # Add require statement if not present
          require_line = "require_relative '#{@component_snake_case}_component'"
          unless content.include?(require_line)
            # Add require after other requires or at the beginning of the module
            if content =~ /^require_relative/
              # Add after the last require
              content.sub!(/^((?:require_relative.*\n)+)/) do
                "#{$1}#{require_line}\n"
              end
            else
              # Add before the module declaration
              content.sub!(/^(# Auto-generated.*\n)\n/) do
                "#{$1}\n#{require_line}\n\n"
              end
            end
          end
          
          # Add new mapping
          new_mapping = "        '#{@component_pascal_case}' => #{@class_name},"
          
          # Insert the new mapping before the closing brace of COMPONENT_MAPPINGS
          content.sub!(/(COMPONENT_MAPPINGS = \{.*?)(,?)(\s*)(      \}\.freeze)/m) do
            existing_mappings = $1
            last_comma = $2
            whitespace = $3
            closing = $4
            
            # If there are existing mappings, add the new one with proper formatting
            if existing_mappings =~ /=>/
              # Ensure the last existing mapping has a comma, then add the new mapping
              "#{existing_mappings},\n#{new_mapping}\n#{closing}"
            else
              # First mapping
              "#{existing_mappings}\n#{new_mapping}\n#{closing}"
            end
          end
          
          File.write(mappings_file, content)
          @logger.info "Updated component_mappings.rb with new mapping"
        end
        
        def create_initial_mappings_file
          # Get the path relative to this generator file
          generator_dir = File.dirname(__FILE__)
          extensions_dir = File.join(generator_dir, '..', 'components', 'extensions')
          extensions_dir = File.expand_path(extensions_dir)
          FileUtils.mkdir_p(extensions_dir)
          
          mappings_file = File.join(extensions_dir, 'component_mappings.rb')
          
          content = <<~RUBY
            # frozen_string_literal: true
            
            # This file maps custom component types to their converter classes
            # Auto-generated by kjui g converter command
            
            require_relative '#{@component_snake_case}_component'
            
            module KjuiTools
              module Compose
                module Components
                  module Extensions
                    COMPONENT_MAPPINGS = {
                      '#{@component_pascal_case}' => #{@class_name},
                    }.freeze
                  end
                end
              end
            end
          RUBY
          
          File.write(mappings_file, content)
          @logger.info "Created component_mappings.rb with initial mapping"
        end

        def converter_template
          <<~RUBY
            # frozen_string_literal: true
            
            require_relative '../../helpers/modifier_builder'
            
            module KjuiTools
              module Compose
                module Components
                  module Extensions
                    class #{@class_name}
                      def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
                        required_imports&.add(:box)
                        
                        # Check if this is a container component
                        children = json_data['children'] || json_data['child']
                        is_container = children && children.is_a?(Array) && !children.empty?
                        
                        # Collect parameters
                        params = []
            
                        # Helper method to format values
                        format_value = lambda do |value, type|
                          case type.downcase
                          when 'string', 'text'
                            "\\"\#{value}\\""
                          when 'int', 'integer', 'float', 'double', 'bool', 'boolean'
                            value.to_s
                          when 'color'
                            if value.is_a?(String) && value.start_with?('#')
                              "Color(android.graphics.Color.parseColor(\\"\#{value}\\"))"
                            else
                              "Color.\#{value}"
                            end
                          else
                            value.to_s
                          end
                        end
            #{generate_parameter_collection}
                        
                        # Build modifiers
                        modifiers = []
                        modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
                        modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
                        modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
                        modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
                        
                        if is_container
                          # Container component with children
                          code = indent("#{@component_pascal_case}(", depth)
                          
                          if !params.empty?
                            params.each_with_index do |param, index|
                              separator = index == params.length - 1 ? '' : ','
                              code += "\\n" + indent("\#{param}\#{separator}", depth + 1)
                            end
                          end
                          
                          if !modifiers.empty?
                            modifier_str = Helpers::ModifierBuilder.format(modifiers, depth)
                            code += (params.empty? ? modifier_str : "," + modifier_str)
                          end
                          
                          code += "\\n" + indent(") {", depth)
                          
                          # Process children - return with metadata for ComposeBuilder to handle
                          return {
                            code: code,
                            children: children,
                            closing: "\\n" + indent("}", depth)
                          }
                        else
                          # Non-container component
                          if params.empty? && modifiers.empty?
                            code = indent("#{@component_pascal_case}()", depth)
                          else
                            code = indent("#{@component_pascal_case}(", depth)
                            
                            if !params.empty?
                              params.each_with_index do |param, index|
                                separator = index == params.length - 1 ? '' : ','
                                code += "\\n" + indent("\#{param}\#{separator}", depth + 1)
                              end
                            end
                            
                            if !modifiers.empty?
                              modifier_str = Helpers::ModifierBuilder.format(modifiers, depth)
                              code += (params.empty? ? modifier_str : "," + modifier_str)
                            end
                            
                            code += "\\n" + indent(")", depth)
                          end
                        end
                        
                        code
                      end
                      
                      private
                      
                      def self.indent(text, level)
                        return text if level == 0
                        spaces = '    ' * level
                        text.split("\\n").map { |line| 
                          line.empty? ? line : spaces + line 
                        }.join("\\n")
                      end
                    end
                  end
                end
              end
            end
          RUBY
        end
        
        def generate_parameter_collection
          return "" if !@options[:attributes] || @options[:attributes].empty?
          
          lines = []
          @options[:attributes].each do |key, type|
            # Check if this is a binding property (starts with @)
            is_binding = key.start_with?('@')
            actual_key = is_binding ? key[1..-1] : key
            
            lines << "            if json_data['#{actual_key}']"
            lines << "              value = json_data['#{actual_key}']"
            lines << "              if value.is_a?(String) && value.match?(/@\\{([^}]+)\\}/)"
            lines << "                # Handle binding"
            lines << "                prop_name = value[2..-2]"
            lines << "                params << \"#{actual_key} = data.\#{prop_name}\""
            lines << "              else"
            lines << "                # Handle static value"
            lines << "                formatted_value = format_value.call(value, '#{type}')"
            lines << "                params << \"#{actual_key} = \#{formatted_value}\" if formatted_value"
            lines << "              end"
            lines << "            end"
          end
          lines.join("\n")
        end
        
        def to_snake_case(str)
          str.gsub(/([A-Z]+)([A-Z][a-z])/,'\1_\2')
             .gsub(/([a-z\d])([A-Z])/,'\1_\2')
             .downcase
        end
      end
    end
  end
end