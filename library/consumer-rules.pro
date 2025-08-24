# Consumer ProGuard rules for KotlinJsonUI library

# Remove dynamic package completely in release builds
-assumenosideeffects class com.kotlinjsonui.dynamic.** {
    public <methods>;
    private <methods>;
    protected <methods>;
}

# Remove all dynamic and hotloader related code  
-assumenosideeffects class com.kotlinjsonui.dynamic.hotloader.** {
    public <methods>;
    private <methods>;
    protected <methods>;
}