plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
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
    
    publishing {
        singleVariant("release") {
            withJavadocJar()
        }
    }
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

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.tai-kimura"
            artifactId = "kotlinjsonui-dynamic"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("KotlinJsonUI Dynamic Components")
                description.set("Dynamic components for KotlinJsonUI - enables hot reload and runtime JSON updates")
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
                        email.set("your-email@example.com")
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
        
        // GitHub Packages
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