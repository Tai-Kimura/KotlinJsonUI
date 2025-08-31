# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class WebComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          required_imports&.add(:webview)
          
          # Web uses 'url' for the web page URL
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
          
          if json_data['allowZoom']
            code += "\n" + indent("settings.builtInZoomControls = true", depth + 3)
            code += "\n" + indent("settings.displayZoomControls = false", depth + 3)
          end
          
          # Load URL
          code += "\n" + indent("loadUrl(#{url})", depth + 3)
          
          # WebViewClient for handling navigation
          code += "\n" + indent("webViewClient = WebViewClient()", depth + 3)
          
          # WebChromeClient for JavaScript alerts
          if json_data['javaScriptEnabled'] != false
            code += "\n" + indent("webChromeClient = WebChromeClient()", depth + 3)
          end
          
          code += "\n" + indent("}", depth + 2)
          code += "\n" + indent("},", depth + 1)
          
          # Update callback to handle URL changes
          code += "\n" + indent("update = { webView ->", depth + 1)
          
          if json_data['url'] && json_data['url'].match(/@\{([^}]+)\}/)
            code += "\n" + indent("webView.loadUrl(#{url})", depth + 2)
          end
          
          code += "\n" + indent("},", depth + 1)
          
          # Build modifiers
          modifiers = []
          
          # Default size for WebView
          if !json_data['width'] && !json_data['height']
            modifiers << ".fillMaxSize()"
          else
            modifiers.concat(Helpers::ModifierBuilder.build_size(json_data))
          end
          
          modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
          modifiers.concat(Helpers::ModifierBuilder.build_margins(json_data))
          
          # Border for WebView
          if json_data['borderWidth'] && json_data['borderColor']
            required_imports&.add(:border)
            modifiers << ".border(#{json_data['borderWidth']}.dp, Helpers::ResourceResolver.process_color('#{json_data['borderColor']}', required_imports))"
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