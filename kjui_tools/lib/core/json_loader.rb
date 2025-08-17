require 'json'
require 'pathname'

module KjuiTools
  module Core
    # JSON loader for kjui_tools
    # Handles loading and parsing JSON layout files
    class JsonLoader
      attr_accessor :layouts_path, :styles_path
      
      def initialize
        @layouts_path = Pathname.new('Layouts')
        @styles_path = Pathname.new('Styles')
      end
      
      # Set the base paths for layouts and styles
      def set_paths(layouts, styles)
        @layouts_path = Pathname.new(layouts)
        @styles_path = Pathname.new(styles)
      end
      
      # Load a JSON layout file
      def load_layout(file_name)
        file_path = @layouts_path.join("#{file_name}.json")
        
        unless file_path.exist?
          puts "Layout file not found: #{file_path}"
          return nil
        end
        
        begin
          json_content = file_path.read
          json = JSON.parse(json_content)
          
          # Process includes
          process_includes(json)
          # Process styles
          process_styles(json)
          
          json
        rescue JSON::ParserError => e
          puts "Error parsing JSON: #{e.message}"
          nil
        rescue => e
          puts "Error loading layout: #{e.message}"
          nil
        end
      end
      
      # Load a style file
      def load_style(style_name)
        file_path = @styles_path.join("#{style_name}.json")
        
        return nil unless file_path.exist?
        
        begin
          json_content = file_path.read
          JSON.parse(json_content)
        rescue JSON::ParserError => e
          puts "Error parsing style JSON: #{e.message}"
          nil
        rescue => e
          puts "Error loading style: #{e.message}"
          nil
        end
      end
      
      # Process includes in a JSON object
      def process_includes(json)
        return unless json.is_a?(Hash)
        
        if json['include']
          include_name = json['include']
          include_path = @layouts_path.join("#{include_name}.json")
          
          if include_path.exist?
            include_json = JSON.parse(include_path.read)
            
            # Handle data passing
            if json['data']
              merge_data(include_json, json['data'])
            end
            
            # Handle shared_data
            if json['shared_data']
              merge_data(include_json, json['shared_data'])
            end
            
            # Copy all properties from include to current object
            include_json.each do |key, value|
              json[key] ||= value
            end
          end
        end
        
        # Process children
        process_children_includes(json)
      end
      
      # Process includes in children
      def process_children_includes(json)
        return unless json.is_a?(Hash)
        
        # Process child array
        if json['child'].is_a?(Array)
          json['child'].each do |child|
            process_includes(child) if child.is_a?(Hash)
          end
        end
        
        # Process subviews array
        if json['subviews'].is_a?(Array)
          json['subviews'].each do |subview|
            process_includes(subview) if subview.is_a?(Hash)
          end
        end
      end
      
      # Process styles in a JSON object
      def process_styles(json)
        return unless json.is_a?(Hash)
        
        if json['style'].is_a?(String)
          style_name = json['style']
          style_json = load_style(style_name)
          
          if style_json
            # Merge style properties (style properties are base, component properties override)
            style_json.each do |key, value|
              json[key] ||= value
            end
          end
          
          # Remove style attribute after processing
          json.delete('style')
        end
        
        # Process children
        process_children_styles(json)
      end
      
      # Process styles in children
      def process_children_styles(json)
        return unless json.is_a?(Hash)
        
        # Process child array
        if json['child'].is_a?(Array)
          json['child'].each do |child|
            process_styles(child) if child.is_a?(Hash)
          end
        end
        
        # Process subviews array
        if json['subviews'].is_a?(Array)
          json['subviews'].each do |subview|
            process_styles(subview) if subview.is_a?(Hash)
          end
        end
      end
      
      # Merge data into JSON object
      def merge_data(target, data)
        return unless target.is_a?(Hash) && data.is_a?(Hash)
        
        # Handle @{} variable replacement
        data.each do |key, value|
          if value.is_a?(String) && value.start_with?('@{') && value.end_with?('}')
            # This would be handled by the binding system
            # For now, just set the value
            target[key] = value
          else
            target[key] = value
          end
        end
      end
      
      # Get all layout files
      def get_all_layouts
        return [] unless @layouts_path.exist? && @layouts_path.directory?
        
        @layouts_path.children
          .select { |f| f.extname == '.json' && !f.basename.to_s.start_with?('_') }
          .map { |f| f.basename('.json').to_s }
      end
    end
  end
end