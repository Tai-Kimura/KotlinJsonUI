plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.kotlinjsonui"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            // Disable minification for JitPack compatibility
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "consumer-rules.pro"
            )
            
            // Include debug sources in release build for unified library
            // Dynamic features will be controlled at runtime
        }
        debug {
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
        viewBinding = true
        dataBinding = true
        compose = true
        buildConfig = true
    }

    // composeOptions is no longer needed with Kotlin 2.0+ Compose plugin
    
    testOptions {
        targetSdk = 36
    }
    
    // Publishing variants are handled by vanniktech plugin
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    
    // Layout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2025.08.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.13.1")
    
    // Image loading
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    
    // Networking (for HotLoader)
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

/* Commented out - using vanniktech plugin instead
publishing {
    repositories {
        maven {
            name = "centralPortal"
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/upload")
            
            credentials {
                username = project.findProperty("centralPortalUsername") as String? ?: ""
                password = project.findProperty("centralPortalPassword") as String? ?: ""
            }
        }
    }
    
    publications {
        // 単一のAAR公開（releaseビルドを使用）
        register<MavenPublication>("release") {
            groupId = "io.github.tai-kimura"  // Maven Central向け
            artifactId = "kotlinjsonui"
            version = "1.0.2"

            afterEvaluate {
                from(components["release"])
            }
            
            // AARのみを公開（ソースは含めない）
            artifacts {
                // AAR is included by default from components["release"]
                // No additional artifacts needed
            }

            pom {
                name.set("KotlinJsonUI")
                description.set("A JSON-based UI framework for Android with Compose support")
                url.set("https://github.com/Tai-Kimura/KotlinJsonUI")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("tai-kimura")
                        name.set("Tai Kimura")
                        email.set("your-email@example.com") // Optional
                    }
                }
                
                scm {
                    url.set("https://github.com/Tai-Kimura/KotlinJsonUI")
                    connection.set("scm:git:git://github.com/Tai-Kimura/KotlinJsonUI.git")
                    developerConnection.set("scm:git:ssh://github.com/Tai-Kimura/KotlinJsonUI.git")
                }
            }
        }
    }
    
    repositories {
        // Maven Central
        maven {
            name = "MavenCentral"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: ""
                password = project.findProperty("ossrhPassword") as String? ?: ""
            }
        }
        
        // GitHub Packages (keep as alternative)
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Tai-Kimura/KotlinJsonUI")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
*/

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
    
    coordinates("io.github.tai-kimura", "kotlinjsonui", "1.0.1")
    
    pom {
        name.set("KotlinJsonUI")
        description.set("A JSON-based UI framework for Android with Compose support")
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