# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class BlurviewComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # BlurView in Compose requires a special modifier or library
          # For now, we'll create a semi-transparent overlay as a fallback
          code = indent("Box(", depth)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # Add blur effect (using semi-transparent background as fallback)
          # In production, you'd use Modifier.blur() from Compose 1.3+ or a library
          blur_radius = json_data['blurRadius'] || 10
          opacity = [0.8, blur_radius.to_f / 20].min  # Convert blur to opacity
          
          # Background color with transparency
          bg_color = json_data['backgroundColor'] || '#FFFFFF'
          modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{bg_color}\")).copy(alpha = #{opacity}f))"
          
          # Add corner radius if specified
          if json_data['cornerRadius']
            required_imports&.add(:shape)
            modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          
          # Note: Real blur would use .blur() modifier if available
          # modifiers << ".blur(#{blur_radius}.dp)"
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          code += "\n" + indent(") {", depth)
          
          # Add child content if present
          if json_data['child']
            code += "\n" + indent("// Child content would be generated here", depth + 1)
            code += "\n" + indent("// Note: Blur effect is simulated with transparency. Use Modifier.blur() for real blur.", depth + 1)
          end
          
          code += "\n" + indent("}", depth)
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