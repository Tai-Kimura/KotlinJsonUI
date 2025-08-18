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
          
          # Add direction settings (reverseLayout)
          if json_data['direction'] && (layout == 'Column' || layout == 'Row')
            case json_data['direction']
            when 'bottomToTop'
              code += ",\n" + indent("reverseLayout = true", depth + 1) if layout == 'Column'
            when 'rightToLeft'
              code += ",\n" + indent("reverseLayout = true", depth + 1) if layout == 'Row'
            # topToBottom and leftToRight are defaults (reverseLayout = false)
            end
          end
          
          # Add spacing for Column/Row
          if json_data['spacing'] && (layout == 'Column' || layout == 'Row')
            required_imports&.add(:arrangement)
            code += ",\n" + indent("verticalArrangement = Arrangement.spacedBy(#{json_data['spacing']}.dp)", depth + 1) if layout == 'Column'
            code += ",\n" + indent("horizontalArrangement = Arrangement.spacedBy(#{json_data['spacing']}.dp)", depth + 1) if layout == 'Row'
          end
          
          # Add distribution for Column/Row
          if json_data['distribution'] && (layout == 'Column' || layout == 'Row')
            required_imports&.add(:arrangement)
            
            arrangement = case json_data['distribution']
            when 'fillEqually'
              'Arrangement.SpaceEvenly'
            when 'fill'
              'Arrangement.SpaceBetween'
            when 'equalSpacing'
              'Arrangement.SpaceAround'
            when 'equalCentering'
              'Arrangement.SpaceEvenly'
            else
              nil
            end
            
            if arrangement
              code += ",\n" + indent("verticalArrangement = #{arrangement}", depth + 1) if layout == 'Column'
              code += ",\n" + indent("horizontalArrangement = #{arrangement}", depth + 1) if layout == 'Row'
            end
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
          # SwiftJsonUI only has 'View' type, not VStack/HStack/ZStack
          # Layout is determined by orientation attribute:
          # - orientation: "vertical" → Column (VStack)
          # - orientation: "horizontal" → Row (HStack)
          # - no orientation → Box (ZStack)
          
          if container_type == 'View'
            if orientation == 'vertical'
              'Column'
            elsif orientation == 'horizontal'
              'Row'
            else
              'Box'
            end
          else
            # For other types (shouldn't happen with proper View type)
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