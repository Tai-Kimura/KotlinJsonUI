#!/usr/bin/env ruby

module XmlGenerator
  module Mappers
    class StyleMapper
      def initialize(text_mapper)
        @text_mapper = text_mapper
      end
      
      def map_style_attributes(key, value)
        case key
        # Background and appearance
        when 'background', 'backgroundColor'
          return { namespace: 'android', name: 'background', value: @text_mapper.send(:convert_color, value) }
        when 'cornerRadius'
          # TODO: Generate drawable with corners
          # For now, add as tools attribute for documentation
          return { namespace: 'tools', name: 'cornerRadius', value: convert_dimension(value) }
        when 'borderWidth', 'strokeWidth'
          # TODO: Generate drawable with stroke
          return { namespace: 'tools', name: 'strokeWidth', value: convert_dimension(value) }
        when 'borderColor', 'strokeColor'
          # TODO: Generate drawable with stroke color
          return { namespace: 'tools', name: 'strokeColor', value: @text_mapper.send(:convert_color, value) }
        when 'borderStyle'
          # TODO: Handle border style in drawable
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
        end
        
        nil
      end
      
      private
      
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