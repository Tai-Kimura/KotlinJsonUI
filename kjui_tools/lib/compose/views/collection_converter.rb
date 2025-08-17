# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class CollectionConverter < BaseViewConverter
        def initialize(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
          super(component, indent_level, action_manager, binding_registry)
          @converter_factory = converter_factory
          @view_registry = view_registry
        end
        
        def convert
          collection_type = determine_collection_type
          
          case collection_type
          when 'LazyColumn'
            generate_lazy_column
          when 'LazyRow'
            generate_lazy_row
          when 'LazyVerticalGrid'
            generate_lazy_grid(false)
          when 'LazyHorizontalGrid'
            generate_lazy_grid(true)
          else
            generate_lazy_column
          end
          
          generated_code
        end
        
        private
        
        def determine_collection_type
          type = @component['type']
          
          case type
          when 'Collection', 'RecyclerView', 'ListView', 'LazyColumn'
            'LazyColumn'
          when 'LazyRow', 'HorizontalList'
            'LazyRow'
          when 'LazyGrid', 'LazyVerticalGrid', 'GridView'
            'LazyVerticalGrid'
          when 'LazyHorizontalGrid'
            'LazyHorizontalGrid'
          else
            # Check orientation
            if @component['orientation'] == 'horizontal'
              'LazyRow'
            elsif @component['columns'] || @component['gridColumns']
              'LazyVerticalGrid'
            else
              'LazyColumn'
            end
          end
        end
        
        def generate_lazy_column
          items_source = get_items_source
          
          add_line "LazyColumn("
          indent do
            # Content padding
            if @component['contentPadding']
              generate_content_padding
            end
            
            # Vertical arrangement
            if @component['verticalArrangement'] || @component['spacing']
              arrangement = map_vertical_arrangement(@component['verticalArrangement'])
              spacing = @component['spacing'] || 0
              add_line "verticalArrangement = Arrangement.spacedBy(#{spacing}dp, Alignment.#{arrangement || 'Top'}),"
            end
            
            # Horizontal alignment
            if @component['horizontalAlignment']
              alignment = map_horizontal_alignment(@component['horizontalAlignment'])
              add_line "horizontalAlignment = Alignment.#{alignment},"
            end
            
            # Reverse layout
            if @component['reverseLayout']
              add_line "reverseLayout = true,"
            end
            
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            if @component['cellClasses']
              generate_cell_classes_items
            elsif items_source
              generate_dynamic_items(items_source)
            else
              generate_static_items
            end
          end
          
          add_line "}"
        end
        
        def generate_lazy_row
          items_source = get_items_source
          
          add_line "LazyRow("
          indent do
            # Content padding
            if @component['contentPadding']
              generate_content_padding
            end
            
            # Horizontal arrangement
            if @component['horizontalArrangement'] || @component['spacing']
              arrangement = map_horizontal_arrangement(@component['horizontalArrangement'])
              spacing = @component['spacing'] || 0
              add_line "horizontalArrangement = Arrangement.spacedBy(#{spacing}dp, Alignment.#{arrangement || 'Start'}),"
            end
            
            # Vertical alignment
            if @component['verticalAlignment']
              alignment = map_vertical_alignment(@component['verticalAlignment'])
              add_line "verticalAlignment = Alignment.#{alignment},"
            end
            
            # Reverse layout
            if @component['reverseLayout']
              add_line "reverseLayout = true,"
            end
            
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            if @component['cellClasses']
              generate_cell_classes_items
            elsif items_source
              generate_dynamic_items(items_source)
            else
              generate_static_items
            end
          end
          
          add_line "}"
        end
        
        def generate_lazy_grid(horizontal)
          items_source = get_items_source
          grid_type = horizontal ? 'LazyHorizontalGrid' : 'LazyVerticalGrid'
          
          add_line "#{grid_type}("
          indent do
            # Grid columns/rows
            columns = @component['columns'] || @component['gridColumns'] || 2
            if horizontal
              add_line "rows = GridCells.Fixed(#{columns}),"
            else
              add_line "columns = GridCells.Fixed(#{columns}),"
            end
            
            # Content padding
            if @component['contentPadding']
              generate_content_padding
            end
            
            # Arrangement
            if @component['verticalArrangement'] || @component['horizontalArrangement'] || @component['spacing']
              spacing = @component['spacing'] || 0
              add_line "verticalArrangement = Arrangement.spacedBy(#{spacing}dp),"
              add_line "horizontalArrangement = Arrangement.spacedBy(#{spacing}dp),"
            end
            
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            if @component['cellClasses']
              generate_cell_classes_items
            elsif items_source
              generate_dynamic_items(items_source)
            else
              generate_static_items
            end
          end
          
          add_line "}"
        end
        
        def generate_content_padding
          padding = @component['contentPadding']
          if padding.is_a?(Hash)
            horizontal = padding['horizontal'] || 0
            vertical = padding['vertical'] || 0
            add_line "contentPadding = PaddingValues(horizontal = #{horizontal}dp, vertical = #{vertical}dp),"
          else
            add_line "contentPadding = PaddingValues(#{padding}dp),"
          end
        end
        
        def get_items_source
          # Check for data binding
          if @component['itemsSource']
            if is_binding?(@component['itemsSource'])
              extract_binding_property(@component['itemsSource'])
            else
              @component['itemsSource']
            end
          elsif @component['items']
            if is_binding?(@component['items'])
              extract_binding_property(@component['items'])
            else
              @component['items']
            end
          else
            nil
          end
        end
        
        def generate_dynamic_items(items_source)
          add_line "items(#{items_source}) { item ->"
          indent do
            if @component['itemTemplate']
              generate_item_template(@component['itemTemplate'])
            else
              # Default item display
              add_line "Text(text = item.toString())"
            end
          end
          add_line "}"
        end
        
        def generate_cell_classes_items
          cell_classes = @component['cellClasses']
          return unless cell_classes.is_a?(Array)
          
          items_source = get_items_source || "listOf()"
          
          add_line "itemsIndexed(#{items_source}) { index, item ->"
          indent do
            add_line "when (index % #{cell_classes.length}) {"
            indent do
              cell_classes.each_with_index do |cell_class, idx|
                add_line "#{idx} -> {"
                indent do
                  # Generate view for this cell class
                  if cell_class.is_a?(Hash)
                    child_converter = @converter_factory.create_converter(
                      cell_class,
                      @indent_level + 2,
                      @action_manager,
                      @converter_factory,
                      @view_registry,
                      @binding_registry
                    )
                    
                    if child_converter
                      child_code = child_converter.convert
                      child_lines = child_code.split("\n")
                      child_lines.each { |line| add_line line.strip unless line.strip.empty? }
                    end
                  else
                    add_line "// Cell class: #{cell_class}"
                    add_line "Text(\"Item $index\")"
                  end
                end
                add_line "}"
              end
              add_line "else -> {}"
            end
            add_line "}"
          end
          add_line "}"
        end
        
        def generate_static_items
          children = @component['child'] || @component['children'] || []
          children = children.is_a?(Array) ? children : [children]
          
          children.each_with_index do |child, index|
            add_line "item {"
            indent do
              if @converter_factory
                child_converter = @converter_factory.create_converter(
                  child,
                  @indent_level + 1,
                  @action_manager,
                  @converter_factory,
                  @view_registry,
                  @binding_registry
                )
                
                if child_converter
                  child_code = child_converter.convert
                  child_lines = child_code.split("\n")
                  child_lines.each { |line| add_line line.strip unless line.strip.empty? }
                end
              end
            end
            add_line "}"
          end
        end
        
        def generate_item_template(template)
          if template.is_a?(Hash)
            child_converter = @converter_factory.create_converter(
              template,
              @indent_level + 1,
              @action_manager,
              @converter_factory,
              @view_registry,
              @binding_registry
            )
            
            if child_converter
              child_code = child_converter.convert
              child_lines = child_code.split("\n")
              child_lines.each { |line| add_line line.strip unless line.strip.empty? }
            end
          else
            add_line "Text(text = item.toString())"
          end
        end
        
        def map_vertical_arrangement(arrangement)
          case arrangement.to_s.downcase
          when 'top'
            'Top'
          when 'center'
            'Center'
          when 'bottom'
            'Bottom'
          when 'space_between'
            'SpaceBetween'
          when 'space_around'
            'SpaceAround'
          when 'space_evenly'
            'SpaceEvenly'
          else
            'Top'
          end
        end
        
        def map_horizontal_arrangement(arrangement)
          case arrangement.to_s.downcase
          when 'start', 'left'
            'Start'
          when 'center'
            'Center'
          when 'end', 'right'
            'End'
          when 'space_between'
            'SpaceBetween'
          when 'space_around'
            'SpaceAround'
          when 'space_evenly'
            'SpaceEvenly'
          else
            'Start'
          end
        end
        
        def map_horizontal_alignment(alignment)
          case alignment.to_s.downcase
          when 'start', 'left'
            'Start'
          when 'center'
            'CenterHorizontally'
          when 'end', 'right'
            'End'
          else
            'Start'
          end
        end
        
        def map_vertical_alignment(alignment)
          case alignment.to_s.downcase
          when 'top'
            'Top'
          when 'center'
            'CenterVertically'
          when 'bottom'
            'Bottom'
          else
            'Top'
          end
        end
      end
    end
  end
end