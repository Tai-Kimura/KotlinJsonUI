# frozen_string_literal: true

require 'compose/generators/converter_generator'
require 'core/config_manager'
require 'core/project_finder'
require 'core/logger'
require 'fileutils'

RSpec.describe KjuiTools::Compose::Generators::ConverterGenerator do
  let(:temp_dir) { Dir.mktmpdir('converter_gen_test') }

  before do
    @original_dir = Dir.pwd
    Dir.chdir(temp_dir)
    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return({
      '_config_dir' => temp_dir,
      'source_directory' => 'src/main',
      'package_name' => 'com.example.app'
    })
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_package_name).and_return('com.example.app')
    allow(KjuiTools::Core::Logger).to receive(:info)
    allow(KjuiTools::Core::Logger).to receive(:warn)
    allow(KjuiTools::Core::Logger).to receive(:success)
  end

  after do
    Dir.chdir(@original_dir)
    FileUtils.rm_rf(temp_dir)
  end

  describe '#initialize' do
    it 'creates generator with name' do
      generator = described_class.new('TestCard')
      expect(generator).to be_a(described_class)
    end

    it 'creates generator with options' do
      generator = described_class.new('TestCard', { attributes: { 'title' => 'String' } })
      expect(generator).to be_a(described_class)
    end
  end

  describe 'private helper methods' do
    let(:generator) { described_class.new('TestCard') }

    describe '#to_snake_case' do
      it 'converts PascalCase to snake_case' do
        result = generator.send(:to_snake_case, 'TestCard')
        expect(result).to eq('test_card')
      end

      it 'converts camelCase to snake_case' do
        result = generator.send(:to_snake_case, 'testCard')
        expect(result).to eq('test_card')
      end

      it 'handles multiple capitals' do
        result = generator.send(:to_snake_case, 'XMLParser')
        expect(result).to eq('xml_parser')
      end

      it 'handles simple lowercase' do
        result = generator.send(:to_snake_case, 'test')
        expect(result).to eq('test')
      end
    end

    describe '#converter_template' do
      it 'generates valid Ruby class' do
        template = generator.send(:converter_template)
        expect(template).to include('module KjuiTools')
        expect(template).to include('class TestCardComponent')
        expect(template).to include('def self.generate')
      end

      it 'includes component name' do
        template = generator.send(:converter_template)
        expect(template).to include('TestCard(')
      end

      it 'includes modifier building' do
        template = generator.send(:converter_template)
        expect(template).to include('ModifierBuilder.build_size')
        expect(template).to include('ModifierBuilder.build_padding')
      end
    end

    describe '#generate_parameter_collection' do
      it 'returns empty string when no attributes' do
        result = generator.send(:generate_parameter_collection)
        expect(result).to eq('')
      end

      it 'generates parameter collection code for attributes' do
        generator_with_attrs = described_class.new('Test', { attributes: { 'title' => 'String' } })
        result = generator_with_attrs.send(:generate_parameter_collection)
        expect(result).to include("json_data['title']")
        expect(result).to include('format_value.call')
      end

      it 'handles binding attributes' do
        generator_with_attrs = described_class.new('Test', { attributes: { '@name' => 'String' } })
        result = generator_with_attrs.send(:generate_parameter_collection)
        expect(result).to include("json_data['name']")
        expect(result).to include('binding')
      end
    end

    describe '#generate_debug_initializer_content' do
      it 'generates debug initializer with package' do
        result = generator.send(:generate_debug_initializer_content, 'com.example.app')
        expect(result).to include('package com.example.app')
        expect(result).to include('DynamicComponentInitializer')
        expect(result).to include('Configuration.customComponentHandler')
      end
    end

    describe '#generate_release_initializer_content' do
      it 'generates release initializer with no-op' do
        result = generator.send(:generate_release_initializer_content, 'com.example.app')
        expect(result).to include('package com.example.app')
        expect(result).to include('DynamicComponentInitializer')
        expect(result).to include('No-op')
      end
    end

    describe '#create_initial_mappings_file' do
      it 'creates initial mappings file' do
        generator.send(:create_initial_mappings_file)

        extensions_dir = File.join(File.dirname(__FILE__), '..', '..', '..', 'lib', 'compose', 'components', 'extensions')
        mappings_file = File.expand_path(File.join(extensions_dir, 'component_mappings.rb'))

        expect(File.exist?(mappings_file)).to be true
        content = File.read(mappings_file)
        expect(content).to include('COMPONENT_MAPPINGS')
        expect(content).to include('TestCard')

        # Cleanup
        FileUtils.rm_f(mappings_file)
      end
    end
  end

  describe '#generate' do
    let(:generator) { described_class.new('StatusBadge', { attributes: { 'text' => 'String', 'color' => 'Color' } }) }

    before do
      # Stub the generators that get called
      allow_any_instance_of(KjuiTools::Compose::Generators::KotlinComponentGenerator).to receive(:generate)
      allow_any_instance_of(KjuiTools::Compose::Generators::DynamicComponentGenerator).to receive(:generate)

      # Allow file operations to stdin for overwrite prompt
      allow($stdin).to receive(:gets).and_return('n')
    end

    it 'creates converter files' do
      # Clean up any existing files first
      extensions_dir = File.join(File.dirname(__FILE__), '..', '..', '..', 'lib', 'compose', 'components', 'extensions')
      extensions_dir = File.expand_path(extensions_dir)
      status_badge_file = File.join(extensions_dir, 'status_badge_component.rb')
      mappings_file = File.join(extensions_dir, 'component_mappings.rb')

      FileUtils.rm_f(status_badge_file)
      FileUtils.rm_f(mappings_file)

      # Run generator
      generator.generate

      # Check files were created
      expect(File.exist?(status_badge_file)).to be true
      expect(File.exist?(mappings_file)).to be true

      # Check content
      converter_content = File.read(status_badge_file)
      expect(converter_content).to include('class StatusBadgeComponent')
      expect(converter_content).to include('StatusBadge(')

      # Cleanup
      FileUtils.rm_f(status_badge_file)
      FileUtils.rm_f(mappings_file)
    end

    it 'creates dynamic initializer files' do
      # Cleanup extensions first
      extensions_dir = File.join(File.dirname(__FILE__), '..', '..', '..', 'lib', 'compose', 'components', 'extensions')
      extensions_dir = File.expand_path(extensions_dir)
      FileUtils.rm_f(File.join(extensions_dir, 'status_badge_component.rb'))
      FileUtils.rm_f(File.join(extensions_dir, 'component_mappings.rb'))

      generator.generate

      debug_dir = File.join(temp_dir, 'src/debug/kotlin/com/example/app')
      release_dir = File.join(temp_dir, 'src/release/kotlin/com/example/app')

      expect(File.exist?(File.join(debug_dir, 'DynamicComponentInitializer.kt'))).to be true
      expect(File.exist?(File.join(release_dir, 'DynamicComponentInitializer.kt'))).to be true

      # Cleanup extensions
      FileUtils.rm_f(File.join(extensions_dir, 'status_badge_component.rb'))
      FileUtils.rm_f(File.join(extensions_dir, 'component_mappings.rb'))
    end
  end

  describe 'converter template with container' do
    let(:generator) { described_class.new('CardContainer', { is_container: true }) }

    it 'generates container-aware template' do
      template = generator.send(:converter_template)
      expect(template).to include('children')
      expect(template).to include('is_container')
    end
  end

  describe 'converter template with multiple attributes' do
    let(:generator) { described_class.new('MyWidget', { attributes: { 'title' => 'String', 'count' => 'Int', 'active' => 'Boolean' } }) }

    it 'includes all attributes in template' do
      params = generator.send(:generate_parameter_collection)
      expect(params).to include("json_data['title']")
      expect(params).to include("json_data['count']")
      expect(params).to include("json_data['active']")
    end
  end
end
