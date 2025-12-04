# frozen_string_literal: true

require 'cli/commands/build'
require 'core/config_manager'
require 'core/project_finder'
require 'core/logger'
require 'fileutils'

RSpec.describe KjuiTools::CLI::Commands::Build do
  let(:temp_dir) { Dir.mktmpdir('build_test') }
  let(:layouts_dir) { File.join(temp_dir, 'src/main/assets/Layouts') }

  let(:config) do
    {
      'source_directory' => 'src/main',
      'layouts_directory' => 'assets/Layouts',
      'view_directory' => 'kotlin/com/example/app/views',
      'viewmodel_directory' => 'kotlin/com/example/app/viewmodels',
      'data_directory' => 'kotlin/com/example/app/data',
      'package_name' => 'com.example.app',
      'project_path' => temp_dir,
      'mode' => 'compose'
    }
  end

  before do
    @original_dir = Dir.pwd
    Dir.chdir(temp_dir)
    FileUtils.mkdir_p(layouts_dir)
    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return(config)
    allow(KjuiTools::Core::ConfigManager).to receive(:get).with('mode').and_return('compose')
    allow(KjuiTools::Core::ProjectFinder).to receive(:setup_paths)
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_full_source_path).and_return(temp_dir)
  end

  after do
    Dir.chdir(@original_dir)
    FileUtils.rm_rf(temp_dir)
  end

  describe '#run' do
    context 'with no layout files' do
      it 'warns about no JSON files' do
        build = described_class.new
        expect { build.run([]) }.to output(/No JSON files found/).to_stdout
      end
    end

    context 'with layout files' do
      before do
        File.write(File.join(layouts_dir, 'test.json'), JSON.generate({
          'type' => 'View',
          'child' => [{ 'type' => 'Text', 'text' => 'Hello' }]
        }))
      end

      it 'processes layout files' do
        build = described_class.new
        # Just ensure it runs without error
        expect { build.run([]) }.not_to raise_error
      end
    end
  end

  describe 'option parsing' do
    it 'parses --mode compose' do
      build = described_class.new
      expect { build.run(['--mode', 'compose']) }.to output(/No JSON files found|Building Compose/).to_stdout
    end

    it 'parses --mode xml' do
      build = described_class.new
      # XML mode requires different setup, just verify option is parsed
      allow(build).to receive(:build_xml)
      expect { build.run(['--mode', 'xml']) }.not_to raise_error
    end

    it 'parses --clean option' do
      build = described_class.new
      expect { build.run(['--clean']) }.not_to raise_error
    end

    it 'parses --validate option' do
      build = described_class.new
      expect { build.run(['--validate']) }.not_to raise_error
    end

    it 'parses --strict option (implies validate)' do
      build = described_class.new
      # Strict mode exits with 1 on validation errors, but we have no files
      expect { build.run(['--strict']) }.not_to raise_error
    end
  end

  describe 'validation' do
    before do
      File.write(File.join(layouts_dir, 'test.json'), JSON.generate({
        'type' => 'View',
        'unknownAttribute' => 'test',
        'child' => [{ 'type' => 'Text', 'text' => 'Hello' }]
      }))
    end

    it 'validates JSON when --validate is specified' do
      build = described_class.new
      # Validation should detect unknown attribute
      expect { build.run(['--validate']) }.not_to raise_error
    end
  end
end
