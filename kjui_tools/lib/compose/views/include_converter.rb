# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class IncludeConverter < BaseViewConverter
        def convert
          include_name = @component['include']
          return "" unless include_name
          
          pascal_name = to_pascal_case(include_name)
          
          add_line "#{pascal_name}View("
          indent do
            # Pass viewModel
            add_line "viewModel = viewModel,"
            
            # Pass data if specified
            if @component['data']
              add_line "data = #{pascal_name}Data("
              indent do
                @component['data'].each do |key, value|
                  processed_value = process_value(value)
                  add_line "#{key} = #{processed_value},"
                end
              end
              add_line "),"
            end
            
            # Pass shared_data if specified
            if @component['shared_data']
              add_line "// shared_data: #{@component['shared_data'].to_json}"
              # In Compose, shared data would be handled through viewModel or state hoisting
              @component['shared_data'].each do |key, value|
                add_line "// Shared: #{key} = #{value}"
              end
            end
            
            # Apply any modifiers
            if has_modifiers?
              apply_modifiers
            end
          end
          add_line ")"
          
          generated_code
        end
        
        private
        
        def has_modifiers?
          @component['padding'] ||
          @component['width'] ||
          @component['height'] ||
          @component['fillMaxWidth'] ||
          @component['fillMaxHeight'] ||
          @component['background'] ||
          @component['onclick']
        end
      end
    end
  end
end