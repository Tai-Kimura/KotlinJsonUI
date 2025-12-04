# frozen_string_literal: true

require 'hotloader/ip_monitor'

RSpec.describe KjuiTools::Hotloader::IpMonitor do
  let(:temp_dir) { Dir.mktmpdir('ip_monitor_test') }
  let(:monitor) { described_class.new(temp_dir) }

  before do
    allow(Dir).to receive(:pwd).and_return(temp_dir)
  end

  after do
    monitor.stop if monitor.instance_variable_get(:@running)
    FileUtils.rm_rf(temp_dir)
  end

  describe '#initialize' do
    it 'sets project root' do
      expect(monitor.instance_variable_get(:@project_root)).to eq(temp_dir)
    end

    it 'sets config path' do
      expect(monitor.instance_variable_get(:@config_path)).to eq(File.join(temp_dir, 'kjui.config.json'))
    end

    it 'initializes as not running' do
      expect(monitor.instance_variable_get(:@running)).to be false
    end
  end

  describe '#start' do
    it 'starts monitoring' do
      expect { monitor.start }.to output(/IP Monitor started/).to_stdout
      expect(monitor.instance_variable_get(:@running)).to be true
      expect(monitor.instance_variable_get(:@thread)).not_to be_nil
    end

    it 'does not start twice' do
      expect { monitor.start }.to output(/IP Monitor started/).to_stdout
      expect { monitor.start }.not_to output.to_stdout
    end
  end

  describe '#stop' do
    it 'stops monitoring' do
      monitor.start
      expect { monitor.stop }.to output(/IP Monitor stopped/).to_stdout
      expect(monitor.instance_variable_get(:@running)).to be false
    end
  end

  describe '#get_local_ip (private)' do
    it 'returns an IP address' do
      ip = monitor.send(:get_local_ip)
      # May be nil if no network interface is available
      expect(ip).to be_nil.or match(/\d+\.\d+\.\d+\.\d+/)
    end
  end

  describe '#update_config (private)' do
    it 'creates config with hotloader settings' do
      monitor.send(:update_config, '192.168.1.100')

      expect(File.exist?(File.join(temp_dir, 'kjui.config.json'))).to be true
      config = JSON.parse(File.read(File.join(temp_dir, 'kjui.config.json')))
      expect(config['hotloader']['ip']).to eq('192.168.1.100')
      expect(config['hotloader']['port']).to eq(8081)
      expect(config['hotloader']['enabled']).to be true
    end

    it 'updates existing config' do
      File.write(File.join(temp_dir, 'kjui.config.json'),
                 JSON.pretty_generate({ 'mode' => 'compose' }))

      monitor.send(:update_config, '10.0.0.1')

      config = JSON.parse(File.read(File.join(temp_dir, 'kjui.config.json')))
      expect(config['mode']).to eq('compose')
      expect(config['hotloader']['ip']).to eq('10.0.0.1')
    end
  end

  describe '#update_android_configs (private)' do
    context 'with local.properties file' do
      before do
        File.write(File.join(temp_dir, 'local.properties'), "sdk.dir=/path/to/sdk\n")
      end

      it 'updates local.properties with hotloader settings' do
        monitor.send(:update_android_configs, '192.168.1.50')

        content = File.read(File.join(temp_dir, 'local.properties'))
        expect(content).to include('hotloader.ip=192.168.1.50')
        expect(content).to include('hotloader.port=8081')
      end
    end
  end

  describe '#update_build_config (private)' do
    it 'creates hotloader.json in assets' do
      File.write(File.join(temp_dir, 'kjui.config.json'),
                 JSON.pretty_generate({ 'source_directory' => 'src/main' }))

      monitor.send(:update_build_config, '192.168.1.200')

      hotloader_config = File.join(temp_dir, 'src/main/assets/hotloader.json')
      expect(File.exist?(hotloader_config)).to be true

      config = JSON.parse(File.read(hotloader_config))
      expect(config['ip']).to eq('192.168.1.200')
      expect(config['websocket_endpoint']).to eq('ws://192.168.1.200:8081')
      expect(config['http_endpoint']).to eq('http://192.168.1.200:8081')
    end
  end

  describe '#find_project_root (private)' do
    context 'when config exists in current directory' do
      before do
        File.write(File.join(temp_dir, 'kjui.config.json'), '{}')
      end

      it 'returns current directory' do
        result = monitor.send(:find_project_root, temp_dir)
        expect(result).to eq(temp_dir)
      end
    end

    context 'when config exists in subdirectory' do
      before do
        sub_dir = File.join(temp_dir, 'app')
        FileUtils.mkdir_p(sub_dir)
        File.write(File.join(sub_dir, 'kjui.config.json'), '{}')
      end

      it 'finds config in subdirectory' do
        result = monitor.send(:find_project_root, temp_dir)
        expect(result).to eq(File.join(temp_dir, 'app'))
      end
    end

    context 'when no config exists' do
      it 'returns pwd' do
        result = monitor.send(:find_project_root, temp_dir)
        expect(result).to eq(Dir.pwd)
      end
    end
  end
end
