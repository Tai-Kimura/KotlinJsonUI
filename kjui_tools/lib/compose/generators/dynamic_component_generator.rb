# frozen_string_literal: true

require 'fileutils'
require_relative '../../core/logger'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module Compose
    module Generators
      class DynamicComponentGenerator
        def initialize(name, options = {})
          @name = name
          @component_name = name  # PascalCase name
          @class_name = "Dynamic#{name}Component"
          @options = options
          @logger = Core::Logger
        end

        def generate
          create_dynamic_component_file
          update_dynamic_registry
        end

        private

        def create_dynamic_component_file
          config = Core::ConfigManager.load_config
          
          # Use config directory if available (where kjui.config.json was found)
          base_path = config['_config_dir'] || Dir.pwd
          source_directory = config['source_directory'] || 'src/main'
          package_name = config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.kotlinjsonui.sample'
          
          # Create dynamic components directory in debug source set
          dynamic_dir = File.join(
            base_path,
            source_directory.gsub('main', 'debug'),  # Replace main with debug
            'kotlin',
            package_name.gsub('.', '/'),
            'dynamic/components/extensions'
          )
          FileUtils.mkdir_p(dynamic_dir)
          
          file_path = File.join(dynamic_dir, "#{@class_name}.kt")
          
          if File.exist?(file_path)
            @logger.warn "Dynamic component file already exists: #{file_path}"
            print "Overwrite? (y/n): "
            response = gets.chomp.downcase
            return unless response == 'y'
          end
          
          File.write(file_path, dynamic_template)
          @logger.info "Created dynamic component file: #{file_path}"
        end
        
        def update_dynamic_registry
          config = Core::ConfigManager.load_config
          
          # Use config directory if available (where kjui.config.json was found)
          base_path = config['_config_dir'] || Dir.pwd
          source_directory = config['source_directory'] || 'src/main'
          package_name = config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.kotlinjsonui.sample'
          
          registry_file = File.join(
            base_path,
            source_directory.gsub('main', 'debug'),  # Replace main with debug
            'kotlin',
            package_name.gsub('.', '/'),
            'dynamic/DynamicComponentRegistry.kt'
          )
          
          if !File.exist?(registry_file)
            create_initial_registry
            return
          end
          
          # Read existing registry
          content = File.read(registry_file)
          
          # Check if component already registered
          if content.include?("\"#{@component_name}\"")
            @logger.warn "Component '#{@component_name}' already registered in DynamicComponentRegistry"
            return
          end
          
          # Add new registration
          new_registration = "            \"#{@component_name}\" -> extensions.#{@class_name}.create(json, data)"
          
          # Insert before the else statement in when block
          content.sub!(/(when \(type\) \{.*?)(            else)/m) do
            existing = $1
            else_clause = $2
            "#{existing}#{new_registration}\n#{else_clause}"
          end
          
          # Add import if not present
          import_line = "import com.kotlinjsonui.dynamic.components.extensions.#{@class_name}"
          unless content.include?(import_line)
            content.sub!(/(import com\.kotlinjsonui\.dynamic\.components\.\*\n)/) do
              "#{$1}#{import_line}\n"
            end
          end
          
          File.write(registry_file, content)
          @logger.info "Updated DynamicComponentRegistry with new component"
        end
        
        def create_initial_registry
          config = Core::ConfigManager.load_config
          
          # Use config directory if available (where kjui.config.json was found)
          base_path = config['_config_dir'] || Dir.pwd
          source_directory = config['source_directory'] || 'src/main'
          package_name = config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.kotlinjsonui.sample'
          
          registry_dir = File.join(
            base_path,
            source_directory.gsub('main', 'debug'),  # Replace main with debug
            'kotlin',
            package_name.gsub('.', '/'),
            'dynamic'
          )
          FileUtils.mkdir_p(registry_dir)
          
          registry_file = File.join(registry_dir, 'DynamicComponentRegistry.kt')
          
          config = Core::ConfigManager.load_config
          package_name = config['package_name'] || 'com.example.kotlinjsonui.sample'
          
          content = <<~KOTLIN
            package #{package_name}.dynamic
            
            import androidx.compose.runtime.Composable
            import com.google.gson.JsonObject
            import com.kotlinjsonui.dynamic.components.*
            import #{package_name}.dynamic.components.extensions.#{@class_name}
            
            /**
             * Registry for dynamic custom components
             * Auto-generated by kjui converter generator
             */
            object DynamicComponentRegistry {
                @Composable
                fun createCustomComponent(
                    type: String,
                    json: JsonObject,
                    data: Map<String, Any>
                ): Boolean {
                    return when (type) {
                        "#{@component_name}" -> {
                            extensions.#{@class_name}.create(json, data)
                            true
                        }
                        else -> false
                    }
                }
            }
          KOTLIN
          
          File.write(registry_file, content)
          @logger.info "Created DynamicComponentRegistry with initial component"
        end

        def dynamic_template
          config = Core::ConfigManager.load_config
          package_name = config['package_name'] || 'com.example.kotlinjsonui.sample'
          
          <<~KOTLIN
            package #{package_name}.dynamic.components.extensions
            
            import androidx.compose.foundation.layout.*
            import androidx.compose.material3.*
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.unit.dp
            import com.google.gson.JsonObject
            import com.kotlinjsonui.dynamic.DynamicView
            import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
            #{generate_dynamic_imports}
            
            /**
             * Dynamic #{@component_name} Component
             * Generated by kjui converter generator
             * 
             * Supported JSON attributes:
            #{generate_attribute_docs}
             */
            class #{@class_name} {
                companion object {
                    @Composable
                    fun create(
                        json: JsonObject,
                        data: Map<String, Any> = emptyMap()
                    ) {
                        // Parse parameters
            #{generate_dynamic_parameter_parsing}
                        
                        // Check if this is a container
                        val children = json.get("children")?.asJsonArray 
                            ?: json.get("child")?.asJsonArray
                        val isContainer = children != null && children.size() > 0
                        
                        // Build modifier
                        val modifier = ModifierBuilder.buildModifier(json)
                        
                        if (isContainer) {
                            // Container component with children
                            Box(modifier = modifier) {
                                children?.forEach { childJson ->
                                    if (childJson.isJsonObject) {
                                        DynamicView(
                                            json = childJson.asJsonObject,
                                            data = data
                                        )
                                    }
                                }
                            }
                        } else {
                            // Non-container component
                            Box(modifier = modifier) {
                                // TODO: Implement custom component logic
                                Text("#{@component_name}")
                            }
                        }
                    }
                    
            #{generate_helper_methods}
                }
            }
          KOTLIN
        end
        
        def generate_dynamic_imports
          return "" if !@options[:attributes] || @options[:attributes].empty?
          
          imports = []
          @options[:attributes].each do |key, type|
            case type.downcase
            when 'alignment'
              imports << "import androidx.compose.ui.Alignment"
            when 'text', 'string'
              imports << "import androidx.compose.ui.text.style.TextAlign"
            end
          end
          
          imports.uniq.join("\n")
        end
        
        def generate_attribute_docs
          return " * - child/children: Array of child components" if !@options[:attributes] || @options[:attributes].empty?
          
          docs = [" * - child/children: Array of child components"]
          @options[:attributes].each do |key, type|
            is_binding = key.start_with?('@')
            actual_key = is_binding ? key[1..-1] : key
            binding_note = is_binding ? " (supports @{binding})" : ""
            docs << " * - #{actual_key}: #{type}#{binding_note}"
          end
          
          docs.join("\n")
        end
        
        def generate_dynamic_parameter_parsing
          return "" if !@options[:attributes] || @options[:attributes].empty?
          
          lines = []
          @options[:attributes].each do |key, type|
            is_binding = key.start_with?('@')
            actual_key = is_binding ? key[1..-1] : key
            
            lines << "            val #{actual_key} = parse#{type.capitalize}(json.get(\"#{actual_key}\"), data)"
          end
          
          lines.join("\n")
        end
        
        def generate_helper_methods
          return "" if !@options[:attributes] || @options[:attributes].empty?
          
          methods = []
          types_added = []
          
          @options[:attributes].each do |key, type|
            next if types_added.include?(type.downcase)
            types_added << type.downcase
            
            case type.downcase
            when 'string', 'text'
              methods << string_parser_method
            when 'int', 'integer'
              methods << int_parser_method
            when 'bool', 'boolean'
              methods << bool_parser_method
            when 'color'
              methods << color_parser_method
            end
          end
          
          methods.join("\n\n")
        end
        
        def string_parser_method
          <<~KOTLIN
                    private fun parseString(element: com.google.gson.JsonElement?, data: Map<String, Any>): String? {
                        if (element == null || element.isJsonNull) return null
                        
                        val value = element.asString
                        
                        // Check for binding
                        if (value.startsWith("@{") && value.endsWith("}")) {
                            val propertyName = value.substring(2, value.length - 1)
                            return data[propertyName]?.toString()
                        }
                        
                        return value
                    }
          KOTLIN
        end
        
        def int_parser_method
          <<~KOTLIN
                    private fun parseInt(element: com.google.gson.JsonElement?, data: Map<String, Any>): Int? {
                        if (element == null || element.isJsonNull) return null
                        
                        if (element.isJsonPrimitive) {
                            val primitive = element.asJsonPrimitive
                            if (primitive.isNumber) {
                                return primitive.asInt
                            } else if (primitive.isString) {
                                val value = primitive.asString
                                // Check for binding
                                if (value.startsWith("@{") && value.endsWith("}")) {
                                    val propertyName = value.substring(2, value.length - 1)
                                    return (data[propertyName] as? Number)?.toInt()
                                }
                                return value.toIntOrNull()
                            }
                        }
                        
                        return null
                    }
          KOTLIN
        end
        
        def bool_parser_method
          <<~KOTLIN
                    private fun parseBool(element: com.google.gson.JsonElement?, data: Map<String, Any>): Boolean? {
                        if (element == null || element.isJsonNull) return null
                        
                        if (element.isJsonPrimitive) {
                            val primitive = element.asJsonPrimitive
                            if (primitive.isBoolean) {
                                return primitive.asBoolean
                            } else if (primitive.isString) {
                                val value = primitive.asString
                                // Check for binding
                                if (value.startsWith("@{") && value.endsWith("}")) {
                                    val propertyName = value.substring(2, value.length - 1)
                                    return data[propertyName] as? Boolean
                                }
                                return value.toBooleanStrictOrNull()
                            }
                        }
                        
                        return null
                    }
          KOTLIN
        end
        
        def color_parser_method
          <<~KOTLIN
                    private fun parseColor(element: com.google.gson.JsonElement?, data: Map<String, Any>): Color? {
                        if (element == null || element.isJsonNull) return null
                        
                        if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
                            val value = element.asString
                            
                            // Check for binding
                            if (value.startsWith("@{") && value.endsWith("}")) {
                                val propertyName = value.substring(2, value.length - 1)
                                val boundValue = data[propertyName]?.toString()
                                return boundValue?.let { parseColorString(it) }
                            }
                            
                            return parseColorString(value)
                        }
                        
                        return null
                    }
                    
                    private fun parseColorString(value: String): Color? {
                        return if (value.startsWith("#")) {
                            try {
                                Color(android.graphics.Color.parseColor(value))
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    }
          KOTLIN
        end
      end
    end
  end
end