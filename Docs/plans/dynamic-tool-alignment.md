# Dynamic モード ⇄ kjui_tools 整合計画

## 目的

kjui_tools（`/Users/like-a-rolling_stone/resource/jsonui-cli/kjui_tools/`）が出力・仕様として宣言する属性と、KotlinJsonUI ライブラリの Dynamic ランタイム（`library-dynamic/src/main/kotlin/com/kotlinjsonui/dynamic/`）が実際に解釈する属性のズレを洗い出し、**tool を正**として Dynamic 側で埋める。

> 既存の `docs/dynamic-v2-rewrite-plan.md` は helper / converter 書き直しの土台。本書はその上に乗る**機能整合の差分計画**。

## 照合対象

| 側 | パス |
|---|---|
| tool（正） | `jsonui-cli/shared/core/attribute_definitions.json` |
|  | `jsonui-cli/kjui_tools/lib/compose/converters/*.rb` |
|  | `jsonui-cli/kjui_tools/lib/compose/helpers/modifier_builder.rb` |
| lib | `KotlinJsonUI/library-dynamic/src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt` |
|  | `.../dynamic/components/Dynamic*Component.kt` |
|  | `.../dynamic/helpers/ModifierBuilder.kt` / `ColorParser.kt` |

## 方針

1. tool 出力 JSON を Dynamic ランタイムがそのまま描画できる状態にする。
2. Compose で原理的に不可能な属性（`scrollMode: "window"`, sticky header など）は **ランタイムで読み捨て＋ログ**。将来 tool 側に `platform` フラグで除外させる。
3. 名前ゆれ（`gravity` / `alignment` / 個別 `align*` フラグ）は **tool 出力が最優先で解釈される**よう統合。
4. v2.3.0 の `themedColorParser` / v2.2.7 の `lazy:false` 整合は維持。

---

## Phase 0: 共通ヘルパー整合（v2 rewrite plan と連動）

### 0-1. gravity 対応（🔴最優先）

tool 側 `container_component.rb` / `modifier_builder.rb` は `gravity`（string または配列, enum: `top|bottom|centerVertical|left|right|centerHorizontal|center`）を出す。
lib の `ModifierBuilder.kt` は **`alignTop` / `alignBottom` / `centerVertical` / `centerHorizontal` / `centerInParent` の個別ブール**しか読まない。

| 対応 | 内容 |
|---|---|
| `ModifierBuilder.parseGravity(value: Any?) -> Set<GravityFlag>` を新設 | `String`（`"top|left"` の pipe 区切り含む）と `List<String>` を両方受ける |
| 既存の `getChildAlignment()` を `gravity` 優先、フォールバックで個別フラグ | tool が `gravity` を emit したら必ず勝つ |
| `DynamicContainerComponent` 系（VStack/HStack/ZStack/ConstraintLayout） | `gravity` を親の `Alignment.Horizontal/Vertical` と子の `align` の両方に反映 |
| 回帰テスト | `{"gravity": "centerHorizontal|top"}` が Column 内で中央寄せ＋Top 配置になる |

### 0-2. alignment 文字列属性

tool は `alignment` 文字列（`"center"`, `"topLeading"` 等）も出す。lib は未読み。

| 対応 | 内容 |
|---|---|
| `ModifierBuilder.parseAlignment(value: String?)` 追加 | SwiftUI スタイル alignment 文字列 → Compose `Alignment` マップ |

### 0-3. テーマ対応カラー（v2.3.0 で整合済み、回帰保護）

`ColorParser.Configuration.themedColorParser(currentThemeMode, key)` は整合済み。tool の `colors.json`（modes/fallback_mode/systemModeMapping）をランタイムが正しく読めるか回帰テストを追加。

---

## Phase 1: コンポーネント別ギャップ

> 🔴 = tool が出すのに lib が解釈しない／🟡 = tool 未定義だが lib 実装あり／🟢 = 名前ゆれ／⚪ = Compose 不可・読み捨て

### 1-1. Label / Text（`DynamicTextComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `edgeInset` | ⚪ | `padding*` に寄せて読み捨て（警告ログ） |
| `hintAttributes`（object） | 🟢 | tool は個別 `hintFont/hintFontSize/hintColor` にも分解して出す。ネスト object も受けるように `hintAttributes.fontColor` 等を展開 |
| `textShadow` | 🟡 | Text 専用シャドウ。現状 ModifierBuilder の component-shadow を流用。Text には `style.shadow` で適用する分岐を追加 |
| `selected`（UIKit限定） | ⚪ | `platform: swift/uikit` で tool 側がそもそも出さない。ノーオペ |

### 1-2. Button（`DynamicButtonComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `image`（アイコンボタン） | 🔴 | Button の leading icon として `Icon(...)` を挿入。`imagePosition` / `iconPosition`（`leading`/`trailing`/`top`/`bottom`）対応 |
| `highlightBackground` | 🔴 | `InteractionSource` + `pressed` state で背景を差し替える Modifier を追加 |
| `textAlign` | 🟡 | Button 内 Text の `textAlign` を通す（現状は center 固定） |
| `hilightColor`（typo） | ⚪ | `highlightColor` と同じキーとして読む互換エイリアス追加（tool も両方出す） |

### 1-3. TextField（`DynamicTextFieldComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `caretAttributes` | 🟡 | `caretAttributes.fontColor` を `TextFieldColors.cursorColor` にマップ |
| `autocorrectionType` | 🔴 | `KeyboardOptions.autoCorrect` に反映（現状 `autocapitalizationType` のみ対応） |
| `keyboardAppearance` | ⚪ | Compose は system 追従。読み捨て＋ログ |
| `hasContainer`（UIKit限定） | ⚪ | ノーオペ |
| `accessory*` / `doneText` | ⚪ | iOS InputAccessoryView 相当なし。ノーオペ＋ログ |

### 1-4. Image（`DynamicImageComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `highlightSrc` | 🔴 | `InteractionSource` の pressed 時に `src` を差し替える |
| `renderingMode`（`original`/`template`） | 🔴 | `template` なら `ColorFilter.tint(tintColor)` を強制。`original` は tint 無視 |
| `loadingImage` / `errorImage` | 🟡 | NetworkImage では既に対応。静的 Image 側でも placeholder サポートを追加（Coil AsyncImage へ移行する場合のみ） |

### 1-5. NetworkImage（`DynamicNetworkImageComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `headers` | 🔴 | Coil `ImageRequest.Builder.setHeader(...)` にマップ |
| URL バリデーション | 🟡 | `runCatching { Uri.parse(url) }` で不正時に `errorImage` にフォールバック |

### 1-6. Toggle / Switch（`DynamicToggleComponent.kt` / `DynamicSwitchComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `labelAttributes` | 🔴 | Switch の隣に `Text` を配置。`labelAttributes.{text, fontSize, fontColor, fontWeight}` を反映。`labelPosition: leading|trailing` も受ける |
| `thumbTintColor` / `trackTintColor` | 🟢 | `SwitchColors` にマップ済み。回帰テスト |

### 1-7. Checkbox / Radio（`DynamicCheckboxComponent.kt` / `DynamicRadioComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `icon` / `selectedIcon` | 🔴 | デフォルトのチェックマークを差し替え可能に。`Icons.Default.*` 名または resource key |
| `iconSize` / `iconColor` | 🔴 | 上記アイコンのサイズ・色 |
| `group`（Radio） | 🟢 | 既存互換 |

### 1-8. Slider（`DynamicSliderComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `step` | 🔴 | `Slider(steps = ((max-min)/step).toInt() - 1, ...)` で離散化 |
| `minimumValue` / `maximumValue` | 🟢 | `minimum` / `maximum` とともに受ける（既存 rawData 経路を整理） |
| `progressTintColor` / `trackTintColor` | 🟢 | `SliderColors.activeTrackColor` / `inactiveTrackColor` に分離 |

### 1-9. Segment / SelectBox

| 属性 | 状態 | 対応 |
|---|---|---|
| Segment: `style` | ⚪ | Material の SegmentedButton は style 自由度が低い。読み捨て＋ログ |
| SelectBox: `includePromptWhenDataBinding` | 🟢 | prompt を items の先頭に足す経路を整理 |
| SelectBox: `caretAttributes` / `labelAttributes` / `dividerAttributes` | 🟡 | 現状 partial 対応。Text/Divider の属性を丸ごと反映するよう拡張 |

### 1-10. Collection（`DynamicCollectionComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `lazy: false` | ✓ | v2.2.7 で整合済み。回帰テスト |
| `layout: flow` / `LeftAligned` / `leftAligned` | 🔴 | `FlowRow`（`androidx.compose.foundation.layout.FlowRow`、Material3 1.4+）でラップ。不可なら明示的に警告＋vertical へフォールバック |
| `showsHorizontalScrollIndicator` / `showsVerticalScrollIndicator` | ⚪ | Compose は自動。読み捨て |
| sticky headers | ⚪ | `LazyColumn.stickyHeader` は標準でサポート可能。`section.sticky: true` を受けて `stickyHeader {}` に振り分ける（実装可能） |
| `insets` 文字列（pipe 区切り） | 🔴 | `"10|5|10|5"` の 4 成分を `PaddingValues` にパース |
| `cellIdProperty` + `autoChangeTrackingId` | ✓ | 整合済み |
| `onItemAppear` / `onPageChanged` | 🟢 | 既存 `onItemAppear` は整合。`onPageChanged` を paging モード時のみ発火するフックを追加 |

### 1-11. ScrollView（`DynamicScrollViewComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `scrollMode`（`inner`/`window`） | ⚪ | Web 専用。Compose では常に `inner`。`window` 指定時は警告ログ＋ignore |
| `keyboardAvoidance` | ✓ | `imePadding` で対応済み。回帰テスト |

### 1-12. Container（VStack / HStack / ZStack / ConstraintLayout）

| 属性 | 状態 | 対応 |
|---|---|---|
| `gravity` | 🔴 | **Phase 0-1 で解消** |
| `distribution` | 🔴 | `fillEqually` → 子に `weight(1f)`、`equalSpacing` → `Arrangement.SpaceBetween`、`equalCentering` → `SpaceEvenly`、`fill` → デフォルト。`Row/Column` の `horizontalArrangement/verticalArrangement` に注入 |
| `alignment` 文字列 | 🔴 | **Phase 0-2 で解消** |
| `direction`（`topToBottom`/`leftToRight`） | ⚪ | Compose の LTR/RTL は `LayoutDirection` で決まる。明示指定は `CompositionLocalProvider(LocalLayoutDirection provides ...)` で対応可能だが当面は読み捨て |
| `alignTopOfView` / `alignBottomOfView` 他（非ConstraintLayout） | 🟡 | 現状は ConstraintLayout 内のみ動作。外側は警告 |

### 1-13. TabView（`DynamicTabViewComponent.kt`）

| 属性 | 状態 | 対応 |
|---|---|---|
| `tabBarBackground` | 🟢 | `TabRow` の `containerColor` に反映 |
| `onPageChanged` | 🟢 | `onValueChange` のエイリアス |

### 1-14. GradientView / Blur / CircleView

| 属性 | 状態 | 対応 |
|---|---|---|
| `gradient` object | 🔴 | `{ colors, locations, startPoint, endPoint }` を Brush.linearGradient に完全マップ |
| `gradientDirection` | 🔴 | `topToBottom` 等の文字列 → `startPoint/endPoint` 生成 |
| Blur: `color` | 🔴 | `"light"|"dark"|"prominent"` → `Modifier.blur(...).background(Color(...))` |

---

## Phase 2: クロスカッティング属性

| 属性 | 状態 | 対応 |
|---|---|---|
| `onLongPress` | ✓ | `Modifier.combinedClickable(onLongClick = ...)` で実装済み。回帰テスト |
| `onAppear` / `onDisappear` | ✓ | `LaunchedEffect` / `DisposableEffect` で実装済み |
| `onClick` の binding 解決 | ✓ | event handler map 経由で解決済み |
| `binding_direction: two-way` メタ | 🟡 | attribute_definitions.json メタは lib 側でまだ利用していない。当面は実装ごとに個別対応（TextField.text / Slider.value / Toggle.value 等） |
| `responsive` | ✓ | `ResponsiveResolver` で実装済み |

---

## Phase 3: `attribute_definitions.json` 逆輸入（tool 側別計画）

lib にあって tool になく、ランタイムで rawData から拾っている属性の tool 側追加は、**jsonui-cli 側の別計画**に切り出した:

→ `/Users/like-a-rolling_stone/resource/jsonui-cli/docs/plans/attribute-definitions-backfill.md`

本計画 Phase 1 / Phase 2 の 🟡 マーク属性が backfill 対象。逆輸入計画の Phase 2〜3 が完了したら、本計画で rawData 経由で対応している属性を canonical 名で受け直す。

---

## 実装順序

1. **Phase 0-1 gravity** — 影響範囲が最も広く、ほぼ全 Container に波及
2. **Phase 0-2 alignment 文字列**
3. **Phase 1-12 distribution**（Container 回りをまとめて片付け）
4. **Phase 1-10 Collection**（flow / insets 文字列 / sticky / onPageChanged）
5. **Phase 1-6, 1-2, 1-3**（Toggle label / Button image / TextField caret, autocorrect）
6. **Phase 1-14 Gradient / Blur**
7. **Phase 1 残り**
8. **Phase 2 メタ整合**

## 受け入れ基準

- [ ] tool 生成 Layout JSON を `library-dynamic` で描画した結果が、`library`（静的生成 Compose）で描画した結果と視覚的に一致（snapshot）
- [ ] `dynamic-v2-rewrite-plan.md` の Phase 1 完了後、本計画の各チェックボックスに対する単体テストが通る
- [ ] `scrollMode:window` / `edgeInset` / `keyboardAppearance` 等の Compose 不可属性は `Log.w(TAG, ...)` で 1 回だけ警告
- [ ] `gravity` を使った既存 JSON が個別フラグに書き換えずに動く（回帰）
- [ ] v2.3.0 のテーマカラー動作が light/dark 切替で即時再コンポーズ
