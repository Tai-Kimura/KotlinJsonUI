# frozen_string_literal: true

require 'core/attribute_validator'
require 'fileutils'
require 'json'

RSpec.describe KjuiTools::Core::AttributeValidator do
  describe '#initialize' do
    it 'loads attribute definitions' do
      validator = described_class.new
      expect(validator.definitions).not_to be_empty
      expect(validator.definitions).to have_key('common')
    end

    it 'defaults to :all mode' do
      validator = described_class.new
      expect(validator.mode).to eq(:all)
    end

    it 'accepts mode parameter' do
      validator = described_class.new(:compose)
      expect(validator.mode).to eq(:compose)
    end

    it 'initializes with empty warnings' do
      validator = described_class.new
      expect(validator.warnings).to be_empty
    end
  end

  describe 'extension definitions loading' do
    let(:extensions_dir) do
      File.join(File.dirname(__FILE__), '..', '..', 'lib', 'compose', 'components', 'extensions', 'attribute_definitions')
    end

    before do
      # Create test extension definition
      FileUtils.mkdir_p(extensions_dir)
      @test_definition_file = File.join(extensions_dir, 'TestCustomComponent.json')
      File.write(@test_definition_file, JSON.pretty_generate({
        'TestCustomComponent' => {
          'customTitle' => {
            'type' => 'string',
            'description' => 'Custom title attribute'
          },
          'customCount' => {
            'type' => 'number',
            'description' => 'Custom count attribute'
          }
        }
      }))
    end

    after do
      FileUtils.rm_f(@test_definition_file) if @test_definition_file && File.exist?(@test_definition_file)
    end

    it 'loads extension definitions' do
      validator = described_class.new
      expect(validator.definitions).to have_key('TestCustomComponent')
    end

    it 'merges extension definitions with base definitions' do
      validator = described_class.new
      expect(validator.definitions).to have_key('common')
      expect(validator.definitions).to have_key('TestCustomComponent')
    end

    it 'validates custom component attributes' do
      validator = described_class.new
      component = {
        'type' => 'TestCustomComponent',
        'customTitle' => 'Test Title',
        'customCount' => 42
      }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end

    it 'warns on invalid custom component attribute' do
      validator = described_class.new
      component = {
        'type' => 'TestCustomComponent',
        'customTitle' => 'Test Title',
        'invalidAttr' => 'value'
      }
      warnings = validator.validate(component)
      expect(warnings).to include(/Unknown attribute 'invalidAttr'/)
    end

    it 'validates custom component attribute types' do
      validator = described_class.new
      component = {
        'type' => 'TestCustomComponent',
        'customCount' => 'not a number'
      }
      warnings = validator.validate(component)
      expect(warnings).to include(/expects number, got string/)
    end
  end

  describe '#validate' do
    let(:validator) { described_class.new }

    context 'with valid Text component' do
      let(:component) do
        {
          'type' => 'Text',
          'text' => 'Hello World',
          'fontSize' => 14,
          'fontColor' => '#333333'
        }
      end

      it 'returns no warnings' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with unknown attribute' do
      let(:component) do
        {
          'type' => 'Text',
          'text' => 'Hello',
          'unknownAttr' => 'value'
        }
      end

      it 'returns warning for unknown attribute' do
        warnings = validator.validate(component)
        expect(warnings).to include(/Unknown attribute 'unknownAttr'/)
      end
    end

    context 'with wrong type' do
      let(:component) do
        {
          'type' => 'Text',
          'fontSize' => 'large'
        }
      end

      it 'returns warning for type mismatch' do
        warnings = validator.validate(component)
        expect(warnings).to include(/expects number, got string/)
      end
    end

    context 'with invalid enum value' do
      let(:component) do
        {
          'type' => 'Text',
          'textAlign' => 'invalid'
        }
      end

      it 'returns warning for invalid enum' do
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid value 'invalid'/)
      end
    end

    context 'with valid enum value' do
      let(:component) do
        {
          'type' => 'Text',
          'textAlign' => 'center'
        }
      end

      it 'returns no warnings' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with alpha out of range' do
      let(:component) do
        {
          'type' => 'View',
          'alpha' => 1.5
        }
      end

      it 'returns warning for value exceeding maximum' do
        warnings = validator.validate(component)
        expect(warnings).to include(/greater than maximum/)
      end
    end

    context 'with negative alpha' do
      let(:component) do
        {
          'type' => 'View',
          'alpha' => -0.5
        }
      end

      it 'returns warning for value below minimum' do
        warnings = validator.validate(component)
        expect(warnings).to include(/less than minimum/)
      end
    end

    context 'with binding expression' do
      let(:component) do
        {
          'type' => 'Text',
          'text' => '@{userName}'
        }
      end

      it 'skips validation for binding expressions' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with binding expression in enum attribute' do
      let(:component) do
        {
          'type' => 'View',
          'visibility' => '@{isVisible}'
        }
      end

      it 'skips enum validation for binding expressions' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with binding in the middle of string' do
      let(:component) do
        {
          'type' => 'View',
          'visibility' => 'invalid @{binding}'
        }
      end

      it 'validates enum and returns warning' do
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid value 'invalid @{binding}'/)
      end
    end

    context 'with valid enum value in visibility' do
      let(:component) do
        {
          'type' => 'View',
          'visibility' => 'visible'
        }
      end

      it 'returns no warnings' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with invalid enum value in visibility' do
      let(:component) do
        {
          'type' => 'View',
          'visibility' => 'invalid'
        }
      end

      it 'returns warning for invalid enum value' do
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid value 'invalid'/)
        expect(warnings).to include(/Valid values: visible, invisible, gone/)
      end
    end

    context 'with required attribute missing' do
      let(:component) do
        {
          'type' => 'Radio'
        }
      end

      it 'returns warning for missing required attribute' do
        warnings = validator.validate(component)
        expect(warnings).to include(/Required attribute 'group' is missing/)
      end
    end

    # New tests for enum array validation
    context 'with enum array values' do
      let(:component) do
        {
          'type' => 'View',
          'gravity' => ['centerVertical']
        }
      end

      it 'accepts single valid enum value in array' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with multiple enum array values' do
      let(:component) do
        {
          'type' => 'View',
          'gravity' => ['centerVertical', 'left']
        }
      end

      it 'accepts multiple valid enum values in array' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with invalid enum array values' do
      let(:component) do
        {
          'type' => 'View',
          'gravity' => ['invalid']
        }
      end

      it 'returns warning for invalid enum values in array' do
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid values \["invalid"\]/)
      end
    end

    context 'with mixed valid and invalid enum array values' do
      let(:component) do
        {
          'type' => 'View',
          'gravity' => ['centerVertical', 'invalid', 'left']
        }
      end

      it 'returns warning for invalid enum values in array' do
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid values \["invalid"\]/)
      end
    end

    # Tests for width/height with enum type definitions
    context 'with matchParent string value for width' do
      let(:component) do
        {
          'type' => 'View',
          'width' => 'matchParent'
        }
      end

      it 'accepts matchParent string value' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with wrapContent string value for height' do
      let(:component) do
        {
          'type' => 'View',
          'height' => 'wrapContent'
        }
      end

      it 'accepts wrapContent string value' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with invalid string value for width' do
      let(:component) do
        {
          'type' => 'View',
          'width' => 'invalid'
        }
      end

      it 'returns warning for invalid enum string value' do
        warnings = validator.validate(component)
        expect(warnings).to include(/expects/)
      end
    end

    context 'with numeric value for width' do
      let(:component) do
        {
          'type' => 'View',
          'width' => 100
        }
      end

      it 'accepts numeric value' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with binding value for width' do
      let(:component) do
        {
          'type' => 'View',
          'width' => '@{dynamicWidth}'
        }
      end

      it 'accepts binding value' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end
  end

  describe '#validate with different component types' do
    let(:validator) { described_class.new }

    context 'Button component' do
      let(:component) do
        {
          'type' => 'Button',
          'text' => 'Click Me',
          'onclick' => 'handleClick',
          'enabled' => true,
          'async' => false
        }
      end

      it 'validates button-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'TextField component' do
      let(:component) do
        {
          'type' => 'TextField',
          'hint' => 'Enter text',
          'keyboardType' => 'email',
          'imeAction' => 'done',
          'secure' => false
        }
      end

      it 'validates textfield-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end

      it 'warns on invalid keyboardType' do
        component['keyboardType'] = 'invalid'
        warnings = validator.validate(component)
        expect(warnings).to include(/invalid value 'invalid'/)
      end
    end

    context 'Image component' do
      let(:component) do
        {
          'type' => 'Image',
          'src' => 'icon_home',
          'contentMode' => 'aspectFit',
          'size' => 24
        }
      end

      it 'validates image-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'Switch component' do
      let(:component) do
        {
          'type' => 'Switch',
          'isOn' => true,
          'onTintColor' => '#00FF00',
          'onValueChange' => 'handleSwitch'
        }
      end

      it 'validates switch-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'SelectBox component' do
      let(:component) do
        {
          'type' => 'SelectBox',
          'items' => ['Option 1', 'Option 2'],
          'hint' => 'Select option',
          'datePickerMode' => 'date'
        }
      end

      it 'validates selectbox-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'Collection component' do
      let(:component) do
        {
          'type' => 'Collection',
          'columns' => 2,
          'itemSpacing' => 8,
          'layout' => 'vertical'
        }
      end

      it 'validates collection-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'View container' do
      let(:component) do
        {
          'type' => 'View',
          'orientation' => 'vertical',
          'gravity' => 'center',
          'spacing' => 8
        }
      end

      it 'validates view-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'ScrollView component' do
      let(:component) do
        {
          'type' => 'ScrollView',
          'orientation' => 'vertical',
          'scrollEnabled' => true
        }
      end

      it 'validates scrollview-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end
  end

  describe '#validate common attributes' do
    let(:validator) { described_class.new }

    context 'size attributes' do
      let(:component) do
        {
          'type' => 'View',
          'width' => 100,
          'height' => 200,
          'minWidth' => 50,
          'maxWidth' => 300
        }
      end

      it 'accepts numeric values' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end

      it 'accepts matchParent string' do
        component['width'] = 'matchParent'
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end

      it 'accepts wrapContent string' do
        component['height'] = 'wrapContent'
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'padding attributes' do
      let(:component) do
        {
          'type' => 'View',
          'padding' => 16,
          'paddingTop' => 8,
          'paddingBottom' => 8
        }
      end

      it 'accepts padding values' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end

      it 'accepts paddings array' do
        component['paddings'] = [8, 16, 8, 16]
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'margin attributes' do
      let(:component) do
        {
          'type' => 'View',
          'margins' => [8, 8, 8, 8],
          'topMargin' => 16
        }
      end

      it 'accepts margin values' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'visual attributes' do
      let(:component) do
        {
          'type' => 'View',
          'background' => '#FFFFFF',
          'cornerRadius' => 8,
          'borderWidth' => 1,
          'borderColor' => '#CCCCCC',
          'shadow' => true
        }
      end

      it 'accepts visual attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'constraint layout attributes' do
      let(:component) do
        {
          'type' => 'View',
          'alignTopOfView' => 'otherId',
          'alignLeftView' => 'otherId',
          'alignCenterVerticalView' => 'parent'
        }
      end

      it 'accepts constraint layout attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end
  end

  describe 'mode compatibility' do
    context 'with xml mode' do
      let(:validator) { described_class.new(:xml) }

      let(:component) do
        {
          'type' => 'View',
          'aspectWidth' => 16,
          'aspectHeight' => 9
        }
      end

      it 'accepts xml-specific attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end

    context 'with compose mode' do
      let(:validator) { described_class.new(:compose) }

      let(:component) do
        {
          'type' => 'View',
          'background' => '#FFFFFF'
        }
      end

      it 'accepts common attributes' do
        warnings = validator.validate(component)
        expect(warnings).to be_empty
      end
    end
  end

  describe '#has_warnings?' do
    let(:validator) { described_class.new }

    it 'returns false when no warnings' do
      validator.validate({ 'type' => 'Text', 'text' => 'Hello' })
      expect(validator.has_warnings?).to be false
    end

    it 'returns true when warnings exist' do
      validator.validate({ 'type' => 'Text', 'unknownAttr' => 'value' })
      expect(validator.has_warnings?).to be true
    end
  end

  describe 'type mapping' do
    let(:validator) { described_class.new }

    it 'maps Label to Text' do
      component = { 'type' => 'Label', 'text' => 'Hello' }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end

    it 'maps EditText to TextField' do
      component = { 'type' => 'EditText', 'hint' => 'Enter' }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end

    it 'maps Toggle to Switch' do
      component = { 'type' => 'Toggle', 'isOn' => true }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end

    it 'maps RecyclerView to Collection' do
      component = { 'type' => 'RecyclerView', 'columns' => 2 }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end

    it 'maps Container to View' do
      component = { 'type' => 'Container', 'orientation' => 'vertical' }
      warnings = validator.validate(component)
      expect(warnings).to be_empty
    end
  end
end
