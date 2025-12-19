# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative 'constraintlayout_component'

module KjuiTools
  module Compose
    module Components
      class ContainerComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          container_type = json_data['type'] || 'View'
          orientation = json_data['orientation']
          
          # Check if any child has relative positioning
          children = json_data['child'] || []
          children = [children] unless children.is_a?(Array)
          
          if has_relative_positioning?(children)
            # Use ConstraintLayout for relative positioning
            return ConstraintLayoutComponent.generate(json_data, depth, required_imports)
          end
          
          # Determine layout type
          layout = determine_layout(container_type, orientation)
          
          code = indent("#{layout}(", depth)
          
          # Build modifiers (correct order for Compose)
          modifiers = []
          
          # Add weight modifier if in Row or Column
          if parent_type == 'Row' || parent_type == 'Column'
            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          end
          
          # 1. Size first (total size including padding)
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # 2. Margins (outer spacing)
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # 3. Background (before padding so padding creates space inside)
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          # 4. Padding (inner spacing) - applied last
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          # Add gravity settings
          if json_data['gravity']
            code += add_gravity_settings(layout, json_data['gravity'], depth)
          end
          
          # Add direction settings
          # Note: reverseLayout is only supported by LazyColumn/LazyRow, not Column/Row
          # For regular Row/Column, we need to manually reverse the children order
          if json_data['direction'] && (layout == 'Column' || layout == 'Row')
            # Direction handling will be done by reversing children order
            # No reverseLayout parameter for regular Row/Column
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
          
          # Reverse children order if direction requires it
          if json_data['direction']
            case json_data['direction']
            when 'bottomToTop'
              children = children.reverse if layout == 'Column'
            when 'rightToLeft'
              children = children.reverse if layout == 'Row'
            end
          end
          
          # Return structure for parent to process children
          { code: code, children: children, closing: "\n" + indent("}", depth), layout_type: layout, json_data: json_data }
        end
        
        private
        
        def self.has_relative_positioning?(children)
          relative_attrs = [
            'alignTopOfView', 'alignBottomOfView', 'alignLeftOfView', 'alignRightOfView',
            'alignTopView', 'alignBottomView', 'alignLeftView', 'alignRightView',
            'alignCenterVerticalView', 'alignCenterHorizontalView'
          ]
          
          children.any? do |child|
            next false unless child.is_a?(Hash)
            relative_attrs.any? { |attr| child[attr] }
          end
        end
        
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
            when 'center'
              # center applies both vertical arrangement and horizontal alignment
              code += ",\n" + indent("verticalArrangement = Arrangement.Center", depth + 1)
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
            when 'center'
              # center applies both horizontal arrangement and vertical alignment
              code += ",\n" + indent("horizontalArrangement = Arrangement.Center", depth + 1)
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