# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class DividerConverter < BaseViewConverter
        def convert
          add_line "HorizontalDivider("
          indent do
            # Thickness
            if @component['thickness'] || @component['height']
              thickness = @component['thickness'] || @component['height'] || 1
              add_line "thickness = #{thickness}dp,"
            end
            
            # Color
            if @component['color'] || @component['dividerColor']
              color = map_color(@component['color'] || @component['dividerColor'])
              add_line "color = #{color},"
            end
            
            # Modifiers
            modifiers = []
            
            # Width
            if @component['fillMaxWidth'] != false
              modifiers << "fillMaxWidth()"
            elsif @component['width']
              modifiers << "width(#{@component['width']}dp)"
            end
            
            # Padding/margins
            if @component['padding']
              padding = @component['padding']
              if padding.is_a?(Hash)
                if padding['horizontal']
                  modifiers << "padding(horizontal = #{padding['horizontal']}dp)"
                elsif padding['vertical']
                  modifiers << "padding(vertical = #{padding['vertical']}dp)"
                elsif padding['all']
                  modifiers << "padding(#{padding['all']}dp)"
                else
                  top = padding['top'] || 0
                  bottom = padding['bottom'] || 0
                  start = padding['start'] || padding['left'] || 0
                  end_val = padding['end'] || padding['right'] || 0
                  modifiers << "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
                end
              else
                modifiers << "padding(#{padding}dp)"
              end
            end
            
            # Build modifier chain
            if modifiers.any?
              add_line "modifier = Modifier"
              modifiers.each do |mod|
                add_modifier_line mod
              end
            end
          end
          add_line ")"
          
          generated_code
        end
      end
      
      # Vertical divider variant
      class VerticalDividerConverter < BaseViewConverter
        def convert
          add_line "VerticalDivider("
          indent do
            # Thickness
            if @component['thickness'] || @component['width']
              thickness = @component['thickness'] || @component['width'] || 1
              add_line "thickness = #{thickness}dp,"
            end
            
            # Color
            if @component['color'] || @component['dividerColor']
              color = map_color(@component['color'] || @component['dividerColor'])
              add_line "color = #{color},"
            end
            
            # Modifiers
            modifiers = []
            
            # Height
            if @component['fillMaxHeight'] != false
              modifiers << "fillMaxHeight()"
            elsif @component['height']
              modifiers << "height(#{@component['height']}dp)"
            end
            
            # Padding/margins
            if @component['padding']
              padding = @component['padding']
              if padding.is_a?(Hash)
                if padding['horizontal']
                  modifiers << "padding(horizontal = #{padding['horizontal']}dp)"
                elsif padding['vertical']
                  modifiers << "padding(vertical = #{padding['vertical']}dp)"
                elsif padding['all']
                  modifiers << "padding(#{padding['all']}dp)"
                else
                  top = padding['top'] || 0
                  bottom = padding['bottom'] || 0
                  start = padding['start'] || padding['left'] || 0
                  end_val = padding['end'] || padding['right'] || 0
                  modifiers << "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
                end
              else
                modifiers << "padding(#{padding}dp)"
              end
            end
            
            # Build modifier chain
            if modifiers.any?
              add_line "modifier = Modifier"
              modifiers.each do |mod|
                add_modifier_line mod
              end
            end
          end
          add_line ")"
          
          generated_code
        end
      end
    end
  end
end