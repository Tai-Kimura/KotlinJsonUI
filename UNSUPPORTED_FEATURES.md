# KotlinJsonUI - 未対応機能チェックリスト

このドキュメントは、SwiftJsonUIのテストケースから移植されたJSONファイルを分析し、KotlinJsonUIで現在未対応の機能をまとめたものです。

## 🔴 未実装のコンポーネント

### 必須コンポーネント
- [ ] **Scroll** - ScrollViewとの紐付けが必要（`radio_icons_test.json`, `segment_test.json`等で使用）
- [ ] **Toggle** - 現在TODOコメントが生成される（`components_test.json`, `form_test.json`で使用）

### ビジュアルエフェクトコンポーネント
- [ ] **GradientView** - グラデーション背景（`converter_test.json`, `components_test.json`で使用）
- [ ] **BlurView** - ぼかし効果（`converter_test.json`, `components_test.json`で使用）

### ナビゲーションコンポーネント
- [ ] **TabView** - タブナビゲーション（`converter_test.json`で使用）

### Webコンポーネント
- [ ] **Web/WebView** - 型マッピングの明確化が必要

## 🟡 コンポーネント別未対応属性

### TextField
#### イベントハンドラ
- [ ] `onFocus` - フォーカスイベント
- [ ] `onBlur` - フォーカス解除イベント
- [ ] `onBeginEditing` - 編集開始イベント
- [ ] `onEndEditing` - 編集終了イベント
- [ ] `onTextChange` - テキスト変更イベント（部分的にサポート済み）

#### Input Accessory（実装不要）
- ~~`accessoryBackground` - ツールバー背景色~~
- ~~`accessoryTextColor` - ツールバーテキスト色~~
- [ ] `doneText` - カスタム完了ボタンテキスト

#### ボーダースタイル
- [ ] `borderStyle` - ボーダースタイル
  - `"roundedRect"` - 角丸四角形
  - `"plain"` - プレーン

### Text/Label
#### 部分的テキストスタイリング（重要機能）
- [ ] **`partialAttributes`** - 範囲ベースのスタイリング配列
  - [ ] `range` - 適用範囲（配列または文字列）
  - [ ] `fontColor` - 範囲別フォント色
  - [ ] `fontWeight` - 範囲別フォント太さ
  - [ ] `fontSize` - 範囲別フォントサイズ
  - [ ] `background` - 範囲別背景色
  - [ ] `underline` - 範囲別下線
  - [ ] `strikethrough` - 範囲別取り消し線
  - [ ] `onclick` - 範囲別クリックイベント

#### リンク検出
- [ ] `linkable` - URL/メールアドレスの自動リンク化

### Radio
#### カスタムアイコン
- [ ] `icon` - 未選択時のカスタムアイコン
- [ ] `selectedIcon` - 選択時のカスタムアイコン

#### データ構造
- [ ] `items` - オプション配列（`options`を`items`に切り替える必要あり）
- [ ] `selectedValue` - 選択値の文字列バインディング

#### グループ管理
- [ ] `group` - ラジオグループ識別子
- [ ] `checked` - デフォルト選択状態

### Segment
#### カラーカスタマイズ
- [ ] `items` - セグメントタイトル配列（現在は`segments`を使用）
- [ ] `selectedIndex` - インデックスバインディング（現在は`bind`を使用）
- [ ] `normalColor` - 通常時のテキスト色
- [ ] `selectedColor` - 選択時のテキスト色
- [ ] `tintColor` - 背景ティント色

#### 状態管理
- [ ] `onChange` - 変更イベントハンドラ
- [ ] `enabled` - 有効/無効状態

### Switch
#### プロパティ
- [ ] `text` - スイッチ横のラベルテキスト
- [ ] `isOn` - ブール状態（現在は`bind`を使用）
- [ ] `tint` vs `tintColor` - 色属性の統一
- [ ] `onValueChange` - 変更イベントハンドラ（部分的にサポート済み）

### Button
#### 状態別カラー
- [ ] `tapBackground` - タップ時の背景色
- [ ] `hilightColor` - ハイライト色（コード内に記載あるが未実装）

### ScrollView
#### スクロールインジケータ
- [ ] `showsVerticalScrollIndicator` - 垂直スクロールバー表示/非表示
- [ ] `showsHorizontalScrollIndicator` - 水平スクロールバー表示/非表示

#### スクロール制御
- [ ] `scrollEnabled` - スクロール有効/無効
- [ ] `keyboardAvoidance` - キーボード回避動作

## 🔵 共通レイアウト属性

### 高度な配置
- [ ] `alignCenterVerticalView` - 特定ビューに対する垂直中央揃え
- [ ] `alignCenterHorizontalView` - 特定ビューに対する水平中央揃え

### サイズ制御
- [ ] `idealWidth` - 理想的な幅
- [ ] `idealHeight` - 理想的な高さ

### 表示制御
- [ ] `clipToBounds` - 境界でコンテンツをクリップ
- [ ] `direction` - レイアウト方向（`rightToLeft`）
- [ ] `distribution` - 配分戦略（`fillEqually`）
- [ ] `edgeInset` - テキスト専用パディング（部分的にサポート済み）

## 🔧 システムレベルの課題

### イベントハンドラシステム
- [ ] **イベント命名の統一**
  - `onValueChange`
  - `onChange`
  - `onFocus`
  - `onBlur`
  - 等の整合性確保

### データバインディング
- [ ] **バインディング方式の統一**
  - `bind`
  - `selectedIndex`
  - `text`
  - `isOn`
  - 等の整合性確保

### コンポーネント型マッピング
- [ ] `Scroll` → `ScrollView`のマッピング
- [ ] `Toggle` → `Switch`のマッピング
- [ ] `Web` vs `WebView`の明確化

## 📍 実装優先度

### 高優先度 🔥
1. **コンポーネント型マッピング** - `Scroll` → `ScrollView`の対応
2. **部分的属性（partialAttributes）** - リッチテキストフォーマット機能
3. **イベントハンドラシステム** - 全コンポーネントでの標準化
4. **Radioカスタムアイコン** - UI カスタマイズの重要機能

### 中優先度 ⚡
1. **TextFieldイベント** - フォーカス/ブラー/編集イベント
2. **Segmentコントロール** - 色カスタマイズと適切な状態管理
3. **レイアウト配置** - 高度なアライメント属性
4. **ScrollViewインジケータ** - スクロールインジケータ制御

### 低優先度 💡
1. **Toggleコンポーネント** - Switchで代替可能
2. **GradientView/BlurView** - 高度なビジュアルエフェクト
3. **TabView** - 複雑なナビゲーションコンポーネント

## 📂 修正が必要なファイル

### 即座の対応が必要
1. `kjui_tools/lib/compose/components/text_component.rb` - partialAttributes サポート追加
2. `kjui_tools/lib/compose/components/textfield_component.rb` - イベントハンドラ追加
3. `kjui_tools/lib/compose/components/radio_component.rb` - アイコンサポートとグループ管理追加
4. `kjui_tools/lib/compose/components/segment_component.rb` - 属性名修正と色サポート
5. コンポーネント型マッピングファイル - `Scroll`、`Toggle`等のマッピング定義

## 📊 影響を受けるテストファイル

### 高影響
- `partial_attributes_test.json` - 部分的テキストスタイリング
- `radio_icons_test.json` - ラジオアイコンカスタマイズ
- `textfield_events_test.json` - TextFieldイベントハンドリング
- `segment_test.json` - セグメントコントロール

### 中影響
- `scroll_test.json` - スクロールビュー属性
- `switch_events_test.json` - スイッチイベント
- `button_test.json` - ボタン状態色

### 低影響
- `implemented_attributes_test.json` - 各種属性テスト
- `text_decoration_test.json` - テキスト装飾

## 🚀 実装ロードマップ

### Phase 1: 基本的な互換性（1-2週間）
- [ ] コンポーネント型マッピングの実装
- [ ] 基本的なイベントハンドラの統一
- [ ] TextFieldの主要イベント実装

### Phase 2: 中核機能（2-3週間）
- [ ] partialAttributes実装（AnnotatedString使用）
- [ ] Radioカスタムアイコン
- [ ] Segmentコントロール完全実装

### Phase 3: 高度な機能（3-4週間）
- [ ] GradientView/BlurView実装
- [ ] TabView実装
- [ ] 高度なレイアウト属性

### Phase 4: 最適化と改善（継続的）
- [ ] パフォーマンス最適化
- [ ] エラーハンドリング改善
- [ ] ドキュメント作成

---

*最終更新: 2025-08-21*
*分析対象: SwiftUITestAppから移植された10個のテストJSONファイル*