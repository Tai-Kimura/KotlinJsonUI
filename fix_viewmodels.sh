#!/bin/bash

# Fix all ViewModels to use proper updateData method

viewmodels_dir="/Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/kotlin/com/example/kotlinjsonui/sample/viewmodels"

for file in "$viewmodels_dir"/*.kt; do
    if grep -q "_data.value.update(updates)" "$file"; then
        # Extract the ViewModel class name and Data class name
        viewmodel_name=$(basename "$file" .kt)
        data_class_name="${viewmodel_name%ViewModel}Data"
        
        echo "Fixing $file"
        
        # Use sed to replace the update method
        sed -i '' "s/_data\.value\.update(updates)/val currentDataMap = _data.value.toMap(this).toMutableMap()\n        currentDataMap.putAll(updates)\n        _data.value = ${data_class_name}.fromMap(currentDataMap)/g" "$file"
        
        # Fix the indentation
        sed -i '' '/currentDataMap\.putAll/s/^/        /' "$file"
        sed -i '' "/_data\.value = ${data_class_name}\.fromMap/s/^/        /" "$file"
    fi
done

echo "ViewModels fixed!"