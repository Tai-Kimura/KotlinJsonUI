# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class SelectBoxComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:selectbox_component)
          
          # Check if this is a date picker
          is_date_picker = json_data['selectItemType'] == 'Date'
          
          # SelectBox uses 'selectedItem' or 'bind' for selected value
          selected = if json_data['selectedItem'] && json_data['selectedItem'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            '""'
          end
          
          # Use DateSelectBox for date type
          if is_date_picker
            required_imports&.add(:date_selectbox_component)
            code = indent("DateSelectBox(", depth)
          else
            code = indent("SelectBox(", depth)
          end
          code += "\n" + indent("value = #{selected},", depth + 1)
          
          # Handle onValueChange callback
          if json_data['selectedItem'] && json_data['selectedItem'].match(/@\{([^}]+)\}/)
            variable = $1
            code += "\n" + indent("onValueChange = { newValue ->", depth + 1)
            code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to newValue))", depth + 2)
            code += "\n" + indent("},", depth + 1)
          elsif json_data['bind'] && json_data['bind'].match(/@\{([^}]+)\}/)
            variable = $1
            code += "\n" + indent("onValueChange = { newValue ->", depth + 1)
            code += "\n" + indent("viewModel.updateData(mapOf(\"#{variable}\" to newValue))", depth + 2)
            code += "\n" + indent("},", depth + 1)
          else
            code += "\n" + indent("onValueChange = { },", depth + 1)
          end
          
          # For date picker, add date-specific parameters
          if is_date_picker
            # Date picker mode (date, time, dateAndTime)
            if json_data['datePickerMode']
              code += "\n" + indent("datePickerMode = \"#{json_data['datePickerMode']}\",", depth + 1)
            end
            
            # Date picker style
            if json_data['datePickerStyle']
              code += "\n" + indent("datePickerStyle = \"#{json_data['datePickerStyle']}\",", depth + 1)
            end
            
            # Date format (or dateStringFormat)
            date_format = json_data['dateFormat'] || json_data['dateStringFormat']
            if date_format
              code += "\n" + indent("dateFormat = \"#{date_format}\",", depth + 1)
            end
            
            # Minute interval for time pickers
            if json_data['minuteInterval']
              code += "\n" + indent("minuteInterval = #{json_data['minuteInterval']},", depth + 1)
            end
            
            # Minimum date
            if json_data['minimumDate']
              code += "\n" + indent("minimumDate = \"#{json_data['minimumDate']}\",", depth + 1)
            end
            
            # Maximum date
            if json_data['maximumDate']
              code += "\n" + indent("maximumDate = \"#{json_data['maximumDate']}\",", depth + 1)
            end
          else
            # Options (use 'items' or 'options') - only for non-date SelectBox
            options_data = json_data['items'] || json_data['options']
            if options_data
            if options_data.is_a?(String) && options_data.match(/@\{([^}]+)\}/)
              # Dynamic options from data binding
              options_var = $1
              code += "\n" + indent("options = data.#{options_var},", depth + 1)
            elsif options_data.is_a?(Array)
              # Static options array
              options_list = options_data.map do |option|
                if option.is_a?(Hash)
                  "\"#{option['label'] || option['value']}\""
                else
                  "\"#{option}\""
                end
              end.join(", ")
              code += "\n" + indent("options = listOf(#{options_list}),", depth + 1)
            else
              code += "\n" + indent("options = emptyList(),", depth + 1)
            end
            else
              code += "\n" + indent("options = emptyList(),", depth + 1)
            end
          end
          
          # Add placeholder/hint if specified
          if json_data['hint']
            code += "\n" + indent("placeholder = \"#{json_data['hint']}\",", depth + 1)
          elsif json_data['placeholder']
            code += "\n" + indent("placeholder = \"#{json_data['placeholder']}\",", depth + 1)
          end
          
          # Add enabled state if specified
          if json_data['disabled']
            code += "\n" + indent("enabled = false,", depth + 1)
          elsif json_data['enabled'] == false
            code += "\n" + indent("enabled = false,", depth + 1)
          end
          
          # Add style parameters
          if json_data['background']
            bg_color = json_data['background']
            if bg_color.start_with?('#')
              code += "\n" + indent("backgroundColor = Color(android.graphics.Color.parseColor(\"#{bg_color}\")),", depth + 1)
            else
              code += "\n" + indent("backgroundColor = Color.#{bg_color},", depth + 1)
            end
          end
          
          if json_data['borderColor']
            border_color = json_data['borderColor']
            if border_color.start_with?('#')
              code += "\n" + indent("borderColor = Color(android.graphics.Color.parseColor(\"#{border_color}\")),", depth + 1)
            else
              code += "\n" + indent("borderColor = Color.#{border_color},", depth + 1)
            end
          end
          
          if json_data['fontColor']
            text_color = json_data['fontColor']
            if text_color.start_with?('#')
              code += "\n" + indent("textColor = Color(android.graphics.Color.parseColor(\"#{text_color}\")),", depth + 1)
            else
              code += "\n" + indent("textColor = Color.#{text_color},", depth + 1)
            end
          end
          
          if json_data['hintColor']
            hint_color = json_data['hintColor']
            if hint_color.start_with?('#')
              code += "\n" + indent("hintColor = Color(android.graphics.Color.parseColor(\"#{hint_color}\")),", depth + 1)
            else
              code += "\n" + indent("hintColor = Color.#{hint_color},", depth + 1)
            end
          end
          
          if json_data['cornerRadius']
            code += "\n" + indent("cornerRadius = #{json_data['cornerRadius']},", depth + 1)
          end
          
          # Add cancel button background color if specified
          if json_data['cancelButtonBackgroundColor']
            cancel_bg = json_data['cancelButtonBackgroundColor']
            if cancel_bg.start_with?('#')
              code += "\n" + indent("cancelButtonBackgroundColor = Color(android.graphics.Color.parseColor(\"#{cancel_bg}\")),", depth + 1)
            else
              code += "\n" + indent("cancelButtonBackgroundColor = Color.#{cancel_bg},", depth + 1)
            end
          end
          
          # Add cancel button text color if specified
          if json_data['cancelButtonTextColor']
            cancel_text = json_data['cancelButtonTextColor']
            if cancel_text.start_with?('#')
              code += "\n" + indent("cancelButtonTextColor = Color(android.graphics.Color.parseColor(\"#{cancel_text}\")),", depth + 1)
            else
              code += "\n" + indent("cancelButtonTextColor = Color.#{cancel_text},", depth + 1)
            end
          end
          
          # Build modifiers
          modifiers = []
          
          # Ensure fillMaxWidth if width is not specified for date pickers
          if is_date_picker && !json_data['width']
            modifiers << ".fillMaxWidth()"
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          
          if modifiers.any? && !modifiers.include?('SKIP_RENDER')
            code += Helpers::ModifierBuilder.format(modifiers, depth)
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