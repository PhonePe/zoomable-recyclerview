# ZoomableRecyclerView

A drop-in replacement for `RecyclerView` that adds **pinch-to-zoom**, **double-tap zoom**, and **pan** support — with no changes required to your existing adapter or layout manager.

---

## Features

- 🔍 **Pinch-to-zoom** — smooth scaling with two fingers
- 👆 **Double-tap zoom** — tap twice to zoom in/out with animation
- ✋ **Pan / drag** — scroll freely when zoomed in
- 🚀 **Fling support** — natural momentum scrolling while zoomed
- 🔒 **Constrained panning** — content never pans outside its bounds
- ⚙️ **Fully configurable** — via XML attributes or Kotlin/Java at runtime
- 🔌 **Drop-in replacement** — works with any existing `RecyclerView` adapter and layout manager

---

## Installation

### Step 1 — Add JitPack to your repositories

In your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2 — Add the dependency

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.PhonePe:zoomable-recyclerview:1.0.0")
}
```

---

## Usage

### XML

Replace `RecyclerView` with `ZoomableRecyclerView` in your layout:

```xml
<com.phonepe.zoomablerecyclerview.ZoomableRecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:zrv_minScale="1.0"
    app:zrv_maxScale="3.0"
    app:zrv_doubleTapZoomScale="2.0"
    app:zrv_zoomAnimationDuration="300"
    app:zrv_zoomEnabled="true"
    app:zrv_doubleTapZoomEnabled="true"
    app:zrv_flingEnabled="true" />
```

### Kotlin

```kotlin
val recyclerView = findViewById<ZoomableRecyclerView>(R.id.recyclerView)

// Set up just like a regular RecyclerView
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = myAdapter

// Optionally configure zoom behaviour at runtime
recyclerView.minScale = 1.0f
recyclerView.maxScale = 4.0f
recyclerView.doubleTapZoomScale = 2.5f
recyclerView.zoomAnimationDuration = 250L
recyclerView.isZoomEnabled = true
recyclerView.isDoubleTapZoomEnabled = true
recyclerView.isFlingEnabled = true
```

---

## XML Attributes

| Attribute                    | Type    | Default | Description                                               |
|-----------------------------|---------|---------|-----------------------------------------------------------|
| `zrv_minScale`              | float   | `1.0`   | Minimum zoom level (can't zoom out beyond natural size)   |
| `zrv_maxScale`              | float   | `3.0`   | Maximum zoom level reachable via pinch                    |
| `zrv_doubleTapZoomScale`    | float   | `2.0`   | Zoom level jumped to on double-tap                        |
| `zrv_zoomAnimationDuration` | integer | `300`   | Duration of the double-tap zoom animation in milliseconds |
| `zrv_zoomEnabled`           | boolean | `true`  | Master switch — disables all zoom/pan when `false`        |
| `zrv_doubleTapZoomEnabled`  | boolean | `true`  | Disables double-tap zoom (pinch still works when `false`) |
| `zrv_flingEnabled`          | boolean | `true`  | Disables fling gesture while zoomed in when `false`       |

---

## Runtime Properties

| Property                  | Type      | Description                                     |
|--------------------------|-----------|-------------------------------------------------|
| `currentScale`           | `Float`   | Read-only. The current zoom scale level.        |
| `minScale`               | `Float`   | Minimum allowed zoom level.                     |
| `maxScale`               | `Float`   | Maximum allowed zoom level.                     |
| `doubleTapZoomScale`     | `Float`   | Target zoom level on double-tap.                |
| `zoomAnimationDuration`  | `Long`    | Animation duration for double-tap zoom (ms).    |
| `isZoomEnabled`          | `Boolean` | Enables/disables all zoom and pan interactions. |
| `isDoubleTapZoomEnabled` | `Boolean` | Enables/disables double-tap zoom.               |
| `isFlingEnabled`         | `Boolean` | Enables/disables fling while zoomed in.         |

---

## Requirements

- **Min SDK:** 21 (Android 5.0+)
- **Compile SDK:** 36

---

## License

```
Copyright 2026 PhonePe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
