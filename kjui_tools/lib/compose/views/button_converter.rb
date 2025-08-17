# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ButtonConverter < BaseViewConverter
        def convert
          text = process_value(@component['title'] || @component['text'] || 'Button')
          onclick = @component['onclick']
          enabled = @component['enabled'] != false
          
          # Determine button type
          button_type = determine_button_type
          
          add_line "#{button_type}("
          indent do
            # onClick handler
            if onclick
              add_line "onClick = { viewModel.#{onclick}() },"
            else
              add_line "onClick = { },"
            end
            
            # Enabled state
            unless enabled
              add_line "enabled = false,"
            end
            
            # Shape
            if @component['cornerRadius']
              add_line "shape = RoundedCornerShape(#{@component['cornerRadius']}dp),"
            end
            
            # Colors
            if has_custom_colors?
              add_line "colors = ButtonDefaults.buttonColors("
              indent do
                if @component['backgroundColor']
                  color = map_color(@component['backgroundColor'])
                  add_line "containerColor = #{color},"
                end
                
                if @component['textColor']
                  color = map_color(@component['textColor'])
                  add_line "contentColor = #{color},"
                end
                
                if @component['disabledBackgroundColor']
                  color = map_color(@component['disabledBackgroundColor'])
                  add_line "disabledContainerColor = #{color},"
                end
                
                if @component['disabledTextColor']
                  color = map_color(@component['disabledTextColor'])
                  add_line "disabledContentColor = #{color},"
                end
              end
              add_line "),"
            end
            
            # Elevation
            if @component['elevation']
              add_line "elevation = ButtonDefaults.buttonElevation("
              indent do
                add_line "defaultElevation = #{@component['elevation']}dp,"
                add_line "pressedElevation = #{(@component['elevation'].to_i * 2)}dp"
              end
              add_line "),"
            end
            
            # Border for outlined button
            if button_type == 'OutlinedButton' && @component['borderColor']
              color = map_color(@component['borderColor'])
              width = @component['borderWidth'] || 1
              add_line "border = BorderStroke(#{width}dp, #{color}),"
            end
            
            # Content padding
            if @component['contentPadding']
              padding = @component['contentPadding']
              if padding.is_a?(Hash)
                horizontal = padding['horizontal'] || 16
                vertical = padding['vertical'] || 8
                add_line "contentPadding = PaddingValues(horizontal = #{horizontal}dp, vertical = #{vertical}dp),"
              else
                add_line "contentPadding = PaddingValues(#{padding}dp),"
              end
            end
            
            apply_modifiers
          end
          add_line ") {"
          
          # Button content
          indent do
            # Check for icon
            if @component['icon']
              generate_button_with_icon(text)
            else
              add_line "Text("
              indent do
                add_line "text = #{text},"
                
                if @component['fontSize']
                  add_line "fontSize = #{@component['fontSize']}sp,"
                end
                
                if @component['fontWeight']
                  weight = map_font_weight(@component['fontWeight'])
                  add_line "fontWeight = FontWeight.#{weight},"
                end
              end
              add_line ")"
            end
          end
          
          add_line "}"
          
          generated_code
        end
        
        private
        
        def determine_button_type
          style = @component['buttonStyle'] || @component['style']
          
          case style
          when 'outlined', 'outline'
            'OutlinedButton'
          when 'text', 'flat'
            'TextButton'
          when 'elevated', 'raised'
            'ElevatedButton'
          when 'filled_tonal', 'tonal'
            'FilledTonalButton'
          else
            # Default button type
            if @component['borderWidth'] || @component['borderColor']
              'OutlinedButton'
            elsif @component['elevation']
              'ElevatedButton'
            else
              'Button'
            end
          end
        end
        
        def has_custom_colors?
          @component['backgroundColor'] || 
          @component['textColor'] || 
          @component['disabledBackgroundColor'] || 
          @component['disabledTextColor']
        end
        
        def generate_button_with_icon(text)
          icon_position = @component['iconPosition'] || 'start'
          icon_name = @component['icon']
          
          add_line "Row("
          indent do
            add_line "verticalAlignment = Alignment.CenterVertically,"
            add_line "horizontalArrangement = Arrangement.Center"
          end
          add_line ") {"
          
          indent do
            if icon_position == 'start'
              generate_icon(icon_name)
              add_line "Spacer(modifier = Modifier.width(8dp))"
              generate_text(text)
            else
              generate_text(text)
              add_line "Spacer(modifier = Modifier.width(8dp))"
              generate_icon(icon_name)
            end
          end
          
          add_line "}"
        end
        
        def generate_icon(icon_name)
          add_line "Icon("
          indent do
            add_line "imageVector = Icons.Default.#{to_pascal_case(icon_name)},"
            add_line "contentDescription = null,"
            
            if @component['iconSize']
              add_line "modifier = Modifier.size(#{@component['iconSize']}dp)"
            end
          end
          add_line ")"
        end
        
        def generate_text(text)
          add_line "Text("
          indent do
            add_line "text = #{text},"
            
            if @component['fontSize']
              add_line "fontSize = #{@component['fontSize']}sp,"
            end
            
            if @component['fontWeight']
              weight = map_font_weight(@component['fontWeight'])
              add_line "fontWeight = FontWeight.#{weight},"
            end
          end
          add_line ")"
        end
        
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
      end
    end
  end
end