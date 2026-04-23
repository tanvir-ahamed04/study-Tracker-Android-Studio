# StudyTracker

StudyTracker is an Android application designed to help users track their study progress, visualize data, and manage their study schedules effectively. The app leverages modern Android libraries and tools to provide a seamless user experience.

## Features
- **Track Study Progress**: Log and monitor your study sessions.
- **Data Visualization**: View study trends using graphs (powered by MPAndroidChart).
- **Database Management**: Store and retrieve data using Room Database.
- **Modern UI**: Built with Material Design components.

## Project Structure
```
StudyTracker/
├── app/                     # Main application module
│   ├── src/                 # Source code
│   │   ├── main/            # Main source set
│   │   │   ├── java/        # Java source files
│   │   │   ├── res/         # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml # App manifest
│   │   ├── androidTest/     # Instrumented tests
│   │   └── test/            # Unit tests
│   ├── build.gradle         # Module-level Gradle configuration
│   └── proguard-rules.pro   # ProGuard rules for release builds
├── build.gradle             # Project-level Gradle configuration
├── settings.gradle          # Gradle settings
├── gradle/                  # Gradle wrapper files
└── gradlew, gradlew.bat     # Gradle wrapper scripts
```

## Prerequisites
- **Android Studio**: Arctic Fox or later
- **JDK**: Java 17
- **Gradle**: Version 8.5.0 (configured via wrapper)

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/tanvir-ahamed04/study-Tracker-Android-Studio.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Build and run the project on an emulator or a physical device.

## Workflow
1. **Development**:
   - Write code in the `app/src/main/java` directory.
   - Add resources (layouts, images, etc.) in the `app/src/main/res` directory.
2. **Testing**:
   - Write unit tests in `app/src/test/java`.
   - Write instrumented tests in `app/src/androidTest/java`.
3. **Build**:
   - Use the Gradle wrapper to build the project:
     ```bash
     ./gradlew build
     ```
4. **Release**:
   - Generate a release APK using the `release` build type.

## Dependencies
The project uses the following dependencies:
- **AndroidX Libraries**: AppCompat, RecyclerView, CardView, etc.
- **Material Design**: For modern UI components.
- **MPAndroidChart**: For data visualization.
- **Room Database**: For local data storage.
- **JUnit and Espresso**: For testing.

## Contribution
Contributions are welcome! Please fork the repository and submit a pull request.

## License
This project is licensed under the MIT License.
