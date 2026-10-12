# UI tests on a minified (R8) release build

`ScreenMarker` — the beacon generated screens carry so a JsonUI UI test can
tell which screen is on display — draws only in a debuggable app, so a shipped
app never carries it. A debuggable build, though, makes AGP run R8 without
optimization or obfuscation, so UI tests on it do not exercise the code R8
ships.

To run the UI tests on the code a release ships, make a build type from
`release` that stays non-debuggable and declares the marker flag in its own
manifest. The flag is application meta-data:

```
com.kotlinjsonui.SCREEN_MARKER
```

## 1. A build type made from release

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        create("uiTestRelease") {
            initWith(getByName("release"))   // same R8 / resource shrinking as release
            isDebuggable = false
            matchingFallbacks += "release"
            // sign it with a key the test APK can use, as for any release-like build
        }
    }
    testBuildType = "uiTestRelease"          // if the instrumented tests target it
}
```

## 2. The flag, in that build type's source set only

`app/src/uiTestRelease/AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data
            android:name="com.kotlinjsonui.SCREEN_MARKER"
            android:value="true" />
    </application>
</manifest>
```

The manifest merger adds it to `uiTestRelease` and to nothing else, so
`release` — what ships — has no flag and draws no marker, as before.

## Notes

- No ProGuard / R8 rule is needed. Generated screens call `ScreenMarker`
  unconditionally and the gate reads the flag at run time, so R8 keeps the
  marker in every build; without the flag it simply draws nothing.
- The flag is read once per screen composition (`PackageManager` with
  `GET_META_DATA`); a debuggable app does not read it.
- From KotlinJsonUI 2.43.8. Ticket:
  kjui-screenmarker-gated-on-debuggable-blocks-r8-ui-tests.
