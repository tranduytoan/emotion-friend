# Emotion Friend — Material 3 Theme Guideline

> **Branch**: `feat/material3-theme-polish`  
> **Audience**: All Android team members (Nghĩa, Bình, Hoa, …)  
> **Design goal**: Autism-friendly — calm, predictable, high-contrast-enough but never harsh.

---

## 1. Imports

All theme symbols live in one package. Import as needed:

```kotlin
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.dimensions      // MaterialTheme.dimensions
// Color tokens (used by feature screens directly):
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.WarmCream
// … etc.
```

---

## 2. Color Palette

### 2.1 Primary — Sky Blue

| Token | Hex | Usage |
|---|---|---|
| `SkyBlue40` | `#4DA8CF` | Primary buttons, active icons, links |
| `SkyBlue80` | `#B3DFF0` | Primary container backgrounds |
| `SkyBlueLight` | `#E3F4FB` | Hover / pressed overlay |

### 2.2 Secondary — Mint Green

| Token | Hex | Usage |
|---|---|---|
| `MintGreen40` | `#5BAD8F` | Secondary actions, progress pills |
| `MintGreen80` | `#B8E3D3` | Secondary container / correct-answer highlight |

### 2.3 Tertiary — Sun Yellow *(warm accent)*

| Token | Hex | Usage |
|---|---|---|
| `SunYellow40` | `#F9A825` | Badges, streak counters, notifications |
| `SunYellow80` | `#FFECB3` | Tertiary container (soft amber) |

### 2.4 Background & Surface

| Token | Hex | Usage |
|---|---|---|
| `WarmCream` | `#FFF8F2` | Screen background — soothing warm white |
| `SurfaceWhite` | `#FFFFFF` | Cards, bottom sheets |
| `SurfaceVariant` | `#F0EBE5` | Alternative card fill, input backgrounds |

### 2.5 On-colours (text / icons on colour surfaces)

| Token | Hex | On which background |
|---|---|---|
| `OnPrimary` | `#FFFFFF` | On SkyBlue40, MintGreen40, SunYellow40 |
| `OnBackground` | `#1C1A18` | On WarmCream, light containers |
| `OnSurface` | `#2A2522` | On SurfaceWhite |
| `OnSurfaceVar` | `#4A4540` | On SurfaceVariant |

### 2.6 Error — Soft red *(not harsh pure red)*

| Token | Hex | Usage |
|---|---|---|
| `ErrorRed` | `#BA1A1A` | Error icons, invalid-field border |
| `ErrorRedContainer` | `#FFDAQ6` | Error banner / toast background |
| `OnErrorRed` | `#FFFFFF` | Text on ErrorRed surface |
| `OnErrorRedContainer` | `#410002` | Text inside error container |

### 2.7 Inverse surface *(Snackbars, dark overlays)*

| Token | Hex | Usage |
|---|---|---|
| `SurfaceInverse` | `#312F2D` | Snackbar background |
| `OnSurfaceInverse` | `#FBEFE8` | Snackbar text |
| `PrimaryInverse` | `#99CDEF` | Snackbar action button text |

### 2.8 Emotion colours *(never change — children rely on consistency)*

| Token | Hex | Emotion |
|---|---|---|
| `EmotionHappy` | `#FFD600` | Happy |
| `EmotionSad` | `#42A5F5` | Sad |
| `EmotionAngry` | `#EF5350` | Angry |
| `EmotionTired` | `#AB47BC` | Tired |
| `EmotionSurprised` | `#FF7043` | Surprised |
| `EmotionCalm` | `#66BB6A` | Calm |

Each emotion also has a `*Bg` variant (soft pastel background for option buttons).

### 2.9 Outlines

| Token | Usage |
|---|---|
| `OutlineMedium` | Default borders, card strokes |
| `OutlineLight` | Subtle dividers, unselected progress pills |

---

## 3. Typography

Use `MaterialTheme.typography.*` — never hard-code font sizes.

| Style | Size | Weight | Usage |
|---|---|---|---|
| `displayLarge` | 40sp Bold | Screen hero text (rarely used) |
| `displayMedium` | 34sp Bold | Large prompt question |
| `displaySmall` | 30sp Bold | Secondary hero |
| `headlineLarge` | 32sp Bold | Section title on full-screen |
| `headlineMedium` | 26sp SemiBold | Card section heading |
| `headlineSmall` | 22sp SemiBold | Sub-section heading |
| `titleLarge` | 22sp SemiBold | **TopAppBar title** (`EmotionScreenScaffold`) |
| `titleMedium` | 18sp Medium | Card title, dialog title |
| `titleSmall` | 15sp Medium | Chip label, tab label |
| `bodyLarge` | 18sp Normal | Primary instruction text |
| `bodyMedium` | 16sp Normal | Secondary description |
| `bodySmall` | 13sp Normal | Caption, hint text |
| `labelLarge` | 18sp **Bold** | **Button text** (`EmotionPrimaryButton`) |
| `labelMedium` | 14sp Medium | Secondary button, chip text |
| `labelSmall` | 11sp Medium | Badge count, micro label |

```kotlin
// Example
Text(text = screenTitle, style = MaterialTheme.typography.titleLarge)
Button(onClick = …) {
    Text("Tiếp theo", style = MaterialTheme.typography.labelLarge)
}
```

---

## 4. Spacing & Dimensions

Access via `MaterialTheme.dimensions` (provided by `LocalAppDimensions` in `EmotionFriendTheme`).

```kotlin
val d = MaterialTheme.dimensions
```

### 4.1 Spacing scale

| Property | Value | Usage |
|---|---|---|
| `d.spacingXxs` | 2dp | Hairline gap |
| `d.spacingXs` | 4dp | Icon-to-label gap |
| `d.spacingSm` | 8dp | Compact element gap |
| `d.spacingMd` | 16dp | Default section gap, content padding |
| `d.spacingLg` | 24dp | Between cards / major sections |
| `d.spacingXl` | 32dp | Large visual break |
| `d.spacingXxl` | 48dp | Full-screen breathing room |

### 4.2 Screen layout

```kotlin
Modifier
    .padding(
        horizontal = MaterialTheme.dimensions.screenHorizontalPadding, // 20dp
        vertical   = MaterialTheme.dimensions.screenVerticalPadding     // 16dp
    )
```

### 4.3 Touch targets

| Property | Value | Rule |
|---|---|---|
| `d.touchTargetMin` | 48dp | Minimum for any tappable element |
| `d.buttonHeight` | 56dp | Primary button (`EmotionPrimaryButton`) |
| `d.optionButtonHeight` | 80dp | Emotion option buttons |
| `d.iconButtonSize` | 48dp | Icon-only buttons |

```kotlin
// Primary button (already set in EmotionPrimaryButton)
Modifier.heightIn(min = MaterialTheme.dimensions.buttonHeight)

// Option button
Modifier.heightIn(min = MaterialTheme.dimensions.optionButtonHeight)
```

### 4.4 Card layout

```kotlin
Card(modifier = Modifier.padding(MaterialTheme.dimensions.cardPadding)) { … }
// elevation = MaterialTheme.dimensions.cardElevation  (2dp)
```

### 4.5 Emoji / icon sizes

| Property | Value | Usage |
|---|---|---|
| `d.emojiMd` | 40dp | Inline emoji in lists |
| `d.emojiLg` | 56dp | Card hero emoji |
| `d.emojiXl` | 72dp | Full-screen featured emoji |

---

## 5. Shapes

Use `MaterialTheme.shapes.*` — never set `RoundedCornerShape` directly.

| Shape tier | Corner radius | Use for |
|---|---|---|
| `extraSmall` | 8dp | Chips, small badges, snackbars |
| `small` | 12dp | Text fields, input boxes |
| `medium` | 20dp | Dialogs, bottom sheets (small) |
| `large` | 28dp | **Cards**, **Option buttons** (`EmotionOptionButton`, `EmotionCard`) |
| `extraLarge` | 50dp (full pill) | **Primary buttons** (`EmotionPrimaryButton`), FABs |

```kotlin
// Card
Card(shape = MaterialTheme.shapes.large) { … }

// Pill button
Button(shape = MaterialTheme.shapes.extraLarge, …) { … }
```

---

## 6. Component Quick Reference

| Component | Shape | Typography | Min Height | Color |
|---|---|---|---|---|
| `EmotionPrimaryButton` | `extraLarge` | `labelLarge` | 56dp | `primary` |
| `EmotionOptionButton` | `large` | `titleMedium` | 80dp | Per-emotion |
| `EmotionCard` | `large` | `titleMedium` | — | `surfaceVariant` |
| `EmotionScreenScaffold` TopAppBar | — | `titleLarge` | — | `WarmCream` bg |
| `FeedbackBanner` (correct) | `medium` | `bodyLarge` | — | `FeedbackCorrectBg` |
| `FeedbackBanner` (wrong) | `medium` | `bodyLarge` | — | `FeedbackWrongBg` |
| `ProgressPill` | `extraLarge` | — | — | `MintGreen40/80` |

---

## 7. Autism-Friendly Design Rules

### ✅ DO

- Use `WarmCream` (`#FFF8F2`) as the screen background — never pure white or dark.
- Keep button labels **short** (≤ 3 words). Use `labelLarge`.
- Always maintain **minimum 48dp touch targets**.
- Use consistent emotion colors — `EmotionHappy` is always yellow. Never swap.
- Use `shapes.large` (28dp) for interactive cards so they look friendly and distinct.
- Provide visual AND text feedback (icon + `FeedbackBanner`) after every action.
- Prefer `spacingLg` (24dp) between major sections — children need breathing room.

### ❌ DON'T

- Don't use `EmotionAngry` (#EF5350) or pure red for UI chrome — only for the angry emotion tile.
- Don't hard-code sizes or colors in feature screens — always use theme tokens.
- Don't add animations that loop or flash — use simple, one-shot transitions.
- Don't use `bodySmall` (13sp) for primary instructions — use `bodyLarge` (18sp) minimum.
- Don't put more than **3 choices** on screen at once.
- Don't use italic or condensed fonts — they reduce readability for children.
- Don't rely on color alone to convey state — always pair with an icon or label.

---

## 8. Adding a New Screen

```kotlin
@Composable
fun MyNewScreen(onBack: () -> Unit) {
    EmotionScreenScaffold(        // sets WarmCream bg + safe insets
        title    = "Tiêu đề",
        onBack   = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(
                    horizontal = MaterialTheme.dimensions.screenHorizontalPadding,
                    vertical   = MaterialTheme.dimensions.screenVerticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spacingLg)
        ) {
            Text("Hướng dẫn", style = MaterialTheme.typography.bodyLarge)

            EmotionPrimaryButton(
                text    = "Bắt đầu",
                onClick = { … }
            )
        }
    }
}
```

---

## 9. Design Token File Map

| File | What it defines |
|---|---|
| [Color.kt](../android-app/app/src/main/java/com/emotionfriend/core/designsystem/theme/Color.kt) | All raw color constants |
| [Theme.kt](../android-app/app/src/main/java/com/emotionfriend/core/designsystem/theme/Theme.kt) | `EmotionFriendTheme`, `lightColorScheme` slots |
| [Type.kt](../android-app/app/src/main/java/com/emotionfriend/core/designsystem/theme/Type.kt) | `Typography` — all 15 M3 text styles |
| [Shape.kt](../android-app/app/src/main/java/com/emotionfriend/core/designsystem/theme/Shape.kt) | `Shapes` — 5 M3 shape tiers |
| [Dimensions.kt](../android-app/app/src/main/java/com/emotionfriend/core/designsystem/theme/Dimensions.kt) | `AppDimensions` spacing/size tokens |
