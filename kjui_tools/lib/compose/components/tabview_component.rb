# frozen_string_literal: true

require_relative '../helpers/modifier_builder'
require_relative '../helpers/resource_resolver'

module KjuiTools
  module Compose
    module Components
      class TabviewComponent
        def self.generate(json_data, depth, required_imports = nil, parent_type = nil)
          # TabView maps to NavigationBar with NavigationBarItem in Compose (Material 3)
          required_imports&.add(:navigation_bar)
          required_imports&.add(:remember_state)
          required_imports&.add(:scaffold)
          required_imports&.add(:icons)

          tabs = json_data['tabs'] || []

          # Generate state variable for selected tab
          state_var = "selectedTab"
          selected_binding = json_data['selectedIndex']

          code = indent("// TabView with NavigationBar", depth)

          # If there's a binding, use it; otherwise create local state
          if selected_binding && selected_binding.is_a?(String) && selected_binding.start_with?('@{')
            binding_prop = selected_binding.gsub(/@\{|\}/, '')
            state_expr = "data.#{binding_prop}"
            setter_expr = "viewModel.updateData(mapOf(\"#{binding_prop}\" to it))"
          else
            code += "\n" + indent("var #{state_var} by remember { mutableStateOf(0) }", depth)
            state_expr = state_var
            setter_expr = "#{state_var} = it"
          end

          code += "\n\n" + indent("Scaffold(", depth)
          code += "\n" + indent("bottomBar = {", depth + 1)

          # NavigationBar
          code += "\n" + indent("NavigationBar(", depth + 2)

          # Tab bar background color
          if json_data['tabBarBackground']
            bg_color = Helpers::ResourceResolver.process_color(json_data['tabBarBackground'], required_imports)
            code += "\n" + indent("containerColor = #{bg_color},", depth + 3)
          end

          code += "\n" + indent(") {", depth + 2)

          # Generate NavigationBarItem for each tab
          tabs.each_with_index do |tab, index|
            title = tab['title'] || "Tab #{index + 1}"
            icon = tab['icon'] || 'circle'
            selected_icon = tab['selectedIcon'] || icon

            code += "\n" + indent("NavigationBarItem(", depth + 3)
            code += "\n" + indent("selected = #{state_expr} == #{index},", depth + 4)
            code += "\n" + indent("onClick = { #{setter_expr.gsub('it', index.to_s)} },", depth + 4)

            # Icon with selected/unselected state
            code += "\n" + indent("icon = {", depth + 4)
            code += "\n" + indent("Icon(", depth + 5)
            code += "\n" + indent("imageVector = if (#{state_expr} == #{index}) Icons.Filled.#{to_icon_name(selected_icon)} else Icons.Outlined.#{to_icon_name(icon)},", depth + 6)
            code += "\n" + indent("contentDescription = \"#{title}\"", depth + 6)
            code += "\n" + indent(")", depth + 5)
            code += "\n" + indent("},", depth + 4)

            # Label (show/hide based on showLabels)
            show_labels = json_data['showLabels'] != false
            if show_labels
              code += "\n" + indent("label = { Text(\"#{title}\") },", depth + 4)
            end

            # Tint colors
            if json_data['tintColor'] || json_data['unselectedColor']
              tint = json_data['tintColor'] ? Helpers::ResourceResolver.process_color(json_data['tintColor'], required_imports) : 'MaterialTheme.colorScheme.primary'
              unselected = json_data['unselectedColor'] ? Helpers::ResourceResolver.process_color(json_data['unselectedColor'], required_imports) : 'MaterialTheme.colorScheme.onSurfaceVariant'
              code += "\n" + indent("colors = NavigationBarItemDefaults.colors(", depth + 4)
              code += "\n" + indent("selectedIconColor = #{tint},", depth + 5)
              code += "\n" + indent("selectedTextColor = #{tint},", depth + 5)
              code += "\n" + indent("unselectedIconColor = #{unselected},", depth + 5)
              code += "\n" + indent("unselectedTextColor = #{unselected}", depth + 5)
              code += "\n" + indent(")", depth + 4)
            end

            # Badge
            if tab['badge']
              badge_value = tab['badge']
              if badge_value.is_a?(String) && badge_value.start_with?('@{')
                binding_prop = badge_value.gsub(/@\{|\}/, '')
                code = code.gsub(/icon = \{/, "icon = {\n#{indent('BadgedBox(badge = { Badge { Text(\"${data.' + binding_prop + '}\") } }) {', depth + 5)}")
              elsif badge_value.is_a?(Integer) && badge_value > 0
                code = code.gsub(/icon = \{/, "icon = {\n#{indent("BadgedBox(badge = { Badge { Text(\"#{badge_value}\") } }) {", depth + 5)}")
              end
            end

            code += "\n" + indent(")", depth + 3)
          end

          code += "\n" + indent("}", depth + 2)
          code += "\n" + indent("}", depth + 1)
          code += "\n" + indent(") { innerPadding ->", depth)

          # Tab content using when expression
          if tabs.any?
            code += "\n" + indent("Box(modifier = Modifier.padding(innerPadding)) {", depth + 1)
            code += "\n" + indent("when (#{state_expr}) {", depth + 2)

            tabs.each_with_index do |tab, index|
              code += "\n" + indent("#{index} -> {", depth + 3)

              # Content for each tab - reference view by name
              view_name = tab['view']
              if view_name
                # Convert snake_case to PascalCase for Kotlin class name
                pascal_name = view_name.split('_').map(&:capitalize).join
                code += "\n" + indent("#{pascal_name}View()", depth + 4)
              else
                code += "\n" + indent("Text(\"#{tab['title'] || "Tab #{index + 1}"} content\")", depth + 4)
              end

              code += "\n" + indent("}", depth + 3)
            end

            code += "\n" + indent("}", depth + 2)
            code += "\n" + indent("}", depth + 1)
          end

          code += "\n" + indent("}", depth)
          code
        end

        private

        def self.indent(text, level)
          return text if level == 0
          spaces = '    ' * level
          text.split("\n").map { |line|
            line.empty? ? line : spaces + line
          }.join("\n")
        end

        # Convert icon name to Material Icons format
        # e.g., "house" -> "Home", "person" -> "Person"
        def self.to_icon_name(icon)
          # Map common SF Symbol names to Material Icons
          icon_map = {
            'house' => 'Home',
            'house.fill' => 'Home',
            'person' => 'Person',
            'person.fill' => 'Person',
            'gearshape' => 'Settings',
            'gearshape.fill' => 'Settings',
            'gear' => 'Settings',
            'magnifyingglass' => 'Search',
            'heart' => 'Favorite',
            'heart.fill' => 'Favorite',
            'star' => 'Star',
            'star.fill' => 'Star',
            'bell' => 'Notifications',
            'bell.fill' => 'Notifications',
            'cart' => 'ShoppingCart',
            'cart.fill' => 'ShoppingCart',
            'list.bullet' => 'List',
            'square.grid.2x2' => 'GridView',
            'circle' => 'Circle'
          }
          icon_map[icon] || icon.split('.').first.capitalize
        end
      end
    end
  end
end
