# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ScrollViewConverter < BaseViewConverter
        def initialize(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
          super(component, indent_level, action_manager, binding_registry)
          @converter_factory = converter_factory
          @view_registry = view_registry
        end
        
        def convert
          scroll_direction = determine_scroll_direction
          
          # Determine if it's horizontal or vertical scroll
          if scroll_direction == 'horizontal'
            generate_horizontal_scroll
          else
            generate_vertical_scroll
          end
          
          generated_code
        end
        
        private
        
        def determine_scroll_direction
          # Check for explicit direction
          return @component['scrollDirection'] if @component['scrollDirection']
          
          # Check component type
          case @component['type']
          when 'HorizontalScrollView'
            'horizontal'
          when 'VerticalScrollView', 'ScrollView'
            'vertical'
          else
            # Check orientation of child
            child = @component['child'] || @component['children']
            if child.is_a?(Hash) && child['orientation'] == 'horizontal'
              'horizontal'
            else
              'vertical'
            end
          end
        end
        
        def generate_vertical_scroll
          add_line "Column("
          indent do
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            add_line "val scrollState = rememberScrollState()"
            add_line ""
            add_line "Column("
            indent do
              add_line "modifier = Modifier"
              add_modifier_line "verticalScroll(scrollState)"
              
              # Apply scrollbar if needed
              if @component['showScrollbar'] != false
                add_modifier_line "scrollbar(scrollState, ScrollbarOrientation.Vertical)"
              end
              
              # Fill width by default for vertical scroll
              unless @component['fillMaxWidth'] == false
                add_modifier_line "fillMaxWidth()"
              end
              
              # Padding for content
              if @component['contentPadding']
                padding = @component['contentPadding']
                if padding.is_a?(Hash)
                  top = padding['top'] || 0
                  bottom = padding['bottom'] || 0
                  start = padding['start'] || 0
                  end_val = padding['end'] || 0
                  add_modifier_line "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
                else
                  add_modifier_line "padding(#{padding}dp)"
                end
              end
            end
            add_line ") {"
            
            # Render children
            indent do
              render_children
            end
            
            add_line "}"
          end
          
          add_line "}"
        end
        
        def generate_horizontal_scroll
          add_line "Row("
          indent do
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            add_line "val scrollState = rememberScrollState()"
            add_line ""
            add_line "Row("
            indent do
              add_line "modifier = Modifier"
              add_modifier_line "horizontalScroll(scrollState)"
              
              # Apply scrollbar if needed
              if @component['showScrollbar'] != false
                add_modifier_line "scrollbar(scrollState, ScrollbarOrientation.Horizontal)"
              end
              
              # Fill height by default for horizontal scroll
              unless @component['fillMaxHeight'] == false
                add_modifier_line "fillMaxHeight()"
              end
              
              # Padding for content
              if @component['contentPadding']
                padding = @component['contentPadding']
                if padding.is_a?(Hash)
                  top = padding['top'] || 0
                  bottom = padding['bottom'] || 0
                  start = padding['start'] || 0
                  end_val = padding['end'] || 0
                  add_modifier_line "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
                else
                  add_modifier_line "padding(#{padding}dp)"
                end
              end
              
              # Vertical alignment for horizontal scroll
              if @component['verticalAlignment']
                alignment = map_vertical_alignment(@component['verticalAlignment'])
                add_line ", verticalAlignment = Alignment.#{alignment}"
              end
            end
            add_line ") {"
            
            # Render children
            indent do
              render_children
            end
            
            add_line "}"
          end
          
          add_line "}"
        end
        
        def render_children
          child_data = @component['child'] || @component['children'] || []
          children = child_data.is_a?(Array) ? child_data : [child_data]
          
          children.each do |child|
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
        end
        
        def map_vertical_alignment(alignment)
          case alignment.to_s.downcase
          when 'top'
            'Top'
          when 'center', 'center_vertical'
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