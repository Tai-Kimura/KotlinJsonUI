# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class TextViewComponent
        def self.generate(json_data, depth, required_imports = nil)
          # TextView is multi-line text input (like TextArea)
          # Uses 'text' for value and 'placeholder' for hint
          value = process_data_binding(json_data['text'] || '')
          placeholder = json_data['placeholder'] || json_data['hint'] || ''
          
          code = indent("OutlinedTextField(", depth)
          code += "\n" + indent("value = #{value},", depth + 1)
          
          # onValueChange handler
          if json_data['text'] && json_data['text'].match(/@\{([^}]+)\}/)
            variable = extract_variable_name(json_data['text'])
            code += "\n" + indent("onValueChange = { newValue -> currentData.value = currentData.value.copy(#{variable} = newValue) },", depth + 1)
          elsif json_data['onTextChange']
            code += "\n" + indent("onValueChange = { viewModel.#{json_data['onTextChange']}(it) },", depth + 1)
          else
            code += "\n" + indent("onValueChange = { },", depth + 1)
          end
          
          # Placeholder
          if placeholder && !placeholder.empty?
            code += "\n" + indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          
          # Size - default to fillMaxWidth for text areas
          if json_data['width'] == 'matchParent' || !json_data['width']
            modifiers << ".fillMaxWidth()"
          else
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          end
          
          # Height for multi-line
          if json_data['height']
            if json_data['height'] == 'matchParent'
              modifiers << ".fillMaxHeight()"
            elsif json_data['height'] == 'wrapContent'
              modifiers << ".wrapContentHeight()"
            else
              modifiers << ".height(#{json_data['height']}.dp)"
            end
          else
            # Default height for text area
            modifiers << ".height(120.dp)"
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Text styling
          if json_data['fontSize'] || json_data['fontColor']
            required_imports&.add(:text_style)
            style_parts = []
            style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
            style_parts << "color = Color(android.graphics.Color.parseColor(\"#{json_data['fontColor']}\"))" if json_data['fontColor']
            
            if style_parts.any?
              code += ",\n" + indent("textStyle = TextStyle(#{style_parts.join(', ')})", depth + 1)
            end
          end
          
          # Max lines for TextView
          if json_data['maxLines']
            code += ",\n" + indent("maxLines = #{json_data['maxLines']}", depth + 1)
          else
            # Default to multiple lines
            code += ",\n" + indent("maxLines = Int.MAX_VALUE", depth + 1)
          end
          
          # Single line false for multi-line
          code += ",\n" + indent("singleLine = false", depth + 1)
          
          # Keyboard options
          if json_data['returnKeyType']
            required_imports&.add(:ime_action)
            ime_action = case json_data['returnKeyType']
            when 'Done'
              'ImeAction.Done'
            when 'Next'
              'ImeAction.Next'
            when 'Default'
              'ImeAction.Default'
            else
              'ImeAction.Default'
            end
            code += ",\n" + indent("keyboardOptions = KeyboardOptions(imeAction = #{ime_action})", depth + 1)
          end
          
          # Enabled state
          if json_data.key?('enabled')
            if json_data['enabled'].is_a?(String) && json_data['enabled'].start_with?('@{')
              variable = json_data['enabled'].match(/@\{([^}]+)\}/)[1]
              code += ",\n" + indent("enabled = data.#{variable}", depth + 1)
            else
              code += ",\n" + indent("enabled = #{json_data['enabled']}", depth + 1)
            end
          end
          
          code += "\n" + indent(")", depth)
          code
        end
        
        private
        
        def self.process_data_binding(text)
          return quote(text) unless text.is_a?(String)
          
          if text.match(/@\{([^}]+)\}/)
            variable = $1
            if variable.include?(' ?? ')
              parts = variable.split(' ?? ')
              var_name = parts[0].strip
              "\"\\${data.#{var_name}}\""
            else
              "\"\\${data.#{variable}}\""
            end
          else
            quote(text)
          end
        end
        
        def self.extract_variable_name(text)
          if text && text.match(/@\{([^}]+)\}/)
            $1.split('.').last
          else
            'value'
          end
        end
        
        def self.quote(text)
          "\"#{text.gsub('"', '\\"')}\""
        end
        
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