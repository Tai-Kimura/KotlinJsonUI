# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class CardConverter < BaseViewConverter
        def initialize(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
          super(component, indent_level, action_manager, binding_registry)
          @converter_factory = converter_factory
          @view_registry = view_registry
        end
        
        def convert
          card_type = determine_card_type
          
          add_line "#{card_type}("
          indent do
            # Click handling
            if @component['onclick']
              add_line "onClick = { viewModel.#{@component['onclick']}() },"
            end
            
            # Shape
            if @component['cornerRadius']
              add_line "shape = RoundedCornerShape(#{@component['cornerRadius']}dp),"
            elsif @component['shape']
              shape = map_shape(@component['shape'])
              add_line "shape = #{shape},"
            end
            
            # Colors
            if @component['backgroundColor'] || @component['containerColor']
              add_line "colors = CardDefaults.cardColors("
              indent do
                color = map_color(@component['backgroundColor'] || @component['containerColor'])
                add_line "containerColor = #{color}"
              end
              add_line "),"
            end
            
            # Elevation
            if @component['elevation']
              add_line "elevation = CardDefaults.cardElevation("
              indent do
                add_line "defaultElevation = #{@component['elevation']}dp"
              end
              add_line "),"
            end
            
            # Border
            if @component['borderWidth'] && @component['borderColor']
              add_line "border = BorderStroke("
              indent do
                add_line "width = #{@component['borderWidth']}dp,"
                add_line "color = #{map_color(@component['borderColor'])}"
              end
              add_line "),"
            end
            
            apply_modifiers
          end
          add_line ") {"
          
          # Card content
          indent do
            render_children
          end
          
          add_line "}"
          
          generated_code
        end
        
        private
        
        def determine_card_type
          style = @component['cardStyle'] || @component['style']
          
          case style
          when 'elevated'
            'ElevatedCard'
          when 'outlined'
            'OutlinedCard'
          when 'filled'
            'Card'
          else
            # Default based on properties
            if @component['elevation']
              'ElevatedCard'
            elsif @component['borderWidth'] || @component['borderColor']
              'OutlinedCard'
            else
              'Card'
            end
          end
        end
        
        def map_shape(shape)
          case shape.to_s.downcase
          when 'rectangle'
            'RectangleShape'
          when 'circle'
            'CircleShape'
          when 'rounded', 'roundedrectangle'
            'RoundedCornerShape(8dp)'
          else
            'RectangleShape'
          end
        end
        
        def render_children
          child_data = @component['child'] || @component['children'] || []
          children = child_data.is_a?(Array) ? child_data : [child_data]
          
          # If card has padding for content
          if @component['contentPadding']
            add_line "Column("
            indent do
              padding = @component['contentPadding']
              if padding.is_a?(Hash)
                if padding['all']
                  add_line "modifier = Modifier.padding(#{padding['all']}dp)"
                else
                  top = padding['top'] || 0
                  bottom = padding['bottom'] || 0
                  start = padding['start'] || 0
                  end_val = padding['end'] || 0
                  add_line "modifier = Modifier.padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
                end
              else
                add_line "modifier = Modifier.padding(#{padding}dp)"
              end
            end
            add_line ") {"
            
            indent do
              render_child_elements(children)
            end
            
            add_line "}"
          else
            render_child_elements(children)
          end
        end
        
        def render_child_elements(children)
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
      end
      
      # Surface is similar to Card but with different defaults
      class SurfaceConverter < CardConverter
        def determine_card_type
          'Surface'
        end
      end
    end
  end
end