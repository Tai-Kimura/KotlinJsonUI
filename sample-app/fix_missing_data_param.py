#!/usr/bin/env python3
import os
import re

components_dir = "/Users/like-a-rolling_stone/resource/KotlinJsonUI/library/src/main/kotlin/com/kotlinjsonui/dynamic/components"

# Components that already have data parameter
skip_components = [
    "DynamicTextComponent.kt",
    "DynamicTextFieldComponent.kt", 
    "DynamicButtonComponent.kt",
    "DynamicImageComponent.kt",
    "DynamicContainerComponent.kt"
]

for filename in os.listdir(components_dir):
    if filename.startswith("Dynamic") and filename.endswith(".kt"):
        if filename in skip_components:
            continue
            
        filepath = os.path.join(components_dir, filename)
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Check if this component has the create method without data parameter
        if "fun create(json: JsonObject)" in content:
            # Replace the signature
            content = content.replace(
                "fun create(json: JsonObject)",
                "fun create(\n            json: JsonObject,\n            data: Map<String, Any> = emptyMap()\n        )"
            )
            
            # Also need to pass data to any child DynamicView calls
            # This is a simple placeholder implementation for now
            if "DynamicView(" in content:
                content = content.replace(
                    "DynamicView(it)",
                    "DynamicView(it, data)"
                )
                content = content.replace(
                    "DynamicView(child)",
                    "DynamicView(child, data)"
                )
            
            with open(filepath, 'w') as f:
                f.write(content)
            print(f"Updated {filename}")

print("Done!")