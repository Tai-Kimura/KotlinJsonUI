# frozen_string_literal: true

require 'cli/commands/hotload'

RSpec.describe KjuiTools::CLI::Commands::Hotload do
  before do
    allow(described_class).to receive(:start_hotloader)
    allow(described_class).to receive(:stop_hotloader)
    allow(described_class).to receive(:show_status)
  end

  describe '.run' do
    it 'calls start_hotloader for start command' do
      expect(described_class).to receive(:start_hotloader)
      described_class.run(['start'])
    end

    it 'calls start_hotloader for listen command' do
      expect(described_class).to receive(:start_hotloader)
      described_class.run(['listen'])
    end

    it 'calls stop_hotloader for stop command' do
      expect(described_class).to receive(:stop_hotloader)
      described_class.run(['stop'])
    end

    it 'calls show_status for status command' do
      expect(described_class).to receive(:show_status)
      described_class.run(['status'])
    end

    it 'shows help for unknown command' do
      expect(described_class).to receive(:show_help)
      described_class.run(['unknown'])
    end

    it 'shows help for no command' do
      expect(described_class).to receive(:show_help)
      described_class.run([])
    end
  end

  describe '.show_help' do
    it 'outputs help text' do
      expect { described_class.send(:show_help) }.to output(/KotlinJsonUI HotLoader Commands/).to_stdout
    end

    it 'includes usage information' do
      expect { described_class.send(:show_help) }.to output(/Usage: kjui hotload/).to_stdout
    end

    it 'includes command descriptions' do
      expect { described_class.send(:show_help) }.to output(/start, listen/).to_stdout
      expect { described_class.send(:show_help) }.to output(/stop/).to_stdout
      expect { described_class.send(:show_help) }.to output(/status/).to_stdout
    end
  end

  describe '.find_project_root' do
    let(:temp_dir) { Dir.mktmpdir }

    after do
      FileUtils.rm_rf(temp_dir)
    end

    it 'returns directory with kjui.config.json' do
      File.write(File.join(temp_dir, 'kjui.config.json'), '{}')
      result = described_class.send(:find_project_root, temp_dir)
      expect(result).to eq(temp_dir)
    end

    it 'returns directory with build.gradle.kts' do
      File.write(File.join(temp_dir, 'build.gradle.kts'), '')
      result = described_class.send(:find_project_root, temp_dir)
      expect(result).to eq(temp_dir)
    end

    it 'returns directory with settings.gradle.kts' do
      File.write(File.join(temp_dir, 'settings.gradle.kts'), '')
      result = described_class.send(:find_project_root, temp_dir)
      expect(result).to eq(temp_dir)
    end

    it 'searches parent directories' do
      sub_dir = File.join(temp_dir, 'subdir')
      FileUtils.mkdir_p(sub_dir)
      File.write(File.join(temp_dir, 'kjui.config.json'), '{}')
      result = described_class.send(:find_project_root, sub_dir)
      expect(result).to eq(temp_dir)
    end
  end

  describe '.get_local_ip' do
    it 'returns an IP address' do
      result = described_class.send(:get_local_ip)
      expect(result).to match(/\d+\.\d+\.\d+\.\d+/)
    end
  end

  describe '.port_in_use?' do
    it 'checks if port is in use' do
      result = described_class.send(:port_in_use?, 65535)
      expect([true, false]).to include(result)
    end
  end
end
