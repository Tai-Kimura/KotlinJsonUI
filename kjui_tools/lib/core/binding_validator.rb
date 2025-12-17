#!/usr/bin/env ruby

require 'json'
require 'set'

module KjuiTools
  module Core
    # Validates binding expressions in JSON layouts
    # Warns when bindings contain business logic that should be in ViewModel
    class BindingValidator
      attr_reader :warnings

      # Patterns that indicate business logic in bindings
      BUSINESS_LOGIC_PATTERNS = [
        # Ternary operators (Kotlin: if-else expression or ternary-like)
        {
          pattern: /\?.*:/,
          message: "ternary operator (?:) - move condition logic to ViewModel"
        },
        # Kotlin if expression
        {
          pattern: /\bif\s*\(/,
          message: "if expression - move condition logic to ViewModel"
        },
        # Kotlin when expression
        {
          pattern: /\bwhen\s*[({]/,
          message: "when expression - move logic to ViewModel"
        },
        # Comparison operators
        {
          pattern: /[<>=!]=|[<>]/,
          message: "comparison operator - move to ViewModel computed property"
        },
        # Arithmetic operators (but allow simple negation)
        {
          pattern: /(?<![a-zA-Z_])[+\/*%]|(?<![a-zA-Z_0-9])-(?![a-zA-Z_0-9}])/,
          message: "arithmetic operator - compute value in ViewModel"
        },
        # Logical operators
        {
          pattern: /&&|\|\|/,
          message: "logical operator (&&, ||) - move logic to ViewModel"
        },
        # Elvis operator (null coalescing)
        {
          pattern: /\?:/,
          message: "elvis operator (?:) - handle null in ViewModel"
        },
        # Method calls with arguments (but allow simple property access)
        {
          pattern: /\.\w+\([^)]+\)/,
          message: "method call with arguments - move to ViewModel"
        },
        # String interpolation
        {
          pattern: /\$\{|\$[a-zA-Z]/,
          message: "string interpolation - compose string in ViewModel"
        },
        # Array subscript with complex expression
        {
          pattern: /\[[^\]]*[+\-*\/<>=]/,
          message: "complex array subscript - simplify in ViewModel"
        },
        # Type casting
        {
          pattern: /\s+as[?\s]+\w+/,
          message: "type casting - handle type conversion in ViewModel"
        },
        # Not-null assertion
        {
          pattern: /!!/,
          message: "not-null assertion (!!) - handle nullability safely in ViewModel"
        },
        # Lambda expressions
        {
          pattern: /\{[^}]*->[^}]*\}/,
          message: "lambda expression - move to ViewModel"
        },
        # Range operators
        {
          pattern: /\.\.|\s+until\s+|\s+downTo\s+/,
          message: "range operator - create range in ViewModel"
        },
        # let/run/apply/also blocks
        {
          pattern: /\.(let|run|apply|also|with)\s*\{/,
          message: "scope function - move logic to ViewModel"
        }
      ].freeze

      # Allowed simple patterns that look like logic but are acceptable
      ALLOWED_PATTERNS = [
        # Simple property access (including safe call)
        /^@\{[a-zA-Z_][a-zA-Z0-9_]*(\??\.[a-zA-Z_][a-zA-Z0-9_]*)*\}$/,
        # Simple negation for boolean
        /^@\{![a-zA-Z_][a-zA-Z0-9_]*\}$/,
        # Simple array access with constant index
        /^@\{[a-zA-Z_][a-zA-Z0-9_]*\[\d+\]\}$/,
        # Action bindings (callbacks)
        /^@\{on[A-Z][a-zA-Z0-9_]*\}$/,
        # data. prefix for accessing data properties (e.g., @{data.name} in Collection cells)
        /^@\{data\.[a-zA-Z_][a-zA-Z0-9_.]*\}$/
      ].freeze

      def initialize
        @warnings = []
        @data_properties = Set.new
      end

      # Validate all bindings in a JSON component tree
      # @param json_data [Hash] The root component
      # @param file_name [String] The file name for error messages
      # @return [Array<String>] Array of warning messages
      def validate(json_data, file_name = nil)
        @warnings = []
        @current_file = file_name
        @data_properties = Set.new

        # First pass: collect all data property names
        collect_data_properties(json_data)

        # Second pass: validate bindings
        validate_component(json_data)
        @warnings
      end

      # Check if there are any warnings
      def has_warnings?
        !@warnings.empty?
      end

      # Print all warnings to stdout
      def print_warnings
        @warnings.each do |warning|
          puts "\e[33m[KJUI Binding Warning]\e[0m #{warning}"
        end
      end

      # Check a single binding expression
      # @param binding_expr [String] The binding expression (without @{ })
      # @param attribute_name [String] The attribute name
      # @param component_type [String] The component type
      # @return [Array<String>] Array of warning messages
      def check_binding(binding_expr, attribute_name, component_type)
        warnings = []

        # Check if it's allowed simple pattern
        full_binding = "@{#{binding_expr}}"
        return warnings if allowed_pattern?(full_binding)

        # Check for business logic patterns
        BUSINESS_LOGIC_PATTERNS.each do |rule|
          if binding_expr.match?(rule[:pattern])
            context = @current_file ? "[#{@current_file}] " : ""
            warnings << "#{context}Binding '@{#{binding_expr}}' in '#{component_type}.#{attribute_name}' contains #{rule[:message]}"
          end
        end

        warnings
      end

      private

      # Collect all data property names from the component tree
      def collect_data_properties(component)
        return unless component.is_a?(Hash)

        # Check for data declarations
        if component['data'].is_a?(Array)
          component['data'].each do |data_item|
            next unless data_item.is_a?(Hash)
            # Skip ViewModel class declarations (they have 'class' key but no 'name')
            # e.g., { "class": "MyViewModel" } - this is a ViewModel class, not a property
            # But include property declarations: { "name": "userName", "class": "String" }
            next if data_item['class'] && !data_item['name']
            # Add property name to the set
            if data_item['name']
              @data_properties << data_item['name']
            end
          end
        end

        # Recurse into children
        children = component['child'] || component['children'] || []
        children = [children] unless children.is_a?(Array)
        children.each { |child| collect_data_properties(child) if child.is_a?(Hash) }

        # Recurse into sections
        if component['sections'].is_a?(Array)
          component['sections'].each do |section|
            next unless section.is_a?(Hash)
            ['header', 'footer', 'cell'].each do |key|
              collect_data_properties(section[key]) if section[key].is_a?(Hash)
            end
          end
        end
      end

      def validate_component(component, parent_type = nil)
        return unless component.is_a?(Hash)

        component_type = component['type'] || parent_type || 'Unknown'

        # Check each attribute for bindings
        component.each do |key, value|
          next if key == 'type' || key == 'child' || key == 'children' || key == 'sections'
          next if key == 'data' || key == 'generatedBy' || key == 'include' || key == 'style'

          check_value_for_bindings(value, key, component_type)
        end

        # Validate children
        children = component['child'] || component['children'] || []
        children = [children] unless children.is_a?(Array)
        children.each { |child| validate_component(child, component_type) if child.is_a?(Hash) }

        # Validate sections (Collection/Table)
        if component['sections'].is_a?(Array)
          component['sections'].each do |section|
            next unless section.is_a?(Hash)
            ['header', 'footer', 'cell'].each do |key|
              validate_component(section[key], component_type) if section[key].is_a?(Hash)
            end
          end
        end
      end

      def check_value_for_bindings(value, attribute_name, component_type)
        case value
        when String
          if value.start_with?('@{') && value.end_with?('}')
            binding_expr = value[2..-2] # Remove @{ and }
            binding_warnings = check_binding(binding_expr, attribute_name, component_type)
            @warnings.concat(binding_warnings)

            # Check if binding variables are defined in data
            check_undefined_variables(binding_expr, attribute_name, component_type)
          end
        when Hash
          value.each do |k, v|
            check_value_for_bindings(v, "#{attribute_name}.#{k}", component_type)
          end
        when Array
          value.each_with_index do |item, index|
            check_value_for_bindings(item, "#{attribute_name}[#{index}]", component_type)
          end
        end
      end

      # Check if variables in binding expression are defined in data
      def check_undefined_variables(binding_expr, attribute_name, component_type)
        # Skip data. prefix bindings (Collection cell bindings)
        return if binding_expr.start_with?('data.')

        # Extract variable names from the binding expression
        variables = extract_variables(binding_expr)

        variables.each do |var|
          unless @data_properties.include?(var)
            context = @current_file ? "[#{@current_file}] " : ""
            @warnings << "#{context}Binding variable '#{var}' in '#{component_type}.#{attribute_name}' is not defined in data. Add: { \"class\": \"#{infer_type(var, attribute_name)}\", \"name\": \"#{var}\" }"
          end
        end
      end

      # Extract variable names from binding expression
      def extract_variables(binding_expr)
        variables = Set.new

        # Remove string literals to avoid false positives
        expr = binding_expr.gsub(/'[^']*'/, '').gsub(/"[^"]*"/, '')

        # Match variable names (identifiers that are not keywords or literals)
        # Skip: numbers, true, false, null, visible, gone
        keywords = %w[true false null visible gone]

        expr.scan(/\b([a-zA-Z_][a-zA-Z0-9_]*)\b/).flatten.each do |match|
          next if keywords.include?(match)
          next if match =~ /^\d/ # Skip if starts with digit
          variables << match
        end

        variables.to_a
      end

      # Infer type from variable name and attribute context
      # Returns Kotlin type format
      def infer_type(var_name, attribute_name)
        # onClick, onXxx -> (() -> Unit)? (Kotlin callback type)
        return '(() -> Unit)?' if var_name.start_with?('on') && var_name[2]&.match?(/[A-Z]/)

        # xxxItems, xxxOptions, xxxList -> List<Any>
        return 'List<Any>' if var_name.end_with?('Items', 'Options', 'List', 'Args', 'Subcommands')

        # isXxx, hasXxx, canXxx, shouldXxx -> Boolean
        return 'Boolean' if var_name.start_with?('is', 'has', 'can', 'should')

        # xxxVisibility -> String
        return 'String' if var_name.end_with?('Visibility')

        # xxxIndex, xxxCount, xxxTab -> Int
        return 'Int' if var_name.end_with?('Index', 'Count', 'Tab')

        # Based on attribute name
        case attribute_name
        when 'onClick', 'onValueChanged', 'onValueChange', 'onTap'
          '(() -> Unit)?'
        when 'items'
          'CollectionDataSource'
        when 'sections'
          'List<Any>'
        when 'visibility', 'text', 'fontColor', 'background'
          'String'
        when 'selectedIndex', 'width', 'height'
          'Int'
        when 'hidden', 'enabled', 'disabled'
          'Boolean'
        else
          'Any'
        end
      end

      def allowed_pattern?(binding)
        ALLOWED_PATTERNS.any? { |pattern| binding.match?(pattern) }
      end
    end
  end
end
