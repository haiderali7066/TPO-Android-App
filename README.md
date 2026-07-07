<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://chatgpt.com/s/m_6a4c4e69967881919f35d140f0edddd6" />
</div>

# TPO – Track Period & Ovulation

A modern Android application for tracking menstrual cycles and predicting ovulation dates with a simple, clean, and user-friendly experience.

Developed by **Devntom Solutions**.

---

## Overview

TPO (Track Period & Ovulation) helps users monitor their menstrual cycle by providing:

* Period predictions
* Ovulation predictions
* Calendar-based tracking
* Smart reminders and notifications
* Cycle history management
* Personalized cycle settings

The app is designed to be lightweight, intuitive, and focused on the two most important cycle events: **Periods** and **Ovulation**.

---

## Features

### User Onboarding

* Enter name and age
* Select last period start date
* Set cycle length
* Set period duration

### Smart Calendar

* Monthly calendar view
* Navigate between months
* Visual indicators for important dates
* Tap dates for detailed information

### Period Tracking

* Predict upcoming periods
* Display period duration
* Highlight period days on the calendar

### Ovulation Prediction

* Calculate estimated ovulation date
* Display ovulation day on calendar
* Show countdown to ovulation

### Notifications

* Period starts tomorrow
* Period starts today
* Ovulation is tomorrow
* Ovulation day reminder

### Cycle History

* View previous cycles
* Track period dates
* Track ovulation dates

### Settings

* Edit personal information
* Change cycle length
* Change period duration
* Update period start date
* Enable or disable notifications

### Dark Mode

* Light Theme
* Dark Theme
* System Theme Support

---

## Tech Stack

### Android

* Kotlin
* Jetpack Compose
* Material 3

### Architecture

* MVVM Architecture
* Repository Pattern

### Local Storage

* Room Database
* DataStore Preferences

### Notifications

* WorkManager

### Navigation

* Navigation Compose

---

## Screens

### Splash Screen

Application branding and loading screen.

### Welcome Screen

Introduction and onboarding.

### User Information Screen

Collect user cycle information.

### Home Screen

Main calendar interface with period and ovulation predictions.

### Date Details Bottom Sheet

Shows information about selected dates.

### History Screen

Displays previous cycle records.

### Notifications Screen

Preview and manage reminders.

### Settings Screen

Manage user preferences and cycle settings.

### About Screen

Application information and version details.

---

## Calendar Indicators

| Icon | Meaning                 |
| ---- | ----------------------- |
| 🩸   | Predicted Period Day    |
| 🧬   | Predicted Ovulation Day |

---

## Prediction Logic

### Period Calculation

Period Start Date → User Selected Date

Period End Date → Start Date + Period Duration - 1

### Next Period

Next Period = Last Period Start Date + Cycle Length

### Ovulation

Ovulation = Next Period - 14 Days

The application automatically generates future predictions and updates them whenever user settings change.

---

## Project Structure

```text
app/
├── data/
│   ├── local/
│   ├── datastore/
│   ├── repository/
│
├── domain/
│   ├── models/
│   ├── usecases/
│
├── presentation/
│   ├── splash/
│   ├── onboarding/
│   ├── home/
│   ├── history/
│   ├── notifications/
│   ├── settings/
│   ├── about/
│
├── navigation/
│
├── workers/
│
├── utils/
│
└── ui/
    ├── components/
    ├── theme/
```

---

## Installation

### Clone Repository

```bash
git clone https://github.com/haiderali7066/TPO-Android-App.git
```

### Open Project


```bash
Gradle Sync
```

### Run

```bash
Run on Android Emulator or Physical Device
```

---

## Privacy Notice

TPO stores cycle information locally on the user's device. No personal health data is shared with third parties without user consent.

---

## Roadmap

### Future Enhancements

* Cloud backup
* Multi-device synchronization
* Advanced cycle analytics
* Health insights
* Wearable integration
* Export reports
* Widget support

---

## Contributing

Contributions, bug reports, and feature requests are welcome.

Please create an issue before submitting large changes.

---

## License

This project is licensed under the MIT License.

---

## Developed By

### Devntom Solutions  devntomsolutions.com

Building modern web, mobile, and software solutions.

© Devntom Solutions. All Rights Reserved.
