# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class TextFieldComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # TextField uses 'text' for value and supports both 'hint' and 'placeholder'
          # For TextField value, we need direct data binding (not string interpolation)
          raw_text = json_data['text'] || ''
          value = if raw_text.match(/@\{([^}]+)\}/)
            variable = $1
            var_name = variable.include?(' ?? ') ? variable.split(' ?? ')[0].strip : variable
            "data.#{var_name}"
          else
            Helpers::ResourceResolver.process_text(raw_text, required_imports)
          end
          placeholder_text = json_data['hint'] || json_data['placeholder'] || ''
          placeholder = placeholder_text.empty? ? '""' : Helpers::ResourceResolver.process_text(placeholder_text, required_imports)
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
          
          # Handle onValueChange/onTextChange
          # Priority: onTextChange (explicit handler) > data binding > empty
          if json_data['onTextChange']
            # Explicit event handler
            code += "\n" + indent("onValueChange = { newValue -> viewModel.#{json_data['onTextChange']}(newValue) },", depth + 1)
          elsif json_data['text'] && json_data['text'].match(/@\{([^}]+)\}/)
            # Data binding update
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
          if placeholder && placeholder != '""'
            if json_data['hintColor'] || json_data['hintFontSize'] || json_data['hintFont']
              # Complex placeholder with styling
              placeholder_code = "placeholder = { Text("
              placeholder_code += "\n" + indent("text = #{placeholder}", depth + 2)
              
              if json_data['hintColor']
                hint_color = Helpers::ResourceResolver.process_color(json_data['hintColor'], required_imports)
                placeholder_code += ",\n" + indent("color = #{hint_color}", depth + 2)
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
              code += "\n" + indent("placeholder = { Text(#{placeholder}) },", depth + 1)
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

          # Field padding - internal padding within the text field
          if json_data['fieldPadding']
            code += "\n" + indent("contentPadding = PaddingValues(#{json_data['fieldPadding']}.dp),", depth + 1)
          end

          # Text padding left - start padding for text content
          if json_data['textPaddingLeft']
            code += "\n" + indent("textPaddingStart = #{json_data['textPaddingLeft']}.dp,", depth + 1)
          end
          
          # Background colors
          if json_data['background']
            bg_color = Helpers::ResourceResolver.process_color(json_data['background'], required_imports)
            code += "\n" + indent("backgroundColor = #{bg_color},", depth + 1)
          end
          
          if json_data['highlightBackground']
            highlight_bg_color = Helpers::ResourceResolver.process_color(json_data['highlightBackground'], required_imports)
            code += "\n" + indent("highlightBackgroundColor = #{highlight_bg_color},", depth + 1)
          end
          
          # Border color for outlined text fields
          if json_data['borderColor']
            border_color = Helpers::ResourceResolver.process_color(json_data['borderColor'], required_imports)
            code += "\n" + indent("borderColor = #{border_color},", depth + 1)
          end
          
          # Border style handling
          # borderStyle: none, line, bezel, roundedRect
          if json_data['borderStyle']
            case json_data['borderStyle'].downcase
            when 'none'
              code += "\n" + indent("isOutlined = false,", depth + 1)
            when 'line', 'bezel', 'roundedrect'
              code += "\n" + indent("isOutlined = true,", depth + 1)
            end
          # Set isOutlined and isSecure flags
          # Automatically use outlined style if borderColor or borderWidth is specified
          elsif json_data['outlined'] == true || json_data['borderColor'] || json_data['borderWidth']
            code += "\n" + indent("isOutlined = true,", depth + 1)
          end
          
          if is_secure
            code += "\n" + indent("isSecure = true,", depth + 1)
          end
          
          
          # Text styling - always add this last before closing
          # Always include textStyle with at least a default color
          required_imports&.add(:text_style)
          style_parts = []
          style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
          
          # Use fontColor if specified, otherwise default to black
          if json_data['fontColor']
            color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
            style_parts << "color = #{color_value}" if color_value
          else
            # Default to black text
            default_color = Helpers::ResourceResolver.process_color('#000000', required_imports)
            style_parts << "color = #{default_color}"
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
          
          # Add focus/blur event handlers
          if json_data['onFocus']
            code += ",\n" + indent("onFocus = { viewModel.#{json_data['onFocus']}() }", depth + 1)
          end
          
          if json_data['onBlur']
            code += ",\n" + indent("onBlur = { viewModel.#{json_data['onBlur']}() }", depth + 1)
          end
          
          if json_data['onBeginEditing']
            code += ",\n" + indent("onBeginEditing = { viewModel.#{json_data['onBeginEditing']}() }", depth + 1)
          end
          
          if json_data['onEndEditing']
            code += ",\n" + indent("onEndEditing = { viewModel.#{json_data['onEndEditing']}() }", depth + 1)
          end
          
          # Keyboard options (input, returnKeyType, contentType, autocapitalizationType, autocorrectionType)
          keyboard_options = []

          # Input type / contentType - contentType takes priority
          if json_data['contentType']
            required_imports&.add(:keyboard_type)
            keyboard_type = case json_data['contentType'].downcase
            when 'emailaddress', 'email'
              'KeyboardType.Email'
            when 'password', 'newpassword'
              'KeyboardType.Password'
            when 'telephonenumber', 'phone'
              'KeyboardType.Phone'
            when 'url'
              'KeyboardType.Uri'
            when 'creditcardnumber'
              'KeyboardType.Number'
            else
              'KeyboardType.Text'
            end
            keyboard_options << "keyboardType = #{keyboard_type}"
          elsif json_data['input']
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

          # Auto-capitalization type
          if json_data['autocapitalizationType']
            required_imports&.add(:keyboard_capitalization)
            capitalization = case json_data['autocapitalizationType'].downcase
            when 'none'
              'KeyboardCapitalization.None'
            when 'words'
              'KeyboardCapitalization.Words'
            when 'sentences'
              'KeyboardCapitalization.Sentences'
            when 'allcharacters', 'characters'
              'KeyboardCapitalization.Characters'
            else
              'KeyboardCapitalization.None'
            end
            keyboard_options << "capitalization = #{capitalization}"
          end

          # Auto-correction type
          if json_data['autocorrectionType']
            auto_correct = case json_data['autocorrectionType'].downcase
            when 'no', 'false', 'off'
              'false'
            when 'yes', 'true', 'on', 'default'
              'true'
            else
              'true'
            end
            keyboard_options << "autoCorrect = #{auto_correct}"
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
        
        def self.extract_variable_name(text)
          if text && text.match(/@\{([^}]+)\}/)
            $1.split('.').last
          else
            'value'
          end
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