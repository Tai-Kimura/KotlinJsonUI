#!/usr/bin/env python3
"""
Fix all Dynamic components to use ModifierBuilder
"""

import os
import re

components_to_fix = [
    "DynamicContainerComponent.kt",
    "DynamicTextComponent.kt",
    "DynamicImageComponent.kt",
    "DynamicTextFieldComponent.kt",
    "DynamicRadioComponent.kt",
    "DynamicCheckBoxComponent.kt",
    "DynamicSwitchComponent.kt"
]

base_path = "/Users/like-a-rolling_stone/resource/KotlinJsonUI/library/src/main/kotlin/com/kotlinjsonui/dynamic/components"

for component_file in components_to_fix:
    file_path = os.path.join(base_path, component_file)
    
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        continue
    
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Find and replace the buildModifier function
    # Pattern to match the entire buildModifier function
    pattern = r'private fun buildModifier\(json: JsonObject\): Modifier \{[^}]*?// Width and height.*?(?=\n        private fun |$)'
    
    # Check if this component has applyMargins and applyPadding functions
    has_apply_functions = 'private fun applyMargins' in content and 'private fun applyPadding' in content
    
    if has_apply_functions:
        # Replace buildModifier to use ModifierBuilder
        new_build_modifier = '''private fun buildModifier(json: JsonObject): Modifier {
            // Use ModifierBuilder for basic size and spacing
            var modifier = ModifierBuilder.buildModifier(json)
            
            // Background color (before clip for proper rendering)
            json.get("background")?.asString?.let { colorStr ->
                try {
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    modifier = modifier.background(color)
                } catch (e: Exception) {
                    // Invalid color
                }
            }
            
            // Corner radius (clip)
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)
                
                // If we have a border, apply it with the same shape
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            } ?: run {
                // No corner radius, but might still have border
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            }
            
            // Shadow/elevation
            json.get("shadow")?.let { shadow ->
                when {
                    shadow.isJsonPrimitive -> {
                        val primitive = shadow.asJsonPrimitive
                        when {
                            primitive.isBoolean && primitive.asBoolean -> {
                                modifier = modifier.shadow(6.dp)
                            }
                            primitive.isNumber -> {
                                modifier = modifier.shadow(primitive.asFloat.dp)
                            }
                        }
                    }
                }
            }
            
            return modifier
        }'''
        
        # Find the buildModifier function
        build_modifier_match = re.search(
            r'(        private fun buildModifier\(json: JsonObject\): Modifier \{.*?\n        \})',
            content,
            re.DOTALL
        )
        
        if build_modifier_match:
            # Replace it
            content = content.replace(build_modifier_match.group(1), new_build_modifier)
            
            # Remove applyMargins and applyPadding functions
            # Pattern to match these functions
            apply_pattern = r'\n        private fun apply(?:Margins|Padding)\(inputModifier: Modifier, json: JsonObject\): Modifier \{[^}]*?\n            \}\n        \}'
            content = re.sub(apply_pattern, '', content, flags=re.DOTALL)
            
            with open(file_path, 'w') as f:
                f.write(content)
            print(f"Fixed {component_file}")
        else:
            print(f"Could not find buildModifier in {component_file}")
    else:
        # For simpler components, just replace the width/height handling
        # Replace the buildModifier to use ModifierBuilder
        simple_pattern = r'private fun buildModifier\(json: JsonObject\): Modifier \{[^}]*?json\.get\("width"\)\?\.asFloat.*?return modifier\s*\}'
        
        simple_replacement = '''private fun buildModifier(json: JsonObject): Modifier {
            // Use ModifierBuilder for size and margins
            return ModifierBuilder.buildSizeModifier(json).let { modifier ->
                ModifierBuilder.applyMargins(modifier, json)
            }
        }'''
        
        content = re.sub(simple_pattern, simple_replacement, content, flags=re.DOTALL)
        
        with open(file_path, 'w') as f:
            f.write(content)
        print(f"Fixed simple component {component_file}")

print("\nDone!")