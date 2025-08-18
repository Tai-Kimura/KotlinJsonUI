# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ConstraintLayoutComponent
        def self.generate(json_data, depth, required_imports = nil)
          required_imports&.add(:constraint_layout)
          
          # Check if any child has relative positioning attributes
          children = json_data['child'] || []
          children = [children] unless children.is_a?(Array)
          
          has_constraints = children.any? { |child| has_relative_positioning?(child) }
          
          if has_constraints
            generate_constraint_layout(json_data, children, depth, required_imports)
          else
            # Fall back to regular Box/Column/Row
            Components::ContainerComponent.generate(json_data, depth, required_imports)
          end
        end
        
        private
        
        def self.has_relative_positioning?(component)
          return false unless component.is_a?(Hash)
          
          relative_attrs = [
            'alignTopOfView', 'alignBottomOfView', 'alignLeftOfView', 'alignRightOfView',
            'alignTopView', 'alignBottomView', 'alignLeftView', 'alignRightView',
            'alignCenterVerticalView', 'alignCenterHorizontalView'
          ]
          
          relative_attrs.any? { |attr| component[attr] }
        end
        
        def self.generate_constraint_layout(json_data, children, depth, required_imports)
          code = indent("ConstraintLayout(", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          code += "\n" + indent(") {", depth)
          
          # Create constraint references
          constraint_refs = []
          children.each_with_index do |child, index|
            if child.is_a?(Hash) && (child['id'] || has_relative_positioning?(child))
              ref_name = child['id'] || "view_#{index}"
              code += "\n" + indent("val #{ref_name} = createRef()", depth + 1)
              constraint_refs << ref_name
            end
          end
          
          code += "\n" if constraint_refs.any?
          
          # Generate children with constraints
          children.each_with_index do |child, index|
            if child.is_a?(Hash)
              ref_name = child['id'] || "view_#{index}"
              
              # Generate the child component
              child_code = generate_child_with_constraints(child, ref_name, depth + 1, required_imports)
              code += "\n" + child_code unless child_code.empty?
            end
          end
          
          code += "\n" + indent("}", depth)
          code
        end
        
        def self.generate_child_with_constraints(child_data, ref_name, depth, required_imports)
          # Get the component type
          component_type = child_data['type'] || 'View'
          
          # Generate the component code based on type
          component_code = case component_type
          when 'Text', 'Label'
            generate_text_component(child_data, depth, required_imports)
          when 'Button'
            generate_button_component(child_data, depth, required_imports)
          when 'Image'
            generate_image_component(child_data, depth, required_imports)
          else
            generate_box_component(child_data, depth, required_imports)
          end
          
          # Add constrainAs modifier
          constraints = Helpers::ModifierBuilder.build_relative_positioning(child_data)
          
          if constraints.any? || has_relative_positioning?(child_data)
            # Insert constrainAs modifier
            modifier_line = "modifier = Modifier.constrainAs(#{ref_name}) {"
            constraint_block = constraints.map { |c| indent(c, depth + 2) }.join("\n")
            constraint_close = indent("}", depth + 1)
            
            # Insert the constraint modifier into the component code
            if component_code.include?("modifier = Modifier")
              component_code.sub!(/modifier = Modifier/, "#{modifier_line}\n#{constraint_block}\n#{constraint_close}")
            else
              # Add modifier if not present
              insert_pos = component_code.index("(") + 1
              component_code.insert(insert_pos, "\n" + indent(modifier_line, depth + 1) + "\n" + constraint_block + "\n" + constraint_close + ",")
            end
          end
          
          component_code
        end
        
        def self.generate_text_component(data, depth, required_imports)
          text = data['text'] || ''
          # Properly escape text
          escaped_text = quote(text)
          code = indent("Text(", depth)
          code += "\n" + indent("text = #{escaped_text}", depth + 1)
          
          if data['fontSize']
            code += ",\n" + indent("fontSize = #{data['fontSize']}.sp", depth + 1)
          end
          
          if data['fontColor']
            code += ",\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{data['fontColor']}\"))", depth + 1)
          end
          
          code += "\n" + indent(")", depth)
          code
        end
        
        def self.generate_button_component(data, depth, required_imports)
          text = data['text'] || 'Button'
          onclick = data['onclick']
          # Properly escape text
          escaped_text = quote(text)
          
          code = indent("Button(", depth)
          
          if onclick
            code += "\n" + indent("onClick = { viewModel.#{onclick}() }", depth + 1)
          else
            code += "\n" + indent("onClick = { }", depth + 1)
          end
          
          code += "\n" + indent(") {", depth)
          code += "\n" + indent("Text(#{escaped_text})", depth + 1)
          code += "\n" + indent("}", depth)
          code
        end
        
        def self.generate_image_component(data, depth, required_imports)
          source = data['src'] || data['source'] || 'placeholder'
          
          code = indent("Image(", depth)
          code += "\n" + indent("painter = painterResource(R.drawable.#{source.gsub('.png', '').gsub('.jpg', '')}),", depth + 1)
          code += "\n" + indent("contentDescription = \"Image\"", depth + 1)
          code += "\n" + indent(")", depth)
          code
        end
        
        def self.generate_box_component(data, depth, required_imports)
          code = indent("Box(", depth)
          code += "\n" + indent(") {", depth)
          code += "\n" + indent("// Content", depth + 1)
          code += "\n" + indent("}", depth)
          code
        end
        
        def self.quote(text)
          # Escape special characters properly
          escaped = text.gsub('\\', '\\\\\\\\')  # Escape backslashes first
                       .gsub('"', '\\"')           # Escape quotes
                       .gsub("\n", '\\n')           # Escape newlines
                       .gsub("\r", '\\r')           # Escape carriage returns
                       .gsub("\t", '\\t')           # Escape tabs
          "\"#{escaped}\""
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