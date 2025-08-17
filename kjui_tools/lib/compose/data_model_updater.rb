# frozen_string_literal: true

require 'json'
require 'fileutils'
require 'set'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative 'style_loader'

module KjuiTools
  module Compose
    class DataModelUpdater
      def initialize
        @config = Core::ConfigManager.load_config
        @source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
        @layouts_dir = File.join(@source_path, @config['layouts_directory'] || 'assets/Layouts')
        @data_dir = File.join(@source_path, @config['data_directory'] || 'app/src/main/kotlin/data')
        @package_name = Core::ProjectFinder.get_package_name || 'com.example.app'
      end

      def update_data_models
        # Process all JSON files in Layouts directory
        json_files = Dir.glob(File.join(@layouts_dir, '**/*.json'))
        
        json_files.each do |json_file|
          process_json_file(json_file)
        end
      end

      private

      def process_json_file(json_file)
        json_content = File.read(json_file)
        json_data = JSON.parse(json_content)
        
        # Load and merge styles into the JSON data
        json_data = StyleLoader.load_and_merge(json_data)
        
        # Extract data properties from JSON
        data_properties = extract_data_properties(json_data)
        
        # Extract onclick actions from JSON (now includes actions from styles)
        onclick_actions = extract_onclick_actions(json_data)
        
        # Always create/update data file, even if no properties
        # Get the view name from file path
        base_name = File.basename(json_file, '.json')
        
        # Update the Data model file
        update_data_file(base_name, data_properties, onclick_actions)
      end
      
      def extract_onclick_actions(json_data, actions = Set.new)
        if json_data.is_a?(Hash)
          # Check for onclick attribute
          if json_data['onclick'] && json_data['onclick'].is_a?(String)
            actions.add(json_data['onclick'])
          end
          
          # Process children
          if json_data['child']
            if json_data['child'].is_a?(Array)
              json_data['child'].each do |child|
                extract_onclick_actions(child, actions)
              end
            else
              extract_onclick_actions(json_data['child'], actions)
            end
          end
        elsif json_data.is_a?(Array)
          json_data.each do |item|
            extract_onclick_actions(item, actions)
          end
        end
        
        actions.to_a
      end

      def extract_data_properties(json_data, properties = [])
        if json_data.is_a?(Hash)
          # Check for data section
          if json_data['data'] && json_data['data'].is_a?(Array)
            json_data['data'].each do |data_item|
              if data_item.is_a?(Hash)
                properties << data_item
              end
            end
          end
          
          # Process children
          if json_data['child']
            if json_data['child'].is_a?(Array)
              json_data['child'].each do |child|
                extract_data_properties(child, properties)
              end
            else
              extract_data_properties(json_data['child'], properties)
            end
          end
        elsif json_data.is_a?(Array)
          json_data.each do |item|
            extract_data_properties(item, properties)
          end
        end
        
        properties
      end

      def update_data_file(base_name, data_properties, onclick_actions = [])
        # Convert base_name to PascalCase for searching
        pascal_view_name = to_pascal_case(base_name)
        
        # Check for existing file with different casing
        existing_file = find_existing_data_file(pascal_view_name)
        
        if existing_file
          # Extract the actual data class name from the existing file
          existing_class_name = extract_class_name(existing_file)
          if existing_class_name
            # Use the exact class name from the existing file
            view_name = existing_class_name.sub(/Data$/, '')
          else
            # Fallback to pascal case if we can't extract the name
            view_name = pascal_view_name
          end
          data_file_path = existing_file
        else
          # For new files, use pascal case
          view_name = pascal_view_name
          data_file_path = File.join(@data_dir, "#{view_name}Data.kt")
          # If file doesn't exist, create it with empty data structure
          unless File.exist?(data_file_path)
            # Create directory if needed
            FileUtils.mkdir_p(@data_dir)
          end
        end
        
        # Generate new content
        content = generate_data_content(view_name, data_properties, onclick_actions)
        
        # Write the updated content
        File.write(data_file_path, content)
        puts "  Updated Data model: #{data_file_path}"
      end
      
      def find_existing_data_file(view_name)
        # Try exact match first
        exact_path = File.join(@data_dir, "#{view_name}Data.kt")
        return exact_path if File.exist?(exact_path)
        
        # Try case-insensitive search
        Dir.glob(File.join(@data_dir, '*Data.kt')).find do |file|
          File.basename(file, '.kt').downcase == "#{view_name}Data".downcase
        end
      end
      
      def extract_class_name(file_path)
        content = File.read(file_path)
        if match = content.match(/data\s+class\s+(\w+Data)\s*\(/)
          match[1]
        else
          nil
        end
      end

      def generate_data_content(view_name, data_properties, onclick_actions = [])
        content = <<~KOTLIN
        package #{@package_name}.data

        import androidx.compose.runtime.MutableState
        import androidx.compose.runtime.mutableStateOf

        data class #{view_name}Data(
        KOTLIN
        
        if data_properties.empty?
            content += "    // No data properties defined in JSON\n"
            content += "    val placeholder: String = \"placeholder\"\n"
        else
          # Add each property with correct type and default value
          data_properties.each_with_index do |prop, index|
            name = prop['name']
            class_type = map_to_kotlin_type(prop['class'])
            default_value = prop['defaultValue']
            
            # If no default value or nil, make it nullable
            if default_value.nil? || default_value == 'nil'
              content += "    var #{name}: #{class_type}? = null"
            else
              formatted_value = format_default_value(default_value, prop['class'])
              content += "    var #{name}: #{class_type} = #{formatted_value}"
            end
            
            # Add comma if not last property
            content += "," if index < data_properties.length - 1
            content += "\n"
          end
        end
        
        content += ") {\n"
        
        # Add companion object with update function
        content += "    companion object {\n"
        content += "        // Update properties from map\n"
        content += "        fun fromMap(map: Map<String, Any>): #{view_name}Data {\n"
        content += "            return #{view_name}Data(\n"
        
        if !data_properties.empty?
          data_properties.each_with_index do |prop, index|
            name = prop['name']
            class_type = prop['class']
            kotlin_type = map_to_kotlin_type(class_type)
            
            # Generate conversion code based on type
            content += "                #{name} = "
            
            case class_type
            when 'String'
              content += "map[\"#{name}\"] as? String ?: \"\""
            when 'Int'
              content += "(map[\"#{name}\"] as? Number)?.toInt() ?: 0"
            when 'Double'
              content += "(map[\"#{name}\"] as? Number)?.toDouble() ?: 0.0"
            when 'Float'
              content += "(map[\"#{name}\"] as? Number)?.toFloat() ?: 0f"
            when 'Bool', 'Boolean'
              content += "map[\"#{name}\"] as? Boolean ?: false"
            else
              # For custom types, try to cast directly
              content += "map[\"#{name}\"] as? #{kotlin_type}"
            end
            
            content += "," if index < data_properties.length - 1
            content += "\n"
          end
        else
          content += "                placeholder = \"placeholder\"\n"
        end
        
        content += "            )\n"
        content += "        }\n"
        content += "    }\n"
        
        # Add toMap function with viewModel parameter
        content += "\n"
        content += "    // Convert properties to map for runtime use\n"
        content += "    fun toMap(viewModel: #{view_name}ViewModel? = null): MutableMap<String, Any> {\n"
        content += "        val map = mutableMapOf<String, Any>()\n"
        
        # Add data properties
        if !data_properties.empty?
          content += "        \n"
          content += "        // Data properties\n"
          data_properties.each do |prop|
            name = prop['name']
            default_value = prop['defaultValue']
            
            # If it's nullable, check for null
            if default_value.nil? || default_value == 'nil'
              content += "        #{name}?.let { map[\"#{name}\"] = it }\n"
            else
              content += "        map[\"#{name}\"] = #{name}\n"
            end
          end
        end
        
        # Add onclick actions if viewModel is provided
        if !onclick_actions.empty?
          content += "        \n"
          content += "        // Add onclick action lambdas if viewModel is provided\n"
          content += "        viewModel?.let { vm ->\n"
          onclick_actions.each do |action|
            content += "            map[\"#{action}\"] = { vm.#{action}() }\n"
          end
          content += "        }\n"
        end
        
        if data_properties.empty? && onclick_actions.empty?
          content += "        // No properties to add\n"
        end
        
        content += "        \n"
        content += "        return map\n"
        content += "    }\n"
        content += "}\n"
        content
      end

      def map_to_kotlin_type(json_class)
        case json_class
        when 'String'
          'String'
        when 'Int'
          'Int'
        when 'Double'
          'Double'
        when 'Float'
          'Float'
        when 'Bool', 'Boolean'
          'Boolean'
        when 'CGFloat'
          'Float'
        else
          # Return as-is for custom types
          json_class
        end
      end

      def format_default_value(value, json_class)
        case json_class
        when 'String'
          # For String class, add quotes
          "\"#{value}\""
        when 'Bool', 'Boolean'
          # Convert string to boolean
          value.downcase == 'true' ? 'true' : 'false'
        when 'Int'
          # Ensure it's an integer
          value.to_i.to_s
        when 'Double'
          # Ensure it's a double
          "#{value.to_f}"
        when 'Float', 'CGFloat'
          # Ensure it's a float with f suffix
          "#{value.to_f}f"
        else
          # For all other cases, use value as-is
          value
        end
      end

      def to_pascal_case(str)
        # Handle various naming patterns
        snake = str.gsub(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
                   .gsub(/([a-z\d])([A-Z])/, '\1_\2')
                   .downcase
        snake.split(/[_\-]/).map(&:capitalize).join
      end
    end
  end
end