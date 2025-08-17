# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class TextConverter < BaseViewConverter
        def convert
          text = process_value(@component['text'] || @component['title'] || '')
          
          add_line "Text("
          indent do
            add_line "text = #{text},"
            
            # Font size
            if @component['fontSize']
              add_line "fontSize = #{@component['fontSize']}sp,"
            end
            
            # Font weight
            if @component['fontWeight']
              weight = map_font_weight(@component['fontWeight'])
              add_line "fontWeight = FontWeight.#{weight},"
            end
            
            # Text color
            if @component['textColor'] || @component['color']
              color = map_color(@component['textColor'] || @component['color'])
              add_line "color = #{color},"
            end
            
            # Text alignment
            if @component['textAlign']
              align = map_text_align(@component['textAlign'])
              add_line "textAlign = TextAlign.#{align},"
            end
            
            # Max lines
            if @component['maxLines']
              add_line "maxLines = #{@component['maxLines']},"
            end
            
            # Overflow
            if @component['overflow']
              overflow = map_text_overflow(@component['overflow'])
              add_line "overflow = TextOverflow.#{overflow},"
            end
            
            # Letter spacing
            if @component['letterSpacing']
              add_line "letterSpacing = #{@component['letterSpacing']}sp,"
            end
            
            # Line height
            if @component['lineHeight']
              add_line "lineHeight = #{@component['lineHeight']}sp,"
            end
            
            # Text decoration
            if @component['textDecoration']
              decoration = map_text_decoration(@component['textDecoration'])
              add_line "textDecoration = TextDecoration.#{decoration},"
            end
            
            # Font style (italic)
            if @component['fontStyle'] == 'italic'
              add_line "fontStyle = FontStyle.Italic,"
            end
            
            # Font family
            if @component['fontFamily']
              add_line "fontFamily = FontFamily.#{@component['fontFamily'].capitalize},"
            end
            
            apply_modifiers
          end
          add_line ")"
          
          generated_code
        end
        
        private
        
        def map_font_weight(weight)
          case weight.to_s.downcase
          when 'thin', '100'
            'Thin'
          when 'light', '300'
            'Light'
          when 'normal', 'regular', '400'
            'Normal'
          when 'medium', '500'
            'Medium'
          when 'semibold', '600'
            'SemiBold'
          when 'bold', '700'
            'Bold'
          when 'extrabold', '800'
            'ExtraBold'
          when 'black', '900'
            'Black'
          else
            'Normal'
          end
        end
        
        def map_text_align(align)
          case align.to_s.downcase
          when 'left', 'start'
            'Start'
          when 'center'
            'Center'
          when 'right', 'end'
            'End'
          when 'justify'
            'Justify'
          else
            'Start'
          end
        end
        
        def map_text_overflow(overflow)
          case overflow.to_s.downcase
          when 'clip'
            'Clip'
          when 'ellipsis'
            'Ellipsis'
          when 'visible'
            'Visible'
          else
            'Clip'
          end
        end
        
        def map_text_decoration(decoration)
          case decoration.to_s.downcase
          when 'underline'
            'Underline'
          when 'line-through', 'linethrough'
            'LineThrough'
          when 'none'
            'None'
          else
            'None'
          end
        end
      end
      
      # Alias for Label
      class LabelConverter < TextConverter; end
    end
  end
end