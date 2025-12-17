# frozen_string_literal: true

require 'json'
require 'fileutils'
require_relative '../../core/config_manager'
require_relative '../../core/project_finder'

module KjuiTools
  module Compose
    module Generators
      class ViewGenerator
        def initialize(name, options = {})
          @name = name
          @options = options
          @config = Core::ConfigManager.load_config
        end

        def generate
          # Parse name for subdirectories
          parts = @name.split('/')
          view_name = parts.last
          subdirectory = parts[0...-1].join('/') if parts.length > 1
          
          # Convert to proper case
          view_class_name = to_pascal_case(view_name)
          json_file_name = to_snake_case(view_name)
          
          # Get directories from config
          source_dir = @config['source_directory'] || 'src/main'
          layouts_dir = @config['layouts_directory'] || 'assets/Layouts'
          view_dir = @config['view_directory'] || 'kotlin/com/example/kotlinjsonui/sample/views'
          viewmodel_dir = @config['viewmodel_directory'] || 'kotlin/com/example/kotlinjsonui/sample/viewmodels'
          data_dir = @config['data_directory'] || 'kotlin/com/example/kotlinjsonui/sample/data'
          package_name = @config['package_name'] || 'com.example.kotlinjsonui.sample'
          
          # Create full paths with subdirectory support
          # Each view gets its own directory (using snake_case for Android)
          view_folder_name = to_snake_case(view_name)
          
          if subdirectory
            json_path = File.join(source_dir, layouts_dir, subdirectory)
            swift_path = File.join(source_dir, view_dir, subdirectory, view_folder_name)
            viewmodel_path = File.join(source_dir, viewmodel_dir, subdirectory)
            data_path = File.join(source_dir, data_dir, subdirectory)
          else
            json_path = File.join(source_dir, layouts_dir)
            # Create a folder for each view (e.g., views/home_view/ for HomeView)
            swift_path = File.join(source_dir, view_dir, view_folder_name)
            viewmodel_path = File.join(source_dir, viewmodel_dir)
            data_path = File.join(source_dir, data_dir)
          end
          
          # Create directories if they don't exist
          FileUtils.mkdir_p(json_path)
          FileUtils.mkdir_p(swift_path)
          FileUtils.mkdir_p(viewmodel_path)
          FileUtils.mkdir_p(data_path)
          
          # Create JSON file
          json_file = File.join(json_path, "#{json_file_name}.json")
          create_json_template(json_file, view_class_name)
          
          # Create Main View file (add View suffix to class name)
          main_kotlin_file = File.join(swift_path, "#{view_class_name}View.kt")
          create_main_view_template(main_kotlin_file, view_class_name, json_file_name, subdirectory, package_name)
          
          # Create Generated View file
          generated_kotlin_file = File.join(swift_path, "#{view_class_name}GeneratedView.kt")
          create_generated_view_template(generated_kotlin_file, view_class_name, json_file_name, subdirectory, package_name)
          
          # Create Data file
          data_file = File.join(data_path, "#{view_class_name}Data.kt")
          create_data_template(data_file, view_class_name, package_name)
          
          # Create ViewModel file
          viewmodel_file = File.join(viewmodel_path, "#{view_class_name}ViewModel.kt")
          create_viewmodel_template(viewmodel_file, view_class_name, json_file_name, subdirectory, package_name)
          
          # Update MainActivity if --root option is specified
          if @options[:root]
            update_main_activity(view_class_name, package_name)
          end
          
          puts "Generated Compose view:"
          puts "  JSON:           #{json_file}"
          puts "  Main View:      #{main_kotlin_file}"
          puts "  Generated View: #{generated_kotlin_file}"
          puts "  Data:           #{data_file}"
          puts "  ViewModel:      #{viewmodel_file}"
          
          if @options[:root]
            puts "  Updated MainActivity to use #{view_class_name}View as root"
          end
          
          puts ""
          puts "Next steps:"
          puts "  1. Edit the JSON layout in #{json_file}"
          puts "  2. Run 'kjui build' to generate the Compose code"
        end

        private

        def to_pascal_case(str)
          # Handle camelCase and PascalCase input
          # First convert to snake_case, then to PascalCase
          snake = str.gsub(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
                     .gsub(/([a-z\d])([A-Z])/, '\1_\2')
                     .downcase
          snake.split(/[_\-]/).map(&:capitalize).join
        end

        def to_snake_case(str)
          str.gsub(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
             .gsub(/([a-z\d])([A-Z])/, '\1_\2')
             .downcase
        end

        def create_json_template(file_path, view_name)
          return if File.exist?(file_path)
          
          template = {
            type: "SafeAreaView",
            background: "#FFFFFF",
            child: [
              {
                type: "View",
                orientation: "vertical",
                padding: 16,
                child: [
                  {
                    type: "Label",
                    text: "@{title}",
                    fontSize: 24,
                    fontWeight: "bold",
                    fontColor: "#000000",
                    marginBottom: 20
                  },
                  {
                    type: "Label",
                    text: "Welcome to #{view_name}",
                    fontSize: 16,
                    fontColor: "#666666",
                    marginBottom: 30
                  },
                  {
                    type: "Button",
                    text: "Get Started",
                    onclick: "onGetStarted",
                    background: "#6200EE",
                    fontColor: "#FFFFFF",
                    padding: [12, 24],
                    cornerRadius: 8
                  }
                ]
              }
            ],
            data: [
              {
                name: "title",
                class: "String",
                defaultValue: "'#{view_name}'"
              }
            ]
          }
          
          File.write(file_path, JSON.pretty_generate(template))
          puts "Created JSON template: #{file_path}"
        end

        def create_main_view_template(file_path, view_name, json_name, subdirectory, package_name)
          return if File.exist?(file_path)
          
          package_parts = package_name.split('.')
          # Each view has its own package (e.g., com.example.views.home_view)
          view_folder_name = to_snake_case(view_name)
          view_package = subdirectory ? "#{package_name}.views.#{subdirectory.gsub('/', '.')}.#{view_folder_name}" : "#{package_name}.views.#{view_folder_name}"
          
          template = <<~KOTLIN
            package #{view_package}

            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.collectAsState
            import androidx.compose.runtime.getValue
            import androidx.lifecycle.viewmodel.compose.viewModel
            import #{package_name}.viewmodels.#{view_name}ViewModel

            @Composable
            fun #{view_name}View(
                viewModel: #{view_name}ViewModel = viewModel()
            ) {
                val data by viewModel.data.collectAsState()

                #{view_name}GeneratedView(data = data)
            }
          KOTLIN
          
          File.write(file_path, template)
          puts "Created Main View template: #{file_path}"
        end
        
        def create_generated_view_template(file_path, view_name, json_name, subdirectory, package_name)
          return if File.exist?(file_path)
          
          json_reference = subdirectory ? "#{subdirectory}/#{json_name}" : json_name
          # Each view has its own package (using snake_case for folder)
          view_folder_name = to_snake_case(view_name)
          view_package = subdirectory ? "#{package_name}.views.#{subdirectory.gsub('/', '.')}.#{view_folder_name}" : "#{package_name}.views.#{view_folder_name}"
          
          template = <<~KOTLIN
            package #{view_package}

            import androidx.compose.foundation.background
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.lazy.LazyColumn
            import androidx.compose.foundation.lazy.LazyRow
            import androidx.compose.material3.*
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.text.style.TextAlign
            import androidx.compose.ui.unit.dp
            import androidx.compose.ui.unit.sp
            import #{package_name}.data.#{view_name}Data

            @Composable
            fun #{view_name}GeneratedView(
                data: #{view_name}Data
            ) {
                // Generated Compose code from #{json_reference}.json
                // This will be updated when you run 'kjui build'
                // >>> GENERATED_CODE_START
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Run 'kjui build' to generate Compose code",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                // >>> GENERATED_CODE_END
            }
          KOTLIN
          
          File.write(file_path, template)
          puts "Created Generated View template: #{file_path}"
        end
        
        def create_data_template(file_path, view_name, package_name)
          return if File.exist?(file_path)
          
          data_package = "#{package_name}.data"
          
          template = <<~KOTLIN
            package #{data_package}

            data class #{view_name}Data(
                var title: String = "#{view_name}",

                // Action closures (called from generated views)
                var onGetStarted: (() -> Unit)? = null
                // Add more data properties as needed based on your JSON structure
            ) {
                // Update properties from map
                fun update(map: Map<String, Any>) {
                    map["title"]?.let {
                        if (it is String) title = it
                    }
                }

                // Convert to map for dynamic mode
                fun toMap(): Map<String, Any> {
                    return mutableMapOf(
                        "title" to title
                    )
                }
            }
          KOTLIN
          
          File.write(file_path, template)
          puts "Created Data template: #{file_path}"
        end
        
        def create_viewmodel_template(file_path, view_name, json_name, subdirectory, package_name)
          return if File.exist?(file_path)
          
          json_reference = subdirectory ? "#{subdirectory}/#{json_name}" : json_name
          viewmodel_package = "#{package_name}.viewmodels"
          
          template = <<~KOTLIN
            package #{viewmodel_package}

            import android.app.Application
            import androidx.lifecycle.AndroidViewModel
            import kotlinx.coroutines.flow.MutableStateFlow
            import kotlinx.coroutines.flow.StateFlow
            import kotlinx.coroutines.flow.asStateFlow
            import #{package_name}.data.#{view_name}Data

            class #{view_name}ViewModel(application: Application) : AndroidViewModel(application) {
                // JSON file reference for hot reload
                val jsonFileName = "#{json_reference}"
                
                // Data model
                private val _data = MutableStateFlow(#{view_name}Data())
                val data: StateFlow<#{view_name}Data> = _data.asStateFlow()
                
                // Action handlers
                fun onGetStarted() {
                    // Handle button tap
                }
                
                // Add more action handlers as needed
                fun updateData(updates: Map<String, Any>) {
                    _data.value.update(updates)
                    _data.value = _data.value.copy() // Trigger recomposition
                }
            }
          KOTLIN
          
          File.write(file_path, template)
          puts "Created ViewModel template: #{file_path}"
        end
        
        def update_main_activity(view_name, package_name)
          source_dir = @config['source_directory'] || 'src/main'
          
          # Find MainActivity file
          activity_files = Dir.glob(File.join(source_dir, '**/MainActivity.kt'))
          if activity_files.empty?
            puts "Warning: Could not find MainActivity.kt file to update"
            return
          end
          
          activity_file = activity_files.first
          content = File.read(activity_file)
          
          # Add import for the new view (view is in its own package with snake_case folder)
          view_folder_name = to_snake_case(view_name)
          import_line = "import #{package_name}.views.#{view_folder_name}.#{view_name}View"
          unless content.include?(import_line)
            # Find the last import line and add after it
            if content =~ /^((?:.*\nimport .*\n)+)/m
              imports_block = $1
              # Add the new import after the last import
              new_imports = imports_block.chomp + "\n#{import_line}\n"
              content.sub!(imports_block, new_imports)
            end
          end
          
          # Update setContent - look for the pattern and replace the content inside
          updated = false
          
          # Pattern 1: Full setContent block with theme
          if content =~ /setContent\s*\{[\s\S]*?\n\s{8}\}/m
            content.gsub!(/setContent\s*\{[\s\S]*?\n\s{8}\}/m) do
              <<~KOTLIN.chomp
                setContent {
                            KotlinJsonUITheme {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    #{view_name}View()
                                }
                            }
                        }
              KOTLIN
            end
            updated = true
          # Pattern 2: Simple setContent
          elsif content =~ /setContent\s*\{[^}]*\}/m
            content.gsub!(/setContent\s*\{[^}]*\}/m) do
              <<~KOTLIN.chomp
                setContent {
                            #{view_name}View()
                        }
              KOTLIN
            end
            updated = true
          end
          
          if updated
            File.write(activity_file, content)
            puts "Updated MainActivity to use #{view_name}View as root"
          else
            puts "Warning: Could not update MainActivity automatically"
            puts "Please manually update your MainActivity to use #{view_name}View()"
          end
        end
      end
    end
  end
end