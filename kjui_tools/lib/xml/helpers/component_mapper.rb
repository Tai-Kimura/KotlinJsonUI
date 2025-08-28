#!/usr/bin/env ruby

module XmlGenerator
  class ComponentMapper
    def initialize
      @component_map = {
        # Layout containers
        'View' => 'LinearLayout',
        'HStack' => 'LinearLayout',
        'VStack' => 'LinearLayout',
        'ZStack' => 'FrameLayout',
        'RelativeView' => 'RelativeLayout',
        'ConstraintView' => 'androidx.constraintlayout.widget.ConstraintLayout',
        'ScrollView' => 'ScrollView',
        'HorizontalScrollView' => 'HorizontalScrollView',
        
        # Basic components
        'Label' => 'TextView',
        'Text' => 'TextView',
        'Button' => 'Button',
        'ImageButton' => 'ImageButton',
        'TextField' => 'EditText',
        'SecureField' => 'EditText',
        'TextView' => 'EditText',
        
        # Images
        'Image' => 'ImageView',
        'NetworkImage' => 'ImageView',
        'CircleImage' => 'de.hdodenhof.circleimageview.CircleImageView',
        
        # Selection components
        'Switch' => 'Switch',
        'Checkbox' => 'CheckBox',
        'Radio' => 'RadioButton',
        'RadioGroup' => 'RadioGroup',
        'Segment' => 'com.google.android.material.tabs.TabLayout',
        'Picker' => 'Spinner',
        'SelectBox' => 'Spinner',
        'DatePicker' => 'DatePicker',
        'TimePicker' => 'TimePicker',
        
        # Progress
        'ProgressBar' => 'ProgressBar',
        'Slider' => 'SeekBar',
        'Rating' => 'RatingBar',
        
        # Lists
        'List' => 'RecyclerView',
        'Table' => 'RecyclerView',
        'Collection' => 'RecyclerView',
        'Grid' => 'GridLayout',
        
        # Material Design components
        'Card' => 'com.google.android.material.card.MaterialCardView',
        'Chip' => 'com.google.android.material.chip.Chip',
        'ChipGroup' => 'com.google.android.material.chip.ChipGroup',
        'FloatingActionButton' => 'com.google.android.material.floatingactionbutton.FloatingActionButton',
        'BottomNavigation' => 'com.google.android.material.bottomnavigation.BottomNavigationView',
        'NavigationView' => 'com.google.android.material.navigation.NavigationView',
        'AppBar' => 'com.google.android.material.appbar.AppBarLayout',
        'Toolbar' => 'androidx.appcompat.widget.Toolbar',
        'TabLayout' => 'com.google.android.material.tabs.TabLayout',
        'TabView' => 'com.google.android.material.tabs.TabLayout',
        
        # Special components
        'WebView' => 'WebView',
        'VideoView' => 'VideoView',
        'MapView' => 'com.google.android.gms.maps.MapView',
        'AdView' => 'com.google.android.gms.ads.AdView',
        
        # Dividers and spacers
        'Divider' => 'View',
        'Spacer' => 'Space',
        
        # Custom components (will be replaced with includes)
        'Include' => 'include'
      }
    end

    def map_component(type)
      # Check for custom component prefix
      if type.start_with?('Custom')
        return 'include'
      end
      
      @component_map[type] || 'View'
    end

    def is_container?(type)
      containers = ['View', 'HStack', 'VStack', 'ZStack', 'ScrollView', 
                   'HorizontalScrollView', 'RelativeView', 'ConstraintView',
                   'Card', 'List', 'Table', 'Collection', 'Grid',
                   'RadioGroup', 'ChipGroup']
      containers.include?(type)
    end

    def needs_adapter?(type)
      ['List', 'Table', 'Collection', 'RecyclerView'].include?(type)
    end

    def is_material_component?(android_class)
      android_class.include?('com.google.android.material')
    end

    def get_layout_params_class(parent_type)
      case parent_type
      when 'RelativeLayout', 'RelativeView'
        'RelativeLayout.LayoutParams'
      when 'LinearLayout', 'View', 'HStack', 'VStack'
        'LinearLayout.LayoutParams'
      when 'FrameLayout', 'ZStack'
        'FrameLayout.LayoutParams'
      when 'ConstraintLayout', 'ConstraintView'
        'ConstraintLayout.LayoutParams'
      when 'GridLayout', 'Grid'
        'GridLayout.LayoutParams'
      else
        'ViewGroup.LayoutParams'
      end
    end

    def get_orientation(type)
      case type
      when 'HStack'
        'horizontal'
      when 'VStack', 'View'
        'vertical'
      else
        nil
      end
    end
  end
end