plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.kotlinjsonui.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kotlinjsonui.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Different app name for debug/release
        manifestPlaceholders["appName"] = "@string/app_name"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        // Release signing config (you'll need to create your own keystore)
        // Uncomment and configure when you have a release keystore
        /*
        create("release") {
            storeFile = file("release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
        */
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            signingConfig = signingConfigs.getByName("debug")
            
            // Enable Dynamic Mode and hot reload for debug builds
            buildConfigField("boolean", "ENABLE_DYNAMIC_MODE", "true")
            buildConfigField("boolean", "ENABLE_HOT_RELOAD", "true")
            
            resValue("string", "app_name_suffix", " (Debug)")
        }
        
        release {
            isDebuggable = false
            isMinifyEnabled = false  // Disable for now to speed up build
            isShrinkResources = false  // Disable for now to speed up build
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // Use debug for now, change to release when available
            
            // Disable Dynamic Mode and hot reload for release builds
            buildConfigField("boolean", "ENABLE_DYNAMIC_MODE", "false")
            buildConfigField("boolean", "ENABLE_HOT_RELOAD", "false")
            
            resValue("string", "app_name_suffix", "")
        }
    }

    // Build variants for different environments
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"http://localhost:8080\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.kotlinjsonui.com\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    // composeOptions is no longer needed with Kotlin 2.0+ Compose plugin
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":library"))
    
    // Core Android
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")
    implementation("androidx.activity:activity-compose:1.10.1")
    
    // ConstraintLayout for XML layouts
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    
    // RecyclerView for list views
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    
    // Material Design Components
    implementation("com.google.android.material:material:1.12.0")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2025.08.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.9.3")
    
    // ConstraintLayout for Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    
    // Image loading with Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Gson for dynamic components (debug only)
    debugImplementation("com.google.code.gson:gson:2.13.1")
}