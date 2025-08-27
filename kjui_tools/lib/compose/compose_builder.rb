# frozen_string_literal: true

require 'json'
require 'fileutils'
require 'set'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative '../core/logger'
require_relative 'style_loader'
require_relative 'data_model_updater'
require_relative 'helpers/import_manager'
require_relative 'helpers/modifier_builder'
require_relative 'components/text_component'
require_relative 'components/button_component'
require_relative 'components/textfield_component'
require_relative 'components/container_component'
require_relative 'components/image_component'
require_relative 'components/scrollview_component'
require_relative 'components/switch_component'
require_relative 'components/slider_component'
require_relative 'components/progress_component'
require_relative 'components/selectbox_component'
require_relative 'components/checkbox_component'
require_relative 'components/radio_component'
require_relative 'components/segment_component'
require_relative 'components/networkimage_component'
require_relative 'components/circleimage_component'
require_relative 'components/indicator_component'
require_relative 'components/textview_component'
require_relative 'components/collection_component'
require_relative 'components/table_component'
require_relative 'components/web_component'
require_relative 'components/gradientview_component'
require_relative 'components/blurview_component'

module KjuiTools
  module Compose
    # Refactored ComposeBuilder - under 300 lines
    class ComposeBuilder
      def initialize
        @config = Core::ConfigManager.load_config
        @source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
        source_directory = @config['source_directory'] || 'src/main'
        @layouts_dir = File.join(@source_path, source_directory, @config['layouts_directory'] || 'assets/Layouts')
        @view_dir = File.join(@source_path, source_directory, @config['view_directory'] || 'kotlin/views')
        @package_name = @config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.app'
        
        FileUtils.mkdir_p(@view_dir) unless File.exist?(@view_dir)
      end
      
      def build(options = {})
        json_files = Dir.glob(File.join(@layouts_dir, '**/*.json'))
        
        if json_files.empty?
          Core::Logger.warn "No JSON files found in #{@layouts_dir}"
          return
        end
        
        # Update data models first
        data_updater = DataModelUpdater.new
        data_updater.update_data_models
        
        # Build each JSON file
        json_files.each { |file| build_file(file) }
      end
      
      def build_file(json_file)
        base_name = File.basename(json_file, '.json')
        snake_case_name = to_snake_case(base_name)
        pascal_case_name = to_pascal_case(base_name)
        
        begin
          json_content = File.read(json_file)
          json_data = JSON.parse(json_content)
          json_data = StyleLoader.load_and_merge(json_data)
          
          @required_imports = Set.new
          @included_views = Set.new
          @cell_views = Set.new
          
          # Find the GeneratedView file
          generated_view_file = File.join(@view_dir, snake_case_name, "#{pascal_case_name}GeneratedView.kt")
          
          if File.exist?(generated_view_file)
            update_generated_file(generated_view_file, json_data)
          else
            Core::Logger.warn "GeneratedView file not found: #{generated_view_file}"
          end
          
        rescue JSON::ParserError => e
          Core::Logger.error "Failed to parse #{json_file}: #{e.message}"
        rescue => e
          Core::Logger.error "Failed to process #{json_file}: #{e.message}"
        end
      end
      
      private
      
      def generate_component(json_data, depth = 0, parent_type = nil)
        return "" unless json_data.is_a?(Hash)
        
        component_type = json_data['type'] || 'View'
        
        # Handle includes
        return generate_include(json_data, depth) if json_data['include']
        
        # Generate component based on type
        case component_type
        when 'ScrollView', 'Scroll'
          result = Components::ScrollViewComponent.generate(json_data, depth, @required_imports, parent_type)
          handle_container_result(result, depth, parent_type)
        when 'SafeAreaView'
          generate_safe_area_view(json_data, depth)
        when 'View'
          result = Components::ContainerComponent.generate(json_data, depth, @required_imports, parent_type)
          handle_container_result(result, depth, parent_type)
        when 'Text', 'Label'
          Components::TextComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Button'
          Components::ButtonComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Image'
          Components::ImageComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'TextField'
          Components::TextFieldComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Switch', 'Toggle'
          Components::SwitchComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Slider'
          Components::SliderComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Progress'
          Components::ProgressComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'SelectBox'
          Components::SelectBoxComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Check', 'Checkbox'
          Components::CheckboxComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Radio'
          Components::RadioComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Segment'
          Components::SegmentComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'NetworkImage'
          Components::NetworkImageComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'CircleImage'
          Components::CircleImageComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Indicator'
          Components::IndicatorComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'TextView'
          Components::TextViewComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Collection'
          # Extract cell classes for imports
          cell_classes = json_data['cellClasses'] || []
          cell_classes.each do |cell_class|
            @cell_views&.add(cell_class)
          end
          Components::CollectionComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Table'
          Components::TableComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'Web'
          Components::WebComponent.generate(json_data, depth, @required_imports, parent_type)
        when 'GradientView'
          result = Components::GradientviewComponent.generate(json_data, depth, @required_imports, parent_type)
          handle_container_result(result, depth, parent_type)
        when 'BlurView'
          result = Components::BlurviewComponent.generate(json_data, depth, @required_imports, parent_type)
          handle_container_result(result, depth, parent_type)
        when 'Spacer'
          "Spacer(modifier = Modifier.height(#{json_data['height'] || 8}.dp))"
        else
          "// TODO: Implement component type: #{component_type}"
        end
      end
      
      def handle_container_result(result, depth, parent_type = nil)
        if result.is_a?(Hash)
          code = result[:code]
          children = result[:children] || []
          layout_type = result[:layout_type] || parent_type
          
          children.each do |child|
            child_code = generate_component(child, depth + 1, layout_type)
            code += "\n" + child_code unless child_code.empty?
          end
          
          code += result[:closing] if result[:closing]
          code
        else
          result
        end
      end
      
      def generate_safe_area_view(json_data, depth)
        code = indent("Box(", depth)
        
        modifiers = ["Modifier", ".fillMaxSize()", ".systemBarsPadding()"]
        modifiers.concat(Helpers::ModifierBuilder.build_padding(json_data))
        modifiers.concat(Helpers::ModifierBuilder.build_background(json_data, @required_imports))
        
        code += Helpers::ModifierBuilder.format(modifiers, depth)
        code += "\n" + indent(") {", depth)
        
        children = json_data['child'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 1)
          code += "\n" + child_code unless child_code.empty?
        end
        
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_include(json_data, depth)
        include_name = json_data['include']
        pascal_name = to_pascal_case(include_name)
        snake_name = to_snake_case(include_name)
        
        # Check if we should use DynamicView
        use_dynamic = json_data['dynamic'] == true
        
        # Track this included view for imports
        @included_views&.add(snake_name) unless use_dynamic
        
        # Track required imports for LaunchedEffect if we have data bindings
        has_data_bindings = false
        
        # Check if there's data or shared_data to pass
        include_data = json_data['data'] || {}
        shared_data = json_data['shared_data'] || {}
        
        # Check for @{} bindings in data
        include_data.each do |key, value|
          if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
            has_data_bindings = true
            unless use_dynamic
              @required_imports.add(:LaunchedEffect)
              @required_imports.add(:remember)
            end
            break
          end
        end
        
        # If using dynamic view, generate DynamicView call
        if use_dynamic
          return generate_dynamic_include(json_data, depth, include_data, shared_data, has_data_bindings)
        end
        
        # Generate unique instance ID for this include
        instance_id = "#{to_camel_case(include_name)}Instance#{depth}"
        
        code = ""
        
        # Create a remember block for the ViewModel instance
        code += indent("val context = LocalContext.current", depth)
        code += "\n"
        code += indent("val #{instance_id} = remember { #{pascal_name}ViewModel(context.applicationContext as Application) }", depth)
        code += "\n"
        
        # If we have data bindings, add LaunchedEffect to update on parent data changes
        if has_data_bindings || shared_data.any?
          code += "\n" + indent("// Update included view when parent data changes", depth)
          code += "\n" + indent("LaunchedEffect(", depth)
          
          # Add keys for all bound variables
          keys = []
          include_data.each do |key, value|
            if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
              variable = $1
              keys << "data.#{variable}"
            end
          end
          shared_data.each do |key, value|
            if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
              variable = $1
              keys << "data.#{variable}"
            end
          end
          
          if keys.any?
            code += keys.join(", ")
          else
            code += "Unit"
          end
          
          code += ") {"
          code += "\n" + indent("val updates = mutableMapOf<String, Any>()", depth + 1)
          
          # Process data (one-way binding from parent to child)
          include_data.each do |key, value|
            if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
              # This is a data binding reference to parent data
              variable = $1
              code += "\n" + indent("updates[\"#{key}\"] = data.#{variable}", depth + 1)
            else
              # This is a static value
              formatted_value = format_value_for_kotlin(value)
              code += "\n" + indent("updates[\"#{key}\"] = #{formatted_value}", depth + 1)
            end
          end
          
          # Process shared_data (two-way binding)
          if shared_data.any?
            code += "\n" + indent("// Shared data for two-way binding", depth + 1)
            shared_data.each do |key, value|
              if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
                # This creates a two-way binding
                variable = $1
                code += "\n" + indent("updates[\"#{key}\"] = data.#{variable}", depth + 1)
                # TODO: Also need to update parent when child changes
              else
                # Static value for shared_data
                formatted_value = format_value_for_kotlin(value)
                code += "\n" + indent("updates[\"#{key}\"] = #{formatted_value}", depth + 1)
              end
            end
          end
          
          code += "\n" + indent("#{instance_id}.updateData(updates)", depth + 1)
          code += "\n" + indent("}", depth)
        end
        
        # Generate the included view call
        code += "\n" + indent("#{pascal_name}View(", depth)
        code += "\n" + indent("viewModel = #{instance_id}", depth + 1)
        code += "\n" + indent(")", depth)
        
        code
      end
      
      def generate_dynamic_include(json_data, depth, include_data, shared_data, has_data_bindings)
        include_name = json_data['include']
        
        # Add required imports for SafeDynamicView
        @required_imports.add(:safe_dynamic_view)
        
        code = ""
        
        # Build data map from bindings and current data
        code += indent("// Build data map with bindings", depth)
        code += "\n" + indent("val dynamicData = mutableMapOf<String, Any>()", depth)
        
        # Add all current data values
        code += "\n" + indent("// Add current data values", depth)
        code += "\n" + indent("data.forEach { (key, value) ->", depth)
        code += "\n" + indent("dynamicData[key] = value", depth + 1)
        code += "\n" + indent("}", depth)
        
        # Process include_data bindings
        if include_data.any?
          code += "\n" + indent("// Process include data bindings", depth)
          include_data.each do |key, value|
            if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
              # This is a data binding reference to parent data
              variable = $1
              code += "\n" + indent("data[\"#{variable}\"]?.let { dynamicData[\"#{key}\"] = it }", depth)
            else
              # This is a static value
              formatted_value = format_value_for_kotlin(value)
              code += "\n" + indent("dynamicData[\"#{key}\"] = #{formatted_value}", depth)
            end
          end
        end
        
        # Process shared_data bindings
        if shared_data.any?
          code += "\n" + indent("// Process shared data bindings", depth)
          shared_data.each do |key, value|
            if value.is_a?(String) && value.match(/@\{([^}]+)\}/)
              # This creates a two-way binding
              variable = $1
              code += "\n" + indent("data[\"#{variable}\"]?.let { dynamicData[\"#{key}\"] = it }", depth)
            else
              # Static value for shared_data
              formatted_value = format_value_for_kotlin(value)
              code += "\n" + indent("dynamicData[\"#{key}\"] = #{formatted_value}", depth)
            end
          end
        end
        
        # Add all viewModel methods as functions to the data map
        code += "\n" + indent("// Add viewModel methods as event handlers", depth)
        code += "\n" + indent("// Note: Add specific method references as needed", depth)
        code += "\n" + indent("// Example: dynamicData[\"onButtonClick\"] = { viewModel.onButtonClick() }", depth)
        
        # Call SafeDynamicView
        code += "\n" + indent("// Render dynamic view", depth)
        code += "\n" + indent("SafeDynamicView(", depth)
        code += "\n" + indent("layoutName = \"#{include_name}\",", depth + 1)
        code += "\n" + indent("data = dynamicData", depth + 1)
        code += "\n" + indent(")", depth)
        
        code
      end
      
      def format_value_for_kotlin(value)
        case value
        when String
          "\"#{value.gsub('"', '\\"')}\""
        when Integer
          value.to_s
        when Float
          "#{value}f"
        when TrueClass, FalseClass
          value.to_s
        when nil
          "null"
        else
          "\"#{value}\""
        end
      end
      
      def update_generated_file(file_path, json_data)
        existing_content = File.read(file_path)
        
        if existing_content.include?('// >>> GENERATED_CODE_START') && 
           existing_content.include?('// >>> GENERATED_CODE_END')
          
          # Extract the layout name from file path
          layout_name = File.basename(File.dirname(file_path))
          
          # Generate both static and dynamic versions
          static_content = generate_component(json_data, 1)
          dynamic_content = generate_dynamic_view_content(layout_name, json_data, 1)
          
          # Create content that switches based on DynamicModeManager
          composable_content = generate_mode_aware_content(layout_name, static_content, dynamic_content, 1)
          
          updated_content = existing_content.gsub(
            /\/\/ >>> GENERATED_CODE_START.*?\/\/ >>> GENERATED_CODE_END/m,
            "// >>> GENERATED_CODE_START\n#{composable_content}    // >>> GENERATED_CODE_END"
          )
          
          updated_content = update_imports(updated_content)
          File.write(file_path, updated_content)
          Core::Logger.success "Updated: #{file_path}"
        else
          Core::Logger.warn "Generated code markers not found in #{file_path}"
        end
      end
      
      def generate_mode_aware_content(layout_name, static_content, dynamic_content, depth)
        indent_str = "    " * depth
        
        code = ""
        code += "#{indent_str}// Check if Dynamic Mode is active\n"
        code += "#{indent_str}if (DynamicModeManager.isActive()) {\n"
        code += "#{indent_str}    // Dynamic Mode - use SafeDynamicView for real-time updates\n"
        code += dynamic_content
        code += "#{indent_str}} else {\n"
        code += "#{indent_str}    // Static Mode - use generated code\n"
        code += "    #{static_content}"
        code += "#{indent_str}}\n"
        
        # Add required imports for DynamicModeManager
        @required_imports.add(:dynamic_mode_manager)
        @required_imports.add(:safe_dynamic_view)
        
        code
      end
      
      def generate_dynamic_view_content(layout_name, json_data, depth)
        indent_str = "    " * depth
        
        code = ""
        code += "#{indent_str}    SafeDynamicView(\n"
        code += "#{indent_str}        layoutName = \"#{layout_name}\",\n"
        code += "#{indent_str}        data = data.toMap(viewModel),\n"
        code += "#{indent_str}        fallback = {\n"
        code += "#{indent_str}            // Show error or loading state when dynamic view is not available\n"
        code += "#{indent_str}            Box(\n"
        code += "#{indent_str}                modifier = Modifier.fillMaxSize(),\n"
        code += "#{indent_str}                contentAlignment = Alignment.Center\n"
        code += "#{indent_str}            ) {\n"
        code += "#{indent_str}                Text(\n"
        code += "#{indent_str}                    text = \"Dynamic view not available\",\n"
        code += "#{indent_str}                    color = Color.Gray\n"
        code += "#{indent_str}                )\n"
        code += "#{indent_str}            }\n"
        code += "#{indent_str}        },\n"
        code += "#{indent_str}        onError = { error ->\n"
        code += "#{indent_str}            // Log error or show error UI\n"
        code += "#{indent_str}            android.util.Log.e(\"DynamicView\", \"Error loading #{layout_name}: \\$error\")\n"
        code += "#{indent_str}        },\n"
        code += "#{indent_str}        onLoading = {\n"
        code += "#{indent_str}            // Show loading indicator\n"
        code += "#{indent_str}            Box(\n"
        code += "#{indent_str}                modifier = Modifier.fillMaxSize(),\n"
        code += "#{indent_str}                contentAlignment = Alignment.Center\n"
        code += "#{indent_str}            ) {\n"
        code += "#{indent_str}                CircularProgressIndicator()\n"
        code += "#{indent_str}            }\n"
        code += "#{indent_str}        }\n"
        code += "#{indent_str}    ) { jsonContent ->\n"
        code += "#{indent_str}        // Parse and render the dynamic JSON content\n"
        code += "#{indent_str}        // This will be handled by the DynamicView implementation\n"
        code += "#{indent_str}    }\n"
        
        # Add required imports
        @required_imports.add(:circular_progress_indicator)
        @required_imports.add(:box)
        
        code
      end
      
      def update_imports(content)
        imports_map = Helpers::ImportManager.get_imports_map
        
        imports_to_add = []
        @required_imports.each do |import_type|
          import_lines = imports_map[import_type]
          if import_lines
            if import_lines.is_a?(Array)
              imports_to_add.concat(import_lines)
            else
              imports_to_add << import_lines
            end
          end
        end
        
        # Add imports for included views
        if @included_views && @included_views.any?
          # Add necessary imports for creating ViewModels
          imports_to_add << "import android.app.Application" unless imports_to_add.include?("import android.app.Application")
          imports_to_add << "import androidx.compose.ui.platform.LocalContext" unless imports_to_add.include?("import androidx.compose.ui.platform.LocalContext")
          
          @included_views.each do |view_name|
            pascal_name = to_pascal_case(view_name)
            view_import = "import #{@package_name}.views.#{view_name}.#{pascal_name}View"
            data_import = "import #{@package_name}.data.#{pascal_name}Data"
            viewmodel_import = "import #{@package_name}.viewmodels.#{pascal_name}ViewModel"
            
            imports_to_add << view_import unless imports_to_add.include?(view_import)
            imports_to_add << data_import unless imports_to_add.include?(data_import)
            imports_to_add << viewmodel_import unless imports_to_add.include?(viewmodel_import)
          end
        end
        
        # Add imports for cell views (used in Collection components)
        if @cell_views && @cell_views.any?
          # Add necessary imports for creating ViewModels in collections
          imports_to_add << "import androidx.lifecycle.viewmodel.compose.viewModel" unless imports_to_add.include?("import androidx.lifecycle.viewmodel.compose.viewModel")
          
          # First, remove any old/incorrect cell view imports
          lines = content.split("\n")
          @cell_views.each do |cell_class|
            snake_name = to_snake_case(cell_class)
            # Remove any existing imports with incorrect capitalization
            lines.reject! { |line| line.match(/^import #{Regexp.escape(@package_name)}\.views\.#{Regexp.escape(snake_name)}\.\w+View$/) }
            lines.reject! { |line| line.match(/^import #{Regexp.escape(@package_name)}\.data\.\w+Data$/) && line.downcase.include?(cell_class.downcase) }
            lines.reject! { |line| line.match(/^import #{Regexp.escape(@package_name)}\.viewmodels\.\w+ViewModel$/) && line.downcase.include?(cell_class.downcase) }
          end
          content = lines.join("\n")
          
          @cell_views.each do |cell_class|
            # Cell class names are already in PascalCase (e.g., "ProductCell")
            # Convert to snake_case for folder path
            snake_name = to_snake_case(cell_class)
            # Keep the original cell class name for the class itself
            
            # Add imports for the cell view and data
            view_import = "import #{@package_name}.views.#{snake_name}.#{cell_class}View"
            data_import = "import #{@package_name}.data.#{cell_class}Data"
            viewmodel_import = "import #{@package_name}.viewmodels.#{cell_class}ViewModel"
            
            imports_to_add << view_import unless imports_to_add.include?(view_import)
            imports_to_add << data_import unless imports_to_add.include?(data_import)
            imports_to_add << viewmodel_import unless imports_to_add.include?(viewmodel_import)
          end
        end
        
        if imports_to_add.any?
          lines = content.split("\n")
          package_index = lines.find_index { |line| line.start_with?("package ") }
          
          if package_index
            last_import_index = lines.each_with_index.select { |line, i| 
              i > package_index && line.start_with?("import ")
            }.map(&:last).max || package_index
            
            imports_to_add.each do |import|
              unless content.include?(import)
                lines.insert(last_import_index + 1, import)
                last_import_index += 1
              end
            end
            
            content = lines.join("\n")
          end
        end
        
        content
      end
      
      def process_data_binding(text)
        return quote(text) unless text.is_a?(String)
        
        if text.match(/@\{([^}]+)\}/)
          variable = $1
          if variable.include?(' ?? ')
            var_name = variable.split(' ?? ')[0].strip
            "\"\${data.#{var_name}}\""
          else
            "\"\${data.#{variable}}\""
          end
        else
          quote(text)
        end
      end
      
      def quote(text)
        # Escape special characters properly
        escaped = text.gsub('\\', '\\\\\\\\')  # Escape backslashes first
                     .gsub('"', '\\"')           # Escape quotes
                     .gsub("\n", '\\n')           # Escape newlines
                     .gsub("\r", '\\r')           # Escape carriage returns
                     .gsub("\t", '\\t')           # Escape tabs
        "\"#{escaped}\""
      end
      
      def indent(text, level)
        return text if level == 0
        spaces = '    ' * level
        text.split("\n").map { |line| 
          line.empty? ? line : spaces + line 
        }.join("\n")
      end
      
      def to_pascal_case(str)
        str.split(/[_\-]/).map(&:capitalize).join
      end
      
      def to_camel_case(str)
        pascal = to_pascal_case(str)
        pascal[0].downcase + pascal[1..-1]
      end
      
      def to_snake_case(str)
        str.gsub(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
           .gsub(/([a-z\d])([A-Z])/, '\1_\2')
           .downcase
      end
    end
  end
end