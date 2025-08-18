# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ContainerComponent
        def self.generate(json_data, depth, required_imports = nil)
          container_type = json_data['type'] || 'View'
          orientation = json_data['orientation']
          
          # Determine layout type
          layout = determine_layout(container_type, orientation)
          
          code = indent("#{layout}(", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          # Add gravity settings
          if json_data['gravity']
            code += add_gravity_settings(layout, json_data['gravity'], depth)
          end
          
          code += "\n" + indent(") {", depth)
          
          # Process children
          children = json_data['child'] || []
          children = [children] unless children.is_a?(Array)
          
          # Return structure for parent to process children
          { code: code, children: children, closing: "\n" + indent("}", depth) }
        end
        
        private
        
        def self.determine_layout(container_type, orientation)
          case container_type
          when 'VStack'
            'Column'
          when 'HStack'
            'Row'
          when 'ZStack'
            'Box'
          when 'View'
            if orientation == 'horizontal'
              'Row'
            elsif orientation == 'vertical'
              'Column'
            else
              'Box'
            end
          else
            'Box'
          end
        end
        
        def self.add_gravity_settings(layout, gravity, depth)
          code = ""
          
          if layout == 'Column'
            case gravity
            when 'top'
              code += ",\n" + indent("verticalArrangement = Arrangement.Top", depth + 1)
            when 'bottom'
              code += ",\n" + indent("verticalArrangement = Arrangement.Bottom", depth + 1)
            when 'centerVertical'
              code += ",\n" + indent("verticalArrangement = Arrangement.Center", depth + 1)
            when 'left'
              code += ",\n" + indent("horizontalAlignment = Alignment.Start", depth + 1)
            when 'right'
              code += ",\n" + indent("horizontalAlignment = Alignment.End", depth + 1)
            when 'centerHorizontal'
              code += ",\n" + indent("horizontalAlignment = Alignment.CenterHorizontally", depth + 1)
            end
          elsif layout == 'Row'
            case gravity
            when 'left'
              code += ",\n" + indent("horizontalArrangement = Arrangement.Start", depth + 1)
            when 'right'
              code += ",\n" + indent("horizontalArrangement = Arrangement.End", depth + 1)
            when 'centerHorizontal'
              code += ",\n" + indent("horizontalArrangement = Arrangement.Center", depth + 1)
            when 'top'
              code += ",\n" + indent("verticalAlignment = Alignment.Top", depth + 1)
            when 'bottom'
              code += ",\n" + indent("verticalAlignment = Alignment.Bottom", depth + 1)
            when 'centerVertical'
              code += ",\n" + indent("verticalAlignment = Alignment.CenterVertically", depth + 1)
            end
          end
          
          code
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