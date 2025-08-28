#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require 'set'
require 'nokogiri'
require_relative '../core/json_loader'
require_relative '../core/style_loader'
require_relative 'helpers/component_mapper'
require_relative 'helpers/attribute_mapper'
require_relative 'helpers/binding_parser'

module XmlGenerator
  class Generator
    def initialize(layout_name, config)
      @layout_name = layout_name
      @config = config
      @json_loader = JsonLoader.new(config)
      @style_loader = StyleLoader.new(config)
      @component_mapper = ComponentMapper.new
      @attribute_mapper = AttributeMapper.new
      @binding_parser = BindingParser.new
    end

    def generate
      puts "Generating XML for #{@layout_name}..."
      
      # Load JSON
      json_content = @json_loader.load_layout(@layout_name)
      if json_content.nil?
        puts "Error: Could not load layout #{@layout_name}"
        return false
      end

      # Parse JSON
      layout_data = JSON.parse(json_content)
      
      # Apply styles
      layout_data = @style_loader.apply_styles(layout_data)
      
      # Generate XML
      xml_content = generate_xml(layout_data)
      
      # Save XML file
      save_xml(xml_content)
      
      true
    rescue => e
      puts "Error generating XML: #{e.message}"
      puts e.backtrace if @config['debug']
      false
    end

    private

    def generate_xml(json_data)
      # Check if layout uses data binding
      has_binding = check_for_bindings(json_data)
      
      if has_binding
        generate_data_binding_xml(json_data)
      else
        generate_regular_xml(json_data)
      end
    end

    def check_for_bindings(json_data)
      # Recursively check for @{} syntax in the JSON
      json_string = json_data.to_json
      json_string.include?('@{')
    end

    def generate_data_binding_xml(json_data)
      # Extract all binding variables
      variables = extract_binding_variables(json_data)
      
      builder = Nokogiri::XML::Builder.new(encoding: 'UTF-8') do |xml|
        xml.comment " Generated from #{@layout_name}.json with Data Binding "
        xml.comment " DO NOT EDIT MANUALLY - Use 'kjui generate' to update "
        
        # Create layout root for data binding
        xml.layout('xmlns:android' => 'http://schemas.android.com/apk/res/android',
                  'xmlns:app' => 'http://schemas.android.com/apk/res-auto',
                  'xmlns:tools' => 'http://schemas.android.com/tools') do
          
          # Add data section
          xml.data do
            # Add common imports
            xml.import(type: 'android.view.View')
            
            # Add data variable
            if json_data['data']
              data_class = "#{camelize(@layout_name)}Data"
              xml.variable(name: 'data', type: "com.example.kotlinjsonui.data.#{data_class}")
            end
            
            # Add viewModel variable if there are onClick handlers
            if has_click_handlers?(json_data)
              view_model_class = "#{camelize(@layout_name)}ViewModel"
              xml.variable(name: 'viewModel', type: "com.example.kotlinjsonui.viewmodel.#{view_model_class}")
            end
          end
          
          # Add the actual layout content
          create_xml_element(xml, json_data, true)
        end
      end
      
      # Format the XML nicely
      doc = Nokogiri::XML(builder.to_xml)
      doc.to_xml(indent: 4)
    end

    def generate_regular_xml(json_data)
      builder = Nokogiri::XML::Builder.new(encoding: 'UTF-8') do |xml|
        xml.comment " Generated from #{@layout_name}.json "
        xml.comment " DO NOT EDIT MANUALLY - Use 'kjui generate' to update "
        
        # Create root layout
        create_xml_element(xml, json_data, true)
      end
      
      # Format the XML nicely
      doc = Nokogiri::XML(builder.to_xml)
      doc.to_xml(indent: 4)
    end

    def extract_binding_variables(json_data)
      variables = Set.new
      extract_variables_recursive(json_data, variables)
      variables
    end

    def extract_variables_recursive(data, variables)
      if data.is_a?(Hash)
        data.each do |key, value|
          if value.is_a?(String) && value.start_with?('@{')
            # Extract variable name from binding expression
            if value.match(/@\{([^}]+)\}/)
              expr = $1
              # Simple variable extraction (can be enhanced)
              if expr.match(/^(\w+)/)
                variables.add($1)
              end
            end
          elsif value.is_a?(Hash) || value.is_a?(Array)
            extract_variables_recursive(value, variables)
          end
        end
      elsif data.is_a?(Array)
        data.each { |item| extract_variables_recursive(item, variables) }
      end
    end

    def has_click_handlers?(json_data)
      json_string = json_data.to_json
      json_string.include?('"onClick"') || json_string.include?('"onclick"')
    end

    def camelize(snake_case)
      snake_case.split('_').map(&:capitalize).join
    end

    def create_xml_element(xml, json_element, is_root = false)
      # Map JSON type to Android view class
      view_class = @component_mapper.map_component(json_element['type'])
      
      # Prepare all attributes first
      attrs = {}
      
      # Add ID if present
      if json_element['id']
        attrs['android:id'] = "@+id/#{json_element['id']}"
      end
      
      # Layout dimensions
      attrs['android:layout_width'] = @attribute_mapper.map_dimension(
        json_element['width'] || (is_root ? 'match_parent' : 'wrap_content')
      )
      attrs['android:layout_height'] = @attribute_mapper.map_dimension(
        json_element['height'] || (is_root ? 'match_parent' : 'wrap_content')
      )
      
      # Add orientation for LinearLayout
      if view_class == 'LinearLayout' && json_element['orientation']
        attrs['android:orientation'] = json_element['orientation']
      elsif view_class == 'LinearLayout'
        # Default to vertical if not specified
        attrs['android:orientation'] = 'vertical'
      end
      
      # Map all other attributes
      json_element.each do |key, value|
        next if ['type', 'child', 'children', 'id', 'width', 'height', 'style', 'data', 'orientation'].include?(key)
        
        android_attr = @attribute_mapper.map_attribute(key, value, json_element['type'])
        if android_attr
          namespace, attr_name = android_attr[:namespace], android_attr[:name]
          attr_value = android_attr[:value]
          
          # Handle data binding
          if attr_value.is_a?(String) && attr_value.start_with?('@{')
            attr_value = process_data_binding(attr_value)
          end
          
          if namespace == 'android'
            attrs["android:#{attr_name}"] = attr_value
          elsif namespace == 'app'
            attrs["app:#{attr_name}"] = attr_value
          else
            attrs[attr_name] = attr_value
          end
        end
      end
      
      # Create element with attributes
      xml.send(view_class.split('.').last, attrs) do
        create_children(xml, json_element)
      end
    end


    def create_children(parent_element, json_element)
      # Handle children
      children = json_element['children'] || json_element['child']
      return unless children
      
      children = [children] unless children.is_a?(Array)
      
      children.each do |child|
        create_xml_element(parent_element, child, false)
      end
    end

    def process_data_binding(value)
      # Convert @{variable} to Android data binding format
      if value.start_with?('@{') && value.end_with?('}')
        # Already in binding format, just ensure proper data. prefix
        expr = value[2..-2]
        
        # Add data. prefix if it's a simple variable
        if expr.match?(/^\w+$/)
          "@{data.#{expr}}"
        elsif expr.include?('(') && !expr.include?('viewModel.')
          # Method call without viewModel prefix
          "@{viewModel.#{expr}}"
        else
          # Keep as is (already has proper prefix or is complex expression)
          value
        end
      else
        value
      end
    end

    def save_xml(xml_content)
      # Determine output path  
      output_dir = File.join(@config['project_path'], 'src', 'main', 'res', 'layout')
      output_dir = File.join(@config['project_path'], 'app', 'src', 'main', 'res', 'layout') if File.exist?(File.join(@config['project_path'], 'app'))
      FileUtils.mkdir_p(output_dir)
      
      output_file = File.join(output_dir, "#{@layout_name.downcase}.xml")
      
      # Save XML file
      File.write(output_file, xml_content)
      puts "✅ Generated: #{output_file}"
    end
  end
end