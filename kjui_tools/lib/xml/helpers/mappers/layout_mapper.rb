#!/usr/bin/env ruby

module XmlGenerator
  module Mappers
    class LayoutMapper
      def initialize(dimension_mapper)
        @dimension_mapper = dimension_mapper
      end
      
      def map_layout_attributes(key, value, component_type, parent_type)
        case key
        # Dimension attributes
        when 'width'
          return { namespace: 'android', name: 'layout_width', value: @dimension_mapper.map_dimension(value) }
        when 'height'
          return { namespace: 'android', name: 'layout_height', value: @dimension_mapper.map_dimension(value) }
          
        # Padding attributes
        when 'padding', 'paddings'
          if value.is_a?(Array)
            return { namespace: 'android', name: 'padding', value: @dimension_mapper.convert_dimension(value.first || 0) }
          else
            return { namespace: 'android', name: 'padding', value: @dimension_mapper.convert_dimension(value) }
          end
        when 'topPadding', 'paddingTop'
          return { namespace: 'android', name: 'paddingTop', value: @dimension_mapper.convert_dimension(value) }
        when 'bottomPadding', 'paddingBottom'
          return { namespace: 'android', name: 'paddingBottom', value: @dimension_mapper.convert_dimension(value) }
        when 'leftPadding', 'paddingLeft', 'startPadding', 'paddingStart'
          return { namespace: 'android', name: 'paddingStart', value: @dimension_mapper.convert_dimension(value) }
        when 'rightPadding', 'paddingRight', 'endPadding', 'paddingEnd'
          return { namespace: 'android', name: 'paddingEnd', value: @dimension_mapper.convert_dimension(value) }
          
        # Margin attributes
        when 'margin'
          if value.is_a?(Array)
            return { namespace: 'android', name: 'layout_margin', value: @dimension_mapper.convert_dimension(value.first || 0) }
          else
            return { namespace: 'android', name: 'layout_margin', value: @dimension_mapper.convert_dimension(value) }
          end
        when 'topMargin', 'marginTop'
          return { namespace: 'android', name: 'layout_marginTop', value: @dimension_mapper.convert_dimension(value) }
        when 'bottomMargin', 'marginBottom'
          return { namespace: 'android', name: 'layout_marginBottom', value: @dimension_mapper.convert_dimension(value) }
        when 'leftMargin', 'marginLeft', 'startMargin', 'marginStart'
          return { namespace: 'android', name: 'layout_marginStart', value: @dimension_mapper.convert_dimension(value) }
        when 'rightMargin', 'marginRight', 'endMargin', 'marginEnd'
          return { namespace: 'android', name: 'layout_marginEnd', value: @dimension_mapper.convert_dimension(value) }
          
        # Layout specific
        when 'orientation'
          return { namespace: 'android', name: 'orientation', value: value }
        when 'weight'
          return { namespace: 'android', name: 'layout_weight', value: value.to_s }
        when 'gravity'
          return { namespace: 'android', name: 'gravity', value: map_gravity(value) }
        when 'layout_gravity'
          return { namespace: 'android', name: 'layout_gravity', value: map_gravity(value) }
        end
        
        nil
      end
      
      def map_alignment_attributes(key, value, parent_type)
        case key
        when 'alignTop'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'top' } if value
          else
            return { namespace: 'android', name: 'layout_alignParentTop', value: value.to_s }
          end
        when 'alignBottom'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'bottom' } if value
          else
            return { namespace: 'android', name: 'layout_alignParentBottom', value: value.to_s }
          end
        when 'alignLeft', 'alignStart'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'start' } if value
          else
            return { namespace: 'android', name: 'layout_alignParentStart', value: value.to_s }
          end
        when 'alignRight', 'alignEnd'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'end' } if value
          else
            return { namespace: 'android', name: 'layout_alignParentEnd', value: value.to_s }
          end
        when 'centerHorizontal'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'center_horizontal' } if value
          else
            return { namespace: 'android', name: 'layout_centerHorizontal', value: value.to_s }
          end
        when 'centerVertical'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'center_vertical' } if value
          else
            return { namespace: 'android', name: 'layout_centerVertical', value: value.to_s }
          end
        when 'centerInParent'
          if parent_type == 'LinearLayout'
            return { namespace: 'android', name: 'layout_gravity', value: 'center' } if value
          else
            return { namespace: 'android', name: 'layout_centerInParent', value: value.to_s }
          end
          
        # RelativeLayout specific
        when 'alignTopOfView'
          return { namespace: 'android', name: 'layout_alignTop', value: "@id/#{value}" }
        when 'alignBottomOfView'
          return { namespace: 'android', name: 'layout_alignBottom', value: "@id/#{value}" }
        when 'alignLeftOfView', 'toLeftOf'
          return { namespace: 'android', name: 'layout_toStartOf', value: "@id/#{value}" }
        when 'alignRightOfView', 'toRightOf'
          return { namespace: 'android', name: 'layout_toEndOf', value: "@id/#{value}" }
        when 'above'
          return { namespace: 'android', name: 'layout_above', value: "@id/#{value}" }
        when 'below'
          return { namespace: 'android', name: 'layout_below', value: "@id/#{value}" }
        end
        
        nil
      end
      
      private
      
      def map_gravity(value)
        if value.is_a?(Array)
          value.join('|')
        else
          case value
          when 'center'
            'center'
          when 'left', 'start'
            'start'
          when 'right', 'end'
            'end'
          when 'top'
            'top'
          when 'bottom'
            'bottom'
          else
            value
          end
        end
      end
    end
  end
end