// Unified AAR build configuration
// This creates a single AAR that includes both debug and release components

android {
    // Custom build type that includes everything
    buildTypes {
        create("unified") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            
            // Include debug sources
            sourceSets {
                getByName("unified") {
                    java.srcDirs("src/main/java", "src/debug/java")
                    kotlin.srcDirs("src/main/kotlin", "src/debug/kotlin")
                    res.srcDirs("src/main/res", "src/debug/res")
                    
                    // Use release version of DynamicViewInitializer
                    kotlin.exclude("**/debug/**/DynamicViewInitializer.kt")
                }
            }
        }
    }
    
    publishing {
        singleVariant("unified") {
            // No sources jar to avoid conflicts
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("unified") {
            groupId = "io.github.tai-kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["unified"])
            }
            
            pom {
                name.set("KotlinJsonUI")
                description.set("A JSON-based UI framework for Android with automatic debug/release detection")
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
}