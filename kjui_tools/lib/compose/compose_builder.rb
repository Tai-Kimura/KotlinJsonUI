# frozen_string_literal: true

require 'json'
require 'fileutils'
require 'set'
require_relative '../core/config_manager'
require_relative '../core/project_finder'
require_relative '../core/logger'
require_relative 'style_loader'
require_relative 'data_model_updater'

module KjuiTools
  module Compose
    class ComposeBuilder
      def initialize
        @config = Core::ConfigManager.load_config
        @source_path = Core::ProjectFinder.get_full_source_path || Dir.pwd
        source_directory = @config['source_directory'] || 'src/main'
        @layouts_dir = File.join(@source_path, source_directory, @config['layouts_directory'] || 'assets/Layouts')
        @view_dir = File.join(@source_path, source_directory, @config['view_directory'] || 'kotlin/views')
        @package_name = @config['package_name'] || Core::ProjectFinder.get_package_name || 'com.example.app'
        
        # Create view directory if it doesn't exist
        FileUtils.mkdir_p(@view_dir) unless File.exist?(@view_dir)
      end
      
      def build(options = {})
        # Process all JSON files in Layouts directory
        json_files = Dir.glob(File.join(@layouts_dir, '**/*.json'))
        
        if json_files.empty?
          Core::Logger.warn "No JSON files found in #{@layouts_dir}"
          return
        end
        
        # Update data models first
        data_updater = DataModelUpdater.new
        data_updater.update_data_models
        
        # Build each JSON file
        json_files.each do |json_file|
          build_file(json_file)
        end
      end
      
      def build_file(json_file)
        relative_path = Pathname.new(json_file).relative_path_from(Pathname.new(@layouts_dir)).to_s
        base_name = File.basename(json_file, '.json')
        snake_case_name = to_snake_case(base_name)
        pascal_case_name = to_pascal_case(base_name)
        
        begin
          # Read and parse JSON
          json_content = File.read(json_file)
          json_data = JSON.parse(json_content)
          
          # Load and merge styles
          json_data = StyleLoader.load_and_merge(json_data)
          
          # Initialize imports collector
          @required_imports = Set.new
          
          # Generate Compose code
          compose_code = generate_compose_code(base_name, json_data)
          
          # Find the GeneratedView file for this view (in snake_case folder)
          generated_view_file = File.join(@view_dir, snake_case_name, "#{pascal_case_name}GeneratedView.kt")
          
          if File.exist?(generated_view_file)
            # Read existing file
            existing_content = File.read(generated_view_file)
            
            # Replace content between GENERATED_CODE_START and GENERATED_CODE_END markers
            if existing_content.include?('// >>> GENERATED_CODE_START') && existing_content.include?('// >>> GENERATED_CODE_END')
              # Extract the generated composable content
              composable_content = extract_composable_content(json_data)
              
              # Replace the content between markers
              updated_content = existing_content.gsub(
                /\/\/ >>> GENERATED_CODE_START.*?\/\/ >>> GENERATED_CODE_END/m,
                "// >>> GENERATED_CODE_START\n    #{composable_content}    // >>> GENERATED_CODE_END"
              )
              
              # Update imports
              updated_content = update_imports(updated_content)
              
              File.write(generated_view_file, updated_content)
              Core::Logger.success "Updated: #{generated_view_file}"
            else
              Core::Logger.warn "Generated code markers not found in #{generated_view_file}"
            end
          else
            Core::Logger.warn "GeneratedView file not found: #{generated_view_file}"
          end
          
        rescue JSON::ParserError => e
          Core::Logger.error "Failed to parse #{json_file}: #{e.message}"
        rescue => e
          Core::Logger.error "Failed to process #{json_file}: #{e.message}"
          Core::Logger.debug e.backtrace.join("\n")
        end
      end
      
      private
      
      def generate_compose_code(view_name, json_data)
        pascal_name = to_pascal_case(view_name)
        
        code = <<~KOTLIN
        package #{@package_name}.views
        
        import androidx.compose.foundation.layout.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.unit.sp
        import #{@package_name}.data.#{pascal_name}Data
        import #{@package_name}.viewmodels.#{pascal_name}ViewModel
        import com.kotlinjsonui.compose.ComponentFactory
        
        @Composable
        fun #{pascal_name}View(
            viewModel: #{pascal_name}ViewModel = remember { #{pascal_name}ViewModel() },
            data: #{pascal_name}Data = #{pascal_name}Data()
        ) {
            val currentData = remember { mutableStateOf(data) }
            
            // Build UI from JSON
        KOTLIN
        
        # Generate component tree
        component_code = generate_component(json_data, 1)
        code += indent(component_code, 1)
        
        code += "}\n"
        code
      end
      
      def generate_component(json_data, depth = 0)
        return "" unless json_data.is_a?(Hash)
        
        component_type = json_data['type'] || 'View'
        
        # Handle includes
        if json_data['include']
          return generate_include(json_data, depth)
        end
        
        # Generate component based on type
        case component_type
        when 'ScrollView'
          generate_scroll_view(json_data, depth)
        when 'SafeAreaView'
          generate_safe_area_view(json_data, depth)
        when 'View', 'VStack', 'HStack', 'ZStack'
          generate_container(json_data, depth)
        when 'Text', 'Label'
          generate_text(json_data, depth)
        when 'Button'
          generate_button(json_data, depth)
        when 'Image'
          generate_image(json_data, depth)
        when 'TextField'
          generate_text_field(json_data, depth)
        when 'Spacer'
          "Spacer(modifier = Modifier.height(#{json_data['height'] || 8}.dp))"
        else
          "// TODO: Implement component type: #{component_type}"
        end
      end
      
      def generate_scroll_view(json_data, depth)
        orientation = json_data['orientation'] || 'vertical'
        
        if orientation == 'horizontal'
          @required_imports.add(:lazy_row) if @required_imports
          code = indent("LazyRow(", depth)
        else
          @required_imports.add(:lazy_column) if @required_imports
          code = indent("LazyColumn(", depth)
        end
        
        # Build modifier chain
        modifiers = []
        if json_data['width'] == 'matchParent'
          modifiers << ".fillMaxWidth()"
        elsif json_data['width']
          modifiers << ".width(#{json_data['width']}.dp)"
        end
        
        if json_data['height'] == 'matchParent'
          modifiers << ".fillMaxHeight()"
        elsif json_data['height']
          modifiers << ".height(#{json_data['height']}.dp)"
        end
        
        if json_data['padding']
          modifiers << ".padding(#{json_data['padding']}.dp)"
        end
        
        if json_data['background']
          @required_imports.add(:background) if @required_imports
          modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
        end
        
        # Format modifiers
        if modifiers.any?
          code += "\n" + indent("modifier = Modifier", depth + 1)
          modifiers.each do |mod|
            code += "\n" + indent("    #{mod}", depth + 1)
          end
        end
        
        code += "\n" + indent(") {", depth)
        code += "\n" + indent("item {", depth + 1)
        
        # 'child' is the official attribute (always an array) per wiki
        children = json_data['child'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 2)
          code += "\n" + child_code unless child_code.empty?
        end
        
        code += "\n" + indent("}", depth + 1)
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_safe_area_view(json_data, depth)
        # In Compose, SafeAreaView is typically handled by Scaffold or Box with systemBarsPadding
        code = "Box("
        
        # Add modifiers
        modifiers = []
        modifiers << "Modifier"
        modifiers << "fillMaxSize()"
        modifiers << "systemBarsPadding()"
        
        if json_data['padding']
          modifiers << "padding(#{json_data['padding']}.dp)"
        end
        
        if json_data['background']
          modifiers << "background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
        end
        
        code += "\n" + indent("modifier = #{modifiers.join('.')}", depth + 1)
        code += "\n" + indent(") {", depth)
        
        # 'child' is the official attribute (always an array) per wiki
        children = json_data['child'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 1)
          code += "\n" + child_code unless child_code.empty?
        end
        
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_container(json_data, depth)
        container_type = json_data['type'] || 'View'
        orientation = json_data['orientation']
        
        # Determine layout type based on type or orientation
        layout = case container_type
        when 'VStack'
          'Column'
        when 'HStack'
          'Row'
        when 'ZStack'
          'Box'
        when 'View'
          if orientation == 'horizontal'
            'Row'
          elsif orientation == 'vertical'
            'Column'
          else
            'Box'
          end
        else
          'Box'
        end
        
        code = indent("#{layout}(", depth)
        
        # Build modifier chain
        modifiers = []
        
        # Handle width
        if json_data['width'] == 'matchParent'
          modifiers << ".fillMaxWidth()"
        elsif json_data['width'] == 'wrapContent'
          modifiers << ".wrapContentWidth()"
        elsif json_data['width']
          modifiers << ".width(#{json_data['width']}.dp)"
        end
        
        # Handle height
        if json_data['height'] == 'matchParent'
          modifiers << ".fillMaxHeight()"
        elsif json_data['height'] == 'wrapContent'
          modifiers << ".wrapContentHeight()"
        elsif json_data['height']
          modifiers << ".height(#{json_data['height']}.dp)"
        end
        
        # Padding and margins
        if json_data['padding']
          modifiers << ".padding(#{json_data['padding']}.dp)"
        end
        
        # Handle individual margins (official SwiftJsonUI attributes)
        if json_data['topMargin']
          modifiers << ".padding(top = #{json_data['topMargin']}.dp)"
        end
        
        if json_data['bottomMargin']
          modifiers << ".padding(bottom = #{json_data['bottomMargin']}.dp)"
        end
        
        if json_data['leftMargin']
          modifiers << ".padding(start = #{json_data['leftMargin']}.dp)"
        end
        
        if json_data['rightMargin']
          modifiers << ".padding(end = #{json_data['rightMargin']}.dp)"
        end
        
        # Background
        if json_data['background']
          @required_imports.add(:background) if @required_imports
          
          # Check if we need corner radius and/or border
          if json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
            @required_imports.add(:border) if @required_imports
            @required_imports.add(:shape) if @required_imports
            
            # Build the modifier chain for background with corner radius
            if json_data['cornerRadius']
              modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
            end
            
            # Add border if specified
            if json_data['borderColor'] && json_data['borderWidth']
              border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
              modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
            end
            
            modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
          else
            modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
          end
        elsif json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
          # Handle corner radius and border without background
          @required_imports.add(:border) if @required_imports
          @required_imports.add(:shape) if @required_imports
          
          if json_data['cornerRadius']
            modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
          end
          
          if json_data['borderColor'] && json_data['borderWidth']
            border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
            modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
          end
        end
        
        # Format modifiers with line breaks
        if modifiers.any?
          code += "\n" + indent("modifier = Modifier", depth + 1)
          modifiers.each do |mod|
            code += "\n" + indent("    #{mod}", depth + 1)
          end
        end
        
        # Handle gravity based on SwiftJsonUI gravity attribute
        # gravity values from wiki: "top", "bottom", "centerVertical", "left", "right", "centerHorizontal"
        if json_data['gravity']
          if layout == 'Column'
            # Vertical gravities work when orientation is horizontal (per wiki)
            # But in Compose Column, we need to map to vertical arrangement
            case json_data['gravity']
            when 'top'
              code += ",\n" + indent("verticalArrangement = Arrangement.Top", depth + 1)
            when 'bottom'
              code += ",\n" + indent("verticalArrangement = Arrangement.Bottom", depth + 1)
            when 'centerVertical'
              code += ",\n" + indent("verticalArrangement = Arrangement.Center", depth + 1)
            when 'left'
              code += ",\n" + indent("horizontalAlignment = Alignment.Start", depth + 1)
            when 'right'
              code += ",\n" + indent("horizontalAlignment = Alignment.End", depth + 1)
            when 'centerHorizontal'
              code += ",\n" + indent("horizontalAlignment = Alignment.CenterHorizontally", depth + 1)
            end
          elsif layout == 'Row'
            # Horizontal gravities work when orientation is vertical (per wiki)
            # But in Compose Row, we need to map to horizontal arrangement
            case json_data['gravity']
            when 'left'
              code += ",\n" + indent("horizontalArrangement = Arrangement.Start", depth + 1)
            when 'right'
              code += ",\n" + indent("horizontalArrangement = Arrangement.End", depth + 1)
            when 'centerHorizontal'
              code += ",\n" + indent("horizontalArrangement = Arrangement.Center", depth + 1)
            when 'top'
              code += ",\n" + indent("verticalAlignment = Alignment.Top", depth + 1)
            when 'bottom'
              code += ",\n" + indent("verticalAlignment = Alignment.Bottom", depth + 1)
            when 'centerVertical'
              code += ",\n" + indent("verticalAlignment = Alignment.CenterVertically", depth + 1)
            end
          end
        end
        
        # alignment is an alternative to gravity per wiki
        
        code += "\n" + indent(") {", depth)
        
        # 'child' is the official attribute (always an array) per wiki
        children = json_data['child'] || []
        children = [children] unless children.is_a?(Array)
        
        children.each do |child|
          child_code = generate_component(child, depth + 1)
          code += "\n" + child_code unless child_code.empty?
        end
        
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_text(json_data, depth)
        text = process_data_binding(json_data['text'] || '')
        
        code = indent("Text(", depth)
        code += "\n" + indent("text = #{text},", depth + 1)
        
        if json_data['fontSize']
          code += "\n" + indent("fontSize = #{json_data['fontSize']}.sp,", depth + 1)
        end
        
        if json_data['fontColor']
          code += "\n" + indent("color = Color(android.graphics.Color.parseColor(\"#{json_data['fontColor']}\")),", depth + 1)
        end
        
        # Handle font attribute for bold text per wiki
        if json_data['font'] == 'bold' || json_data['fontWeight'] == 'bold'
          code += "\n" + indent("fontWeight = FontWeight.Bold,", depth + 1)
        elsif json_data['fontWeight']
          code += "\n" + indent("fontWeight = FontWeight.#{json_data['fontWeight'].capitalize},", depth + 1)
        end
        
        # Build modifiers
        modifiers = []
        
        # Alignment (only works in Box context)
        if json_data['alignTop'] && json_data['alignLeft']
          modifiers << ".align(Alignment.TopStart)"
        elsif json_data['alignTop'] && json_data['alignRight']
          modifiers << ".align(Alignment.TopEnd)"
        elsif json_data['alignBottom'] && json_data['alignLeft']
          modifiers << ".align(Alignment.BottomStart)"
        elsif json_data['alignBottom'] && json_data['alignRight']
          modifiers << ".align(Alignment.BottomEnd)"
        elsif json_data['alignTop'] && json_data['centerHorizontal']
          modifiers << ".align(Alignment.TopCenter)"
        elsif json_data['alignBottom'] && json_data['centerHorizontal']
          modifiers << ".align(Alignment.BottomCenter)"
        elsif json_data['alignLeft'] && json_data['centerVertical']
          modifiers << ".align(Alignment.CenterStart)"
        elsif json_data['alignRight'] && json_data['centerVertical']
          modifiers << ".align(Alignment.CenterEnd)"
        elsif json_data['centerInParent']
          modifiers << ".align(Alignment.Center)"
        elsif json_data['alignTop']
          modifiers << ".align(Alignment.TopCenter)"
        elsif json_data['alignBottom']
          modifiers << ".align(Alignment.BottomCenter)"
        elsif json_data['alignLeft']
          modifiers << ".align(Alignment.CenterStart)"
        elsif json_data['alignRight']
          modifiers << ".align(Alignment.CenterEnd)"
        elsif json_data['centerVertical']
          modifiers << ".align(Alignment.CenterStart)"
        elsif json_data['centerHorizontal']
          modifiers << ".fillMaxWidth()"
        end
        
        # Padding and margins
        if json_data['padding']
          modifiers << ".padding(#{json_data['padding']}.dp)"
        end
        
        if json_data['bottomMargin']
          modifiers << ".padding(bottom = #{json_data['bottomMargin']}.dp)"
        end
        
        if json_data['topMargin']
          modifiers << ".padding(top = #{json_data['topMargin']}.dp)"
        end
        
        # Background
        if json_data['background']
          @required_imports.add(:background) if @required_imports
          
          # Check if we need corner radius and/or border
          if json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
            @required_imports.add(:border) if @required_imports
            @required_imports.add(:shape) if @required_imports
            
            # Build the modifier chain for background with corner radius
            if json_data['cornerRadius']
              modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
            end
            
            # Add border if specified
            if json_data['borderColor'] && json_data['borderWidth']
              border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
              modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
            end
            
            modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
          else
            modifiers << ".background(Color(android.graphics.Color.parseColor(\"#{json_data['background']}\")))"
          end
        elsif json_data['cornerRadius'] || json_data['borderColor'] || json_data['borderWidth']
          # Handle corner radius and border without background
          @required_imports.add(:border) if @required_imports
          @required_imports.add(:shape) if @required_imports
          
          if json_data['cornerRadius']
            modifiers << ".clip(RoundedCornerShape(#{json_data['cornerRadius']}.dp))"
          end
          
          if json_data['borderColor'] && json_data['borderWidth']
            border_shape = json_data['cornerRadius'] ? "RoundedCornerShape(#{json_data['cornerRadius']}.dp)" : "RectangleShape"
            modifiers << ".border(#{json_data['borderWidth']}.dp, Color(android.graphics.Color.parseColor(\"#{json_data['borderColor']}\")), #{border_shape})"
          end
        end
        
        # Width and height
        if json_data['width'] == 'wrapContent'
          modifiers << ".wrapContentWidth()"
        elsif json_data['width'] == 'matchParent'
          modifiers << ".fillMaxWidth()"
        elsif json_data['width']
          modifiers << ".width(#{json_data['width']}.dp)"
        end
        
        if json_data['height'] == 'wrapContent'
          modifiers << ".wrapContentHeight()"
        elsif json_data['height']
          modifiers << ".height(#{json_data['height']}.dp)"
        end
        
        # Format modifiers
        if modifiers.any?
          code += "\n" + indent("modifier = Modifier", depth + 1)
          if modifiers.length == 1 && modifiers[0].start_with?('.')
            # Single modifier on same line
            code += modifiers[0]
          else
            # Multiple modifiers on separate lines
            modifiers.each do |mod|
              code += "\n" + indent("    #{mod}", depth + 1)
            end
          end
        else
          code += "\n" + indent("modifier = Modifier", depth + 1)
        end
        
        # Text alignment
        if json_data['centerHorizontal']
          @required_imports.add(:text_align) if @required_imports
          code += ",\n" + indent("textAlign = TextAlign.Center", depth + 1)
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_button(json_data, depth)
        text = process_data_binding(json_data['title'] || json_data['text'] || 'Button')
        onclick = json_data['onclick']
        
        code = indent("Button(", depth)
        
        if onclick
          code += "\n" + indent("onClick = { viewModel.#{onclick}() },", depth + 1)
        else
          code += "\n" + indent("onClick = { },", depth + 1)
        end
        
        # Build modifiers
        modifiers = []
        
        if json_data['padding']
          modifiers << ".padding(#{json_data['padding']}.dp)"
        end
        
        if json_data['bottomMargin']
          modifiers << ".padding(bottom = #{json_data['bottomMargin']}.dp)"
        end
        
        if json_data['topMargin']
          modifiers << ".padding(top = #{json_data['topMargin']}.dp)"
        end
        
        # Format modifiers
        code += "\n" + indent("modifier = Modifier", depth + 1)
        if modifiers.any?
          modifiers.each do |mod|
            code += "\n" + indent("    #{mod}", depth + 1)
          end
        end
        
        code += "\n" + indent(") {", depth)
        code += "\n" + indent("Text(#{text})", depth + 1)
        code += "\n" + indent("}", depth)
        code
      end
      
      def generate_image(json_data, depth)
        # 'src' is the official attribute for images per wiki
        image_name = json_data['src'] || 'placeholder'
        
        code = "Image(\n"
        code += indent("painter = painterResource(id = R.drawable.#{image_name}),", depth + 1) + "\n"
        code += indent("contentDescription = #{quote(json_data['contentDescription'] || '')},", depth + 1) + "\n"
        code += indent("modifier = Modifier", depth + 1)
        
        if json_data['width'] && json_data['height']
          code += ".size(#{json_data['width']}dp, #{json_data['height']}.dp)"
        elsif json_data['size']
          code += ".size(#{json_data['size']}.dp)"
        end
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}.dp)"
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_text_field(json_data, depth)
        # TextField uses 'text' for value and 'hint' for placeholder per wiki
        value = process_data_binding(json_data['text'] || '')
        placeholder = json_data['hint'] || ''
        is_secure = json_data['secure'] == true
        
        code = ""
        if is_secure
          # For secure text fields, use OutlinedTextField with password visual transformation
          @required_imports.add(:visual_transformation) if @required_imports
          code = "OutlinedTextField(\n"
        else
          code = "TextField(\n"
        end
        
        code += indent("value = #{value},", depth + 1) + "\n"
        # onTextChange is the official attribute per wiki (not onValueChange)
        code += indent("onValueChange = { newValue -> currentData.value = currentData.value.copy(#{extract_variable_name(json_data['text'])} = newValue) },", depth + 1) + "\n"
        
        if placeholder && !placeholder.empty?
          code += indent("placeholder = { Text(#{quote(placeholder)}) },", depth + 1) + "\n"
        end
        
        # Add password visual transformation for secure fields
        if is_secure
          code += indent("visualTransformation = PasswordVisualTransformation(),", depth + 1) + "\n"
        end
        
        code += indent("modifier = Modifier", depth + 1)
        
        # Handle width for text field
        if json_data['width'] == 'matchParent'
          code += ".fillMaxWidth()"
        elsif json_data['width']
          code += ".width(#{json_data['width']}.dp)"
        end
        
        if json_data['padding']
          code += ".padding(#{json_data['padding']}.dp)"
        end
        
        code += "\n" + indent(")", depth)
        code
      end
      
      def generate_include(json_data, depth)
        include_name = json_data['include']
        pascal_name = to_pascal_case(include_name)
        
        code = "#{pascal_name}View(\n"
        code += indent("viewModel = viewModel.#{to_camel_case(include_name)}ViewModel,", depth + 1) + "\n"
        
        # Pass data if specified
        if json_data['data']
          code += indent("data = #{pascal_name}Data(", depth + 1) + "\n"
          json_data['data'].each do |key, value|
            processed_value = process_data_binding(value.to_s)
            code += indent("#{key} = #{processed_value},", depth + 2) + "\n"
          end
          code += indent(")", depth + 1) + "\n"
        end
        
        code += indent(")", depth)
        code
      end
      
      def process_data_binding(text)
        return quote(text) unless text.is_a?(String)
        
        # Check for data binding syntax @{variable}
        if text.match(/@\{([^}]+)\}/)
          variable = $1
          # Handle Swift's nil-coalescing operator ?? 
          # In Kotlin, since we define default values in the data class,
          # we can just use the variable directly without Elvis operator
          if variable.include?(' ?? ')
            parts = variable.split(' ?? ')
            var_name = parts[0].strip
            # Just use the variable directly since it has a default value in the data class
            "\"\${data.#{var_name}}\""
          else
            # Use string interpolation for Kotlin
            "\"\${data.#{variable}}\""
          end
        else
          quote(text)
        end
      end
      
      def extract_variable_name(text)
        if text && text.match(/@\{([^}]+)\}/)
          $1.split('.').last
        else
          'value'
        end
      end
      
      def quote(text)
        "\"#{text.gsub('"', '\\"')}\""
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
      
      def extract_composable_content(json_data)
        # Generate the content that goes between the markers
        # Start with depth 1 (inside the function body)
        component_code = generate_component(json_data, 1)
        # Return code with proper line breaks
        "#{component_code}\n"
      end
      
      def update_imports(content)
        # Define all necessary imports based on what was used
        all_imports = [
          "import androidx.compose.foundation.background",
          "import androidx.compose.foundation.border",
          "import androidx.compose.foundation.layout.*",
          "import androidx.compose.foundation.lazy.LazyColumn",
          "import androidx.compose.foundation.lazy.LazyRow",
          "import androidx.compose.foundation.shape.RoundedCornerShape",
          "import androidx.compose.material3.*",
          "import androidx.compose.runtime.Composable",
          "import androidx.compose.ui.Alignment",
          "import androidx.compose.ui.Modifier",
          "import androidx.compose.ui.draw.clip",
          "import androidx.compose.ui.graphics.Color",
          "import androidx.compose.ui.graphics.RectangleShape",
          "import androidx.compose.ui.text.font.FontWeight",
          "import androidx.compose.ui.text.input.PasswordVisualTransformation",
          "import androidx.compose.ui.text.style.TextAlign",
          "import androidx.compose.ui.unit.dp",
          "import androidx.compose.ui.unit.sp"
        ]
        
        # Add imports that are needed based on @required_imports
        imports_to_add = []
        
        if @required_imports.include?(:lazy_column)
          imports_to_add << "import androidx.compose.foundation.lazy.LazyColumn" unless content.include?("import androidx.compose.foundation.lazy.LazyColumn")
        end
        
        if @required_imports.include?(:lazy_row)
          imports_to_add << "import androidx.compose.foundation.lazy.LazyRow" unless content.include?("import androidx.compose.foundation.lazy.LazyRow")
        end
        
        if @required_imports.include?(:background)
          imports_to_add << "import androidx.compose.foundation.background" unless content.include?("import androidx.compose.foundation.background")
        end
        
        if @required_imports.include?(:text_align)
          imports_to_add << "import androidx.compose.ui.text.style.TextAlign" unless content.include?("import androidx.compose.ui.text.style.TextAlign")
        end
        
        if @required_imports.include?(:border)
          imports_to_add << "import androidx.compose.foundation.border" unless content.include?("import androidx.compose.foundation.border")
        end
        
        if @required_imports.include?(:shape)
          imports_to_add << "import androidx.compose.foundation.shape.RoundedCornerShape" unless content.include?("import androidx.compose.foundation.shape.RoundedCornerShape")
          imports_to_add << "import androidx.compose.ui.draw.clip" unless content.include?("import androidx.compose.ui.draw.clip")
        end
        
        if @required_imports.include?(:visual_transformation)
          imports_to_add << "import androidx.compose.ui.text.input.PasswordVisualTransformation" unless content.include?("import androidx.compose.ui.text.input.PasswordVisualTransformation")
        end
        
        # Insert new imports after the package declaration
        if imports_to_add.any?
          lines = content.split("\n")
          package_index = lines.find_index { |line| line.start_with?("package ") }
          
          if package_index
            # Find where existing imports end
            last_import_index = package_index
            lines.each_with_index do |line, i|
              if i > package_index && line.start_with?("import ")
                last_import_index = i
              end
            end
            
            # Insert new imports after the last import
            imports_to_add.each do |import|
              lines.insert(last_import_index + 1, import)
              last_import_index += 1
            end
            
            content = lines.join("\n")
          end
        end
        
        content
      end
    end
  end
end