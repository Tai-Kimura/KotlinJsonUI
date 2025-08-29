# KotlinJsonUI TODO - Binding Issues

## ✅ 解決済み - XML生成時のスキップによる対応

以下のバインディングエラーはXML生成時にスキップすることで解決しました：

### 1. StatusColor Binding Error ✅
- **File**: custom_component_test.xml 
- **Error**: `Could not find accessor com.example.kotlinjsonui.sample.data.CustomComponentTestData.statusColor`
- **Issue**: statusColorはCompose UI Color型だが、android:tintはString型を期待
- **Resolution**: XML生成時にstatusColorを含むtint/colorバインディングをスキップ

### 2. Visibility Binding ✅
- **File**: visibility_test.xml
- **Error**: `Cannot find a setter for android:visibility that accepts parameter type 'java.lang.String'`
- **Resolution**: XML生成時にvisibilityバインディングをスキップ（BindingAdapterは既に実装済み）

### 3. Progress Binding with Double Type ✅
- **Files**: components_test.xml, binding_test.xml
- **Error**: `Cannot find a setter for android:progress that accepts parameter type 'double'`
- **Components**: SeekBar (Slider)
- **Resolution**: XML生成時にSliderのvalueバインディング（progressにマップ）をスキップ

### 4. RecyclerView Items Binding ✅
- **Files**: converter_test.xml, collection_test.xml
- **Error**: `Cannot find a setter for app:items that accepts parameter type 'CollectionDataSource'`
- **Resolution**: XML生成時にRecyclerView/Collectionのitemsバインディングをスキップ

## 実装が必要なバインディングアダプター

以下のバインディングアダプターをlibrary/src/main/kotlin/com/kotlinjsonui/binding/に実装する必要があります：

### BindingAdapters.kt に追加が必要なアダプター

```kotlin
// 1. Visibility binding for custom views
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visibility: String?) {
    view.visibility = when (visibility) {
        "visible" -> View.VISIBLE
        "invisible" -> View.INVISIBLE
        "gone" -> View.GONE
        else -> View.VISIBLE
    }
}

// 2. Progress binding with type conversion
@BindingAdapter("android:progress")
fun setProgress(view: ProgressBar, progress: Double?) {
    view.progress = progress?.toInt() ?: 0
}

@BindingAdapter("android:progress")
fun setProgress(view: SeekBar, progress: Double?) {
    view.progress = progress?.toInt() ?: 0
}

// 3. RecyclerView items binding
@BindingAdapter("app:items")
fun setRecyclerViewItems(recyclerView: RecyclerView, items: List<Any>?) {
    // Adapter implementation needed
}

// 4. Tint color binding
@BindingAdapter("android:tint")
fun setImageTint(imageView: ImageView, color: String?) {
    color?.let {
        try {
            imageView.setColorFilter(Color.parseColor(it))
        } catch (e: Exception) {
            // Handle color parsing error
        }
    }
}
```

## XML生成時の対応

kjui_tools/lib/xml/helpers/attribute_mapper.rb の should_skip_binding? メソッドで以下のバインディングをスキップしています：

1. ✅ Compose UI Color型のtint/colorバインディング（statusColor）
2. ✅ String型のvisibilityバインディング
3. ✅ double型のprogressバインディング（Sliderのvalue）
4. ✅ RecyclerView/Collectionのitemsバインディング

これらのバインディングは、XMLには出力されず、将来的な実装のためにログに記録されます（/tmp/skipped_bindings.json）。

## 今後の作業

1. [ ] 将来的にこれらのバインディングを動作させる場合は、適切なバインディングアダプターを実装
2. [ ] Compose UIモードではこれらのバインディングは正常に動作
3. [ ] XMLモードでの制限事項としてドキュメント化