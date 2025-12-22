# frozen_string_literal: true

require 'compose/components/button_component'
require 'compose/helpers/modifier_builder'
require 'compose/helpers/resource_resolver'

RSpec.describe KjuiTools::Compose::Components::ButtonComponent do
  let(:required_imports) { Set.new }

  before do
    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return({})
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_full_source_path).and_return('/tmp')
  end

  describe '.generate' do
    it 'generates basic Button component' do
      json_data = { 'type' => 'Button', 'text' => 'Click Me' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Button(')
      expect(result).to include('Click Me')
    end

    it 'generates Button with onClick handler' do
      json_data = { 'type' => 'Button', 'text' => 'Submit', 'onclick' => 'handleSubmit' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onClick = { data.handleSubmit?.invoke() }')
    end

    it 'generates Button with empty onClick when no handler' do
      json_data = { 'type' => 'Button', 'text' => 'Test' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onClick = { }')
    end

    it 'generates Button with cornerRadius' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'cornerRadius' => 8 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('shape = RoundedCornerShape(8.dp)')
      expect(required_imports).to include(:shape)
    end

    it 'generates Button with margins' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'margins' => [10, 20, 30, 40] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding(')
    end

    it 'generates Button with width' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'width' => 200 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.width(200.dp)')
    end

    it 'generates Button with height' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'height' => 50 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.height(50.dp)')
    end

    it 'generates Button with single padding value' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'padding' => 16 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('contentPadding = PaddingValues(16.dp)')
      expect(required_imports).to include(:button_padding)
    end

    it 'generates Button with paddings array of 1' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddings' => [16] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('contentPadding = PaddingValues(16.dp)')
    end

    it 'generates Button with paddings array of 2' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddings' => [10, 20] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('vertical = 10.dp')
      expect(result).to include('horizontal = 20.dp')
    end

    it 'generates Button with paddings array of 3' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddings' => [10, 20, 30] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('top = 10.dp')
      expect(result).to include('horizontal = 20.dp')
      expect(result).to include('bottom = 30.dp')
    end

    it 'generates Button with paddings array of 4' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddings' => [10, 20, 30, 40] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('top = 10.dp')
      expect(result).to include('end = 20.dp')
      expect(result).to include('bottom = 30.dp')
      expect(result).to include('start = 40.dp')
    end

    it 'generates Button with individual padding attributes' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddingTop' => 10, 'paddingBottom' => 20 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('contentPadding')
    end

    it 'generates Button with paddingHorizontal and paddingVertical' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'paddingHorizontal' => 16, 'paddingVertical' => 8 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('horizontal = 16.dp')
      expect(result).to include('vertical = 8.dp')
    end

    it 'generates Button with background color' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'background' => '#007AFF' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('colors = ButtonDefaults.buttonColors')
      expect(result).to include('containerColor')
      expect(required_imports).to include(:button_colors)
    end

    it 'generates Button with disabledBackground' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'disabledBackground' => '#CCCCCC' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('disabledContainerColor')
    end

    it 'generates Button with disabledFontColor' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'disabledFontColor' => '#888888' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('disabledContentColor')
    end

    it 'generates Button with hilightColor comment' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'hilightColor' => '#FF0000' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('// hilightColor')
    end

    it 'generates Button with enabled false' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'enabled' => false }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('enabled = false')
    end

    it 'generates Button with enabled true' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'enabled' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('enabled = true')
    end

    it 'generates Button with enabled data binding' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'enabled' => '@{isActive}' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('enabled = data.isActive')
    end

    it 'generates Button with fontSize' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'fontSize' => 18 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontSize = 18.sp')
    end

    it 'generates Button with fontColor' do
      json_data = { 'type' => 'Button', 'text' => 'Test', 'fontColor' => '#FFFFFF' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('contentColor =')
    end

    it 'generates Text content inside button' do
      json_data = { 'type' => 'Button', 'text' => 'Submit' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include(') {')
      expect(result).to include('Text(')
    end

    it 'uses default text when none provided' do
      json_data = { 'type' => 'Button' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Button')
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
