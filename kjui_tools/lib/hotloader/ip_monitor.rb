# frozen_string_literal: true

require 'socket'
require 'json'
require 'fileutils'

module KjuiTools
  module Hotloader
    class IpMonitor
      CONFIG_FILE = 'kjui.config.json'
      CHECK_INTERVAL = 5 # seconds
      
      def initialize(project_root = nil)
        @project_root = project_root || find_project_root
        @config_path = File.join(@project_root, CONFIG_FILE)
        @running = false
        @thread = nil
        @last_ip = nil
      end
      
      def start
        return if @running
        
        @running = true
        @thread = Thread.new do
          while @running
            check_and_update_ip
            sleep CHECK_INTERVAL
          end
        end
        
        puts "IP Monitor started"
      end
      
      def stop
        @running = false
        @thread&.join
        puts "IP Monitor stopped"
      end
      
      private
      
      def find_project_root(start_path = Dir.pwd)
        current = start_path
        
        # First check current and parent directories
        while current != '/'
          if File.exist?(File.join(current, CONFIG_FILE))
            return current
          end
          
          # Check subdirectories for kjui.config.json
          Dir.glob(File.join(current, '*', CONFIG_FILE)).each do |config_path|
            if File.exist?(config_path)
              return File.dirname(config_path)
            end
          end
          
          current = File.dirname(current)
        end
        
        Dir.pwd
      end
      
      def check_and_update_ip
        current_ip = get_local_ip
        
        if current_ip && current_ip != @last_ip
          update_config(current_ip)
          update_android_configs(current_ip)
          @last_ip = current_ip
          puts "IP updated to: #{current_ip}"
        end
      rescue => e
        puts "Error checking IP: #{e.message}"
      end
      
      def get_local_ip
        # Try to get WiFi IP first (common interface names)
        interfaces = ['wlan0', 'wlp2s0', 'wlp3s0', 'en0', 'en1', 'eth0', 'eth1']
        
        interfaces.each do |interface|
          ip = get_interface_ip(interface)
          return ip if ip && !ip.start_with?('127.')
        end
        
        # Fallback: get any non-localhost IP
        Socket.ip_address_list.each do |addr|
          if addr.ipv4? && !addr.ipv4_loopback? && !addr.ipv4_multicast?
            return addr.ip_address
          end
        end
        
        nil
      end
      
      def get_interface_ip(interface)
        Socket.getifaddrs.each do |ifaddr|
          if ifaddr.name == interface && ifaddr.addr&.ipv4?
            return ifaddr.addr.ip_address
          end
        end
        nil
      rescue
        nil
      end
      
      def update_config(ip)
        config = if File.exist?(@config_path)
                   JSON.parse(File.read(@config_path))
                 else
                   {}
                 end
        
        config['hotloader'] ||= {}
        config['hotloader']['ip'] = ip
        config['hotloader']['port'] ||= 8081
        config['hotloader']['enabled'] = true
        
        File.write(@config_path, JSON.pretty_generate(config))
      end
      
      def update_android_configs(ip)
        # Update local.properties if it exists
        local_props = File.join(@project_root, 'local.properties')
        if File.exist?(local_props)
          content = File.read(local_props)
          
          # Remove old hotloader.ip line if exists
          content.gsub!(/^hotloader\.ip=.*$/, '')
          content.gsub!(/^hotloader\.port=.*$/, '')
          
          # Add new lines
          content += "\nhotloader.ip=#{ip}"
          content += "\nhotloader.port=8081"
          
          File.write(local_props, content)
        end
        
        # Update any BuildConfig or resource files
        update_build_config(ip)
      end
      
      def update_build_config(ip)
        # Load config to get source directory
        config = if File.exist?(@config_path)
                   JSON.parse(File.read(@config_path))
                 else
                   {}
                 end
        
        source_dir = config['source_directory'] || 'src/main'
        
        # Create or update hotloader config in assets
        assets_dir = File.join(@project_root, source_dir, 'assets')
        FileUtils.mkdir_p(assets_dir)
        
        hotloader_config = File.join(assets_dir, 'hotloader.json')
        config = {
          'ip' => ip,
          'port' => 8081,
          'enabled' => true,
          'websocket_endpoint' => "ws://#{ip}:8081",
          'http_endpoint' => "http://#{ip}:8081"
        }
        
        File.write(hotloader_config, JSON.pretty_generate(config))
      end
    end
  end
end