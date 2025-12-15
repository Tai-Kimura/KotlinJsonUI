# frozen_string_literal: true

require 'compose/components/text_component'
require 'compose/helpers/modifier_builder'
require 'compose/helpers/visibility_helper'
require 'compose/helpers/resource_resolver'

RSpec.describe KjuiTools::Compose::Components::TextComponent do
  let(:required_imports) { Set.new }

  describe '.generate' do
    it 'generates basic text component' do
      json_data = { 'type' => 'Text', 'text' => 'Hello World' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Text(')
      expect(result).to include('text = "Hello World"')
    end

    it 'generates text with font size' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'fontSize' => 16 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontSize = 16.sp')
    end

    it 'generates text with font color' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'fontColor' => '#FF0000' }
      result = described_class.generate(json_data, 0, required_imports)
      # ResourceResolver.process_color returns parseColor format
      expect(result).to include('color = ')
      expect(result).to include('#FF0000')
    end

    it 'generates text with bold font weight' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'fontWeight' => 'bold' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontWeight = FontWeight.Bold')
    end

    it 'generates text with font attribute for bold' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'font' => 'bold' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontWeight = FontWeight.Bold')
    end

    it 'generates text with font attribute for semibold' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'font' => 'semibold' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontWeight = FontWeight.SemiBold')
    end

    it 'generates text with custom font family' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'font' => 'Roboto-Regular' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontFamily = FontFamily(Font(R.font.roboto_regular))')
      expect(required_imports).to include(:font_family)
    end

    it 'generates text with various font weights' do
      weights = {
        'thin' => 'Thin',
        'light' => 'Light',
        'normal' => 'Normal',
        'medium' => 'Medium',
        'semibold' => 'SemiBold',
        'extrabold' => 'ExtraBold'
      }

      weights.each do |input, output|
        json_data = { 'type' => 'Text', 'text' => 'Test', 'fontWeight' => input }
        result = described_class.generate(json_data, 0, Set.new)
        expect(result).to include("fontWeight = FontWeight.#{output}")
      end
    end

    it 'generates text with underline' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'underline' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textDecoration = TextDecoration.Underline')
      expect(required_imports).to include(:text_decoration)
    end

    it 'generates text with strikethrough' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'strikethrough' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textDecoration = TextDecoration.LineThrough')
    end

    it 'generates text with combined decorations' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'underline' => true, 'strikethrough' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('TextDecoration.combine')
    end

    it 'generates text with text alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'textAlign' => 'center' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.Center')
      expect(required_imports).to include(:text_align)
    end

    it 'generates text with right alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'textAlign' => 'right' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.End')
    end

    it 'generates text with max lines' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'lines' => 2 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('maxLines = 2')
    end

    it 'generates text with unlimited lines (0)' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'lines' => 0 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('maxLines = Int.MAX_VALUE')
    end

    it 'generates text with line break mode' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'lineBreakMode' => 'tail' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('overflow = TextOverflow.Ellipsis')
    end

    it 'generates text with clip line break mode' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'lineBreakMode' => 'clip' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('overflow = TextOverflow.Clip')
    end

    it 'generates text with lineHeightMultiple' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'fontSize' => 14, 'lineHeightMultiple' => 1.5 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('style = TextStyle')
      expect(result).to include('lineHeight')
    end

    it 'generates text with edgeInset' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'edgeInset' => [10, 20, 30, 40] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding(top = 10.dp, end = 20.dp, bottom = 30.dp, start = 40.dp)')
    end

    it 'generates text with single value edgeInset' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'edgeInset' => 16 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding(16.dp)')
    end

    it 'handles centerHorizontal for text alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'centerHorizontal' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.Center')
    end

    it 'returns empty string for hidden visibility' do
      allow(KjuiTools::Compose::Helpers::VisibilityHelper).to receive(:should_skip_render?).and_return(true)
      json_data = { 'type' => 'Text', 'text' => 'Test', 'visibility' => 'gone' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to eq('')
    end

    it 'generates text with minimumScaleFactor' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'minimumScaleFactor' => 0.5 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('maxLines = 1')
      expect(result).to include('overflow = TextOverflow.Ellipsis')
    end
  end

  describe '.generate_with_partial_attributes_for_linkable' do
    it 'generates PartialAttributesText for linkable text' do
      json_data = { 'type' => 'Text', 'text' => 'Test link', 'linkable' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('PartialAttributesText(')
      expect(result).to include('linkable = true')
      expect(required_imports).to include(:partial_attributes_text)
    end
  end

  describe '.generate_with_partial_attributes_component' do
    it 'generates PartialAttributesText with partialAttributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'fontColor' => '#FF0000', 'fontSize' => 20 }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('PartialAttributesText(')
      expect(result).to include('partialAttributes = listOf(')
      expect(result).to include('PartialAttribute.fromJsonRange')
    end

    it 'handles string range' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => 'Hello', 'fontWeight' => 'bold' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('range = "Hello"')
    end

    it 'handles onclick in partial attributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Click here',
        'partialAttributes' => [
          { 'range' => [0, 5], 'onclick' => 'handleClick' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onClick = { viewModel.handleClick() }')
    end
  end

  describe 'additional text attributes' do
    it 'generates text with binding expression' do
      json_data = { 'type' => 'Text', 'text' => '@{title}' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('data.title')
    end

    it 'generates text with font bold' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'font' => 'bold' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontWeight = FontWeight.Bold')
    end

    it 'handles Label type same as Text' do
      json_data = { 'type' => 'Label', 'text' => 'Label Text' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Text(')
      expect(result).to include('Label Text')
    end
  end

  describe 'modifiers' do
    it 'applies width modifier' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'width' => 100 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Modifier')
    end

    it 'applies height modifier' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'height' => 50 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Modifier')
    end

    it 'applies background modifier' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'background' => '#FF0000' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.background')
    end

    it 'applies width with match_parent' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'width' => 'match_parent' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('Modifier')
    end
  end

  describe 'additional partial attributes' do
    it 'generates text with background in partial attributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'background' => '#FFFF00' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('background = "#FFFF00"')
    end

    it 'generates text with underline in partial attributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'underline' => true }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('underline = true')
    end

    it 'generates text with strikethrough in partial attributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'strikethrough' => true }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('strikethrough = true')
    end

    it 'handles multiple partial attributes' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'fontColor' => '#FF0000' },
          { 'range' => [6, 11], 'fontColor' => '#00FF00' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('#FF0000')
      expect(result).to include('#00FF00')
    end

    it 'handles partial attributes without onclick' do
      json_data = {
        'type' => 'Text',
        'text' => 'Hello World',
        'partialAttributes' => [
          { 'range' => [0, 5], 'fontColor' => '#FF0000' }
        ]
      }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('onClick = null')
    end
  end

  describe 'linkable text styles' do
    it 'generates linkable text with fontSize' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'fontSize' => 14 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontSize = 14.sp')
    end

    it 'generates linkable text with fontColor' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'fontColor' => '#0000FF' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('color =')
    end

    it 'generates linkable text with font weight' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'fontWeight' => 'medium' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('fontWeight = FontWeight.Medium')
    end

    it 'generates linkable text with text alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'textAlign' => 'center' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.Center')
    end

    it 'generates linkable text with edgeInset array' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'edgeInset' => [5, 10, 5, 10] }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding')
    end

    it 'generates linkable text with edgeInset number' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'edgeInset' => 8 }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('.padding(8.dp)')
    end

    it 'generates linkable text with right alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'textAlign' => 'right' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.End')
    end

    it 'generates linkable text with left alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Link', 'linkable' => true, 'textAlign' => 'left' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.Start')
    end
  end

  describe 'text shadow' do
    it 'generates text with textShadow' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'textShadow' => true }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('style = TextStyle')
      expect(result).to include('shadow = Shadow')
      expect(required_imports).to include(:shadow_style)
    end
  end

  describe 'left alignment' do
    it 'generates text with left alignment' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'textAlign' => 'left' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('textAlign = TextAlign.Start')
    end
  end

  describe 'word line break mode' do
    it 'generates text with word line break mode' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'lineBreakMode' => 'word' }
      result = described_class.generate(json_data, 0, required_imports)
      expect(result).to include('overflow = TextOverflow.Ellipsis')
    end
  end

  describe 'parent type handling' do
    it 'applies weight in Row parent' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'weight' => 1 }
      result = described_class.generate(json_data, 0, required_imports, 'Row')
      expect(result).to include('.weight')
    end

    it 'applies weight in Column parent' do
      json_data = { 'type' => 'Text', 'text' => 'Test', 'weight' => 1 }
      result = described_class.generate(json_data, 0, required_imports, 'Column')
      expect(result).to include('.weight')
    end
  end

  describe 'helper methods' do
    describe '.escape_string' do
      it 'escapes quotes' do
        result = described_class.send(:escape_string, 'text "with" quotes')
        expect(result).to eq('text \\"with\\" quotes')
      end

      it 'escapes newlines' do
        result = described_class.send(:escape_string, "line1\nline2")
        expect(result).to eq('line1\\nline2')
      end

      it 'escapes carriage returns' do
        result = described_class.send(:escape_string, "line1\rline2")
        expect(result).to eq('line1\\rline2')
      end

      it 'escapes tabs' do
        result = described_class.send(:escape_string, "col1\tcol2")
        expect(result).to eq('col1\\tcol2')
      end

      it 'escapes backslashes' do
        result = described_class.send(:escape_string, 'path\\to\\file')
        expect(result).to include('\\\\')
      end
    end

    describe '.quote' do
      it 'quotes and escapes text' do
        result = described_class.send(:quote, 'Hello "World"')
        expect(result).to start_with('"')
        expect(result).to end_with('"')
        expect(result).to include('\\"')
      end

      it 'handles backslashes' do
        result = described_class.send(:quote, 'C:\\path')
        expect(result).to include('\\\\')
      end
    end

    describe '.indent' do
      it 'adds indentation' do
        result = described_class.send(:indent, 'text', 2)
        expect(result).to eq('        text')
      end

      it 'returns unchanged for level 0' do
        result = described_class.send(:indent, 'text', 0)
        expect(result).to eq('text')
      end

      it 'preserves empty lines' do
        result = described_class.send(:indent, "line1\n\nline2", 1)
        expect(result).to eq("    line1\n\n    line2")
      end
    end

    describe '.build_text_style' do
      it 'returns nil when no style parts' do
        result = described_class.send(:build_text_style, { 'type' => 'Text' }, 0, Set.new)
        expect(result).to be_nil
      end

      it 'builds style with fontSize' do
        imports = Set.new
        result = described_class.send(:build_text_style, { 'fontSize' => 16 }, 0, imports)
        expect(result).to include('fontSize = 16.sp')
      end

      it 'builds style with fontColor' do
        imports = Set.new
        result = described_class.send(:build_text_style, { 'fontColor' => '#FF0000' }, 0, imports)
        expect(result).to include('color =')
      end

      it 'builds style with textAlign' do
        imports = Set.new
        result = described_class.send(:build_text_style, { 'textAlign' => 'center' }, 0, imports)
        expect(result).to include('textAlign = TextAlign.Center')
        expect(imports).to include(:text_align)
      end
    end
  end
end
