# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ViewConverter < BaseViewConverter
        def initialize(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
          super(component, indent_level, action_manager, binding_registry)
          @converter_factory = converter_factory
          @view_registry = view_registry
        end
        
        def convert
          child_data = @component['child'] || @component['children'] || []
          # Convert single child to array
          children = child_data.is_a?(Array) ? child_data : [child_data]
          # Filter out data declarations
          children = children.reject { |child| 
            child.is_a?(Hash) && child['data'] && !child['type'] && !child['include']
          }
          
          if children.empty?
            # No children - create empty Box or Spacer
            if @component['background']
              add_line "Box("
              apply_modifiers
              add_line ")"
            else
              add_line "Spacer(modifier = Modifier"
              if @component['height']
                add_modifier_line "height(#{@component['height']}dp)"
              elsif @component['width']
                add_modifier_line "width(#{@component['width']}dp)"
              else
                add_modifier_line "size(0dp)"
              end
              add_line ")"
            end
          else
            # Determine layout type based on orientation
            orientation = @component['orientation']
            
            # Check if children have weights
            has_weights = children.any? { |child| 
              child.is_a?(Hash) && (child['weight'] || child['widthWeight'] || child['heightWeight'])
            }
            
            case orientation
            when 'horizontal'
              generate_row(children, has_weights)
            when 'vertical'
              generate_column(children, has_weights)
            else
              generate_box(children)
            end
          end
          
          generated_code
        end
        
        private
        
        def generate_row(children, has_weights)
          add_line "Row("
          
          # Add arrangement and alignment
          indent do
            arrangement = get_horizontal_arrangement
            alignment = get_vertical_alignment
            
            if arrangement
              add_line "horizontalArrangement = Arrangement.#{arrangement},"
            end
            
            if alignment
              add_line "verticalAlignment = Alignment.#{alignment},"
            end
            
            apply_modifiers
          end
          
          add_line ") {"
          
          # Render children
          indent do
            children.each do |child|
              if has_weights && child.is_a?(Hash)
                weight = child['weight'] || child['widthWeight'] || 1.0
                child_copy = child.dup
                child_copy['modifier_weight'] = weight
              end
              
              render_child(child_copy || child)
            end
          end
          
          add_line "}"
        end
        
        def generate_column(children, has_weights)
          add_line "Column("
          
          # Add arrangement and alignment
          indent do
            arrangement = get_vertical_arrangement
            alignment = get_horizontal_alignment
            
            if arrangement
              add_line "verticalArrangement = Arrangement.#{arrangement},"
            end
            
            if alignment
              add_line "horizontalAlignment = Alignment.#{alignment},"
            end
            
            apply_modifiers
          end
          
          add_line ") {"
          
          # Render children
          indent do
            children.each do |child|
              if has_weights && child.is_a?(Hash)
                weight = child['weight'] || child['heightWeight'] || 1.0
                child_copy = child.dup
                child_copy['modifier_weight'] = weight
              end
              
              render_child(child_copy || child)
            end
          end
          
          add_line "}"
        end
        
        def generate_box(children)
          add_line "Box("
          
          # Add alignment
          indent do
            alignment = get_box_alignment
            
            if alignment
              add_line "contentAlignment = Alignment.#{alignment},"
            end
            
            apply_modifiers
          end
          
          add_line ") {"
          
          # Render children
          indent do
            children.each do |child|
              render_child(child)
            end
          end
          
          add_line "}"
        end
        
        def render_child(child)
          return unless child
          
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
              
              # Collect state variables
              if child_converter.respond_to?(:state_variables) && child_converter.state_variables
                @state_variables.concat(child_converter.state_variables)
              end
            end
          end
        end
        
        def get_horizontal_arrangement
          gravity = @component['gravity'] || @component['horizontalGravity']
          return nil unless gravity
          
          case gravity
          when /center/i
            'Center'
          when /right|end/i
            'End'
          when /left|start/i
            'Start'
          when /space_between/i
            'SpaceBetween'
          when /space_around/i
            'SpaceAround'
          when /space_evenly/i
            'SpaceEvenly'
          else
            nil
          end
        end
        
        def get_vertical_arrangement
          gravity = @component['gravity'] || @component['verticalGravity']
          return nil unless gravity
          
          case gravity
          when /center/i
            'Center'
          when /bottom/i
            'Bottom'
          when /top/i
            'Top'
          when /space_between/i
            'SpaceBetween'
          when /space_around/i
            'SpaceAround'
          when /space_evenly/i
            'SpaceEvenly'
          else
            nil
          end
        end
        
        def get_horizontal_alignment
          gravity = @component['gravity'] || @component['horizontalAlignment']
          return nil unless gravity
          
          case gravity
          when /center/i
            'CenterHorizontally'
          when /right|end/i
            'End'
          when /left|start/i
            'Start'
          else
            nil
          end
        end
        
        def get_vertical_alignment
          gravity = @component['gravity'] || @component['verticalAlignment']
          return nil unless gravity
          
          case gravity
          when /center/i
            'CenterVertically'
          when /bottom/i
            'Bottom'
          when /top/i
            'Top'
          else
            nil
          end
        end
        
        def get_box_alignment
          gravity = @component['gravity'] || @component['contentAlignment']
          return 'TopStart' unless gravity
          
          # Parse compound gravity like "center|bottom"
          parts = gravity.split('|').map(&:strip)
          
          vertical = 'Top'
          horizontal = 'Start'
          
          parts.each do |part|
            case part.downcase
            when 'top'
              vertical = 'Top'
            when 'center', 'center_vertical'
              vertical = 'Center'
            when 'bottom'
              vertical = 'Bottom'
            when 'left', 'start'
              horizontal = 'Start'
            when 'center_horizontal'
              horizontal = 'Center'
            when 'right', 'end'
              horizontal = 'End'
            end
          end
          
          # Handle single center
          if gravity.downcase == 'center'
            return 'Center'
          end
          
          # Combine vertical and horizontal
          "#{vertical}#{horizontal}"
        end
      end
    end
  end
end