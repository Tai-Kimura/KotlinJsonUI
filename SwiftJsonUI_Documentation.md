# SwiftJsonUI Documentation

## Overview
SwiftJsonUI is a declarative UI framework that allows building native iOS interfaces using JSON configuration. This document serves as the complete reference for creating the Android/Kotlin equivalent library.

## Core Concepts

### 1. JSON-Based UI Declaration
- UI layouts are defined in JSON format
- Components are instantiated via a factory pattern
- Supports both UIKit and SwiftUI rendering backends

### 2. Data Binding
- Variables bound using `@{variableName}` syntax
- Supports bidirectional binding for input components
- Real-time updates when data changes

### 3. Style System
- External style files loaded from `Styles/` directory
- Style attribute references JSON file name (without .json extension)
- Component attributes override style file attributes
- All attributes are placed at root level of JSON object

## Component Library

### Core Components

#### View
Base container component
```json
{
  "type": "View",
  "id": "container",
  "background": "#FFFFFF",
  "cornerRadius": 8,
  "padding": 16,
  "subviews": []
}
```

With external style:
```json
{
  "type": "View",
  "id": "container",
  "style": "card_view",
  "subviews": []
}
```

#### SafeAreaView
Container that respects device safe areas (Android: edge-to-edge display support)
```json
{
  "type": "SafeAreaView",
  "background": "#FFFFFF",
  "subviews": []
}
```
Note: On Android, this implements edge-to-edge display with proper insets handling for system bars.

#### Label
Text display component
```json
{
  "type": "Label",
  "text": "Hello World",
  "fontSize": 16,
  "fontWeight": "bold",
  "fontColor": "#000000",
  "textAlign": "center",
  "numberOfLines": 2
}
```

#### Button
Interactive button component
```json
{
  "type": "Button",
  "text": "Click Me",
  "onClick": "handleClick",
  "background": "#007AFF",
  "fontColor": "#FFFFFF",
  "cornerRadius": 8,
  "padding": [8, 16]
}
```

With external style:
```json
{
  "type": "Button",
  "style": "primary_button",
  "text": "Click Me",
  "onClick": "handleClick"
}
```

#### TextField
Single-line text input
```json
{
  "type": "TextField",
  "placeholder": "Enter text",
  "bind": "@{inputValue}",
  "borderWidth": 1,
  "borderColor": "#CCCCCC",
  "padding": 8,
  "cornerRadius": 4
}
```

#### TextView
Multi-line text input
```json
{
  "type": "TextView",
  "placeholder": "Enter long text",
  "bind": "@{textContent}",
  "height": 100,
  "borderWidth": 1,
  "borderColor": "#CCCCCC",
  "padding": 8
}
```

### Layout Components

#### ScrollView
Scrollable container
```json
{
  "type": "Scroll",
  "direction": "vertical",
  "showsIndicator": true,
  "flex": 1,
  "subviews": []
}
```

#### CollectionView
Grid/list layout component with custom cell classes
```json
{
  "type": "Collection",
  "id": "product_collection",
  "cellClasses": [
    {
      "className": "ProductListCollectionViewCell",
      "identifier": "ProductCell"
    },
    {
      "className": "HeaderCollectionViewCell"
    }
  ],
  "headerClasses": [
    {
      "className": "SectionHeaderView",
      "identifier": "HeaderView"
    }
  ],
  "columns": 2,
  "itemSpacing": 10,
  "lineSpacing": 10
}
```

Note: `cellClasses` define custom cell views that are registered automatically. Each cell class should inherit from the base collection view cell class and define its own layout.

#### TableView
List with sections and custom cell classes
```json
{
  "type": "Table",
  "id": "user_table",
  "cellClasses": [
    {
      "className": "UserTableViewCell",
      "identifier": "UserCell"
    },
    {
      "className": "DetailTableViewCell",
      "identifier": "DetailCell"
    }
  ],
  "rowHeight": 60,
  "separatorStyle": "singleLine",
  "separatorInset": {
    "left": 16,
    "right": 0
  }
}
```

Note: Similar to CollectionView, `cellClasses` register custom table view cells that can be used for rendering different row types.

### Input Components

#### Switch
Toggle switch component
```json
{
  "type": "Switch",
  "bind": "@{isEnabled}",
  "onTintColor": "#34C759",
  "thumbTintColor": "#FFFFFF"
}
```

#### SelectBox (Picker)
Dropdown selection component
```json
{
  "type": "SelectBox",
  "bind": "@{selectedValue}",
  "options": "@{options}",
  "placeholder": "Select an option"
}
```

#### Radio
Radio button group
```json
{
  "type": "Radio",
  "bind": "@{selectedOption}",
  "options": [
    {"value": "1", "label": "Option 1"},
    {"value": "2", "label": "Option 2"}
  ]
}
```

#### Checkbox
Checkbox component
```json
{
  "type": "Check",
  "bind": "@{isChecked}",
  "label": "Accept terms"
}
```

#### Slider
Value slider component
```json
{
  "type": "Slider",
  "bind": "@{sliderValue}",
  "min": 0,
  "max": 100,
  "step": 1
}
```

#### ProgressView
Progress indicator
```json
{
  "type": "Progress",
  "value": "@{progress}",
  "progressTintColor": "#007AFF",
  "trackTintColor": "#E5E5EA"
}
```

### Media Components

#### Image
Local image display
```json
{
  "type": "Image",
  "source": "logo.png",
  "width": 100,
  "height": 100,
  "contentMode": "aspectFit"
}
```

#### NetworkImage
Remote image with loading
```json
{
  "type": "NetworkImage",
  "url": "@{imageUrl}",
  "placeholder": "placeholder.png",
  "width": 200,
  "height": 200,
  "contentMode": "aspectFill"
}
```

#### CircleImage
Circular image component
```json
{
  "type": "CircleImage",
  "source": "@{profileImage}",
  "size": 80,
  "borderWidth": 2,
  "borderColor": "#007AFF"
}
```

#### WebView
Web content viewer
```json
{
  "type": "Web",
  "url": "@{webUrl}",
  "height": 300
}
```

### Visual Components

#### GradientView
Gradient background container
```json
{
  "type": "GradientView",
  "colors": ["#FF6B6B", "#4ECDC4"],
  "startPoint": {"x": 0, "y": 0},
  "endPoint": {"x": 1, "y": 1},
  "subviews": []
}
```

#### CircleView
Circular container
```json
{
  "type": "CircleView",
  "size": 100,
  "background": "#007AFF",
  "subviews": []
}
```

#### BlurView
Blur effect overlay
```json
{
  "type": "Blur",
  "style": "light",
  "intensity": 0.8,
  "subviews": []
}
```

#### IconLabel
Icon with text component
```json
{
  "type": "IconLabel",
  "icon": "star.fill",
  "text": "Favorites",
  "spacing": 4,
  "iconColor": "#FFD700",
  "fontColor": "#000000"
}
```

#### ActivityIndicator
Loading spinner
```json
{
  "type": "Indicator",
  "animating": "@{isLoading}",
  "style": "large",
  "color": "#007AFF"
}
```

#### SegmentedControl
Segmented selector
```json
{
  "type": "Segment",
  "bind": "@{selectedSegment}",
  "segments": ["Tab 1", "Tab 2", "Tab 3"],
  "selectedSegmentTintColor": "#007AFF"
}
```

## Layout System

### Constraint-Based Layout
```json
{
  "type": "View",
  "constraints": {
    "top": 20,
    "leading": 16,
    "trailing": -16,
    "height": 100
  }
}
```

### Relative Positioning
```json
{
  "type": "View",
  "layout": {
    "position": "relative",
    "top": "@{previous.bottom + 8}",
    "centerX": "@{parent.centerX}"
  }
}
```

### Flex Layout
```json
{
  "type": "View",
  "flex": 1,
  "flexDirection": "row",
  "justifyContent": "space-between",
  "alignItems": "center"
}
```

## Style System

### External Style Files
The `style` attribute references external JSON files from the `Styles/` directory.

#### Component with style reference:
```json
{
  "type": "Button",
  "style": "primary_button",
  "text": "Submit",
  "onClick": "handleSubmit"
}
```

#### Style file (`Styles/primary_button.json`):
```json
{
  "background": "#007AFF",
  "fontColor": "#FFFFFF",
  "cornerRadius": 8,
  "padding": [12, 24],
  "fontSize": 16,
  "fontWeight": "bold"
}
```

### Style Override
Component attributes override style file attributes:
```json
{
  "type": "Button",
  "style": "primary_button",
  "background": "#34C759",
  "text": "Success"
}
```

## Data Binding

### Data Definition
Variables are defined using the `data` key as an array of objects. All `defaultValue` entries are strings that get converted to native Kotlin types:
```json
{
  "type": "View",
  "id": "main_view",
  "data": [
    {
      "name": "userName",
      "class": "String",
      "defaultValue": "'Guest'"
    },
    {
      "name": "userAge",
      "class": "Int",
      "defaultValue": "25"
    },
    {
      "name": "isLoggedIn",
      "class": "Bool",
      "defaultValue": "false"
    },
    {
      "name": "items",
      "class": "[String]",
      "defaultValue": "[]"
    },
    {
      "name": "message",
      "class": "String",
      "defaultValue": "\"Hello World\""
    }
  ],
  "child": [
    {
      "type": "Label",
      "text": "@{userName}"
    }
  ]
}
```

### Data Properties
- **name**: Variable name used for binding (required)
- **class**: Data type (String, Int, Bool, [String], etc.) (required)  
- **defaultValue**: Initial value as string that gets converted to native code (optional)
  - String values: Use single quotes `'text'` or double quotes `"text"` with escaping
  - Numbers: Plain string like `"42"` or `"3.14"`
  - Booleans: `"true"` or `"false"`
  - Arrays: `"[]"` or `"[1, 2, 3]"`
  - Objects: `"null"` or JSON string

### Variable Reference
Use `@{variableName}` syntax to reference data-bound variables:
```json
{
  "type": "Label",
  "text": "Welcome, @{userName}!"
}
```

### Two-Way Binding
```json
{
  "type": "TextField",
  "bind": "@{userName}",
  "placeholder": "Enter name"
}
```

### Array Binding
```json
{
  "data": [
    {
      "name": "products",
      "class": "[Product]",
      "defaultValue": "[]"
    },
    {
      "name": "names",
      "class": "[String]",
      "defaultValue": "['Alice', 'Bob', 'Charlie']"
    }
  ],
  "child": [
    {
      "type": "Collection",
      "items": "@{products}",
      "cellClass": "ProductCell"
    }
  ]
}
```

## Event Handling

### onclick vs onClick

#### onclick (String value - calls ViewModel method)
The `onclick` attribute takes a string value that specifies the name of a ViewModel method to call:
```json
{
  "type": "Button",
  "text": "Submit",
  "onclick": "handleSubmit"
}
```
This automatically generates code to call `viewModel.handleSubmit()` when the button is clicked.

#### onClick (Variable reference - executes closure)
The `onClick` attribute references a closure variable using `@{}` syntax:
```json
{
  "type": "Button",
  "text": "Dynamic Action",
  "onClick": "@{dynamicAction}"
}
```
This executes the closure stored in the `dynamicAction` variable.

### Complete Example
```json
{
  "data": [
    {
      "name": "isEnabled",
      "class": "Bool",
      "defaultValue": "true"
    }
  ],
  "child": [
    {
      "type": "Button",
      "text": "Toggle State",
      "onclick": "toggleEnabled",
      "enabled": "@{isEnabled}"
    },
    {
      "type": "Button",
      "text": "Custom Action",
      "onClick": "@{customClosure}"
    }
  ]
}
```

### Component-Specific Events

#### TextField
```json
{
  "type": "TextField",
  "placeholder": "Enter text",
  "onTextChange": "handleTextChange"
}
```

#### Switch
```json
{
  "type": "Switch",
  "bind": "@{isEnabled}",
  "onValueChange": "handleSwitchToggle"
}
```

#### Segment
```json
{
  "type": "Segment",
  "segments": ["Tab 1", "Tab 2"],
  "valueChange": "handleSegmentChange"
}
```

### Gesture Events (Any Component)
```json
{
  "type": "View",
  "onClick": "handleTap",
  "onLongPress": {
    "closure": "handleLongPress",
    "duration": 2.0
  },
  "onPan": "handlePan",
  "onPinch": "handlePinch"
}
```

### Button Advanced Events
```json
{
  "type": "Button",
  "text": "Advanced",
  "onclick": [
    {"event": "Down", "action": "handleTouchDown"},
    {"event": "UpInside", "action": "handleTouchUp"},
    {"event": "Cancel", "action": "handleCancel"}
  ]
}
```

## Include System

### Basic Include
Include other JSON layout files without specifying type:
```json
{
  "include": "header"
}
```
This loads `header.json` from the Layouts directory.

### Include with Data
Pass data to included views using the `data` object. All values are strings that get converted to native types:
```json
{
  "include": "user_card",
  "data": {
    "userName": "'John Doe'",
    "userAge": "25",
    "isActive": "true"
  }
}
```

### Include with Variable References
Reference parent view variables using `@{}` syntax:
```json
{
  "include": "detail_view",
  "data": {
    "title": "@{mainTitle}",
    "content": "@{mainContent}"
  }
}
```

### Shared Data
Use `shared_data` for parent-child data synchronization:
```json
{
  "include": "child_view",
  "shared_data": {
    "parentValue": "@{sharedValue}"
  },
  "data": {
    "localValue": "Override value"
  }
}
```
Note: `data` values override `shared_data` values when both are present.

### Nested Includes
Includes can contain other includes:
```json
{
  "include": "common/navigation",
  "data": {
    "showBack": true
  }
}
```
This loads `common/navigation.json` from the Layouts directory.

## Platform-Specific Features

### iOS-Specific
- Safe area handling (iOS safe areas)
- Haptic feedback
- System colors and fonts
- SF Symbols support

### Android Equivalents
- Edge-to-edge display (SafeAreaView → WindowInsets)
- Material Design components
- ConstraintLayout
- View binding
- Android resource system
- Material icons (instead of SF Symbols)

## Implementation Architecture

### Core Modules

1. **JSON Parser**
   - Parse JSON configuration
   - Validate schema
   - Handle includes

2. **Component Factory**
   - Register component types
   - Create view instances
   - Apply configurations

3. **Style Loader**
   - Load external style JSON files from Styles/ directory
   - Merge style attributes with component attributes
   - Cache loaded styles for performance

4. **Cell Registry**
   - Register custom cell classes for Collection/Table views
   - Use reflection to instantiate cells by className
   - Support for multiple cell types per collection

5. **Data Binding Engine**
   - Variable management
   - Expression evaluation (`@{variable}` syntax)
   - Change notification

6. **Layout Engine**
   - Constraint solving
   - Relative positioning
   - Flex layout

7. **Event System**
   - Action dispatch
   - Gesture handling
   - Lifecycle events

### Generated Code (Android/Kotlin)
For `onclick="handleSubmit"`, the library generates:
```kotlin
class MainViewModel : ViewModel() {
    fun handleSubmit() {
        // Auto-generated method stub
    }
}
```

For `onClick="@{customAction}"`, it references:
```kotlin
val customAction: () -> Unit = {
    // Closure logic here
}
```

## Android Library Structure

### Project Structure
```
KotlinJsonUI/
├── library/                      # Core library module
│   └── src/main/kotlin/
│       └── com.kotlinjsonui/
│           ├── core/
│           ├── components/
│           ├── binding/
│           ├── style/
│           └── utils/
├── installer/                    # Installation scripts
│   ├── README.md                # Installation documentation
│   ├── bootstrap.sh             # Bootstrap script for remote installation
│   └── install_kjui.sh          # Main installer script
├── kjui_tools/                   # Code generation tools (equivalent to sjui_tools)
│   ├── bin/                     # Executable scripts
│   │   ├── kjui                 # Main CLI executable
│   │   └── install_deps         # Dependency installer
│   ├── config/                  # Configuration files
│   │   └── library_versions.json
│   ├── lib/                     # Core library code
│   │   ├── cli/                # Command Line Interface
│   │   │   ├── main.kt
│   │   │   ├── commands/       # CLI commands
│   │   │   │   ├── init.kt
│   │   │   │   ├── generate.kt
│   │   │   │   ├── build.kt
│   │   │   │   └── watch.kt
│   │   │   └── version.kt
│   │   ├── core/               # Core functionality
│   │   │   ├── json_loader.kt
│   │   │   ├── config_manager.kt
│   │   │   ├── file_watcher.kt
│   │   │   └── project_manager.kt
│   │   ├── xml/                # Android View System (XML)
│   │   │   ├── json_analyzer.kt
│   │   │   ├── binding_manager.kt
│   │   │   ├── generators/
│   │   │   │   ├── layout_generator.kt
│   │   │   │   ├── viewmodel_generator.kt
│   │   │   │   └── binding_generator.kt
│   │   │   ├── handlers/
│   │   │   │   ├── button_handler.kt
│   │   │   │   ├── textfield_handler.kt
│   │   │   │   └── switch_handler.kt
│   │   │   └── views/          # View converters
│   │   │       ├── view_converter.kt
│   │   │       ├── button_converter.kt
│   │   │       └── label_converter.kt
│   │   └── compose/            # Jetpack Compose
│   │       ├── json_to_compose.kt
│   │       ├── data_model_updater.kt
│   │       ├── generators/
│   │       │   ├── composable_generator.kt
│   │       │   └── viewmodel_generator.kt
│   │       ├── binding/
│   │       │   └── state_binding_handler.kt
│   │       └── views/          # Composable converters
│   │           ├── view_converter.kt
│   │           ├── button_converter.kt
│   │           └── text_converter.kt
│   └── scripts/                # Utility scripts
│       └── setup_project.sh
└── sample/                      # Sample application
    ├── xml_sample/             # XML-based sample
    └── compose_sample/         # Compose-based sample
```

### kjui_tools Implementation
The `kjui_tools` directory follows the same structure as SwiftJsonUI's `sjui_tools`:

#### Directory Organization
- **bin/**: Executable CLI scripts
- **config/**: Configuration files
- **lib/cli/**: Command-line interface and commands
- **lib/core/**: Core utilities shared across platforms
- **lib/xml/**: Android XML View System implementation (equivalent to UIKit)
- **lib/compose/**: Jetpack Compose implementation (equivalent to SwiftUI)
- **scripts/**: Utility and setup scripts

#### Platform Mapping
- `uikit/` → `xml/` (Traditional Android Views)
- `swiftui/` → `compose/` (Jetpack Compose)

#### Key Components
- JSON parsing and validation
- Style file loading from Styles/ directory
- Data binding with `@{variable}` syntax
- Code generation for both XML and Compose
- Hot reload support (future enhancement)

### Installer
The `installer/` directory provides automated installation scripts:

#### bootstrap.sh
Remote installation entry point:
```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash
```

#### install_kjui.sh
Main installer that:
- Downloads specified versions from GitHub
- Extracts kjui_tools to target location
- Installs Kotlin/Java dependencies
- Sets up executable permissions
- Creates initial configuration files
- Supports both XML and Compose modes

#### Installation Options
- `-v, --version`: Specify version/branch/tag
- `-d, --directory`: Installation directory
- `-m, --mode`: Installation mode (xml or compose)
- `-s, --skip-deps`: Skip dependency installation

## Gradle Configuration

### Library Module
```gradle
plugins {
    id 'com.android.library'
    id 'kotlin-android'
    id 'maven-publish'
}

android {
    compileSdk 34
    
    defaultConfig {
        minSdk 21
        targetSdk 34
    }
}

dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
}
```

## Key Differences for Android

1. **View System**: Android Views vs iOS UIView/SwiftUI
2. **Layout**: ConstraintLayout vs Auto Layout
3. **Data Binding**: Android Data Binding vs SwiftUI @State
4. **Resources**: Android resources vs iOS assets
5. **Lifecycle**: Activity/Fragment vs UIViewController
6. **Styling**: Android themes vs iOS appearance
7. **Cell Classes**: RecyclerView.Adapter/ViewHolder vs UICollectionView cells
   - Android uses ViewHolder pattern with RecyclerView
   - Cell classes need to be registered in adapter
   - Use reflection to instantiate cell classes by name

## Development Roadmap

### Phase 1: Core Foundation
- JSON parsing and validation
- Basic component factory
- Simple view creation

### Phase 2: Basic Components
- View, Label, Button
- TextField, Image
- Basic layouts

### Phase 3: Advanced Features
- Data binding
- Style system
- Event handling

### Phase 4: Full Component Set
- All UI components
- Complex layouts
- Animations

### Phase 5: Distribution
- Maven/JitPack publishing
- Documentation
- Sample apps

## Testing Strategy

1. Unit tests for parsers and engines
2. UI tests for components
3. Integration tests for full layouts
4. Performance benchmarks
5. Sample app demonstrations

## Performance Considerations

1. Lazy component instantiation
2. View recycling for lists
3. Efficient data binding updates
4. Style caching
5. Layout optimization

## Security Considerations

1. Input validation
2. Safe JSON parsing
3. Resource access control
4. Network image security
5. WebView sandboxing