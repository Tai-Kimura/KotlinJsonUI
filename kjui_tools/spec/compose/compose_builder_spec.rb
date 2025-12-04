# frozen_string_literal: true

require 'compose/compose_builder'

RSpec.describe KjuiTools::Compose::ComposeBuilder do
  let(:temp_dir) { Dir.mktmpdir('compose_builder_test') }
  let(:layouts_dir) { File.join(temp_dir, 'src/main/assets/Layouts') }
  let(:view_dir) { File.join(temp_dir, 'src/main/kotlin/com/example/app/views') }

  let(:config) do
    {
      'source_directory' => 'src/main',
      'layouts_directory' => 'assets/Layouts',
      'view_directory' => 'kotlin/com/example/app/views',
      'package_name' => 'com.example.app',
      'project_path' => temp_dir
    }
  end

  before do
    FileUtils.mkdir_p(layouts_dir)
    FileUtils.mkdir_p(view_dir)

    allow(KjuiTools::Core::ConfigManager).to receive(:load_config).and_return(config)
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_full_source_path).and_return(temp_dir)
    allow(KjuiTools::Core::ProjectFinder).to receive(:get_package_name).and_return('com.example.app')
    allow(Dir).to receive(:pwd).and_return(temp_dir)
  end

  after do
    FileUtils.rm_rf(temp_dir)
  end

  describe '#initialize' do
    it 'sets up directories correctly' do
      builder = described_class.new
      expect(builder.instance_variable_get(:@layouts_dir)).to include('assets/Layouts')
      expect(builder.instance_variable_get(:@view_dir)).to include('kotlin/com/example/app/views')
    end

    it 'creates view directory if not exists' do
      FileUtils.rm_rf(view_dir)
      described_class.new
      expect(Dir.exist?(view_dir)).to be true
    end
  end

  describe '#build' do
    context 'when no JSON files found' do
      it 'logs warning' do
        builder = described_class.new
        expect { builder.build }.to output(/No JSON files found/).to_stdout
      end
    end

    context 'when JSON files exist in Resources folder' do
      before do
        resources_dir = File.join(layouts_dir, 'Resources')
        FileUtils.mkdir_p(resources_dir)
        File.write(File.join(resources_dir, 'strings.json'), '{}')
      end

      it 'excludes Resources folder from build' do
        builder = described_class.new
        expect { builder.build }.to output(/No JSON files found/).to_stdout
      end
    end
  end

  describe '#build_file' do
    let(:builder) { described_class.new }

    context 'with invalid JSON' do
      before do
        File.write(File.join(layouts_dir, 'invalid.json'), 'not valid json')
      end

      it 'logs error for invalid JSON' do
        expect { builder.build_file(File.join(layouts_dir, 'invalid.json')) }.to output(/Failed to parse/).to_stdout
      end
    end

    context 'when generated file not found' do
      before do
        File.write(File.join(layouts_dir, 'test_view.json'), '{"type": "View"}')
      end

      it 'logs warning' do
        expect { builder.build_file(File.join(layouts_dir, 'test_view.json')) }.to output(/GeneratedView file not found/).to_stdout
      end
    end
  end

  describe 'private helper methods' do
    let(:builder) { described_class.new }

    describe '#to_pascal_case' do
      it 'converts snake_case to PascalCase' do
        expect(builder.send(:to_pascal_case, 'test_view_name')).to eq('TestViewName')
      end

      it 'converts kebab-case to PascalCase' do
        expect(builder.send(:to_pascal_case, 'test-view-name')).to eq('TestViewName')
      end
    end

    describe '#to_camel_case' do
      it 'converts to camelCase' do
        expect(builder.send(:to_camel_case, 'test_view')).to eq('testView')
      end
    end

    describe '#to_snake_case' do
      it 'converts PascalCase to snake_case' do
        expect(builder.send(:to_snake_case, 'TestViewName')).to eq('test_view_name')
      end
    end

    describe '#indent' do
      it 'adds correct indentation' do
        result = builder.send(:indent, 'text', 2)
        expect(result).to eq('        text')
      end

      it 'returns text unchanged for level 0' do
        result = builder.send(:indent, 'text', 0)
        expect(result).to eq('text')
      end
    end

    describe '#quote' do
      it 'escapes quotes' do
        result = builder.send(:quote, 'text with "quotes"')
        expect(result).to eq('"text with \\"quotes\\""')
      end

      it 'escapes newlines' do
        result = builder.send(:quote, "line1\nline2")
        expect(result).to eq('"line1\\nline2"')
      end

      it 'escapes tabs' do
        result = builder.send(:quote, "text\twith\ttabs")
        expect(result).to eq('"text\\twith\\ttabs"')
      end
    end

    describe '#process_data_binding' do
      it 'processes simple binding' do
        result = builder.send(:process_data_binding, '@{userName}')
        expect(result).to eq('"${data.userName}"')
      end

      it 'processes binding with null coalescing' do
        result = builder.send(:process_data_binding, '@{userName ?? "Guest"}')
        expect(result).to eq('"${data.userName}"')
      end

      it 'quotes non-binding text' do
        result = builder.send(:process_data_binding, 'plain text')
        expect(result).to eq('"plain text"')
      end
    end

    describe '#format_value_for_kotlin' do
      it 'formats string values' do
        expect(builder.send(:format_value_for_kotlin, 'test')).to eq('"test"')
      end

      it 'formats integer values' do
        expect(builder.send(:format_value_for_kotlin, 42)).to eq('42')
      end

      it 'formats float values' do
        expect(builder.send(:format_value_for_kotlin, 3.14)).to eq('3.14f')
      end

      it 'formats boolean true' do
        expect(builder.send(:format_value_for_kotlin, true)).to eq('true')
      end

      it 'formats boolean false' do
        expect(builder.send(:format_value_for_kotlin, false)).to eq('false')
      end

      it 'formats nil' do
        expect(builder.send(:format_value_for_kotlin, nil)).to eq('null')
      end

      it 'formats arrays as strings' do
        expect(builder.send(:format_value_for_kotlin, [1, 2, 3])).to eq('"[1, 2, 3]"')
      end
    end

    describe '#generate_component' do
      before do
        builder.instance_variable_set(:@required_imports, Set.new)
        builder.instance_variable_set(:@included_views, Set.new)
        builder.instance_variable_set(:@cell_views, Set.new)
        builder.instance_variable_set(:@custom_components, Set.new)
      end

      it 'returns empty string for non-hash data' do
        expect(builder.send(:generate_component, 'not a hash')).to eq('')
        expect(builder.send(:generate_component, nil)).to eq('')
      end

      it 'generates Text component' do
        result = builder.send(:generate_component, { 'type' => 'Text', 'text' => 'Hello' })
        expect(result).to include('Text(')
      end

      it 'generates Label as Text component' do
        result = builder.send(:generate_component, { 'type' => 'Label', 'text' => 'Hello' })
        expect(result).to include('Text(')
      end

      it 'generates Button component' do
        result = builder.send(:generate_component, { 'type' => 'Button', 'text' => 'Click' })
        expect(result).to include('Button(')
      end

      it 'generates Image component' do
        result = builder.send(:generate_component, { 'type' => 'Image', 'src' => 'icon' })
        expect(result).to include('Image(')
      end

      it 'generates TextField component' do
        result = builder.send(:generate_component, { 'type' => 'TextField' })
        expect(result).not_to be_empty
      end

      it 'generates Switch component' do
        result = builder.send(:generate_component, { 'type' => 'Switch' })
        expect(result).to include('Switch')
      end

      it 'generates Toggle as Switch component' do
        result = builder.send(:generate_component, { 'type' => 'Toggle' })
        expect(result).to include('Switch')
      end

      it 'generates Slider component' do
        result = builder.send(:generate_component, { 'type' => 'Slider' })
        expect(result).to include('Slider')
      end

      it 'generates Progress component' do
        result = builder.send(:generate_component, { 'type' => 'Progress' })
        expect(result).to include('ProgressIndicator')
      end

      it 'generates SelectBox component' do
        result = builder.send(:generate_component, { 'type' => 'SelectBox' })
        expect(result).not_to be_empty
      end

      it 'generates Check/Checkbox component' do
        result = builder.send(:generate_component, { 'type' => 'Check' })
        expect(result).not_to be_empty
      end

      it 'generates Checkbox component' do
        result = builder.send(:generate_component, { 'type' => 'Checkbox' })
        expect(result).not_to be_empty
      end

      it 'generates Radio component' do
        result = builder.send(:generate_component, { 'type' => 'Radio' })
        expect(result).not_to be_empty
      end

      it 'generates Segment component' do
        result = builder.send(:generate_component, { 'type' => 'Segment' })
        expect(result).not_to be_empty
      end

      it 'generates NetworkImage component' do
        result = builder.send(:generate_component, { 'type' => 'NetworkImage', 'url' => 'https://example.com/img.png' })
        expect(result).not_to be_empty
      end

      it 'generates CircleImage component' do
        result = builder.send(:generate_component, { 'type' => 'CircleImage' })
        expect(result).not_to be_empty
      end

      it 'generates Indicator component' do
        result = builder.send(:generate_component, { 'type' => 'Indicator' })
        expect(result).not_to be_empty
      end

      it 'generates TextView component' do
        result = builder.send(:generate_component, { 'type' => 'TextView' })
        expect(result).not_to be_empty
      end

      it 'generates Collection component' do
        result = builder.send(:generate_component, { 'type' => 'Collection', 'cellClasses' => ['ProductCell'] })
        expect(result).not_to be_empty
        expect(builder.instance_variable_get(:@cell_views)).to include('ProductCell')
      end

      it 'generates Table component' do
        result = builder.send(:generate_component, { 'type' => 'Table' })
        expect(result).not_to be_empty
      end

      it 'generates Web component' do
        result = builder.send(:generate_component, { 'type' => 'Web', 'url' => 'https://example.com' })
        expect(result).not_to be_empty
      end

      it 'generates GradientView component' do
        result = builder.send(:generate_component, { 'type' => 'GradientView' })
        expect(result).not_to be_empty
      end

      it 'generates BlurView component' do
        result = builder.send(:generate_component, { 'type' => 'BlurView' })
        expect(result).not_to be_empty
      end

      it 'generates Spacer component' do
        result = builder.send(:generate_component, { 'type' => 'Spacer', 'height' => 16 })
        expect(result).to include('Spacer')
        expect(result).to include('16.dp')
      end

      it 'generates Spacer with default height' do
        result = builder.send(:generate_component, { 'type' => 'Spacer' })
        expect(result).to include('8.dp')
      end

      it 'generates TODO for unknown component' do
        result = builder.send(:generate_component, { 'type' => 'UnknownWidget' })
        expect(result).to include('TODO')
      end

      it 'generates View as container' do
        result = builder.send(:generate_component, { 'type' => 'View' })
        expect(result).not_to be_empty
      end

      it 'generates ScrollView' do
        result = builder.send(:generate_component, { 'type' => 'ScrollView' })
        expect(result).not_to be_empty
      end

      it 'generates Scroll as ScrollView' do
        result = builder.send(:generate_component, { 'type' => 'Scroll' })
        expect(result).not_to be_empty
      end
    end

    describe '#generate_safe_area_view' do
      before do
        builder.instance_variable_set(:@required_imports, Set.new)
      end

      it 'generates Box with systemBarsPadding' do
        result = builder.send(:generate_safe_area_view, {}, 0)
        expect(result).to include('Box(')
        expect(result).to include('.systemBarsPadding()')
      end

      it 'generates with child components' do
        data = { 'child' => { 'type' => 'Text', 'text' => 'Hello' } }
        result = builder.send(:generate_safe_area_view, data, 0)
        expect(result).to include('Text(')
      end

      it 'handles child array' do
        data = { 'child' => [{ 'type' => 'Text', 'text' => 'Hello' }] }
        result = builder.send(:generate_safe_area_view, data, 0)
        expect(result).to include('Text(')
      end
    end

    describe '#generate_include' do
      before do
        builder.instance_variable_set(:@required_imports, Set.new)
        builder.instance_variable_set(:@included_views, Set.new)
      end

      it 'generates include with remember' do
        result = builder.send(:generate_include, { 'include' => 'product_cell' }, 0)
        expect(result).to include('remember')
        expect(result).to include('ProductCellViewModel')
      end

      it 'adds included view to set' do
        builder.send(:generate_include, { 'include' => 'product_cell' }, 0)
        expect(builder.instance_variable_get(:@included_views)).to include('product_cell')
      end

      it 'generates with data bindings' do
        result = builder.send(:generate_include, {
          'include' => 'product_cell',
          'data' => { 'name' => '@{productName}' }
        }, 0)
        expect(result).to include('LaunchedEffect')
      end

      it 'generates with shared_data' do
        result = builder.send(:generate_include, {
          'include' => 'product_cell',
          'shared_data' => { 'value' => '@{sharedValue}' }
        }, 0)
        expect(result).to include('LaunchedEffect')
      end

      it 'generates dynamic include' do
        result = builder.send(:generate_include, {
          'include' => 'product_cell',
          'dynamic' => true
        }, 0)
        expect(result).to include('SafeDynamicView')
      end
    end

    describe '#handle_container_result' do
      before do
        builder.instance_variable_set(:@required_imports, Set.new)
        builder.instance_variable_set(:@included_views, Set.new)
        builder.instance_variable_set(:@cell_views, Set.new)
        builder.instance_variable_set(:@custom_components, Set.new)
      end

      it 'returns string result as-is' do
        expect(builder.send(:handle_container_result, 'simple', 0)).to eq('simple')
      end

      it 'processes hash result with code and children' do
        result = builder.send(:handle_container_result, {
          code: 'Box(',
          children: [{ 'type' => 'Text', 'text' => 'Hello' }],
          closing: ')'
        }, 0)
        expect(result).to include('Box(')
        expect(result).to include(')')
      end
    end

    describe '#update_imports' do
      before do
        builder.instance_variable_set(:@required_imports, Set.new([:text]))
        builder.instance_variable_set(:@included_views, Set.new)
        builder.instance_variable_set(:@cell_views, Set.new)
        builder.instance_variable_set(:@custom_components, Set.new)
      end

      it 'adds imports to content' do
        content = "package com.example.app\n\nimport android.app.Activity\n\nclass Test {}"
        result = builder.send(:update_imports, content)
        expect(result).to include('import')
      end
    end
  end
end
