# frozen_string_literal: true

module KjuiTools
  module Compose
    module Views
      # Base class for all view converters
      class BaseConverter
        attr_reader :json_data, :depth, :required_imports
        
        def initialize(json_data, depth, required_imports)
          @json_data = json_data
          @depth = depth
          @required_imports = required_imports
        end
        
        def convert
          raise NotImplementedError, "Subclasses must implement convert method"
        end
        
        protected
        
        # Helper method to indent text
        def indent(text, level)
          return text if level == 0
          spaces = '    ' * level
          text.split("\n").map { |line| 
            line.empty? ? line : spaces + line 
          }.join("\n")
        end
        
        # Helper to quote strings
        def quote(text)
          "\"#{text.gsub('"', '\\"')}\""
        end
        
        # Process data binding syntax @{variable}
        def process_data_binding(text)
          return quote(text) unless text.is_a?(String)
          
          # Check for data binding syntax @{variable}
          if text.match(/@\{([^}]+)\}/)
            variable = $1
            # Handle Swift's nil-coalescing operator ?? 
            # In Kotlin, since we define default values in the data class,
            # we can just use the variable directly without Elvis operator
            if variable.include?(' ?? ')
              parts = variable.split(' ?? ')
              var_name = parts[0].strip
              # Just use the variable directly since it has a default value in the data class
              "\"\\${data.#{var_name}}\""
            else
              # Use string interpolation for Kotlin
              "\"\\${data.#{variable}}\""
            end
          else
            quote(text)
          end
        end
        
        # Build padding modifiers from JSON attributes
        def build_padding_modifiers(json_data)
          modifiers = []
          
          # Handle padding attribute (can be array [top, right, bottom, left] or single value)
          if json_data['padding']
            if json_data['padding'].is_a?(Array)
              pad_values = json_data['padding']
              if pad_values.length == 4
                modifiers << ".padding(top = #{pad_values[0]}.dp, end = #{pad_values[1]}.dp, bottom = #{pad_values[2]}.dp, start = #{pad_values[3]}.dp)"
              elsif pad_values.length == 1
                modifiers << ".padding(#{pad_values[0]}.dp)"
              end
            else
              modifiers << ".padding(#{json_data['padding']}.dp)"
            end
          end
          
          # Handle paddings attribute (same as padding)
          if json_data['paddings']
            if json_data['paddings'].is_a?(Array)
              pad_values = json_data['paddings']
              if pad_values.length == 4
                modifiers << ".padding(top = #{pad_values[0]}.dp, end = #{pad_values[1]}.dp, bottom = #{pad_values[2]}.dp, start = #{pad_values[3]}.dp)"
              elsif pad_values.length == 1
                modifiers << ".padding(#{pad_values[0]}.dp)"
              end
            else
              modifiers << ".padding(#{json_data['paddings']}.dp)"
            end
          end
          
          # Individual padding attributes
          modifiers << ".padding(top = #{json_data['paddingTop']}.dp)" if json_data['paddingTop']
          modifiers << ".padding(bottom = #{json_data['paddingBottom']}.dp)" if json_data['paddingBottom']
          modifiers << ".padding(start = #{json_data['paddingLeft']}.dp)" if json_data['paddingLeft']
          modifiers << ".padding(end = #{json_data['paddingRight']}.dp)" if json_data['paddingRight']
          
          modifiers
        end
        
        # Build margin modifiers from JSON attributes
        def build_margin_modifiers(json_data)
          modifiers = []
          
          # Handle margins attribute (can be array [top, right, bottom, left] or single value)
          if json_data['margins']
            if json_data['margins'].is_a?(Array)
              margin_values = json_data['margins']
              if margin_values.length == 4
                modifiers << ".padding(top = #{margin_values[0]}.dp, end = #{margin_values[1]}.dp, bottom = #{margin_values[2]}.dp, start = #{margin_values[3]}.dp)"
              elsif margin_values.length == 1
                modifiers << ".padding(#{margin_values[0]}.dp)"
              end
            else
              modifiers << ".padding(#{json_data['margins']}.dp)"
            end
          end
          
          # Individual margin attributes
          modifiers << ".padding(top = #{json_data['topMargin']}.dp)" if json_data['topMargin']
          modifiers << ".padding(bottom = #{json_data['bottomMargin']}.dp)" if json_data['bottomMargin']
          modifiers << ".padding(start = #{json_data['leftMargin']}.dp)" if json_data['leftMargin']
          modifiers << ".padding(end = #{json_data['rightMargin']}.dp)" if json_data['rightMargin']
          
          modifiers
        end
        
        # Build size modifiers
        def build_size_modifiers(json_data)
          modifiers = []
          
          # Width
          if json_data['width'] == 'matchParent'
            modifiers << ".fillMaxWidth()"
          elsif json_data['width'] == 'wrapContent'
            modifiers << ".wrapContentWidth()"
          elsif json_data['width']
            modifiers << ".width(#{json_data['width']}.dp)"
          end
          
          # Height
          if json_data['height'] == 'matchParent'
            modifiers << ".fillMaxHeight()"
          elsif json_data['height'] == 'wrapContent'
            modifiers << ".wrapContentHeight()"
          elsif json_data['height']
            modifiers << ".height(#{json_data['height']}.dp)"
          end
          
          modifiers
        end
        
        # Build background and border modifiers
        def build_background_modifiers(json_data)
          modifiers = []
          
          if json_data['background']
            @required_imports.add(:background) if @required_imports
            
            # Check if we need corner radius and/or border
            if json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
              @required_imports.add(:border) if @required_imports
              @required_imports.add(:shape) if @required_imports
              
              # Build the modifier chain for background with corner radius
              if json_data['cornerRadius']
                modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
              end
              
              # Add border if specified
              if json_data['borderColor'] && json_data['borderWidth']
                border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
                modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
              end
              
              modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
            else
              modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
            end
          elsif json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
            # Handle corner radius and border without background
            @required_imports.add(:border) if @required_imports
            @required_imports.add(:shape) if @required_imports
            
            if json_data['cornerRadius']
              modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
            end
            
            if json_data['borderColor'] && json_data['borderWidth']
              border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
              modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
            end
          end
          
          modifiers
        end
        
        # Build alignment modifiers
        def build_alignment_modifiers(json_data)
          modifiers = []
          
          # Alignment (only works in Box context)
          if json_data['alignTop'] && json_data['alignLeft']
            modifiers << ".align(Alignment.TopStart)"
          elsif json_data['alignTop'] && json_data['alignRight']
            modifiers << ".align(Alignment.TopEnd)"
          elsif json_data['alignBottom'] && json_data['alignLeft']
            modifiers << ".align(Alignment.BottomStart)"
          elsif json_data['alignBottom'] && json_data['alignRight']
            modifiers << ".align(Alignment.BottomEnd)"
          elsif json_data['alignTop'] && json_data['centerHorizontal']
            modifiers << ".align(Alignment.TopCenter)"
          elsif json_data['alignBottom'] && json_data['centerHorizontal']
            modifiers << ".align(Alignment.BottomCenter)"
          elsif json_data['alignLeft'] && json_data['centerVertical']
            modifiers << ".align(Alignment.CenterStart)"
          elsif json_data['alignRight'] && json_data['centerVertical']
            modifiers << ".align(Alignment.CenterEnd)"
          elsif json_data['centerInParent']
            modifiers << ".align(Alignment.Center)"
          elsif json_data['alignTop']
            modifiers << ".align(Alignment.TopCenter)"
          elsif json_data['alignBottom']
            modifiers << ".align(Alignment.BottomCenter)"
          elsif json_data['alignLeft']
            modifiers << ".align(Alignment.CenterStart)"
          elsif json_data['alignRight']
            modifiers << ".align(Alignment.CenterEnd)"
          elsif json_data['centerVertical']
            modifiers << ".align(Alignment.CenterStart)"
          elsif json_data['centerHorizontal']
            modifiers << ".fillMaxWidth()"
          end
          
          modifiers
        end
        
        # Format modifiers for output
        def format_modifiers(modifiers, depth)
          return "" if modifiers.empty?
          
          code = "\n" + indent("modifier = Modifier", depth + 1)
          
          if modifiers.length == 1 && modifiers[0].start_with?('.')
            # Single modifier on same line
            code += modifiers[0]
          else
            # Multiple modifiers on separate lines
            modifiers.each do |mod|
              code += "\n" + indent("    #{mod}", depth + 1)
            end
          end
          
          code
        end
      end
    end
  end
end