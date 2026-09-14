plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.kotlinjsonui.dynamic"
    compileSdk = 37

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


    buildFeatures {
        compose = true
    }

    testOptions {
        // Plain-JVM unit tests exercise parse/bridge logic that emits
        // debug warnings through android.util.Log — return defaults
        // instead of throwing "not mocked".
        unitTests.isReturnDefaultValues = true
    }

    // Publishing variants are handled by vanniktech plugin
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// AGP 9 disables the release unit-test component by default, so
// testReleaseUnitTest stops existing. Falling back to testDebugUnitTest would
// silently change what the arms are about: library/src/main branches on
// BuildConfig.DEBUG in two places (DynamicModeManager.kt:28 seeds
// _isDynamicModeAvailable from it, DynamicModeToggle.kt:27 gates on it), and
// DynamicModeManagerTest reads that seed. Under debug the default flips
// false -> true and the suite would be asserting about a variant that is not
// the one shipped. Keep the release component instead.
androidComponents {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        // The property lives on HasUnitTestBuilder; the beforeVariants lambda
        // hands back the plain VariantBuilder, so it needs the narrowing cast.
        (variantBuilder as com.android.build.api.variant.HasUnitTestBuilder)
            .enableUnitTest = true
    }
}

dependencies {
    // Depend on the core library
    implementation(project(":library"))

    // Compose dependencies
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3.adaptive:adaptive")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    // EmbedContainer moved to the main library module (compiles into release
    // builds where library-dynamic isn't on the classpath). The viewmodel-
    // compose dependency lives over there now; we pick it up transitively.

    // Other dependencies
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.google.code.gson:gson:2.13.1")
    // coil3 (see library/build.gradle.kts); the network fetcher arrives on the
    // runtime classpath through :library.
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.code.gson:gson:2.13.1")
    testImplementation("androidx.window:window:1.4.0")

    // Instrumented pins for dynamic-face layout fidelity (chip-shaped
    // labels, horizontal collection spacing — downstream reports 2026-08-10).
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    // CoilNetworkFetcherTest: a local HTTP image server, so the "does a URL
    // image load at all" measurement needs no internet from the emulator.
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
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
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    // AGP 8.x + the bundled Dokka can't parse `PermittedSubclasses` (Java 17
    // sealed-class bytecode attribute) on a transitive dependency. After
    // EmbedContainer moved to `:library`, the `:library-dynamic`
    // javaDocReleaseGeneration task chokes resolving the cross-module
    // `EmbeddedEvent` sealed class. Switch the javadoc artifact to an empty
    // jar (Maven Central just requires existence) so publishing skips the
    // failing Dokka step.
    configure(
        com.vanniktech.maven.publish.AndroidSingleVariantLibrary(
            // The (String, Boolean, Boolean) constructor is deprecated in
            // 0.37.0. The typed one takes (JavadocJar, SourcesJar, variant) —
            // signature read from the plugin jar with javap, not guessed.
            //
            // Same meaning as `sourcesJar = true, publishJavadocJar = false`:
            // real sources, and an EMPTY javadoc jar. Central requires a
            // javadoc artifact to exist; Dokka fails on the sealed
            // `EmbeddedEvent`, which is why it was never generated.
            //
            // ⚠️ `JavadocJar.None()`, NOT `.Empty()`. This file already
            // registers its own `emptyJavadocJar` below and attaches it to
            // the publication by hand; `.Empty()` makes the PLUGIN register a
            // task of that same name and configuration fails outright
            // ("Cannot add task 'emptyJavadocJar'"). `publishJavadocJar =
            // false` meant exactly this: the plugin contributes nothing and
            // the hand-rolled jar is the artifact.
            javadocJar = com.vanniktech.maven.publish.JavadocJar.None(),
            sourcesJar = com.vanniktech.maven.publish.SourcesJar.Sources(),
            variant = "release"
        )
    )
    // Provide an empty javadoc.jar so Sonatype Central accepts the bundle.
    val emptyJavadocJar = tasks.register<Jar>("emptyJavadocJar") {
        archiveClassifier.set("javadoc")
    }
    afterEvaluate {
        publishing.publications.withType<MavenPublication>().configureEach {
            artifact(emptyJavadocJar)
        }
    }

    coordinates("io.github.tai-kimura", "kotlinjsonui-dynamic", project.findProperty("version") as String)

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
