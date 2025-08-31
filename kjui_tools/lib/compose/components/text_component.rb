# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/visibility_helper'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class TextComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # Check if component should be skipped entirely (static gone/hidden)
          return "" if Helpers::VisibilityHelper.should_skip_render?(json_data)
          
          # Check if we need to use PartialAttributesText for partial attributes
          if json_data['partialAttributes'] && json_data['partialAttributes'].any?
            return generate_with_partial_attributes_component(json_data, depth, required_imports, parent_type)
          end
          
          # Check if we need to use PartialAttributesText for linkable attribute
          if json_data['linkable']
            return generate_with_partial_attributes_for_linkable(json_data, depth, required_imports, parent_type)
          end
          
          text = Helpers::ResourceResolver.process_text(json_data['text'] || '', required_imports)
          
          component_code = indent("Text(", depth)
          component_code += "\n" + indent("text = #{text},", depth + 1)
          
          # Font size
          if json_data['fontSize']
            component_code += "\n" + indent("fontSize = #{json_data['fontSize']}.sp,", depth + 1)
          end
          
          # Font color (official attribute)
          if json_data['fontColor']
            color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
            component_code += "\n" + indent("color = #{color_value},", depth + 1) if color_value
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
          
          # 1. Add size first (total size including padding)
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          # 2. Add margins (outside spacing)
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # 3. Add shadow before background
          modifiers.concat(Helpers::ModifierBuilder.build_shadow(json_data, required_imports))
          
          # 4. Add background before padding (so padding creates space inside the background)
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          
          # 5. Handle edgeInset for text-specific padding (inside spacing) - applied last
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
        
        def self.generate_with_partial_attributes_for_linkable(json_data, depth, required_imports, parent_type)
          required_imports&.add(:partial_attributes_text)
          
          text = json_data['text'] || ''
          
          code = indent("PartialAttributesText(", depth)
          code += "\n" + indent("text = \"#{escape_string(text)}\",", depth + 1)
          code += "\n" + indent("linkable = true,", depth + 1)
          
          # Build style
          style_parts = []
          
          if json_data['fontSize']
            style_parts << "fontSize = #{json_data['fontSize']}.sp"
          end
          
          if json_data['fontColor']
            color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
            style_parts << "color = #{color_value}" if color_value
          end
          
          if json_data['font'] == 'bold' || json_data['fontWeight'] == 'bold'
            style_parts << "fontWeight = FontWeight.Bold"
          elsif json_data['fontWeight']
            weight_mapping = {
              'thin' => 'Thin',
              'extralight' => 'ExtraLight',
              'light' => 'Light',
              'normal' => 'Normal',
              'medium' => 'Medium',
              'semibold' => 'SemiBold',
              'bold' => 'Bold',
              'extrabold' => 'ExtraBold',
              'black' => 'Black'
            }
            weight = weight_mapping[json_data['fontWeight'].downcase] || json_data['fontWeight'].capitalize
            style_parts << "fontWeight = FontWeight.#{weight}"
          end
          
          if json_data['textAlign']
            required_imports&.add(:text_align)
            case json_data['textAlign'].downcase
            when 'center'
              style_parts << "textAlign = TextAlign.Center"
            when 'right'
              style_parts << "textAlign = TextAlign.End"
            when 'left'
              style_parts << "textAlign = TextAlign.Start"
            end
          end
          
          if style_parts.any?
            required_imports&.add(:text_style)
            code += "\n" + indent("style = TextStyle(#{style_parts.join(', ')}),", depth + 1)
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # Handle edgeInset for text-specific padding
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
          
          # Add background
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          if modifiers.any?
            code += Helpers::ModifierBuilder.format(modifiers, depth)
          else
            code += "\n" + indent("modifier = Modifier", depth + 1)
          end
          
          code += "\n" + indent(")", depth)
          
          # Wrap with VisibilityWrapper if needed
          Helpers::VisibilityHelper.wrap_with_visibility(json_data, code, depth, required_imports)
        end
        
        def self.generate_with_partial_attributes_component(json_data, depth, required_imports, parent_type)
          required_imports&.add(:partial_attributes_text)
          
          text = json_data['text'] || ''
          partial_attrs = json_data['partialAttributes']
          
          code = indent("PartialAttributesText(", depth)
          code += "\n" + indent("text = \"#{escape_string(text)}\",", depth + 1)
          
          # Build partial attributes list
          code += "\n" + indent("partialAttributes = listOf(", depth + 1)
          
          partial_attrs.each_with_index do |attr, index|
            code += "\n" + indent("PartialAttribute.fromJsonRange(", depth + 2)
            
            # Handle range - can be array or string
            range = attr['range']
            if range.is_a?(Array)
              code += "\n" + indent("range = listOf(#{range.join(', ')}),", depth + 3)
            elsif range.is_a?(String)
              code += "\n" + indent("range = \"#{escape_string(range)}\",", depth + 3)
            end
            
            code += "\n" + indent("text = \"#{escape_string(text)}\",", depth + 3)
            
            # Add optional attributes
            if attr['fontColor']
              code += "\n" + indent("fontColor = \"#{attr['fontColor']}\",", depth + 3)
            end
            if attr['fontSize']
              code += "\n" + indent("fontSize = #{attr['fontSize']},", depth + 3)
            end
            if attr['fontWeight']
              code += "\n" + indent("fontWeight = \"#{attr['fontWeight']}\",", depth + 3)
            end
            if attr['background']
              code += "\n" + indent("background = \"#{attr['background']}\",", depth + 3)
            end
            if attr['underline']
              code += "\n" + indent("underline = #{attr['underline']},", depth + 3)
            end
            if attr['strikethrough']
              code += "\n" + indent("strikethrough = #{attr['strikethrough']},", depth + 3)
            end
            if attr['onclick']
              code += "\n" + indent("onClick = { viewModel.#{attr['onclick']}() }", depth + 3)
            else
              code += "\n" + indent("onClick = null", depth + 3)
            end
            
            code += "\n" + indent(")!!", depth + 2) # !! because fromJsonRange returns nullable
            code += "," if index < partial_attrs.length - 1
          end
          
          code += "\n" + indent("),", depth + 1)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          if modifiers.any?
            code += Helpers::ModifierBuilder.format(modifiers, depth)
          else
            code += "\n" + indent("modifier = Modifier", depth + 1)
          end
          
          # Add style
          style_parts = []
          style_parts << "fontSize = #{json_data['fontSize']}.sp" if json_data['fontSize']
          
          if json_data['fontColor']
            color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
            style_parts << "color = #{color_value}" if color_value
          end
          
          if json_data['textAlign']
            required_imports&.add(:text_align)
            case json_data['textAlign'].downcase
            when 'center'
              style_parts << "textAlign = TextAlign.Center"
            when 'right'
              style_parts << "textAlign = TextAlign.End"
            when 'left'
              style_parts << "textAlign = TextAlign.Start"
            end
          end
          
          if style_parts.any?
            required_imports&.add(:text_style)
            code += ",\n" + indent("style = TextStyle(#{style_parts.join(', ')})", depth + 1)
          end
          
          code += "\n" + indent(")", depth)
          
          # Wrap with VisibilityWrapper if needed
          Helpers::VisibilityHelper.wrap_with_visibility(json_data, code, depth, required_imports)
        end
        
        def self.generate_with_partial_attributes(json_data, depth, required_imports, parent_type)
          required_imports&.add(:annotated_string)
          required_imports&.add(:clickable_text)
          required_imports&.add(:remember_state)
          
          text = json_data['text'] || ''
          partial_attrs = json_data['partialAttributes']
          
          # Build AnnotatedString as a variable first
          code = indent("val annotatedText = buildAnnotatedString {", depth)
          code += "\n" + indent("append(\"#{escape_string(text)}\")", depth + 1)
          
          # Apply partial attributes
          partial_attrs.each do |attr|
            range = attr['range']
            next unless range && range.is_a?(Array) && range.length == 2
            
            start_idx = range[0]
            end_idx = range[1]
            
            # Build SpanStyle for this range
            span_styles = []
            
            if attr['fontColor']
              color_resolved = Helpers::ResourceResolver.process_color(attr['fontColor'], required_imports)
              span_styles << "color = #{color_resolved}"
            end
            
            if attr['fontSize']
              span_styles << "fontSize = #{attr['fontSize']}.sp"
            end
            
            if attr['fontWeight']
              weight_mapping = {
                'bold' => 'Bold',
                'semibold' => 'SemiBold',
                'medium' => 'Medium',
                'light' => 'Light'
              }
              weight = weight_mapping[attr['fontWeight'].downcase] || 'Normal'
              span_styles << "fontWeight = FontWeight.#{weight}"
            end
            
            if attr['background']
              background_resolved = Helpers::ResourceResolver.process_color(attr['background'], required_imports)
              span_styles << "background = #{background_resolved}"
            end
            
            if attr['underline']
              required_imports&.add(:text_decoration)
              span_styles << "textDecoration = TextDecoration.Underline"
            end
            
            if attr['strikethrough']
              required_imports&.add(:text_decoration)
              span_styles << "textDecoration = TextDecoration.LineThrough"
            end
            
            if span_styles.any?
              code += "\n" + indent("addStyle(", depth + 1)
              code += "\n" + indent("style = SpanStyle(#{span_styles.join(', ')}),", depth + 2)
              code += "\n" + indent("start = #{start_idx},", depth + 2)
              code += "\n" + indent("end = #{end_idx}", depth + 2)
              code += "\n" + indent(")", depth + 1)
            end
            
            # Add clickable annotation if onclick is specified
            if attr['onclick']
              code += "\n" + indent("addStringAnnotation(", depth + 1)
              code += "\n" + indent("tag = \"CLICKABLE\",", depth + 2)
              code += "\n" + indent("annotation = \"#{attr['onclick']}\",", depth + 2)
              code += "\n" + indent("start = #{start_idx},", depth + 2)
              code += "\n" + indent("end = #{end_idx}", depth + 2)
              code += "\n" + indent(")", depth + 1)
            end
          end
          
          code += "\n" + indent("}", depth)
          code += "\n"
          
          # Now use ClickableText with the annotatedString
          code += indent("ClickableText(", depth)
          code += "\n" + indent("text = annotatedText,", depth + 1)
          
          # Add onClick handler for clickable ranges
          if partial_attrs.any? { |attr| attr['onclick'] }
            code += "\n" + indent("onClick = { offset ->", depth + 1)
            code += "\n" + indent("annotatedText.getStringAnnotations(\"CLICKABLE\", offset, offset)", depth + 2)
            code += "\n" + indent(".firstOrNull()?.let { annotation ->", depth + 3)
            code += "\n" + indent("viewModel.handlePartialClick(annotation.item)", depth + 4)
            code += "\n" + indent("}", depth + 3)
            code += "\n" + indent("},", depth + 1)
          else
            code += "\n" + indent("onClick = { },", depth + 1)
          end
          
          # Add style (fontSize, color, etc. for the whole text)
          style_code = build_text_style(json_data, depth + 1, required_imports)
          if style_code
            code += style_code
          end
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          
          if modifiers.any?
            code += Helpers::ModifierBuilder.format(modifiers, depth)
          else
            code += "\n" + indent("modifier = Modifier", depth + 1)
          end
          
          code += "\n" + indent(")", depth)
          
          # Wrap with VisibilityWrapper if needed
          Helpers::VisibilityHelper.wrap_with_visibility(json_data, code, depth, required_imports)
        end
        
        def self.build_text_style(json_data, depth, required_imports)
          style_parts = []
          
          if json_data['fontSize']
            style_parts << "fontSize = #{json_data['fontSize']}.sp"
          end
          
          if json_data['fontColor']
            color_value = Helpers::ResourceResolver.process_color(json_data['fontColor'], required_imports)
            style_parts << "color = #{color_value}" if color_value
          end
          
          if json_data['textAlign']
            required_imports&.add(:text_align)
            case json_data['textAlign'].downcase
            when 'center'
              style_parts << "textAlign = TextAlign.Center"
            when 'right'
              style_parts << "textAlign = TextAlign.End"
            when 'left'
              style_parts << "textAlign = TextAlign.Start"
            end
          end
          
          if style_parts.any?
            required_imports&.add(:text_style)
            return ",\n" + indent("style = TextStyle(#{style_parts.join(', ')})", depth)
          end
          
          nil
        end
        
        def self.escape_string(text)
          text.gsub('\\', '\\\\\\\\')
              .gsub('"', '\\"')
              .gsub("\n", '\\n')
              .gsub("\r", '\\r')
              .gsub("\t", '\\t')
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