# Maven Central 公開ガイド

## 事前準備

### 1. Sonatype OSSRH アカウント作成

1. **Sonatype JIRAアカウント作成**
   - https://issues.sonatype.org/secure/Signup!default.jspa
   - ユーザー名とパスワードを保存（後で使用）

2. **新規プロジェクトチケット作成**
   - https://issues.sonatype.org/secure/CreateIssue.jspa
   - Project: `Community Support - Open Source Project Repository Hosting (OSSRH)`
   - Issue Type: `New Project`
   - Summary: `Publishing com.github.tai-kimura:kotlinjsonui`
   - Group Id: `com.github.tai-kimura` または `io.github.tai-kimura`
   - Project URL: `https://github.com/Tai-Kimura/KotlinJsonUI`
   - SCM URL: `https://github.com/Tai-Kimura/KotlinJsonUI.git`

   **注意**: GitHub系のGroup IDの場合、GitHubアカウントの所有権確認が必要です。

### 2. GPG署名の設定

```bash
# GPGキーの生成
gpg --gen-key

# キーIDを確認
gpg --list-secret-keys --keyid-format=short

# 出力例:
# sec   rsa3072/XXXXXXXX 2024-01-01 [SC]
#       Key fingerprint = XXXX XXXX XXXX XXXX
# uid         [ultimate] Your Name <your-email@example.com>

# キーをサーバーに公開
gpg --keyserver keyserver.ubuntu.com --send-keys XXXXXXXX

# 秘密鍵をエクスポート（Base64形式）
gpg --export-secret-keys XXXXXXXX | base64 > private-key.txt
```

### 3. gradle.properties の設定

`~/.gradle/gradle.properties` または プロジェクトルートの `gradle.properties` に以下を追加：

```properties
# Sonatype OSSRH credentials
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

# GPG signing
signing.keyId=XXXXXXXX
signing.password=your-gpg-passphrase
signing.secretKeyRingFile=/Users/your-username/.gnupg/secring.gpg

# または Base64エンコードされた秘密鍵を使用
signing.key=LS0tLS1CRUdJTi... (base64エンコードされた秘密鍵)
signing.password=your-gpg-passphrase
```

## build.gradle.kts の確認

現在の設定で問題ありません：

```kotlin
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.tai-kimura"  // Maven Central用に変更
            artifactId = "kotlinjsonui"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
            
            pom {
                // POM情報は設定済み
            }
        }
    }
    
    repositories {
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
    }
}

signing {
    sign(publishing.publications["release"])
}
```

## 公開手順

### 1. ローカルでビルドテスト

```bash
# クリーンビルド
./gradlew clean

# ライブラリのビルド
./gradlew :library:build

# ローカルMavenリポジトリに公開（テスト）
./gradlew :library:publishToMavenLocal
```

### 2. Maven Central へ公開

```bash
# Stagingリポジトリへアップロード
./gradlew :library:publishReleasePublicationToMavenCentralRepository
```

### 3. Sonatype Nexus でリリース

1. https://s01.oss.sonatype.org/ にログイン
2. 左メニューの「Staging Repositories」をクリック
3. リストから自分のリポジトリを探す（io.github.tai-kimura-XXXX）
4. リポジトリを選択して「Close」ボタンをクリック
5. バリデーションが成功したら「Release」ボタンをクリック
6. 「Drop」をクリックしてStagingリポジトリをクリーンアップ

### 4. 確認

リリース後、以下で確認できます：

- **即座**: https://s01.oss.sonatype.org/content/groups/public/
- **10分後**: https://repo1.maven.org/maven2/
- **2時間後**: https://search.maven.org/

## ユーザーの使用方法

Maven Centralに公開後：

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.tai-kimura:kotlinjsonui:1.0.0")
}
```

## トラブルシューティング

### GPGエラーの場合

```bash
# 新しい形式のキーリングを古い形式に変換
gpg --export-secret-keys > ~/.gnupg/secring.gpg
```

### Stagingリポジトリが見つからない場合

- OSSRHのユーザー名/パスワードが正しいか確認
- Group IDの承認が完了しているか確認

### バリデーションエラーの場合

よくあるエラー：
- POM情報の不足（name, description, url, licenses, developers, scm）
- 署名の失敗
- sources.jarやjavadoc.jarの不足

## 自動化（GitHub Actions）

`.github/workflows/publish.yml`:

```yaml
name: Publish to Maven Central

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Decode GPG Key
        run: |
          echo "${{ secrets.GPG_KEY_BASE64 }}" | base64 --decode > private-key.asc
          gpg --import private-key.asc
      
      - name: Publish to Maven Central
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
        run: |
          ./gradlew publishReleasePublicationToMavenCentralRepository \
            -Psigning.keyId=$SIGNING_KEY_ID \
            -Psigning.password=$SIGNING_PASSWORD \
            -Psigning.secretKeyRingFile=private-key.asc \
            -PossrhUsername=$OSSRH_USERNAME \
            -PossrhPassword=$OSSRH_PASSWORD
```

## 次のステップ

1. Sonatype JIRAでチケットを作成
2. GPGキーを生成・設定
3. gradle.propertiesに認証情報を設定
4. テスト公開を実行
5. 問題なければ本番公開

準備ができたらお知らせください。公開をサポートします！