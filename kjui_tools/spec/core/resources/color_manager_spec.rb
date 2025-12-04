# frozen_string_literal: true

require 'core/resources/color_manager'
require 'json'
require 'tempfile'
require 'fileutils'

RSpec.describe KjuiTools::Core::Resources::ColorManager do
  let(:temp_dir) { Dir.mktmpdir }
  let(:resources_dir) { File.join(temp_dir, 'Resources') }
  let(:source_path) { temp_dir }
  let(:config) { { 'source_directory' => 'src/main' } }
  let(:manager) { described_class.new(config, source_path, resources_dir) }

  before do
    FileUtils.mkdir_p(resources_dir)
    allow(KjuiTools::Core::Logger).to receive(:info)
    allow(KjuiTools::Core::Logger).to receive(:debug)
    allow(KjuiTools::Core::Logger).to receive(:warn)
    allow(KjuiTools::Core::Logger).to receive(:error)
  end

  after do
    FileUtils.rm_rf(temp_dir)
  end

  describe '#initialize' do
    it 'creates manager with config' do
      expect(manager).to be_a(described_class)
    end

    it 'loads existing colors.json' do
      colors = { 'primary' => '#007AFF' }
      File.write(File.join(resources_dir, 'colors.json'), JSON.generate(colors))

      new_manager = described_class.new(config, source_path, resources_dir)
      expect(new_manager).to be_a(described_class)
    end

    it 'handles invalid colors.json' do
      File.write(File.join(resources_dir, 'colors.json'), 'invalid json')

      expect { described_class.new(config, source_path, resources_dir) }.not_to raise_error
    end

    it 'loads existing defined_colors.json' do
      colors = { 'custom_color' => nil }
      File.write(File.join(resources_dir, 'defined_colors.json'), JSON.generate(colors))

      new_manager = described_class.new(config, source_path, resources_dir)
      expect(new_manager).to be_a(described_class)
    end

    it 'handles invalid defined_colors.json' do
      File.write(File.join(resources_dir, 'defined_colors.json'), 'invalid json')

      expect { described_class.new(config, source_path, resources_dir) }.not_to raise_error
    end
  end

  describe '#process_colors' do
    it 'returns early for empty processed files' do
      manager.process_colors([], 0, 0)
      expect(File.exist?(File.join(resources_dir, 'colors.json'))).to be false
    end

    it 'extracts colors from JSON files' do
      json_file = File.join(temp_dir, 'test.json')
      File.write(json_file, JSON.generate({ 'background' => '#FF0000' }))

      manager.process_colors([json_file], 1, 0)

      expect(File.exist?(File.join(resources_dir, 'colors.json'))).to be true
    end
  end

  describe '#apply_to_color_assets' do
    it 'creates colors.xml if it does not exist' do
      colors = { 'primary' => '#007AFF' }
      File.write(File.join(resources_dir, 'colors.json'), JSON.generate(colors))

      new_manager = described_class.new(config, source_path, resources_dir)
      new_manager.apply_to_color_assets

      colors_xml_path = File.join(temp_dir, 'src/main/res/values/colors.xml')
      expect(File.exist?(colors_xml_path)).to be true
    end

    it 'updates existing colors.xml' do
      colors_dir = File.join(temp_dir, 'src/main/res/values')
      FileUtils.mkdir_p(colors_dir)

      File.write(File.join(colors_dir, 'colors.xml'), <<~XML)
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
          <color name="existing">#FFFFFF</color>
        </resources>
      XML

      colors = { 'primary' => '#007AFF' }
      File.write(File.join(resources_dir, 'colors.json'), JSON.generate(colors))

      new_manager = described_class.new(config, source_path, resources_dir)
      new_manager.apply_to_color_assets

      content = File.read(File.join(colors_dir, 'colors.xml'))
      expect(content).to include('primary')
    end
  end

  describe '#is_color_property?' do
    it 'identifies background properties' do
      expect(manager.send(:is_color_property?, 'background')).to be true
      expect(manager.send(:is_color_property?, 'backgroundColor')).to be true
    end

    it 'identifies text color properties' do
      expect(manager.send(:is_color_property?, 'fontColor')).to be true
      expect(manager.send(:is_color_property?, 'textColor')).to be true
      expect(manager.send(:is_color_property?, 'color')).to be true
    end

    it 'identifies state background properties' do
      expect(manager.send(:is_color_property?, 'disabledBackground')).to be true
      expect(manager.send(:is_color_property?, 'tapBackground')).to be true
      expect(manager.send(:is_color_property?, 'pressedBackground')).to be true
      expect(manager.send(:is_color_property?, 'selectedBackground')).to be true
    end

    it 'identifies gradient color properties' do
      expect(manager.send(:is_color_property?, 'gradientStartColor')).to be true
      expect(manager.send(:is_color_property?, 'gradientEndColor')).to be true
      expect(manager.send(:is_color_property?, 'startColor')).to be true
    end

    it 'rejects non-color properties' do
      expect(manager.send(:is_color_property?, 'width')).to be false
      expect(manager.send(:is_color_property?, 'text')).to be false
      expect(manager.send(:is_color_property?, 'type')).to be false
    end
  end

  describe '#is_hex_color?' do
    it 'identifies 6-character hex colors' do
      expect(manager.send(:is_hex_color?, '#FF0000')).to be true
      expect(manager.send(:is_hex_color?, 'FF0000')).to be true
    end

    it 'identifies 3-character hex colors' do
      expect(manager.send(:is_hex_color?, '#F00')).to be true
      expect(manager.send(:is_hex_color?, 'F00')).to be true
    end

    it 'identifies 8-character ARGB hex colors' do
      expect(manager.send(:is_hex_color?, '#80FF0000')).to be true
    end

    it 'rejects non-hex strings' do
      expect(manager.send(:is_hex_color?, 'red')).to be false
      expect(manager.send(:is_hex_color?, 'primaryColor')).to be false
    end

    it 'rejects non-string values' do
      expect(manager.send(:is_hex_color?, 123)).to be false
      expect(manager.send(:is_hex_color?, nil)).to be false
    end
  end

  describe '#normalize_hex_color' do
    it 'adds # prefix if missing' do
      expect(manager.send(:normalize_hex_color, 'FF0000')).to eq('#FF0000')
    end

    it 'converts to uppercase' do
      expect(manager.send(:normalize_hex_color, '#ff0000')).to eq('#FF0000')
    end

    it 'expands 3-character hex' do
      expect(manager.send(:normalize_hex_color, '#F00')).to eq('#FF0000')
    end

    it 'preserves 8-character ARGB hex' do
      expect(manager.send(:normalize_hex_color, '#80FF0000')).to eq('#80FF0000')
    end
  end

  describe '#parse_hex_to_rgb' do
    it 'parses 6-character hex' do
      expect(manager.send(:parse_hex_to_rgb, '#FF0000')).to eq([255, 0, 0])
      expect(manager.send(:parse_hex_to_rgb, '#00FF00')).to eq([0, 255, 0])
      expect(manager.send(:parse_hex_to_rgb, '#0000FF')).to eq([0, 0, 255])
    end

    it 'parses 3-character hex' do
      expect(manager.send(:parse_hex_to_rgb, '#F00')).to eq([255, 0, 0])
    end

    it 'parses 8-character ARGB hex' do
      expect(manager.send(:parse_hex_to_rgb, '#80FF0000')).to eq([255, 0, 0])
    end

    it 'returns nil for invalid hex' do
      expect(manager.send(:parse_hex_to_rgb, 'invalid')).to be_nil
    end
  end

  describe '#generate_color_key' do
    it 'generates white for bright colors' do
      key = manager.send(:generate_color_key, '#FFFFFF')
      expect(key).to eq('white')
    end

    it 'generates black for dark colors' do
      key = manager.send(:generate_color_key, '#000000')
      expect(key).to eq('black')
    end

    it 'generates key with color suffix for colored values' do
      key = manager.send(:generate_color_key, '#FF0000')
      expect(key).to include('red')
    end

    it 'generates key with gray suffix for grayscale' do
      key = manager.send(:generate_color_key, '#808080')
      expect(key).to include('gray')
    end

    it 'adds counter suffix for duplicates' do
      # First key
      key1 = manager.send(:generate_color_key, '#000000')
      expect(key1).to eq('black')

      # Add to extracted colors
      manager.instance_variable_get(:@extracted_colors)['black'] = '#000000'

      # Second key should be incremented
      key2 = manager.send(:generate_color_key, '#010101')
      expect(key2).to eq('black_2')
    end
  end

  describe '#process_and_replace_color' do
    it 'skips data binding expressions' do
      result = manager.send(:process_and_replace_color, '@{data.color}')
      expect(result).to eq('@{data.color}')
    end

    it 'replaces hex colors with keys' do
      result = manager.send(:process_and_replace_color, '#FF0000')
      expect(result).not_to eq('#FF0000')
    end

    it 'returns existing key for known colors' do
      manager.instance_variable_get(:@colors_data)['primary'] = '#007AFF'
      result = manager.send(:process_and_replace_color, '#007AFF')
      expect(result).to eq('primary')
    end

    it 'tracks undefined color keys' do
      manager.send(:process_and_replace_color, 'undefinedColor')
      expect(manager.instance_variable_get(:@undefined_colors)).to include('undefinedColor')
    end
  end

  describe '#replace_colors_recursive' do
    it 'replaces colors in hash' do
      data = { 'background' => '#FF0000' }
      manager.send(:replace_colors_recursive, data)
      expect(data['background']).not_to eq('#FF0000')
    end

    it 'handles nested hashes' do
      data = { 'view' => { 'background' => '#FF0000' } }
      manager.send(:replace_colors_recursive, data)
      expect(data['view']['background']).not_to eq('#FF0000')
    end

    it 'handles arrays' do
      data = [{ 'background' => '#FF0000' }]
      manager.send(:replace_colors_recursive, data)
      expect(data[0]['background']).not_to eq('#FF0000')
    end

    it 'ignores non-color properties' do
      data = { 'text' => 'Hello' }
      modified = manager.send(:replace_colors_recursive, data)
      expect(modified).to be false
      expect(data['text']).to eq('Hello')
    end
  end

  describe '#snake_to_camel' do
    it 'converts snake_case to camelCase' do
      expect(manager.send(:snake_to_camel, 'primary_blue')).to eq('primaryBlue')
      expect(manager.send(:snake_to_camel, 'dark_gray')).to eq('darkGray')
    end

    it 'handles single word' do
      expect(manager.send(:snake_to_camel, 'primary')).to eq('primary')
    end

    it 'handles numbers' do
      expect(manager.send(:snake_to_camel, 'white_2')).to eq('white2')
    end
  end

  describe '#generate_kotlin_code' do
    it 'generates valid Kotlin code' do
      colors = { 'primary' => '#007AFF' }
      code = manager.send(:generate_kotlin_code, colors)

      expect(code).to include('object ColorManager')
      expect(code).to include('object android')
      expect(code).to include('object compose')
    end

    it 'includes color accessors' do
      colors = { 'primary_blue' => '#007AFF' }
      code = manager.send(:generate_kotlin_code, colors)

      expect(code).to include('primaryBlue')
    end
  end
end
