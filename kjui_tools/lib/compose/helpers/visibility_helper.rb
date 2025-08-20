# frozen_string_literal: true

module KjuiTools
  module Compose
    module Helpers
      class VisibilityHelper
        def self.wrap_with_visibility(json_data, component_code, depth, required_imports)
          visibility_result = ModifierBuilder.build_visibility(json_data, required_imports)
          visibility_info = visibility_result[:visibility_info]
          
          # If no visibility attributes, return the component as-is
          return component_code if visibility_info.empty?
          
          # Build VisibilityWrapper
          wrapper_code = indent("VisibilityWrapper(", depth)
          
          # Add visibility parameters
          if visibility_info[:visibility_binding]
            wrapper_code += "\n" + indent("visibility = #{visibility_info[:visibility_binding]},", depth + 1)
          elsif visibility_info[:visibility]
            wrapper_code += "\n" + indent("visibility = \"#{visibility_info[:visibility]}\",", depth + 1)
          end
          
          if visibility_info[:hidden_binding]
            wrapper_code += "\n" + indent("hidden = #{visibility_info[:hidden_binding]},", depth + 1)
          elsif visibility_info[:hidden]
            wrapper_code += "\n" + indent("hidden = true,", depth + 1)
          end
          
          wrapper_code += "\n" + indent(") {", depth)
          wrapper_code += "\n" + component_code
          wrapper_code += "\n" + indent("}", depth)
          
          wrapper_code
        end
        
        def self.should_skip_render?(json_data)
          # Check if component should not be rendered at all (static gone/hidden)
          return true if json_data['visibility'] == 'gone' && !json_data['visibility'].to_s.include?('@{')
          return true if json_data['hidden'] == true && !json_data['hidden'].to_s.include?('@{')
          false
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