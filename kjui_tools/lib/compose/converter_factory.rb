# frozen_string_literal: true

require_relative 'view_registry'

module KjuiTools
  module Compose
    class ConverterFactory
      def initialize
        @view_registry = ViewRegistry.new
      end
      
      def create_converter(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
        return nil unless component
        
        # Use self as converter_factory if not provided
        converter_factory ||= self
        view_registry ||= @view_registry
        
        # Determine component type
        type = determine_component_type(component)
        
        # Get converter class from registry
        converter_class = @view_registry.get_converter_class(type)
        
        # Create and return converter instance
        converter_class.new(
          component,
          indent_level,
          action_manager,
          converter_factory,
          view_registry,
          binding_registry
        )
      end
      
      private
      
      def determine_component_type(component)
        return 'View' unless component.is_a?(Hash)
        
        # Check for include first
        return 'Include' if component['include']
        
        # Get type from component
        type = component['type'] || 'View'
        
        # Handle orientation-based type determination
        if type == 'View' && component['orientation']
          case component['orientation']
          when 'horizontal'
            return 'Row'
          when 'vertical'
            return 'Column'
          end
        end
        
        # Map iOS/SwiftUI types to Android/Compose types
        case type
        when 'UILabel', 'UITextView'
          'Text'
        when 'UIButton'
          'Button'
        when 'UIImageView'
          'Image'
        when 'UITextField'
          'TextField'
        when 'UISwitch'
          'Switch'
        when 'UISlider'
          'Slider'
        when 'UIProgressView'
          'ProgressBar'
        when 'UIScrollView'
          'ScrollView'
        when 'UITableView', 'UICollectionView'
          'LazyColumn'
        when 'UIStackView'
          if component['axis'] == 'horizontal'
            'Row'
          else
            'Column'
          end
        else
          type
        end
      end
    end
  end
end