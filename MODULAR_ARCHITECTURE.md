# KotlinJsonUI Modular Architecture

## Overview

KotlinJsonUI uses a modular architecture that allows dynamic components to be conditionally included at app build time. This ensures that:
- Release builds are optimized and don't include debug-only features
- Dynamic components are only compiled when needed
- Apps have full control over their build configuration

## Module Structure

### 1. Core Library (`library`)
- **Artifact ID**: `kotlinjsonui`
- **Description**: Core library with all production features
- **Includes**:
  - JSON parsing and UI generation
  - All standard components
  - Style system
  - Data binding
  - Static generated views

### 2. Dynamic Components Module (`library-dynamic`)
- **Artifact ID**: `kotlinjsonui-dynamic`
- **Description**: Optional module for development features
- **Includes**:
  - DynamicView components
  - Hot reload support
  - Runtime JSON updates
  - Debug-only features
- **Only included in debug builds**

## Installation

### For App Developers

#### Basic Setup (Production Only)
```kotlin
dependencies {
    implementation("io.github.tai-kimura:kotlinjsonui:1.0.0")
}
```

#### Development Setup (With Dynamic Components)
```kotlin
dependencies {
    implementation("io.github.tai-kimura:kotlinjsonui:1.0.0")
    
    // Include dynamic components only in debug builds
    debugImplementation("io.github.tai-kimura:kotlinjsonui-dynamic:1.0.0")
}
```

### Initialization

In your MainActivity or Application class:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize core library
        KotlinJsonUI.initialize(applicationContext)
        
        // Initialize dynamic components if available (debug builds only)
        if (BuildConfig.DEBUG) {
            try {
                // Use reflection to initialize dynamic components if present
                val dynamicInitClass = Class.forName("com.kotlinjsonui.dynamic.DynamicViewInitializer")
                val initMethod = dynamicInitClass.getMethod("initialize")
                val initInstance = dynamicInitClass.getField("INSTANCE").get(null)
                initMethod.invoke(initInstance)
                
                // Enable dynamic mode if needed
                DynamicModeManager.setDynamicModeEnabled(this, true)
            } catch (e: Exception) {
                // Dynamic module not available, continue without it
                Log.d("MainActivity", "Dynamic components not available")
            }
        }
    }
}
```

## Build Variants

### Debug Build
- Includes both `kotlinjsonui` and `kotlinjsonui-dynamic`
- Dynamic mode available
- Hot reload enabled
- Runtime JSON updates supported

### Release Build
- Only includes `kotlinjsonui`
- No dynamic components
- Optimized for production
- Smaller APK size

## How It Works

### Conditional Compilation
The dynamic components are **not pre-compiled** into the library. Instead:
1. Dynamic components exist as a separate module
2. Apps include this module only in debug builds using `debugImplementation`
3. The module is compiled as part of the app's build process
4. This ensures dynamic components match the app's build configuration

### Runtime Detection
The core library uses reflection to detect if dynamic components are available:
```kotlin
// In DynamicViewProvider
fun renderDynamicView(...) {
    if (DynamicModeManager.isActive() && renderer != null) {
        // Use dynamic renderer
    } else {
        // Fall back to static view or show fallback UI
    }
}
```

## Benefits

1. **Optimized Production Builds**: Release builds don't include unnecessary debug code
2. **Developer Flexibility**: Full dynamic features available during development
3. **Clean Separation**: Debug and release code are clearly separated
4. **Build-time Decision**: Dynamic components are compiled based on the app's build type
5. **No Runtime Overhead**: No checking for debug features in production

## Migration Guide

If you're currently using the old unified approach:

1. Update your dependencies:
```kotlin
// Old approach
implementation("io.github.tai-kimura:kotlinjsonui:0.9.0")

// New approach
implementation("io.github.tai-kimura:kotlinjsonui:1.0.0")
debugImplementation("io.github.tai-kimura:kotlinjsonui-dynamic:1.0.0")
```

2. Update initialization code to use reflection (see Initialization section above)

3. Remove any direct imports of dynamic components in your production code

## Publishing

Both modules are published separately to Maven Central:
- Core: `io.github.tai-kimura:kotlinjsonui:1.0.0`
- Dynamic: `io.github.tai-kimura:kotlinjsonui-dynamic:1.0.0`

For JitPack users:
- Core: `com.github.Tai-Kimura.KotlinJsonUI:library:1.0.0`
- Dynamic: `com.github.Tai-Kimura.KotlinJsonUI:library-dynamic:1.0.0`