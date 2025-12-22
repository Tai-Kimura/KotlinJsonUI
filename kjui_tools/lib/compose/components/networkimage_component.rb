# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class NetworkImageComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:async_image)

          # NetworkImage uses 'source' or 'url' for image URL
          url = process_data_binding(json_data['source'] || json_data['url'] || json_data['src'] || '')
          # Support both 'hint' (primary) and 'placeholder' (alias)
          placeholder = json_data['hint'] || json_data['placeholder']
          content_description = json_data['contentDescription'] || 'Image'

          code = indent("AsyncImage(", depth)
          code += "\n" + indent("model = #{url},", depth + 1)
          code += "\n" + indent("contentDescription = \"#{content_description}\",", depth + 1)

          # Content scale (case-insensitive check)
          if json_data['contentMode']
            required_imports&.add(:content_scale)
            scale = case json_data['contentMode'].to_s.downcase
            when 'aspectfit'
              'ContentScale.Fit'
            when 'aspectfill'
              'ContentScale.Crop'
            when 'fill', 'scaletofill'
              'ContentScale.FillBounds'
            when 'center'
              'ContentScale.None'
            else
              'ContentScale.Fit'
            end
            code += "\n" + indent("contentScale = #{scale},", depth + 1)
          end

          # Placeholder
          if placeholder
            code += "\n" + indent("placeholder = painterResource(R.drawable.#{placeholder.gsub('.png', '').gsub('.jpg', '')}),", depth + 1)
          end

          # Build modifiers
          modifiers = []

          # Handle size
          if json_data['size']
            # size is a single value for both width and height
            modifiers << ".size(#{json_data['size']}.dp)"
          else
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          end

          # Corner radius for rounded images
          if json_data['cornerRadius']
            required_imports&.add(:shape)
            modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
          end

          # Border
          if json_data['borderWidth'] && json_data['borderColor']
            required_imports&.add(:border)
            shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
            modifiers << ".border(#{json_data['borderWidth']}.dp, Helpers::ResourceResolver.process_color('#{json_data['borderColor']}', required_imports), #{shape})"
          end

          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, required_imports))
          modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))

          code += Helpers::ModifierBuilder.format(modifiers, depth)

          # Error handling
          if json_data['errorImage']
            code += ",\n" + indent("error = painterResource(R.drawable.#{json_data['errorImage'].gsub('.png', '').gsub('.jpg', '')})", depth + 1)
          end

          code += "\n" + indent(")", depth)
          code
        end

        private

        def self.process_data_binding(text)
          return quote(text) unless text.is_a?(String)

          if text.match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          else
            quote(text)
          end
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
