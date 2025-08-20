# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class CircleImageComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # CircleImage can be local or network image
          is_network = json_data['url'] || (json_data['source'] && json_data['source'].start_with?('http'))
          
          if is_network
            required_imports&.add(:async_image)
            url = process_data_binding(json_data['url'] || json_data['source'] || json_data['src'] || '')
            
            code = indent("AsyncImage(", depth)
            code += "\n" + indent("model = #{url},", depth + 1)
          else
            # Local image
            image_name = json_data['source'] || json_data['src'] || 'placeholder'
            # Remove file extension and convert to resource name
            resource_name = image_name.gsub('.png', '').gsub('.jpg', '').gsub('-', '_').downcase
            
            code = indent("Image(", depth)
            code += "\n" + indent("painter = painterResource(id = R.drawable.#{resource_name}),", depth + 1)
          end
          
          content_description = json_data['contentDescription'] || 'Profile Image'
          code += "\n" + indent("contentDescription = \"#{content_description}\",", depth + 1)
          
          # Content scale - typically Crop for circular images
          required_imports&.add(:content_scale)
          code += "\n" + indent("contentScale = ContentScale.Crop,", depth + 1)
          
          # Build modifiers for circular shape
          modifiers = []
          
          # Size (use 'size' attribute or default to 48dp)
          size = json_data['size'] || 48
          modifiers << ".size(#{size}.dp)"
          
          # Circular clip
          required_imports&.add(:shape)
          modifiers << ".clip(CircleShape)"
          
          # Border for circle
          if json_data['borderWidth'] && json_data['borderColor']
            required_imports&.add(:border)
            modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), CircleShape)"
          end
          
          # Background (in case image doesn't load)
          if json_data['background']
            required_imports&.add(:background)
            modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          # Error handling for network images
          if is_network && json_data['errorImage']
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