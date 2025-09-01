# Maven Publishing Guide for KotlinJsonUI

## Installation for Users

### Option 1: JitPack (推奨 - すぐに使える)

1. Add JitPack repository to your root `build.gradle.kts`:
```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

2. Add the dependency to your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.Tai-Kimura:KotlinJsonUI:1.0.0")
}
```

### Option 2: Maven Central (準備中)

```kotlin
dependencies {
    implementation("com.tai-kimura:kotlinjsonui:1.0.0")
}
```

### Option 3: GitHub Packages

1. Add GitHub Packages repository:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Tai-Kimura/KotlinJsonUI")
        credentials {
            username = "your-github-username"
            password = "your-github-token"
        }
    }
}
```

2. Add the dependency:
```kotlin
dependencies {
    implementation("com.tai-kimura:kotlinjsonui:1.0.0")
}
```

## Publishing Instructions (For Maintainers)

### Publishing to JitPack

JitPack automatically builds from GitHub releases:

1. Create a new release on GitHub
2. Tag it with version (e.g., `v1.0.0`)
3. JitPack will automatically build and publish

Check build status at: https://jitpack.io/#Tai-Kimura/KotlinJsonUI

### Publishing to Maven Central

1. **Setup Sonatype Account:**
   - Create account at https://s01.oss.sonatype.org/
   - Create a JIRA ticket to claim your groupId

2. **Setup GPG Signing:**
```bash
# Generate GPG key
gpg --gen-key

# List keys
gpg --list-secret-keys

# Export for gradle.properties
gpg --export-secret-keys YOUR_KEY_ID | base64
```

3. **Configure gradle.properties:**
   - Copy `gradle.properties.template` to `gradle.properties`
   - Fill in your credentials

4. **Publish:**
```bash
# Build and publish to staging
./gradlew :library:publishReleasePublicationToMavenCentralRepository

# Then manually release from:
# https://s01.oss.sonatype.org/#stagingRepositories
```

### Publishing to GitHub Packages

```bash
# Set environment variables or gradle.properties
export USERNAME=your-github-username
export TOKEN=your-github-token

# Publish
./gradlew :library:publishReleasePublicationToGitHubPackagesRepository
```

## Versioning

Update version in `library/build.gradle.kts`:
```kotlin
version = "1.0.1"  // Update this
```

Follow Semantic Versioning:
- MAJOR.MINOR.PATCH
- 1.0.0 -> 1.0.1 (bug fixes)
- 1.0.0 -> 1.1.0 (new features, backward compatible)
- 1.0.0 -> 2.0.0 (breaking changes)