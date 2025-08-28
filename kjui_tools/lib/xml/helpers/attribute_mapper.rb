#!/usr/bin/env ruby

require_relative 'mappers/dimension_mapper'
require_relative 'mappers/text_mapper'
require_relative 'mappers/layout_mapper'
require_relative 'mappers/style_mapper'
require_relative 'mappers/input_mapper'

module XmlGenerator
  class AttributeMapper
    def initialize
      @dimension_mapper = Mappers::DimensionMapper.new
      @text_mapper = Mappers::TextMapper.new
      @layout_mapper = Mappers::LayoutMapper.new(@dimension_mapper)
      @style_mapper = Mappers::StyleMapper.new(@text_mapper)
      @input_mapper = Mappers::InputMapper.new
      
      @attribute_map = create_attribute_map
    end
    
    def map_dimension(value)
      @dimension_mapper.map_dimension(value)
    end
    
    def map_attribute(key, value, component_type, parent_type = nil)
      # Try layout attributes first (includes dimensions, padding, margin, alignment)
      result = @layout_mapper.map_layout_attributes(key, value, component_type, parent_type)
      return result if result
      
      # Try alignment attributes
      result = @layout_mapper.map_alignment_attributes(key, value, parent_type)
      return result if result
      
      # Try text attributes
      result = @text_mapper.map_text_attributes(key, value, component_type)
      return result if result
      
      # Try style attributes
      result = @style_mapper.map_style_attributes(key, value)
      return result if result
      
      # Try input attributes
      result = @input_mapper.map_input_attributes(key, value)
      return result if result
      
      # Custom component properties (store as tag or tools attribute)
      case key
      when 'title'
        return { namespace: 'tools', name: 'title', value: value }
      when 'count'
        return { namespace: 'tools', name: 'count', value: value.to_s }
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
        # Additional mappings not covered by specific mappers
        'contentDescription' => { name: 'contentDescription', type: 'string' },
        'tag' => { name: 'tag', type: 'string' },
        'transitionName' => { name: 'transitionName', type: 'string' },
        'elevation' => { name: 'elevation', type: 'dimension' },
        'translationZ' => { name: 'translationZ', type: 'dimension' },
        'rotation' => { name: 'rotation', type: 'float' },
        'rotationX' => { name: 'rotationX', type: 'float' },
        'rotationY' => { name: 'rotationY', type: 'float' },
        'scaleX' => { name: 'scaleX', type: 'float' },
        'scaleY' => { name: 'scaleY', type: 'float' }
      }
    end
    
    def convert_value(value, type)
      case type
      when 'dimension'
        @dimension_mapper.convert_dimension(value)
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
    
    def map_constraint_attribute(key, value)
      # ConstraintLayout attributes mapping
      constraint_map = {
        'constraintStartToStartOf' => 'layout_constraintStart_toStartOf',
        'constraintEndToEndOf' => 'layout_constraintEnd_toEndOf',
        'constraintTopToTopOf' => 'layout_constraintTop_toTopOf',
        'constraintBottomToBottomOf' => 'layout_constraintBottom_toBottomOf',
        'constraintStartToEndOf' => 'layout_constraintStart_toEndOf',
        'constraintEndToStartOf' => 'layout_constraintEnd_toStartOf',
        'constraintTopToBottomOf' => 'layout_constraintTop_toBottomOf',
        'constraintBottomToTopOf' => 'layout_constraintBottom_toTopOf'
      }
      
      if constraint_map[key]
        constraint_value = value == 'parent' ? 'parent' : "@id/#{value}"
        return { namespace: 'app', name: constraint_map[key], value: constraint_value }
      end
      
      nil
    end
  end
end