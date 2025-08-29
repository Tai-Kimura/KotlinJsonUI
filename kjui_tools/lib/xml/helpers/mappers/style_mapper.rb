#!/usr/bin/env ruby

module XmlGenerator
  module Mappers
    class StyleMapper
      def initialize(text_mapper, drawable_generator = nil)
        @text_mapper = text_mapper
        @drawable_generator = drawable_generator
      end
      
      def map_style_attributes(key, value, json_element = nil, component_type = nil)
        case key
        # Background and appearance
        when 'background', 'backgroundColor'
          # Check if we need to generate a drawable
          if @drawable_generator && json_element && needs_drawable?(json_element, component_type)
            drawable_name = @drawable_generator.get_background_drawable(json_element, component_type)
            if drawable_name
              return { namespace: 'android', name: 'background', value: "@drawable/#{drawable_name}" }
            end
          end
          return { namespace: 'android', name: 'background', value: @text_mapper.send(:convert_color, value) }
        when 'cornerRadius'
          # Handled in drawable generation
          return nil if @drawable_generator
          return { namespace: 'tools', name: 'cornerRadius', value: convert_dimension(value) }
        when 'borderWidth', 'strokeWidth'
          # Handled in drawable generation
          return nil if @drawable_generator
          return { namespace: 'tools', name: 'strokeWidth', value: convert_dimension(value) }
        when 'borderColor', 'strokeColor'
          # Handled in drawable generation
          return nil if @drawable_generator
          return { namespace: 'tools', name: 'strokeColor', value: @text_mapper.send(:convert_color, value) }
        when 'borderStyle'
          # Handled in drawable generation if available
          return nil if @drawable_generator
          return { namespace: 'tools', name: 'borderStyle', value: value }
        when 'opacity', 'alpha'
          return { namespace: 'android', name: 'alpha', value: value.to_f.to_s }
        when 'visibility'
          return { namespace: 'android', name: 'visibility', value: map_visibility(value) }
        when 'enabled'
          return { namespace: 'android', name: 'enabled', value: value.to_s }
        when 'clickable'
          return { namespace: 'android', name: 'clickable', value: value.to_s }
        when 'focusable'
          return { namespace: 'android', name: 'focusable', value: value.to_s }
          
        # Image attributes
        when 'src', 'source', 'image'
          return map_image_source(value)
        when 'scaleType'
          return { namespace: 'android', name: 'scaleType', value: map_scale_type(value) }
        when 'tint'
          return { namespace: 'android', name: 'tint', value: @text_mapper.send(:convert_color, value) }
          
        # State-specific attributes (handled by drawable generation)
        when 'disabledBackground', 'tapBackground', 'pressedBackground', 
             'selectedBackground', 'focusedBackground', 'checkedBackground',
             'rippleColor', 'rippleBorderless'
          # These are handled by drawable generation
          return nil if @drawable_generator
          return { namespace: 'tools', name: key, value: value.to_s }
        end
        
        nil
      end
      
      private
      
      def needs_drawable?(json_element, component_type)
        return false unless json_element
        
        # Check if any drawable-related attributes exist
        json_element['cornerRadius'] ||
        json_element['borderWidth'] ||
        json_element['borderColor'] ||
        json_element['gradient'] ||
        json_element['disabledBackground'] ||
        json_element['tapBackground'] ||
        json_element['pressedBackground'] ||
        json_element['selectedBackground'] ||
        json_element['focusedBackground'] ||
        json_element['checkedBackground'] ||
        json_element['onClick'] ||
        json_element['onclick'] ||
        json_element['rippleColor'] ||
        ['Button', 'ImageButton', 'Card', 'ListItem'].include?(component_type)
      end
      
      def convert_dimension(value)
        case value
        when Integer, Float
          "#{value.to_i}dp"
        when String
          if value.match?(/^\d+$/)
            "#{value}dp"
          else
            value
          end
        else
          value.to_s
        end
      end
      
      def map_visibility(value)
        case value
        when true, 'visible'
          'visible'
        when false, 'gone'
          'gone'
        when 'invisible'
          'invisible'
        else
          value
        end
      end
      
      def map_image_source(value)
        if value.start_with?('http')
          # Network image - use tools for documentation
          { namespace: 'tools', name: 'src', value: value }
        else
          # Local resource
          resource_name = value.gsub(/\.\w+$/, '').downcase.gsub(/[^a-z0-9_]/, '_')
          { namespace: 'android', name: 'src', value: "@drawable/#{resource_name}" }
        end
      end
      
      def map_scale_type(value)
        scale_type_map = {
          'fill' => 'centerCrop',
          'fit' => 'fitCenter',
          'stretch' => 'fitXY',
          'center' => 'center'
        }
        scale_type_map[value] || value
      end
    end
  end
end