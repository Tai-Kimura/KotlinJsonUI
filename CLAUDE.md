# KotlinJsonUI Development Guide

## Documentation Reference

When looking for information about KotlinJsonUI components, attributes, or features:
- **KotlinJsonUI Wiki**: `~/resource/KotlinJsonUI_wiki/`
- **SwiftJsonUI Wiki**: `~/resource/SwiftyJsonUI_wiki/`

Always check these wiki directories first for accurate documentation.

## Project Overview
This project is creating an Android library (KotlinJsonUI) that is the Android/Kotlin equivalent of SwiftJsonUI. The library allows developers to build native Android UI using JSON configuration files.

## Project Goals
- Create a Gradle-installable Android library
- Implement all features from SwiftJsonUI for Android
- Support JSON-based UI declaration with data binding
- Provide Material Design components
- Enable distribution via Maven/JitPack

## Platform Mode Policy (decided 2026-07-03)

**XML (Android Views) mode is maintenance-frozen. Jetpack Compose is the only
forward path.**

- New attributes, components, and modernization work target Compose only
  (static codegen + dynamic mode). The Views runtime and the kjui_tools XML
  codegen path stay as-is: they keep compiling but receive no new features.
- Bug reports against the XML path are closed as frozen unless they break
  the build.
- The XML path is a removal candidate for the next major release (3.0).
- Rationale: the Android platform itself is Compose-first (Material3,
  adaptive layout APIs), hotloader/dynamic mode here is Compose-only in
  practice, and there is no remaining reason to pick Views for a new
  JsonUI project.

## Key Implementation Requirements

### Core Features to Implement
1. **JSON Parser**: Parse and validate JSON UI configurations
2. **Component Factory**: Create Android Views from JSON definitions
3. **Data Binding**: Support `@{variable}` syntax for two-way binding
4. **Style System**: External style files loaded from Styles/ directory
5. **Layout Engine**: Support ConstraintLayout, RelativeLayout, and LinearLayout
6. **Event Handling**: onclick (method call) vs onClick (closure execution)
7. **Code Generation Tools (kjui_tools)**:
   - XML: Generate Android XML layouts and ViewBinding (maintenance-frozen —
     see Platform Mode Policy)
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
├── library/                     # :library — core published module (io.github.tai-kimura:kotlinjsonui)
│   └── src/main/kotlin/com/kotlinjsonui/
│       ├── core/               # KotlinJsonUI entry, Configuration, DynamicModeManager,
│       │                       # DynamicViewProvider (reflection bridge), ColorResolver, FontSpec
│       ├── components/         # Compose composables (SafeDynamicView, CollectionStack, SelectBox, ...)
│       ├── binding/            # BindingAdapters
│       ├── data/ embed/ graphics/ utils/ dynamic/
│       └── views/              # Kjui* Android View widgets (XML interop — maintenance-frozen)
├── library-dynamic/             # :library-dynamic — runtime JSON→Compose interpreter
│                                # (io.github.tai-kimura:kotlinjsonui-dynamic, same version as :library)
│   └── src/main/kotlin/com/kotlinjsonui/dynamic/
│       ├── DynamicView.kt / DataBindingContext.kt / TypedAttrs.kt / ...
│       ├── components/         # Dynamic*Component.kt (runtime mirror of kjui_tools emitters)
│       └── hotloader/          # WebSocket hot reload client
├── sample-app/                  # :sample-app — sample Compose app (dev/prod flavors)
├── conformance-host/            # :conformance-host — Compose-dynamic conformance runner (UIAutomator)
├── build.gradle.kts / settings.gradle.kts
├── gradle.properties            # version= — the single version source for BOTH published modules
└── jitpack.yml
```

**The code generation tool (`kjui_tools`) is Ruby, and does NOT live in this
repository.** It is part of the `jsonui-cli` monorepo:

- Dev checkout: `~/resource/jsonui-cli/kjui_tools/`
- Installed: `~/.jsonui-cli/kjui_tools/`
- Consumer projects get a project-local mirror via `jui sync_tool`

## Development Commands

### Build Commands
```bash
# Build the library
./gradlew :library:build

# Run tests
./gradlew :library:test

# Run lint checks
./gradlew :library:lint

# Build sample app (dev/prod flavors)
./gradlew :sample-app:assembleDevDebug

# Install sample app
./gradlew :sample-app:installDevDebug
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
- **KotlinJsonUI Wiki**: `~/resource/KotlinJsonUI_wiki/`
- **SwiftJsonUI Wiki**: `~/resource/SwiftyJsonUI_wiki/`
- Original Swift implementation: `~/resource/SwiftJsonUI/`

## Important Notes
- Maintain API compatibility with SwiftJsonUI where possible
- Use Android native components (not custom drawing)
- Support Android API 24+ (minSdk 24, compileSdk 36)
- Optimize for both phones and tablets
- Jetpack Compose is the primary UI path; XML (Android Views) is maintenance-frozen (see Platform Mode Policy)

## Development Workflow
1. Always run lint checks before committing
2. Write tests for new components
3. Update sample app with examples
4. Document public APIs with KDoc
5. Follow semantic versioning