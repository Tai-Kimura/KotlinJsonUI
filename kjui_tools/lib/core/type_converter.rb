# frozen_string_literal: true

module KjuiTools
  module Core
    # Converts JSON primitive types to Kotlin types
    # This ensures cross-platform compatibility with SwiftJsonUI and ReactJsonUI
    class TypeConverter
      # JSON type -> Kotlin type mapping
      TYPE_MAPPING = {
        # Standard types (cross-platform)
        'String' => 'String',
        'string' => 'String',
        'Int' => 'Int',
        'int' => 'Int',
        'Integer' => 'Int',
        'integer' => 'Int',
        'Double' => 'Double',
        'double' => 'Double',
        'Float' => 'Float',
        'float' => 'Float',
        'Bool' => 'Boolean',
        'bool' => 'Boolean',
        'Boolean' => 'Boolean',
        'boolean' => 'Boolean',
        # iOS-specific types mapped to Kotlin equivalents
        'CGFloat' => 'Float',
        # Kotlin/Compose-specific types
        'Color' => 'Color',
        'Dp' => 'Dp',
        'Alignment' => 'Alignment'
      }.freeze

      # Default values for each Kotlin type
      DEFAULT_VALUES = {
        'String' => '""',
        'Int' => '0',
        'Double' => '0.0',
        'Float' => '0f',
        'Boolean' => 'false',
        'Color' => 'Color.Unspecified',
        'Dp' => '0.dp',
        'Alignment' => 'Alignment.TopStart'
      }.freeze

      class << self
        # Convert JSON type to Kotlin type
        # @param json_type [String] the type specified in JSON
        # @return [String] the corresponding Kotlin type
        def to_kotlin_type(json_type)
          return json_type if json_type.nil? || json_type.empty?

          TYPE_MAPPING[json_type] || json_type
        end

        # Check if the type is a primitive type
        # @param json_type [String] the type to check
        # @return [Boolean] true if it's a primitive type
        def primitive?(json_type)
          return false if json_type.nil? || json_type.empty?

          TYPE_MAPPING.key?(json_type)
        end

        # Get default value for a Kotlin type
        # @param kotlin_type [String] the Kotlin type
        # @return [String] the default value as Kotlin code
        def default_value(kotlin_type)
          DEFAULT_VALUES[kotlin_type] || 'null'
        end

        # Format a value for Kotlin code based on type
        # @param value [Object] the value to format
        # @param kotlin_type [String] the Kotlin type
        # @return [String] the formatted value as Kotlin code
        def format_value(value, kotlin_type)
          return 'null' if value.nil?

          case kotlin_type
          when 'String'
            format_string_value(value)
          when 'Int'
            value.to_i.to_s
          when 'Double'
            "#{value.to_f}"
          when 'Float'
            "#{value.to_f}f"
          when 'Boolean'
            value.to_s.downcase
          when 'Color'
            format_color_value(value)
          else
            value.to_s
          end
        end

        # Convert data property from JSON format to normalized format
        # @param data_prop [Hash] the data property from JSON
        # @return [Hash] normalized data property with Kotlin type
        def normalize_data_property(data_prop)
          return data_prop unless data_prop.is_a?(Hash)

          normalized = data_prop.dup
          if normalized['class']
            normalized['class'] = to_kotlin_type(normalized['class'])
          end
          normalized
        end

        # Convert array of data properties
        # @param data_props [Array<Hash>] array of data properties
        # @return [Array<Hash>] normalized data properties
        def normalize_data_properties(data_props)
          return [] unless data_props.is_a?(Array)

          data_props.map { |prop| normalize_data_property(prop) }
        end

        private

        def format_string_value(value)
          str = value.to_s
          # Handle already quoted strings
          if str.start_with?('"') && str.end_with?('"')
            str
          elsif str.start_with?("'") && str.end_with?("'")
            # Convert single quotes to double quotes
            inner = str[1..-2]
            "\"#{escape_string(inner)}\""
          else
            "\"#{escape_string(str)}\""
          end
        end

        def escape_string(str)
          str.gsub('\\', '\\\\').gsub('"', '\\"')
        end

        def format_color_value(value)
          if value.is_a?(String) && value.start_with?('#')
            hex = value.sub('#', '')
            if hex.length == 6
              "Color(0xFF#{hex.upcase})"
            elsif hex.length == 8
              "Color(0x#{hex.upcase})"
            else
              "Color.Unspecified"
            end
          else
            value.to_s
          end
        end
      end
    end
  end
end
