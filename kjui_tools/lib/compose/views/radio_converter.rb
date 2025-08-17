# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class RadioConverter < BaseViewConverter
        def convert
          if @component['type'] == 'RadioGroup'
            generate_radio_group
          else
            generate_radio_button
          end
          
          generated_code
        end
        
        private
        
        def generate_radio_button
          selected = get_binding_value('selected', false)
          enabled = @component['enabled'] != false
          
          add_line "RadioButton("
          indent do
            # Selected state and onClick
            if is_binding?(@component['selected'])
              property_name = extract_binding_property(@component['selected']).split('.').last
              add_line "selected = #{extract_binding_property(@component['selected'])},"
              add_line "onClick = {"
              indent do
                add_line "currentData.value = currentData.value.copy(#{property_name} = true)"
                
                # Call onClick callback if specified
                if @component['onClick']
                  add_line "viewModel.#{@component['onClick']}()"
                end
              end
              add_line "},"
            else
              add_line "selected = #{selected},"
              if @component['onClick']
                add_line "onClick = { viewModel.#{@component['onClick']}() },"
              else
                add_line "onClick = { },"
              end
            end
            
            # Enabled state
            unless enabled
              add_line "enabled = false,"
            end
            
            # Colors customization
            if has_custom_colors?
              add_line "colors = RadioButtonDefaults.colors("
              indent do
                if @component['selectedColor']
                  color = map_color(@component['selectedColor'])
                  add_line "selectedColor = #{color},"
                end
                
                if @component['unselectedColor']
                  color = map_color(@component['unselectedColor'])
                  add_line "unselectedColor = #{color},"
                end
                
                if @component['disabledSelectedColor']
                  color = map_color(@component['disabledSelectedColor'])
                  add_line "disabledSelectedColor = #{color},"
                end
                
                if @component['disabledUnselectedColor']
                  color = map_color(@component['disabledUnselectedColor'])
                  add_line "disabledUnselectedColor = #{color},"
                end
              end
              add_line "),"
            end
            
            apply_modifiers
          end
          add_line ")"
        end
        
        def generate_radio_group
          selected_value = get_binding_value('selectedValue', '')
          options = @component['options'] || []
          orientation = @component['orientation'] || 'vertical'
          
          # Container based on orientation
          container = orientation == 'horizontal' ? 'Row' : 'Column'
          
          add_line "#{container}("
          indent do
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            if is_binding?(@component['selectedValue'])
              property_name = extract_binding_property(@component['selectedValue']).split('.').last
              
              options.each do |option|
                value = option.is_a?(Hash) ? option['value'] : option
                label = option.is_a?(Hash) ? option['label'] : option
                
                add_line "Row("
                indent do
                  add_line "verticalAlignment = Alignment.CenterVertically,"
                  add_line "modifier = Modifier"
                  add_modifier_line "fillMaxWidth()"
                  add_modifier_line "selectable("
                  indent do
                    add_line "selected = (#{extract_binding_property(@component['selectedValue'])} == #{quote(value)}),"
                    add_line "onClick = {"
                    indent do
                      add_line "currentData.value = currentData.value.copy(#{property_name} = #{quote(value)})"
                      
                      if @component['onSelectionChange']
                        add_line "viewModel.#{@component['onSelectionChange']}(#{quote(value)})"
                      end
                    end
                    add_line "}"
                  end
                  add_modifier_line ")"
                  add_modifier_line "padding(horizontal = 16dp, vertical = 8dp)"
                end
                add_line ") {"
                
                indent do
                  add_line "RadioButton("
                  indent do
                    add_line "selected = (#{extract_binding_property(@component['selectedValue'])} == #{quote(value)}),"
                    add_line "onClick = null // Handled by Row's selectable"
                  end
                  add_line ")"
                  
                  add_line "Spacer(modifier = Modifier.width(8dp))"
                  
                  add_line "Text("
                  indent do
                    add_line "text = #{quote(label)},"
                    add_line "style = MaterialTheme.typography.bodyLarge"
                  end
                  add_line ")"
                end
                
                add_line "}"
              end
            else
              # Static radio group
              options.each do |option|
                value = option.is_a?(Hash) ? option['value'] : option
                label = option.is_a?(Hash) ? option['label'] : option
                
                add_line "Row("
                indent do
                  add_line "verticalAlignment = Alignment.CenterVertically,"
                  add_line "modifier = Modifier.padding(vertical = 4dp)"
                end
                add_line ") {"
                
                indent do
                  add_line "RadioButton("
                  indent do
                    add_line "selected = false,"
                    add_line "onClick = { }"
                  end
                  add_line ")"
                  
                  add_line "Spacer(modifier = Modifier.width(8dp))"
                  
                  add_line "Text(text = #{quote(label)})"
                end
                
                add_line "}"
              end
            end
          end
          
          add_line "}"
        end
        
        def has_custom_colors?
          @component['selectedColor'] ||
          @component['unselectedColor'] ||
          @component['disabledSelectedColor'] ||
          @component['disabledUnselectedColor']
        end
      end
      
      # Alias
      class RadioButtonConverter < RadioConverter; end
      class RadioGroupConverter < RadioConverter; end
    end
  end
end