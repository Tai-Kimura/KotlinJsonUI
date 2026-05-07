# KotlinJsonUI Dynamic Mode v2 書き直し計画

## 方針

- **基本機構はそのまま維持**: DynamicView, DataBindingContext, DynamicLayoutLoader, IncludeExpander, DynamicStyleLoader, FocusManager, HotLoader, ResourceCache, SafeAreaConfig
- **ヘルパーを全て書き直し**: ModifierBuilder, ColorParser を kjui_tools の modifier_builder.rb / resource_resolver.rb を正として1から再実装
- **全コンバーターを1から書き直し**: kjui_tools の各 component.rb を正とし、Dynamic版（ランタイムJSON→Compose）として再実装

## 維持するファイル

### 変更不要（検証済み）

| ファイル | 検証結果 |
|---------|---------|
| DataBindingContext.kt | OK: `@{prop}`, `@{prop ?? default}`, ネストパス、配列アクセス全対応 |
| DynamicLayoutLoader.kt | OK: キャッシュ・サブディレクトリ検索・include展開連携 |
| DynamicStyleLoader.kt | OK: スタイル継承・マージ・HotLoader連携 |
| IncludeExpander.kt | OK: IDプレフィックスcamelCase化、バインディング変換 |
| ResourceCache.kt | OK: 文字列・色リソース解決 |
| FocusManager.kt | OK: SharedFlowベースのフォーカスチェーン |
| SafeAreaConfig.kt | OK: CompositionLocal連携 |
| HotLoader.kt | OK: WebSocketベースのホットリロード |

### 要修正（検証で問題発見）

| ファイル | 問題 | 修正内容 |
|---------|------|---------|
| DynamicView.kt | weight + visibility gone ガードが未実装。visibility_helper.rb では gone 時に composition 自体をスキップする if ブロックを生成するが、DynamicView.kt の VisibilityWrapper 呼び出しにはこのガードがない | コンテナ系コンバーター内で子要素の weight + visibility を検出し、gone 時に composition をスキップする処理を追加 |
| VisibilityWrapper.kt | RowScope/ColumnScope オーバーロードがない。現在 Box でラップしているため、weight modifier を渡しても Row/Column の weight 配分に参加しない | RowScope/ColumnScope 用オーバーロードを追加するか、コンバーター側で weight を外部管理する方式に変更 |

## Phase 1: ヘルパー書き直し

### 1-1. ModifierBuilder.kt（書き直し）

kjui_tools の `modifier_builder.rb` を正として1から書き直す。

**参照元**: `jsonui-cli/kjui_tools/lib/compose/helpers/modifier_builder.rb`

**Modifier適用順序**（modifier_builder.rb の build 系メソッド呼び出し順に準拠）:

```
testTag → margins → weight → size → alpha → shadow → background(clip+border+bg) → clickable → padding → alignment
```

**メソッド一覧**（modifier_builder.rb の全メソッドに対応）:

| メソッド | 参照元メソッド | 処理内容 |
|---------|--------------|---------|
| applyTestTag | build_test_tag | id → `.testTag("id")` + `.semantics { testTagsAsResourceId = true }` |
| applyMargins | build_margins | margins配列, topMargin/bottomMargin/leftMargin/rightMargin/startMargin/endMargin。**バインディング対応**: `@{prop}` → data mapから値取得 |
| applyWeight | build_weight | weight > 0 かつ parentType が Row/Column の場合のみ `.weight(Nf)` |
| applySize | build_size | frame object, width/height (matchParent/wrapContent/数値/負数→fillMax), minWidth/maxWidth/minHeight/maxHeight, aspectWidth/aspectHeight→aspectRatio |
| applyAlpha | build_alpha / build_visibility | alpha/opacity。**バインディング対応**: `@{prop}` → `.alpha(data.prop.toFloat())` |
| applyShadow | build_shadow | shadow (String→簡易 / Hash→radius+shape) |
| applyBackground | build_background | background色 + cornerRadius→clip(RoundedCornerShape) + border(solid/dashed/dotted) |
| applyClickable | build_clickable | onClick/onclick → `.clickable { handler }` イベントハンドラ呼び出し |
| applyPadding | build_padding | padding/paddings (配列/単値), paddingTop/paddingBottom/paddingLeft/paddingRight/paddingVertical/paddingHorizontal |
| applyAlignment | build_alignment | parent_type (Row/Column/Box) による分岐。Row→Top/Bottom/CenterVertically, Column→Start/End/CenterHorizontally, Box→全組み合わせ+BiasAlignment |
| applyRelativePositioning | build_relative_positioning | ConstraintLayout用: alignTopOfView/alignBottomOfView/alignLeftOfView/alignRightOfView + margin対応 |
| buildModifier | (組み合わせ) | 上記を統一順序で適用する統合メソッド。引数: json, data, parentType, context |
| ApplyLifecycleEffects | build_lifecycle_effects | onAppear→LaunchedEffect, onDisappear→DisposableEffect |
| getChildAlignment | build_alignment (Box用) | Alignment / BiasAlignment を返す。centerInParent, alignTop+alignLeft 等の組み合わせ |

**イベントハンドラ解決**（modifier_builder.rb のヘルパーメソッドに対応）:

| メソッド | 処理内容 |
|---------|---------|
| isBinding | `@{...}` 形式かチェック |
| extractBindingProperty | `@{propertyName}` → `propertyName` を抽出 |
| resolveEventHandler | onClick → data mapから `Function` を取得して invoke。view_id と value_expr のオプション引数対応 |

### 1-2. ColorParser.kt（書き直し）

kjui_tools の `resource_resolver.rb` の色関連処理を正として書き直す。

**参照元**: `jsonui-cli/kjui_tools/lib/compose/helpers/resource_resolver.rb` の `process_color`

| メソッド | 処理内容 |
|---------|---------|
| parseColor | JSON key → Color。解決順: binding(`@{prop}` → data mapからColor/String取得) → colorResource(名前→R.color.xxx) → hex(#FFFFFF/FFFFFF) |
| parseColorWithBinding | binding付き色値: `@{prop}` → data map の Color オブジェクト or String→parseColor |
| parseColorString | 文字列→Color。hex / リソース名 / null |
| init | Context 保持、Configuration.colorParser 連携 |

### 1-3. ResourceResolver.kt（新規作成）

kjui_tools の `resource_resolver.rb` に相当するランタイム版を新規作成。

**参照元**: `jsonui-cli/kjui_tools/lib/compose/helpers/resource_resolver.rb`

| メソッド | 参照元 | 処理内容 |
|---------|--------|---------|
| resolveText | process_text | 解決順: binding(`@{prop}` → data mapから取得) → stringResource(キー名→R.string.xxx) → リテラル文字列 |
| resolveColor | process_color | ColorParser に委譲 |
| resolveDrawable | process_drawable | 解決順: リソース名→`R.drawable.xxx` → 動的lookup(`context.resources.getIdentifier()`) |
| processDataBinding | (既存関数の統合) | `@{key}` → data map 置換。`@{key ?? default}` のデフォルト値対応 |

### 1-4. DashedBorderModifier.kt（維持）

既存のまま維持。border(solid/dashed/dotted) の描画ユーティリティとして ModifierBuilder.applyBackground から利用。

## Phase 2: コンバーター書き直し（全27種）

全コンバーターを kjui_tools の対応する component.rb を正として書き直す。

### コンバーター共通パターン

```kotlin
class DynamicXxxComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // 1. ライフサイクルイベント
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // 2. 属性パース（バインディング解決含む）
            val text = ResourceResolver.resolveText(json, "text", data, context)
            val color = ResourceResolver.resolveColor(json, "fontColor", data, context)

            // 3. Modifier構築（統一順序）
            val modifier = ModifierBuilder.buildModifier(json, data, parentType)

            // 4. Compose コンポーネント描画
            Text(text = text, modifier = modifier, color = color)
        }
    }
}
```

### 2-1. テキスト系

| コンバーター | 参照元 (kjui_tools) | 主要属性 |
|------------|---------------------|---------|
| DynamicTextComponent | text_component.rb | text, fontSize, fontColor, font(weight), underline, strikethrough, lineHeightMultiple, lineSpacing, textAlign, lines, autoShrink, partialAttributes, linkable |
| DynamicTextViewComponent | textview_component.rb | text(binding), hint/placeholder, hintColor, hintFontSize, hintLineHeightMultiple, secure |

### 2-2. 入力系

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicTextFieldComponent | textfield_component.rb | text(binding), hint/placeholder, secure, onTextChange, onFocus, onBlur, fieldId, nextFocusId, returnKeyType, contentType, autocapitalization, autocorrection, contentPadding, borderColor, borderStyle, cornerRadius |
| DynamicSwitchComponent | switch_component.rb | isOn/value/checked/bind, onValueChange, onTintColor, thumbTintColor, labelAttributes |
| DynamicCheckBoxComponent | checkbox_component.rb | isOn/checked/bind, label/text, icon/selectedIcon (IconToggleButton), checkColor, uncheckedColor, onValueChange |
| DynamicSliderComponent | slider_component.rb | value/bind, min/max, step, onValueChange, thumbTintColor, minimumTrackTintColor, maximumTrackTintColor |
| DynamicRadioComponent | radio_component.rb | isOn/checked/bind, onValueChange |
| DynamicSelectBoxComponent | selectbox_component.rb | ドロップダウン選択 |
| DynamicSegmentComponent | segment_component.rb | セグメント選択 |
| DynamicToggleComponent | toggle_component.rb | Switch のエイリアス |

### 2-3. ボタン系

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicButtonComponent | button_component.rb | text, onClick/onclick, background, fontColor, disabledBackground, disabledFontColor, hilightColor, cornerRadius, borderColor, borderWidth, contentPadding, enabled |

### 2-4. 画像系

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicImageComponent | image_component.rb | srcName/src, defaultImage, contentMode(aspectFill/aspectFit/fill/center), contentDescription |
| DynamicNetworkImageComponent | networkimage_component.rb | source/url/src(binding), hint/placeholder(drawable), contentMode, errorImage |
| DynamicCircleImageComponent | circleimage_component.rb | 円形クリッピング |

### 2-5. コンテナ系

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicContainerComponent | container_component.rb | orientation(vertical/horizontal/none)→Column/Row/Box, gravity, spacing, distribution, direction(reverse) |
| DynamicScrollViewComponent | scrollview_component.rb | horizontalScroll, orientation, keyboardAvoidance→imePadding |
| DynamicHStackComponent | container (Row固定) | gravity, spacing, distribution |
| DynamicVStackComponent | container (Column固定) | gravity, spacing, distribution |
| DynamicZStackComponent | container (Box固定) | contentAlignment |
| DynamicConstraintLayoutComponent | constraintlayout_component.rb | 相対配置(alignTopOfView等) |
| DynamicSafeAreaViewComponent | - | セーフエリア対応コンテナ |

### 2-6. リスト/コレクション系

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicCollectionComponent | collection_component.rb | LazyColumn/LazyRow/LazyGrid |
| DynamicTableComponent | table_component.rb | テーブルレイアウト |

### 2-7. その他

| コンバーター | 参照元 | 主要属性 |
|------------|--------|---------|
| DynamicTabViewComponent | tabview_component.rb | tabs, selectedIndex, icons, tabBarBackground, showLabels |
| DynamicWebViewComponent | webview_component.rb | URL/HTMLロード |
| DynamicWebComponent | web_component.rb | WebView snippet |
| DynamicProgressComponent | progress_component.rb | value, style(linear/circular), colors |
| DynamicIndicatorComponent | indicator_component.rb | スピナー |
| DynamicGradientViewComponent | gradientview_component.rb | グラデーション |
| DynamicBlurViewComponent | blurview_component.rb | ブラーエフェクト |
| DynamicCircleViewComponent | - | 円形ビュー |
| DynamicTriangleComponent | - | 三角形ビュー |
| DynamicIconLabelComponent | - | アイコン+ラベル |

### 削除予定

| ファイル | 理由 |
|---------|------|
| DynamicIncludeComponent.kt | include展開はIncludeExpanderで処理済み。DynamicViewで直接処理 |

## Phase 3: DynamicView.kt 更新

- VisibilityWrapper のweight対応（gone時にcomposition自体をスキップ）
- 新規コンポーネントタイプの追加（必要に応じて）

## 実装順序

```
Phase 1: ヘルパー更新
  1-1. ModifierBuilder.kt（全コンバーターの土台）
  1-2. ColorParser.kt
  1-3. ResourceResolver.kt（新規）
  ↓
Phase 2: コンバーター書き直し（優先度順）
  2-5. コンテナ系（Container, HStack, VStack, ZStack, ScrollView）← 他の全てを内包
  2-1. テキスト系（Text, TextView）← 最頻出
  2-3. ボタン系（Button）
  2-2. 入力系（TextField, Switch, CheckBox, Slider, Radio, SelectBox, Segment）
  2-4. 画像系（Image, NetworkImage, CircleImage）
  2-7. その他（TabView, WebView, Progress, Indicator, Gradient, Blur等）
  2-6. リスト系（Collection, Table）← 最も複雑
  ↓
Phase 3: DynamicView.kt 更新
  VisibilityWrapper weight対応
  型マッピング確認
```

## 注意事項

- **Modifier順序の統一**: testTag → margins → weight → size → alpha → shadow → background(clip+border+bg) → clickable → padding → alignment
- **バインディング構文**: `@{property}` → data map から取得。イベントは `data[name] as? Function` でinvoke
- **VisibilityWrapper + weight**: gone時は composition 自体をスキップして weight space を解放する
- **リソース解決**: 文字列はstringResource相当、色はcolorResource相当をランタイムで解決
- **kjui_tools を常に正とする**: コード生成で出力されるCompose構造をそのままランタイムで再現する

## コンバーター実装時の検証ルール

各コンバーター実装時に、そのコンバーターが使う全バインディングパターンを DataBindingContext / ResourceResolver / ColorParser で正しく処理できるか都度検証する。

### 検証対象

| カテゴリ | 検証内容 | 検証方法 |
|---------|---------|---------|
| テキストバインディング | `@{prop}` → 文字列取得、`@{prop ?? "default"}` → デフォルト値 | ResourceResolver.resolveText が DataBindingContext.evaluateExpression を通して正しく解決するか |
| 色バインディング | `@{colorProp}` → Color オブジェクト or hex文字列 → Color | ColorParser.parseColorWithBinding が data map の Color / String 両方を処理できるか |
| 数値バインディング | `@{width}`, `@{margin}` → Int/Float → .dp 変換 | ModifierBuilder の margin/size 系で DataBindingContext.evaluateExpression の戻り値(Number)を正しくキャストできるか |
| Boolean バインディング | `@{isEnabled}`, `@{hidden}` → Boolean | data map から Boolean を取得し、Compose の enabled/hidden に正しく渡せるか |
| イベントハンドラ | `@{onClick}`, `@{onValueChange}` → Function invoke | data map から `() -> Unit` / `(String) -> Unit` / `(String, Boolean) -> Unit` を取得し、正しい引数で invoke できるか |
| 二方向バインディング | TextField text, Switch isOn, Slider value | data map への書き戻し（`DataBindingContext.updateValue`）が正しく動作するか |
| visibility バインディング | `@{visibility}` → "visible"/"gone"/"invisible" | processDataBinding で文字列解決後、VisibilityWrapper に正しく渡されるか。weight + gone の場合の composition スキップも確認 |
| リソース解決 | hint, text → stringResource 相当 | ResourceCache.resolveString が文字列キーから正しくローカライズ値を返すか |

### 検証タイミング

各コンバーター実装完了時に、そのコンバーターが使うバインディングパターンを上表と照合し、未対応パターンが見つかった場合は：
1. DataBindingContext / ResourceResolver / ColorParser 側を先に修正
2. その後コンバーター実装を継続
