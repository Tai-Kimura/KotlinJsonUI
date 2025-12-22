# frozen_string_literal: true

require_relative '../helpers/modifier_builder'

module KjuiTools
  module Compose
    module Components
      class WebviewComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:webview)
          
          # WebView uses 'url' for the web page URL
          url = if json_data['url'] && json_data['url'].match(/@\{([^}]+)\}/)
            variable = $1
            "data.#{variable}"
          elsif json_data['url']
            "\"#{json_data['url']}\""
          else
            '""'
          end
          
          # Generate WebView using AndroidView
          code = indent("AndroidView(", depth)
          code += "\n" + indent("factory = { context ->", depth + 1)
          code += "\n" + indent("WebView(context).apply {", depth + 2)
          
          # WebView settings
          code += "\n" + indent("settings.javaScriptEnabled = #{json_data['javaScriptEnabled'] != false}", depth + 3)
          
          if json_data['userAgent']
            code += "\n" + indent("settings.userAgentString = \"#{json_data['userAgent']}\"", depth + 3)
          end
          
          code += "\n" + indent("webViewClient = WebViewClient()", depth + 3)
          code += "\n" + indent("webChromeClient = WebChromeClient()", depth + 3)
          
          # Load URL
          code += "\n" + indent("loadUrl(#{url})", depth + 3)
          
          code += "\n" + indent("}", depth + 2)
          code += "\n" + indent("},", depth + 1)
          
          # Build modifiers
          modifiers = []
          modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_alignment(json_data, required_imports, parent_type))
          modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))

          if json_data['cornerRadius']
            required_imports&.add(:shape)
            modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
          end
          
          code += Helpers::ModifierBuilder.format(modifiers, depth)
          
          code += "\n" + indent(")", depth)
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