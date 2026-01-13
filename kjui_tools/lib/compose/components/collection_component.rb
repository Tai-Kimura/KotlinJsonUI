# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class CollectionComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:lazy_grid)
          required_imports&.add(:grid_item_span)
          
          # Check if sections are defined
          sections = json_data['sections'] || []
          # Support both 'layout' and 'orientation' attributes for horizontal/vertical
          layout = json_data['layout'] || json_data['orientation'] || 'vertical'
          is_horizontal = layout == 'horizontal'
          
          # Legacy: Extract cellClasses, headerClasses, footerClasses (string arrays)
          cell_classes = json_data['cellClasses'] || []
          header_classes = json_data['headerClasses'] || []
          footer_classes = json_data['footerClasses'] || []
          
          # Use the class names directly
          cell_class_name = cell_classes.first if cell_classes.any?
          header_class_name = header_classes.first if header_classes.any?
          footer_class_name = footer_classes.first if footer_classes.any?
          
          # Calculate the grid columns based on sections or default
          default_columns = json_data['columns'] || 1
          
          if sections.any?
            # Collect all unique column counts from sections
            section_columns = sections.map { |s| s['columns'] || default_columns }.uniq
            
            # If sections have different column counts, use LCM
            if section_columns.size > 1
              columns = calculate_lcm(section_columns)
            else
              columns = section_columns.first
            end
          else
            columns = default_columns
          end
          
          # Determine grid type based on layout
          direction = is_horizontal ? 'horizontal' : 'vertical'
          
          if direction == 'horizontal'
            code = indent("LazyHorizontalGrid(", depth)
            code += "\n" + indent("rows = GridCells.Fixed(#{columns}),", depth + 1)
          else
            code = indent("LazyVerticalGrid(", depth)
            code += "\n" + indent("columns = GridCells.Fixed(#{columns}),", depth + 1)
          end
          
          # Content padding
          # Support contentPadding (array or number), insetHorizontal, insetVertical
          content_padding = json_data['contentPadding']
          inset_horizontal = json_data['insetHorizontal']
          inset_vertical = json_data['insetVertical']

          if content_padding
            if content_padding.is_a?(Array) && content_padding.length == 4
              code += "\n" + indent("contentPadding = PaddingValues(top = #{content_padding[0]}.dp, end = #{content_padding[1]}.dp, bottom = #{content_padding[2]}.dp, start = #{content_padding[3]}.dp),", depth + 1)
            elsif content_padding.is_a?(Numeric)
              code += "\n" + indent("contentPadding = PaddingValues(#{content_padding}.dp),", depth + 1)
            end
          elsif inset_horizontal || inset_vertical
            # Use insetHorizontal and/or insetVertical
            h_inset = inset_horizontal || 0
            v_inset = inset_vertical || 0
            code += "\n" + indent("contentPadding = PaddingValues(horizontal = #{h_inset}.dp, vertical = #{v_inset}.dp),", depth + 1)
          end
          
          # Item spacing
          # lineSpacing: vertical spacing between rows (minimumLineSpacing in iOS)
          # columnSpacing: horizontal spacing between columns (minimumInteritemSpacing in iOS)
          # itemSpacing/spacing: uniform spacing (fallback)
          line_spacing = json_data['lineSpacing'] || json_data['itemSpacing'] || json_data['spacing']
          column_spacing = json_data['columnSpacing'] || json_data['itemSpacing'] || json_data['spacing']

          if line_spacing || column_spacing
            required_imports&.add(:arrangement)
            if line_spacing
              code += "\n" + indent("verticalArrangement = Arrangement.spacedBy(#{line_spacing}.dp),", depth + 1)
            end
            if column_spacing
              code += "\n" + indent("horizontalArrangement = Arrangement.spacedBy(#{column_spacing}.dp),", depth + 1)
            end
          end

          # Parse gravity for item alignment (Box contentAlignment uses Alignment, not Alignment.Vertical/Horizontal)
          # Horizontal scroll: default is TopStart, can be Center/BottomStart
          # Vertical scroll: default is TopStart, can be TopCenter/TopEnd
          gravity = json_data['gravity']
          if is_horizontal
            # Horizontal scroll - vertical alignment
            gravity_alignment = case gravity.to_s.downcase
            when 'center', 'centervertical'
              'Alignment.CenterStart'
            when 'bottom'
              'Alignment.BottomStart'
            else # 'top' is default for horizontal scroll
              'Alignment.TopStart'
            end
          else
            # Vertical scroll - horizontal alignment
            gravity_alignment = case gravity.to_s.downcase
            when 'center', 'centerhorizontal'
              'Alignment.TopCenter'
            when 'right'
              'Alignment.TopEnd'
            else # 'left' is default for vertical scroll
              'Alignment.TopStart'
            end
          end
          
          # Build modifiers
          modifiers = []

          # Add testTag and contentDescription for UI testing
          modifiers.concat(Helpers::ModifierBuilder.build_test_tag(json_data, required_imports))

          # IMPORTANT: LazyVerticalGrid requires bounded width from parent
          # LazyHorizontalGrid requires bounded height from parent
          # If width/height is wrapContent, we MUST change it to avoid runtime crash
          width_value = json_data['width']
          height_value = json_data['height']

          if !is_horizontal && width_value == 'wrapContent'
            # LazyVerticalGrid with wrapContent width causes crash
            # Use fillMaxWidth to take parent's width (works when parent has bounded width)
            modified_json = json_data.merge('width' => 'matchParent')
            modifiers.concat(Helpers::ModifierBuilder.build_size(modified_json))
          elsif is_horizontal && height_value == 'wrapContent'
            # LazyHorizontalGrid with wrapContent height causes crash
            # Use fillMaxHeight to take parent's height
            modified_json = json_data.merge('height' => 'matchParent')
            modifiers.concat(Helpers::ModifierBuilder.build_size(modified_json))
          else
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          end

          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))

          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          code += "\n" + indent(") {", depth)
          
          # Check if sections are defined
          if sections.any?
            # Generate section-based collection
            code += generate_sections_content(json_data, sections, columns, depth, required_imports, gravity_alignment)
          elsif cell_class_name
            # Check if items property is specified (e.g., "@{items}")
            items_property = json_data['items']
            
            if items_property && items_property.match(/@\{([^}]+)\}/)
              # Extract property name from @{propertyName}
              property_name = $1
              
              # Items should be a Map<String, List<Any>> where key is cell class name
              # Get the items for this specific cell class
              code += "\n" + indent("// Collection with data source: #{property_name}[\"#{cell_class_name}\"]", depth + 1)
              code += "\n" + indent("val cellItems = data.#{property_name}?.get(\"#{cell_class_name}\") ?: emptyList()", depth + 1)
              code += "\n" + indent("items(cellItems.size) { index ->", depth + 1)
              code += "\n" + indent("val item = cellItems[index]", depth + 2)
            else
              # Default to empty list
              code += "\n" + indent("// Collection with no data source", depth + 1)
              code += "\n" + indent("items(0) { index ->", depth + 1)
              code += "\n" + indent("// No items", depth + 2)
            end
            
            # Create cell view with data
            code += "\n" + indent("when (val itemData = item) {", depth + 2)
            code += "\n" + indent("is #{cell_class_name}Data -> {", depth + 3)
            code += "\n" + indent("#{cell_class_name}View(", depth + 4)
            code += "\n" + indent("data = itemData,", depth + 5)
            code += "\n" + indent("viewModel = viewModel(),", depth + 5)
            code += "\n" + indent("modifier = Modifier", depth + 5)
            
            # Cell-specific modifiers
            if json_data['cellHeight']
              code += "\n" + indent("    .height(#{json_data['cellHeight']}.dp)", depth + 5)
            end
            
            # For grid layouts, ensure cells expand to fill width
            if columns > 1
              code += "\n" + indent("    .fillMaxWidth()", depth + 5)
            end
            
            code += "\n" + indent(")", depth + 4)
            code += "\n" + indent("}", depth + 3)
            code += "\n" + indent("is Map<*, *> -> {", depth + 3)
            code += "\n" + indent("// Convert map to data class", depth + 4)
            code += "\n" + indent("val data = #{cell_class_name}Data.fromMap(itemData as Map<String, Any>)", depth + 4)
            code += "\n" + indent("#{cell_class_name}View(", depth + 4)
            code += "\n" + indent("data = data,", depth + 5)
            code += "\n" + indent("viewModel = viewModel(),", depth + 5)
            code += "\n" + indent("modifier = Modifier", depth + 5)
            
            # Cell-specific modifiers
            if json_data['cellHeight']
              code += "\n" + indent("    .height(#{json_data['cellHeight']}.dp)", depth + 5)
            end
            
            # For grid layouts, ensure cells expand to fill width
            if columns > 1
              code += "\n" + indent("    .fillMaxWidth()", depth + 5)
            end
            
            code += "\n" + indent(")", depth + 4)
            code += "\n" + indent("}", depth + 3)
            code += "\n" + indent("else -> {", depth + 3)
            code += "\n" + indent("// Unsupported item type", depth + 4)
            code += "\n" + indent("}", depth + 3)
            code += "\n" + indent("}", depth + 2)
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
        
        def self.generate_sections_content(json_data, sections, grid_columns, depth, required_imports, gravity_alignment)
          code = ""
          items_property = json_data['items']
          default_columns = json_data['columns'] || 1
          
          # Check if we need GridItemSpan
          # Need it for headers/footers or when sections have different column counts
          has_headers_or_footers = sections.any? { |s| s['header'] || s['footer'] }
          section_columns_vary = sections.map { |s| s['columns'] || default_columns }.uniq.size > 1
          needs_span = sections.any? { |s| s['columns'] && s['columns'] != grid_columns }
          
          if has_headers_or_footers || section_columns_vary || needs_span
            required_imports&.add(:grid_item_span)
          end

          # Always add cell imports for all sections (regardless of items binding)
          sections.each do |section|
            cell_view_name = section['cell']
            if cell_view_name
              required_imports&.add("cell:#{cell_view_name}")
            end
            if section['header']
              required_imports&.add("cell:#{section['header']}")
            end
            if section['footer']
              required_imports&.add("cell:#{section['footer']}")
            end
          end

          if items_property && items_property.match(/@\{([^}]+)\}/)
            property_name = $1

            # Generate sections with GridItemSpan for different column counts
            sections.each_with_index do |section, index|
              cell_view_name = section['cell']
              section_columns = section['columns'] || default_columns

              # Calculate the span for items in this section
              item_span = grid_columns / section_columns

              if cell_view_name
                
                code += "\n" + indent("// Section #{index + 1}: #{cell_view_name} (#{section_columns} columns)", depth + 1)
                code += "\n" + indent("data.#{property_name}?.sections?.getOrNull(#{index})?.let { section ->", depth + 1)
                
                # Generate header if present
                if section['header']
                  header_view_name = section['header']
                  code += "\n" + indent("// Section #{index + 1} Header: #{header_view_name}", depth + 2)
                  code += "\n" + indent("section.header?.let { headerData ->", depth + 2)
                  code += "\n" + indent("item(span = { GridItemSpan(maxLineSpan) }) {", depth + 3)
                  code += "\n" + indent("val headerViewModel: #{header_view_name}ViewModel = viewModel(key = \"#{header_view_name}_header_#{index}\")", depth + 4)
                  code += "\n" + indent("headerViewModel.updateData(headerData.data)", depth + 4)
                  code += "\n" + indent("#{header_view_name}View(", depth + 4)
                  code += "\n" + indent("viewModel = headerViewModel,", depth + 5)
                  code += "\n" + indent("modifier = Modifier.fillMaxWidth()", depth + 5)
                  code += "\n" + indent(")", depth + 4)
                  code += "\n" + indent("}", depth + 3)
                  code += "\n" + indent("}", depth + 2)
                end
                
                # Generate cells
                code += "\n" + indent("section.cells?.let { cellData ->", depth + 2)
                if item_span > 1
                  code += "\n" + indent("items(cellData.data.size, span = { GridItemSpan(#{item_span}) }) { cellIndex ->", depth + 3)
                else
                  code += "\n" + indent("items(cellData.data.size) { cellIndex ->", depth + 3)
                end
                # Wrap cell in Box for alignment
                code += "\n" + indent("Box(", depth + 4)
                code += "\n" + indent("modifier = Modifier.fillMaxSize(),", depth + 5)
                code += "\n" + indent("contentAlignment = #{gravity_alignment}", depth + 5)
                code += "\n" + indent(") {", depth + 4)
                code += "\n" + indent("val cellViewModel: #{cell_view_name}ViewModel = viewModel(key = \"#{cell_view_name}_cell_\$cellIndex\")", depth + 5)
                code += "\n" + indent("cellViewModel.updateData(cellData.data[cellIndex])", depth + 5)
                code += "\n" + indent("#{cell_view_name}View(", depth + 5)
                code += "\n" + indent("viewModel = cellViewModel,", depth + 6)
                code += "\n" + indent("modifier = Modifier", depth + 6)
                code += "\n" + indent(")", depth + 5)
                code += "\n" + indent("}", depth + 4)
                code += "\n" + indent("}", depth + 3)
                code += "\n" + indent("}", depth + 2)
                
                # Generate footer if present
                if section['footer']
                  footer_view_name = section['footer']
                  code += "\n" + indent("// Section #{index + 1} Footer: #{footer_view_name}", depth + 2)
                  code += "\n" + indent("section.footer?.let { footerData ->", depth + 2)
                  code += "\n" + indent("item(span = { GridItemSpan(maxLineSpan) }) {", depth + 3)
                  code += "\n" + indent("val footerViewModel: #{footer_view_name}ViewModel = viewModel(key = \"#{footer_view_name}_footer_#{index}\")", depth + 4)
                  code += "\n" + indent("footerViewModel.updateData(footerData.data)", depth + 4)
                  code += "\n" + indent("#{footer_view_name}View(", depth + 4)
                  code += "\n" + indent("viewModel = footerViewModel,", depth + 5)
                  code += "\n" + indent("modifier = Modifier.fillMaxWidth()", depth + 5)
                  code += "\n" + indent(")", depth + 4)
                  code += "\n" + indent("}", depth + 3)
                  code += "\n" + indent("}", depth + 2)
                end
                
                  code += "\n" + indent("}", depth + 1)
                end
              end
          else
            code += "\n" + indent("// No items binding specified", depth + 1)
          end
          
          code
        end
        
        private
        
        def self.calculate_lcm(numbers)
          numbers.reduce(1) { |lcm, n| lcm.lcm(n) }
        end
        
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