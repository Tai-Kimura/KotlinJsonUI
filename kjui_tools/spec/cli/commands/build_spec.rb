# frozen_string_literal: true

require 'cli/commands/build'

RSpec.describe KjuiTools::CLI::Commands::Build do
  let(:command) { described_class.new }
  let(:temp_dir) { Dir.mktmpdir('build_test') }

  after do
    FileUtils.rm_rf(temp_dir)
  end

  describe '#run' do
    context 'with --help flag' do
      it 'shows help and exits' do
        expect { command.run(['--help']) }.to raise_error(SystemExit)
      end
    end

    context 'with --mode option' do
      before do
        allow(KjuiTools::Core::ConfigManager).to receive(:get).and_return('compose')
        allow(command).to receive(:build_xml)
        allow(command).to receive(:build_compose)
      end

      it 'accepts compose mode' do
        command.run(['--mode', 'compose'])
        expect(command).to have_received(:build_compose)
        expect(command).not_to have_received(:build_xml)
      end

      it 'accepts xml mode' do
        command.run(['--mode', 'xml'])
        expect(command).to have_received(:build_xml)
        expect(command).not_to have_received(:build_compose)
      end

      it 'accepts all mode' do
        command.run(['--mode', 'all'])
        expect(command).to have_received(:build_xml)
        expect(command).to have_received(:build_compose)
      end
    end

    context 'with --validate option' do
      before do
        allow(KjuiTools::Core::ConfigManager).to receive(:get).and_return('compose')
        allow(command).to receive(:build_compose)
      end

      it 'enables validation' do
        command.run(['--validate'])
        expect(command).to have_received(:build_compose).with(hash_including(validate: true))
      end
    end

    context 'with --strict option' do
      before do
        allow(KjuiTools::Core::ConfigManager).to receive(:get).and_return('compose')
        allow(command).to receive(:build_compose)
      end

      it 'enables strict mode and validation' do
        command.run(['--strict'])
        expect(command).to have_received(:build_compose).with(hash_including(strict: true, validate: true))
      end
    end

    context 'with --clean option' do
      before do
        allow(KjuiTools::Core::ConfigManager).to receive(:get).and_return('compose')
        allow(command).to receive(:build_compose)
      end

      it 'passes clean option' do
        command.run(['--clean'])
        expect(command).to have_received(:build_compose).with(hash_including(clean: true))
      end
    end
  end

  describe '#validate_json (private)' do
    let(:validator) { KjuiTools::Core::AttributeValidator.new(:compose) }

    it 'validates component recursively' do
      json_data = {
        'type' => 'View',
        'child' => [
          { 'type' => 'Text', 'text' => 'Hello' },
          { 'type' => 'Button', 'text' => 'Click' }
        ]
      }

      warnings = command.send(:validate_json, json_data, validator, 'test')
      expect(warnings).to be_empty
    end

    it 'collects warnings from nested components' do
      json_data = {
        'type' => 'View',
        'child' => [
          { 'type' => 'Text', 'unknownAttr' => 'value' }
        ]
      }

      warnings = command.send(:validate_json, json_data, validator, 'test')
      expect(warnings).to include(/Unknown attribute/)
    end

    it 'validates sections in Collection' do
      json_data = {
        'type' => 'Collection',
        'columns' => 2,
        'sections' => [
          {
            'cell' => { 'type' => 'Text', 'invalidAttr' => 'value' }
          }
        ]
      }

      warnings = command.send(:validate_json, json_data, validator, 'test')
      expect(warnings).to include(/Unknown attribute/)
    end
  end
end
