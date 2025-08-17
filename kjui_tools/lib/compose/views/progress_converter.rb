# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class ProgressConverter < BaseViewConverter
        def convert
          progress_type = determine_progress_type
          
          if progress_type == 'circular'
            generate_circular_progress
          else
            generate_linear_progress
          end
          
          generated_code
        end
        
        private
        
        def determine_progress_type
          type = @component['type']
          
          case type
          when 'CircularProgress', 'CircularProgressIndicator'
            'circular'
          when 'LinearProgress', 'LinearProgressIndicator', 'ProgressBar'
            'linear'
          else
            # Default based on style or shape
            if @component['progressStyle'] == 'circular' || @component['shape'] == 'circle'
              'circular'
            else
              'linear'
            end
          end
        end
        
        def generate_circular_progress
          progress = get_binding_value('progress', nil)
          indeterminate = @component['indeterminate'] || progress.nil?
          
          if indeterminate
            add_line "CircularProgressIndicator("
          else
            add_line "CircularProgressIndicator("
            indent do
              if is_binding?(@component['progress'])
                add_line "progress = #{extract_binding_property(@component['progress'])}.toFloat(),"
              else
                add_line "progress = #{progress || 0}f,"
              end
            end
          end
          
          indent do
            # Color
            if @component['color'] || @component['progressColor']
              color = map_color(@component['color'] || @component['progressColor'])
              add_line "color = #{color},"
            end
            
            # Track color
            if @component['trackColor'] || @component['backgroundColor']
              color = map_color(@component['trackColor'] || @component['backgroundColor'])
              add_line "trackColor = #{color},"
            end
            
            # Stroke width
            if @component['strokeWidth']
              add_line "strokeWidth = #{@component['strokeWidth']}dp,"
            end
            
            # Stroke cap
            if @component['strokeCap']
              cap = map_stroke_cap(@component['strokeCap'])
              add_line "strokeCap = StrokeCap.#{cap},"
            end
            
            apply_modifiers
          end
          
          add_line ")"
        end
        
        def generate_linear_progress
          progress = get_binding_value('progress', nil)
          indeterminate = @component['indeterminate'] || progress.nil?
          
          if indeterminate
            add_line "LinearProgressIndicator("
          else
            add_line "LinearProgressIndicator("
            indent do
              if is_binding?(@component['progress'])
                add_line "progress = #{extract_binding_property(@component['progress'])}.toFloat(),"
              else
                add_line "progress = #{progress || 0}f,"
              end
            end
          end
          
          indent do
            # Color
            if @component['color'] || @component['progressColor']
              color = map_color(@component['color'] || @component['progressColor'])
              add_line "color = #{color},"
            end
            
            # Track color
            if @component['trackColor'] || @component['backgroundColor']
              color = map_color(@component['trackColor'] || @component['backgroundColor'])
              add_line "trackColor = #{color},"
            end
            
            # Stroke cap for linear progress
            if @component['strokeCap']
              cap = map_stroke_cap(@component['strokeCap'])
              add_line "strokeCap = StrokeCap.#{cap},"
            end
            
            apply_modifiers
          end
          
          add_line ")"
        end
        
        def map_stroke_cap(cap)
          case cap.to_s.downcase
          when 'round'
            'Round'
          when 'square'
            'Square'
          when 'butt'
            'Butt'
          else
            'Butt'
          end
        end
      end
      
      # Aliases
      class CircularProgressConverter < ProgressConverter; end
      class LinearProgressConverter < ProgressConverter; end
      class ProgressBarConverter < ProgressConverter; end
    end
  end
end