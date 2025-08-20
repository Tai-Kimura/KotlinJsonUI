# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class TextFieldComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # TextField uses 'text' for value and 'hint' for placeholder per SwiftJsonUI spec
          value = process_data_binding(json_data['text'] || '')
          placeholder = json_data['hint'] || ''
          is_secure = json_data['secure'] == true
          
          # Check if we need to wrap in Box for margins
          has_margins = json_data['margins'] || json_data['topMargin'] || json_data['bottomMargin'] || 
                       json_data['leftMargin'] || json_data['rightMargin']
          
          # Always use CustomTextField
          required_imports&.add(:custom_textfield)
          required_imports&.add(:visual_transformation) if is_secure
          
          code = ""
          if has_margins
            required_imports&.add(:box)
            code = indent("CustomTextFieldWithMargins(", depth)
          else
            code = indent("CustomTextField(", depth)
          end
          
          code += "\n" + indent("value = #{value},", depth + 1)
          
          # onTextChange is the official attribute per wiki
          if json_data['text'] && json_data['text'].match(/@\{([^}]+)\}/)
            variable = extract_variable_name(json_data['text'])
            # Use a map update to notify the viewModel
            code += "\n" + indent("onValueChange = { newValue -> viewModel.updateData(mapOf(\"#{variable}\" to newValue)) },", depth + 1)
          else
            code += "\n" + indent("onValueChange = { },", depth + 1)
          end
          
          # For CustomTextFieldWithMargins, we need to specify modifiers differently
          if has_margins
            # Box modifier with margins
            box_modifiers = []
            box_modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
            if box_modifiers.any?
              code += "\n" + indent("boxModifier = Modifier", depth + 1)
              box_modifiers.each do |mod|
                code += "\n" + indent("    #{mod}", depth + 1)
              end
              code += ","
            end
            
            # TextField modifier
            textfield_modifiers = []
            textfield_modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
            textfield_modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
            if textfield_modifiers.any?
              code += "\n" + indent("textFieldModifier = Modifier", depth + 1)
              textfield_modifiers.each do |mod|
                code += "\n" + indent("    #{mod}", depth + 1)
              end
              code += ","
            end
          else
            # Regular modifiers for CustomTextField
            modifiers = []
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
            modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
            modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
            
            if modifiers.any?
              code += "\n" + indent("modifier = Modifier", depth + 1)
              modifiers.each do |mod|
                code += "\n" + indent("    #{mod}", depth + 1)
              end
              code += ","
            end
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
          
          # Add visual transformation for secure fields
          if is_secure
            code += "\n" + indent("visualTransformation = PasswordVisualTransformation(),", depth + 1)
          end
          
          # Add custom TextField parameters
          
          # Shape with corner radius
          if json_data['cornerRadius']
            required_imports&.add(:shape)
            code += "\n" + indent("shape = RoundedCornerShape(#{json_data['cornerRadius']}.dp),", depth + 1)
          end
          
          # Background colors
          if json_data['background']
            code += "\n" + indent("backgroundColor = Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")),", depth + 1)
          end
          
          if json_data['highlightBackground']
            code += "\n" + indent("highlightBackgroundColor = Color(android.graphics.Color.parseColor(\"#{json_data['highlightBackground']}\")),", depth + 1)
          end
          
          # Border color for outlined text fields
          if json_data['borderColor']
            code += "\n" + indent("borderColor = Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")),", depth + 1)
          end
          
          # Set isOutlined and isSecure flags
          if json_data['outlined'] == true
            code += "\n" + indent("isOutlined = true,", depth + 1)
          end
          
          if is_secure
            code += "\n" + indent("isSecure = true,", depth + 1)
          end
          
          # Text styling - always add this last before closing
          if json_data['fontSize'] || json_data['textAlign'] || json_data['fontColor']
            required_imports&.add(:text_style)
            style_parts = []
            style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
            
            if json_data['fontColor']
              color = json_data['fontColor']
              if color.start_with?('#')
                style_parts << "color = Color(android.graphics.Color.parseColor(\"#{color}\"))"
              else
                style_parts << "color = Color.#{color}"
              end
            end
            
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
              # Remove trailing comma before adding textStyle
              if code.end_with?(',')
                code = code[0..-2]
              end
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
          
          # Remove trailing comma and close
          if code.end_with?(',')
            code = code[0..-2]
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
              "\"\${data.#{var_name}}\""
            else
              "\"\${data.#{variable}}\""
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
          # Escape special characters properly
          escaped = text.gsub('\\', '\\\\\\\\')  # Escape backslashes first
                       .gsub('"', '\\"')           # Escape quotes
                       .gsub("\n", '\\n')           # Escape newlines
                       .gsub("\r", '\\r')           # Escape carriage returns
                       .gsub("\t", '\\t')           # Escape tabs
          "\"#{escaped}\""
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