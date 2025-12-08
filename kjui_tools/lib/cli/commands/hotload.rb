# frozen_string_literal: true

require 'fileutils'
require 'json'
require 'open3'
require_relative '../../hotloader/ip_monitor'

module KjuiTools
  module CLI
    module Commands
      class Hotload
        def self.run(args)
          command = args.first
          
          case command
          when 'start', 'listen'
            start_hotloader
          when 'stop'
            stop_hotloader
          when 'status'
            show_status
          else
            show_help
          end
        end
        
        private
        
        def self.start_hotloader
          puts "Starting KotlinJsonUI HotLoader..."
          puts "================================="

          # Check if Node.js is installed
          unless system('which node > /dev/null 2>&1')
            puts "Error: Node.js is not installed. Please install Node.js first."
            puts "Visit: https://nodejs.org/"
            exit 1
          end

          # Find project root
          project_root = find_project_root
          hotloader_dir = File.join(File.dirname(__FILE__), '../../hotloader')

          # Load config to get port
          config_path = File.join(project_root, 'kjui.config.json')
          config = File.exist?(config_path) ? JSON.parse(File.read(config_path)) : {}
          port = config.dig('hotloader', 'port') || 8081

          # Install npm dependencies if needed
          Dir.chdir(hotloader_dir) do
            unless Dir.exist?('node_modules')
              puts "Installing dependencies..."
              system('npm install')
            end
          end

          # Kill any existing processes on the port
          kill_port_process(port)

          # Start IP monitor
          ip_monitor = KjuiTools::Hotloader::IpMonitor.new(project_root)
          ip_monitor.start

          # Get current IP
          ip = get_local_ip
          puts "\nLocal IP: #{ip}"
          puts "Port: #{port}"

          # Start Node.js server
          puts "\nStarting server..."
          Dir.chdir(hotloader_dir) do
            ENV['HOST'] = '0.0.0.0'
            ENV['PORT'] = port.to_s
            ENV['PROJECT_ROOT'] = project_root

            # Start server in foreground
            system('node server.js')
          end

          # Stop IP monitor when server stops
          ip_monitor.stop
        end
        
        def self.stop_hotloader
          puts "Stopping KotlinJsonUI HotLoader..."

          # Load config to get port
          project_root = find_project_root
          config_path = File.join(project_root, 'kjui.config.json')
          config = File.exist?(config_path) ? JSON.parse(File.read(config_path)) : {}
          port = config.dig('hotloader', 'port') || 8081

          # Kill Node.js server
          kill_port_process(port)

          # Kill any node processes running server.js
          system("pkill -f 'node.*server.js'")

          puts "HotLoader stopped"
        end

        def self.show_status
          puts "KotlinJsonUI HotLoader Status"
          puts "============================="

          # Load config to get port
          project_root = find_project_root
          config_path = File.join(project_root, 'kjui.config.json')
          config = File.exist?(config_path) ? JSON.parse(File.read(config_path)) : {}
          port = config.dig('hotloader', 'port') || 8081

          # Check if server is running
          if port_in_use?(port)
            puts "Status: ✅ Running"

            # Try to get server info
            begin
              require 'net/http'
              require 'uri'

              ip = get_local_ip
              uri = URI.parse("http://#{ip}:#{port}/")
              response = Net::HTTP.get_response(uri)

              if response.code == '200'
                info = JSON.parse(response.body)
                puts "Project: #{info['projectRoot']}"
                puts "Connected clients: #{info['connectedClients']}"
              end
            rescue => e
              puts "Server is running but couldn't get details"
            end
          else
            puts "Status: ❌ Not running"
          end

          # Show configuration
          if config['hotloader']
            puts "\nConfiguration:"
            puts "IP: #{config['hotloader']['ip']}"
            puts "Port: #{config['hotloader']['port']}"
            puts "Enabled: #{config['hotloader']['enabled']}"
          end
        end
        
        def self.show_help
          puts <<~HELP
            KotlinJsonUI HotLoader Commands
            ===============================
            
            Usage: kjui hotload <command>
            
            Commands:
              start, listen  - Start the hotloader server
              stop          - Stop the hotloader server
              status        - Show server status
              
            The hotloader enables real-time UI updates during development.
            It watches for changes in Layouts/ and Styles/ directories and
            automatically rebuilds and reloads the UI in your Android app.
            
            Example:
              kjui hotload start    # Start development server
              kjui hotload stop     # Stop server
              kjui hotload status   # Check if server is running
          HELP
        end
        
        def self.find_project_root(start_path = Dir.pwd)
          current = start_path
          
          while current != '/'
            # Check for kjui.config.json
            if File.exist?(File.join(current, 'kjui.config.json'))
              return current
            end
            
            # Check for Android project files
            if File.exist?(File.join(current, 'build.gradle.kts')) ||
               File.exist?(File.join(current, 'settings.gradle.kts'))
              return current
            end
            
            current = File.dirname(current)
          end
          
          Dir.pwd
        end
        
        def self.get_local_ip
          require 'socket'
          
          # Try common interface names
          interfaces = ['wlan0', 'wlp2s0', 'en0', 'en1', 'eth0']
          
          interfaces.each do |interface|
            Socket.getifaddrs.each do |ifaddr|
              if ifaddr.name == interface && ifaddr.addr&.ipv4?
                ip = ifaddr.addr.ip_address
                return ip unless ip.start_with?('127.')
              end
            end
          end
          
          # Fallback
          Socket.ip_address_list.find { |ai| ai.ipv4? && !ai.ipv4_loopback? }&.ip_address || '127.0.0.1'
        rescue
          '127.0.0.1'
        end
        
        def self.port_in_use?(port)
          system("lsof -i:#{port} > /dev/null 2>&1")
        end
        
        def self.kill_port_process(port)
          if port_in_use?(port)
            puts "Killing existing process on port #{port}..."
            system("lsof -ti:#{port} | xargs kill -9 2>/dev/null")
            sleep 1
          end
        end
      end
    end
  end
end