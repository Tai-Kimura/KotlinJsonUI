plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    // Needed by the vendored jsonui-test-runner driver models (@Serializable)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.kotlinjsonui.conformance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kotlinjsonui.conformance"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":library"))
    implementation(project(":library-dynamic"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation(platform("androidx.compose:compose-bom:2026.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")

    // library-dynamic parses layouts with Gson; the host reads fixture JSON with it too
    implementation("com.google.code.gson:gson:2.13.1")
    // Image loading used by dynamic image components
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Instrumented conformance suite + vendored jsonui-test-runner driver
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

// ---------------------------------------------------------------------------
// Conformance fixture sync
//
// Copies $CONFORMANCE_DIR/{fixtures,manifest.json} into this module's assets.
// CONFORMANCE_DIR is a runtime parameter (the conformance suite lives outside
// this repository) — it must never be hardcoded here.
// Usage: CONFORMANCE_DIR=/path/to/conformance ./gradlew :conformance-host:syncConformanceFixtures
// ---------------------------------------------------------------------------
tasks.register("syncConformanceFixtures") {
    description = "Sync conformance fixtures from \$CONFORMANCE_DIR into src/main/assets/conformance"
    group = "conformance"
    doLast {
        val conformanceDir = System.getenv("CONFORMANCE_DIR")
            ?: throw GradleException(
                "CONFORMANCE_DIR is not set. Point it at the conformance suite directory " +
                    "(containing manifest.json and fixtures/)."
            )
        val src = file(conformanceDir)
        require(src.resolve("manifest.json").isFile) {
            "CONFORMANCE_DIR does not contain manifest.json: $conformanceDir"
        }
        val dest = layout.projectDirectory.dir("src/main/assets/conformance").asFile
        dest.deleteRecursively()
        dest.mkdirs()
        src.resolve("manifest.json").copyTo(dest.resolve("manifest.json"))
        src.resolve("fixtures").copyRecursively(dest.resolve("fixtures"))
        println("Synced conformance fixtures: $conformanceDir -> ${dest.relativeTo(rootDir)}")

        // Companion embedded-screen layouts (Embed fixtures) must be
        // loadable by bare screen name through DynamicLayoutLoader, which
        // searches assets/Layouts/. Mirror every manifest `companions`
        // entry there under its bare name (embed_root.layout.json →
        // Layouts/embed_root.json).
        val manifest = groovy.json.JsonSlurper().parse(src.resolve("manifest.json")) as Map<*, *>
        val fixtures = manifest["fixtures"] as? List<*> ?: emptyList<Any>()
        val companions = fixtures
            .filterIsInstance<Map<*, *>>()
            .flatMap { (it["companions"] as? List<*>).orEmpty() }
            .filterIsInstance<String>()
            .toSortedSet()
        if (companions.isNotEmpty()) {
            val layoutsDest = layout.projectDirectory.dir("src/main/assets/Layouts").asFile
            layoutsDest.mkdirs()
            for (companion in companions) {
                val file = src.resolve(companion)
                require(file.isFile) { "companion layout missing: $companion" }
                val bare = file.name.removeSuffix(".layout.json") + ".json"
                file.copyTo(layoutsDest.resolve(bare), overwrite = true)
            }
            println("Synced ${companions.size} companion embedded-screen layout(s) -> assets/Layouts")
        }
    }
}
