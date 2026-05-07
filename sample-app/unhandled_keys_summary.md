# Unhandled Keys Summary for XML Generator

## Test Results
- ✅ **All 53 JSON files parsed successfully**
- 📊 Found 851 total components
- 🏷️ 30 unique component types (all mapped)
- 🔑 169 unique attributes found

## Most Common Unhandled Attributes

These attributes appear in JSON files but are not yet handled by the XML generator:

| Attribute | Occurrences | Priority | Notes |
|-----------|-------------|----------|-------|
| `cornerRadius` | 144 | High | Needs drawable background with rounded corners |
| `font` | 39 | High | Font family/style handling |
| `fontWeight` | 34 | High | Bold/normal text style |
| `borderWidth` | 30 | High | Stroke width for shapes |
| `borderColor` | 30 | High | Stroke color for shapes |
| `color` | 22 | High | Text color for various components |
| `alignTop` | 21 | Medium | RelativeLayout alignment |
| `alignLeft` | 21 | Medium | RelativeLayout alignment |
| `margin` | 18 | High | Layout margins |
| `alignRight` | 18 | Medium | RelativeLayout alignment |
| `items` | 17 | Medium | Spinner/SelectBox items |
| `alignBottom` | 17 | Medium | RelativeLayout alignment |
| `padding` | 16 | High | View padding |
| `centerHorizontal` | 16 | Medium | RelativeLayout centering |
| `centerVertical` | 13 | Medium | RelativeLayout centering |
| `centerInParent` | 11 | Medium | RelativeLayout centering |
| `title` | 10 | Low | Custom component property |
| `count` | 10 | Low | Custom component property |
| `selectItemType` | 9 | Low | Spinner configuration |
| `selectedItem` | 9 | Low | Spinner configuration |

## Component-Specific Issues

### Button
- Missing: `cornerRadius` (144 occurrences)
- Needs custom drawable background for styling

### Label/Text/TextView
- Missing: `font`, `fontWeight`, `color`, `margin`
- Alignment attributes for RelativeLayout

### View (Containers)
- Missing: `borderWidth`, `borderColor`, `cornerRadius`
- Needs drawable background for borders and corners

### SelectBox (Spinner)
- Missing: `items`, `selectedItem`, `selectItemType`
- Needs adapter configuration

## Recommendations

1. **Priority 1: Visual Styling**
   - Implement `cornerRadius` with drawable backgrounds
   - Add `borderWidth` and `borderColor` support
   - Handle `font`, `fontWeight`, and `color` attributes

2. **Priority 2: Layout**
   - Add proper `margin` and `padding` mapping
   - Implement RelativeLayout alignment attributes
   - Support centering attributes

3. **Priority 3: Component Data**
   - Handle `items` for Spinner/SelectBox
   - Map custom component properties

## Files to Update

1. `/kjui_tools/lib/xml/helpers/attribute_mapper.rb` - Add missing attribute mappings
2. `/kjui_tools/lib/xml/helpers/drawable_generator.rb` - Create for generating drawable resources
3. `/kjui_tools/lib/xml/helpers/style_generator.rb` - Create for generating styles

## Verification Files Generated

- `verification_report.md` - Full detailed report
- `verification_report.json` - Machine-readable version
- `unhandled_keys_summary.md` - This summary