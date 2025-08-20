# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class RadioComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Radio uses 'bind' for selected value
          selected = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            '""'
          end
          
          code = indent("Column(", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          code += "\n" + indent(") {", depth)
          
          # Radio options
          if json_data['options']
            if json_data['options'].is_a?(Array)
              json_data['options'].each do |option|
                option_value = option.is_a?(Hash) ? option['value'] : option
                option_label = option.is_a?(Hash) ? option['label'] : option
                
                code += "\n" + indent("Row(", depth + 1)
                code += "\n" + indent("verticalAlignment = Alignment.CenterVertically,", depth + 2)
                code += "\n" + indent("modifier = Modifier", depth + 2)
                code += "\n" + indent("    .fillMaxWidth()", depth + 2)
                code += "\n" + indent("    .clickable {", depth + 2)
                
                if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                  variable = $1
                  code += "\n" + indent("        viewModel.updateData(mapOf(\"#{variable}\" to \"#{option_value}\"))", depth + 2)
                elsif json_data['onValueChange']
                  code += "\n" + indent("        viewModel.#{json_data['onValueChange']}(\"#{option_value}\")", depth + 2)
                end
                
                code += "\n" + indent("    }", depth + 2)
                code += "\n" + indent(") {", depth + 1)
                
                # RadioButton
                code += "\n" + indent("RadioButton(", depth + 2)
                code += "\n" + indent("selected = (#{selected} == \"#{option_value}\"),", depth + 3)
                code += "\n" + indent("onClick = {", depth + 3)
                
                if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                  variable = $1
                  code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to \"#{option_value}\"))", depth + 4)
                elsif json_data['onValueChange']
                  code += "\n" + indent("viewModel.#{json_data['onValueChange']}(\"#{option_value}\")", depth + 4)
                end
                
                code += "\n" + indent("}", depth + 3)
                
                # RadioButton colors
                if json_data['selectedColor'] || json_data['unselectedColor']
                  required_imports&.add(:radio_colors)
                  colors_params = []
                  
                  if json_data['selectedColor']
                    colors_params << "selectedColor = Color(android.graphics.Color.parseColor(\"#{json_data['selectedColor']}\"))"
                  end
                  
                  if json_data['unselectedColor']
                    colors_params << "unselectedColor = Color(android.graphics.Color.parseColor(\"#{json_data['unselectedColor']}\"))"
                  end
                  
                  if colors_params.any?
                    code += ",\n" + indent("colors = RadioButtonDefaults.colors(", depth + 3)
                    code += "\n" + colors_params.map { |param| indent(param, depth + 4) }.join(",\n")
                    code += "\n" + indent(")", depth + 3)
                  end
                end
                
                code += "\n" + indent(")", depth + 2)
                
                # Label text
                code += "\n" + indent("Spacer(modifier = Modifier.width(8.dp))", depth + 2)
                code += "\n" + indent("Text(\"#{option_label}\")", depth + 2)
                
                code += "\n" + indent("}", depth + 1)
              end
            elsif json_data['options'].is_a?(String) && json_data['options'].match(/@\{([^}]+)\}/)
              # Dynamic options from data binding
              options_var = $1
              code += "\n" + indent("data.#{options_var}.forEach { option ->", depth + 1)
              code += "\n" + indent("Row(", depth + 2)
              code += "\n" + indent("verticalAlignment = Alignment.CenterVertically,", depth + 3)
              code += "\n" + indent("modifier = Modifier.fillMaxWidth().clickable {", depth + 3)
              
              if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                variable = $1
                code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to option))", depth + 4)
              end
              
              code += "\n" + indent("}", depth + 3)
              code += "\n" + indent(") {", depth + 2)
              code += "\n" + indent("RadioButton(", depth + 3)
              code += "\n" + indent("selected = (#{selected} == option),", depth + 4)
              code += "\n" + indent("onClick = {", depth + 4)
              
              if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                variable = $1
                code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to option))", depth + 5)
              end
              
              code += "\n" + indent("}", depth + 4)
              code += "\n" + indent(")", depth + 3)
              code += "\n" + indent("Spacer(modifier = Modifier.width(8.dp))", depth + 3)
              code += "\n" + indent("Text(option)", depth + 3)
              code += "\n" + indent("}", depth + 2)
              code += "\n" + indent("}", depth + 1)
            end
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