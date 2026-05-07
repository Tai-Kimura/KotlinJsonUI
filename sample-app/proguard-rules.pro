# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable

# Keep annotations
-keepattributes *Annotation*

# KotlinJsonUI library rules
-keep class com.kotlinjsonui.** { *; }
-keepclassmembers class com.kotlinjsonui.** { *; }

# Keep data classes
-keep class com.example.kotlinjsonui.sample.data.** { *; }
-keepclassmembers class com.example.kotlinjsonui.sample.data.** { *; }

# Keep ViewModels
-keep class com.example.kotlinjsonui.sample.viewmodels.** { *; }
-keepclassmembers class com.example.kotlinjsonui.sample.viewmodels.** { *; }

# Keep generated views
-keep class com.example.kotlinjsonui.sample.views.** { *; }
-keepclassmembers class com.example.kotlinjsonui.sample.views.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Kotlin serialization (if used)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Compose rules
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Material3
-keep class com.google.android.material.** { *; }

# Coil image loading
-keep class coil.** { *; }
-keep interface coil.** { *; }

# WebSocket (for hot reload - will be removed in release anyway)
-keep class org.java_websocket.** { *; }
-keepclassmembers class org.java_websocket.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep generic type information for Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Lifecycle
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(android.app.Application);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Remove logs in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove println in release builds
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}