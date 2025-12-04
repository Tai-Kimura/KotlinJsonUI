# frozen_string_literal: true

require 'compose/components/tabview_component'
require 'compose/helpers/modifier_builder'
require 'compose/helpers/resource_resolver'

RSpec.describe KjuiTools::Compose::Components::TabviewComponent do
  let(:required_imports) { Set.new }

  before do
    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return({})
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_full_source_path).and_return('/tmp')
  end

  describe '.generate' do
    it 'generates TabRow component' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          { 'title' => 'Tab 1' },
          { 'title' => 'Tab 2' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('TabRow(')
      expect(result).to include('Tab(')
      expect(required_imports).to include(:tab_row)
      expect(required_imports).to include(:remember_state)
    end

    it 'generates state variable for selected tab' do
      json_data = {
        'type' => 'TabView',
        'items' => [{ 'title' => 'Tab' }]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('var selectedTab_')
      expect(result).to include('by remember { mutableStateOf(0) }')
    end

    it 'generates tabs for each item' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          { 'title' => 'First' },
          { 'title' => 'Second' },
          { 'title' => 'Third' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('First')
      expect(result).to include('Second')
      expect(result).to include('Third')
    end

    it 'generates default title when not provided' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          {},
          {}
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Tab 1')
      expect(result).to include('Tab 2')
    end

    it 'generates when expression for tab content' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          { 'title' => 'Home' },
          { 'title' => 'Settings' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('when (selectedTab_')
      expect(result).to include('0 -> {')
      expect(result).to include('1 -> {')
    end

    it 'handles child content in tabs' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          { 'title' => 'Tab 1', 'child' => { 'type' => 'Text', 'text' => 'Content' } }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('// Content for tab 0')
    end

    it 'generates default content when no child' do
      json_data = {
        'type' => 'TabView',
        'items' => [
          { 'title' => 'My Tab' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Text("Content for My Tab")')
    end

    it 'wraps in Column container' do
      json_data = {
        'type' => 'TabView',
        'items' => [{ 'title' => 'Tab' }]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Column(')
    end

    it 'applies size modifiers' do
      json_data = {
        'type' => 'TabView',
        'width' => 'matchParent',
        'height' => 200,
        'items' => [{ 'title' => 'Tab' }]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Modifier')
    end

    it 'applies padding modifiers' do
      json_data = {
        'type' => 'TabView',
        'padding' => 16,
        'items' => [{ 'title' => 'Tab' }]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding(16.dp)')
    end

    it 'handles empty items array' do
      json_data = {
        'type' => 'TabView',
        'items' => []
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('TabRow(')
      expect(result).not_to include('Tab(')
    end

    it 'adds tab click handler' do
      json_data = {
        'type' => 'TabView',
        'items' => [{ 'title' => 'Tab 1' }]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onClick = { selectedTab_')
    end
  end

  describe '.indent' do
    it 'returns text unchanged for level 0' do
      result = described_class.send(:indent, 'text', 0)
      expect(result).to eq('text')
    end

    it 'adds indentation for level 1' do
      result = described_class.send(:indent, 'text', 1)
      expect(result).to eq('    text')
    end

    it 'adds indentation for level 2' do
      result = described_class.send(:indent, 'text', 2)
      expect(result).to eq('        text')
    end
  end
end
