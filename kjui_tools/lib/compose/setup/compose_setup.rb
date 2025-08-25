# frozen_string_literal: true

require 'fileutils'
require 'json'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module Compose
    module Setup
      class ComposeSetup
        def initialize(project_file_path = nil)
          @project_file_path = project_file_path
          @config = Core::ConfigManager.load_config
          @source_path = Core::ProjectFinder.get_full_source_path
          @package_name = Core::ProjectFinder.package_name
        end
        
        def run_full_setup
          puts "Setting up Compose project..."
          
          # Create directory structure
          create_directory_structure
          
          # Copy base files
          copy_base_files
          
          # Create hotloader config
          create_hotloader_config
          
          # Setup network security for hot reload
          setup_network_security
          
          # Update build.gradle
          update_build_gradle
          
          # Create sample layouts
          create_sample_layouts
          
          puts "Compose setup complete!"
        end
        
        private
        
        def create_directory_structure
          puts "Creating directory structure..."
          
          # Get source directory from config
          source_dir = @config['source_directory'] || 'src/main'
          
          directories = [
            File.join(source_dir, 'assets/Layouts'),
            File.join(source_dir, 'assets/Styles'),
            package_path('ui/components'),
            package_path('ui/theme')
            # data, viewmodels, views directories will be created by g view command
          ]
          
          directories.each do |dir|
            # All paths should be relative to the project root
            FileUtils.mkdir_p(dir) unless Dir.exist?(dir)
            puts "  Created: #{dir}"
          end
        end
        
        def copy_base_files
          puts "Creating base files..."
          
          # Create theme file
          create_theme_file
          
          # Create base components
          create_base_components
          
          # Create MainActivity with Compose setup
          create_main_activity
        end
        
        def create_theme_file
          theme_path = File.join(package_path('ui/theme'), 'Theme.kt')
          
          content = <<~KOTLIN
            package #{@package_name}.ui.theme
            
            import androidx.compose.foundation.isSystemInDarkTheme
            import androidx.compose.material3.*
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.Color
            
            private val LightColorScheme = lightColorScheme(
                primary = Color(0xFF6200EE),
                onPrimary = Color.White,
                secondary = Color(0xFF03DAC6),
                onSecondary = Color.Black,
                background = Color(0xFFF5F5F5),
                onBackground = Color.Black,
                surface = Color.White,
                onSurface = Color.Black,
            )
            
            private val DarkColorScheme = darkColorScheme(
                primary = Color(0xFFBB86FC),
                onPrimary = Color.Black,
                secondary = Color(0xFF03DAC6),
                onSecondary = Color.Black,
                background = Color(0xFF121212),
                onBackground = Color.White,
                surface = Color(0xFF121212),
                onSurface = Color.White,
            )
            
            @Composable
            fun KotlinJsonUITheme(
                darkTheme: Boolean = isSystemInDarkTheme(),
                content: @Composable () -> Unit
            ) {
                val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
                
                MaterialTheme(
                    colorScheme = colorScheme,
                    typography = Typography(),
                    content = content
                )
            }
          KOTLIN
          
          File.write(theme_path, content)
          puts "  Created: Theme.kt"
        end
        
        def create_base_components
          # Create JsonUILoader component
          loader_path = File.join(package_path('ui/components'), 'JsonUILoader.kt')
          
          content = <<~KOTLIN
            package #{@package_name}.ui.components
            
            import androidx.compose.runtime.*
            import androidx.compose.ui.platform.LocalContext
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.withContext
            import org.json.JSONObject
            
            /**
             * Loads and renders a JSON UI layout
             */
            @Composable
            fun JsonUILoader(
                layoutName: String,
                onAction: (String) -> Unit = {}
            ) {
                val context = LocalContext.current
                var jsonContent by remember { mutableStateOf<JSONObject?>(null) }
                
                LaunchedEffect(layoutName) {
                    withContext(Dispatchers.IO) {
                        try {
                            val inputStream = context.assets.open("Layouts/$layoutName.json")
                            val json = inputStream.bufferedReader().use { it.readText() }
                            jsonContent = JSONObject(json)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                jsonContent?.let { json ->
                    // Render the JSON UI
                    JsonUIRenderer(json = json, onAction = onAction)
                }
            }
            
            @Composable
            fun JsonUIRenderer(
                json: JSONObject,
                onAction: (String) -> Unit = {}
            ) {
                // TODO: Implement JSON to Compose rendering
                // This will be generated by kjui_tools
            }
          KOTLIN
          
          File.write(loader_path, content)
          puts "  Created: JsonUILoader.kt"
        end
        
        def create_main_activity
          source_dir = @config['source_directory'] || 'src/main'
          package_dirs = @package_name.gsub('.', '/')
          activity_path = File.join(source_dir, "kotlin/#{package_dirs}", 'MainActivity.kt')
          
          content = <<~KOTLIN
            package #{@package_name}
            
            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.foundation.layout.fillMaxSize
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import #{@package_name}.ui.theme.KotlinJsonUITheme
            import #{@package_name}.ui.components.JsonUILoader
            
            class MainActivity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        KotlinJsonUITheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                // Load main layout from JSON
                                JsonUILoader(
                                    layoutName = "main",
                                    onAction = { action ->
                                        handleAction(action)
                                    }
                                )
                            }
                        }
                    }
                }
                
                private fun handleAction(action: String) {
                    // Handle actions from JSON UI
                    when (action) {
                        // Add action handlers here
                        else -> println("Unknown action: $action")
                    }
                }
            }
          KOTLIN
          
          File.write(activity_path, content) unless File.exist?(activity_path)
          puts "  Created: MainActivity.kt" unless File.exist?(activity_path)
        end
        
        def update_build_gradle
          puts "Updating build.gradle..."
          
          gradle_file = find_app_gradle_file
          return unless gradle_file
          
          content = File.read(gradle_file)
          
          # Check if Compose is already configured
          unless content.include?('compose')
            puts "  Adding Compose dependencies to build.gradle..."
            
            # Add compose to buildFeatures
            unless content.include?('buildFeatures')
              content.gsub!(/android\s*\{/, "android {\n    buildFeatures {\n        compose = true\n    }")
            end
            
            # Add compose options
            unless content.include?('composeOptions')
              content.gsub!(/android\s*\{/, "android {\n    composeOptions {\n        kotlinCompilerExtensionVersion = \"1.5.7\"\n    }")
            end
            
            # Add Compose BOM
            unless content.include?('androidx.compose:compose-bom')
              dependencies_section = content.match(/dependencies\s*\{(.*?)\}/m)
              if dependencies_section
                new_deps = <<~GRADLE
                    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
                    implementation("androidx.compose.ui:ui")
                    implementation("androidx.compose.ui:ui-tooling-preview")
                    implementation("androidx.compose.material3:material3")
                    implementation("androidx.compose.runtime:runtime")
                    implementation("androidx.activity:activity-compose:1.8.0")
                GRADLE
                
                content.gsub!(/dependencies\s*\{/, "dependencies {\n#{new_deps}")
              end
            end
            
            File.write(gradle_file, content)
            puts "  Updated build.gradle with Compose dependencies"
          else
            puts "  Compose already configured in build.gradle"
          end
        end
        
        def create_hotloader_config
          puts "Creating hotloader configuration..."
          
          # Determine the correct project directory
          project_root = Core::ProjectFinder.project_dir || Dir.pwd
          
          # Check if we're in sample-app
          if File.exist?(File.join(project_root, 'sample-app'))
            assets_dir = File.join(project_root, 'sample-app', 'src', 'main', 'assets')
          else
            source_dir = @config['source_directory'] || 'src/main'
            assets_dir = File.join(project_root, source_dir, 'assets')
          end
          
          FileUtils.mkdir_p(assets_dir)
          
          # Get IP from config or detect it
          ip = if @config['hotloader'] && @config['hotloader']['ip']
                 @config['hotloader']['ip']
               else
                 get_local_ip || '10.0.2.2' # Default to Android emulator IP
               end
          
          port = if @config['hotloader'] && @config['hotloader']['port']
                   @config['hotloader']['port']
                 else
                   8081
                 end
          
          # Create hotloader.json
          hotloader_config_path = File.join(assets_dir, 'hotloader.json')
          hotloader_config = {
            'ip' => ip,
            'port' => port,
            'enabled' => false, # Default to disabled for initial setup
            'websocket_endpoint' => "ws://#{ip}:#{port}",
            'http_endpoint' => "http://#{ip}:#{port}"
          }
          
          File.write(hotloader_config_path, JSON.pretty_generate(hotloader_config))
          puts "  Created: hotloader.json (IP: #{ip}:#{port})"
        end
        
        def setup_network_security
          puts "Setting up network security for hot reload..."
          
          # Determine the correct project directory
          project_root = Core::ProjectFinder.project_dir || Dir.pwd
          
          # Check if we're in sample-app
          if File.exist?(File.join(project_root, 'sample-app'))
            res_dir = File.join(project_root, 'sample-app', 'src', 'main', 'res', 'xml')
            debug_dir = File.join(project_root, 'sample-app', 'src', 'debug')
            manifest_path = File.join(project_root, 'sample-app', 'src', 'main', 'AndroidManifest.xml')
          else
            source_dir = @config['source_directory'] || 'src/main'
            res_dir = File.join(project_root, source_dir, 'res', 'xml')
            debug_dir = File.join(project_root, 'src', 'debug')
            manifest_path = File.join(project_root, source_dir, 'AndroidManifest.xml')
          end
          
          # Create network security config
          FileUtils.mkdir_p(res_dir)
          network_config_path = File.join(res_dir, 'network_security_config.xml')
          
          network_config = <<~XML
            <?xml version="1.0" encoding="utf-8"?>
            <network-security-config>
                <!-- Allow cleartext traffic for hot reload development server -->
                <domain-config cleartextTrafficPermitted="true">
                    <!-- Android emulator localhost -->
                    <domain includeSubdomains="true">10.0.2.2</domain>
                    <!-- Common local network ranges -->
                    <domain includeSubdomains="true">localhost</domain>
                    <domain includeSubdomains="true">127.0.0.1</domain>
                    <!-- Local network IPs (adjust as needed) -->
                    <domain includeSubdomains="true">192.168.0.0/16</domain>
                    <domain includeSubdomains="true">192.168.1.0/24</domain>
                    <domain includeSubdomains="true">192.168.3.0/24</domain>
                    <domain includeSubdomains="true">10.0.0.0/8</domain>
                </domain-config>
                
                <!-- Default configuration for production -->
                <base-config cleartextTrafficPermitted="false">
                    <trust-anchors>
                        <certificates src="system" />
                    </trust-anchors>
                </base-config>
            </network-security-config>
          XML
          
          File.write(network_config_path, network_config)
          puts "  Created: network_security_config.xml"
          
          # Create debug-specific AndroidManifest.xml with both network config and cleartext traffic
          FileUtils.mkdir_p(debug_dir)
          debug_manifest_path = File.join(debug_dir, 'AndroidManifest.xml')
          
          debug_manifest = <<~XML
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools">
            
                <!-- Debug-only configuration for hot reload -->
                <application
                    android:networkSecurityConfig="@xml/network_security_config"
                    android:usesCleartextTraffic="true"
                    tools:targetApi="31">
                </application>
            
            </manifest>
          XML
          
          File.write(debug_manifest_path, debug_manifest)
          puts "  Created: debug/AndroidManifest.xml with cleartext traffic enabled for debug builds only"
        end
        
        def get_local_ip
          # Try to get WiFi IP first (common interface names)
          require 'socket'
          
          Socket.ip_address_list.each do |addr|
            if addr.ipv4? && !addr.ipv4_loopback? && !addr.ipv4_multicast?
              return addr.ip_address
            end
          end
          
          nil
        rescue
          nil
        end
        
        def create_sample_layouts
          puts "Creating sample layouts..."
          
          # Create main.json
          source_dir = @config['source_directory'] || 'src/main'
          main_layout = File.join(source_dir, 'assets/Layouts/main.json')
          
          content = <<~JSON
            {
              "type": "SafeAreaView",
              "background": "#FFFFFF",
              "child": [
                {
                  "type": "View",
                  "orientation": "vertical",
                  "padding": 16,
                  "child": [
                    {
                      "type": "Label",
                      "text": "Welcome to KotlinJsonUI",
                      "fontSize": 24,
                      "fontWeight": "bold",
                      "fontColor": "#000000",
                      "marginBottom": 20
                    },
                    {
                      "type": "Label",
                      "text": "Build native Android UIs with JSON",
                      "fontSize": 16,
                      "fontColor": "#666666",
                      "marginBottom": 30
                    },
                    {
                      "type": "Button",
                      "text": "Get Started",
                      "onclick": "getStarted",
                      "background": "#6200EE",
                      "fontColor": "#FFFFFF",
                      "padding": [12, 24],
                      "cornerRadius": 8
                    }
                  ]
                }
              ],
              "data": [
                {
                  "name": "title",
                  "class": "String",
                  "defaultValue": "'Welcome'"
                }
              ]
            }
          JSON
          
          FileUtils.mkdir_p(File.dirname(main_layout))
          File.write(main_layout, content) unless File.exist?(main_layout)
          puts "  Created: main.json" unless File.exist?(main_layout)
          
          # Create sample style
          source_dir = @config['source_directory'] || 'src/main'
          button_style = File.join(source_dir, 'assets/Styles/primary_button.json')
          
          style_content = <<~JSON
            {
              "background": "#6200EE",
              "fontColor": "#FFFFFF",
              "fontSize": 16,
              "fontWeight": "medium",
              "padding": [12, 24],
              "cornerRadius": 8
            }
          JSON
          
          FileUtils.mkdir_p(File.dirname(button_style))
          File.write(button_style, style_content) unless File.exist?(button_style)
          puts "  Created: primary_button.json style" unless File.exist?(button_style)
        end
        
        def package_path(subpath)
          source_dir = @config['source_directory'] || 'src/main'
          package_dirs = @package_name.gsub('.', '/')
          File.join(source_dir, "kotlin/#{package_dirs}/#{subpath}")
        end
        
        def find_app_gradle_file
          # Look for app/build.gradle or app/build.gradle.kts
          candidates = [
            'app/build.gradle.kts',
            'app/build.gradle',
            'build.gradle.kts',
            'build.gradle'
          ]
          
          project_root = Core::ProjectFinder.project_dir || Dir.pwd
          
          candidates.each do |candidate|
            path = File.join(project_root, candidate)
            return path if File.exist?(path)
          end
          
          nil
        end
      end
    end
  end
end