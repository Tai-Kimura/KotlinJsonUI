#!/usr/bin/env ruby

module XmlGenerator
  module Mappers
    class TextMapper
      def map_text_attributes(key, value, component_type)
        case key
        when 'text'
          return { namespace: 'android', name: 'text', value: process_text_value(value) }
        when 'hint'
          return { namespace: 'android', name: 'hint', value: value }
        when 'fontSize', 'textSize'
          return { namespace: 'android', name: 'textSize', value: convert_text_size(value) }
        when 'fontColor', 'textColor'
          return { namespace: 'android', name: 'textColor', value: convert_color(value) }
        when 'color'
          # Generic color attribute - determine based on component type
          if ['Label', 'Text', 'TextView', 'Button'].include?(component_type)
            return { namespace: 'android', name: 'textColor', value: convert_color(value) }
          else
            return { namespace: 'android', name: 'tint', value: convert_color(value) }
          end
        when 'font', 'fontFamily'
          return { namespace: 'android', name: 'fontFamily', value: value }
        when 'fontWeight'
          return map_font_weight(value)
        when 'fontStyle'
          return { namespace: 'android', name: 'textStyle', value: value }
        when 'textAlign', 'textAlignment'
          return { namespace: 'android', name: 'textAlignment', value: map_text_alignment(value) }
        when 'maxLines'
          return { namespace: 'android', name: 'maxLines', value: value.to_s }
        when 'ellipsize'
          return { namespace: 'android', name: 'ellipsize', value: value }
        end
        
        nil
      end
      
      private
      
      def process_text_value(value)
        # Handle data binding
        if value.is_a?(String) && value.start_with?('@{')
          value
        else
          value.to_s
        end
      end
      
      def convert_text_size(value)
        case value
        when Integer, Float
          "#{value}sp"
        when String
          if value.match?(/^\d+$/)
            "#{value}sp"
          else
            value
          end
        else
          "14sp"
        end
      end
      
      def convert_color(value)
        return nil if value.nil?
        
        # Handle color values
        if value.is_a?(String)
          if value.start_with?('#')
            value
          elsif value == 'clear' || value == 'transparent'
            '#00000000'
          else
            # Try to map common color names
            color_map = {
              'black' => '#000000',
              'white' => '#FFFFFF',
              'red' => '#FF0000',
              'green' => '#00FF00',
              'blue' => '#0000FF',
              'gray' => '#808080',
              'grey' => '#808080'
            }
            color_map[value.downcase] || value
          end
        else
          value.to_s
        end
      end
      
      def map_font_weight(value)
        case value
        when 'bold'
          { namespace: 'android', name: 'textStyle', value: 'bold' }
        when 'normal'
          { namespace: 'android', name: 'textStyle', value: 'normal' }
        else
          nil
        end
      end
      
      def map_text_alignment(value)
        case value
        when 'left', 'start'
          'textStart'
        when 'right', 'end'
          'textEnd'
        when 'center'
          'center'
        else
          value
        end
      end
    end
  end
end