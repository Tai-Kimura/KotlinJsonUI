# Add project specific ProGuard rules here.

# Keep all library classes except dynamic package
-keep class !com.kotlinjsonui.dynamic.**,com.kotlinjsonui.** { *; }

# Fix for missing StringConcatFactory
-dontwarn java.lang.invoke.StringConcatFactory

# Don't use -assumenosideeffects with wildcard - be more specific
# Remove only specific methods in dynamic package
-assumenosideeffects class com.kotlinjsonui.dynamic.** {
    public <methods>;
    private <methods>;
    protected <methods>;
}

# Remove all references to dynamic package
-dontwarn com.kotlinjsonui.dynamic.**
-dontnote com.kotlinjsonui.dynamic.**

# Keep Compose related classes
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep OkHttp for networking (used by HotLoader in debug only)
-dontwarn okhttp3.**
-dontwarn okio.**