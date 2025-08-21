# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class SegmentComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:tab_row)
          
          # Segment uses 'selectedIndex' or 'bind' for selected index
          selected_index = if json_data['selectedIndex']
            if json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
              variable = $1
              "data.#{variable}"
            else
              # Direct integer value
              json_data['selectedIndex'].to_s
            end
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            '0'
          end
          
          # Support both 'items' and 'segments' attribute names
          segments = json_data['items'] || json_data['segments'] || []
          
          code = indent("TabRow(", depth)
          code += "\n" + indent("selectedTabIndex = #{selected_index},", depth + 1)
          
          # Tab colors
          if json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor'] || json_data['backgroundColor']
            colors_params = []
            
            # tintColor and selectedColor both map to contentColor
            if json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
              color = json_data['selectedColor'] || json_data['tintColor'] || json_data['selectedSegmentTintColor']
              colors_params << "contentColor = Color(android.graphics.Color.parseColor(\"#{color}\"))"
            end
            
            if json_data['backgroundColor']
              colors_params << "containerColor = Color(android.graphics.Color.parseColor(\"#{json_data['backgroundColor']}\"))"
            end
            
            if colors_params.any?
              code += "\n" + indent(colors_params.join(",\n"), depth + 1) + ","
            end
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
              code += "\n" + indent("selected = (#{selected_index} == #{index}),", depth + 2)
              code += "\n" + indent("onClick = {", depth + 2)
              
              if json_data['selectedIndex'] && json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
                variable = $1
                code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to #{index}))", depth + 3)
              elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
                variable = $1
                code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to #{index}))", depth + 3)
              elsif json_data['onValueChange']
                code += "\n" + indent("viewModel.#{json_data['onValueChange']}(#{index})", depth + 3)
              else
                # No action if selectedIndex is a static value
                code += "\n" + indent("// Static selected index", depth + 3)
              end
              
              code += "\n" + indent("},", depth + 2)
              code += "\n" + indent("text = { Text(\"#{segment}\") }", depth + 2)
              code += "\n" + indent(")", depth + 1)
            end
          elsif segments.is_a?(String) && segments.match(/@\{([^}]+)\}/)
            # Dynamic segments from data binding
            segments_var = $1
            code += "\n" + indent("data.#{segments_var}.forEachIndexed { index, segment ->", depth + 1)
            code += "\n" + indent("Tab(", depth + 2)
            code += "\n" + indent("selected = (#{selected_index} == index),", depth + 3)
            code += "\n" + indent("onClick = {", depth + 3)
            
            if json_data['selectedIndex'] && json_data['selectedIndex'].is_a?(String) && json_data['selectedIndex'].match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to index))", depth + 4)
            elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
              variable = $1
              code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to index))", depth + 4)
            else
              code += "\n" + indent("// Static selected index", depth + 4)
            end
            
            code += "\n" + indent("},", depth + 3)
            code += "\n" + indent("text = { Text(segment) }", depth + 3)
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