# Mis Finanzas

![Mis Finanzas Logo](https://via.placeholder.com/150 ) <!-- Reemplaza con la URL de tu logo si tienes uno -->

### Overview
"**Mis Finanzas**" is a mobile application designed to help users manage their personal finances effectively. The app focuses on financial literacy, budgeting, and responsible spending habits, making it an essential tool for young adults and anyone looking to take control of their financial health.

This repository contains the source code for the "Mis Finanzas" application, developed using modern Android technologies such as Jetpack Compose, Room, and Firebase.

---

## Features

- **Financial Tracking:**  
  - Record income and expenses with detailed categorization.
  - View summaries of monthly income, expenses, and balance.
  - Analyze spending patterns through visual reports.

- **Subscription Management:**  
  - Track recurring subscriptions with automatic reminders.
  - Set up notifications for upcoming payments.

- **Budgeting Tools:**  
  - Set financial goals and track progress.
  - Receive alerts when approaching budget limits.

- **User-Friendly Interface:**  
  - Intuitive design built with Jetpack Compose.
  - Dark mode support for better readability.

- **Data Security:**  
  - Secure data storage using Room Database (local) and Firebase (cloud).
  - Compliance with LFPDPPP and GDPR standards.

---

## Directory Structure

The project is organized into the following key directories:

- **`MisFinanzas/`**:  
  - Contains the main source code for the application.
  - Follows a clean architecture pattern with modular components.

- **`Documentation/`**:  
  - Includes technical documentation, user guides, and design specifications.

- **`ProjectProgress/`**:  
  - Progress updates, wireframes, and planning documents.

- **`.gitignore`**:  
  - Specifies files and directories to ignore during version control.

---

## Getting Started

### Prerequisites

- **Android Studio**: Ensure you have the latest version installed.
- **Kotlin**: Familiarity with Kotlin programming language.
- **Firebase Account**: Required for authentication and cloud storage.

### Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/ZapatoProgramming/MisFinanzas.git

2. **Import Project:**
- Open Android Studio.
- Navigate to File > Open and select the MisFinanzas folder.
3. **Configure Firebase:**
- Create a Firebase project in the Firebase Console .
- Add your Android app to Firebase and download the google-services.json file.
- Place the file in the app/ directory.
4. **Run the App:**
- Connect a physical device or use an emulator.
- Build and run the app from Android Studio.

## Architecture Overview

The application follows the MVVM (Model-View-ViewModel) architecture:

- Model: Handles data persistence (Room Database, Firebase).
- View: Built with Jetpack Compose for a modern UI.
- ViewModel: Manages business logic and state management.

### Key Components:

Room Database: Local storage for transactions, categories, and user data.
Firebase: Authentication, cloud storage, and real-time synchronization.
Jetpack Compose: Declarative UI framework for building responsive interfaces.
Contributing

We welcome contributions to enhance the app! Here's how you can get involved:

## How to Contribute

1. **Fork the Repository:**
Fork this repository to your GitHub account.
2. **Create a New Branch:**
```bash
  git checkout -b feature/new-feature
```
3. **Make Changes:**
Implement your changes and ensure they align with the project's coding standards.
4. **Test Your Changes:**
Run unit tests and ensure no regressions are introduced:
5. **Submit a Pull Request:**
Push your branch to GitHub and open a pull request against the develop branch. Provide a clear description of your changes.

## Acknowledgments

Thanks to the open-source community for providing tools like Jetpack Compose, Room, and Firebase.
Special thanks to contributors who helped improve this project.

Happy coding! 🚀
