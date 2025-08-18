# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class ImageComponent
        def self.generate(json_data, depth, required_imports = nil)
          # 'src' is the official attribute for images per wiki
          image_name = json_data['src'] || 'placeholder'
          
          code = indent("Image(", depth)
          code += "\n" + indent("painter = painterResource(id = R.drawable.#{image_name}),", depth + 1)
          
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
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Content mode
          if json_data['contentMode']
            case json_data['contentMode'].downcase
            when 'aspectfill'
              code += ",\n" + indent("contentScale = ContentScale.Crop", depth + 1)
            when 'aspectfit'
              code += ",\n" + indent("contentScale = ContentScale.Fit", depth + 1)
            when 'center'
              code += ",\n" + indent("contentScale = ContentScale.None", depth + 1)
            end
          end
          
          code += "\n" + indent(")", depth)
          code
        end
        
        private
        
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