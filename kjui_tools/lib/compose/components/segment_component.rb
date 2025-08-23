# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class SegmentComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:segment)
          
          # Segment uses 'selectedIndex' or 'bind' for selected index
          # Track if the selected index is dynamic (from data binding) or static
          is_dynamic_index = false
          selected_index = if json_data['selectedIndex']
            if json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
              variable = $1
              is_dynamic_index = true
              "data.#{variable}"
            else
              # Direct integer value - keep as integer for proper comparison
              json_data['selectedIndex'].to_i
            end
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            is_dynamic_index = true
            "data.#{variable}"
          else
            0  # Default to 0 as integer
          end
          
          # Support both 'items' and 'segments' attribute names
          segments = json_data['items'] || json_data['segments'] || []
          
          code = indent("Segment(", depth)
          # For display in Segment parameter, always output as string
          selected_tab_param = is_dynamic_index ? selected_index : selected_index.to_s
          code += "\n" + indent("selectedTabIndex = #{selected_tab_param},", depth + 1)
          
          # Add enabled state if specified
          if json_data.key?('enabled')
            enabled_value = json_data['enabled']
            if enabled_value.is_a?(String) && enabled_value.match(/@\{([^}]+)\}/)
              code += "\n" + indent("enabled = data.#{$1},", depth + 1)
            else
              code += "\n" + indent("enabled = #{enabled_value},", depth + 1)
            end
          end
          
          # Tab colors - only add if specified, otherwise use defaults from Configuration
          colors_params = []
          
          # Background color (containerColor)
          if json_data['backgroundColor']
            colors_params << "containerColor = Color(android.graphics.Color.parseColor(\"#{json_data['backgroundColor']}\"))"
          end
          
          # Normal text color (contentColor) - for unselected tabs
          if json_data['normalColor']
            colors_params << "contentColor = Color(android.graphics.Color.parseColor(\"#{json_data['normalColor']}\"))"
          end
          
          # Selected text color (selectedContentColor) 
          if json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
            color = json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
            colors_params << "selectedContentColor = Color(android.graphics.Color.parseColor(\"#{color}\"))"
          end
          
          # Indicator color - only if specified
          if json_data['indicatorColor']
            colors_params << "indicatorColor = Color(android.graphics.Color.parseColor(\"#{json_data['indicatorColor']}\"))"
          end
          
          if colors_params.any?
            code += "\n" + indent(colors_params.join(",\n"), depth + 1) + ","
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          code += "\n" + indent(") {", depth)
          
          # Generate tabs
          if segments.is_a?(Array)
            segments.each_with_index do |segment, index|
              code += "\n" + indent("Tab(", depth + 1)
              # For selected comparison, handle both dynamic and static cases
              selected_comparison = is_dynamic_index ? "(#{selected_index} == #{index})" : (selected_index == index).to_s
              code += "\n" + indent("selected = #{selected_comparison},", depth + 2)
              
              # Add enabled state to Tab if segment is disabled
              if json_data.key?('enabled')
                enabled_value = json_data['enabled']
                if enabled_value.is_a?(String) && enabled_value.match(/@\{([^}]+)\}/)
                  code += "\n" + indent("enabled = data.#{$1},", depth + 2)
                else
                  code += "\n" + indent("enabled = #{enabled_value},", depth + 2)
                end
              end
              
              code += "\n" + indent("onClick = {", depth + 2)
              
              # Check if we have a binding variable
              has_binding = false
              binding_variable = nil
              
              if json_data['selectedIndex'] && json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
                has_binding = true
                binding_variable = $1
              elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                has_binding = true
                binding_variable = $1
              end
              
              # Generate onClick handler
              if json_data['onValueChange']
                # Use custom handler if specified
                code += "\n" + indent("viewModel.#{json_data['onValueChange']}(#{index})", depth + 3)
              elsif has_binding
                # Update the bound variable
                code += "\n" + indent("viewModel.updateData(mapOf(\"#{binding_variable}\" to #{index}))", depth + 3)
              else
                # No action if selectedIndex is a static value with no binding
                code += "\n" + indent("// Static selected index", depth + 3)
              end
              
              code += "\n" + indent("},", depth + 2)
              
              # Generate text with color based on selection
              # Store color info for later use
              normal_color = json_data['normalColor']
              selected_color = json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
              
              if normal_color || selected_color
                # Need to handle text color based on selection
                code += "\n" + indent("text = {", depth + 2)
                code += "\n" + indent("Text(", depth + 3)
                code += "\n" + indent("\"#{segment}\",", depth + 4)
                
                # Use conditional color based on selection
                if is_dynamic_index
                  if selected_color && normal_color
                    code += "\n" + indent("color = if (#{selected_index} == #{index}) Color(android.graphics.Color.parseColor(\"#{selected_color}\")) else Color(android.graphics.Color.parseColor(\"#{normal_color}\"))", depth + 4)
                  elsif selected_color
                    code += "\n" + indent("color = if (#{selected_index} == #{index}) Color(android.graphics.Color.parseColor(\"#{selected_color}\")) else Color.Unspecified", depth + 4)
                  elsif normal_color
                    code += "\n" + indent("color = if (#{selected_index} == #{index}) Color.Unspecified else Color(android.graphics.Color.parseColor(\"#{normal_color}\"))", depth + 4)
                  end
                else
                  # Static index
                  is_selected = (selected_index == index)
                  if is_selected && selected_color
                    code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{selected_color}\"))", depth + 4)
                  elsif !is_selected && normal_color
                    code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{normal_color}\"))", depth + 4)
                  end
                end
                
                code += "\n" + indent(")", depth + 3)
                code += "\n" + indent("}", depth + 2)
              else
                code += "\n" + indent("text = { Text(\"#{segment}\") }", depth + 2)
              end
              
              code += "\n" + indent(")", depth + 1)
            end
          elsif segments.is_a?(String) && segments.match(/@\{([^}]+)\}/)
            # Dynamic segments from data binding
            segments_var = $1
            code += "\n" + indent("data.#{segments_var}.forEachIndexed { index, segment ->", depth + 1)
            code += "\n" + indent("Tab(", depth + 2)
            # For dynamic segments, selected_index comparison depends on whether the index itself is dynamic
            selected_comparison = is_dynamic_index ? "(#{selected_index} == index)" : "(#{selected_index} == index)"
            code += "\n" + indent("selected = #{selected_comparison},", depth + 3)
            
            # Add enabled state to Tab if segment is disabled
            if json_data.key?('enabled')
              enabled_value = json_data['enabled']
              if enabled_value.is_a?(String) && enabled_value.match(/@\{([^}]+)\}/)
                code += "\n" + indent("enabled = data.#{$1},", depth + 3)
              else
                code += "\n" + indent("enabled = #{enabled_value},", depth + 3)
              end
            end
            
            code += "\n" + indent("onClick = {", depth + 3)
            
            # Check if we have a binding variable
            has_binding = false
            binding_variable = nil
            
            if json_data['selectedIndex'] && json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
              has_binding = true
              binding_variable = $1
            elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
              has_binding = true
              binding_variable = $1
            end
            
            # Generate onClick handler
            if json_data['onValueChange']
              # Use custom handler if specified
              code += "\n" + indent("viewModel.#{json_data['onValueChange']}(index)", depth + 4)
            elsif has_binding
              # Update the bound variable
              code += "\n" + indent("viewModel.updateData(mapOf(\"#{binding_variable}\" to index))", depth + 4)
            else
              # No action if selectedIndex is a static value with no binding
              code += "\n" + indent("// Static selected index", depth + 4)
            end
            
            code += "\n" + indent("},", depth + 3)
            
            # Generate text with color based on selection for dynamic segments
            normal_color = json_data['normalColor']
            selected_color = json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
            
            if normal_color || selected_color
              code += "\n" + indent("text = {", depth + 3)
              code += "\n" + indent("Text(", depth + 4)
              code += "\n" + indent("segment,", depth + 5)
              
              # Use conditional color based on selection
              if selected_color && normal_color
                code += "\n" + indent("color = if (#{selected_comparison}) Color(android.graphics.Color.parseColor(\"#{selected_color}\")) else Color(android.graphics.Color.parseColor(\"#{normal_color}\"))", depth + 5)
              elsif selected_color
                code += "\n" + indent("color = if (#{selected_comparison}) Color(android.graphics.Color.parseColor(\"#{selected_color}\")) else Color.Unspecified", depth + 5)
              elsif normal_color
                code += "\n" + indent("color = if (#{selected_comparison}) Color.Unspecified else Color(android.graphics.Color.parseColor(\"#{normal_color}\"))", depth + 5)
              end
              
              code += "\n" + indent(")", depth + 4)
              code += "\n" + indent("}", depth + 3)
            else
              code += "\n" + indent("text = { Text(segment) }", depth + 3)
            end
            
            code += "\n" + indent(")", depth + 2)
            code += "\n" + indent("}", depth + 1)
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