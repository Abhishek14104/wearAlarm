# Wear OS Alarm App - Qualification Task
![Static Badge](https://img.shields.io/badge/GSoC'25%20Qualification%20Task-8A2BE2)
## Introduction
This project is part of the **GSoC 2025 Qualification Task** for integrating a Wear OS alarm system with the [Ultimate Alarm Clock](https://github.com/CCExtractor/ultimate_alarm_clock). It serves as a foundation for a future **companion app**, focusing on alarm management within Wear OS.

Watch the [Demo Video](https://youtu.be/hqj3ZzVvHEM?si=Tue3dLpux-eK1gLq).

## Features
- Set, Edit, and Dismiss Alarms directly from the watch.
- Wear OS notifications with vibration and ringtone.
- Alarms persist even after a device restart.
- Efficient performance without draining the battery.


## Implementation Details

### Alarm Scheduling & Storage
- Uses AlarmManager to trigger alarms at the correct time.
- Stores alarms locally using SharedPreferences.
- Future updates may transition to SQLite for better performance.

### Alarm Notifications & Dismissal
- Notifications include dismiss and snooze actions.
- Full-screen notification for immediate user attention.
- Can be dismissed from either Wear OS or the mobile app.

### Future Companion App Transition
- Will use **Wear OS Data Layer API** for syncing alarms with the mobile app.
- Alarm modifications on either device will reflect across both platforms.


## Cons
- SharedPreferences (not a real database like SQLite) - used for multiple alarms and is easy to implement
- UI doesn't feel too good (just made for functionalities, as there is always scope for better)


