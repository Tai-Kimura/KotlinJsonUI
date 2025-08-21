# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ConstraintLayoutComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
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
            'alignCenterVerticalView', 'alignCenterHorizontalView',
            'alignTop', 'alignBottom', 'alignLeft', 'alignRight',
            'centerHorizontal', 'centerVertical', 'centerInParent'
          ]
          
          relative_attrs.any? { |attr| component[attr] }
        end
        
        def self.has_positioning_constraints?(component)
          return false unless component.is_a?(Hash)
          
          # These are constraints that use margins in linkTo()
          # For alignXxxView, margins should be applied as padding modifiers
          # For alignTop/Bottom/Left/Right to parent, margins are applied in linkTo()
          positioning_attrs = [
            'alignTopOfView', 'alignBottomOfView', 'alignLeftOfView', 'alignRightOfView',
            'alignTopView', 'alignBottomView', 'alignLeftView', 'alignRightView',
            'alignCenterVerticalView', 'alignCenterHorizontalView',
            'alignTop', 'alignBottom', 'alignLeft', 'alignRight'
          ]
          
          # centerInParent, centerHorizontal, centerVertical don't use margins in linkTo()
          # so they should still apply margins as padding
          positioning_attrs.any? { |attr| component[attr] }
        end
        
        def self.should_apply_margins_as_padding?(component)
          return false unless component.is_a?(Hash)
          
          # Don't apply margins as padding if they're already handled in linkTo()
          # All positioning constraints now handle margins in linkTo()
          return !has_positioning_constraints?(component)
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
          
          # Always add constrainAs for all children in ConstraintLayout
          # Insert constrainAs modifier
          constraint_content = if constraints.any?
            constraints.map { |c| indent(c, depth + 2) }.join("\n")
          else
            "" # Empty constraint block
          end
          
          # Find where to insert the constrainAs modifier
          if component_code.include?("modifier = Modifier")
            # Replace existing modifier with constrainAs
            component_code.sub!(/modifier = Modifier(.*?)(?=,\n|\n)/m) do |match|
              existing_modifiers = $1
              "modifier = Modifier.constrainAs(#{ref_name}) {\n#{constraint_content}\n" + indent("}", depth + 1) + existing_modifiers
            end
          else
            # Add new modifier after the opening parenthesis
            insert_pos = component_code.index("(") + 1
            modifier_code = "\n" + indent("modifier = Modifier.constrainAs(#{ref_name}) {", depth + 1)
            if constraint_content.length > 0
              modifier_code += "\n#{constraint_content}"
            end
            modifier_code += "\n" + indent("}", depth + 1) + ","
            component_code.insert(insert_pos, modifier_code)
          end
          
          component_code
        end
        
        def self.generate_text_component(data, depth, required_imports)
          text = data['text'] || ''
          # Check for data binding
          if text.start_with?('@{')
            variable_name = text[2..-2]
            escaped_text = "\"${data.#{variable_name}}\""
          else
            escaped_text = quote(text)
          end
          
          code = indent("Text(", depth)
          
          # Add modifier with constraints
          # In ConstraintLayout:
          # - If element has relative positioning constraints, margins are handled ONLY in linkTo()
          # - If element has no constraints (just centerInParent etc), margins are applied as padding
          modifiers = []
          
          # Apply margins BEFORE size so they act as outer spacing
          # This ensures the size is the actual content size, not reduced by margins
          if should_apply_margins_as_padding?(data)
            modifiers.concat(Helpers::ModifierBuilder.build_margins(data))
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_size(data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(data, required_imports))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(data))
          
          if modifiers.any?
            code += "\n" + indent("modifier = Modifier", depth + 1)
            modifiers.each do |mod|
              code += "\n" + indent("    #{mod}", depth + 1)
            end
            code += ","
          end
          
          code += "\n" + indent("text = #{escaped_text}", depth + 1)
          
          if data['fontSize']
            code += ",\n" + indent("fontSize = #{data['fontSize']}.sp", depth + 1)
          end
          
          if data['fontColor'] || data['color']
            color = data['fontColor'] || data['color']
            code += ",\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{color}\"))", depth + 1)
          end
          
          if data['font'] == 'bold' || data['fontWeight'] == 'bold'
            required_imports&.add(:font_weight)
            code += ",\n" + indent("fontWeight = FontWeight.Bold", depth + 1)
          end
          
          if data['textAlign']
            required_imports&.add(:text_align)
            align = case data['textAlign']
            when 'center' then 'TextAlign.Center'
            when 'left' then 'TextAlign.Left'
            when 'right' then 'TextAlign.Right'
            else 'TextAlign.Start'
            end
            code += ",\n" + indent("textAlign = #{align}", depth + 1)
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