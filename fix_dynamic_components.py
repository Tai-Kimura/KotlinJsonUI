#!/usr/bin/env python3
"""
Fix all Dynamic components to use ModifierBuilder instead of directly using asFloat for width/height
"""

import os
import re

# Components that need to be updated (based on grep results)
components_to_update = [
    "DynamicButtonComponent.kt",
    "DynamicTextComponent.kt", 
    "DynamicContainerComponent.kt",
    "DynamicImageComponent.kt",
    "DynamicTextFieldComponent.kt",
    "DynamicRadioComponent.kt",
    "DynamicCheckBoxComponent.kt",
    "DynamicSwitchComponent.kt"
]

base_path = "/Users/like-a-rolling_stone/resource/KotlinJsonUI/library/src/main/kotlin/com/kotlinjsonui/dynamic/components"

for component_file in components_to_update:
    file_path = os.path.join(base_path, component_file)
    
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        continue
        
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Check if already uses ModifierBuilder
    if "import com.kotlinjsonui.dynamic.helpers.ModifierBuilder" in content:
        print(f"Skipping {component_file} - already uses ModifierBuilder")
        continue
    
    # Add import if not present
    if "import com.kotlinjsonui.dynamic.helpers.ModifierBuilder" not in content:
        # Add import after other dynamic imports
        import_pattern = r'(import com\.kotlinjsonui\.dynamic\..*\n)'
        last_dynamic_import = list(re.finditer(import_pattern, content))
        if last_dynamic_import:
            insert_pos = last_dynamic_import[-1].end()
            content = content[:insert_pos] + "import com.kotlinjsonui.dynamic.helpers.ModifierBuilder\n" + content[insert_pos:]
        else:
            # Add after package statement
            package_match = re.search(r'(package .*\n\n)', content)
            if package_match:
                insert_pos = package_match.end()
                content = content[:insert_pos] + "import com.kotlinjsonui.dynamic.helpers.ModifierBuilder\n" + content[insert_pos:]
    
    # Replace buildModifier function that uses asFloat directly
    # This is a simplified replacement - we'll need to handle each component's specific needs
    
    # For now, just add the import and print which files need manual update
    with open(file_path, 'w') as f:
        f.write(content)
    
    print(f"Added ModifierBuilder import to {component_file}")
    print(f"  TODO: Update buildModifier function to use ModifierBuilder.buildModifier()")

print("\nDone! Now need to manually update the buildModifier functions in each component.")