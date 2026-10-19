# Project Plan

Create a modern Android Journaling app inspired by A2UI (https://github.com/a2ui-project/a2ui). The home screen should display a list of past entries sorted by date, as well as a button to create new entries. When creating a new entry, include fields for title, date, content, and a drop-down to select mood. The app should have a modern look using Material 3 and incorporate images of tranquil nature scenes into the header.

## Project Brief

# Project Brief: Modern Nature Journal App

A modern, tranquil Android journaling application inspired by A2UI, designed to capture daily thoughts and track moods within a serene Material 3 interface.

## Features

1. **Chronological Entry List**: A feed displaying past journal entries sorted by date with title summaries, date tags, and mood indicators.
2. **Entry Creation Form**: An interactive creation screen/dialog with fields for entry title, date selection, multi-line body text, and a drop-down selector for mood.
3. **Tranquil Nature Header**: A modern Material 3 header layout featuring serene nature scene imagery to evoke a calm journaling environment.
4. **Adaptive List-Detail Layout**: Responsive layout structure providing smooth navigation between the entry list and entry creation/detail views across various screen sizes.

## High-Level Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (`androidx.compose.material3`)
- **Navigation**: Jetpack Navigation 3 (state-driven navigation)
- **Adaptive Strategy**: Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Architecture & Asynchronous Execution**: Android ViewModel, StateFlow, and Kotlin Coroutines (In-Memory State Management)

## Implementation Steps

