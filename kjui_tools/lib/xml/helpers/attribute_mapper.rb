#!/usr/bin/env ruby

module XmlGenerator
  class AttributeMapper
    def initialize
      @attribute_map = create_attribute_map
    end

    def map_dimension(value)
      case value
      when 'matchParent', 'match_parent'
        'match_parent'
      when 'wrapContent', 'wrap_content'
        'wrap_content'
      when /^\d+$/
        "#{value}dp"
      when /^\d+dp$/
        value
      when /^\d+%$/
        "0dp" # Will need layout_weight
      else
        value
      end
    end

    def map_attribute(key, value, component_type)
      # Special handling for certain attributes
      case key
      # Layout attributes
      when 'padding'
        return { namespace: 'android', name: 'padding', value: convert_dimension(value) }
      when 'topPadding', 'paddingTop'
        return { namespace: 'android', name: 'paddingTop', value: convert_dimension(value) }
      when 'bottomPadding', 'paddingBottom'
        return { namespace: 'android', name: 'paddingBottom', value: convert_dimension(value) }
      when 'leftPadding', 'paddingLeft', 'startPadding', 'paddingStart'
        return { namespace: 'android', name: 'paddingStart', value: convert_dimension(value) }
      when 'rightPadding', 'paddingRight', 'endPadding', 'paddingEnd'
        return { namespace: 'android', name: 'paddingEnd', value: convert_dimension(value) }
        
      when 'margin'
        return { namespace: 'android', name: 'layout_margin', value: convert_dimension(value) }
      when 'topMargin', 'marginTop'
        return { namespace: 'android', name: 'layout_marginTop', value: convert_dimension(value) }
      when 'bottomMargin', 'marginBottom'
        return { namespace: 'android', name: 'layout_marginBottom', value: convert_dimension(value) }
      when 'leftMargin', 'marginLeft', 'startMargin', 'marginStart'
        return { namespace: 'android', name: 'layout_marginStart', value: convert_dimension(value) }
      when 'rightMargin', 'marginRight', 'endMargin', 'marginEnd'
        return { namespace: 'android', name: 'layout_marginEnd', value: convert_dimension(value) }
        
      # Text attributes
      when 'text'
        return { namespace: 'android', name: 'text', value: process_text_value(value) }
      when 'hint'
        return { namespace: 'android', name: 'hint', value: value }
      when 'fontSize', 'textSize'
        return { namespace: 'android', name: 'textSize', value: convert_text_size(value) }
      when 'fontColor', 'textColor'
        return { namespace: 'android', name: 'textColor', value: convert_color(value) }
      when 'fontWeight'
        return map_font_weight(value)
      when 'fontStyle'
        return { namespace: 'android', name: 'textStyle', value: value }
      when 'textAlign', 'textAlignment'
        return { namespace: 'android', name: 'textAlignment', value: map_text_alignment(value) }
      when 'maxLines'
        return { namespace: 'android', name: 'maxLines', value: value.to_s }
      when 'ellipsize'
        return { namespace: 'android', name: 'ellipsize', value: value }
        
      # Background and appearance
      when 'background', 'backgroundColor'
        return { namespace: 'android', name: 'background', value: convert_color(value) }
      when 'cornerRadius'
        return nil # Will need to create drawable
      when 'opacity', 'alpha'
        return { namespace: 'android', name: 'alpha', value: value.to_f.to_s }
      when 'visibility'
        return { namespace: 'android', name: 'visibility', value: map_visibility(value) }
      when 'enabled'
        return { namespace: 'android', name: 'enabled', value: value.to_s }
      when 'clickable'
        return { namespace: 'android', name: 'clickable', value: value.to_s }
      when 'focusable'
        return { namespace: 'android', name: 'focusable', value: value.to_s }
        
      # Image attributes
      when 'src', 'source', 'image'
        return map_image_source(value)
      when 'scaleType'
        return { namespace: 'android', name: 'scaleType', value: map_scale_type(value) }
      when 'tint'
        return { namespace: 'android', name: 'tint', value: convert_color(value) }
        
      # Layout specific
      when 'orientation'
        return { namespace: 'android', name: 'orientation', value: value }
      when 'weight'
        return { namespace: 'android', name: 'layout_weight', value: value.to_s }
      when 'gravity'
        return { namespace: 'android', name: 'gravity', value: map_gravity(value) }
      when 'layout_gravity'
        return { namespace: 'android', name: 'layout_gravity', value: map_gravity(value) }
        
      # Events (will be handled in binding)
      when 'onClick', 'onclick'
        return { namespace: 'android', name: 'onClick', value: value }
      when 'onTextChanged'
        return nil # Handled in code
        
      # Input attributes
      when 'inputType'
        return { namespace: 'android', name: 'inputType', value: map_input_type(value) }
      when 'placeholder'
        return { namespace: 'android', name: 'hint', value: value }
      when 'secure', 'password'
        return { namespace: 'android', name: 'inputType', value: 'textPassword' }
        
      # Switch/Checkbox
      when 'checked', 'isChecked'
        return { namespace: 'android', name: 'checked', value: value.to_s }
        
      # Progress/Slider
      when 'progress'
        return { namespace: 'android', name: 'progress', value: value.to_s }
      when 'max', 'maxValue'
        return { namespace: 'android', name: 'max', value: value.to_s }
      when 'min', 'minValue'
        return { namespace: 'android', name: 'min', value: value.to_s }
        
      # ConstraintLayout attributes
      when /^constraint/
        return map_constraint_attribute(key, value)
        
      else
        # Check if it's in the standard map
        if @attribute_map[key]
          mapped = @attribute_map[key]
          return { 
            namespace: mapped[:namespace] || 'android', 
            name: mapped[:name], 
            value: convert_value(value, mapped[:type])
          }
        end
      end
      
      nil
    end

    private

    def create_attribute_map
      {
        # Additional mappings not covered in case statement
        'contentDescription' => { name: 'contentDescription', type: 'string' },
        'tag' => { name: 'tag', type: 'string' },
        'transitionName' => { name: 'transitionName', type: 'string' },
        'elevation' => { name: 'elevation', type: 'dimension' },
        'translationZ' => { name: 'translationZ', type: 'dimension' },
        'rotation' => { name: 'rotation', type: 'float' },
        'rotationX' => { name: 'rotationX', type: 'float' },
        'rotationY' => { name: 'rotationY', type: 'float' },
        'scaleX' => { name: 'scaleX', type: 'float' },
        'scaleY' => { name: 'scaleY', type: 'float' },
        'pivotX' => { name: 'pivotX', type: 'dimension' },
        'pivotY' => { name: 'pivotY', type: 'dimension' }
      }
    end

    def convert_dimension(value)
      return value if value.is_a?(String) && value.match?(/^\d+(dp|sp|px|dip)$/)
      return "#{value}dp" if value.is_a?(Numeric) || value.match?(/^\d+$/)
      value
    end

    def convert_text_size(value)
      return value if value.is_a?(String) && value.match?(/^\d+(sp|dp|px)$/)
      return "#{value}sp" if value.is_a?(Numeric) || value.match?(/^\d+$/)
      value
    end

    def convert_color(value)
      return value if value.start_with?('@color/', '#', '@android:color/')
      
      # Convert color names to hex
      colors = {
        'black' => '#000000',
        'white' => '#FFFFFF',
        'red' => '#FF0000',
        'green' => '#00FF00',
        'blue' => '#0000FF',
        'yellow' => '#FFFF00',
        'cyan' => '#00FFFF',
        'magenta' => '#FF00FF',
        'gray' => '#808080',
        'grey' => '#808080',
        'transparent' => '#00000000'
      }
      
      colors[value.downcase] || value
    end

    def process_text_value(value)
      # Handle data binding
      return value if value.start_with?('@{')
      
      # Handle string resources
      return value if value.start_with?('@string/')
      
      # Regular text
      value
    end

    def map_font_weight(value)
      case value.to_s.downcase
      when 'bold', '700', '800', '900'
        { namespace: 'android', name: 'textStyle', value: 'bold' }
      when 'normal', '400'
        { namespace: 'android', name: 'textStyle', value: 'normal' }
      else
        nil
      end
    end

    def map_text_alignment(value)
      case value.to_s.downcase
      when 'left', 'start'
        'textStart'
      when 'right', 'end'
        'textEnd'
      when 'center'
        'center'
      when 'justify'
        'viewStart'
      else
        value
      end
    end

    def map_visibility(value)
      case value.to_s.downcase
      when 'visible', 'true'
        'visible'
      when 'invisible', 'hidden'
        'invisible'
      when 'gone', 'false'
        'gone'
      else
        value
      end
    end

    def map_image_source(value)
      if value.start_with?('http://', 'https://')
        # Network image - will need special handling
        { namespace: 'app', name: 'imageUrl', value: value }
      elsif value.start_with?('@drawable/')
        { namespace: 'android', name: 'src', value: value }
      else
        # Assume it's a drawable resource name
        { namespace: 'android', name: 'src', value: "@drawable/#{value}" }
      end
    end

    def map_scale_type(value)
      scale_types = {
        'fill' => 'fitXY',
        'fit' => 'fitCenter',
        'aspectFit' => 'fitCenter',
        'aspectFill' => 'centerCrop',
        'center' => 'center',
        'centerCrop' => 'centerCrop',
        'centerInside' => 'centerInside'
      }
      
      scale_types[value] || value
    end

    def map_gravity(value)
      gravity_map = {
        'left' => 'start',
        'right' => 'end',
        'top' => 'top',
        'bottom' => 'bottom',
        'center' => 'center',
        'centerHorizontal' => 'center_horizontal',
        'centerVertical' => 'center_vertical'
      }
      
      # Handle multiple gravity values
      if value.include?('|')
        value.split('|').map { |v| gravity_map[v.strip] || v.strip }.join('|')
      else
        gravity_map[value] || value
      end
    end

    def map_input_type(value)
      input_types = {
        'text' => 'text',
        'number' => 'number',
        'decimal' => 'numberDecimal',
        'phone' => 'phone',
        'email' => 'textEmailAddress',
        'password' => 'textPassword',
        'multiline' => 'textMultiLine',
        'url' => 'textUri',
        'date' => 'date',
        'time' => 'time',
        'datetime' => 'datetime'
      }
      
      input_types[value] || value
    end

    def map_constraint_attribute(key, value)
      # Map constraint attributes for ConstraintLayout
      constraint_map = {
        'constraintLeft' => 'layout_constraintLeft_toLeftOf',
        'constraintRight' => 'layout_constraintRight_toRightOf',
        'constraintTop' => 'layout_constraintTop_toTopOf',
        'constraintBottom' => 'layout_constraintBottom_toBottomOf',
        'constraintStart' => 'layout_constraintStart_toStartOf',
        'constraintEnd' => 'layout_constraintEnd_toEndOf',
        'constraintHorizontalBias' => 'layout_constraintHorizontal_bias',
        'constraintVerticalBias' => 'layout_constraintVertical_bias'
      }
      
      mapped_name = constraint_map[key]
      if mapped_name
        { namespace: 'app', name: mapped_name, value: value }
      else
        nil
      end
    end

    def convert_value(value, type)
      case type
      when 'dimension'
        convert_dimension(value)
      when 'color'
        convert_color(value)
      when 'float'
        value.to_f.to_s
      when 'integer'
        value.to_i.to_s
      when 'boolean'
        value.to_s
      else
        value
      end
    end
  end
end