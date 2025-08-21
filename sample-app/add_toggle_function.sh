#!/bin/bash

# List of ViewModels that need toggleDynamicMode function
viewmodels=(
    "AlignmentComboTestViewModel"
    "AlignmentTestViewModel"
    "BindingTestViewModel"
    "ButtonEnabledTestViewModel"
    "ComponentsTestViewModel"
    "ConverterTestViewModel"
    "DatePickerTestViewModel"
    "DisabledTestViewModel"
    "FormTestViewModel"
    "IncludeTestViewModel"
    "KeyboardAvoidanceTestViewModel"
    "LineBreakTestViewModel"
    "MarginsTestViewModel"
    "RelativeTestViewModel"
    "SecureFieldTestViewModel"
    "TextStylingTestViewModel"
    "TextViewHintTestViewModel"
    "VisibilityTestViewModel"
    "WeightTestWithFixedViewModel"
    "WeightTestViewModel"
    "WidthTestViewModel"
)

for vm in "${viewmodels[@]}"; do
    file="/Users/like-a-rolling_stone/resource/KotlinJsonUI/sample-app/src/main/kotlin/com/example/kotlinjsonui/sample/viewmodels/${vm}.kt"
    
    if [ -f "$file" ]; then
        # Check if toggleDynamicMode already exists
        if ! grep -q "toggleDynamicMode" "$file"; then
            echo "Adding toggleDynamicMode to $vm"
            
            # Add the function after the data field declaration
            sed -i '' '/val data: StateFlow<.*Data> = _data.asStateFlow()/a\
\
    // Dynamic mode toggle\
    fun toggleDynamicMode() {\
        val currentStatus = _data.value.dynamicModeStatus\
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"\
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)\
    }
' "$file"
        else
            echo "$vm already has toggleDynamicMode"
        fi
    else
        echo "File not found: $file"
    fi
done

echo "Done!"