# frozen_string_literal: true

require 'xml/xml_builder'

RSpec.describe KjuiTools::Xml::XmlBuilder do
  let(:temp_dir) { Dir.mktmpdir('xml_builder_test') }
  let(:layouts_dir) { File.join(temp_dir, 'src/main/assets/Layouts') }
  let(:output_dir) { File.join(temp_dir, 'src/main/res/layout') }

  let(:config) do
    {
      'source_directory' => 'src/main',
      'layouts_directory' => 'assets/Layouts',
      'project_path' => temp_dir
    }
  end

  before do
    FileUtils.mkdir_p(layouts_dir)
    FileUtils.mkdir_p(output_dir)

    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return(config)
    allow(KjuiTools::Core::ProjectFinder).to receive(:setup_paths)
    allow(Dir).to receive(:pwd).and_return(temp_dir)
  end

  after do
    FileUtils.rm_rf(temp_dir)
  end

  describe '#initialize' do
    it 'sets up directories correctly' do
      builder = described_class.new(config)
      expect(builder.instance_variable_get(:@layouts_dir)).to include('assets/Layouts')
      expect(builder.instance_variable_get(:@output_dir)).to include('res/layout')
    end

    it 'initializes validation as disabled' do
      builder = described_class.new(config)
      expect(builder.validation_enabled).to be false
    end
  end

  describe '#validation_enabled' do
    it 'can be enabled' do
      builder = described_class.new(config)
      builder.validation_enabled = true
      expect(builder.validation_enabled).to be true
    end
  end

  describe '#validation_callback' do
    it 'can be set' do
      builder = described_class.new(config)
      callback = ->(file, warnings) { puts warnings }
      builder.validation_callback = callback
      expect(builder.validation_callback).to eq(callback)
    end
  end

  describe '#build' do
    context 'when layouts directory does not exist' do
      before do
        FileUtils.rm_rf(layouts_dir)
      end

      it 'returns false' do
        builder = described_class.new(config)
        expect(builder.build).to be false
      end
    end

    context 'when no JSON files found' do
      it 'returns true' do
        builder = described_class.new(config)
        expect(builder.build).to be true
      end
    end
  end

  describe 'validate_json (private)' do
    let(:builder) do
      b = described_class.new(config)
      b.validation_enabled = true
      b.instance_variable_set(:@validator, KjuiTools::Core::AttributeValidator.new(:xml))
      b
    end

    it 'validates component and returns warnings' do
      json_data = {
        'type' => 'Text',
        'unknownAttr' => 'value'
      }

      warnings = builder.send(:validate_json, json_data)
      expect(warnings).to include(/Unknown attribute/)
    end

    it 'validates nested children' do
      json_data = {
        'type' => 'View',
        'child' => [
          { 'type' => 'Text', 'badAttr' => 'value' }
        ]
      }

      warnings = builder.send(:validate_json, json_data)
      expect(warnings).to include(/Unknown attribute/)
    end

    it 'returns empty array for valid component' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello',
        'fontSize' => 14
      }

      warnings = builder.send(:validate_json, json_data)
      expect(warnings).to be_empty
    end
  end
end
