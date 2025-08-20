# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ScrollViewComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          orientation = json_data['orientation'] || 'vertical'
          
          if orientation == 'horizontal'
            required_imports&.add(:lazy_row)
            code = indent("LazyRow(", depth)
          else
            required_imports&.add(:lazy_column)
            code = indent("LazyColumn(", depth)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?
          
          code += "\n" + indent(") {", depth)
          code += "\n" + indent("item {", depth + 1)
          
          # Process children
          children = json_data['child'] || []
          children = [children] unless children.is_a?(Array)
          
          # Return structure for parent to process children
          { 
            code: code, 
            children: children,
            closing: "\n" + indent("}", depth + 1) + "\n" + indent("}", depth)
          }
        end
        
        private
        
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