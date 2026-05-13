# KutiraKone 🧶

KutiraKone is a specialized Android application designed for fabric enthusiasts, designers, and hobbyists to discover, upload, and swap unique fabric pieces. Whether you have high-quality scraps or premium silk meters, KutiraKone connects you with a community looking for the perfect material.

## 🚀 Features

- **Fabric Marketplace**: Browse a wide variety of fabrics like Silk, Cotton, and Wool.
- **Search & Filter**: Find exactly what you need using the real-time search and material category filters.
- **Fabric Upload**: Share your own fabric collection with the community by uploading photos and details.
- **Swap Requests**: Interested in a fabric? Send a swap request directly to the owner.
- **Design Ideas**: Get inspiration for your next project with built-in design concepts.
- **Modern UI**: Built with Jetpack Compose for a smooth, modern, and responsive user experience.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Asynchronous Programming**: Coroutines & Flow
- **Navigation**: Compose Navigation
- **Image Loading**: Coil
- **Backend**: Firebase
    - **Authentication**: Anonymous Sign-in (expandable to Google/Email)
    - **Database**: Firestore (for real-time fabric listings)
    - **Storage**: Firebase Storage (for high-quality fabric images)
- **Architecture**: MVVM (Model-View-ViewModel)

## 📦 Installation

To run this project locally:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Chandrakanth-CyberK/KutiraKone.git
   ```
2. **Open in Android Studio**:
   Open the cloned folder as an existing Android Studio project.
3. **Connect Firebase**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with the package name `com.example.kutirakone`.
   - Download the `google-services.json` and place it in the `app/` directory.
   - Enable **Anonymous Authentication**, **Cloud Firestore**, and **Firebase Storage**.
4. **Sync & Run**:
   Sync with Gradle and run the app on an emulator or physical device.

## 🖼 Screenshots

*(Add your screenshots here later)*

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---
Developed by **K Chandrakanth** 🚀
