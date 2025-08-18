# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class SelectBoxComponent
        def self.generate(json_data, depth, required_imports = nil)
          required_imports&.add(:dropdown_menu)
          
          # SelectBox uses 'bind' for selected value
          selected = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            '""'
          end
          
          # Generate unique state variable for dropdown expansion
          dropdown_id = "dropdown_#{Time.now.to_i}_#{rand(1000)}"
          
          code = indent("// Dropdown menu state", depth)
          code += "\n" + indent("var #{dropdown_id}Expanded by remember { mutableStateOf(false) }", depth)
          code += "\n\n" + indent("Box(", depth)
          
          # Build modifiers
          modifiers = ["Modifier"]
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          code += "\n" + indent(") {", depth)
          
          # TextField showing selected value
          code += "\n" + indent("OutlinedTextField(", depth + 1)
          code += "\n" + indent("value = #{selected},", depth + 2)
          code += "\n" + indent("onValueChange = { },", depth + 2)
          code += "\n" + indent("readOnly = true,", depth + 2)
          
          if json_data['placeholder']
            code += "\n" + indent("placeholder = { Text(\"#{json_data['placeholder']}\") },", depth + 2)
          end
          
          code += "\n" + indent("trailingIcon = {", depth + 2)
          code += "\n" + indent("Icon(", depth + 3)
          code += "\n" + indent("imageVector = Icons.Default.ArrowDropDown,", depth + 4)
          code += "\n" + indent("contentDescription = \"Dropdown\"", depth + 4)
          code += "\n" + indent(")", depth + 3)
          code += "\n" + indent("},", depth + 2)
          
          code += "\n" + indent("modifier = Modifier", depth + 2)
          code += "\n" + indent("    .fillMaxWidth()", depth + 2)
          code += "\n" + indent("    .clickable { #{dropdown_id}Expanded = true }", depth + 2)
          code += "\n" + indent(")", depth + 1)
          
          # Dropdown menu
          code += "\n\n" + indent("DropdownMenu(", depth + 1)
          code += "\n" + indent("expanded = #{dropdown_id}Expanded,", depth + 2)
          code += "\n" + indent("onDismissRequest = { #{dropdown_id}Expanded = false }", depth + 2)
          code += "\n" + indent(") {", depth + 1)
          
          # Options
          if json_data['options']
            if json_data['options'].is_a?(String) && json_data['options'].match(/@\{([^}]+)\}/)
              # Dynamic options from data binding
              options_var = $1
              code += "\n" + indent("data.#{options_var}.forEach { option ->", depth + 2)
              code += "\n" + indent("DropdownMenuItem(", depth + 3)
              code += "\n" + indent("text = { Text(option) },", depth + 4)
              code += "\n" + indent("onClick = {", depth + 4)
              
              if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                variable = $1
                code += "\n" + indent("currentData.value = currentData.value.copy(#{variable} = option)", depth + 5)
              end
              
              code += "\n" + indent("#{dropdown_id}Expanded = false", depth + 5)
              code += "\n" + indent("}", depth + 4)
              code += "\n" + indent(")", depth + 3)
              code += "\n" + indent("}", depth + 2)
            elsif json_data['options'].is_a?(Array)
              # Static options array
              json_data['options'].each do |option|
                option_value = option.is_a?(Hash) ? option['value'] : option
                option_label = option.is_a?(Hash) ? option['label'] : option
                
                code += "\n" + indent("DropdownMenuItem(", depth + 2)
                code += "\n" + indent("text = { Text(\"#{option_label}\") },", depth + 3)
                code += "\n" + indent("onClick = {", depth + 3)
                
                if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                  variable = $1
                  code += "\n" + indent("currentData.value = currentData.value.copy(#{variable} = \"#{option_value}\")", depth + 4)
                end
                
                code += "\n" + indent("#{dropdown_id}Expanded = false", depth + 4)
                code += "\n" + indent("}", depth + 3)
                code += "\n" + indent(")", depth + 2)
              end
            end
          end
          
          code += "\n" + indent("}", depth + 1)
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