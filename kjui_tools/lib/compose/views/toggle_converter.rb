# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ToggleConverter < BaseViewConverter
        def convert
          checked = get_binding_value('checked', false)
          enabled = @component['enabled'] != false
          
          add_line "Switch("
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
            
            # Thumb content (icon inside the switch thumb)
            if @component['thumbContent'] || @component['thumbIcon']
              add_line "thumbContent = {"
              indent do
                icon = @component['thumbContent'] || @component['thumbIcon']
                if icon.is_a?(String)
                  add_line "Icon("
                  indent do
                    add_line "imageVector = Icons.Default.#{to_pascal_case(icon)},"
                    add_line "contentDescription = null,"
                    add_line "modifier = Modifier.size(SwitchDefaults.IconSize)"
                  end
                  add_line ")"
                end
              end
              add_line "},"
            end
            
            # Colors customization
            if has_custom_colors?
              add_line "colors = SwitchDefaults.colors("
              indent do
                if @component['checkedThumbColor']
                  color = map_color(@component['checkedThumbColor'])
                  add_line "checkedThumbColor = #{color},"
                end
                
                if @component['checkedTrackColor']
                  color = map_color(@component['checkedTrackColor'])
                  add_line "checkedTrackColor = #{color},"
                end
                
                if @component['checkedBorderColor']
                  color = map_color(@component['checkedBorderColor'])
                  add_line "checkedBorderColor = #{color},"
                end
                
                if @component['checkedIconColor']
                  color = map_color(@component['checkedIconColor'])
                  add_line "checkedIconColor = #{color},"
                end
                
                if @component['uncheckedThumbColor']
                  color = map_color(@component['uncheckedThumbColor'])
                  add_line "uncheckedThumbColor = #{color},"
                end
                
                if @component['uncheckedTrackColor']
                  color = map_color(@component['uncheckedTrackColor'])
                  add_line "uncheckedTrackColor = #{color},"
                end
                
                if @component['uncheckedBorderColor']
                  color = map_color(@component['uncheckedBorderColor'])
                  add_line "uncheckedBorderColor = #{color},"
                end
                
                if @component['uncheckedIconColor']
                  color = map_color(@component['uncheckedIconColor'])
                  add_line "uncheckedIconColor = #{color},"
                end
                
                if @component['disabledCheckedThumbColor']
                  color = map_color(@component['disabledCheckedThumbColor'])
                  add_line "disabledCheckedThumbColor = #{color},"
                end
                
                if @component['disabledCheckedTrackColor']
                  color = map_color(@component['disabledCheckedTrackColor'])
                  add_line "disabledCheckedTrackColor = #{color},"
                end
                
                if @component['disabledUncheckedThumbColor']
                  color = map_color(@component['disabledUncheckedThumbColor'])
                  add_line "disabledUncheckedThumbColor = #{color},"
                end
                
                if @component['disabledUncheckedTrackColor']
                  color = map_color(@component['disabledUncheckedTrackColor'])
                  add_line "disabledUncheckedTrackColor = #{color},"
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
          @component['checkedThumbColor'] ||
          @component['checkedTrackColor'] ||
          @component['checkedBorderColor'] ||
          @component['checkedIconColor'] ||
          @component['uncheckedThumbColor'] ||
          @component['uncheckedTrackColor'] ||
          @component['uncheckedBorderColor'] ||
          @component['uncheckedIconColor'] ||
          @component['disabledCheckedThumbColor'] ||
          @component['disabledCheckedTrackColor'] ||
          @component['disabledUncheckedThumbColor'] ||
          @component['disabledUncheckedTrackColor']
        end
      end
      
      # Alias for Switch
      class SwitchConverter < ToggleConverter; end
    end
  end
end