# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class IndicatorComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Indicator can be circular or linear based on style
          style = json_data['style'] || 'medium'
          is_animating = json_data['animating']
          
          # Check if animating is controlled by data binding
          show_condition = if is_animating && is_animating.is_a?(String) && is_animating.match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          elsif is_animating == false
            'false'
          else
            'true'
          end
          
          # Wrap in if condition if controlled by animating attribute
          if is_animating != nil
            code = indent("if (#{show_condition}) {", depth)
            actual_depth = depth + 1
          else
            code = ""
            actual_depth = depth
          end
          
          # Determine indicator type based on style
          if style == 'linear'
            code += "\n" if is_animating != nil
            code += indent("LinearProgressIndicator(", actual_depth)
          else
            code += "\n" if is_animating != nil
            code += indent("CircularProgressIndicator(", actual_depth)
          end
          
          # Build modifiers
          modifiers = []
          
          # Size based on style
          if style == 'large'
            modifiers << ".size(48.dp)"
          elsif style == 'small'
            modifiers << ".size(16.dp)"
          elsif json_data['size']
            modifiers << ".size(#{json_data['size']}.dp)"
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          
          # Add weight modifier if in Row or Column
          if parent_type == 'Row' || parent_type == 'Column'
            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          end
          
          code += Helpers::ModifierBuilder.format(modifiers, actual_depth) if modifiers.any?
          
          # Color
          if json_data['color']
            code += ",\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['color']}\"))", actual_depth + 1)
          end
          
          # Track color for linear progress
          if style == 'linear' && json_data['trackColor']
            code += ",\n" + indent("trackColor = Color(android.graphics.Color.parseColor(\"#{json_data['trackColor']}\"))", actual_depth + 1)
          end
          
          # Stroke width for circular progress
          if style != 'linear' && json_data['strokeWidth']
            code += ",\n" + indent("strokeWidth = #{json_data['strokeWidth']}.dp", actual_depth + 1)
          end
          
          code += "\n" + indent(")", actual_depth)
          
          # Close if condition
          if is_animating != nil
            code += "\n" + indent("}", depth)
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