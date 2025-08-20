#!/usr/bin/env ruby

require 'json'
require 'fileutils'

# Directory containing JSON files
layouts_dir = '/Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/assets/Layouts'

# Process each JSON file
Dir.glob(File.join(layouts_dir, '*.json')).each do |file_path|
  puts "Processing: #{File.basename(file_path)}"
  
  # Read and parse JSON
  content = File.read(file_path)
  data = JSON.parse(content)
  
  # Function to recursively add fontColor to text elements
  def add_font_color(element)
    if element.is_a?(Hash)
      # Check if it's a text element without fontColor
      if ['Label', 'Text'].include?(element['type']) && !element.key?('fontColor')
        element['fontColor'] = '#000000'
        puts "  Added fontColor to #{element['type']}: #{element['text']&.slice(0, 30)}"
      end
      
      # Process children
      if element['child']
        if element['child'].is_a?(Array)
          element['child'].each { |child| add_font_color(child) }
        else
          add_font_color(element['child'])
        end
      end
      
      # Process other properties that might contain elements
      element.each_value do |value|
        if value.is_a?(Array)
          value.each { |item| add_font_color(item) if item.is_a?(Hash) }
        elsif value.is_a?(Hash)
          add_font_color(value)
        end
      end
    elsif element.is_a?(Array)
      element.each { |item| add_font_color(item) }
    end
  end
  
  # Add fontColor to all text elements
  add_font_color(data)
  
  # Write back the modified JSON
  File.write(file_path, JSON.pretty_generate(data))
  puts "  Updated: #{file_path}"
end

puts "Done! All text elements now have black fontColor."