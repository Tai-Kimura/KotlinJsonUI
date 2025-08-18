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
    # Main builder class for converting JSON to Compose - under 300 lines
    class ComposeBuilderV2
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
          # Read and parse JSON
          json_content = File.read(json_file)
          json_data = JSON.parse(json_content)
          
          # Load and merge styles
          json_data = StyleLoader.load_and_merge(json_data)
          
          # Initialize imports collector
          @required_imports = Set.new
          
          # Generate Compose code
          compose_code = generate_compose_code(base_name, json_data)
          
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
          Core::Logger.debug e.backtrace.join("\n")
        end
      end
      
      private
      
      def generate_compose_code(view_name, json_data)
        pascal_name = to_pascal_case(view_name)
        
        code = <<~KOTLIN
        package #{@package_name}.views
        
        import androidx.compose.foundation.layout.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.unit.sp
        import #{@package_name}.data.#{pascal_name}Data
        import #{@package_name}.viewmodels.#{pascal_name}ViewModel
        
        @Composable
        fun #{pascal_name}View(
            viewModel: #{pascal_name}ViewModel = remember { #{pascal_name}ViewModel() },
            data: #{pascal_name}Data = #{pascal_name}Data()
        ) {
            val currentData = remember { mutableStateOf(data) }
            
        KOTLIN
        
        # Generate component tree
        component_code = generate_component(json_data, 1)
        code += indent(component_code, 1)
        code += "}\n"
        code
      end
      
      def generate_component(json_data, depth = 0)
        return "" unless json_data.is_a?(Hash)
        
        component_type = json_data['type'] || 'View'
        
        # Handle includes
        if json_data['include']
          return generate_include(json_data, depth)
        end
        
        # Delegate to specific component generators
        case component_type
        when 'ScrollView'
          Components::ScrollViewComponent.generate(json_data, depth, @required_imports)
        when 'SafeAreaView'
          generate_safe_area_view(json_data, depth)
        when 'View', 'VStack', 'HStack', 'ZStack'
          Components::ContainerComponent.generate(json_data, depth, @required_imports)
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
      
      def generate_safe_area_view(json_data, depth)
        # SafeAreaView handled as Box with systemBarsPadding
        code = "Box("
        
        modifiers = ["Modifier", "fillMaxSize()", "systemBarsPadding()"]
        modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
        modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, @required_imports))
        
        code += "\n" + indent("modifier = #{modifiers.join('.')}", depth + 1)
        code += "\n" + indent(") {", depth)
        
        # Add children
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
        
        code = "#{pascal_name}View(\n"
        code += indent("viewModel = viewModel.#{to_camel_case(include_name)}ViewModel,", depth + 1) + "\n"
        
        if json_data['data']
          code += indent("data = #{pascal_name}Data(", depth + 1) + "\n"
          json_data['data'].each do |key, value|
            processed_value = process_data_binding(value.to_s)
            code += indent("#{key} = #{processed_value},", depth + 2) + "\n"
          end
          code += indent(")", depth + 1) + "\n"
        end
        
        code += indent(")", depth)
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
        imports_to_add = []
        
        @required_imports.each do |import_type|
          case import_type
          when :lazy_column
            imports_to_add << "import androidx.compose.foundation.lazy.LazyColumn"
          when :lazy_row
            imports_to_add << "import androidx.compose.foundation.lazy.LazyRow"
          when :background
            imports_to_add << "import androidx.compose.foundation.background"
          when :border
            imports_to_add << "import androidx.compose.foundation.border"
          when :shape
            imports_to_add << "import androidx.compose.foundation.shape.RoundedCornerShape"
            imports_to_add << "import androidx.compose.ui.draw.clip"
          when :text_align
            imports_to_add << "import androidx.compose.ui.text.style.TextAlign"
          when :visual_transformation
            imports_to_add << "import androidx.compose.ui.text.input.PasswordVisualTransformation"
          end
        end
        
        # Insert new imports after package declaration
        if imports_to_add.any?
          lines = content.split("\n")
          package_index = lines.find_index { |line| line.start_with?("package ") }
          
          if package_index
            last_import_index = package_index
            lines.each_with_index do |line, i|
              if i > package_index && line.start_with?("import ")
                last_import_index = i
              end
            end
            
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
        return "\"#{text}\"" unless text.is_a?(String)
        
        if text.match(/@\{([^}]+)\}/)
          variable = $1
          if variable.include?(' ?? ')
            parts = variable.split(' ?? ')
            var_name = parts[0].strip
            "\"\\${data.#{var_name}}\""
          else
            "\"\\${data.#{variable}}\""
          end
        else
          "\"#{text.gsub('"', '\\"')}\""
        end
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