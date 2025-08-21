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
          # Weight must be greater than 0 in Compose
          if json_data['weight'] && parent_orientation && json_data['weight'].to_f > 0
            modifiers << ".weight(#{json_data['weight']}f)"
          end
          
          modifiers
        end
        
        def self.build_size(json_data)
          modifiers = []
          
          # Width - skip if weight is present and width is 0
          if json_data['width'] == 'matchParent'
            modifiers << ".fillMaxWidth()"
          elsif json_data['width'] == 'wrapContent'
            modifiers << ".wrapContentWidth()"
          elsif json_data['width'] && !(json_data['weight'] && json_data['width'] == 0)
            modifiers << ".width(#{json_data['width']}.dp)"
          end
          
          # Height - skip if heightWeight is present and height is 0
          if json_data['height'] == 'matchParent'
            modifiers << ".fillMaxHeight()"
          elsif json_data['height'] == 'wrapContent'
            modifiers << ".wrapContentHeight()"
          elsif json_data['height'] && !(json_data['heightWeight'] && json_data['height'] == 0)
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
          
          # Aspect ratio
          if json_data['aspectWidth'] && json_data['aspectHeight']
            ratio = json_data['aspectWidth'].to_f / json_data['aspectHeight'].to_f
            modifiers << ".aspectRatio(#{ratio}f)"
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
        
        def self.build_visibility(json_data, required_imports = nil)
          modifiers = []
          visibility_info = {}
          
          # Handle visibility attribute (static or data-bound)
          if json_data['visibility']
            if json_data['visibility'].is_a?(String) && json_data['visibility'].start_with?('@{')
              # Data binding for visibility
              variable = json_data['visibility'].gsub('@{', '').gsub('}', '')
              visibility_info[:visibility_binding] = "data.#{variable}"
              required_imports&.add(:visibility_wrapper)
            else
              # Static visibility
              visibility_info[:visibility] = json_data['visibility']
              required_imports&.add(:visibility_wrapper)
            end
          end
          
          # Handle hidden attribute (boolean or data binding)
          if json_data['hidden']
            if json_data['hidden'].is_a?(String) && json_data['hidden'].start_with?('@{')
              # Data binding for hidden
              variable = json_data['hidden'].gsub('@{', '').gsub('}', '')
              visibility_info[:hidden_binding] = "data.#{variable}"
              required_imports&.add(:visibility_wrapper)
            elsif json_data['hidden'] == true
              visibility_info[:hidden] = true
              required_imports&.add(:visibility_wrapper)
            end
          end
          
          # Handle alpha/opacity attribute separately (not part of visibility wrapper)
          # Support both 'alpha' and 'opacity' for compatibility
          alpha_value = json_data['alpha'] || json_data['opacity']
          if alpha_value
            required_imports&.add(:alpha)
            modifiers << ".alpha(#{alpha_value}f)"
          end
          
          # Return both visibility info and modifiers
          { modifiers: modifiers, visibility_info: visibility_info }
        end
        
        def self.build_alignment(json_data, required_imports = nil, parent_type = nil)
          modifiers = []
          
          # For Row, only vertical alignment is allowed
          if parent_type == 'Row'
            if json_data['alignTop']
              modifiers << ".align(Alignment.Top)"
            elsif json_data['alignBottom']
              modifiers << ".align(Alignment.Bottom)"
            elsif json_data['centerVertical']
              modifiers << ".align(Alignment.CenterVertically)"
            end
          # For Column, only horizontal alignment is allowed
          elsif parent_type == 'Column'
            if json_data['alignLeft']
              modifiers << ".align(Alignment.Start)"
            elsif json_data['alignRight']
              modifiers << ".align(Alignment.End)"
            elsif json_data['centerHorizontal']
              modifiers << ".align(Alignment.CenterHorizontally)"
            end
          # For Box and other containers, full alignment options
          elsif parent_type == 'Box'
            # Check if any alignment is specified
            has_alignment = json_data['alignTop'] || json_data['alignBottom'] || 
                          json_data['alignLeft'] || json_data['alignRight'] || 
                          json_data['centerHorizontal'] || json_data['centerVertical'] || 
                          json_data['centerInParent']
            
            # First check for both-direction constraints (centering behavior)
            has_horizontal_both = json_data['alignLeft'] && json_data['alignRight']
            has_vertical_both = json_data['alignTop'] && json_data['alignBottom']
            
            # Handle combined alignments
            if has_horizontal_both && has_vertical_both
              # Both horizontal and vertical constraints - center completely
              modifiers << ".align(Alignment.Center)"
            elsif has_horizontal_both && json_data['alignTop']
              # Center horizontally, align top
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, -1f))"
            elsif has_horizontal_both && json_data['alignBottom']
              # Center horizontally, align bottom
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, 1f))"
            elsif has_horizontal_both
              # Just center horizontally
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, 0f))"
            elsif has_vertical_both && json_data['alignLeft']
              # Center vertically, align left
              modifiers << ".align(Alignment.CenterStart)"
            elsif has_vertical_both && json_data['alignRight']
              # Center vertically, align right
              modifiers << ".align(Alignment.CenterEnd)"
            elsif has_vertical_both
              # Just center vertically
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, 0f))"
            elsif json_data['alignTop'] && json_data['alignLeft']
              modifiers << ".align(Alignment.TopStart)"
            elsif json_data['alignTop'] && json_data['alignRight']
              modifiers << ".align(Alignment.TopEnd)"
            elsif json_data['alignBottom'] && json_data['alignLeft']
              modifiers << ".align(Alignment.BottomStart)"
            elsif json_data['alignBottom'] && json_data['alignRight']
              modifiers << ".align(Alignment.BottomEnd)"
            elsif json_data['alignTop'] && json_data['centerHorizontal']
              # TopCenter doesn't exist in BoxScope, use BiasAlignment
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, -1f))"
            elsif json_data['alignBottom'] && json_data['centerHorizontal']
              # BottomCenter doesn't exist in BoxScope, use BiasAlignment
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, 1f))"
            elsif json_data['alignLeft'] && json_data['centerVertical']
              modifiers << ".align(Alignment.CenterStart)"
            elsif json_data['alignRight'] && json_data['centerVertical']
              modifiers << ".align(Alignment.CenterEnd)"
            elsif json_data['centerInParent']
              modifiers << ".align(Alignment.Center)"
            # Handle single alignments for Box
            elsif json_data['alignTop']
              # Just top alignment - align to top-left
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(-1f, -1f))"
            elsif json_data['alignBottom']
              # Just bottom alignment - align to bottom-left
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(-1f, 1f))"
            elsif json_data['alignLeft']
              # Just left alignment - align to top-left
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(-1f, -1f))"
            elsif json_data['alignRight']
              # Just right alignment - align to top-right
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(1f, -1f))"
            elsif json_data['centerHorizontal']
              # Center horizontally only - align to top-center
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(0f, -1f))"
            elsif json_data['centerVertical']
              # Center vertically only - align to center-left
              required_imports&.add(:bias_alignment)
              modifiers << ".align(BiasAlignment(-1f, 0f))"
            elsif !has_alignment
              # No alignment specified - default to TopStart (top-left)
              modifiers << ".align(Alignment.TopStart)"
            end
          end
          
          modifiers
        end
        
        def self.build_relative_positioning(json_data)
          # These attributes require ConstraintLayout
          # They generate constraint references instead of modifiers
          constraints = []
          
          # Extract margins for use in constraints
          top_margin = json_data['topMargin'] || 0
          bottom_margin = json_data['bottomMargin'] || 0
          start_margin = json_data['leftMargin'] || 0
          end_margin = json_data['rightMargin'] || 0
          
          if json_data['margins'] && json_data['margins'].is_a?(Array) && json_data['margins'].length == 4
            top_margin = json_data['margins'][0] unless json_data['topMargin']
            end_margin = json_data['margins'][1] unless json_data['rightMargin']
            bottom_margin = json_data['margins'][2] unless json_data['bottomMargin']
            start_margin = json_data['margins'][3] unless json_data['leftMargin']
          end
          
          # Relative to other views
          if json_data['alignTopOfView']
            margin = bottom_margin > 0 ? ", margin = #{bottom_margin}.dp" : ""
            constraints << "bottom.linkTo(#{json_data['alignTopOfView']}.top#{margin})"
          end
          
          if json_data['alignBottomOfView']
            margin = top_margin > 0 ? ", margin = #{top_margin}.dp" : ""
            constraints << "top.linkTo(#{json_data['alignBottomOfView']}.bottom#{margin})"
          end
          
          if json_data['alignLeftOfView']
            margin = end_margin > 0 ? ", margin = #{end_margin}.dp" : ""
            constraints << "end.linkTo(#{json_data['alignLeftOfView']}.start#{margin})"
          end
          
          if json_data['alignRightOfView']
            margin = start_margin > 0 ? ", margin = #{start_margin}.dp" : ""
            constraints << "start.linkTo(#{json_data['alignRightOfView']}.end#{margin})"
          end
          
          # Align edges with other views
          # For align operations, use negative margins to move in the expected direction
          if json_data['alignTopView']
            # alignTop with topMargin means move DOWN from the aligned position
            # linkTo margin pushes away, so use negative to pull closer (move down)
            margin = top_margin > 0 ? ", margin = (-#{top_margin}).dp" : ""
            constraints << "top.linkTo(#{json_data['alignTopView']}.top#{margin})"
          end
          
          if json_data['alignBottomView']
            # alignBottom with bottomMargin means move UP from the aligned position  
            # linkTo margin pushes away, so use negative to pull closer (move up)
            margin = bottom_margin > 0 ? ", margin = (-#{bottom_margin}).dp" : ""
            constraints << "bottom.linkTo(#{json_data['alignBottomView']}.bottom#{margin})"
          end
          
          if json_data['alignLeftView']
            # alignLeft with leftMargin means move RIGHT from the aligned position
            # linkTo margin pushes away, so use negative to pull closer (move right)
            margin = start_margin > 0 ? ", margin = (-#{start_margin}).dp" : ""
            constraints << "start.linkTo(#{json_data['alignLeftView']}.start#{margin})"
          end
          
          if json_data['alignRightView']
            # alignRight with rightMargin means move LEFT from the aligned position
            # linkTo margin pushes away, so use negative to pull closer (move left)
            margin = end_margin > 0 ? ", margin = (-#{end_margin}).dp" : ""
            constraints << "end.linkTo(#{json_data['alignRightView']}.end#{margin})"
          end
          
          # Center with other views
          if json_data['alignCenterVerticalView']
            constraints << "top.linkTo(#{json_data['alignCenterVerticalView']}.top)"
            constraints << "bottom.linkTo(#{json_data['alignCenterVerticalView']}.bottom)"
          end
          
          if json_data['alignCenterHorizontalView']
            constraints << "start.linkTo(#{json_data['alignCenterHorizontalView']}.start)"
            constraints << "end.linkTo(#{json_data['alignCenterHorizontalView']}.end)"
          end
          
          # Parent constraints
          # For parent alignment, margins should work normally as offsets
          if json_data['alignTop']
            margin = top_margin > 0 ? ", margin = #{top_margin}.dp" : ""
            constraints << "top.linkTo(parent.top#{margin})"
          end
          
          if json_data['alignBottom']
            margin = bottom_margin > 0 ? ", margin = #{bottom_margin}.dp" : ""
            constraints << "bottom.linkTo(parent.bottom#{margin})"
          end
          
          if json_data['alignLeft']
            margin = start_margin > 0 ? ", margin = #{start_margin}.dp" : ""
            constraints << "start.linkTo(parent.start#{margin})"
          end
          
          if json_data['alignRight']
            margin = end_margin > 0 ? ", margin = #{end_margin}.dp" : ""
            constraints << "end.linkTo(parent.end#{margin})"
          end
          
          if json_data['centerHorizontal']
            constraints << "start.linkTo(parent.start)"
            constraints << "end.linkTo(parent.end)"
          end
          
          if json_data['centerVertical']
            constraints << "top.linkTo(parent.top)"
            constraints << "bottom.linkTo(parent.bottom)"
          end
          
          if json_data['centerInParent']
            constraints << "top.linkTo(parent.top)"
            constraints << "bottom.linkTo(parent.bottom)"
            constraints << "start.linkTo(parent.start)"
            constraints << "end.linkTo(parent.end)"
          end
          
          constraints
        end
        
        def self.format(modifiers, depth)
          return "" if modifiers.empty?
          
          # Check if first modifier is already "Modifier"
          if modifiers[0] == "Modifier"
            code = "\n" + indent("modifier = Modifier", depth + 1)
            # Skip the first "Modifier" and process the rest
            modifiers[1..-1].each do |mod|
              code += "\n" + indent("    #{mod}", depth + 1)
            end
          else
            code = "\n" + indent("modifier = Modifier", depth + 1)
            
            if modifiers.length == 1 && modifiers[0].start_with?('.')
              code += modifiers[0]
            else
              modifiers.each do |mod|
                code += "\n" + indent("    #{mod}", depth + 1)
              end
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