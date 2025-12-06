# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      # SwitchComponent handles both Switch (primary) and Toggle (alias) component types
      # Switch is the primary component name. Toggle is supported as an alias for backward compatibility.
      class SwitchComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Switch/Toggle uses 'isOn' or 'bind' for binding
          checked = if json_data['isOn']
            if json_data['isOn'].is_a?(String) && json_data['isOn'].match(/@\{([^}]+)\}/)
              variable = $1
              "data.#{variable}"
            else
              # Direct boolean value
              json_data['isOn'].to_s
            end
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            'false'
          end

          code = indent("Switch(", depth)
          code += "\n" + indent("checked = #{checked},", depth + 1)

          # onCheckedChange handler
          binding_variable = nil
          if json_data['isOn'] && json_data['isOn'].is_a?(String) && json_data['isOn'].match(/@\{([^}]+)\}/)
            binding_variable = $1
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            binding_variable = $1
          end

          if json_data['onValueChange']
            # Use custom handler if specified
            code += "\n" + indent("onCheckedChange = { viewModel.#{json_data['onValueChange']}(it) },", depth + 1)
          elsif binding_variable
            # Update the bound variable
            code += "\n" + indent("onCheckedChange = { newValue -> viewModel.updateData(mapOf(\"#{binding_variable}\" to newValue)) },", depth + 1)
          else
            code += "\n" + indent("onCheckedChange = { },", depth + 1)
          end

          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))

          # Add weight modifier if in Row or Column
          if parent_type == 'Row' || parent_type == 'Column'
            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          end

          code += Helpers::ModifierBuilder.format(modifiers, depth) if modifiers.any?

          # Switch colors
          if json_data['onTintColor'] || json_data['thumbTintColor']
            required_imports&.add(:switch_colors)
            colors_params = []

            if json_data['onTintColor']
              checkedtrackcolor_resolved = Helpers::ResourceResolver.process_color(json_data['onTintColor'], required_imports)
              colors_params << "checkedTrackColor = #{checkedtrackcolor_resolved}"
            end

            if json_data['thumbTintColor']
              checkedthumbcolor_resolved = Helpers::ResourceResolver.process_color(json_data['thumbTintColor'], required_imports)
              colors_params << "checkedThumbColor = #{checkedthumbcolor_resolved}"
            end

            if colors_params.any?
              code += ",\n" + indent("colors = SwitchDefaults.colors(", depth + 1)
              code += "\n" + colors_params.map { |param| indent(param, depth + 2) }.join(",\n")
              code += "\n" + indent(")", depth + 1)
            end
          end

          # Handle enabled attribute
          if json_data.key?('enabled')
            if json_data['enabled'].is_a?(String) && json_data['enabled'].start_with?('@{')
              variable = json_data['enabled'].match(/@\{([^}]+)\}/)[1]
              code += ",\n" + indent("enabled = data.#{variable}", depth + 1)
            else
              code += ",\n" + indent("enabled = #{json_data['enabled']}", depth + 1)
            end
          end

          code += "\n" + indent(")", depth)
          code
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
