# Flavor/BuildType による自動切り替え設定

## 方法1: ビルドタイプによる切り替え（推奨）

ユーザーのアプリの `build.gradle.kts`:

```kotlin
dependencies {
    // Debug ビルドでは Dynamic Components 付き
    debugImplementation("com.github.Tai-Kimura:kotlinjsonui-debug:1.0.0")
    
    // Release ビルドでは通常版
    releaseImplementation("com.github.Tai-Kimura:kotlinjsonui:1.0.0")
}
```

これで自動的に：
- `./gradlew assembleDebug` → Dynamic Components付き
- `./gradlew assembleRelease` → 通常版

## 方法2: Product Flavor による切り替え

ユーザーのアプリの `build.gradle.kts`:

```kotlin
android {
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            // 開発環境用の設定
        }
        create("prod") {
            dimension = "environment"
            // 本番環境用の設定
        }
    }
}

dependencies {
    // 開発フレーバーでは Dynamic Components 付き
    devImplementation("com.github.Tai-Kimura:kotlinjsonui-debug:1.0.0")
    
    // 本番フレーバーでは通常版
    prodImplementation("com.github.Tai-Kimura:kotlinjsonui:1.0.0")
}
```

## 方法3: 単一ライブラリで内部切り替え（ライブラリ側の改修が必要）

### ライブラリ側の設定（KotlinJsonUI）

`library/build.gradle.kts`:

```kotlin
android {
    buildTypes {
        release {
            buildConfigField("boolean", "ENABLE_DYNAMIC_VIEWS", "false")
        }
        debug {
            buildConfigField("boolean", "ENABLE_DYNAMIC_VIEWS", "true")
        }
    }
    
    // Release版にもdebugソースを含める（ただし実行時に無効化）
    sourceSets {
        getByName("release") {
            java.srcDirs("src/main/java", "src/debug/java")
            kotlin.srcDirs("src/main/kotlin", "src/debug/kotlin")
        }
    }
}
```

`DynamicViewProvider.kt`:

```kotlin
object DynamicViewProvider {
    fun renderDynamicView(...): Boolean {
        // BuildConfigでの制御
        if (!BuildConfig.ENABLE_DYNAMIC_VIEWS) {
            fallback()
            return false
        }
        
        // または、ユーザーアプリのBuildConfigを参照
        if (!isDebugBuild()) {
            fallback()
            return false
        }
        
        // Dynamic View のレンダリング
        ...
    }
    
    private fun isDebugBuild(): Boolean {
        // ユーザーアプリのBuildConfigをリフレクションで確認
        return try {
            val appBuildConfig = Class.forName(
                "${context.packageName}.BuildConfig"
            )
            appBuildConfig.getField("DEBUG").getBoolean(null)
        } catch (e: Exception) {
            false
        }
    }
}
```

## 方法4: Gradle Configuration による自動選択

ユーザーのアプリの `build.gradle.kts`:

```kotlin
configurations {
    create("dynamicImplementation")
    create("staticImplementation")
}

dependencies {
    // 条件付きで依存関係を追加
    val isDynamicEnabled = project.hasProperty("dynamic") || 
                           gradle.startParameter.taskNames.any { it.contains("Debug") }
    
    if (isDynamicEnabled) {
        implementation("com.github.Tai-Kimura:kotlinjsonui-debug:1.0.0")
    } else {
        implementation("com.github.Tai-Kimura:kotlinjsonui:1.0.0")
    }
}
```

## 方法5: 単一アーティファクトで全部含める（最もシンプル）

### ライブラリ側の変更

```kotlin
// library/build.gradle.kts
android {
    buildTypes {
        release {
            // Release版にもdebugソースを含める
            // ただし、isMinifyEnabled = false にする必要がある
        }
    }
    
    // 全てのソースを含める
    sourceSets {
        getByName("main") {
            // debugソースもmainに含める
            java.srcDirs("src/main/java", "src/debug/java")
            kotlin.srcDirs("src/main/kotlin", "src/debug/kotlin")
        }
    }
}
```

### DynamicModeManager を拡張

```kotlin
object DynamicModeManager {
    private var isDynamicModeEnabled = false
    
    fun initialize(context: Context) {
        // アプリのBuildConfigを確認
        isDynamicModeEnabled = checkAppDebugMode(context)
    }
    
    fun isActive(): Boolean {
        return isDynamicModeEnabled && BuildConfig.DEBUG
    }
    
    private fun checkAppDebugMode(context: Context): Boolean {
        return try {
            // ユーザーアプリのBuildConfigをチェック
            val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
            val debugField = buildConfigClass.getField("DEBUG")
            debugField.getBoolean(null)
        } catch (e: Exception) {
            false
        }
    }
}
```

## 推奨アプローチ

### 最もシンプルで実用的な方法：**方法1（ビルドタイプ別依存）**

```kotlin
dependencies {
    debugImplementation("com.github.Tai-Kimura:kotlinjsonui-debug:1.0.0")
    releaseImplementation("com.github.Tai-Kimura:kotlinjsonui:1.0.0")
}
```

**メリット：**
- 設定が1行で済む
- 自動的に切り替わる
- アプリサイズが最適化される（releaseには不要なコードが含まれない）

**デメリット：**
- 2つのアーティファクトを管理する必要がある

### より柔軟な方法：**方法5（単一アーティファクト）**

全機能を含む単一ライブラリを提供し、実行時にユーザーアプリのBuildConfigを見て自動切り替え。

**メリット：**
- 1つのライブラリだけ管理
- ユーザーは何も考えなくていい

**デメリット：**
- Releaseビルドにも不要なコードが含まれる
- ProGuardの設定が複雑になる