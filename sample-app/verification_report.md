# KotlinJsonUI JSON Parser Verification Report

**Generated:** 2025-08-29 07:27:44

## Summary

| Metric | Value |
|--------|-------|
| Total JSON files | 53 |
| Successfully parsed | 53 |
| Failed to parse | 0 |
| Total components | 851 |
| Unique component types | 30 |
| Unique attributes | 168 |

## Component Types Found

- ✅ `BlurView`
- ✅ `Button`
- ✅ `Checkbox`
- ✅ `CircleImage`
- ✅ `Collection`
- ✅ `GradientView`
- ✅ `Image`
- ✅ `Indicator`
- ✅ `Label`
- ✅ `NetworkImage`
- ✅ `Progress`
- ✅ `Radio`
- ✅ `SafeAreaView`
- ✅ `SampleCard`
- ✅ `Scroll`
- ✅ `ScrollView`
- ✅ `Segment`
- ✅ `SelectBox`
- ✅ `Slider`
- ✅ `StatusBadge`
- ✅ `Switch`
- ✅ `TabView`
- ✅ `Text`
- ✅ `TextField`
- ✅ `TextView`
- ✅ `Toggle`
- ✅ `UserAvatar`
- ✅ `View`
- ✅ `Web`
- ✅ `WebView`

## All Attributes Found

```
accessoryBackground
accessoryTextColor
alignBottom
alignBottomOfView
alignBottomView
alignCenterHorizontalView
alignCenterVerticalView
alignLeft
alignLeftOfView
alignLeftView
alignRight
alignRightOfView
alignRightView
alignTop
alignTopOfView
alignTopView
alpha
avatarUrl
background
blurRadius
borderColor
borderStyle
borderWidth
bottomMargin
cancelButtonBackgroundColor
cancelButtonTextColor
centerHorizontal
centerInParent
centerVertical
checked
child
children
clipToBounds
color
colors
columns
containerInset
contentMode
cornerRadius
count
data
dateFormat
datePickerMode
datePickerStyle
dateStringFormat
defaultImage
defaultIndex
direction
disabledBackground
disabledFontColor
distribution
doneText
edgeInset
enabled
endDate
endPoint
flexible
font
fontColor
fontSize
fontWeight
group
height
hidden
hideOnFocused
hilightColor
hint
hintAttributes
hintColor
icon
id
idealHeight
idealWidth
include
indicatorStyle
input
isOn
isOnline
itemSpacing
items
javascriptEnabled
keyboardAvoidance
keyboardType
layout
leftMargin
leftPadding
lineBreakMode
lineSpacing
lines
linkable
margin
marginBottom
marginRight
marginTop
margins
maxHeight
maxValue
maxWidth
maximumDate
maximumValue
minHeight
minValue
minWidth
minimumDate
minimumValue
minuteInterval
name
normalColor
onBeginEditing
onBlur
onClick
onEndEditing
onFocus
onTextChange
onValueChange
onclick
opacity
orientation
padding
paddings
partialAttributes
placeholder
progress
prompt
returnKeyType
rightMargin
rightPadding
scalesPageToFit
scrollEnabled
sections
secure
selectItemType
selectedColor
selectedIcon
selectedIndex
selectedItem
selectedTabIndex
selectedValue
shared_data
showsHorizontalScrollIndicator
showsVerticalScrollIndicator
size
src
srcName
startDate
startPoint
status
style
subtitle
tabs
tapBackground
text
textAlign
textColor
tint
tintColor
title
topMargin
touchDisabledState
type
underline
url
value
visibility
weight
width
widthWeight
zIndex
```

## Unhandled Keys and Attributes

### binding_test.json

- **Location:** `root[0][12]`
  - Attribute 'value' with value '"@{sliderValue}"' not handled for type 'Slider'
- **Location:** `root[0][12]`
  - Attribute 'minimumValue' with value '0' not handled for type 'Slider'
- **Location:** `root[0][12]`
  - Attribute 'maximumValue' with value '100' not handled for type 'Slider'
- **Location:** `root[0][12]`
  - Attribute 'onValueChange' with value '"sliderChanged"' not handled for type 'Slider'
- **Location:** `root[0][16]`
  - Attribute 'hintColor' with value '"#999999"' not handled for type 'SelectBox'
- **Location:** `root[0][19]`
  - Attribute 'datePickerStyle' with value '"wheels"' not handled for type 'SelectBox'
- **Location:** `root[0][19]`
  - Attribute 'dateFormat' with value '"yyyy年MM月dd日"' not handled for type 'SelectBox'
- **Location:** `root[0][19]`
  - Attribute 'minimumDate' with value '"2020-01-01"' not handled for type 'SelectBox'
- **Location:** `root[0][19]`
  - Attribute 'maximumDate' with value '"2030-12-31"' not handled for type 'SelectBox'
- **Location:** `root[0][19]`
  - Attribute 'hintColor' with value '"#999999"' not handled for type 'SelectBox'
- **Location:** `root[0][21]`
  - Attribute 'datePickerStyle' with value '"compact"' not handled for type 'SelectBox'
- **Location:** `root[0][21]`
  - Attribute 'dateFormat' with value '"MM/dd/yyyy"' not handled for type 'SelectBox'
- **Location:** `root[0][21]`
  - Attribute 'hintColor' with value '"#999999"' not handled for type 'SelectBox'

### collection_test.json

- **Location:** `root[0][0][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][2]`
  - Attribute 'sections' with value '[{"header"=>"SectionHeader", "cell"=>"BasicCell", "footer"=>"SectionFooter", "columns"=>2}]' not handled for type 'Collection'
- **Location:** `root[0][0][2]`
  - Attribute 'layout' with value '"vertical"' not handled for type 'Collection'
- **Location:** `root[0][0][3]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][4]`
  - Attribute 'layout' with value '"vertical"' not handled for type 'Collection'
- **Location:** `root[0][0][4]`
  - Attribute 'columns' with value '2' not handled for type 'Collection'
- **Location:** `root[0][0][4]`
  - Attribute 'sections' with value '[{"header"=>"GridHeader", "cell"=>"ImageCell", "footer"=>"GridFooter", "columns"=>3}]' not handled for type 'Collection'
- **Location:** `root[0][0][5]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][6]`
  - Attribute 'sections' with value '[{"header"=>"HorizontalHeader", "cell"=>"HorizontalCard", "columns"=>1}]' not handled for type 'Collection'
- **Location:** `root[0][0][6]`
  - Attribute 'layout' with value '"horizontal"' not handled for type 'Collection'
- **Location:** `root[0][0][7]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][8]`
  - Attribute 'sections' with value '[{"header"=>"CategoryHeader", "cell"=>"ProductCell", "footer"=>"CategoryFooter", "columns"=>2}]' not handled for type 'Collection'
- **Location:** `root[0][0][8]`
  - Attribute 'layout' with value '"vertical"' not handled for type 'Collection'
- **Location:** `root[0][0][9]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][10]`
  - Attribute 'sections' with value '[{"header"=>"CategoryHeader", "cell"=>"ProductCell", "footer"=>"CategoryFooter", "columns"=>3}, {"header"=>"FeaturedHeader", "cell"=>"FeatureCell", "columns"=>2}, {"header"=>"GridHeader", "cell"=>"ImageCell", "footer"=>"GridFooter", "columns"=>4}]' not handled for type 'Collection'
- **Location:** `root[0][0][10]`
  - Attribute 'layout' with value '"vertical"' not handled for type 'Collection'

### components_test.json

- **Location:** `root[0][3]`
  - Attribute 'isOn' with value '"@{toggle1IsOn}"' not handled for type 'Toggle'
- **Location:** `root[0][4]`
  - Attribute 'isOn' with value '"@{checkbox1IsOn}"' not handled for type 'Checkbox'
- **Location:** `root[0][7]`
  - Attribute 'value' with value '"@{slider1Value}"' not handled for type 'Slider'
- **Location:** `root[0][9]`
  - Attribute 'selectedIndex' with value '"@{selectedSegment1}"' not handled for type 'Segment'
- **Location:** `root[0][10]`
  - Attribute 'selectedValue' with value '"@{selectedRadio1}"' not handled for type 'Radio'
- **Location:** `root[0][12]`
  - Attribute 'indicatorStyle' with value '"circular"' not handled for type 'Indicator'
- **Location:** `root[0][14]`
  - Attribute 'url' with value '"person.circle.fill"' not handled for type 'CircleImage'
- **Location:** `root[0][18]`
  - Attribute 'indicatorStyle' with value '"light"' not handled for type 'BlurView'

### converter_test.json

- **Location:** `root[0][3]`
  - Attribute 'colors' with value '["#FF0000", "#00FF00", "#0000FF"]' not handled for type 'GradientView'
- **Location:** `root[0][3]`
  - Attribute 'startPoint' with value '{"x"=>0, "y"=>0}' not handled for type 'GradientView'
- **Location:** `root[0][3]`
  - Attribute 'endPoint' with value '{"x"=>1, "y"=>1}' not handled for type 'GradientView'
- **Location:** `root[0][5]`
  - Attribute 'zIndex' with value 'true' not handled for type 'View'
- **Location:** `root[0][5][1]`
  - Attribute 'blurRadius' with value '20' not handled for type 'BlurView'
- **Location:** `root[0][7]`
  - Attribute 'url' with value '"https://www.example.com"' not handled for type 'WebView'
- **Location:** `root[0][9]`
  - Attribute 'selectedTabIndex' with value '0' not handled for type 'TabView'
- **Location:** `root[0][9]`
  - Attribute 'tabs' with value '[{"title"=>"Tab 1", "child"=>[{"type"=>"Label", "text"=>"Content of Tab 1", "fontSize"=>14, "centerInParent"=>true, "fontColor"=>"#000000"}]}, {"title"=>"Tab 2", "child"=>[{"type"=>"Label", "text"=>"Content of Tab 2", "fontSize"=>14, "centerInParent"=>true, "fontColor"=>"#000000"}]}]' not handled for type 'TabView'
- **Location:** `root[0][11]`
  - Attribute 'itemSpacing' with value '15' not handled for type 'Collection'
- **Location:** `root[0][11]`
  - Attribute 'sections' with value '[{"cell"=>"ConverterTestCell", "columns"=>3}]' not handled for type 'Collection'
- **Location:** `root[0][13]`
  - Attribute 'srcName' with value '"star.fill"' not handled for type 'Image'
- **Location:** `root[0][13]`
  - Attribute 'defaultImage' with value '"exclamationmark.triangle"' not handled for type 'Image'
- **Location:** `root[0][13]`
  - Attribute 'contentMode' with value '"scaleAspectFit"' not handled for type 'Image'
- **Location:** `root[0][15]`
  - Attribute 'defaultImage' with value '"photo"' not handled for type 'NetworkImage'
- **Location:** `root[0][15]`
  - Attribute 'contentMode' with value '"scaleAspectFill"' not handled for type 'NetworkImage'

### custom_component_test.json

- **Location:** `root[0][0][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][2]`
  - Attribute 'subtitle' with value '"This is a static subtitle"' not handled for type 'SampleCard'
- **Location:** `root[0][0][3]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][4]`
  - Attribute 'subtitle' with value '"@{cardSubtitle}"' not handled for type 'SampleCard'
- **Location:** `root[0][0][4][0][0]`
  - Attribute 'fontWeight' with value '"medium"' not handled for type 'Label'
- **Location:** `root[0][0][5]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][6][0]`
  - Attribute 'subtitle' with value '"First custom card"' not handled for type 'SampleCard'
- **Location:** `root[0][0][6][1]`
  - Attribute 'subtitle' with value '"Second custom card"' not handled for type 'SampleCard'
- **Location:** `root[0][0][6][2]`
  - Attribute 'subtitle' with value '"Third custom card"' not handled for type 'SampleCard'
- **Location:** `root[0][0][7]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][0][8][0]`
  - Attribute 'status' with value '"Active"' not handled for type 'StatusBadge'
- **Location:** `root[0][0][8][1]`
  - Attribute 'status' with value '"@{currentStatus}"' not handled for type 'StatusBadge'
- **Location:** `root[0][0][8][2]`
  - Attribute 'status' with value '"Error"' not handled for type 'StatusBadge'

### date_picker_test.json

- **Location:** `root[0][3]`
  - Attribute 'datePickerMode' with value '"date"' not handled for type 'SelectBox'
- **Location:** `root[0][3]`
  - Attribute 'dateStringFormat' with value '"yyyy-MM-dd"' not handled for type 'SelectBox'
- **Location:** `root[0][6]`
  - Attribute 'minimumDate' with value '"2025-01-01"' not handled for type 'SelectBox'
- **Location:** `root[0][6]`
  - Attribute 'maximumDate' with value '"2025-12-31"' not handled for type 'SelectBox'
- **Location:** `root[0][6]`
  - Attribute 'datePickerMode' with value '"date"' not handled for type 'SelectBox'
- **Location:** `root[0][6]`
  - Attribute 'dateStringFormat' with value '"yyyy-MM-dd"' not handled for type 'SelectBox'
- **Location:** `root[0][8]`
  - Attribute 'datePickerMode' with value '"time"' not handled for type 'SelectBox'
- **Location:** `root[0][8]`
  - Attribute 'dateStringFormat' with value '"HH:mm"' not handled for type 'SelectBox'
- **Location:** `root[0][10]`
  - Attribute 'datePickerMode' with value '"dateAndTime"' not handled for type 'SelectBox'
- **Location:** `root[0][10]`
  - Attribute 'dateStringFormat' with value '"yyyy-MM-dd HH:mm"' not handled for type 'SelectBox'
- **Location:** `root[0][13]`
  - Attribute 'datePickerMode' with value '"time"' not handled for type 'SelectBox'
- **Location:** `root[0][13]`
  - Attribute 'minuteInterval' with value '15' not handled for type 'SelectBox'
- **Location:** `root[0][13]`
  - Attribute 'dateStringFormat' with value '"HH:mm"' not handled for type 'SelectBox'
- **Location:** `root[0][15]`
  - Attribute 'datePickerMode' with value '"date"' not handled for type 'SelectBox'
- **Location:** `root[0][15]`
  - Attribute 'datePickerStyle' with value '"graphical"' not handled for type 'SelectBox'
- **Location:** `root[0][15]`
  - Attribute 'dateStringFormat' with value '"yyyy-MM-dd"' not handled for type 'SelectBox'
- **Location:** `root[0][16]`
  - Attribute 'startDate' with value '"@{startDate}"' not handled for type 'SelectBox'
- **Location:** `root[0][16]`
  - Attribute 'endDate' with value '"@{endDate}"' not handled for type 'SelectBox'

### disabled_test.json

- **Location:** `root[0][5]`
  - Attribute 'disabledBackground' with value '"#CCCCCC"' not handled for type 'Button'
- **Location:** `root[0][5]`
  - Attribute 'disabledFontColor' with value '"#999999"' not handled for type 'Button'
- **Location:** `root[0][7]`
  - Attribute 'touchDisabledState' with value 'true' not handled for type 'Button'
- **Location:** `root[0][11]`
  - Attribute 'disabledBackground' with value '"#E0E0E0"' not handled for type 'TextField'
- **Location:** `root[0][11]`
  - Attribute 'disabledFontColor' with value '"#999999"' not handled for type 'TextField'
- **Location:** `root[0][14]`
  - Attribute 'disabledBackground' with value '"#D0D0D0"' not handled for type 'Button'
- **Location:** `root[0][14]`
  - Attribute 'disabledFontColor' with value '"#888888"' not handled for type 'Button'

### form_test.json

- **Location:** `root`
  - Attribute 'keyboardAvoidance' with value 'true' not handled for type 'ScrollView'
- **Location:** `root[0][2]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][8]`
  - Attribute 'keyboardType' with value '"email"' not handled for type 'TextField'
- **Location:** `root[0][10]`
  - Attribute 'keyboardType' with value '"phone"' not handled for type 'TextField'
- **Location:** `root[0][11]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][17]`
  - Attribute 'keyboardType' with value '"number"' not handled for type 'TextField'
- **Location:** `root[0][20]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][25]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[0][27]`
  - Attribute 'flexible' with value 'true' not handled for type 'TextView'
- **Location:** `root[0][27]`
  - Attribute 'minHeight' with value '80' not handled for type 'TextView'
- **Location:** `root[0][27]`
  - Attribute 'maxHeight' with value '200' not handled for type 'TextView'
- **Location:** `root[0][27]`
  - Attribute 'hintColor' with value '"#999999"' not handled for type 'TextView'
- **Location:** `root[0][29]`
  - Attribute 'hintColor' with value '"#AAAAAA"' not handled for type 'TextView'
- **Location:** `root[0][29]`
  - Attribute 'hideOnFocused' with value 'false' not handled for type 'TextView'
- **Location:** `root[0][31]`
  - Attribute 'flexible' with value 'true' not handled for type 'TextView'
- **Location:** `root[0][31]`
  - Attribute 'minHeight' with value '60' not handled for type 'TextView'
- **Location:** `root[0][31]`
  - Attribute 'maxHeight' with value '300' not handled for type 'TextView'
- **Location:** `root[0][31]`
  - Attribute 'hintAttributes' with value '{"fontColor"=>"#BBBBBB", "fontSize"=>14, "fontStyle"=>"italic"}' not handled for type 'TextView'
- **Location:** `root[0][32][0]`
  - Attribute 'isOn' with value '"@{agreeToTerms}"' not handled for type 'Toggle'
- **Location:** `root[0][33]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Button'
- **Location:** `root[0][34]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Button'

### horizontal_card.json

- **Location:** `root[1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'

### image_cell.json

- **Location:** `root[0]`
  - Attribute 'contentMode' with value '"scaleAspectFill"' not handled for type 'NetworkImage'
- **Location:** `root[1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'

### implemented_attributes_test.json

- **Location:** `root[child]`
  - Attribute 'keyboardAvoidance' with value 'true' not handled for type 'ScrollView'
- **Location:** `root[child][child][2][1]`
  - Attribute 'alignCenterVerticalView' with value '"target1"' not handled for type 'Label'
- **Location:** `root[child][child][2][2]`
  - Attribute 'alignCenterHorizontalView' with value '"target1"' not handled for type 'Label'
- **Location:** `root[child][child][4]`
  - Attribute 'idealWidth' with value '200' not handled for type 'View'
- **Location:** `root[child][child][4]`
  - Attribute 'idealHeight' with value '100' not handled for type 'View'
- **Location:** `root[child][child][6]`
  - Attribute 'clipToBounds' with value 'true' not handled for type 'View'
- **Location:** `root[child][child][8]`
  - Attribute 'direction' with value '"rightToLeft"' not handled for type 'View'
- **Location:** `root[child][child][8]`
  - Attribute 'distribution' with value '"fillEqually"' not handled for type 'View'
- **Location:** `root[child][child][10]`
  - Attribute 'edgeInset' with value '20' not handled for type 'Label'
- **Location:** `root[child][child][12]`
  - Attribute 'tapBackground' with value '"#ff00ff"' not handled for type 'Button'
- **Location:** `root[child][child][13]`
  - Attribute 'hilightColor' with value '"#ff0000"' not handled for type 'Button'
- **Location:** `root[child][child][14]`
  - Attribute 'disabledFontColor' with value '"#999999"' not handled for type 'Button'
- **Location:** `root[child][child][14]`
  - Attribute 'disabledBackground' with value '"#f0f0f0"' not handled for type 'Button'
- **Location:** `root[child][child][16]`
  - Attribute 'onFocus' with value '"handleFocus"' not handled for type 'TextField'
- **Location:** `root[child][child][16]`
  - Attribute 'onBlur' with value '"handleBlur"' not handled for type 'TextField'
- **Location:** `root[child][child][16]`
  - Attribute 'onBeginEditing' with value '"handleBeginEditing"' not handled for type 'TextField'
- **Location:** `root[child][child][16]`
  - Attribute 'onEndEditing' with value '"handleEndEditing"' not handled for type 'TextField'
- **Location:** `root[child][child][18][0]`
  - Attribute 'group' with value '"radioGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][18][0]`
  - Attribute 'icon' with value '"circle"' not handled for type 'Radio'
- **Location:** `root[child][child][18][0]`
  - Attribute 'selectedIcon' with value '"checkmark.circle.fill"' not handled for type 'Radio'
- **Location:** `root[child][child][18][1]`
  - Attribute 'group' with value '"radioGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][18][1]`
  - Attribute 'icon' with value '"circle"' not handled for type 'Radio'
- **Location:** `root[child][child][18][1]`
  - Attribute 'selectedIcon' with value '"checkmark.circle.fill"' not handled for type 'Radio'
- **Location:** `root[child][child][20]`
  - Attribute 'selectedIndex' with value '"@{selectedSegment}"' not handled for type 'Segment'
- **Location:** `root[child][child][20]`
  - Attribute 'normalColor' with value '"#000000"' not handled for type 'Segment'
- **Location:** `root[child][child][20]`
  - Attribute 'selectedColor' with value '"#0000ff"' not handled for type 'Segment'
- **Location:** `root[child][child][20]`
  - Attribute 'tintColor' with value '"#0000ff"' not handled for type 'Segment'
- **Location:** `root[child][child][22]`
  - Attribute 'prompt' with value '"Choose an option"' not handled for type 'SelectBox'
- **Location:** `root[child][child][22]`
  - Attribute 'defaultIndex' with value '0' not handled for type 'SelectBox'
- **Location:** `root[child][child][22]`
  - Attribute 'cancelButtonBackgroundColor' with value '"#FFE5E5"' not handled for type 'SelectBox'
- **Location:** `root[child][child][22]`
  - Attribute 'cancelButtonTextColor' with value '"#FF0000"' not handled for type 'SelectBox'
- **Location:** `root[child][child][24]`
  - Attribute 'url' with value '"https://www.example.com"' not handled for type 'Web'
- **Location:** `root[child][child][24]`
  - Attribute 'scalesPageToFit' with value 'true' not handled for type 'Web'
- **Location:** `root[child][child][24]`
  - Attribute 'javascriptEnabled' with value 'true' not handled for type 'Web'

### include_test.json

- **Location:** `root[0][1][1][3][0]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'

### keyboard_avoidance_test.json

- **Location:** `root`
  - Attribute 'keyboardAvoidance' with value 'true' not handled for type 'ScrollView'
- **Location:** `root[0][3]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[0][5]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[0][7]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[0][9]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[0][11]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[0][13]`
  - Attribute 'hintColor' with value '"#999999"' not handled for type 'TextView'

### line_break_test.json

- **Location:** `root[0][3]`
  - Attribute 'lineBreakMode' with value '"Word"' not handled for type 'Label'
- **Location:** `root[0][3]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][5]`
  - Attribute 'lineBreakMode' with value '"Char"' not handled for type 'Label'
- **Location:** `root[0][5]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][7]`
  - Attribute 'lineBreakMode' with value '"Clip"' not handled for type 'Label'
- **Location:** `root[0][7]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][9]`
  - Attribute 'lineBreakMode' with value '"Head"' not handled for type 'Label'
- **Location:** `root[0][9]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][11]`
  - Attribute 'lineBreakMode' with value '"Middle"' not handled for type 'Label'
- **Location:** `root[0][11]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][13]`
  - Attribute 'lineBreakMode' with value '"Tail"' not handled for type 'Label'
- **Location:** `root[0][13]`
  - Attribute 'lines' with value '2' not handled for type 'Label'
- **Location:** `root[0][16]`
  - Attribute 'lines' with value '1' not handled for type 'Label'
- **Location:** `root[0][16]`
  - Attribute 'lineBreakMode' with value '"Tail"' not handled for type 'Label'
- **Location:** `root[0][18]`
  - Attribute 'lines' with value '3' not handled for type 'Label'
- **Location:** `root[0][18]`
  - Attribute 'lineBreakMode' with value '"Tail"' not handled for type 'Label'
- **Location:** `root[0][20]`
  - Attribute 'lines' with value '0' not handled for type 'Label'

### margins_test.json

- **Location:** `root[0][2]`
  - Attribute 'margins' with value '[20, 20, 20, 20]' not handled for type 'Label'
- **Location:** `root[0][10][child]`
  - Attribute 'maxWidth' with value '200' not handled for type 'Label'
- **Location:** `root[0][11][child]`
  - Attribute 'minWidth' with value '150' not handled for type 'Label'

### partial_attributes_test.json

- **Location:** `root[child][child][2]`
  - Attribute 'partialAttributes' with value '[{"range"=>[14, 21], "fontColor"=>"#FF0000", "fontWeight"=>"bold"}, {"range"=>[22, 29], "fontColor"=>"#00FF00", "underline"=>true}, {"range"=>[50, 55], "fontColor"=>"#0000FF", "fontSize"=>20, "background"=>"#FFFF00"}]' not handled for type 'Label'
- **Location:** `root[child][child][3]`
  - Attribute 'partialAttributes' with value '[{"range"=>[6, 10], "fontColor"=>"#0000FF", "underline"=>true, "onclick"=>"navigateToPage1"}, {"range"=>[27, 31], "fontColor"=>"#0000FF", "underline"=>true, "onclick"=>"navigateToPage2"}]' not handled for type 'Label'
- **Location:** `root[child][child][4]`
  - Attribute 'partialAttributes' with value '[{"range"=>[14, 18], "fontWeight"=>"bold"}, {"range"=>[20, 26], "fontColor"=>"#FF00FF"}, {"range"=>[28, 37], "underline"=>true}, {"range"=>[39, 53], "strikethrough"=>true, "fontColor"=>"#999999"}]' not handled for type 'Label'
- **Location:** `root[child][child][5]`
  - Attribute 'partialAttributes' with value '[{"range"=>"天気", "fontColor"=>"#FF0000", "fontWeight"=>"bold", "fontSize"=>20}, {"range"=>"晴れる", "fontColor"=>"#0000FF", "underline"=>true}]' not handled for type 'Label'

### product_cell.json

- **Location:** `root[0]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'

### radio_icons_test.json

- **Location:** `root[child][child][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][2]`
  - Attribute 'group' with value '"defaultGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][3]`
  - Attribute 'group' with value '"defaultGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][4]`
  - Attribute 'group' with value '"defaultGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][5]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][6]`
  - Attribute 'group' with value '"customGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][6]`
  - Attribute 'icon' with value '"star"' not handled for type 'Radio'
- **Location:** `root[child][child][6]`
  - Attribute 'selectedIcon' with value '"star.fill"' not handled for type 'Radio'
- **Location:** `root[child][child][7]`
  - Attribute 'group' with value '"customGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][7]`
  - Attribute 'icon' with value '"heart"' not handled for type 'Radio'
- **Location:** `root[child][child][7]`
  - Attribute 'selectedIcon' with value '"heart.fill"' not handled for type 'Radio'
- **Location:** `root[child][child][8]`
  - Attribute 'group' with value '"customGroup"' not handled for type 'Radio'
- **Location:** `root[child][child][8]`
  - Attribute 'icon' with value '"square"' not handled for type 'Radio'
- **Location:** `root[child][child][8]`
  - Attribute 'selectedIcon' with value '"checkmark.square.fill"' not handled for type 'Radio'
- **Location:** `root[child][child][9]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][10]`
  - Attribute 'selectedValue' with value '"@{selectedColor}"' not handled for type 'Radio'

### relative_test.json

- **Location:** `root[child][6][0]`
  - Attribute 'margins' with value '[20, 25, 20, 25]' not handled for type 'Label'
- **Location:** `root[child][6][1]`
  - Attribute 'alignTopView' with value '"anchor_with_margin"' not handled for type 'Label'
- **Location:** `root[child][6][2]`
  - Attribute 'alignBottomView' with value '"anchor_with_margin"' not handled for type 'Label'
- **Location:** `root[child][6][3]`
  - Attribute 'alignLeftView' with value '"anchor_with_margin"' not handled for type 'Label'
- **Location:** `root[child][6][4]`
  - Attribute 'alignRightView' with value '"anchor_with_margin"' not handled for type 'Label'
- **Location:** `root[child][8][0]`
  - Attribute 'margins' with value '[15, 15, 15, 15]' not handled for type 'Label'
- **Location:** `root[child][10][0]`
  - Attribute 'margins' with value '[10, 10, 10, 10]' not handled for type 'Label'
- **Location:** `root[child][10][2]`
  - Attribute 'margins' with value '[15, 15, 15, 15]' not handled for type 'Label'
- **Location:** `root[child][12][2]`
  - Attribute 'margins' with value '[15, 20, 15, 20]' not handled for type 'Label'
- **Location:** `root[child][14][3]`
  - Attribute 'alignLeftView' with value '"chain_middle"' not handled for type 'Label'
- **Location:** `root[child][14][4]`
  - Attribute 'alignRightView' with value '"chain_start"' not handled for type 'Label'
- **Location:** `root[child][16][3]`
  - Attribute 'alignTopView' with value '"ref1"' not handled for type 'Label'
- **Location:** `root[child][16][3]`
  - Attribute 'alignRightView' with value '"ref2"' not handled for type 'Label'
- **Location:** `root[child][16][4]`
  - Attribute 'alignLeftView' with value '"ref1"' not handled for type 'Label'

### scroll_test.json

- **Location:** `root[1]`
  - Attribute 'showsVerticalScrollIndicator' with value 'false' not handled for type 'ScrollView'
- **Location:** `root[2]`
  - Attribute 'scrollEnabled' with value 'false' not handled for type 'ScrollView'
- **Location:** `root[3]`
  - Attribute 'showsHorizontalScrollIndicator' with value 'false' not handled for type 'ScrollView'

### secure_field_test.json

- **Location:** `root[3]`
  - Attribute 'secure' with value 'false' not handled for type 'TextField'
- **Location:** `root[5]`
  - Attribute 'secure' with value 'true' not handled for type 'TextField'
- **Location:** `root[7]`
  - Attribute 'secure' with value 'true' not handled for type 'TextField'

### segment_test.json

- **Location:** `root[child][child][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][2]`
  - Attribute 'selectedIndex' with value '"@{selectedBasic}"' not handled for type 'Segment'
- **Location:** `root[child][child][3]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][4]`
  - Attribute 'selectedIndex' with value '"@{selectedColor}"' not handled for type 'Segment'
- **Location:** `root[child][child][4]`
  - Attribute 'normalColor' with value '"#666666"' not handled for type 'Segment'
- **Location:** `root[child][child][4]`
  - Attribute 'selectedColor' with value '"#FF0000"' not handled for type 'Segment'
- **Location:** `root[child][child][5]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][6]`
  - Attribute 'selectedIndex' with value '"@{selectedEvent}"' not handled for type 'Segment'
- **Location:** `root[child][child][6]`
  - Attribute 'onValueChange' with value '"handleSegmentChange"' not handled for type 'Segment'
- **Location:** `root[child][child][8]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][9]`
  - Attribute 'selectedIndex' with value '"@{selectedDisabled}"' not handled for type 'Segment'

### switch_events_test.json

- **Location:** `root[child][child][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][2]`
  - Attribute 'isOn' with value '"@{notificationEnabled}"' not handled for type 'Switch'
- **Location:** `root[child][child][2]`
  - Attribute 'onValueChange' with value '"handleNotificationChange"' not handled for type 'Switch'
- **Location:** `root[child][child][4]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][5]`
  - Attribute 'isOn' with value '"@{darkModeEnabled}"' not handled for type 'Switch'
- **Location:** `root[child][child][5]`
  - Attribute 'onValueChange' with value '"handleDarkModeChange"' not handled for type 'Switch'
- **Location:** `root[child][child][7]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][8]`
  - Attribute 'isOn' with value '"@{wifiEnabled}"' not handled for type 'Switch'
- **Location:** `root[child][child][8]`
  - Attribute 'tintColor' with value '"#4CAF50"' not handled for type 'Switch'
- **Location:** `root[child][child][8]`
  - Attribute 'onValueChange' with value '"handleWifiChange"' not handled for type 'Switch'
- **Location:** `root[child][child][9]`
  - Attribute 'isOn' with value '"@{bluetoothEnabled}"' not handled for type 'Switch'
- **Location:** `root[child][child][9]`
  - Attribute 'tintColor' with value '"#2196F3"' not handled for type 'Switch'
- **Location:** `root[child][child][9]`
  - Attribute 'onValueChange' with value '"handleBluetoothChange"' not handled for type 'Switch'
- **Location:** `root[child][child][10]`
  - Attribute 'isOn' with value '"@{locationEnabled}"' not handled for type 'Switch'
- **Location:** `root[child][child][10]`
  - Attribute 'tintColor' with value '"#FF9800"' not handled for type 'Switch'
- **Location:** `root[child][child][10]`
  - Attribute 'onValueChange' with value '"handleLocationChange"' not handled for type 'Switch'
- **Location:** `root[child][child][11]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'

### text_decoration_test.json

- **Location:** `root[0][1]`
  - Attribute 'linkable' with value 'true' not handled for type 'Label'
- **Location:** `root[0][2]`
  - Attribute 'linkable' with value 'true' not handled for type 'Label'
- **Location:** `root[0][3]`
  - Attribute 'linkable' with value 'true' not handled for type 'Label'
- **Location:** `root[0][4]`
  - Attribute 'edgeInset' with value '10' not handled for type 'Label'
- **Location:** `root[0][4]`
  - Attribute 'linkable' with value 'true' not handled for type 'Label'
- **Location:** `root[0][5]`
  - Attribute 'linkable' with value 'false' not handled for type 'Label'

### text_styling_test.json

- **Location:** `root[0][9]`
  - Attribute 'underline' with value 'true' not handled for type 'Label'
- **Location:** `root[0][15]`
  - Attribute 'lineSpacing' with value '5' not handled for type 'Label'
- **Location:** `root[0][16]`
  - Attribute 'lineSpacing' with value '10' not handled for type 'Label'

### text_view_hint_test.json

- **Location:** `root[3]`
  - Attribute 'hintColor' with value '"#FF0000"' not handled for type 'TextView'
- **Location:** `root[3]`
  - Attribute 'containerInset' with value '12' not handled for type 'TextView'
- **Location:** `root[5]`
  - Attribute 'flexible' with value 'true' not handled for type 'TextView'
- **Location:** `root[5]`
  - Attribute 'minHeight' with value '80' not handled for type 'TextView'
- **Location:** `root[5]`
  - Attribute 'maxHeight' with value '200' not handled for type 'TextView'
- **Location:** `root[5]`
  - Attribute 'hintColor' with value '"#0000FF"' not handled for type 'TextView'
- **Location:** `root[5]`
  - Attribute 'containerInset' with value '12' not handled for type 'TextView'

### textfield_events_test.json

- **Location:** `root[child][child][1]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][2]`
  - Attribute 'input' with value '"email"' not handled for type 'TextField'
- **Location:** `root[child][child][2]`
  - Attribute 'returnKeyType' with value '"done"' not handled for type 'TextField'
- **Location:** `root[child][child][2]`
  - Attribute 'onTextChange' with value '"handleEmailChange"' not handled for type 'TextField'
- **Location:** `root[child][child][4]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][5]`
  - Attribute 'secure' with value 'true' not handled for type 'TextField'
- **Location:** `root[child][child][5]`
  - Attribute 'returnKeyType' with value '"done"' not handled for type 'TextField'
- **Location:** `root[child][child][5]`
  - Attribute 'onTextChange' with value '"handlePasswordChange"' not handled for type 'TextField'
- **Location:** `root[child][child][7]`
  - Attribute 'fontWeight' with value '"semibold"' not handled for type 'Label'
- **Location:** `root[child][child][8]`
  - Attribute 'accessoryBackground' with value '"#007AFF"' not handled for type 'TextField'
- **Location:** `root[child][child][8]`
  - Attribute 'accessoryTextColor' with value '"#FFFFFF"' not handled for type 'TextField'
- **Location:** `root[child][child][8]`
  - Attribute 'doneText' with value '"Finish"' not handled for type 'TextField'

### textfield_test.json

- **Location:** `root[1]`
  - Attribute 'input' with value '"email"' not handled for type 'TextField'
- **Location:** `root[1]`
  - Attribute 'returnKeyType' with value '"next"' not handled for type 'TextField'
- **Location:** `root[2]`
  - Attribute 'secure' with value 'true' not handled for type 'TextField'
- **Location:** `root[2]`
  - Attribute 'returnKeyType' with value '"done"' not handled for type 'TextField'
- **Location:** `root[3]`
  - Attribute 'input' with value '"phone"' not handled for type 'TextField'
- **Location:** `root[3]`
  - Attribute 'returnKeyType' with value '"next"' not handled for type 'TextField'
- **Location:** `root[4]`
  - Attribute 'input' with value '"number"' not handled for type 'TextField'
- **Location:** `root[4]`
  - Attribute 'returnKeyType' with value '"done"' not handled for type 'TextField'
- **Location:** `root[5]`
  - Attribute 'returnKeyType' with value '"search"' not handled for type 'TextField'
- **Location:** `root[6]`
  - Attribute 'input' with value '"url"' not handled for type 'TextField'
- **Location:** `root[6]`
  - Attribute 'returnKeyType' with value '"go"' not handled for type 'TextField'

### user_profile_test.json

- **Location:** `root[0][0]`
  - Attribute 'subtitle' with value '"Manage your account"' not handled for type 'SampleCard'
- **Location:** `root[0][0][0][0][0]`
  - Attribute 'name' with value '"@{userName}"' not handled for type 'UserAvatar'
- **Location:** `root[0][0][0][0][0]`
  - Attribute 'avatarUrl' with value '"@{userAvatar}"' not handled for type 'UserAvatar'
- **Location:** `root[0][0][0][0][0]`
  - Attribute 'size' with value '64' not handled for type 'UserAvatar'
- **Location:** `root[0][0][0][0][0]`
  - Attribute 'isOnline' with value '"@{isOnline}"' not handled for type 'UserAvatar'
- **Location:** `root[0][0][0][2]`
  - Attribute 'status' with value '"@{userStatus}"' not handled for type 'StatusBadge'
- **Location:** `root[0][2][0]`
  - Attribute 'name' with value '"Alice Johnson"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][0]`
  - Attribute 'size' with value '48' not handled for type 'UserAvatar'
- **Location:** `root[0][2][0]`
  - Attribute 'isOnline' with value 'true' not handled for type 'UserAvatar'
- **Location:** `root[0][2][1]`
  - Attribute 'name' with value '"Bob Smith"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][1]`
  - Attribute 'avatarUrl' with value '"https://i.pravatar.cc/150?img=3"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][1]`
  - Attribute 'size' with value '48' not handled for type 'UserAvatar'
- **Location:** `root[0][2][1]`
  - Attribute 'isOnline' with value 'false' not handled for type 'UserAvatar'
- **Location:** `root[0][2][2]`
  - Attribute 'name' with value '"Carol Williams"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][2]`
  - Attribute 'avatarUrl' with value '"https://i.pravatar.cc/150?img=5"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][2]`
  - Attribute 'size' with value '48' not handled for type 'UserAvatar'
- **Location:** `root[0][2][2]`
  - Attribute 'isOnline' with value 'true' not handled for type 'UserAvatar'
- **Location:** `root[0][2][3]`
  - Attribute 'name' with value '"David Brown"' not handled for type 'UserAvatar'
- **Location:** `root[0][2][3]`
  - Attribute 'size' with value '48' not handled for type 'UserAvatar'
- **Location:** `root[0][2][3]`
  - Attribute 'isOnline' with value 'false' not handled for type 'UserAvatar'

### visibility_test.json

- **Location:** `root[0][19]`
  - Attribute 'hidden' with value '"@{isHidden}"' not handled for type 'Label'

### weight_test.json

- **Location:** `root[7][0]`
  - Attribute 'widthWeight' with value '1' not handled for type 'Label'
- **Location:** `root[7][1]`
  - Attribute 'widthWeight' with value '1' not handled for type 'Label'
