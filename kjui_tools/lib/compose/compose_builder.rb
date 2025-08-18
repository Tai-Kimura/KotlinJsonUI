# frozen_string_literal: true

require 'json'
require 'fileutils'
require 'set'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative '../core/logger'
require_relative 'style_loader'
require_relative 'data_model_updater'
require_relative 'helpers/modifier_builder'
require_relative 'components/text_component'
require_relative 'components/button_component'
require_relative 'components/textfield_component'
require_relative 'components/container_component'
require_relative 'components/image_component'
require_relative 'components/scrollview_component'

module KjuiTools
  module Compose
    # Refactored ComposeBuilder - under 300 lines
    class ComposeBuilder
      def initialize
        @config = Core::ConfigManager.load_config
        @source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
        source_directory = @config['source_directory'] || 'src/main'
        @layouts_dir = File.join(@source_path, source_directory, @config['layouts_directory'] || 'assets/Layouts')
        @view_dir = File.join(@source_path, source_directory, @config['view_directory'] || 'kotlin/views')
        @package_name = @config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.app'
        
        FileUtils.mkdir_p(@view_dir) unless File.exist?(@view_dir)
      end
      
      def build(options = {})
        json_files = Dir.glob(File.join(@layouts_dir, '**/*.json'))
        
        if json_files.empty?
          Core::Logger.warn "No JSON files found in #{@layouts_dir}"
          return
        end
        
        # Update data models first
        data_updater = DataModelUpdater.new
        data_updater.update_data_models
        
        # Build each JSON file
        json_files.each { |file| build_file(file) }
      end
      
      def build_file(json_file)
        base_name = File.basename(json_file, '.json')
        snake_case_name = to_snake_case(base_name)
        pascal_case_name = to_pascal_case(base_name)
        
        begin
          json_content = File.read(json_file)
          json_data = JSON.parse(json_content)
          json_data = StyleLoader.load_and_merge(json_data)
          
          @required_imports = Set.new
          
          # Find the GeneratedView file
          generated_view_file = File.join(@view_dir, snake_case_name, "#{pascal_case_name}GeneratedView.kt")
          
          if File.exist?(generated_view_file)
            update_generated_file(generated_view_file, json_data)
          else
            Core::Logger.warn "GeneratedView file not found: #{generated_view_file}"
          end
          
        rescue JSON::ParserError => e
          Core::Logger.error "Failed to parse #{json_file}: #{e.message}"
        rescue => e
          Core::Logger.error "Failed to process #{json_file}: #{e.message}"
        end
      end
      
      private
      
      def generate_component(json_data, depth = 0)
        return "" unless json_data.is_a?(Hash)
        
        component_type = json_data['type'] || 'View'
        
        # Handle includes
        return generate_include(json_data, depth) if json_data['include']
        
        # Generate component based on type
        case component_type
        when 'ScrollView'
          result = Components::ScrollViewComponent.generate(json_data, depth, @required_imports)
          handle_container_result(result, depth)
        when 'SafeAreaView'
          generate_safe_area_view(json_data, depth)
        when 'View'
          result = Components::ContainerComponent.generate(json_data, depth, @required_imports)
          handle_container_result(result, depth)
        when 'Text', 'Label'
          Components::TextComponent.generate(json_data, depth, @required_imports)
        when 'Button'
          Components::ButtonComponent.generate(json_data, depth, @required_imports)
        when 'Image'
          Components::ImageComponent.generate(json_data, depth, @required_imports)
        when 'TextField'
          Components::TextFieldComponent.generate(json_data, depth, @required_imports)
        when 'Spacer'
          "Spacer(modifier = Modifier.height(#{json_data['height'] || 8}.dp))"
        else
          "// TODO: Implement component type: #{component_type}"
        end
      end
      
      def handle_container_result(result, depth)
        if result.is_a?(Hash)
          code = result[:code]
          children = result[:children] || []
          
          children.each do |child|
            child_code = generate_component(child, depth + 1)
            code += "\n" + child_code unless child_code.empty?
          end
          
          code += result[:closing] if result[:closing]
          code
        else
          result
        end
      end
      
      def generate_safe_area_view(json_data, depth)
        code = indent("Box(", depth)
        
        modifiers = ["Modifier", ".fillMaxSize()", ".systemBarsPadding()"]
        modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
        modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, @required_imports))
        
        code += Helpers::ModifierBuilder.format(modifiers, depth)
        code += "\n" + indent(") {", depth)
        
        children = json_data['child'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 1)
          code += "\n" + child_code unless child_code.empty?
        end
        
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_include(json_data, depth)
        include_name = json_data['include']
        pascal_name = to_pascal_case(include_name)
        
        code = indent("#{pascal_name}View(", depth)
        code += "\n" + indent("viewModel = viewModel.#{to_camel_case(include_name)}ViewModel,", depth + 1)
        
        if json_data['data']
          code += "\n" + indent("data = #{pascal_name}Data(", depth + 1)
          json_data['data'].each do |key, value|
            processed = process_data_binding(value.to_s)
            code += "\n" + indent("#{key} = #{processed},", depth + 2)
          end
          code += "\n" + indent(")", depth + 1)
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def update_generated_file(file_path, json_data)
        existing_content = File.read(file_path)
        
        if existing_content.include?('// >>> GENERATED_CODE_START') && 
           existing_content.include?('// >>> GENERATED_CODE_END')
          
          composable_content = generate_component(json_data, 1)
          
          updated_content = existing_content.gsub(
            /\/\/ >>> GENERATED_CODE_START.*?\/\/ >>> GENERATED_CODE_END/m,
            "// >>> GENERATED_CODE_START\n    #{composable_content}    // >>> GENERATED_CODE_END"
          )
          
          updated_content = update_imports(updated_content)
          File.write(file_path, updated_content)
          Core::Logger.success "Updated: #{file_path}"
        else
          Core::Logger.warn "Generated code markers not found in #{file_path}"
        end
      end
      
      def update_imports(content)
        imports_map = {
          lazy_column: "import androidx.compose.foundation.lazy.LazyColumn",
          lazy_row: "import androidx.compose.foundation.lazy.LazyRow",
          background: "import androidx.compose.foundation.background",
          border: "import androidx.compose.foundation.border",
          shape: ["import androidx.compose.foundation.shape.RoundedCornerShape",
                  "import androidx.compose.ui.draw.clip"],
          text_align: "import androidx.compose.ui.text.style.TextAlign",
          visual_transformation: "import androidx.compose.ui.text.input.PasswordVisualTransformation"
        }
        
        imports_to_add = []
        @required_imports.each do |import_type|
          import_lines = imports_map[import_type]
          if import_lines
            if import_lines.is_a?(Array)
              imports_to_add.concat(import_lines)
            else
              imports_to_add << import_lines
            end
          end
        end
        
        if imports_to_add.any?
          lines = content.split("\n")
          package_index = lines.find_index { |line| line.start_with?("package ") }
          
          if package_index
            last_import_index = lines.each_with_index.select { |line, i| 
              i > package_index && line.start_with?("import ")
            }.map(&:last).max || package_index
            
            imports_to_add.each do |import|
              unless content.include?(import)
                lines.insert(last_import_index + 1, import)
                last_import_index += 1
              end
            end
            
            content = lines.join("\n")
          end
        end
        
        content
      end
      
      def process_data_binding(text)
        return quote(text) unless text.is_a?(String)
        
        if text.match(/@\{([^}]+)\}/)
          variable = $1
          if variable.include?(' ?? ')
            var_name = variable.split(' ?? ')[0].strip
            "\"\\${data.#{var_name}}\""
          else
            "\"\\${data.#{variable}}\""
          end
        else
          quote(text)
        end
      end
      
      def quote(text)
        "\"#{text.gsub('"', '\\"')}\""
      end
      
      def indent(text, level)
        return text if level == 0
        spaces = '    ' * level
        text.split("\n").map { |line| 
          line.empty? ? line : spaces + line 
        }.join("\n")
      end
      
      def to_pascal_case(str)
        str.split(/[_\-]/).map(&:capitalize).join
      end
      
      def to_camel_case(str)
        pascal = to_pascal_case(str)
        pascal[0].downcase + pascal[1..-1]
      end
      
      def to_snake_case(str)
        str.gsub(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
           .gsub(/([a-z\d])([A-Z])/, '\1_\2')
           .downcase
      end
    end
  end
end