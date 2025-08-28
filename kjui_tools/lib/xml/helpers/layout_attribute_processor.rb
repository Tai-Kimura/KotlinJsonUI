#!/usr/bin/env ruby

require_relative 'data_binding_helper'

module XmlGenerator
  class LayoutAttributeProcessor
    def initialize(attribute_mapper)
      @attribute_mapper = attribute_mapper
    end
    
    # Process layout dimensions with weight support
    def process_dimensions(json_element, is_root, parent_orientation)
      attrs = {}
      
      has_weight = json_element['weight']
      
      # Default dimensions
      default_width = 'wrap_content'
      default_height = 'wrap_content'
      
      # If root element, default to match_parent
      if is_root
        default_width = 'match_parent'
        default_height = 'match_parent'
      # If weight is specified, set the dimension in the orientation direction to 0dp
      elsif has_weight && parent_orientation
        if parent_orientation == 'horizontal'
          default_width = '0dp' if !json_element['width']
        elsif parent_orientation == 'vertical'
          default_height = '0dp' if !json_element['height']
        end
      end
      
      attrs['android:layout_width'] = @attribute_mapper.map_dimension(
        json_element['width'] || default_width
      )
      attrs['android:layout_height'] = @attribute_mapper.map_dimension(
        json_element['height'] || default_height
      )
      
      attrs
    end
    
    # Process all attributes with gravity combination support
    def process_attributes(json_element, parent_type)
      attrs = {}
      gravity_values = []
      
      # Map all attributes
      json_element.each do |key, value|
        next if ['type', 'child', 'children', 'id', 'width', 'height', 'style', 'data', 'orientation'].include?(key)
        
        android_attr = @attribute_mapper.map_attribute(key, value, json_element['type'], parent_type)
        if android_attr
          namespace, attr_name = android_attr[:namespace], android_attr[:name]
          attr_value = android_attr[:value]
          
          # Handle data binding
          if attr_value.is_a?(String) && attr_value.start_with?('@{')
            attr_value = DataBindingHelper.process_data_binding(attr_value)
          end
          
          # Collect gravity values to combine them
          if attr_name == 'layout_gravity' && parent_type == 'LinearLayout'
            gravity_values << attr_value if value
          else
            if namespace == 'android'
              attrs["android:#{attr_name}"] = attr_value
            elsif namespace == 'app'
              attrs["app:#{attr_name}"] = attr_value
            elsif namespace == 'tools'
              attrs["tools:#{attr_name}"] = attr_value
            else
              attrs[attr_name] = attr_value
            end
          end
        end
      end
      
      # Combine gravity values if there are multiple
      if gravity_values.any?
        attrs['android:layout_gravity'] = gravity_values.join('|')
      end
      
      attrs
    end
    
    # Process LinearLayout orientation
    def process_orientation(view_class, json_element)
      attrs = {}
      
      if view_class == 'LinearLayout' && json_element['orientation']
        attrs['android:orientation'] = json_element['orientation']
      elsif view_class == 'LinearLayout'
        # Default to vertical if not specified
        attrs['android:orientation'] = 'vertical'
      end
      
      attrs
    end
    
  end
end