# Miss NO More 🚆📍

An An droid app that helps commuters avoid missing their bus, train, or metro stop by tracking their destination and triggering an alarm when they get close.

![Map Screen](https://github.com/YeMinNaing1112/MissNoMore/blob/3f7ea0210d1894edfa306d55042e4663f1091354/MissNoMorePhoto.png)

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt
- **Local Database:** Room
- **Map:** OpenStreetMap + OSMDroid
- **Navigation:** Jetpack Navigation Compose
- **Async:** Kotlin Coroutines + Flow
- **Location:** Android Location Services + Geofencing

## 🌐 APIs & Services

- **Nominatim API** — Place search
- **OSRM API** — Route calculation
- **OpenStreetMap** — Map data

## ✨ Features

- 🔎 Search for destinations
- 🕘 Save and manage recent places
- 📍 Track current location
- 🗺️ Display destination and route
- 🔔 Alarm and notification when approaching the destination
- ⛔ Cancel destination tracking

## 🏗️ Architecture

Presentation
↓
ViewModel
↓
UseCase
↓
Repository
↓
Remote / Local Data
