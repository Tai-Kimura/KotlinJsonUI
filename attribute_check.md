# KotlinJsonUI Attribute Check

## Status: ✅ Fixed

All major non-standard attributes have been fixed to match the official SwiftJsonUI specification.

## Currently Used Attributes in compose_builder.rb

### ✅ Official Attributes (Supported in SwiftJsonUI)

#### Layout & Structure
- `type` - Component type
- `child` / `children` - Child components
- `include` - Include other JSON files
- `orientation` - "vertical" or "horizontal" for layouts
- `width` - Width specification ("matchParent", "wrapContent", number)
- `height` - Height specification ("matchParent", "wrapContent", number)

#### Spacing & Margins
- `padding` - Padding around content
- `topMargin` - Top margin
- `bottomMargin` - Bottom margin
- `leftMargin` - Left margin (should use "start" in Android)
- `rightMargin` - Right margin (should use "end" in Android)

#### Alignment
- `alignTop` - Align to top
- `alignBottom` - Align to bottom
- `alignLeft` - Align to left
- `alignRight` - Align to right
- `centerHorizontal` - Center horizontally
- `centerVertical` - Center vertically
- `centerInParent` - Center in parent
- `alignment` - General alignment setting

#### Visual
- `background` - Background color
- `fontSize` - Font size for text
- `fontColor` - Font color for text
- `fontWeight` - Font weight (e.g., "bold")

#### Text Components
- `text` - Text content
- `title` - Alternative to text for buttons
- `placeholder` - Placeholder text for input fields
- `hint` - Hint text

#### Interaction
- `onclick` - Click handler

#### Image
- `name` / `source` - Image source
- `contentDescription` - Accessibility description
- `size` - Image size

#### TextField
- `value` - Text field value
- `onValueChange` - Value change handler (NOT OFFICIAL - should be `onTextChange`)

### ✅ Fixed Non-Official Attributes

These attributes were incorrect but have now been fixed:

1. ✅ **`margin`** - Now using individual margin attributes (topMargin, bottomMargin, etc.)
2. ✅ **`verticalArrangement`/`horizontalArrangement`** - Removed, now properly mapping `gravity` attribute
3. ✅ **`color`** - Fixed to use `fontColor` for text color
4. ✅ **`fillMaxWidth`** - Now using `width: "matchParent"` 
5. ✅ **`children`** - Fixed to use `child` (array)
6. ✅ **`source`/`name`** - Fixed to use `src` for images
7. ✅ **`onValueChange`** - Fixed to map to `onTextChange` internally
8. ✅ **`value`** - Fixed TextField to use `text` attribute
9. ✅ **`placeholder`** - Fixed TextField to use `hint` attribute

### ✅ Newly Implemented Attributes

These official SwiftJsonUI attributes have now been added:

1. ✅ **`secure`** - For password fields (TextField)
2. ✅ **`cornerRadius`** - Corner radius support
3. ✅ **`borderColor`** - Border color support  
4. ✅ **`borderWidth`** - Border width support
5. ✅ **`font`** - Support for "bold" font attribute
6. ✅ **`gravity`** - Properly mapped to Compose arrangements/alignments

### 📝 Still Missing Official Attributes

Important attributes from SwiftJsonUI that aren't yet implemented:

#### Layout
- `minWidth`, `maxWidth`, `minHeight`, `maxHeight`
- `weight` - Layout weight for linear layouts
- `spacing` - Space between children
- `direction` - Layout direction
- `gravity` - Child alignment in container
- `distribution` - Child distribution in stack

#### Margins & Padding
- `margins` - Array format [top, right, bottom, left]
- `paddings` - Array format [top, right, bottom, left]
- `paddingLeft`, `paddingRight`, `paddingTop`, `paddingBottom`

#### Visual
- `cornerRadius` - Corner radius
- `borderColor`, `borderWidth` - Border styling
- `alpha` - Transparency
- `shadow` - Shadow configuration
- `visibility` - "visible", "invisible", "gone"
- `hidden` - Hidden state

#### Text
- `font` - Font family
- `lines` - Number of lines
- `lineBreakMode` - Line break mode
- `textAlign` - Text alignment
- `edgeInset` - Text padding
- `hintColor`, `hintFont`, `hintFontSize` - Hint styling

#### Button
- `enabled` - Enabled state
- `disabledFontColor`, `disabledBackground` - Disabled state styling
- `hilightColor` - Highlight color

#### TextField
- `secure` - Secure text entry
- `input` - Keyboard type
- `returnKeyType` - Return key type
- `borderStyle` - Border style
- `textAlign` - Text alignment

## Recommended Fixes

### Priority 1 - Fix Wrong Attribute Names
1. Replace `margin` with proper margin handling
2. Replace `color` with `fontColor`
3. Replace `fillMaxWidth` with `width: "matchParent"`
4. Replace `children` with `child`
5. Replace `source`/`name` with `src` for images
6. Replace `onValueChange` with `onTextChange`

### Priority 2 - Remove Custom Attributes
1. Remove `verticalArrangement` and `horizontalArrangement`
2. Map these to proper SwiftJsonUI attributes like `gravity` or `alignment`

### Priority 3 - Add Missing Essential Attributes
1. Add `visibility` and `hidden` support
2. Add `enabled` for buttons
3. Add `cornerRadius` support
4. Add `borderColor` and `borderWidth`
5. Add proper `margins` and `paddings` array support
6. Add `spacing` for container layouts
7. Add `textAlign` for text components