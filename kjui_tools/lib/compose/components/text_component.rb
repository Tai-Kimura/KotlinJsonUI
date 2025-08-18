# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class TextComponent
        def self.generate(json_data, depth, required_imports = nil)
          text = process_data_binding(json_data['text'] || '')
          
          code = indent("Text(", depth)
          code += "\n" + indent("text = #{text},", depth + 1)
          
          # Font size
          if json_data['fontSize']
            code += "\n" + indent("fontSize = #{json_data['fontSize']}.sp,", depth + 1)
          end
          
          # Font color (official attribute)
          if json_data['fontColor']
            code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['fontColor']}\")),", depth + 1)
          end
          
          # Font weight - handle both 'font' and 'fontWeight' attributes
          if json_data['font'] == 'bold' || json_data['fontWeight'] == 'bold'
            code += "\n" + indent("fontWeight = FontWeight.Bold,", depth + 1)
          elsif json_data['fontWeight']
            code += "\n" + indent("fontWeight = FontWeight.#{json_data['fontWeight'].capitalize},", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # Format modifiers
          if modifiers.any?
            code += Helpers::ModifierBuilder.format(modifiers, depth)
          else
            code += "\n" + indent("modifier = Modifier", depth + 1)
          end
          
          # Text alignment
          if json_data['textAlign']
            required_imports&.add(:text_align)
            case json_data['textAlign'].downcase
            when 'center'
              code += ",\n" + indent("textAlign = TextAlign.Center", depth + 1)
            when 'right'
              code += ",\n" + indent("textAlign = TextAlign.End", depth + 1)
            when 'left'
              code += ",\n" + indent("textAlign = TextAlign.Start", depth + 1)
            end
          elsif json_data['centerHorizontal']
            required_imports&.add(:text_align)
            code += ",\n" + indent("textAlign = TextAlign.Center", depth + 1)
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