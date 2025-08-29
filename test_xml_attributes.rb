#!/usr/bin/env ruby

require 'json'
require 'nokogiri'

# Analyze JSON files to find all unique attributes used
def analyze_json_attributes(json_dir)
  attributes = {}
  
  Dir.glob("#{json_dir}/*.json").each do |json_file|
    begin
      json_data = JSON.parse(File.read(json_file))
      extract_attributes(json_data, attributes, File.basename(json_file))
    rescue => e
      puts "Error reading #{json_file}: #{e.message}"
    end
  end
  
  attributes
end

def extract_attributes(data, attributes, filename)
  if data.is_a?(Hash)
    data.each do |key, value|
      next if ['type', 'child', 'children', 'data'].include?(key)
      
      attributes[key] ||= []
      attributes[key] << { file: filename, value: value } unless attributes[key].any? { |a| a[:file] == filename }
      
      if value.is_a?(Hash) || value.is_a?(Array)
        extract_attributes(value, attributes, filename)
      end
    end
  elsif data.is_a?(Array)
    data.each { |item| extract_attributes(item, attributes, filename) }
  end
end

# Check if attributes are present in XML files
def check_xml_attributes(xml_dir, json_attributes)
  missing_mappings = {}
  
  json_attributes.each do |attr, occurrences|
    found_in_xml = false
    
    occurrences.each do |occurrence|
      xml_file = "#{xml_dir}/#{occurrence[:file].sub('.json', '.xml')}"
      
      if File.exist?(xml_file)
        xml_content = File.read(xml_file)
        # Check for various Android attribute patterns
        android_attrs = [
          "android:#{attr}",
          "app:#{attr}",
          "android:layout_#{attr}",
          "app:layout_constraint#{attr.capitalize}",
          map_known_attribute(attr)
        ].compact
        
        found = android_attrs.any? { |android_attr| xml_content.include?(android_attr) }
        found_in_xml = true if found
      end
    end
    
    unless found_in_xml
      missing_mappings[attr] = occurrences
    end
  end
  
  missing_mappings
end

# Map known attributes to their Android equivalents
def map_known_attribute(attr)
  mappings = {
    'fontSize' => 'android:textSize',
    'fontColor' => 'android:textColor',
    'fontWeight' => 'android:textStyle',
    'cornerRadius' => 'android:radius',
    'borderWidth' => 'android:strokeWidth',
    'borderColor' => 'android:strokeColor',
    'onclick' => 'android:onClick',
    'onClick' => 'android:onClick',
    'maxLines' => 'android:maxLines',
    'lineBreakMode' => 'android:ellipsize',
    'textAlignment' => 'android:textAlignment',
    'alignTop' => 'app:layout_constraintTop_toTopOf',
    'alignBottom' => 'app:layout_constraintBottom_toBottomOf',
    'alignLeft' => 'app:layout_constraintStart_toStartOf',
    'alignRight' => 'app:layout_constraintEnd_toEndOf',
    'alignCenterHorizontal' => 'app:layout_constraintStart_toStartOf',
    'alignCenterVertical' => 'app:layout_constraintTop_toTopOf',
    'alignCenterInParent' => 'app:layout_constraintStart_toStartOf',
    'topMargin' => 'android:layout_marginTop',
    'bottomMargin' => 'android:layout_marginBottom',
    'leftMargin' => 'android:layout_marginStart',
    'rightMargin' => 'android:layout_marginEnd',
    'startMargin' => 'android:layout_marginStart',
    'endMargin' => 'android:layout_marginEnd'
  }
  
  mappings[attr]
end

# Analyze attribute patterns
def analyze_patterns(missing_mappings)
  patterns = {
    alignment: [],
    styling: [],
    layout: [],
    events: [],
    custom: [],
    other: []
  }
  
  missing_mappings.each do |attr, _|
    if attr.include?('align') || attr.include?('center')
      patterns[:alignment] << attr
    elsif attr.include?('font') || attr.include?('text') || attr.include?('color')
      patterns[:styling] << attr
    elsif attr.include?('margin') || attr.include?('padding') || attr.include?('width') || attr.include?('height')
      patterns[:layout] << attr
    elsif attr.include?('on') || attr.include?('click') || attr.include?('event')
      patterns[:events] << attr
    elsif attr[0] == attr[0].upcase  # Likely custom component attributes
      patterns[:custom] << attr
    else
      patterns[:other] << attr
    end
  end
  
  patterns
end

# Main execution
json_dir = "/Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/assets/Layouts"
xml_dir = "/Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/res/layout"

puts "Analyzing JSON attributes..."
json_attributes = analyze_json_attributes(json_dir)
puts "Found #{json_attributes.size} unique attributes in JSON files"

puts "\nChecking XML mappings..."
missing_mappings = check_xml_attributes(xml_dir, json_attributes)

if missing_mappings.empty?
  puts "✅ All attributes are mapped!"
else
  puts "⚠️  Found #{missing_mappings.size} unmapped attributes:\n\n"
  
  patterns = analyze_patterns(missing_mappings)
  
  patterns.each do |category, attrs|
    next if attrs.empty?
    
    puts "#{category.to_s.capitalize} Attributes:"
    attrs.sort.each do |attr|
      examples = missing_mappings[attr].take(2).map { |o| o[:file] }.join(', ')
      puts "  - #{attr} (found in: #{examples})"
    end
    puts
  end
  
  # Output detailed report
  puts "\nDetailed Report:"
  puts "=" * 50
  
  missing_mappings.sort.each do |attr, occurrences|
    puts "\n#{attr}:"
    occurrences.take(3).each do |occ|
      value_preview = occ[:value].to_s.truncate(50) rescue occ[:value].to_s[0..50]
      puts "  File: #{occ[:file]}"
      puts "  Value: #{value_preview}"
    end
  end
end

# Check for attributes that might need special handling
puts "\n" + "=" * 50
puts "Special Cases to Review:"
puts "=" * 50

# Check for relative positioning attributes
relative_attrs = json_attributes.keys.select { |k| k.include?('align') && k.include?('View') }
unless relative_attrs.empty?
  puts "\nRelative Positioning (View-to-View):"
  relative_attrs.each do |attr|
    puts "  - #{attr}"
  end
end

# Check for custom component attributes
custom_attrs = json_attributes.keys.select { |k| k[0] == k[0].upcase && !['View', 'Label', 'Button'].include?(k) }
unless custom_attrs.empty?
  puts "\nCustom Component Attributes:"
  custom_attrs.each do |attr|
    puts "  - #{attr}"
  end
end

puts "\n✅ Test completed!"