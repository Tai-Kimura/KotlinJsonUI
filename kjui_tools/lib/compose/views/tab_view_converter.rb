# frozen_string_literal: true

require_relative 'base_view_converter'

module KjuiTools
  module Compose
    module Views
      class TabViewConverter < BaseViewConverter
        def initialize(component, indent_level = 0, action_manager = nil, converter_factory = nil, view_registry = nil, binding_registry = nil)
          super(component, indent_level, action_manager, binding_registry)
          @converter_factory = converter_factory
          @view_registry = view_registry
        end
        
        def convert
          tabs = @component['tabs'] || []
          selected_index = get_binding_value('selectedIndex', 0)
          
          add_line "Column("
          indent do
            apply_modifiers
          end
          add_line ") {"
          
          indent do
            # State for selected tab
            if is_binding?(@component['selectedIndex'])
              property_name = extract_binding_property(@component['selectedIndex']).split('.').last
              add_line "var selectedTabIndex by remember { mutableStateOf(#{extract_binding_property(@component['selectedIndex'])}) }"
            else
              add_line "var selectedTabIndex by remember { mutableStateOf(#{selected_index}) }"
            end
            
            add_line ""
            
            # Tab row
            generate_tab_row(tabs)
            
            add_line ""
            
            # Tab content
            generate_tab_content(tabs)
          end
          
          add_line "}"
          
          generated_code
        end
        
        private
        
        def generate_tab_row(tabs)
          scrollable = @component['scrollable'] || tabs.length > 3
          
          if scrollable
            add_line "ScrollableTabRow("
          else
            add_line "TabRow("
          end
          
          indent do
            add_line "selectedTabIndex = selectedTabIndex,"
            
            # Container color
            if @component['containerColor'] || @component['backgroundColor']
              color = map_color(@component['containerColor'] || @component['backgroundColor'])
              add_line "containerColor = #{color},"
            end
            
            # Content color
            if @component['contentColor']
              color = map_color(@component['contentColor'])
              add_line "contentColor = #{color},"
            end
            
            # Indicator
            if @component['indicatorColor'] || @component['showIndicator'] != false
              add_line "indicator = { tabPositions ->"
              indent do
                add_line "TabRowDefaults.Indicator("
                indent do
                  add_line "modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),"
                  
                  if @component['indicatorColor']
                    color = map_color(@component['indicatorColor'])
                    add_line "color = #{color},"
                  end
                  
                  if @component['indicatorHeight']
                    add_line "height = #{@component['indicatorHeight']}dp"
                  end
                end
                add_line ")"
              end
              add_line "},"
            end
            
            # Divider
            if @component['showDivider'] != false
              add_line "divider = {"
              indent do
                add_line "HorizontalDivider("
                indent do
                  if @component['dividerColor']
                    color = map_color(@component['dividerColor'])
                    add_line "color = #{color},"
                  end
                  
                  if @component['dividerThickness']
                    add_line "thickness = #{@component['dividerThickness']}dp"
                  end
                end
                add_line ")"
              end
              add_line "}"
            end
          end
          add_line ") {"
          
          # Generate individual tabs
          indent do
            tabs.each_with_index do |tab, index|
              generate_tab(tab, index)
            end
          end
          
          add_line "}"
        end
        
        def generate_tab(tab, index)
          title = tab['title'] || "Tab #{index + 1}"
          icon = tab['icon']
          
          add_line "Tab("
          indent do
            add_line "selected = selectedTabIndex == #{index},"
            add_line "onClick = {"
            indent do
              add_line "selectedTabIndex = #{index}"
              
              if @component['onTabSelected']
                add_line "viewModel.#{@component['onTabSelected']}(#{index})"
              end
            end
            add_line "},"
            
            # Tab colors
            if has_tab_colors?
              add_line "selectedContentColor = #{map_color(@component['selectedTabColor'] || 'primary')},"
              add_line "unselectedContentColor = #{map_color(@component['unselectedTabColor'] || 'onSurface')},"
            end
            
            # Tab content
            if icon && title
              add_line "text = {"
              indent do
                add_line "Column("
                indent do
                  add_line "horizontalAlignment = Alignment.CenterHorizontally"
                end
                add_line ") {"
                indent do
                  generate_tab_icon(icon)
                  add_line "Text(text = #{quote(title)})"
                end
                add_line "}"
              end
              add_line "}"
            elsif icon
              add_line "icon = {"
              indent do
                generate_tab_icon(icon)
              end
              add_line "}"
            else
              add_line "text = { Text(text = #{quote(title)}) }"
            end
          end
          add_line ")"
        end
        
        def generate_tab_icon(icon)
          add_line "Icon("
          indent do
            if icon.start_with?('ic_') || icon.start_with?('icon_')
              add_line "painter = painterResource(id = R.drawable.#{icon}),"
            else
              add_line "imageVector = Icons.Default.#{to_pascal_case(icon)},"
            end
            add_line "contentDescription = null"
          end
          add_line ")"
        end
        
        def generate_tab_content(tabs)
          add_line "Box("
          indent do
            add_line "modifier = Modifier"
            add_modifier_line "fillMaxSize()"
            add_modifier_line "padding(16dp)"
          end
          add_line ") {"
          
          indent do
            add_line "when (selectedTabIndex) {"
            indent do
              tabs.each_with_index do |tab, index|
                add_line "#{index} -> {"
                indent do
                  # Generate content for this tab
                  if tab['content']
                    if @converter_factory
                      child_converter = @converter_factory.create_converter(
                        tab['content'],
                        @indent_level + 2,
                        @action_manager,
                        @converter_factory,
                        @view_registry,
                        @binding_registry
                      )
                      
                      if child_converter
                        child_code = child_converter.convert
                        child_lines = child_code.split("\n")
                        child_lines.each { |line| add_line line.strip unless line.strip.empty? }
                      end
                    end
                  else
                    add_line "Text(\"Content for #{tab['title'] || "Tab #{index + 1}"}\")"
                  end
                end
                add_line "}"
              end
            end
            add_line "}"
          end
          
          add_line "}"
        end
        
        def has_tab_colors?
          @component['selectedTabColor'] || @component['unselectedTabColor']
        end
      end
      
      # Alias
      class TabRowConverter < TabViewConverter; end
    end
  end
end