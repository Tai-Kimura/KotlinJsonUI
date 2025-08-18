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
          if is_secure
            # For secure text fields, use OutlinedTextField with password visual transformation
            required_imports&.add(:visual_transformation)
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
          
          # Add placeholder/hint
          if placeholder && !placeholder.empty?
            code += "\n" + indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1)
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
          if json_data['fontSize']
            code += ",\n" + indent("textStyle = TextStyle(fontSize = #{json_data['fontSize']}.sp)", depth + 1)
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