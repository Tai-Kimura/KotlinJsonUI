# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class CollectionComponent
        def self.generate(json_data, depth, required_imports = nil)
          required_imports&.add(:lazy_grid)
          
          # Collection uses data binding for items
          items = if json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          elsif json_data['items'] && json_data['items'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            'emptyList()'
          end
          
          # Number of columns
          columns = json_data['columns'] || 2
          
          # Determine grid type based on scroll direction
          direction = json_data['scrollDirection'] || 'vertical'
          
          if direction == 'horizontal'
            code = indent("LazyHorizontalGrid(", depth)
            code += "\n" + indent("rows = GridCells.Fixed(#{columns}),", depth + 1)
          else
            code = indent("LazyVerticalGrid(", depth)
            code += "\n" + indent("columns = GridCells.Fixed(#{columns}),", depth + 1)
          end
          
          # Content padding
          if json_data['contentPadding']
            padding = json_data['contentPadding']
            if padding.is_a?(Array) && padding.length == 4
              code += "\n" + indent("contentPadding = PaddingValues(top = #{padding[0]}.dp, end = #{padding[1]}.dp, bottom = #{padding[2]}.dp, start = #{padding[3]}.dp),", depth + 1)
            elsif padding.is_a?(Numeric)
              code += "\n" + indent("contentPadding = PaddingValues(#{padding}.dp),", depth + 1)
            end
          end
          
          # Spacing between items
          if json_data['spacing']
            required_imports&.add(:arrangement)
            code += "\n" + indent("verticalArrangement = Arrangement.spacedBy(#{json_data['spacing']}.dp),", depth + 1)
            code += "\n" + indent("horizontalArrangement = Arrangement.spacedBy(#{json_data['spacing']}.dp),", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          code += "\n" + indent(") {", depth)
          
          # Items
          code += "\n" + indent("items(#{items}) { item ->", depth + 1)
          
          # Cell content
          if json_data['cell']
            # Custom cell template
            cell_content = generate_cell_content(json_data['cell'], depth + 2, required_imports)
            code += "\n" + cell_content
          else
            # Default cell with text
            code += "\n" + indent("Card(", depth + 2)
            code += "\n" + indent("modifier = Modifier", depth + 3)
            code += "\n" + indent("    .padding(4.dp)", depth + 3)
            code += "\n" + indent("    .fillMaxWidth()", depth + 3)
            code += "\n" + indent(") {", depth + 2)
            code += "\n" + indent("Text(", depth + 3)
            code += "\n" + indent("text = item.toString(),", depth + 4)
            code += "\n" + indent("modifier = Modifier.padding(16.dp)", depth + 4)
            code += "\n" + indent(")", depth + 3)
            code += "\n" + indent("}", depth + 2)
          end
          
          code += "\n" + indent("}", depth + 1)
          code += "\n" + indent("}", depth)
          code
        end
        
        private
        
        def self.generate_cell_content(cell_data, depth, required_imports)
          # Generate content for custom cell
          # This is simplified - in reality would need to parse cell JSON
          code = indent("Card(", depth)
          code += "\n" + indent("modifier = Modifier.padding(4.dp)", depth + 1)
          code += "\n" + indent(") {", depth)
          
          if cell_data.is_a?(Hash)
            # Parse cell structure
            code += "\n" + indent("// Custom cell content", depth + 1)
            code += "\n" + indent("Box(modifier = Modifier.padding(8.dp)) {", depth + 1)
            code += "\n" + indent("Text(text = item.toString())", depth + 2)
            code += "\n" + indent("}", depth + 1)
          else
            code += "\n" + indent("Text(text = item.toString(), modifier = Modifier.padding(8.dp))", depth + 1)
          end
          
          code += "\n" + indent("}", depth)
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