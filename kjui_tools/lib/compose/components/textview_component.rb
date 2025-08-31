# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class TextViewComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # TextView is multi-line text input (like TextArea)
          # Uses 'text' for value and 'placeholder' for hint
          value = process_data_binding(json_data['text'] || '')
          placeholder = json_data['placeholder'] || json_data['hint'] || ''
          
          # Check if we need to wrap in Box for margins
          has_margins = json_data['margins'] || json_data['topMargin'] || json_data['bottomMargin'] || 
                       json_data['leftMargin'] || json_data['rightMargin']
          
          # Always use CustomTextField
          required_imports&.add(:custom_textfield)
          
          code = ""
          if has_margins
            required_imports&.add(:box)
            code = indent("CustomTextFieldWithMargins(", depth)
          else
            code = indent("CustomTextField(", depth)
          end
          code += "\n" + indent("value = #{value},", depth + 1)
          
          # onValueChange handler
          if json_data['text'] && json_data['text'].match(/@\{([^}]+)\}/)
            variable = extract_variable_name(json_data['text'])
            code += "\n" + indent("onValueChange = { newValue -> viewModel.updateData(mapOf(\"#{variable}\" to newValue)) },", depth + 1)
          elsif json_data['onTextChange']
            code += "\n" + indent("onValueChange = { viewModel.#{json_data['onTextChange']}(it) },", depth + 1)
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
            # Size - default to fillMaxWidth for text areas
            if json_data['width'] == 'matchParent' || !json_data['width']
              textfield_modifiers << ".fillMaxWidth()"
            else
              textfield_modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
            end
            
            # Height for multi-line
            if json_data['height']
              if json_data['height'] == 'matchParent'
                textfield_modifiers << ".fillMaxHeight()"
              elsif json_data['height'] == 'wrapContent'
                textfield_modifiers << ".wrapContentHeight()"
              else
                textfield_modifiers << ".height(#{json_data['height']}.dp)"
              end
            else
              # Default height for text area
              textfield_modifiers << ".height(120.dp)"
            end
            
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
            
            if modifiers.any?
              code += "\n" + indent("modifier = Modifier", depth + 1)
              modifiers.each do |mod|
                code += "\n" + indent("    #{mod}", depth + 1)
              end
              code += ","
            end
          end
          
          # Placeholder
          if placeholder && !placeholder.empty?
            code += "\n" + indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1)
          end
          
          # Shape with corner radius
          if json_data['cornerRadius']
            required_imports&.add(:shape)
            code += "\n" + indent("shape = RoundedCornerShape(#{json_data['cornerRadius']}.dp),", depth + 1)
          end
          
          # Background colors
          if json_data['background']
            bg_color = Helpers::ResourceResolver.process_color(json_data['background'], required_imports)
            code += "\n" + indent("backgroundColor = #{bg_color},", depth + 1)
          end
          
          if json_data['highlightBackground']
            highlight_color = Helpers::ResourceResolver.process_color(json_data['highlightBackground'], required_imports)
            code += "\n" + indent("highlightBackgroundColor = #{highlight_color},", depth + 1)
          end
          
          # Border color for outlined text fields
          if json_data['borderColor']
            border_color = Helpers::ResourceResolver.process_color(json_data['borderColor'], required_imports)
            code += "\n" + indent("borderColor = #{border_color},", depth + 1)
          end
          
          # Set isOutlined flag (TextView usually wants outlined style)
          code += "\n" + indent("isOutlined = true,", depth + 1)
          
          # Max lines for TextView
          if json_data['maxLines']
            code += "\n" + indent("maxLines = #{json_data['maxLines']},", depth + 1)
          else
            # Default to multiple lines
            code += "\n" + indent("maxLines = Int.MAX_VALUE,", depth + 1)
          end
          
          # Single line false for multi-line
          code += "\n" + indent("singleLine = false,", depth + 1)
          
          # Text styling
          if json_data['fontSize'] || json_data['fontColor']
            required_imports&.add(:text_style)
            style_parts = []
            style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
            if json_data['fontColor']
              font_color = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
              style_parts << "color = #{font_color}"
            end
            
            if style_parts.any?
              code += "\n" + indent("textStyle = TextStyle(#{style_parts.join(', ')})", depth + 1)
            end
          end
          
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
              "data.#{var_name}"
            else
              "data.#{variable}"
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