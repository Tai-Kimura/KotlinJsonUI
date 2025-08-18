# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class SwitchComponent
        def self.generate(json_data, depth, required_imports = nil)
          # Switch uses 'bind' for two-way binding
          checked = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            'false'
          end
          
          code = indent("Switch(", depth)
          code += "\n" + indent("checked = #{checked},", depth + 1)
          
          # onCheckedChange handler
          if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            code += "\n" + indent("onCheckedChange = { newValue -> currentData.value = currentData.value.copy(#{variable} = newValue) },", depth + 1)
          elsif json_data['onValueChange']
            code += "\n" + indent("onCheckedChange = { viewModel.#{json_data['onValueChange']}(it) },", depth + 1)
          else
            code += "\n" + indent("onCheckedChange = { },", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          # Switch colors
          if json_data['onTintColor'] || json_data['thumbTintColor']
            required_imports&.add(:switch_colors)
            colors_params = []
            
            if json_data['onTintColor']
              colors_params << "checkedTrackColor = Color(android.graphics.Color.parseColor(\"#{json_data['onTintColor']}\"))"
            end
            
            if json_data['thumbTintColor']
              colors_params << "checkedThumbColor = Color(android.graphics.Color.parseColor(\"#{json_data['thumbTintColor']}\"))"
            end
            
            if colors_params.any?
              code += ",\n" + indent("colors = SwitchDefaults.colors(", depth + 1)
              code += "\n" + colors_params.map { |param| indent(param, depth + 2) }.join(",\n")
              code += "\n" + indent(")", depth + 1)
            end
          end
          
          # Handle enabled attribute
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