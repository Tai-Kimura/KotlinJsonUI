# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ProgressComponent
        def self.generate(json_data, depth, required_imports = nil)
          # Progress can have a value (determinate) or be indeterminate
          has_value = json_data['value'] || json_data['bind']
          
          if has_value
            # Determinate progress (LinearProgressIndicator)
            value = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
              variable = $1
              "data.#{variable}.toFloat()"
            elsif json_data['value'] && json_data['value'].match(/@\{([^}]+)\}/)
              variable = $1
              "data.#{variable}.toFloat()"
            elsif json_data['value']
              "#{json_data['value']}f"
            else
              '0f'
            end
            
            code = indent("LinearProgressIndicator(", depth)
            code += "\n" + indent("progress = { #{value} },", depth + 1)
          else
            # Indeterminate progress
            style = json_data['style'] || 'linear'
            
            if style == 'circular' || style == 'large'
              code = indent("CircularProgressIndicator(", depth)
            else
              code = indent("LinearProgressIndicator(", depth)
            end
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          # Progress colors
          if json_data['progressTintColor'] || json_data['trackTintColor']
            colors_params = []
            
            if json_data['progressTintColor']
              colors_params << "color = Color(android.graphics.Color.parseColor(\"#{json_data['progressTintColor']}\"))"
            end
            
            if json_data['trackTintColor']
              colors_params << "trackColor = Color(android.graphics.Color.parseColor(\"#{json_data['trackTintColor']}\"))"
            end
            
            if colors_params.any?
              code += ",\n" + colors_params.map { |param| indent(param, depth + 1) }.join(",\n")
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