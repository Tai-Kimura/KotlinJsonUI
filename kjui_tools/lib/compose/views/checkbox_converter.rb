# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class CheckboxConverter < BaseViewConverter
        def convert
          checked = get_binding_value('checked', false)
          enabled = @component['enabled'] != false
          
          add_line "Checkbox("
          indent do
            # Checked state and onCheckedChange
            if is_binding?(@component['checked'])
              property_name = extract_binding_property(@component['checked']).split('.').last
              add_line "checked = #{extract_binding_property(@component['checked'])},"
              add_line "onCheckedChange = { isChecked ->"
              indent do
                add_line "currentData.value = currentData.value.copy(#{property_name} = isChecked)"
                
                # Call onChange callback if specified
                if @component['onChange']
                  add_line "viewModel.#{@component['onChange']}(isChecked)"
                end
              end
              add_line "},"
            else
              add_line "checked = #{checked},"
              if @component['onChange']
                add_line "onCheckedChange = { isChecked -> viewModel.#{@component['onChange']}(isChecked) },"
              else
                add_line "onCheckedChange = { },"
              end
            end
            
            # Enabled state
            unless enabled
              add_line "enabled = false,"
            end
            
            # Colors customization
            if has_custom_colors?
              add_line "colors = CheckboxDefaults.colors("
              indent do
                if @component['checkedColor']
                  color = map_color(@component['checkedColor'])
                  add_line "checkedColor = #{color},"
                end
                
                if @component['uncheckedColor']
                  color = map_color(@component['uncheckedColor'])
                  add_line "uncheckedColor = #{color},"
                end
                
                if @component['checkmarkColor']
                  color = map_color(@component['checkmarkColor'])
                  add_line "checkmarkColor = #{color},"
                end
                
                if @component['disabledCheckedColor']
                  color = map_color(@component['disabledCheckedColor'])
                  add_line "disabledCheckedColor = #{color},"
                end
                
                if @component['disabledUncheckedColor']
                  color = map_color(@component['disabledUncheckedColor'])
                  add_line "disabledUncheckedColor = #{color},"
                end
                
                if @component['disabledIndeterminateColor']
                  color = map_color(@component['disabledIndeterminateColor'])
                  add_line "disabledIndeterminateColor = #{color},"
                end
              end
              add_line "),"
            end
            
            apply_modifiers
          end
          add_line ")"
          
          generated_code
        end
        
        private
        
        def has_custom_colors?
          @component['checkedColor'] ||
          @component['uncheckedColor'] ||
          @component['checkmarkColor'] ||
          @component['disabledCheckedColor'] ||
          @component['disabledUncheckedColor'] ||
          @component['disabledIndeterminateColor']
        end
      end
    end
  end
end