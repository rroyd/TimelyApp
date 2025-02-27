# Timely - A Service Booking App

A mobile application for booking services, built using **Java** in **Android Studio**, with **Firebase Realtime Database** integration for user authentication, data storage, and real-time updates.



## 🚀 Features
- User authentication with Firebase
- Service listing and filtering
- Booking services through an interactive UI
- Firebase Realtime Database integration for storing and retrieving data
- Dynamic UI with **TabLayout, ViewPager, and Dialogs**
- Multi-step service creation and booking
- Localization support for **Hebrew**

## 🛠️ Tech Stack
- **Language:** Java
- **Framework:** Android SDK
- **Database:** Firebase Realtime Database
- **UI Components:** Material Design, ConstraintLayout, LinearLayout, TextInputLayout, Spinner (Dropdown)

## 📸 Screenshots
Soon to come!

## 📂 Project Structure
```
ServiceBookingApp/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/ex2/
│   │   │   │   ├── activities/ (All Activities)
│   │   │   │   ├── fragments/ (UI Fragments)
│   │   │   │   ├── models/ (Service, User, etc.)
│   │   │   │   ├── adapters/ (RecyclerView & Spinner Adapters)
│   │   │   ├── res/
│   │   │   │   ├── layout/ (XML UI files)
│   │   │   │   ├── values/ (colors, strings, themes)
│   │   │   │   ├── drawable/ (icons, images)
│── README.md
│── build.gradle
│── AndroidManifest.xml
```

## 🔧 Installation & Setup
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-username/service-booking-app.git
   cd service-booking-app
   ```

2. **Open in Android Studio**

3. **Set up Firebase:**
   - Create a Firebase project.
   - Add the `google-services.json` file inside `app/`.
   - Enable Firebase Authentication and Realtime Database.

4. **Run the app:**
   ```sh
   ./gradlew build
   ```
   Or use the **Run** button in Android Studio.

## 🎯 Future Enhancements
- Add push notifications for booking confirmation
- Implement payment integration
- Improve UI/UX with animations

## 📝 License
This project is open-source and available under the **MIT License**.

---
**Made with ❤️ using Java and Firebase**

