# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class SpacerConverter < BaseViewConverter
        def convert
          add_line "Spacer("
          indent do
            modifiers = []
            
            # Size modifiers
            if @component['fillMaxWidth']
              modifiers << "fillMaxWidth()"
            end
            
            if @component['fillMaxHeight']
              modifiers << "fillMaxHeight()"
            end
            
            if @component['width'] && @component['height']
              modifiers << "size(#{@component['width']}dp, #{@component['height']}dp)"
            elsif @component['size']
              modifiers << "size(#{@component['size']}dp)"
            elsif @component['width']
              modifiers << "width(#{@component['width']}dp)"
            elsif @component['height']
              modifiers << "height(#{@component['height']}dp)"
            elsif @component['weight']
              modifiers << "weight(#{@component['weight']}f)"
            else
              # Default to small spacer if no size specified
              modifiers << "size(8dp)"
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