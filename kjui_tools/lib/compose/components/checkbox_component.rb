# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class CheckboxComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Check uses 'bind' for two-way binding
          checked = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            'false'
          end
          
          has_label = json_data['label'] || json_data['text']
          
          if has_label
            # Checkbox with label
            code = indent("Row(", depth)
            code += "\n" + indent("verticalAlignment = Alignment.CenterVertically,", depth + 1)
            
            # Build modifiers for Row
            modifiers = []
            modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
            modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
            
            code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
            code += "\n" + indent(") {", depth)
            
            # Checkbox
            code += "\n" + indent("Checkbox(", depth + 1)
            code += "\n" + indent("checked = #{checked},", depth + 2)
            
            # onCheckedChange handler
            if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("onCheckedChange = { newValue -> viewModel.updateData(mapOf(\"#{variable}\" to newValue)) }", depth + 2)
            elsif json_data['onValueChange']
              code += "\n" + indent("onCheckedChange = { viewModel.#{json_data['onValueChange']}(it) }", depth + 2)
            else
              code += "\n" + indent("onCheckedChange = { }", depth + 2)
            end
            
            code += "\n" + indent(")", depth + 1)
            
            # Label text
            label_text = json_data['label'] || json_data['text']
            code += "\n" + indent("Spacer(modifier = Modifier.width(8.dp))", depth + 1)
            code += "\n" + indent("Text(\"#{label_text}\")", depth + 1)
            
            code += "\n" + indent("}", depth)
          else
            # Checkbox without label
            code = indent("Checkbox(", depth)
            code += "\n" + indent("checked = #{checked},", depth + 1)
            
            # onCheckedChange handler
            if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("onCheckedChange = { newValue -> viewModel.updateData(mapOf(\"#{variable}\" to newValue)) },", depth + 1)
            elsif json_data['onValueChange']
              code += "\n" + indent("onCheckedChange = { viewModel.#{json_data['onValueChange']}(it) },", depth + 1)
            else
              code += "\n" + indent("onCheckedChange = { },", depth + 1)
            end
            
            # Build modifiers
            modifiers = []
            modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
            modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
            modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          
          # Add weight modifier if in Row or Column
          if parent_type == 'Row' || parent_type == 'Column'
            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          end
            
            code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
            
            # Checkbox colors
            if json_data['checkColor'] || json_data['uncheckedColor']
              required_imports&.add(:checkbox_colors)
              colors_params = []
              
              if json_data['checkColor']
                checked_color = Helpers::ResourceResolver.process_color(json_data['checkColor'], required_imports)
                colors_params << "checkedColor = #{checked_color}"
              end
              
              if json_data['uncheckedColor']
                unchecked_color = Helpers::ResourceResolver.process_color(json_data['uncheckedColor'], required_imports)
                colors_params << "uncheckedColor = #{unchecked_color}"
              end
              
              if colors_params.any?
                code += ",\n" + indent("colors = CheckboxDefaults.colors(", depth + 1)
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
          end
          
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