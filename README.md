# Wear OS Alarm App - Qualification Task

## Introduction
This project is part of the **GSoC 2025 Qualification Task** for integrating a Wear OS alarm system with the [Ultimate Alarm Clock](https://github.com/CCExtractor/ultimate_alarm_clock). It serves as a foundation for a future **companion app**, focusing on alarm management within Wear OS.

For detailed documentation, refer to the official project document: [Google Docs](https://docs.google.com/document/d/1taKun29BOqkpHbgkU0SdB0K-2pjz-hcU8WgJIgWeynM/edit?tab=t.0).
## Features
- Set, Edit, and Dismiss Alarms directly from the watch.
- Wear OS notifications with vibration and ringtone.
- Alarms persist even after a device restart.
- Optimized UI using Jetpack Compose for Wear OS.
- Efficient performance without draining battery.

## Implementation Details

### Alarm Scheduling & Storage
- Uses `AlarmManager` to trigger alarms at the correct time.
- Stores alarms locally using SharedPreferences.
- Future updates may transition to SQLite for better performance.

### Alarm Notifications & Dismissal
- Notifications include dismiss and snooze actions.
- Full-screen notification for immediate user attention.
- Can be dismissed from either Wear OS or mobile app.

### Future Companion App Transition
- Will use **Wear OS Data Layer API** for syncing alarms with the mobile app.
- Alarm modifications on either device will reflect across both platforms.

## Why Not Use Flutter for Wear OS UI?
Flutter lacks official support for Wear OS, causing issues like:
1. No official Wear OS widgets for optimized UI ([Flutter Docs](https://docs.flutter.dev)).
2. Performance overhead due to Flutter’s extra rendering layer ([Very Good Ventures](https://verygood.ventures/blog/building-wear-os-apps-with-flutter-a-very-good-guide?utm_source=chatgpt.com)).
3. Lack of community support and optimization.

Solution: Kotlin & Jetpack Compose ensure a native, optimized Wear OS experience.

## Future Enhancements
- Implement snooze functionality with customizable durations.
- Add recurring alarms (Daily, Weekly, Custom intervals).
- Introduce custom alarm sounds.

This qualification task demonstrates the feasibility of a Wear OS alarm system, forming the basis for a full **companion app** in future development phases.

