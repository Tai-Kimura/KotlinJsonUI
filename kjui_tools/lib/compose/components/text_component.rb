# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/visibility_helper'

module KjuiTools
  module Compose
    module Components
      class TextComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Check if component should be skipped entirely (static gone/hidden)
          return "" if Helpers::VisibilityHelper.should_skip_render?(json_data)
          
          text = process_data_binding(json_data['text'] || '')
          
          component_code = indent("Text(", depth)
          component_code += "\n" + indent("text = #{text},", depth + 1)
          
          # Font size
          if json_data['fontSize']
            component_code += "\n" + indent("fontSize = #{json_data['fontSize']}.sp,", depth + 1)
          end
          
          # Font color (official attribute)
          if json_data['fontColor']
            component_code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['fontColor']}\")),", depth + 1)
          end
          
          # Font weight - handle both 'font' and 'fontWeight' attributes
          if json_data['font'] == 'bold' || json_data['fontWeight'] == 'bold'
            component_code += "\n" + indent("fontWeight = FontWeight.Bold,", depth + 1)
          elsif json_data['fontWeight']
            # Map font weight values to Compose FontWeight constants
            weight_mapping = {
              'thin' => 'Thin',
              'extralight' => 'ExtraLight',
              'light' => 'Light',
              'normal' => 'Normal',
              'medium' => 'Medium',
              'semibold' => 'SemiBold',  # Note: SemiBold with capital B
              'bold' => 'Bold',
              'extrabold' => 'ExtraBold',
              'black' => 'Black'
            }
            weight = weight_mapping[json_data['fontWeight'].downcase] || json_data['fontWeight'].capitalize
            component_code += "\n" + indent("fontWeight = FontWeight.#{weight},", depth + 1)
          end
          
          # Text decoration (underline, strikethrough)
          text_decorations = []
          if json_data['underline']
            required_imports&.add(:text_decoration)
            text_decorations << "TextDecoration.Underline"
          end
          
          if json_data['strikethrough']
            required_imports&.add(:text_decoration)
            text_decorations << "TextDecoration.LineThrough"
          end
          
          if text_decorations.any?
            if text_decorations.length > 1
              component_code += "\n" + indent("textDecoration = TextDecoration.combine(listOf(#{text_decorations.join(', ')})),", depth + 1)
            else
              component_code += "\n" + indent("textDecoration = #{text_decorations.first},", depth + 1)
            end
          end
          
          # Text shadow and line height
          style_parts = []
          
          if json_data['textShadow']
            required_imports&.add(:shadow_style)
            style_parts << "shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f)"
          end
          
          if json_data['lineHeightMultiple']
            required_imports&.add(:text_style)
            # Line height multiplier - apply to font size
            line_height = json_data['fontSize'] ? json_data['fontSize'].to_f * json_data['lineHeightMultiple'].to_f : 14.0 * json_data['lineHeightMultiple'].to_f
            style_parts << "lineHeight = #{line_height}.sp"
          end
          
          if style_parts.any?
            required_imports&.add(:text_style)
            component_code += "\n" + indent("style = TextStyle(#{style_parts.join(', ')}),", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          
          # Get visibility info (but don't add to modifiers, will be handled by wrapper)
          visibility_result = Helpers::ModifierBuilder.build_visibility(json_data, required_imports)
          modifiers.concat(visibility_result[:modifiers]) if visibility_result[:modifiers].any?
          
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          
          # Add weight modifier if in Row or Column
          if parent_type == 'Row' || parent_type == 'Column'
            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          end
          
          # Add margins first (outside spacing)
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # Add shadow before background
          modifiers.concat(Helpers::ModifierBuilder.build_shadow(json_data, required_imports))
          
          # Add background before padding (so padding creates space inside the background)
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          # Handle edgeInset for text-specific padding (inside spacing)
          if json_data['edgeInset']
            insets = json_data['edgeInset']
            if insets.is_a?(Array) && insets.length == 4
              modifiers << ".padding(top = #{insets[0]}.dp, end = #{insets[1]}.dp, bottom = #{insets[2]}.dp, start = #{insets[3]}.dp)"
            elsif insets.is_a?(Numeric)
              modifiers << ".padding(#{insets}.dp)"
            end
          else
            modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # Format modifiers
          if modifiers.any?
            component_code += Helpers::ModifierBuilder.format(modifiers, depth)
          else
            component_code += "\n" + indent("modifier = Modifier", depth + 1)
          end
          
          # Text alignment
          if json_data['textAlign']
            required_imports&.add(:text_align)
            case json_data['textAlign'].downcase
            when 'center'
              component_code += ",\n" + indent("textAlign = TextAlign.Center", depth + 1)
            when 'right'
              component_code += ",\n" + indent("textAlign = TextAlign.End", depth + 1)
            when 'left'
              component_code += ",\n" + indent("textAlign = TextAlign.Start", depth + 1)
            end
          elsif json_data['centerHorizontal']
            required_imports&.add(:text_align)
            component_code += ",\n" + indent("textAlign = TextAlign.Center", depth + 1)
          end
          
          # Lines (maxLines)
          if json_data['lines']
            if json_data['lines'] == 0
              component_code += ",\n" + indent("maxLines = Int.MAX_VALUE", depth + 1)
            else
              component_code += ",\n" + indent("maxLines = #{json_data['lines']}", depth + 1)
            end
          end
          
          # Minimum scale factor (auto-shrink text)
          # In Compose, this is achieved with softWrap=false and overflow=Visible to allow text to scale
          if json_data['minimumScaleFactor']
            # Note: Compose doesn't have direct equivalent, but we can use single line with ellipsis
            # or recommend using a custom composable. For now, we'll add a comment
            component_code += ",\n" + indent("// minimumScaleFactor: #{json_data['minimumScaleFactor']} - Consider using AutoSizeText library", depth + 1)
            component_code += ",\n" + indent("maxLines = 1", depth + 1)
            required_imports&.add(:text_overflow)
            component_code += ",\n" + indent("overflow = TextOverflow.Ellipsis", depth + 1)
          end
          
          # Line break mode (overflow)
          if json_data['lineBreakMode']
            required_imports&.add(:text_overflow)
            case json_data['lineBreakMode'].downcase
            when 'clip'
              component_code += ",\n" + indent("overflow = TextOverflow.Clip", depth + 1)
            when 'tail', 'word'
              component_code += ",\n" + indent("overflow = TextOverflow.Ellipsis", depth + 1)
            end
          end
          
          component_code += "\n" + indent(")", depth)
          
          # Wrap with VisibilityWrapper if needed
          Helpers::VisibilityHelper.wrap_with_visibility(json_data, component_code, depth, required_imports)
        end
        
        private
        
        def self.process_data_binding(text)
          return quote(text) unless text.is_a?(String)
          
          if text.match(/@\{([^}]+)\}/)
            variable = $1
            if variable.include?(' ?? ')
              parts = variable.split(' ?? ')
              var_name = parts[0].strip
              "\"\${data.#{var_name}}\""
            else
              "\"\${data.#{variable}}\""
            end
          else
            quote(text)
          end
        end
        
        def self.quote(text)
          # Escape special characters properly
          escaped = text.gsub('\\', '\\\\\\\\')  # Escape backslashes first
                       .gsub('"', '\\"')           # Escape quotes
                       .gsub("\n", '\\n')           # Escape newlines
                       .gsub("\r", '\\r')           # Escape carriage returns
                       .gsub("\t", '\\t')           # Escape tabs
          "\"#{escaped}\""
        end
        
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