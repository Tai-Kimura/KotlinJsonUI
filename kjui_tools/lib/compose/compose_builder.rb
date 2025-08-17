# frozen_string_literal: true

require 'json'
require 'fileutils'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative '../core/logger'
require_relative 'style_loader'
require_relative 'data_model_updater'

module KjuiTools
  module Compose
    class ComposeBuilder
      def initialize
        @config = Core::ConfigManager.load_config
        @source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
        @layouts_dir = File.join(@source_path, @config['layouts_directory'] || 'assets/Layouts')
        @view_dir = File.join(@source_path, @config['view_directory'] || 'app/src/main/kotlin/views')
        @package_name = Core::ProjectFinder.get_package_name || 'com.example.app'
        
        # Create view directory if it doesn't exist
        FileUtils.mkdir_p(@view_dir) unless File.exist?(@view_dir)
      end
      
      def build(options = {})
        # Process all JSON files in Layouts directory
        json_files = Dir.glob(File.join(@layouts_dir, '**/*.json'))
        
        if json_files.empty?
          Core::Logger.warn "No JSON files found in #{@layouts_dir}"
          return
        end
        
        # Update data models first
        data_updater = DataModelUpdater.new
        data_updater.update_data_models
        
        # Build each JSON file
        json_files.each do |json_file|
          build_file(json_file)
        end
      end
      
      def build_file(json_file)
        relative_path = Pathname.new(json_file).relative_path_from(Pathname.new(@layouts_dir)).to_s
        base_name = File.basename(json_file, '.json')
        
        begin
          # Read and parse JSON
          json_content = File.read(json_file)
          json_data = JSON.parse(json_content)
          
          # Load and merge styles
          json_data = StyleLoader.load_and_merge(json_data)
          
          # Generate Compose code
          compose_code = generate_compose_code(base_name, json_data)
          
          # Write to view file
          output_file = File.join(@view_dir, "#{to_pascal_case(base_name)}View.kt")
          File.write(output_file, compose_code)
          
          Core::Logger.success "Generated: #{output_file}"
          
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
        import com.kotlinjsonui.compose.ComponentFactory
        
        @Composable
        fun #{pascal_name}View(
            viewModel: #{pascal_name}ViewModel = remember { #{pascal_name}ViewModel() },
            data: #{pascal_name}Data = #{pascal_name}Data()
        ) {
            val currentData = remember { mutableStateOf(data) }
            
            // Build UI from JSON
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
        
        # Generate component based on type
        case component_type
        when 'View', 'VStack', 'HStack', 'ZStack'
          generate_container(json_data, depth)
        when 'Text', 'Label'
          generate_text(json_data, depth)
        when 'Button'
          generate_button(json_data, depth)
        when 'Image'
          generate_image(json_data, depth)
        when 'TextField'
          generate_text_field(json_data, depth)
        when 'Spacer'
          "Spacer(modifier = Modifier.height(#{json_data['height'] || 8}dp))"
        else
          "// TODO: Implement component type: #{component_type}"
        end
      end
      
      def generate_container(json_data, depth)
        container_type = json_data['type'] || 'View'
        
        layout = case container_type
        when 'VStack'
          'Column'
        when 'HStack'
          'Row'
        when 'ZStack'
          'Box'
        else
          'Box'
        end
        
        code = "#{layout}("
        
        # Add modifiers
        modifiers = []
        modifiers << "Modifier"
        modifiers << "fillMaxWidth()" if json_data['fillMaxWidth']
        modifiers << "fillMaxHeight()" if json_data['fillMaxHeight']
        modifiers << "padding(#{json_data['padding'] || 0}dp)" if json_data['padding']
        
        code += "\n#{indent('modifier = ' + modifiers.join('.'), depth + 1)}"
        
        # Add arrangement for Row/Column
        if layout == 'Column' && json_data['verticalArrangement']
          code += ",\n#{indent('verticalArrangement = Arrangement.' + json_data['verticalArrangement'], depth + 1)}"
        elsif layout == 'Row' && json_data['horizontalArrangement']
          code += ",\n#{indent('horizontalArrangement = Arrangement.' + json_data['horizontalArrangement'], depth + 1)}"
        end
        
        # Add alignment
        if json_data['alignment']
          code += ",\n#{indent('horizontalAlignment = Alignment.' + json_data['alignment'], depth + 1)}" if layout == 'Column'
          code += ",\n#{indent('verticalAlignment = Alignment.' + json_data['alignment'], depth + 1)}" if layout == 'Row'
        end
        
        code += "\n#{indent(') {', depth)}\n"
        
        # Add children
        children = json_data['child'] || json_data['children'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 1)
          code += indent(child_code, depth + 1) + "\n" unless child_code.empty?
        end
        
        code += indent('}', depth)
        code
      end
      
      def generate_text(json_data, depth)
        text = process_data_binding(json_data['text'] || '')
        
        code = "Text(\n"
        code += indent("text = #{text},", depth + 1) + "\n"
        
        if json_data['fontSize']
          code += indent("fontSize = #{json_data['fontSize']}sp,", depth + 1) + "\n"
        end
        
        if json_data['color']
          code += indent("color = MaterialTheme.colorScheme.#{json_data['color']},", depth + 1) + "\n"
        end
        
        code += indent("modifier = Modifier", depth + 1)
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}dp)"
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_button(json_data, depth)
        text = process_data_binding(json_data['title'] || json_data['text'] || 'Button')
        onclick = json_data['onclick']
        
        code = "Button(\n"
        
        if onclick
          code += indent("onClick = { viewModel.#{onclick}() },", depth + 1) + "\n"
        else
          code += indent("onClick = { },", depth + 1) + "\n"
        end
        
        code += indent("modifier = Modifier", depth + 1)
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}dp)"
        end
        
        code += "\n" + indent(") {", depth) + "\n"
        code += indent("Text(#{text})", depth + 1) + "\n"
        code += indent("}", depth)
        code
      end
      
      def generate_image(json_data, depth)
        image_name = json_data['name'] || json_data['source'] || 'placeholder'
        
        code = "Image(\n"
        code += indent("painter = painterResource(id = R.drawable.#{image_name}),", depth + 1) + "\n"
        code += indent("contentDescription = #{quote(json_data['contentDescription'] || '')},", depth + 1) + "\n"
        code += indent("modifier = Modifier", depth + 1)
        
        if json_data['width'] && json_data['height']
          code += ".size(#{json_data['width']}dp, #{json_data['height']}dp)"
        elsif json_data['size']
          code += ".size(#{json_data['size']}dp)"
        end
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}dp)"
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_text_field(json_data, depth)
        value = process_data_binding(json_data['value'] || '')
        placeholder = json_data['placeholder'] || ''
        
        code = "TextField(\n"
        code += indent("value = #{value},", depth + 1) + "\n"
        code += indent("onValueChange = { newValue -> currentData.value = currentData.value.copy(#{extract_variable_name(json_data['value'])} = newValue) },", depth + 1) + "\n"
        
        if placeholder
          code += indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1) + "\n"
        end
        
        code += indent("modifier = Modifier", depth + 1)
        
        if json_data['fillMaxWidth']
          code += ".fillMaxWidth()"
        end
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}dp)"
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_include(json_data, depth)
        include_name = json_data['include']
        pascal_name = to_pascal_case(include_name)
        
        code = "#{pascal_name}View(\n"
        code += indent("viewModel = viewModel.#{to_camel_case(include_name)}ViewModel,", depth + 1) + "\n"
        
        # Pass data if specified
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
      
      def process_data_binding(text)
        return quote(text) unless text.is_a?(String)
        
        # Check for data binding syntax @{variable}
        if text.match(/@\{([^}]+)\}/)
          variable = $1
          if variable.include?('.')
            "currentData.value.#{variable}"
          else
            "currentData.value.#{variable}"
          end
        else
          quote(text)
        end
      end
      
      def extract_variable_name(text)
        if text.match(/@\{([^}]+)\}/)
          $1.split('.').last
        else
          'value'
        end
      end
      
      def quote(text)
        "\"#{text.gsub('"', '\\"')}\""
      end
      
      def indent(text, level)
        spaces = '    ' * level
        text.split("\n").map { |line| spaces + line }.join("\n")
      end
      
      def to_pascal_case(str)
        str.split(/[_\-]/).map(&:capitalize).join
      end
      
      def to_camel_case(str)
        pascal = to_pascal_case(str)
        pascal[0].downcase + pascal[1..-1]
      end
    end
  end
end