# KotlinJsonUI Development Guide

## Project Overview
This project is creating an Android library (KotlinJsonUI) that is the Android/Kotlin equivalent of SwiftJsonUI. The library allows developers to build native Android UI using JSON configuration files.

## Project Goals
- Create a Gradle-installable Android library
- Implement all features from SwiftJsonUI for Android
- Support JSON-based UI declaration with data binding
- Provide Material Design components
- Enable distribution via Maven/JitPack

## Key Implementation Requirements

### Core Features to Implement
1. **JSON Parser**: Parse and validate JSON UI configurations
2. **Component Factory**: Create Android Views from JSON definitions
3. **Data Binding**: Support `@{variable}` syntax for two-way binding
4. **Style System**: External style files loaded from Styles/ directory
5. **Layout Engine**: Support ConstraintLayout, RelativeLayout, and LinearLayout
6. **Event Handling**: onclick (method call) vs onClick (closure execution)
7. **Code Generation Tools (kjui_tools)**:
   - XML: Generate Android XML layouts and ViewBinding
   - Compose: Generate @Composable functions and ViewModels

### Component Library (27+ components)
- Core: View, SafeAreaView, Label, Button, TextField, TextView
- Layout: ScrollView, RecyclerView (Collection), ListView (Table)
- Input: Switch, Spinner (SelectBox), RadioGroup, CheckBox, SeekBar (Slider), ProgressBar
- Media: ImageView, NetworkImage (with Glide/Coil), CircleImage, WebView
- Visual: GradientDrawable, CircleView, BlurView, IconLabel, ProgressBar, TabLayout (Segment)

## Project Structure
```
KotlinJsonUI/
├── library/                     # Android library module
│   ├── src/main/kotlin/
│   │   └── com/kotlinjsonui/
│   │       ├── core/           # Core parsing and factory
│   │       ├── components/     # UI component implementations
│   │       ├── binding/        # Data binding engine
│   │       ├── style/          # Style system
│   │       └── layout/         # Layout managers
│   └── build.gradle.kts
├── installer/                   # Installation scripts
│   ├── README.md               # Installation documentation
│   ├── bootstrap.sh            # Bootstrap script for remote installation
│   └── install_kjui.sh         # Main installer script
├── kjui_tools/                  # Code generation tools (structured like sjui_tools)
│   ├── bin/                    # Executable scripts
│   │   ├── kjui               # Main CLI executable (Kotlin script)
│   │   └── install_deps       # Dependency installer
│   ├── config/                # Configuration files
│   │   └── library_versions.json
│   ├── lib/                   # Core library code
│   │   ├── cli/              # Command Line Interface
│   │   │   ├── main.kt
│   │   │   ├── commands/     # CLI commands
│   │   │   │   ├── init.kt
│   │   │   │   ├── generate.kt
│   │   │   │   ├── build.kt
│   │   │   │   └── watch.kt
│   │   │   └── version.kt
│   │   ├── core/             # Core functionality
│   │   │   ├── json_loader.kt
│   │   │   ├── config_manager.kt
│   │   │   ├── file_watcher.kt
│   │   │   └── style_loader.kt
│   │   ├── xml/              # Android XML View System (like UIKit)
│   │   │   ├── json_analyzer.kt
│   │   │   ├── binding_manager.kt
│   │   │   ├── generators/
│   │   │   ├── handlers/
│   │   │   └── views/
│   │   └── compose/          # Jetpack Compose (like SwiftUI)
│   │       ├── json_to_compose.kt
│   │       ├── data_model_updater.kt
│   │       ├── generators/
│   │       ├── binding/
│   │       └── views/
│   └── scripts/              # Utility scripts
│       └── setup_project.sh
├── sample/                    # Sample applications
│   ├── xml_sample/           # XML-based sample app
│   └── compose_sample/       # Compose-based sample app
├── build.gradle.kts          # Root build file
├── settings.gradle.kts
└── gradle.properties
```

## Development Commands

### Build Commands
```bash
# Build the library
./gradlew :library:build

# Run tests
./gradlew :library:test

# Run lint checks
./gradlew :library:lint

# Build sample app
./gradlew :sample:assembleDebug

# Install sample app
./gradlew :sample:installDebug
```

### Code Quality
```bash
# Run Kotlin lint
./gradlew ktlintCheck

# Format Kotlin code
./gradlew ktlintFormat

# Run all checks
./gradlew check
```

## Testing Requirements
- Unit tests for all core components
- UI tests using Espresso
- Integration tests for JSON parsing
- Sample app with various JSON layouts

## Code Style Guidelines
- Follow Kotlin coding conventions
- Use KTX extensions where appropriate
- Implement proper null safety
- Add KDoc comments for public APIs
- Use sealed classes for component types

## Dependencies
```kotlin
// Core
implementation("org.jetbrains.kotlin:kotlin-stdlib")
implementation("androidx.core:core-ktx")
implementation("androidx.appcompat:appcompat")

// JSON parsing
implementation("com.google.code.gson:gson")

// UI
implementation("com.google.android.material:material")
implementation("androidx.constraintlayout:constraintlayout")

// Image loading
implementation("io.coil-kt:coil")

// Testing
testImplementation("junit:junit")
androidTestImplementation("androidx.test.espresso:espresso-core")
```

## Architecture Patterns
- **Factory Pattern**: For component creation
- **Builder Pattern**: For view construction
- **Observer Pattern**: For data binding
- **Strategy Pattern**: For layout strategies
- **Visitor Pattern**: For style application

## Android-Specific Considerations
1. **Context Management**: Pass context appropriately
2. **Lifecycle Awareness**: Handle configuration changes
3. **Resource Access**: Use Android resource system
4. **Theme Support**: Integrate with Material themes
5. **Performance**: Implement view recycling for lists

## Distribution Plan
1. Publish to Maven Central or JitPack
2. Provide comprehensive documentation
3. Create migration guide from SwiftJsonUI
4. Include sample JSON files
5. Provide code generation tools

## Reference Documentation
- Full SwiftJsonUI documentation: `SwiftJsonUI_Documentation.md`
- Original Swift implementation: `~/resource/SwiftJsonUI/`
- Wiki documentation: `~/resource/SwiftJsonUI_Wiki/`

## Current Progress
- [x] Document SwiftJsonUI features
- [ ] Set up Android library structure
- [ ] Implement JSON parser
- [ ] Create component factory
- [ ] Build basic components
- [ ] Add data binding
- [ ] Implement style system
- [ ] Complete all components
- [ ] Create sample app
- [ ] Configure distribution

## Important Notes
- Maintain API compatibility with SwiftJsonUI where possible
- Use Android native components (not custom drawing)
- Support Android API 21+ (Lollipop)
- Optimize for both phones and tablets
- Consider adding Compose support in future

## Development Workflow
1. Always run lint checks before committing
2. Write tests for new components
3. Update sample app with examples
4. Document public APIs with KDoc
5. Follow semantic versioning