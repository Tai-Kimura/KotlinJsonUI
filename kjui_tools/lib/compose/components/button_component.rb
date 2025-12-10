# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class ButtonComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Button uses 'text' attribute per SwiftJsonUI spec
          text = Helpers::ResourceResolver.process_text(json_data['text'] || 'Button', required_imports)

          code = indent("Button(", depth)

          # Handle click events
          # onclick (lowercase) -> selector format (string only)
          # onClick (camelCase) -> binding format only (@{functionName})
          if json_data['onclick']
            handler_call = Helpers::ModifierBuilder.get_event_handler_call(json_data['onclick'], is_camel_case: false)
            code += "\n" + indent("onClick = { #{handler_call} }", depth + 1)
          elsif json_data['onClick']
            handler_call = Helpers::ModifierBuilder.get_event_handler_call(json_data['onClick'], is_camel_case: true)
            code += "\n" + indent("onClick = { #{handler_call} }", depth + 1)
          else
            code += "\n" + indent("onClick = { }", depth + 1)
          end
          
          # Build modifiers (only margins and size, not padding)
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # Format modifiers only if there are modifiers
          if modifiers.any?
            code += ","
            code += Helpers::ModifierBuilder.format(modifiers, depth)
          end
          
          # Add shape with cornerRadius if specified
          if json_data['cornerRadius']
            required_imports&.add(:shape)
            code += ",\n" + indent("shape = RoundedCornerShape(#{json_data['cornerRadius']}.dp)", depth + 1)
          end
          
          # Add contentPadding for internal padding
          # Support both 'padding' (number), 'paddings' (array), and individual padding attributes
          padding_data = json_data['paddings'] || json_data['padding']
          
          if padding_data || json_data['paddingTop'] || json_data['paddingBottom'] || 
             json_data['paddingLeft'] || json_data['paddingRight'] || json_data['paddingStart'] || 
             json_data['paddingEnd'] || json_data['paddingHorizontal'] || json_data['paddingVertical']
            required_imports&.add(:button_padding)
            
            padding_values = []
            
            if padding_data
              # Handle paddings array or padding number
              if padding_data.is_a?(Array)
                case padding_data.length
                when 1
                  # One value: all sides
                  padding_values << "#{padding_data[0]}.dp"
                when 2
                  # Two values: [vertical, horizontal]
                  padding_values << "vertical = #{padding_data[0]}.dp"
                  padding_values << "horizontal = #{padding_data[1]}.dp"
                when 3
                  # Three values: [top, horizontal, bottom]
                  padding_values << "top = #{padding_data[0]}.dp"
                  padding_values << "horizontal = #{padding_data[1]}.dp"
                  padding_values << "bottom = #{padding_data[2]}.dp"
                when 4
                  # Four values: [top, right, bottom, left]
                  padding_values << "top = #{padding_data[0]}.dp"
                  padding_values << "end = #{padding_data[1]}.dp"
                  padding_values << "bottom = #{padding_data[2]}.dp"
                  padding_values << "start = #{padding_data[3]}.dp"
                end
              else
                # Single number: all sides
                padding_values << "#{padding_data}.dp"
              end
            else
              # Handle individual padding attributes
              top_padding = json_data['paddingTop'] || json_data['paddingVertical'] || 0
              bottom_padding = json_data['paddingBottom'] || json_data['paddingVertical'] || 0
              start_padding = json_data['paddingStart'] || json_data['paddingLeft'] || json_data['paddingHorizontal'] || 0
              end_padding = json_data['paddingEnd'] || json_data['paddingRight'] || json_data['paddingHorizontal'] || 0
              
              if top_padding == bottom_padding && start_padding == end_padding && top_padding == start_padding
                # All same, use single value
                padding_values << "#{top_padding}.dp" if top_padding > 0
              elsif top_padding == bottom_padding && start_padding == end_padding
                # Different horizontal and vertical
                padding_values << "horizontal = #{start_padding}.dp" if start_padding > 0
                padding_values << "vertical = #{top_padding}.dp" if top_padding > 0
              else
                # All different, need to specify each
                padding_values << "start = #{start_padding}.dp" if start_padding > 0
                padding_values << "top = #{top_padding}.dp" if top_padding > 0
                padding_values << "end = #{end_padding}.dp" if end_padding > 0
                padding_values << "bottom = #{bottom_padding}.dp" if bottom_padding > 0
              end
            end
            
            if padding_values.any?
              code += ",\n" + indent("contentPadding = PaddingValues(#{padding_values.join(', ')})", depth + 1)
            end
          end
          
          # Button colors including normal, disabled, and pressed states
          if json_data['background'] || json_data['disabledBackground'] || json_data['disabledFontColor'] || json_data['hilightColor']
            required_imports&.add(:button_colors)
            colors_code = "colors = ButtonDefaults.buttonColors("
            color_params = []
            
            if json_data['background']
              background_color = Helpers::ResourceResolver.process_color(json_data['background'], required_imports)
              color_params << "containerColor = #{background_color}"
            end
            
            if json_data['disabledBackground']
              disabled_bg_color = Helpers::ResourceResolver.process_color(json_data['disabledBackground'], required_imports)
              color_params << "disabledContainerColor = #{disabled_bg_color}"
            end
            
            if json_data['disabledFontColor']
              disabled_font_color = Helpers::ResourceResolver.process_color(json_data['disabledFontColor'], required_imports)
              color_params << "disabledContentColor = #{disabled_font_color}"
            end
            
            # Note: hilightColor (pressed state) isn't directly supported in Material3 ButtonDefaults
            # We'd need a custom button implementation or InteractionSource for true pressed state
            if json_data['hilightColor']
              color_params << "// hilightColor: #{json_data['hilightColor']} - Use InteractionSource for pressed state"
            end
            
            if color_params.any?
              colors_code += "\n" + color_params.map { |param| indent(param, depth + 2) }.join(",\n")
              colors_code += "\n" + indent(")", depth + 1)
              code += ",\n" + indent(colors_code, depth + 1)
            end
          end
          
          # Handle enabled attribute
          if json_data.key?('enabled')
            if json_data['enabled'].is_a?(String) && json_data['enabled'].start_with?('@{')
              # Data binding for enabled
              variable = json_data['enabled'].match(/@\{([^}]+)\}/)[1]
              code += ",\n" + indent("enabled = data.#{variable}", depth + 1)
            else
              code += ",\n" + indent("enabled = #{json_data['enabled']}", depth + 1)
            end
          end
          
          code += "\n" + indent(") {", depth)
          code += "\n" + indent("Text(#{text})", depth + 1)
          
          # Apply text attributes if specified
          if json_data['fontSize'] || json_data['fontColor']
            text_code = "\n" + indent("Text(", depth + 1)
            text_code += "\n" + indent("text = #{text},", depth + 2)
            
            if json_data['fontSize']
              text_code += "\n" + indent("fontSize = #{json_data['fontSize']}.sp,", depth + 2)
            end
            
            if json_data['fontColor']
              color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
              text_code += "\n" + indent("color = #{color_value},", depth + 2) if color_value
            end
            
            text_code += "\n" + indent(")", depth + 1)
            code = code.sub(/Text\(#{Regexp.escape(text)}\)/, text_code.strip)
          end
          
          code += "\n" + indent("}", depth)
          code
        end
        
        private
        
        def self.indent(text, level)
          return text if level == 0
          spaces = '    ' * level
          text.split("\n").map { |line| 
            line.empty? ? line : spaces + line 
          }.join("\n")
        end
      end
    end
  end
end