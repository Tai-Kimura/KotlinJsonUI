# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class TextFieldConverter < BaseViewConverter
        def convert
          value = get_binding_value('value', '')
          placeholder = @component['placeholder'] || @component['hint'] || ''
          label = @component['label']
          enabled = @component['enabled'] != false
          readonly = @component['readOnly'] || @component['readonly']
          
          # Determine TextField type
          field_type = determine_field_type
          
          add_line "#{field_type}("
          indent do
            # Value and onValueChange
            if is_binding?(@component['value'])
              property_name = extract_binding_property(@component['value']).split('.').last
              add_line "value = #{extract_binding_property(@component['value'])},"
              add_line "onValueChange = { newValue ->"
              indent do
                add_line "currentData.value = currentData.value.copy(#{property_name} = newValue)"
              end
              add_line "},"
            else
              add_line "value = #{quote(value)},"
              add_line "onValueChange = { },"
            end
            
            # Label
            if label
              add_line "label = { Text(#{quote(label)}) },"
            end
            
            # Placeholder
            if placeholder && !label
              add_line "placeholder = { Text(#{quote(placeholder)}) },"
            end
            
            # Leading icon
            if @component['leadingIcon']
              add_line "leadingIcon = {"
              indent do
                generate_icon(@component['leadingIcon'])
              end
              add_line "},"
            end
            
            # Trailing icon
            if @component['trailingIcon']
              add_line "trailingIcon = {"
              indent do
                generate_icon(@component['trailingIcon'])
              end
              add_line "},"
            end
            
            # Supporting text (helper text)
            if @component['helperText'] || @component['supportingText']
              text = @component['helperText'] || @component['supportingText']
              add_line "supportingText = { Text(#{quote(text)}) },"
            end
            
            # Error state
            if @component['isError'] || @component['error']
              add_line "isError = true,"
              
              if @component['errorMessage']
                add_line "supportingText = { Text(#{quote(@component['errorMessage'])}) },"
              end
            end
            
            # Visual transformation (password, phone, etc.)
            if @component['inputType']
              transformation = map_visual_transformation(@component['inputType'])
              if transformation
                add_line "visualTransformation = #{transformation},"
              end
            end
            
            # Keyboard options
            keyboard_options = build_keyboard_options
            if keyboard_options
              add_line "keyboardOptions = #{keyboard_options},"
            end
            
            # Keyboard actions
            if @component['onDone'] || @component['onNext'] || @component['onSearch']
              add_line "keyboardActions = KeyboardActions("
              indent do
                add_line "onDone = { viewModel.#{@component['onDone']}() }," if @component['onDone']
                add_line "onNext = { viewModel.#{@component['onNext']}() }," if @component['onNext']
                add_line "onSearch = { viewModel.#{@component['onSearch']}() }," if @component['onSearch']
              end
              add_line "),"
            end
            
            # Single line
            if @component['singleLine'] != false
              add_line "singleLine = true,"
            else
              add_line "singleLine = false,"
              
              # Max lines for multiline
              if @component['maxLines']
                add_line "maxLines = #{@component['maxLines']},"
              end
              
              # Min lines
              if @component['minLines']
                add_line "minLines = #{@component['minLines']},"
              end
            end
            
            # Enabled state
            unless enabled
              add_line "enabled = false,"
            end
            
            # Read only
            if readonly
              add_line "readOnly = true,"
            end
            
            # Text style
            if @component['textStyle']
              add_line "textStyle = #{map_text_style(@component['textStyle'])},"
            end
            
            # Colors
            if has_custom_colors?
              generate_custom_colors
            end
            
            # Shape
            if @component['shape'] || @component['cornerRadius']
              if @component['cornerRadius']
                add_line "shape = RoundedCornerShape(#{@component['cornerRadius']}dp),"
              elsif @component['shape'] == 'rounded'
                add_line "shape = RoundedCornerShape(4dp),"
              end
            end
            
            apply_modifiers
          end
          add_line ")"
          
          generated_code
        end
        
        private
        
        def determine_field_type
          style = @component['textFieldStyle'] || @component['style']
          
          case style
          when 'outlined', 'outline'
            'OutlinedTextField'
          when 'filled'
            'TextField'
          else
            # Default to OutlinedTextField
            'OutlinedTextField'
          end
        end
        
        def generate_icon(icon_name)
          add_line "Icon("
          indent do
            if icon_name.start_with?('ic_') || icon_name.start_with?('icon_')
              add_line "painter = painterResource(id = R.drawable.#{icon_name}),"
            else
              add_line "imageVector = Icons.Default.#{to_pascal_case(icon_name)},"
            end
            add_line "contentDescription = null"
          end
          add_line ")"
        end
        
        def map_visual_transformation(input_type)
          case input_type.to_s.downcase
          when 'password', 'textpassword'
            'PasswordVisualTransformation()'
          when 'phone'
            '// Phone transformation not implemented'
          when 'creditcard', 'credit_card'
            '// Credit card transformation not implemented'
          else
            nil
          end
        end
        
        def build_keyboard_options
          return nil unless @component['inputType'] || @component['keyboardType'] || @component['imeAction']
          
          options = []
          
          # Keyboard type
          if @component['inputType'] || @component['keyboardType']
            type = map_keyboard_type(@component['inputType'] || @component['keyboardType'])
            options << "keyboardType = KeyboardType.#{type}" if type
          end
          
          # IME action
          if @component['imeAction']
            action = map_ime_action(@component['imeAction'])
            options << "imeAction = ImeAction.#{action}" if action
          end
          
          # Capitalization
          if @component['capitalization']
            cap = map_capitalization(@component['capitalization'])
            options << "capitalization = KeyboardCapitalization.#{cap}" if cap
          end
          
          # Auto correct
          if @component['autoCorrect'] == false
            options << "autoCorrect = false"
          end
          
          return nil if options.empty?
          
          "KeyboardOptions(\n" + options.map { |opt| "        #{opt}" }.join(",\n") + "\n    )"
        end
        
        def map_keyboard_type(type)
          case type.to_s.downcase
          when 'text', 'textmultiline'
            'Text'
          when 'number', 'numberdecimal'
            'Number'
          when 'phone'
            'Phone'
          when 'email', 'textemailaddress'
            'Email'
          when 'password', 'textpassword'
            'Password'
          when 'numberpassword'
            'NumberPassword'
          when 'uri', 'texturi'
            'Uri'
          when 'decimal'
            'Decimal'
          else
            'Text'
          end
        end
        
        def map_ime_action(action)
          case action.to_s.downcase
          when 'done'
            'Done'
          when 'go'
            'Go'
          when 'next'
            'Next'
          when 'previous'
            'Previous'
          when 'search'
            'Search'
          when 'send'
            'Send'
          when 'none'
            'None'
          else
            'Default'
          end
        end
        
        def map_capitalization(cap)
          case cap.to_s.downcase
          when 'none'
            'None'
          when 'characters'
            'Characters'
          when 'words'
            'Words'
          when 'sentences'
            'Sentences'
          else
            'None'
          end
        end
        
        def map_text_style(style)
          case style.to_s.downcase
          when 'body1', 'body'
            'MaterialTheme.typography.bodyLarge'
          when 'body2'
            'MaterialTheme.typography.bodyMedium'
          when 'headline', 'h1'
            'MaterialTheme.typography.headlineLarge'
          when 'h2'
            'MaterialTheme.typography.headlineMedium'
          when 'h3'
            'MaterialTheme.typography.headlineSmall'
          when 'subtitle1'
            'MaterialTheme.typography.titleLarge'
          when 'subtitle2'
            'MaterialTheme.typography.titleMedium'
          when 'caption'
            'MaterialTheme.typography.labelSmall'
          else
            'TextStyle.Default'
          end
        end
        
        def has_custom_colors?
          @component['textColor'] || 
          @component['backgroundColor'] || 
          @component['cursorColor'] ||
          @component['focusedBorderColor'] ||
          @component['unfocusedBorderColor'] ||
          @component['errorBorderColor']
        end
        
        def generate_custom_colors
          field_type = determine_field_type
          colors_function = field_type == 'OutlinedTextField' ? 'outlinedTextFieldColors' : 'textFieldColors'
          
          add_line "colors = TextFieldDefaults.#{colors_function}("
          indent do
            if @component['textColor']
              color = map_color(@component['textColor'])
              add_line "textColor = #{color},"
            end
            
            if @component['backgroundColor']
              color = map_color(@component['backgroundColor'])
              add_line "containerColor = #{color},"
            end
            
            if @component['cursorColor']
              color = map_color(@component['cursorColor'])
              add_line "cursorColor = #{color},"
            end
            
            if @component['focusedBorderColor']
              color = map_color(@component['focusedBorderColor'])
              add_line "focusedBorderColor = #{color},"
            end
            
            if @component['unfocusedBorderColor']
              color = map_color(@component['unfocusedBorderColor'])
              add_line "unfocusedBorderColor = #{color},"
            end
            
            if @component['errorBorderColor']
              color = map_color(@component['errorBorderColor'])
              add_line "errorBorderColor = #{color},"
            end
          end
          add_line "),"
        end
      end
      
      # Alias for EditText
      class EditTextConverter < TextFieldConverter; end
    end
  end
end