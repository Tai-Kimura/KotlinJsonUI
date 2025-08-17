# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class SliderConverter < BaseViewConverter
        def convert
          value = get_binding_value('value', 0.5)
          min_value = @component['min'] || @component['minimumValue'] || 0.0
          max_value = @component['max'] || @component['maximumValue'] || 1.0
          steps = @component['steps'] || 0
          enabled = @component['enabled'] != false
          
          add_line "Slider("
          indent do
            # Value and onValueChange
            if is_binding?(@component['value'])
              property_name = extract_binding_property(@component['value']).split('.').last
              add_line "value = #{extract_binding_property(@component['value'])}.toFloat(),"
              add_line "onValueChange = { newValue ->"
              indent do
                add_line "currentData.value = currentData.value.copy(#{property_name} = newValue)"
                
                # Call onValueChange callback if specified
                if @component['onValueChange']
                  add_line "viewModel.#{@component['onValueChange']}(newValue)"
                end
              end
              add_line "},"
            else
              add_line "value = #{value}f,"
              if @component['onValueChange']
                add_line "onValueChange = { newValue -> viewModel.#{@component['onValueChange']}(newValue) },"
              else
                add_line "onValueChange = { },"
              end
            end
            
            # Value range
            add_line "valueRange = #{min_value}f..#{max_value}f,"
            
            # Steps
            if steps > 0
              add_line "steps = #{steps},"
            end
            
            # Enabled state
            unless enabled
              add_line "enabled = false,"
            end
            
            # onValueChangeFinished callback
            if @component['onValueChangeFinished']
              add_line "onValueChangeFinished = {"
              indent do
                add_line "viewModel.#{@component['onValueChangeFinished']}()"
              end
              add_line "},"
            end
            
            # Colors customization
            if has_custom_colors?
              add_line "colors = SliderDefaults.colors("
              indent do
                if @component['thumbColor']
                  color = map_color(@component['thumbColor'])
                  add_line "thumbColor = #{color},"
                end
                
                if @component['activeTrackColor']
                  color = map_color(@component['activeTrackColor'])
                  add_line "activeTrackColor = #{color},"
                end
                
                if @component['activeTickColor']
                  color = map_color(@component['activeTickColor'])
                  add_line "activeTickColor = #{color},"
                end
                
                if @component['inactiveTrackColor']
                  color = map_color(@component['inactiveTrackColor'])
                  add_line "inactiveTrackColor = #{color},"
                end
                
                if @component['inactiveTickColor']
                  color = map_color(@component['inactiveTickColor'])
                  add_line "inactiveTickColor = #{color},"
                end
                
                if @component['disabledThumbColor']
                  color = map_color(@component['disabledThumbColor'])
                  add_line "disabledThumbColor = #{color},"
                end
                
                if @component['disabledActiveTrackColor']
                  color = map_color(@component['disabledActiveTrackColor'])
                  add_line "disabledActiveTrackColor = #{color},"
                end
                
                if @component['disabledActiveTickColor']
                  color = map_color(@component['disabledActiveTickColor'])
                  add_line "disabledActiveTickColor = #{color},"
                end
                
                if @component['disabledInactiveTrackColor']
                  color = map_color(@component['disabledInactiveTrackColor'])
                  add_line "disabledInactiveTrackColor = #{color},"
                end
                
                if @component['disabledInactiveTickColor']
                  color = map_color(@component['disabledInactiveTickColor'])
                  add_line "disabledInactiveTickColor = #{color},"
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
          @component['thumbColor'] ||
          @component['activeTrackColor'] ||
          @component['activeTickColor'] ||
          @component['inactiveTrackColor'] ||
          @component['inactiveTickColor'] ||
          @component['disabledThumbColor'] ||
          @component['disabledActiveTrackColor'] ||
          @component['disabledActiveTickColor'] ||
          @component['disabledInactiveTrackColor'] ||
          @component['disabledInactiveTickColor']
        end
      end
    end
  end
end