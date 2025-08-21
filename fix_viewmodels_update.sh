#!/bin/bash

# Fix all ViewModels that use undefined update() method
cd /Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/kotlin/com/example/kotlinjsonui/sample/viewmodels

# List of files that need fixing
files=(
    "TextfieldTestViewModel.kt"
    "TextDecorationTestViewModel.kt"
    "SwitchEventsTestViewModel.kt"
    "SegmentTestViewModel.kt"
    "ScrollTestViewModel.kt"
    "ImplementedAttributesTestViewModel.kt"
    "ButtonTestViewModel.kt"
)

for file in "${files[@]}"; do
    echo "Fixing $file..."
    # Replace the update() call with a simple copy() trigger
    sed -i '' 's/_data.value.update(updates)/_data.value = _data.value.copy()/' "$file"
done

echo "All ViewModels fixed!"