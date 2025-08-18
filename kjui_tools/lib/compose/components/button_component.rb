# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ButtonComponent
        def self.generate(json_data, depth, required_imports = nil)
          # Button uses 'text' attribute per SwiftJsonUI spec
          text = process_data_binding(json_data['text'] || 'Button')
          onclick = json_data['onclick']
          
          code = indent("Button(", depth)
          
          if onclick
            code += "\n" + indent("onClick = { viewModel.#{onclick}() },", depth + 1)
          else
            code += "\n" + indent("onClick = { },", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # Format modifiers
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Button colors including normal, disabled, and pressed states
          if json_data['background'] || json_data['disabledBackground'] || json_data['disabledFontColor'] || json_data['hilightColor']
            required_imports&.add(:button_colors)
            colors_code = "colors = ButtonDefaults.buttonColors("
            color_params = []
            
            if json_data['background']
              color_params << "containerColor = Color(android.graphics.Color.parseColor(\"#{json_data['background']}\"))"
            end
            
            if json_data['disabledBackground']
              color_params << "disabledContainerColor = Color(android.graphics.Color.parseColor(\"#{json_data['disabledBackground']}\"))"
            end
            
            if json_data['disabledFontColor']
              color_params << "disabledContentColor = Color(android.graphics.Color.parseColor(\"#{json_data['disabledFontColor']}\"))"
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
              text_code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['fontColor']}\")),", depth + 2)
            end
            
            text_code += "\n" + indent(")", depth + 1)
            code = code.sub(/Text\(#{Regexp.escape(text)}\)/, text_code.strip)
          end
          
          code += "\n" + indent("}", depth)
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