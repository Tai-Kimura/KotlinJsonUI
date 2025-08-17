require 'json'
require_relative '../core/json_loader'
require_relative 'data_model_updater'
require_relative 'view_registry'

module KjuiTools
  module Compose
    # Main converter from JSON to Jetpack Compose code
    # Based on SwiftJsonUI's json_to_swiftui_converter.rb
    class JsonToComposeConverter
      attr_accessor :indent_level, :output
      
      def initialize
        @indent_level = 0
        @output = []
        @view_registry = ViewRegistry.new
        @data_model_updater = DataModelUpdater.new
        register_converters
      end
      
      # Register all view converters
      def register_converters
        # Lazy load converters
        Dir[File.join(__dir__, 'views', '*_converter.rb')].each do |file|
          require_relative file
        end
        
        # Register each converter
        @view_registry.register('View', Views::ViewConverter.new)
        @view_registry.register('SafeAreaView', Views::SafeAreaViewConverter.new)
        @view_registry.register('Label', Views::LabelConverter.new)
        @view_registry.register('Button', Views::ButtonConverter.new)
        @view_registry.register('TextField', Views::TextFieldConverter.new)
        @view_registry.register('TextView', Views::TextViewConverter.new)
        @view_registry.register('Switch', Views::SwitchConverter.new)
        @view_registry.register('ScrollView', Views::ScrollViewConverter.new)
        @view_registry.register('Scroll', Views::ScrollViewConverter.new)
        @view_registry.register('Collection', Views::CollectionConverter.new)
        @view_registry.register('Table', Views::TableConverter.new)
        @view_registry.register('Image', Views::ImageConverter.new)
        @view_registry.register('NetworkImage', Views::NetworkImageConverter.new)
        @view_registry.register('Web', Views::WebViewConverter.new)
        # Add more converters as needed
      end
      
      # Convert JSON layout to Compose code
      def convert(json, view_name)
        @output = []
        @indent_level = 0
        
        # Extract data bindings
        data_bindings = extract_data_bindings(json)
        
        # Extract actions
        actions = extract_actions(json)
        
        # Generate imports
        generate_imports
        
        # Generate data class
        generate_data_class(view_name, data_bindings)
        
        # Generate ViewModel
        generate_view_model(view_name, data_bindings, actions)
        
        # Generate Composable function
        generate_composable(json, view_name)
        
        @output.join("\n")
      end
      
      private
      
      # Generate import statements
      def generate_imports
        add_line "package com.example.app.ui.screens"
        add_line ""
        add_line "import androidx.compose.foundation.*"
        add_line "import androidx.compose.foundation.layout.*"
        add_line "import androidx.compose.material3.*"
        add_line "import androidx.compose.runtime.*"
        add_line "import androidx.compose.ui.*"
        add_line "import androidx.compose.ui.graphics.*"
        add_line "import androidx.compose.ui.text.*"
        add_line "import androidx.compose.ui.text.font.*"
        add_line "import androidx.compose.ui.unit.*"
        add_line "import androidx.compose.ui.Alignment"
        add_line "import androidx.compose.ui.Modifier"
        add_line "import androidx.lifecycle.ViewModel"
        add_line "import androidx.lifecycle.viewmodel.compose.viewModel"
        add_line "import kotlinx.coroutines.flow.MutableStateFlow"
        add_line "import kotlinx.coroutines.flow.StateFlow"
        add_line "import kotlinx.coroutines.flow.asStateFlow"
        add_line "import coil.compose.AsyncImage"
        add_line ""
      end
      
      # Generate data class from JSON
      def generate_data_class(view_name, data_bindings)
        add_line "/**"
        add_line " * Data class for #{view_name}"
        add_line " */"
        add_line "data class #{view_name}Data("
        
        indent do
          if data_bindings.empty?
            add_line "val placeholder: String = \"\""
          else
            data_bindings.each_with_index do |binding, index|
              name = binding['name']
              class_name = binding['class'] || 'String'
              default_value = binding['defaultValue']
              
              kotlin_type = map_to_kotlin_type(class_name)
              kotlin_default = parse_default_value(default_value, class_name)
              
              comma = index < data_bindings.length - 1 ? ',' : ''
              add_line "val #{name}: #{kotlin_type} = #{kotlin_default}#{comma}"
            end
          end
        end
        
        add_line ")"
        add_line ""
      end
      
      # Generate ViewModel from JSON
      def generate_view_model(view_name, data_bindings, actions)
        add_line "/**"
        add_line " * ViewModel for #{view_name}"
        add_line " */"
        add_line "class #{view_name}ViewModel : ViewModel() {"
        
        indent do
          add_line "private val _data = MutableStateFlow(#{view_name}Data())"
          add_line "val data: StateFlow<#{view_name}Data> = _data.asStateFlow()"
          add_line ""
          
          # Generate action methods
          actions.each do |action|
            add_line "fun #{action}() {"
            indent do
              add_line "// TODO: Implement #{action}"
            end
            add_line "}"
            add_line ""
          end
        end
        
        add_line "}"
        add_line ""
      end
      
      # Generate Composable function
      def generate_composable(json, view_name)
        add_line "/**"
        add_line " * Composable for #{view_name}"
        add_line " */"
        add_line "@Composable"
        add_line "fun #{view_name}Screen("
        indent do
          add_line "viewModel: #{view_name}ViewModel = viewModel()"
        end
        add_line ") {"
        
        indent do
          add_line "val data by viewModel.data.collectAsState()"
          add_line ""
          
          # Convert the root component
          convert_component(json)
        end
        
        add_line "}"
      end
      
      # Convert a component to Compose code
      def convert_component(json)
        return unless json.is_a?(Hash)
        
        type = json['type'] || 'View'
        converter = @view_registry.get_converter(type)
        
        if converter
          converter.convert(json, self)
        else
          # Default to View converter
          Views::ViewConverter.new.convert(json, self)
        end
      end
      
      # Convert children components
      def convert_children(json)
        return unless json.is_a?(Hash)
        
        # Process child array
        if json['child'].is_a?(Array)
          json['child'].each do |child|
            convert_component(child) if child.is_a?(Hash)
          end
        end
        
        # Process subviews array
        if json['subviews'].is_a?(Array)
          json['subviews'].each do |subview|
            convert_component(subview) if subview.is_a?(Hash)
          end
        end
      end
      
      # Extract data bindings from JSON
      def extract_data_bindings(json)
        return [] unless json.is_a?(Hash) && json['data'].is_a?(Array)
        json['data']
      end
      
      # Extract action names from JSON
      def extract_actions(json, actions = Set.new)
        return actions unless json.is_a?(Hash)
        
        # Extract from current object
        actions.add(json['onclick']) if json['onclick'].is_a?(String)
        actions.add(json['onTextChange']) if json['onTextChange'].is_a?(String)
        actions.add(json['onValueChange']) if json['onValueChange'].is_a?(String)
        actions.add(json['valueChange']) if json['valueChange'].is_a?(String)
        
        # Extract from children
        if json['child'].is_a?(Array)
          json['child'].each do |child|
            extract_actions(child, actions) if child.is_a?(Hash)
          end
        end
        
        if json['subviews'].is_a?(Array)
          json['subviews'].each do |subview|
            extract_actions(subview, actions) if subview.is_a?(Hash)
          end
        end
        
        actions
      end
      
      # Map JSON class type to Kotlin type
      def map_to_kotlin_type(class_name)
        case class_name
        when 'String' then 'String'
        when 'Int' then 'Int'
        when 'Double' then 'Double'
        when 'Float' then 'Float'
        when 'Bool', 'Boolean' then 'Boolean'
        when '[String]' then 'List<String>'
        when '[Int]' then 'List<Int>'
        else 'Any'
        end
      end
      
      # Parse default value string to Kotlin code
      def parse_default_value(default_value, class_name)
        if default_value.nil?
          case class_name
          when 'String' then '""'
          when 'Int' then '0'
          when 'Double' then '0.0'
          when 'Float' then '0f'
          when 'Bool', 'Boolean' then 'false'
          when '[String]', '[Int]' then 'emptyList()'
          else 'null'
          end
        else
          case class_name
          when 'String'
            # Handle string with quotes
            if default_value.start_with?("'") && default_value.end_with?("'")
              "\"#{default_value[1..-2]}\""
            elsif default_value.start_with?('"') && default_value.end_with?('"')
              default_value
            else
              "\"#{default_value}\""
            end
          when 'Bool', 'Boolean'
            default_value
          when '[String]'
            if default_value == '[]'
              'emptyList()'
            else
              # Parse array of strings
              "listOf(#{default_value[1..-2]})"
            end
          else
            default_value
          end
        end
      end
      
      # Add a line to output with proper indentation
      def add_line(line)
        @output << ("    " * @indent_level + line)
      end
      
      # Execute block with increased indentation
      def indent
        @indent_level += 1
        yield
        @indent_level -= 1
      end
    end
  end
end