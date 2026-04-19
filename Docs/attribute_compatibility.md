# KotlinJsonUI 属性互換性ドキュメント

このドキュメントは、Android XML、Kotlin Compose Static (kjui build)、Kotlin Compose Dynamic の3つのレンダリングモード間での
JSON属性名の違いを記録します。

## 属性定義の場所

### Android XML (library)
各コンポーネントクラス内で処理。
`library/src/main/kotlin/com/kotlinjsonui/`

| パッケージ | 対応コンポーネント |
|----------|-------------------|
| `components/` | 再利用可能なUIコンポーネント |
| `views/` | カスタムAndroidビュー |
| `binding/` | データバインディングアダプター |
| `core/` | 設定、初期化 |

### Kotlin Compose Static (kjui build)
Rubyコンバーターで `@component['属性名']` を使用して処理。
`kjui_tools/lib/compose/views/`

| ファイル | 対応コンポーネント |
|----------|-------------------|
| `base_view_converter.rb` | 共通属性 |
| `text_converter.rb` | Text, Label |
| `textfield_converter.rb` | TextField |
| `textview_converter.rb` | TextView |
| `button_converter.rb` | Button |
| `image_converter.rb` | Image |
| `network_image_converter.rb` | NetworkImage |
| `switch_converter.rb` | Switch |
| `slider_converter.rb` | Slider |
| `selectbox_converter.rb` | SelectBox, Spinner |
| `progress_converter.rb` | Progress |
| `radio_converter.rb` | Radio |
| `checkbox_converter.rb` | Checkbox |
| `segment_converter.rb` | Segment, TabLayout |
| `collection_converter.rb` | Collection, RecyclerView |
| `table_converter.rb` | Table, ListView |
| `scrollview_converter.rb` | ScrollView |
| `view_converter.rb` | View, Container |

### Kotlin Compose Dynamic
各`Dynamic*Component.kt`ファイルで `json.get("属性名")` を使用して処理。
`library-dynamic/src/main/kotlin/com/kotlinjsonui/dynamic/components/`

| ファイル | 対応コンポーネント |
|----------|-------------------|
| `DynamicTextComponent.kt` | Text, Label |
| `DynamicTextFieldComponent.kt` | TextField |
| `DynamicTextViewComponent.kt` | TextView |
| `DynamicButtonComponent.kt` | Button |
| `DynamicImageComponent.kt` | Image |
| `DynamicNetworkImageComponent.kt` | NetworkImage |
| `DynamicCircleImageComponent.kt` | CircleImage |
| `DynamicSwitchComponent.kt` | Switch |
| `DynamicCheckBoxComponent.kt` | Checkbox |
| `DynamicRadioComponent.kt` | Radio |
| `DynamicSliderComponent.kt` | Slider |
| `DynamicSelectBoxComponent.kt` | SelectBox, Spinner |
| `DynamicProgressComponent.kt` | Progress |
| `DynamicIndicatorComponent.kt` | Indicator |
| `DynamicSegmentComponent.kt` | Segment, TabLayout |
| `DynamicCollectionComponent.kt` | Collection, RecyclerView, LazyGrid |
| `DynamicTableComponent.kt` | Table, ListView |
| `DynamicScrollViewComponent.kt` | ScrollView |
| `DynamicContainerComponent.kt` | View, Container |
| `DynamicConstraintLayoutComponent.kt` | ConstraintLayout |
| `DynamicGradientViewComponent.kt` | GradientView |
| `DynamicWebComponent.kt` | Web |
| `DynamicTabViewComponent.kt` | TabView |

---

## 属性名の不一致一覧

### 1. イベントハンドラー系

| 機能 | Android XML | Compose Static | Compose Dynamic | 推奨統一名 |
|------|-------------|----------------|-----------------|------------|
| タップイベント | `onclick` | `onclick` | `onclick` | `onclick` |
| ロングタップ | `onLongClick` | ❌ 未実装 | ❌ 未実装 | `onLongPress` |
| スイッチ値変更 | `onValueChange` | `onValueChange` | `onValueChange` | `onValueChange` |
| セグメント変更 | `onSelect` | `onSelect` | `onSelect` | `onSelect` |
| テキスト変更 | `onTextChange` | `onTextChange` | `onTextChange` | `onTextChange` |
| 表示時 | ❌ 未実装 | ❌ 未実装 | ❌ 未実装 | `onAppear` |
| 非表示時 | ❌ 未実装 | ❌ 未実装 | ❌ 未実装 | `onDisappear` |

### 2. パディング系 ✅ 統一完了

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 複合パディング | `paddings` (配列) | `paddings` | `paddings` | ✅ 統一済み |
| 上パディング | `paddingTop` | `paddingTop` | `paddingTop` | ✅ 統一済み |
| 下パディング | `paddingBottom` | `paddingBottom` | `paddingBottom` | ✅ 統一済み |
| 左パディング | `paddingLeft` / `paddingStart` | `paddingLeft` | `paddingStart` | ✅ 統一済み |
| 右パディング | `paddingRight` / `paddingEnd` | `paddingRight` | `paddingEnd` | ✅ 統一済み |
| 単一パディング | `padding` | `padding` | `padding` | ✅ 統一済み |

### 3. マージン系 ✅ 統一完了

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 複合マージン | `margins` (配列) | `margins` | `margins` | ✅ 統一済み |
| 上マージン | `topMargin` | `topMargin` | `topMargin` | ✅ 統一済み |
| 下マージン | `bottomMargin` | `bottomMargin` | `bottomMargin` | ✅ 統一済み |
| 左マージン | `leftMargin` / `startMargin` | `leftMargin` | `startMargin` | ✅ 統一済み |
| 右マージン | `rightMargin` / `endMargin` | `rightMargin` | `endMargin` | ✅ 統一済み |

### 4. サイズ/フレーム系

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 幅 | `width` | `width` | `width` | 統一済み |
| 高さ | `height` | `height` | `height` | 統一済み |
| 最小幅 | `minWidth` | `minWidth` | `minWidth` | ✅ 統一済み |
| 最大幅 | `maxWidth` | `maxWidth` | `maxWidth` | ✅ 統一済み |
| 最小高さ | `minHeight` | `minHeight` | `minHeight` | ✅ 統一済み |
| 最大高さ | `maxHeight` | `maxHeight` | `maxHeight` | ✅ 統一済み |
| ウェイト | `weight` | `weight` | `weight` | ✅ 統一済み |
| アスペクト幅 | `aspectWidth` | `aspectWidth` | ❌ 未実装 | |
| アスペクト高さ | `aspectHeight` | `aspectHeight` | ❌ 未実装 | |
| matchParent | `width: "matchParent"` | `width: "matchParent"` | `width: "matchParent"` | ✅ fillMaxWidth() |
| wrapContent | `width: "wrapContent"` | `width: "wrapContent"` | `width: "wrapContent"` | ✅ wrapContentWidth() |

### 5. 相対レイアウト系 (ConstraintLayout)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 下に配置 | `alignTopOfView` | `alignTopOfView` | `alignTopOfView` | ✅ 統一済み |
| 上に配置 | `alignBottomOfView` | `alignBottomOfView` | `alignBottomOfView` | ✅ 統一済み |
| 右に配置 | `alignLeftOfView` | `alignLeftOfView` | `alignLeftOfView` | ✅ 統一済み |
| 左に配置 | `alignRightOfView` | `alignRightOfView` | `alignRightOfView` | ✅ 統一済み |
| 上端揃え | `alignTopView` | `alignTopView` | `alignTopView` | ✅ 統一済み |
| 下端揃え | `alignBottomView` | `alignBottomView` | `alignBottomView` | ✅ 統一済み |
| 左端揃え | `alignLeftView` | `alignLeftView` | `alignLeftView` | ✅ 統一済み |
| 右端揃え | `alignRightView` | `alignRightView` | `alignRightView` | ✅ 統一済み |
| 垂直中央揃え | `alignCenterVerticalView` | `alignCenterVerticalView` | `alignCenterVerticalView` | ✅ 統一済み |
| 水平中央揃え | `alignCenterHorizontalView` | `alignCenterHorizontalView` | `alignCenterHorizontalView` | ✅ 統一済み |

### 6. テキスト系 (DynamicTextComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| テキスト | `text` | `text` | `text` | ✅ 統一済み |
| 行数制限 | `lines` | `lines` | `lines` | ✅ 統一済み (maxLines) |
| テキスト配置 | `textAlign` | `textAlign` | `textAlign` | ✅ 統一済み |
| フォント | `font` | `font` | `font` | ✅ 統一済み ("bold") |
| フォントウェイト | `fontWeight` | `fontWeight` | `fontWeight` | ✅ 統一済み |
| フォントサイズ | `fontSize` | `fontSize` | `fontSize` | ✅ 統一済み |
| フォント色 | `fontColor` | `fontColor` | `fontColor` | ✅ 統一済み |
| 下線 | `underline` | `underline` | `underline` | ✅ 統一済み |
| 取り消し線 | `strikethrough` | `strikethrough` | `strikethrough` | ✅ 統一済み |
| 改行モード | `lineBreakMode` | `lineBreakMode` | `lineBreakMode` | ✅ 統一済み (clip/tail/word) |
| リンク有効 | `linkable` | `linkable` | `linkable` | ✅ 統一済み |
| 内部余白 | `edgeInset` | `edgeInset` | `edgeInset` | ✅ 統一済み |
| 行高さ | `lineHeight` | `lineHeight` | `lineHeight` | ✅ 統一済み |
| 文字間隔 | `letterSpacing` | `letterSpacing` | `letterSpacing` | ✅ 統一済み |
| 部分属性 | `partialAttributes` | `partialAttributes` | `partialAttributes` | ✅ 統一済み |
| 水平中央 | `centerHorizontal` | `centerHorizontal` | `centerHorizontal` | ✅ 統一済み |

### 7. 画像系 (DynamicImageComponent.kt, DynamicNetworkImageComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 画像ソース | `src` | `src` | `src` | ✅ 統一済み |
| コンテンツモード | `contentMode` | `contentMode` | `contentMode` | ✅ 統一済み |
| デフォルト画像 | `defaultImage` | `placeholder` | `defaultImage` / `placeholder` | 両方サポート |
| エラー画像 | `errorImage` | `errorImage` | `errorImage` | ✅ 統一済み |
| コンテンツ説明 | `contentDescription` | `contentDescription` | `contentDescription` | ✅ 統一済み |
| サイズ | `size` | `size` | `size` | ✅ 統一済み (正方形) |
| ティント | `tint` | `tint` | `tint` | ✅ 統一済み |

**contentMode 値:**
- `aspectFill` → `ContentScale.Crop`
- `aspectFit` → `ContentScale.Fit`
- `center` → `ContentScale.None`
- `fill` → `ContentScale.FillBounds`
- `inside` → `ContentScale.Inside`

### 8. 入力フィールド系 (DynamicTextFieldComponent.kt, DynamicTextViewComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| ヒント | `hint` / `placeholder` | `hint` / `placeholder` | `hint` / `placeholder` | ✅ 両方サポート |
| ヒント色 | `hintColor` | `hintColor` | `hintColor` | ✅ 統一済み |
| ヒントフォントサイズ | `hintFontSize` | `hintFontSize` | `hintFontSize` | ✅ 統一済み |
| セキュア入力 | `secure` | `secure` | `secure` | ✅ 統一済み (パスワード) |
| キーボードタイプ | `keyboardType` | `keyboardType` | `keyboardType` | ✅ 統一済み |
| IMEアクション | `imeAction` | `imeAction` | `imeAction` | ✅ 統一済み |
| 最大行数 | `maxLines` | `maxLines` | `maxLines` | ✅ 統一済み |
| 無効状態 | `disabled` | `disabled` | `disabled` | ✅ 統一済み |
| アウトライン | `outlined` | `outlined` | `outlined` | ✅ 統一済み |
| ハイライト背景 | `highlightBackground` | `highlightBackground` | `highlightBackground` | ✅ 統一済み |

**keyboardType 値:**
- `text` → `KeyboardType.Text`
- `number` / `numeric` → `KeyboardType.Number`
- `phone` → `KeyboardType.Phone`
- `email` → `KeyboardType.Email`
- `password` → `KeyboardType.Password`
- `decimal` → `KeyboardType.Decimal`
- `uri` / `url` → `KeyboardType.Uri`

**imeAction 値:**
- `done` → `ImeAction.Done`
- `go` → `ImeAction.Go`
- `next` → `ImeAction.Next`
- `previous` → `ImeAction.Previous`
- `search` → `ImeAction.Search`
- `send` → `ImeAction.Send`

### 9. セレクトボックス/日付ピッカー系 (DynamicSelectBoxComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 選択タイプ | `selectItemType` | `selectItemType` | `selectItemType` | ✅ 統一済み ("Date") |
| 日付モード | `datePickerMode` | `datePickerMode` | `datePickerMode` | ✅ 統一済み |
| 日付スタイル | `datePickerStyle` | `datePickerStyle` | `datePickerStyle` | ✅ 統一済み |
| 日付フォーマット | `dateFormat` / `dateStringFormat` | `dateStringFormat` | `dateFormat` / `dateStringFormat` | ✅ 両方サポート |
| 最小日付 | `minimumDate` | `minimumDate` | `minimumDate` | ✅ 統一済み |
| 最大日付 | `maximumDate` | `maximumDate` | `maximumDate` | ✅ 統一済み |
| 分間隔 | `minuteInterval` | `minuteInterval` | `minuteInterval` | ✅ 統一済み |
| オプション | `items` / `options` | `items` / `options` | `items` / `options` | ✅ 両方サポート |
| 選択値 | `selectedItem` / `bind` | `selectedItem` / `bind` | `selectedItem` / `bind` | ✅ 両方サポート |
| キャンセルボタン背景 | `cancelButtonBackgroundColor` | ❌ 未実装 | `cancelButtonBackgroundColor` | |
| キャンセルボタンテキスト | `cancelButtonTextColor` | ❌ 未実装 | `cancelButtonTextColor` | |

### 10. セグメント系 (DynamicSegmentComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| セグメント | `segments` / `items` | `segments` / `items` | `segments` / `items` | ✅ 統一済み |
| 選択インデックス | `selectedIndex` | `selectedIndex` | `selectedIndex` | ✅ 統一済み |
| 選択イベント | `onSelect` | `onSelect` | `onSelect` | ✅ 統一済み |

### 11. スイッチ系 (DynamicSwitchComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| オン状態 | `isOn` | `isOn` | `isOn` | ✅ 統一済み |
| バインド | `bind` | `bind` | `bind` | ✅ 統一済み |
| 値変更イベント | `onValueChange` | `onValueChange` | `onValueChange` | ✅ 統一済み |
| 有効状態 | `enabled` | `enabled` | `enabled` | ✅ 統一済み |
| オン時トラック色 | `onTintColor` | `onTintColor` | `onTintColor` | ✅ 統一済み |
| サム色 | `thumbTintColor` | `thumbTintColor` | `thumbTintColor` | ✅ 統一済み |

### 12. チェック/ラジオ系 (DynamicCheckBoxComponent.kt, DynamicRadioComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| ラベル | `label` / `text` | `label` / `text` | `label` / `text` | ✅ 両方サポート |
| チェック状態 | `isChecked` | `isChecked` | `isChecked` | ✅ 統一済み |
| バインド | `bind` | `bind` | `bind` | ✅ 統一済み |
| グループ | `group` | `group` | `group` | ✅ 統一済み |
| チェック色 | `checkColor` | `checkColor` | `checkColor` | ✅ 統一済み |
| オプション | `options` | `options` | `options` | ✅ 統一済み (Radio) |

### 13. スライダー系 (DynamicSliderComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 値 | `value` / `bind` | `value` / `bind` | `value` / `bind` | ✅ 両方サポート |
| 最小値 | `min` / `minimumValue` | `min` | `min` / `minimumValue` | ✅ 統一済み |
| 最大値 | `max` / `maximumValue` | `max` | `max` / `maximumValue` | ✅ 統一済み |
| ステップ | `step` | `step` | `step` | ✅ 統一済み |
| 値変更イベント | `onValueChange` | `onValueChange` | `onValueChange` | ✅ 統一済み |

### 14. コレクション/テーブル系 (DynamicCollectionComponent.kt, DynamicTableComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| セクション | `sections` | `sections` | `sections` | ✅ 統一済み |
| セルクラス | `cellClasses` | `cellClasses` | `cellClasses` | ✅ 統一済み |
| ヘッダークラス | `headerClasses` | `headerClasses` | `headerClasses` | ✅ 統一済み |
| フッタークラス | `footerClasses` | `footerClasses` | `footerClasses` | ✅ 統一済み |
| アイテム | `items` | `items` | `items` | ✅ 統一済み |
| 列数 | `columns` | `columns` | `columns` | ✅ 統一済み |
| レイアウト | `layout` | `layout` | `layout` | ✅ 統一済み |
| コンテンツパディング | `contentPadding` | `contentPadding` | `contentPadding` | ✅ 統一済み |
| アイテム間隔 | `itemSpacing` / `spacing` | `itemSpacing` | `itemSpacing` / `spacing` | ✅ 両方サポート |
| セル高さ | `cellHeight` | `cellHeight` | `cellHeight` | ✅ 統一済み |
| セル幅 | `cellWidth` | `cellWidth` | `cellWidth` | ✅ 統一済み |

### 15. スクロール系 (DynamicScrollViewComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| スクロール有効 | `scrollEnabled` | `scrollEnabled` | `scrollEnabled` | ✅ 統一済み |
| 横スクロール | `horizontalScroll` | `horizontalScroll` | `horizontalScroll` | ✅ 統一済み |
| 方向 | `orientation` | `orientation` | `orientation` | ✅ 統一済み |

### 16. ビジュアル系共通

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 背景色 | `background` | `background` | `background` | ✅ 統一済み |
| 透明度 | `alpha` | `alpha` | `alpha` | ✅ 統一済み |
| 角丸 | `cornerRadius` | `cornerRadius` | `cornerRadius` | ✅ 統一済み |
| 枠線幅 | `borderWidth` | `borderWidth` | `borderWidth` | ✅ 統一済み |
| 枠線色 | `borderColor` | `borderColor` | `borderColor` | ✅ 統一済み |
| 影 | `shadow` | `shadow` | `shadow` | ✅ 統一済み (Boolean/Number) |
| 非表示 | `hidden` | `hidden` | `hidden` | ✅ 統一済み |
| 可視性 | `visibility` | `visibility` | `visibility` | ✅ 統一済み |

### 17. View/Container系 (DynamicContainerComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| 方向 | `orientation` | `orientation` | `orientation` | ✅ 統一済み |
| 子要素 | `child` / `children` | `child` / `children` | `child` / `children` | ✅ 両方サポート |
| 配置 | `gravity` | `gravity` | `gravity` | ✅ 統一済み |
| 間隔 | `spacing` | `spacing` | `spacing` | ✅ 統一済み |
| 配分 | `distribution` | `distribution` | `distribution` | ✅ 統一済み |
| 方向逆転 | `direction` | `direction` | `direction` | ✅ 統一済み (bottomToTop/rightToLeft) |

### 18. ボタン系 (DynamicButtonComponent.kt)

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| テキスト | `text` | `text` | `text` | ✅ 統一済み |
| 有効状態 | `enabled` / `disabled` | `enabled` / `disabled` | `enabled` / `disabled` | ✅ 両方サポート |
| 非同期 | `async` | `async` | `async` | ✅ 統一済み |
| ローディング | `isLoading` | `isLoading` | `isLoading` | ✅ 統一済み |
| ローディングテキスト | `loadingText` | `loadingText` | `loadingText` | ✅ 統一済み |
| フォントウェイト | `fontWeight` | `fontWeight` | `fontWeight` | ✅ 統一済み |

### 19. その他共通

| 機能 | Android XML | Compose Static | Compose Dynamic | 備考 |
|------|-------------|----------------|-----------------|------|
| ID | `id` | `id` | `id` | ✅ 統一済み |
| タイプ | `type` | `type` | `type` | ✅ 統一済み |
| インクルード | `include` | `include` | `include` | ✅ 統一済み |
| 変数 | `variables` | `variables` | `variables` | ✅ 統一済み |

---

## データバインディング

### バインディング構文

```json
{
  "text": "@{userName}",
  "text": "@{userName ?? デフォルト値}"
}
```

- `@{variable}` - 単純なバインディング
- `@{variable ?? default}` - デフォルト値付きバインディング

### 双方向バインディング

```json
{
  "type": "TextField",
  "text": "@{inputText}",
  "onTextChange": "handleTextChange"
}
```

```json
{
  "type": "Switch",
  "bind": "@{isEnabled}",
  "onValueChange": "handleValueChange"
}
```

---

## SwiftJsonUIとの互換性

KotlinJsonUIはSwiftJsonUIと同じJSON仕様に従います。以下の属性は両プラットフォームで共通です：

### 完全互換属性
- `type`, `id`, `child`/`children`
- `width`, `height`, `minWidth`, `maxWidth`, `minHeight`, `maxHeight`
- `padding`, `paddings`, `paddingTop/Bottom/Left/Right`
- `margins`, `topMargin/bottomMargin/leftMargin/rightMargin`
- `background`, `cornerRadius`, `borderWidth`, `borderColor`, `shadow`
- `alpha`, `hidden`, `visibility`
- `text`, `fontSize`, `fontColor`, `fontWeight`, `textAlign`
- `onclick`, `onTextChange`, `onValueChange`
- `src`, `contentMode`
- `hint`, `placeholder`, `secure`
- `items`, `sections`, `cellClasses`
- 相対レイアウト属性 (`alignTopOfView`, etc.)

### プラットフォーム固有属性
| 属性 | iOS (SwiftJsonUI) | Android (KotlinJsonUI) | 備考 |
|------|-------------------|------------------------|------|
| `returnKeyType` | submitLabel | imeAction | 機能は同等 |
| `keyboardType` | input | keyboardType | 機能は同等 |
| `startMargin`/`endMargin` | ❌ | ✅ | Android RTL対応 |
| `paddingStart`/`paddingEnd` | ❌ | ✅ | Android RTL対応 |

---

## 更新履歴

- 2024年9月 - 初版作成
  - SwiftJsonUI仕様に合わせて属性名を統一
  - ConstraintLayout相対レイアウト属性を追加
  - すべての主要コンポーネントを実装
