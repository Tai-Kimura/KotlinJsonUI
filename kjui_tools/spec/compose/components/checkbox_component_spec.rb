# frozen_string_literal: true

require 'compose/components/checkbox_component'

RSpec.describe KjuiTools::Compose::Components::CheckboxComponent do
  let(:required_imports) { Set.new }

  describe '.generate' do
    it 'generates basic Checkbox component' do
      json_data = { 'type' => 'Checkbox' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Checkbox(')
    end

    it 'generates Checkbox with checked state' do
      json_data = { 'type' => 'Checkbox', 'checked' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('checked')
    end

    it 'generates Checkbox with binding' do
      json_data = { 'type' => 'Checkbox', 'checked' => '@{isSelected}' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('checked')
    end

    it 'generates Checkbox with onChange handler' do
      json_data = { 'type' => 'Checkbox', 'onChange' => 'handleCheck' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onCheckedChange')
    end

    it 'adds checkbox_colors import when checkColor specified' do
      json_data = { 'type' => 'Checkbox', 'checkColor' => '#007AFF' }
      described_class.generate(json_data, 0, required_imports)
      expect(required_imports).to include(:checkbox_colors)
    end
  end
end
