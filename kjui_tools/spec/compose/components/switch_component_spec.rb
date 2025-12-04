# frozen_string_literal: true

require 'compose/components/switch_component'

RSpec.describe KjuiTools::Compose::Components::SwitchComponent do
  let(:required_imports) { Set.new }

  describe '.generate' do
    it 'generates basic Switch component' do
      json_data = { 'type' => 'Switch' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Switch(')
    end

    it 'generates Switch with checked state binding' do
      json_data = { 'type' => 'Switch', 'on' => '@{isEnabled}' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('checked')
    end

    it 'generates Switch with static checked state true' do
      json_data = { 'type' => 'Switch', 'on' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('checked')
    end

    it 'generates Switch with onChange handler' do
      json_data = { 'type' => 'Switch', 'onChange' => 'handleToggle' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onCheckedChange')
    end

    it 'adds switch_colors import when onTintColor specified' do
      json_data = { 'type' => 'Switch', 'onTintColor' => '#007AFF' }
      described_class.generate(json_data, 0, required_imports)
      expect(required_imports).to include(:switch_colors)
    end
  end
end
