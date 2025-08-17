# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ImageConverter < BaseViewConverter
        def convert
          source = @component['source'] || @component['name'] || @component['src']
          content_description = @component['contentDescription'] || @component['alt'] || ''
          
          # Determine if it's a network image or local resource
          if is_network_image?(source)
            generate_network_image(source, content_description)
          else
            generate_local_image(source, content_description)
          end
          
          generated_code
        end
        
        private
        
        def is_network_image?(source)
          return false unless source.is_a?(String)
          source.start_with?('http://', 'https://')
        end
        
        def generate_local_image(source, content_description)
          # Process binding if needed
          image_source = if is_binding?(source)
            extract_binding_property(source)
          else
            "R.drawable.#{sanitize_resource_name(source)}"
          end
          
          add_line "Image("
          indent do
            if is_binding?(source)
              add_line "painter = painterResource(id = #{image_source}),"
            else
              add_line "painter = painterResource(id = #{image_source}),"
            end
            
            add_line "contentDescription = #{quote(content_description)},"
            
            # Content scale (fit mode)
            if @component['contentScale'] || @component['scaleType']
              scale = map_content_scale(@component['contentScale'] || @component['scaleType'])
              add_line "contentScale = ContentScale.#{scale},"
            end
            
            # Color filter/tint
            if @component['tint'] || @component['colorFilter']
              color = map_color(@component['tint'] || @component['colorFilter'])
              add_line "colorFilter = ColorFilter.tint(#{color}),"
            end
            
            # Alignment
            if @component['alignment']
              alignment = map_alignment(@component['alignment'])
              add_line "alignment = Alignment.#{alignment},"
            end
            
            # Alpha
            if @component['alpha']
              add_line "alpha = #{@component['alpha']}f,"
            end
            
            apply_image_modifiers
          end
          add_line ")"
        end
        
        def generate_network_image(url, content_description)
          # Use AsyncImage for network images (requires Coil library)
          add_line "AsyncImage("
          indent do
            url_value = process_value(url)
            add_line "model = #{url_value},"
            add_line "contentDescription = #{quote(content_description)},"
            
            # Content scale
            if @component['contentScale'] || @component['scaleType']
              scale = map_content_scale(@component['contentScale'] || @component['scaleType'])
              add_line "contentScale = ContentScale.#{scale},"
            end
            
            # Placeholder
            if @component['placeholder']
              placeholder = sanitize_resource_name(@component['placeholder'])
              add_line "placeholder = painterResource(R.drawable.#{placeholder}),"
            end
            
            # Error image
            if @component['error']
              error = sanitize_resource_name(@component['error'])
              add_line "error = painterResource(R.drawable.#{error}),"
            end
            
            # Fallback
            if @component['fallback']
              fallback = sanitize_resource_name(@component['fallback'])
              add_line "fallback = painterResource(R.drawable.#{fallback}),"
            end
            
            # Loading listener
            if @component['onLoading'] || @component['onSuccess'] || @component['onError']
              add_line "onLoading = { loading -> viewModel.onImageLoading(loading) }," if @component['onLoading']
              add_line "onSuccess = { success -> viewModel.onImageSuccess(success) }," if @component['onSuccess']
              add_line "onError = { error -> viewModel.onImageError(error) }," if @component['onError']
            end
            
            # Alignment
            if @component['alignment']
              alignment = map_alignment(@component['alignment'])
              add_line "alignment = Alignment.#{alignment},"
            end
            
            # Alpha
            if @component['alpha']
              add_line "alpha = #{@component['alpha']}f,"
            end
            
            apply_image_modifiers
          end
          add_line ")"
        end
        
        def apply_image_modifiers
          modifiers = []
          
          # Size modifiers
          if @component['fillMaxWidth']
            modifiers << "fillMaxWidth()"
          end
          
          if @component['fillMaxHeight']
            modifiers << "fillMaxHeight()"
          end
          
          if @component['width'] && @component['height']
            modifiers << "size(#{@component['width']}dp, #{@component['height']}dp)"
          elsif @component['size']
            modifiers << "size(#{@component['size']}dp)"
          elsif @component['width']
            modifiers << "width(#{@component['width']}dp)"
          elsif @component['height']
            modifiers << "height(#{@component['height']}dp)"
          end
          
          # Aspect ratio
          if @component['aspectRatio']
            ratio = @component['aspectRatio']
            if ratio.is_a?(String) && ratio.include?(':')
              parts = ratio.split(':')
              ratio_value = parts[0].to_f / parts[1].to_f
              modifiers << "aspectRatio(#{ratio_value}f)"
            else
              modifiers << "aspectRatio(#{ratio}f)"
            end
          end
          
          # Clip shape
          if @component['clipShape'] || @component['cornerRadius']
            if @component['cornerRadius']
              modifiers << "clip(RoundedCornerShape(#{@component['cornerRadius']}dp))"
            elsif @component['clipShape'] == 'circle'
              modifiers << "clip(CircleShape)"
            end
          end
          
          # Border
          if @component['borderWidth'] && @component['borderColor']
            color = map_color(@component['borderColor'])
            width = @component['borderWidth']
            if @component['cornerRadius']
              modifiers << "border(#{width}dp, #{color}, RoundedCornerShape(#{@component['cornerRadius']}dp))"
            elsif @component['clipShape'] == 'circle'
              modifiers << "border(#{width}dp, #{color}, CircleShape)"
            else
              modifiers << "border(#{width}dp, #{color})"
            end
          end
          
          # Padding
          if @component['padding']
            padding = @component['padding']
            if padding.is_a?(Hash)
              if padding['all']
                modifiers << "padding(#{padding['all']}dp)"
              else
                top = padding['top'] || 0
                bottom = padding['bottom'] || 0
                start = padding['start'] || padding['left'] || 0
                end_val = padding['end'] || padding['right'] || 0
                modifiers << "padding(start = #{start}dp, top = #{top}dp, end = #{end_val}dp, bottom = #{bottom}dp)"
              end
            else
              modifiers << "padding(#{padding}dp)"
            end
          end
          
          # Click handling
          if @component['onclick']
            modifiers << "clickable { viewModel.#{@component['onclick']}() }"
          end
          
          # Build modifier chain
          if modifiers.any?
            add_line "modifier = Modifier"
            modifiers.each do |mod|
              add_modifier_line mod
            end
          end
        end
        
        def map_content_scale(scale)
          case scale.to_s.downcase
          when 'crop', 'center_crop', 'centercrop'
            'Crop'
          when 'fit', 'fit_center', 'fitcenter'
            'Fit'
          when 'fill', 'fill_bounds', 'fillbounds'
            'FillBounds'
          when 'fill_height', 'fillheight'
            'FillHeight'
          when 'fill_width', 'fillwidth'
            'FillWidth'
          when 'inside'
            'Inside'
          when 'none'
            'None'
          else
            'Fit'
          end
        end
        
        def map_alignment(alignment)
          case alignment.to_s.downcase
          when 'center'
            'Center'
          when 'top_start', 'topstart'
            'TopStart'
          when 'top_center', 'topcenter'
            'TopCenter'
          when 'top_end', 'topend'
            'TopEnd'
          when 'center_start', 'centerstart'
            'CenterStart'
          when 'center_end', 'centerend'
            'CenterEnd'
          when 'bottom_start', 'bottomstart'
            'BottomStart'
          when 'bottom_center', 'bottomcenter'
            'BottomCenter'
          when 'bottom_end', 'bottomend'
            'BottomEnd'
          else
            'Center'
          end
        end
        
        def sanitize_resource_name(name)
          return 'placeholder' unless name
          # Remove file extension and special characters
          name.to_s.downcase.gsub(/\.(png|jpg|jpeg|gif|svg|webp)$/, '').gsub(/[^a-z0-9_]/, '_')
        end
      end
      
      # Network image converter (alias)
      class NetworkImageConverter < ImageConverter; end
    end
  end
end