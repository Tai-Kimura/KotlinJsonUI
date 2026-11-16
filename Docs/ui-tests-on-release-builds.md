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

## 3. The keep rules, for that build type only

```kotlin
// app/build.gradle.kts
dependencies {
    // the same version as jsonui-test-runner-android, from 1.21.0
    "uiTestReleaseImplementation"("io.github.tai-kimura:jsonui-test-runner-android-keep:1.21.0")
}
```

The test APK runs in the app's process and takes from it Compose, Kotlin and
its runner's libraries; R8 removes from the app what the app itself does not
call. The artifact holds no code, only those keep rules, on that build type's
classpath, so `release` is built without them. The driver's own consumer rules
cannot do this: an `androidTest` dependency's rules reach the test APK's R8
only, never the app's. KotlinJsonUI's cannot either: they would reach `release`
too, and keeping Kotlin and the runner's libraries there is 3.1 MB of dex in a
small app.

Without it (measured on AGP 9.5.0, Compose BOM 2026.09.00, API 35):

| Rules | What happens |
|---|---|
| none | The runner dies before the first test: `NoClassDefFoundError: androidx.tracing.Trace` |
| the runner's only | `initialState`, `state` and `hidden` fail naming this artifact, as does a lookup of an element with a 0 x 0 box |

## Notes

- The marker itself needs no rule. Generated screens call `ScreenMarker`
  unconditionally and the gate reads the flag at run time, so R8 keeps the
  marker in every build; without the flag it simply draws nothing.
- The flag is read once per screen composition (`PackageManager` with
  `GET_META_DATA`); a debuggable app does not read it.
- From KotlinJsonUI 2.43.8. Ticket:
  kjui-screenmarker-gated-on-debuggable-blocks-r8-ui-tests.
- The same flag turns on the test bridge (KotlinJsonUI 2.44.0,
  `rememberTestBridge`): the screen applies a UI test's `initialState` from
  the Activity's intent and publishes its Data on the marker node for the
  `state` assertion. With the flag off — a shipped build — the bridge applies
  nothing and publishes nothing, so a test that sends an `initialState` fails
  on the missing receipt rather than running against default values.
