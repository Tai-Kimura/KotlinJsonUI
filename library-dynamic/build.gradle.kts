plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.kotlinjsonui.dynamic"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }
    
    // Publishing variants are handled by vanniktech plugin
}

dependencies {
    // Depend on the core library
    implementation(project(":library"))
    
    // Compose dependencies
    implementation(platform("androidx.compose:compose-bom:2025.08.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    
    // Other dependencies
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
}

// Signing configuration for vanniktech plugin
signing {
    val signingKey = project.findProperty("signing.key") as String?
    val signingPassword = project.findProperty("signing.password") as String?
    
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

// Configure vanniktech plugin for Central Portal
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    
    coordinates("io.github.tai-kimura", "kotlinjsonui-dynamic", "1.0.2")
    
    pom {
        name.set("KotlinJsonUI Dynamic Components")
        description.set("Dynamic components for KotlinJsonUI - enables hot reload and runtime JSON updates")
        url.set("https://github.com/Tai-Kimura/KotlinJsonUI")
        
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        
        developers {
            developer {
                id.set("tai-kimura")
                name.set("Taichiro Kimura")
                email.set("kimura@tanosys.com")
            }
        }
        
        scm {
            connection.set("scm:git:git://github.com/Tai-Kimura/KotlinJsonUI.git")
            developerConnection.set("scm:git:ssh://github.com/Tai-Kimura/KotlinJsonUI.git")
            url.set("https://github.com/Tai-Kimura/KotlinJsonUI")
        }
    }
}