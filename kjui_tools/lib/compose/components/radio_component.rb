# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class RadioComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Handle Radio group with items FIRST (higher priority)
          if json_data['items']
            return generate_radio_group_with_items(json_data, depth, required_imports, parent_type)
          end
          
          # Handle individual Radio item (not a group)
          if json_data['group'] || json_data['text']
            return generate_radio_item(json_data, depth, required_imports, parent_type)
          end
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
        
        def self.generate_radio_item(json_data, depth, required_imports, parent_type)
          group = json_data['group'] || 'default'
          id = json_data['id'] || "radio_#{rand(1000)}"
          text = json_data['text'] || ''
          
          # Get the selected state from binding
          selected_var = "selectedRadiogroup"  # Default variable name
          if group.downcase != 'default'
            # Use group name as part of the variable
            selected_var = "selected#{group.capitalize}"
          end
          
          code = indent("Row(", depth)
          code += "\n" + indent("    verticalAlignment = Alignment.CenterVertically,", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          if modifiers.any?
            code += "\n" + indent("    modifier = Modifier", depth)
            modifiers.each do |mod|
              code += "\n" + indent("        #{mod}", depth)
            end
          end
          
          code += "\n" + indent(") {", depth)
          
          # Handle custom icons or default RadioButton
          if json_data['icon'] || json_data['selectedIcon']
            # Use IconButton with custom icons
            required_imports&.add(:icon_button)
            required_imports&.add(:icons)
            
            icon = map_icon_name(json_data['icon'] || 'circle')
            selected_icon = map_icon_name(json_data['selectedIcon'] || 'checkmark.circle.fill')
            
            code += "\n" + indent("    val isSelected = data.#{selected_var} == \"#{id}\"", depth)
            code += "\n" + indent("    IconButton(", depth)
            code += "\n" + indent("        onClick = { viewModel.updateData(mapOf(\"#{selected_var}\" to \"#{id}\")) }", depth)
            code += "\n" + indent("    ) {", depth)
            code += "\n" + indent("        Icon(", depth)
            code += "\n" + indent("            imageVector = if (isSelected) #{selected_icon} else #{icon},", depth)
            code += "\n" + indent("            contentDescription = \"#{text}\",", depth)
            
            if json_data['selectedColor'] || json_data['tintColor']
              color = json_data['selectedColor'] || json_data['tintColor']
              code += "\n" + indent("            tint = if (isSelected) Color(android.graphics.Color.parseColor(\"#{color}\")) else Color.Gray", depth)
            else
              code += "\n" + indent("            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray", depth)
            end
            
            code += "\n" + indent("        )", depth)
            code += "\n" + indent("    }", depth)
          else
            # Default RadioButton
            code += "\n" + indent("    RadioButton(", depth)
            code += "\n" + indent("        selected = data.#{selected_var} == \"#{id}\",", depth)
            code += "\n" + indent("        onClick = { viewModel.updateData(mapOf(\"#{selected_var}\" to \"#{id}\")) }", depth)
            code += "\n" + indent("    )", depth)
          end
          
          # Add text label
          if text && !text.empty?
            code += "\n" + indent("    Spacer(modifier = Modifier.width(8.dp))", depth)
            code += "\n" + indent("    Text(\"#{text}\")", depth)
          end
          
          code += "\n" + indent("}", depth)
          code
        end
        
        def self.generate_radio_group_with_items(json_data, depth, required_imports, parent_type)
          items = json_data['items']
          selected_value = json_data['selectedValue']
          
          # Extract binding variable
          selected_var = if selected_value && selected_value.match(/@\{([^}]+)\}/)
            "data.#{$1}"
          else
            '""'
          end
          
          code = indent("Column(", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          if modifiers.any?
            code += "\n" + indent("    modifier = Modifier", depth)
            modifiers.each do |mod|
              code += "\n" + indent("        #{mod}", depth)
            end
          end
          
          code += "\n" + indent(") {", depth)
          
          # Add label if present
          if json_data['text']
            code += "\n" + indent("    Text(\"#{json_data['text']}\")", depth)
            code += "\n" + indent("    Spacer(modifier = Modifier.height(8.dp))", depth)
          end
          
          # Generate radio items
          items.each do |item|
            code += "\n" + indent("    Row(", depth)
            code += "\n" + indent("        verticalAlignment = Alignment.CenterVertically,", depth)
            code += "\n" + indent("        modifier = Modifier", depth)
            code += "\n" + indent("            .fillMaxWidth()", depth)
            code += "\n" + indent("            .clickable {", depth)
            
            if selected_value && selected_value.match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("                viewModel.updateData(mapOf(\"#{variable}\" to \"#{item}\"))", depth)
            end
            
            code += "\n" + indent("            }", depth)
            code += "\n" + indent("    ) {", depth)
            code += "\n" + indent("        RadioButton(", depth)
            code += "\n" + indent("            selected = #{selected_var} == \"#{item}\",", depth)
            code += "\n" + indent("            onClick = {", depth)
            
            if selected_value && selected_value.match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("                viewModel.updateData(mapOf(\"#{variable}\" to \"#{item}\"))", depth)
            end
            
            code += "\n" + indent("            }", depth)
            code += "\n" + indent("        )", depth)
            code += "\n" + indent("        Spacer(modifier = Modifier.width(8.dp))", depth)
            code += "\n" + indent("        Text(\"#{item}\")", depth)
            code += "\n" + indent("    }", depth)
          end
          
          code += "\n" + indent("}", depth)
          code
        end
        
        def self.map_icon_name(icon_name)
          # Map iOS SF Symbols to Material Icons
          icon_map = {
            'circle' => 'Icons.Outlined.RadioButtonUnchecked',
            'checkmark.circle.fill' => 'Icons.Filled.CheckCircle',
            'star' => 'Icons.Outlined.Star',
            'star.fill' => 'Icons.Filled.Star',
            'heart' => 'Icons.Outlined.FavoriteBorder',
            'heart.fill' => 'Icons.Filled.Favorite',
            'square' => 'Icons.Outlined.CheckBoxOutlineBlank',
            'checkmark.square.fill' => 'Icons.Filled.CheckBox'
          }
          
          icon_map[icon_name] || 'Icons.Default.RadioButtonUnchecked'
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