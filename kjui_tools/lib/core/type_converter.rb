# frozen_string_literal: true

module KjuiTools
  module Core
    # Converts JSON primitive types to Kotlin types
    # This ensures cross-platform compatibility with SwiftJsonUI and ReactJsonUI
    class TypeConverter
      # Language key for this platform
      LANGUAGE = 'kotlin'

      # Available modes for this platform
      MODES = %w[compose xml].freeze

      # JSON type -> Kotlin type mapping (common types)
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
        'Dp' => 'Dp',
        'Alignment' => 'Alignment'
      }.freeze

      # Mode-specific type mapping (types that differ between compose and xml)
      MODE_TYPE_MAPPING = {
        'Color' => { 'compose' => 'Color', 'xml' => 'Int' },
        'color' => { 'compose' => 'Color', 'xml' => 'Int' },
        'Image' => { 'compose' => 'Painter', 'xml' => 'Drawable' },
        'image' => { 'compose' => 'Painter', 'xml' => 'Drawable' }
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
        'Alignment' => 'Alignment.TopStart',
        'Painter' => 'EmptyPainter()',
        'Drawable' => 'null'
      }.freeze

      class << self
        # Extract platform-specific value from a potentially nested hash
        # Supports three formats:
        # 1. Simple value: "String" -> "String"
        # 2. Language only: { "swift": "Int", "kotlin": "Int" } -> "Int"
        # 3. Language + mode: { "kotlin": { "compose": "Color", "xml": "Int" } } -> "Color" or "Int"
        #
        # @param value [Object] the value (String, Hash, or other)
        # @param mode [String] the mode (compose, xml)
        # @return [Object] the extracted value for this platform/mode
        def extract_platform_value(value, mode = nil)
          return value unless value.is_a?(Hash)

          # Try to get language-specific value
          lang_value = value[LANGUAGE]
          return value unless lang_value # No language key found, return original hash

          # If language value is a hash, try to get mode-specific value
          if lang_value.is_a?(Hash) && mode
            mode_value = lang_value[mode]
            return mode_value if mode_value

            # Fallback: try first available mode
            MODES.each do |m|
              return lang_value[m] if lang_value[m]
            end

            # No mode found, return the hash as-is (might be a custom structure)
            lang_value
          else
            # Language value is not a hash, return it directly
            lang_value
          end
        end

        # Convert JSON type to Kotlin type
        # @param json_type [String] the type specified in JSON
        # @param mode [String] the mode (compose, xml) for mode-specific types
        # @return [String] the corresponding Kotlin type
        def to_kotlin_type(json_type, mode = nil)
          return json_type if json_type.nil? || json_type.to_s.empty?

          type_str = json_type.to_s

          # Check mode-specific mapping first
          if mode && MODE_TYPE_MAPPING.key?(type_str)
            return MODE_TYPE_MAPPING[type_str][mode] || MODE_TYPE_MAPPING[type_str]['compose']
          end

          # Then check common mapping, or return as-is if not found
          TYPE_MAPPING[type_str] || type_str
        end

        # Check if the type is a primitive type
        # @param json_type [String] the type to check
        # @return [Boolean] true if it's a primitive type
        def primitive?(json_type)
          return false if json_type.nil? || json_type.to_s.empty?

          TYPE_MAPPING.key?(json_type.to_s)
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
        # @param mode [String] the mode (compose, xml)
        # @return [Hash] normalized data property with Kotlin type
        def normalize_data_property(data_prop, mode = nil)
          return data_prop unless data_prop.is_a?(Hash)

          normalized = data_prop.dup

          # Extract platform-specific class
          if normalized['class']
            raw_class = extract_platform_value(normalized['class'], mode)
            normalized['class'] = to_kotlin_type(raw_class, mode)
          end

          # Extract platform-specific defaultValue
          if normalized['defaultValue']
            normalized['defaultValue'] = extract_platform_value(normalized['defaultValue'], mode)
          end

          normalized
        end

        # Convert array of data properties
        # @param data_props [Array<Hash>] array of data properties
        # @param mode [String] the mode (compose, xml)
        # @return [Array<Hash>] normalized data properties
        def normalize_data_properties(data_props, mode = nil)
          return [] unless data_props.is_a?(Array)

          data_props.map { |prop| normalize_data_property(prop, mode) }
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
