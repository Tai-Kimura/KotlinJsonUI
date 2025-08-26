# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class CollectionComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:lazy_grid)
          
          # Extract cellClasses, headerClasses, footerClasses (string arrays)
          cell_classes = json_data['cellClasses'] || []
          header_classes = json_data['headerClasses'] || []
          footer_classes = json_data['footerClasses'] || []
          
          # Extract the first cell class name (primary cell type)
          cell_class_name = extract_view_name(cell_classes.first) if cell_classes.any?
          header_class_name = extract_view_name(header_classes.first) if header_classes.any?
          footer_class_name = extract_view_name(footer_classes.first) if footer_classes.any?
          
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
          
          # Item spacing
          if json_data['itemSpacing'] || json_data['spacing']
            spacing = json_data['itemSpacing'] || json_data['spacing'] || 10
            required_imports&.add(:arrangement)
            code += "\n" + indent("verticalArrangement = Arrangement.spacedBy(#{spacing}.dp),", depth + 1)
            code += "\n" + indent("horizontalArrangement = Arrangement.spacedBy(#{spacing}.dp),", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          code += "\n" + indent(") {", depth)
          
          # Generate collection content based on cellClasses
          if cell_class_name
            # Check if items property is specified (e.g., "@{items}")
            items_property = json_data['items']
            
            if items_property && items_property.match(/@\{([^}]+)\}/)
              # Extract property name from @{propertyName}
              property_name = $1
              
              # Get the original class name for getCellData
              original_class_name = cell_classes.first
              
              # Use getCellData pattern for collections with data sources
              code += "\n" + indent("// Collection with data source: #{property_name}", depth + 1)
              code += "\n" + indent("val cellData = data.#{property_name}.getCellData(\"#{original_class_name}\")", depth + 1)
              code += "\n" + indent("items(cellData.size) { index ->", depth + 1)
              code += "\n" + indent("val item = cellData[index]", depth + 2)
            else
              # Default to collectionDataSource
              original_class_name = cell_classes.first
              
              code += "\n" + indent("// Collection with default data source", depth + 1)
              code += "\n" + indent("val cellData = data.collectionDataSource.getCellData(\"#{original_class_name}\")", depth + 1)
              code += "\n" + indent("items(cellData.size) { index ->", depth + 1)
              code += "\n" + indent("val item = cellData[index]", depth + 2)
            end
            
            # Create cell view with data
            code += "\n" + indent("#{cell_class_name}(", depth + 2)
            code += "\n" + indent("data = item,", depth + 3)
            code += "\n" + indent("modifier = Modifier", depth + 3)
            
            # Cell-specific modifiers
            if json_data['cellHeight']
              code += "\n" + indent("    .height(#{json_data['cellHeight']}.dp)", depth + 3)
            end
            
            # For grid layouts, ensure cells expand to fill width
            if columns > 1
              code += "\n" + indent("    .fillMaxWidth()", depth + 3)
            end
            
            code += "\n" + indent(")", depth + 2)
            code += "\n" + indent("}", depth + 1)
          else
            # No cell class specified - show placeholder
            code += "\n" + indent("// No cellClasses specified", depth + 1)
            code += "\n" + indent("items(10) { index ->", depth + 1)
            code += "\n" + indent("Card(", depth + 2)
            code += "\n" + indent("modifier = Modifier", depth + 3)
            code += "\n" + indent("    .padding(4.dp)", depth + 3)
            code += "\n" + indent("    .fillMaxWidth()", depth + 3)
            code += "\n" + indent("    .height(80.dp)", depth + 3)
            code += "\n" + indent(") {", depth + 2)
            code += "\n" + indent("Box(", depth + 3)
            code += "\n" + indent("modifier = Modifier.fillMaxSize(),", depth + 4)
            code += "\n" + indent("contentAlignment = Alignment.Center", depth + 4)
            code += "\n" + indent(") {", depth + 3)
            code += "\n" + indent("Text(\"Item ${index}\")", depth + 4)
            code += "\n" + indent("}", depth + 3)
            code += "\n" + indent("}", depth + 2)
            code += "\n" + indent("}", depth + 1)
          end
          
          code += "\n" + indent("}", depth)
          code
        end
        
        private
        
        def self.extract_view_name(class_name)
          return nil unless class_name
          
          # Convert cell class name to Compose view name
          # Remove common suffixes and add appropriate naming
          view_name = class_name
          
          # Remove common UIKit/Android suffixes
          view_name = view_name.sub(/CollectionViewCell$/, '')
          view_name = view_name.sub(/Cell$/, '')
          view_name = view_name.sub(/cell$/, '')
          
          # Convert to proper case and add View suffix if needed
          view_name = to_pascal_case(view_name)
          view_name += 'View' unless view_name.end_with?('View')
          
          view_name
        end
        
        def self.to_pascal_case(str)
          return str if str.nil? || str.empty?
          
          # Handle snake_case or kebab-case to PascalCase
          parts = str.split(/[_-]/)
          parts.map(&:capitalize).join
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