# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class TextFieldComponent
        def self.generate(json_data, depth, required_imports = nil)
          # TextField uses 'text' for value and 'hint' for placeholder per SwiftJsonUI spec
          value = process_data_binding(json_data['text'] || '')
          placeholder = json_data['hint'] || ''
          is_secure = json_data['secure'] == true
          
          code = ""
          # Determine TextField variant based on borderStyle
          use_outlined = is_secure || json_data['borderStyle'] == 'RoundedRect' || json_data['borderStyle'] == 'Line'
          
          if use_outlined
            required_imports&.add(:visual_transformation) if is_secure
            code = indent("OutlinedTextField(", depth)
          else
            code = indent("TextField(", depth)
          end
          
          code += "\n" + indent("value = #{value},", depth + 1)
          
          # onTextChange is the official attribute per wiki
          if json_data['text'] && json_data['text'].match(/@\{([^}]+)\}/)
            variable = extract_variable_name(json_data['text'])
            code += "\n" + indent("onValueChange = { newValue -> currentData.value = currentData.value.copy(#{variable} = newValue) },", depth + 1)
          else
            code += "\n" + indent("onValueChange = { },", depth + 1)
          end
          
          # Add placeholder/hint with styling
          if placeholder && !placeholder.empty?
            if json_data['hintColor'] || json_data['hintFontSize'] || json_data['hintFont']
              # Complex placeholder with styling
              placeholder_code = "placeholder = { Text("
              placeholder_code += "\n" + indent("text = #{quote(placeholder)}", depth + 2)
              
              if json_data['hintColor']
                placeholder_code += ",\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['hintColor']}\"))", depth + 2)
              end
              
              if json_data['hintFontSize']
                placeholder_code += ",\n" + indent("fontSize = #{json_data['hintFontSize']}.sp", depth + 2)
              end
              
              if json_data['hintFont'] == 'bold'
                placeholder_code += ",\n" + indent("fontWeight = FontWeight.Bold", depth + 2)
              end
              
              placeholder_code += "\n" + indent(") }", depth + 1)
              code += "\n" + indent(placeholder_code, depth + 1) + ","
            else
              # Simple placeholder
              code += "\n" + indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1)
            end
          end
          
          # Add password visual transformation for secure fields
          if is_secure
            code += "\n" + indent("visualTransformation = PasswordVisualTransformation(),", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Text styling
          if json_data['fontSize'] || json_data['textAlign']
            required_imports&.add(:text_style)
            style_parts = []
            style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
            
            if json_data['textAlign']
              required_imports&.add(:text_align)
              case json_data['textAlign'].downcase
              when 'center'
                style_parts << "textAlign = TextAlign.Center"
              when 'right'
                style_parts << "textAlign = TextAlign.End"
              when 'left'
                style_parts << "textAlign = TextAlign.Start"
              end
            end
            
            if style_parts.any?
              code += ",\n" + indent("textStyle = TextStyle(#{style_parts.join(', ')})", depth + 1)
            end
          end
          
          # Keyboard options (input and returnKeyType attributes)
          keyboard_options = []
          
          if json_data['input']
            required_imports&.add(:keyboard_type)
            keyboard_type = case json_data['input']
            when 'email'
              'KeyboardType.Email'
            when 'password'
              'KeyboardType.Password'
            when 'number'
              'KeyboardType.Number'
            when 'decimal'
              'KeyboardType.Decimal'
            when 'phone'
              'KeyboardType.Phone'
            else
              'KeyboardType.Text'
            end
            keyboard_options << "keyboardType = #{keyboard_type}"
          end
          
          if json_data['returnKeyType']
            required_imports&.add(:ime_action)
            ime_action = case json_data['returnKeyType']
            when 'Done'
              'ImeAction.Done'
            when 'Next'
              'ImeAction.Next'
            when 'Search'
              'ImeAction.Search'
            when 'Send'
              'ImeAction.Send'
            when 'Go'
              'ImeAction.Go'
            else
              'ImeAction.Default'
            end
            keyboard_options << "imeAction = #{ime_action}"
          end
          
          if keyboard_options.any?
            code += ",\n" + indent("keyboardOptions = KeyboardOptions(#{keyboard_options.join(', ')})", depth + 1)
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