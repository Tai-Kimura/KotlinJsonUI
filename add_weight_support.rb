#!/usr/bin/env ruby

require 'fileutils'

# Components that should support weight
components_to_update = [
  'button_component.rb',
  'checkbox_component.rb',
  'image_component.rb',
  'networkimage_component.rb',
  'circleimage_component.rb',
  'radio_component.rb',
  'selectbox_component.rb',
  'slider_component.rb',
  'switch_component.rb',
  'textfield_component.rb',
  'textview_component.rb',
  'toggle_component.rb',
  'container_component.rb',
  'progress_component.rb',
  'indicator_component.rb'
]

components_dir = '/Users/like-a-rolling_stone/resource/KotlinJsonUI/kjui_tools/lib/compose/components'

components_to_update.each do |filename|
  file_path = File.join(components_dir, filename)
  
  if File.exist?(file_path)
    content = File.read(file_path)
    
    # Check if weight support is already added
    if content.include?('build_weight')
      puts "#{filename}: Already has weight support"
      next
    end
    
    # Find where to add weight support (after alignment)
    if content =~ /(modifiers\.concat\(Helpers::ModifierBuilder\.build_alignment\([^)]+\)\))/
      alignment_line = $1
      insert_after = alignment_line
      
      # Add weight support
      weight_code = "\n          \n          # Add weight modifier if in Row or Column\n          if parent_type == 'Row' || parent_type == 'Column'\n            modifiers.concat(Helpers::ModifierBuilder.build_weight(json_data, parent_type))\n          end"
      
      new_content = content.sub(insert_after, "#{insert_after}#{weight_code}")
      
      File.write(file_path, new_content)
      puts "#{filename}: Added weight support"
    else
      puts "#{filename}: Could not find alignment line"
    end
  else
    puts "#{filename}: File not found"
  end
end

puts "\nDone! Weight support added to components."