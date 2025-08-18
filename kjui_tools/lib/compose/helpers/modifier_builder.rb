# frozen_string_literal: true

module KjuiTools
  module Compose
    module Helpers
      # Helper class to build Compose modifiers from JSON attributes
      class ModifierBuilder
        def self.build_padding(json_data)
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
        
        def self.build_margins(json_data)
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
        
        def self.build_weight(json_data, parent_orientation = nil)
          modifiers = []
          
          # Weight only works in Row/Column contexts
          if json_data['weight'] && parent_orientation
            modifiers << ".weight(#{json_data['weight']}f)"
          end
          
          modifiers
        end
        
        def self.build_size(json_data)
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
          
          # Min/Max constraints
          if json_data['minWidth']
            modifiers << ".widthIn(min = #{json_data['minWidth']}.dp)"
          end
          
          if json_data['maxWidth']
            modifiers << ".widthIn(max = #{json_data['maxWidth']}.dp)"
          end
          
          if json_data['minHeight']
            modifiers << ".heightIn(min = #{json_data['minHeight']}.dp)"
          end
          
          if json_data['maxHeight']
            modifiers << ".heightIn(max = #{json_data['maxHeight']}.dp)"
          end
          
          # Combined min/max if both specified
          if json_data['minWidth'] && json_data['maxWidth']
            modifiers = modifiers.reject { |m| m.include?('.widthIn') }
            modifiers << ".widthIn(min = #{json_data['minWidth']}.dp, max = #{json_data['maxWidth']}.dp)"
          end
          
          if json_data['minHeight'] && json_data['maxHeight']
            modifiers = modifiers.reject { |m| m.include?('.heightIn') }
            modifiers << ".heightIn(min = #{json_data['minHeight']}.dp, max = #{json_data['maxHeight']}.dp)"
          end
          
          modifiers
        end
        
        def self.build_shadow(json_data, required_imports = nil)
          modifiers = []
          
          if json_data['shadow']
            required_imports&.add(:shadow)
            
            if json_data['shadow'].is_a?(String)
              # Simple shadow with color
              modifiers << ".shadow(4.dp, shape = RectangleShape)"
            elsif json_data['shadow'].is_a?(Hash)
              # Complex shadow configuration
              shadow = json_data['shadow']
              elevation = shadow['radius'] || 4
              shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
              modifiers << ".shadow(#{elevation}.dp, shape = #{shape})"
            end
          end
          
          modifiers
        end
        
        def self.build_background(json_data, required_imports = nil)
          modifiers = []
          
          if json_data['background']
            required_imports&.add(:background)
            
            if json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
              required_imports&.add(:border)
              required_imports&.add(:shape)
              
              if json_data['cornerRadius']
                modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
              end
              
              if json_data['borderColor'] && json_data['borderWidth']
                border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
                modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
              end
              
              modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
            else
              modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
            end
          elsif json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
            required_imports&.add(:border)
            required_imports&.add(:shape)
            
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
        
        def self.build_visibility(json_data)
          modifiers = []
          
          # Handle visibility attribute
          if json_data['visibility']
            case json_data['visibility']
            when 'gone'
              # In Compose, gone is handled by not rendering the component
              return ['SKIP_RENDER']
            when 'invisible'
              modifiers << ".alpha(0f)"
            # 'visible' is the default, no modifier needed
            end
          end
          
          # Handle hidden attribute (boolean or data binding)
          if json_data['hidden']
            if json_data['hidden'] == true
              return ['SKIP_RENDER']
            elsif json_data['hidden'].is_a?(String) && json_data['hidden'].start_with?('@{')
              # Data binding for hidden - needs to be handled at component level
              modifiers << ".alpha(if (#{json_data['hidden'].gsub('@{', 'data.').gsub('}', '')}) 0f else 1f)"
            end
          end
          
          # Handle alpha attribute
          if json_data['alpha']
            modifiers << ".alpha(#{json_data['alpha']}f)"
          end
          
          modifiers
        end
        
        def self.build_alignment(json_data)
          modifiers = []
          
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
        
        def self.format(modifiers, depth)
          return "" if modifiers.empty?
          
          code = "\n" + indent("modifier = Modifier", depth + 1)
          
          if modifiers.length == 1 && modifiers[0].start_with?('.')
            code += modifiers[0]
          else
            modifiers.each do |mod|
              code += "\n" + indent("    #{mod}", depth + 1)
            end
          end
          
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