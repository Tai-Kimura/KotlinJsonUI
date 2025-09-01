# Publishing Multiple Variants (Debug/Release) Guide

## Maven Central / GitHub Packagesでの複数バリアント公開

### 方法1: 異なるartifactIdとして公開

```kotlin
// library/build.gradle.kts
publishing {
    publications {
        // Release版（通常のライブラリ）
        register<MavenPublication>("release") {
            groupId = "com.tai-kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
        }
        
        // Debug版（Dynamic Components含む）
        register<MavenPublication>("debug") {
            groupId = "com.tai-kimura"
            artifactId = "kotlinjsonui-debug"  // 別のartifactId
            version = "1.0.0"
            
            afterEvaluate {
                from(components["debug"])
            }
        }
    }
}
```

**ユーザーの使い方:**
```kotlin
// 通常版（本番用）
implementation("com.tai-kimura:kotlinjsonui:1.0.0")

// Debug版（開発用、Dynamic Components含む）
implementation("com.tai-kimura:kotlinjsonui-debug:1.0.0")
```

### 方法2: Classifierを使った公開

```kotlin
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.tai-kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
            
            // Debug版をclassifierとして追加
            artifact(tasks.register<Jar>("debugJar") {
                archiveClassifier.set("debug")
                from(android.sourceSets["debug"].output)
            })
        }
    }
}
```

**ユーザーの使い方:**
```kotlin
// 通常版
implementation("com.tai-kimura:kotlinjsonui:1.0.0")

// Debug版
implementation("com.tai-kimura:kotlinjsonui:1.0.0:debug")
```

### 方法3: バージョン番号で区別

```kotlin
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.tai-kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0"  // 通常版
            
            afterEvaluate {
                from(components["release"])
            }
        }
        
        register<MavenPublication>("debug") {
            groupId = "com.tai-kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0-debug"  // Debug版はサフィックス付き
            
            afterEvaluate {
                from(components["debug"])
            }
        }
    }
}
```

**ユーザーの使い方:**
```kotlin
// 通常版
implementation("com.tai-kimura:kotlinjsonui:1.0.0")

// Debug版
implementation("com.tai-kimura:kotlinjsonui:1.0.0-debug")
```

## JitPackでの複数バリアント

JitPackは基本的に1つのビルドしかサポートしませんが、ブランチやタグで区別できます：

### ブランチ戦略
```
main branch → release build
debug branch → debug build
```

**ユーザーの使い方:**
```kotlin
// Release版（mainブランチ）
implementation("com.github.Tai-Kimura:KotlinJsonUI:1.0.0")

// Debug版（debugブランチ）
implementation("com.github.Tai-Kimura:KotlinJsonUI:debug-SNAPSHOT")
```

### タグ戦略
```
v1.0.0 → release build
v1.0.0-debug → debug build
```

## 推奨アプローチ

### KotlinJsonUIの場合の推奨構成：

1. **開発者向け（Dynamic Components必要）**
   - Maven Centralに`kotlinjsonui-debug`として公開
   - またはGitHub Packagesでdebug版を提供

2. **本番アプリ向け（Static Views）**
   - JitPackまたはMaven Centralに通常版として公開

### 実装例：

```kotlin
// library/build.gradle.kts
android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
        
        // Debug版も公開する場合
        singleVariant("debug") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        // 本番用
        register<MavenPublication>("release") {
            groupId = "com.github.Tai-Kimura"
            artifactId = "kotlinjsonui"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
            
            pom {
                name.set("KotlinJsonUI")
                description.set("JSON-based UI framework for Android (Production)")
            }
        }
        
        // 開発用（Dynamic Components含む）
        register<MavenPublication>("debug") {
            groupId = "com.github.Tai-Kimura"
            artifactId = "kotlinjsonui-dev"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["debug"])
            }
            
            pom {
                name.set("KotlinJsonUI Development")
                description.set("JSON-based UI framework with Dynamic Components")
            }
        }
    }
}
```

## 公開コマンド

```bash
# Release版を公開
./gradlew :library:publishReleasePublicationToMavenCentralRepository

# Debug版を公開
./gradlew :library:publishDebugPublicationToMavenCentralRepository

# 両方公開
./gradlew :library:publish
```

## メリット・デメリット

### 複数バリアント公開のメリット
- ユーザーが用途に応じて選択可能
- 開発版と本番版を明確に分離
- Dynamic Componentsを必要な人だけが使える

### デメリット
- 管理が複雑になる
- バージョニングに注意が必要
- ドキュメントで明確に説明する必要がある

## 結論

**Maven Central/GitHub Packages**: 複数バリアントの公開が簡単
**JitPack**: 1つのバリアントのみ（ブランチ/タグで工夫は可能）

KotlinJsonUIの場合、以下がおすすめ：
1. JitPackで通常版（release）を公開
2. 開発者向けにGitHub Packagesでdebug版も提供
3. またはローカルビルドでdebug版を使ってもらう