# frozen_string_literal: true

module KjuiTools
  module Compose
    module Views
      class BaseViewConverter
        attr_reader :state_variables
        
        def initialize(component, indent_level = 0, action_manager = nil, binding_registry = nil)
          @component = component || {}
          @indent_level = indent_level
          @action_manager = action_manager
          @generated_code = []
          @state_variables = []
          @binding_registry = binding_registry
        end

        def convert
          raise NotImplementedError, "Subclasses must implement convert method"
        end

        protected
        
        # Get value with binding support
        def get_binding_value(key, default = nil)
          value = @component[key]
          return default if value.nil?
          
          if is_binding?(value)
            extract_binding_property(value)
          else
            value
          end
        end
        
        # Check if a value is a binding expression
        def is_binding?(value)
          return false unless value.is_a?(String)
          value.match?(/^@\{.+\}$/)
        end
        
        # Extract property name from binding expression
        # "@{propertyName}" -> "currentData.value.propertyName"
        def extract_binding_property(value)
          return nil unless value.is_a?(String)
          if value =~ /^@\{(.+)\}$/
            property = $1
            "currentData.value.#{property}"
          else
            value
          end
        end
        
        def process_value(value)
          if is_binding?(value)
            extract_binding_property(value)
          elsif value.is_a?(String)
            quote(value)
          else
            value
          end
        end

        def add_line(line)
          @generated_code << ("    " * @indent_level + line)
        end

        def add_modifier_line(modifier)
          add_line ".#{modifier}"
        end

        def indent(&block)
          @indent_level += 1
          yield
          @indent_level -= 1
        end

        def generated_code
          @generated_code.join("\n")
        end

        # Apply common modifiers
        def apply_modifiers
          modifiers = []
          
          # Size modifiers
          if @component['fillMaxWidth']
            modifiers << "fillMaxWidth()"
          end
          
          if @component['fillMaxHeight']
            modifiers << "fillMaxHeight()"
          end
          
          if @component['width'] && @component['height']
            modifiers << "size(#{@component['width']}dp, #{@component['height']}dp)"
          elsif @component['size']
            modifiers << "size(#{@component['size']}dp)"
          elsif @component['width']
            modifiers << "width(#{@component['width']}dp)"
          elsif @component['height']
            modifiers << "height(#{@component['height']}dp)"
          end
          
          # Padding
          if @component['padding']
            padding = @component['padding']
            if padding.is_a?(Hash)
              if padding['all']
                modifiers << "padding(#{padding['all']}dp)"
              else
                top = padding['top'] || 0
                bottom = padding['bottom'] || 0
                start = padding['start'] || padding['left'] || 0
                end_val = padding['end'] || padding['right'] || 0
                modifiers << "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
              end
            else
              modifiers << "padding(#{padding}dp)"
            end
          end
          
          # Background color
          if @component['background']
            color = map_color(@component['background'])
            modifiers << "background(#{color})"
          end
          
          # Corner radius
          if @component['cornerRadius']
            modifiers << "clip(RoundedCornerShape(#{@component['cornerRadius']}dp))"
          end
          
          # Border
          if @component['borderWidth'] && @component['borderColor']
            color = map_color(@component['borderColor'])
            width = @component['borderWidth']
            if @component['cornerRadius']
              modifiers << "border(#{width}dp, #{color}, RoundedCornerShape(#{@component['cornerRadius']}dp))"
            else
              modifiers << "border(#{width}dp, #{color})"
            end
          end
          
          # Alpha/Opacity
          if @component['alpha'] || @component['opacity']
            opacity = @component['alpha'] || @component['opacity']
            modifiers << "alpha(#{opacity}f)"
          end
          
          # Offset
          if @component['offsetX'] || @component['offsetY']
            x = @component['offsetX'] || 0
            y = @component['offsetY'] || 0
            modifiers << "offset(x = #{x}dp, y = #{y}dp)"
          end
          
          # Click handling
          if @component['onclick'] && @component['type'] != 'Button'
            modifiers << "clickable { viewModel.#{@component['onclick']}() }"
          end
          
          # Build modifier chain
          if modifiers.any?
            if modifiers.length == 1
              add_modifier_line modifiers.first
            else
              add_line "modifier = Modifier"
              modifiers.each do |mod|
                add_modifier_line mod
              end
            end
          end
        end
        
        # Helper methods
        def quote(text)
          return "\"\"" if text.nil?
          "\"#{text.to_s.gsub('"', '\\"')}\""
        end
        
        def map_color(color)
          return "Color.Transparent" if color.nil?
          
          # Handle hex colors
          if color.start_with?('#')
            hex = color.delete('#')
            if hex.length == 6
              "Color(0xFF#{hex.upcase})"
            elsif hex.length == 8
              "Color(0x#{hex.upcase})"
            else
              "Color.Black"
            end
          else
            # Map named colors to Material theme colors
            case color.downcase
            when 'primary'
              'MaterialTheme.colorScheme.primary'
            when 'secondary'
              'MaterialTheme.colorScheme.secondary'
            when 'background'
              'MaterialTheme.colorScheme.background'
            when 'surface'
              'MaterialTheme.colorScheme.surface'
            when 'error'
              'MaterialTheme.colorScheme.error'
            when 'black'
              'Color.Black'
            when 'white'
              'Color.White'
            when 'red'
              'Color.Red'
            when 'blue'
              'Color.Blue'
            when 'green'
              'Color.Green'
            when 'yellow'
              'Color.Yellow'
            when 'gray', 'grey'
              'Color.Gray'
            else
              'Color.Black'
            end
          end
        end
        
        def to_pascal_case(str)
          return "" if str.nil?
          str.split(/[_\-]/).map(&:capitalize).join
        end
        
        def to_camel_case(str)
          return "" if str.nil?
          pascal = to_pascal_case(str)
          return "" if pascal.empty?
          pascal[0].downcase + pascal[1..-1]
        end
      end
    end
  end
end