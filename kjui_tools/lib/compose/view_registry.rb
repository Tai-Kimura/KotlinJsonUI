# frozen_string_literal: true

require_relative 'views/base_view_converter'
require_relative 'views/view_converter'
require_relative 'views/text_converter'
require_relative 'views/button_converter'
require_relative 'views/image_converter'
require_relative 'views/textfield_converter'
require_relative 'views/scrollview_converter'
require_relative 'views/collection_converter'
require_relative 'views/toggle_converter'
require_relative 'views/slider_converter'
require_relative 'views/progress_converter'
require_relative 'views/checkbox_converter'
require_relative 'views/radio_converter'
require_relative 'views/spacer_converter'
require_relative 'views/divider_converter'
require_relative 'views/tab_view_converter'
require_relative 'views/include_converter'
require_relative 'views/card_converter'

module KjuiTools
  module Compose
    class ViewRegistry
      def initialize
        @converters = {}
        @registered_views = {}
        register_default_converters
      end
      
      def register_converter(type, converter_class)
        @converters[type.to_s] = converter_class
      end
      
      def get_converter_class(type)
        type_str = type.to_s
        
        # Return registered converter or default View converter
        @converters[type_str] || Views::ViewConverter
      end
      
      def register_view(id, component)
        @registered_views[id] = component if id
      end
      
      def get_registered_view(id)
        @registered_views[id]
      end
      
      private
      
      def register_default_converters
        # Container views
        register_converter('View', Views::ViewConverter)
        register_converter('VStack', Views::ViewConverter)
        register_converter('HStack', Views::ViewConverter)
        register_converter('ZStack', Views::ViewConverter)
        register_converter('Box', Views::ViewConverter)
        register_converter('Column', Views::ViewConverter)
        register_converter('Row', Views::ViewConverter)
        
        # Text views
        register_converter('Text', Views::TextConverter)
        register_converter('Label', Views::LabelConverter)
        
        # Button
        register_converter('Button', Views::ButtonConverter)
        
        # Image
        register_converter('Image', Views::ImageConverter)
        register_converter('NetworkImage', Views::NetworkImageConverter)
        register_converter('AsyncImage', Views::ImageConverter)
        
        # Input views
        register_converter('TextField', Views::TextFieldConverter)
        register_converter('EditText', Views::EditTextConverter)
        register_converter('TextInput', Views::TextFieldConverter)
        
        # Toggle/Switch
        register_converter('Toggle', Views::ToggleConverter)
        register_converter('Switch', Views::ToggleConverter)
        
        # Slider
        register_converter('Slider', Views::SliderConverter)
        
        # Progress indicators
        register_converter('ProgressBar', Views::ProgressConverter)
        register_converter('CircularProgress', Views::ProgressConverter)
        register_converter('LinearProgress', Views::ProgressConverter)
        register_converter('CircularProgressIndicator', Views::ProgressConverter)
        register_converter('LinearProgressIndicator', Views::ProgressConverter)
        
        # Selection controls
        register_converter('Checkbox', Views::CheckboxConverter)
        register_converter('RadioButton', Views::RadioConverter)
        register_converter('RadioGroup', Views::RadioConverter)
        
        # ScrollView
        register_converter('ScrollView', Views::ScrollViewConverter)
        register_converter('HorizontalScrollView', Views::ScrollViewConverter)
        register_converter('VerticalScrollView', Views::ScrollViewConverter)
        
        # Collection views
        register_converter('Collection', Views::CollectionConverter)
        register_converter('LazyColumn', Views::CollectionConverter)
        register_converter('LazyRow', Views::CollectionConverter)
        register_converter('LazyGrid', Views::CollectionConverter)
        register_converter('LazyVerticalGrid', Views::CollectionConverter)
        register_converter('LazyHorizontalGrid', Views::CollectionConverter)
        register_converter('RecyclerView', Views::CollectionConverter)
        register_converter('ListView', Views::CollectionConverter)
        register_converter('Table', Views::CollectionConverter)
        
        # Tab views
        register_converter('TabView', Views::TabViewConverter)
        register_converter('TabRow', Views::TabViewConverter)
        
        # Other views
        register_converter('Spacer', Views::SpacerConverter)
        register_converter('Divider', Views::DividerConverter)
        register_converter('HorizontalDivider', Views::DividerConverter)
        register_converter('VerticalDivider', Views::VerticalDividerConverter) if defined?(Views::VerticalDividerConverter)
        register_converter('Card', Views::CardConverter)
        register_converter('ElevatedCard', Views::CardConverter)
        register_converter('OutlinedCard', Views::CardConverter)
        register_converter('Surface', Views::SurfaceConverter)
        
        # Include special handler
        register_converter('Include', Views::IncludeConverter)
      end
    end
  end
end