# Beyhive Alert - Android App

This is the Android version of the Beyhive Alert app, built with Jetpack Compose and following Material Design 3 principles.

## Features

- **Home Screen**: Instagram feeds, games, and news sections
- **Videos**: Live stream functionality (coming soon)
- **Game**: Album ranking game and daily trivia
- **Trackers**: Tour notification settings
- **Schedule**: Tour events and schedule information

## Project Structure

```
android-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/beyhivealert/android/
│   │   │   ├── components/          # Reusable UI components
│   │   │   ├── data/               # Data models
│   │   │   ├── navigation/         # Navigation setup
│   │   │   ├── screens/            # Screen composables
│   │   │   ├── ui/theme/           # Theme and styling
│   │   │   ├── viewmodels/         # ViewModels for data management
│   │   │   └── MainActivity.kt     # Main activity
│   │   ├── res/                    # Resources (strings, drawables, etc.)
│   │   └── AndroidManifest.xml     # App manifest
│   └── build.gradle.kts            # App-level dependencies
├── build.gradle.kts                # Project-level configuration
└── settings.gradle.kts             # Project settings
```

## Setup Instructions

1. **Prerequisites**:
   - Android Studio Arctic Fox or later
   - JDK 11 or later
   - Android SDK API 34

2. **Clone and Open**:
   ```bash
   cd android-app
   # Open in Android Studio
   ```

3. **Build and Run**:
   - Sync project with Gradle files
   - Build the project
   - Run on an emulator or physical device

## Key Components

### Navigation
- Bottom navigation with 5 tabs (Home, Videos, Game, Trackers, Schedule)
- Custom bee icon in the center for the Game tab
- Material Design 3 styling

### Instagram Feed
- RSS feed parsing for Instagram content
- Card-based layout with profile images
- Async image loading with Coil

### Theme
- Custom color scheme matching the iOS app
- Yellow primary color with pink and blue accents
- Material Design 3 components

## Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **Navigation Compose**: Navigation between screens
- **Coil**: Image loading library
- **Lifecycle**: ViewModel and state management
- **Material 3**: Design system components

## Development Status

This is a work-in-progress conversion of the iOS Beyhive Alert app. The basic structure and UI components are in place, with more features to be implemented:

- [x] Basic navigation structure
- [x] Home screen with Instagram feeds
- [x] Theme and styling
- [ ] Complete Instagram feed functionality
- [ ] Album ranking game
- [ ] Notification system
- [ ] Tour schedule integration
- [ ] Settings and user preferences

## Contributing

This Android app maintains feature parity with the iOS version while following Android development best practices and Material Design guidelines. 