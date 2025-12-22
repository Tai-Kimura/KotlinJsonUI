# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ImageComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # 'src' is the official attribute for images per wiki
          raw_src = json_data['src'] || 'placeholder'

          # Add required imports
          required_imports&.add(:image)

          code = indent("Image(", depth)

          # Check if src is a binding expression
          if Helpers::ModifierBuilder.is_binding?(raw_src)
            # @{mapTabIcon} -> data.mapTabIcon (expects Painter type in Data)
            property_name = Helpers::ModifierBuilder.extract_binding_property(raw_src)
            camel_case_name = to_camel_case(property_name)
            # Binding case doesn't need painterResource import since Data provides Painter directly
            # Painter is optional, so use inline empty painter as default
            required_imports&.add(:painter_class)
            code += "\n" + indent("painter = data.#{camel_case_name} ?: object : Painter() { override val intrinsicSize get() = Size.Unspecified; override fun DrawScope.onDraw() {} },", depth + 1)
          else
            # Static resource name needs painterResource
            required_imports&.add(:painter_resource)
            required_imports&.add(:r_class)
            code += "\n" + indent("painter = painterResource(id = R.drawable.#{raw_src}),", depth + 1)
          end
          
          # Content description for accessibility
          content_desc = json_data['contentDescription'] || ''
          code += "\n" + indent("contentDescription = #{quote(content_desc)},", depth + 1)
          
          # Build modifiers
          modifiers = []
          
          # Size handling
          if json_data['width'] && json_data['height']
            modifiers << ".size(#{json_data['width']}.dp, #{json_data['height']}.dp)"
          elsif json_data['size']
            modifiers << ".size(#{json_data['size']}.dp)"
          else
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))

          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Content mode (case-insensitive)
          if json_data['contentMode']
            required_imports&.add(:content_scale)
            case json_data['contentMode'].to_s.downcase
            when 'aspectfill'
              code += ",\n" + indent("contentScale = ContentScale.Crop", depth + 1)
            when 'aspectfit'
              code += ",\n" + indent("contentScale = ContentScale.Fit", depth + 1)
            when 'fill', 'scaletofill'
              code += ",\n" + indent("contentScale = ContentScale.FillBounds", depth + 1)
            when 'center'
              code += ",\n" + indent("contentScale = ContentScale.None", depth + 1)
            end
          end
          
          code += "\n" + indent(")", depth)
          code
        end
        
        private

        def self.to_camel_case(snake_case_string)
          return snake_case_string unless snake_case_string.include?('_')
          parts = snake_case_string.split('_')
          parts[0] + parts[1..-1].map(&:capitalize).join
        end

        def self.quote(text)
          "\"#{text.gsub('"', '\\"')}\""
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