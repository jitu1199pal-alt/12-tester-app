# TestCircle – Android App Testing Community

TestCircle is a native Android application built with **Kotlin** and **Jetpack Compose** designed to help Android developers complete Google Play's 14/15-day closed testing requirements with genuine, active community testers.

---

## 🚀 Key Features

1. **Free Testing Dashboard**: Peer-to-peer testing queue. Developers test each other's apps to receive test coverage.
2. **Pro Testing Dashboard (₹699)**: Guaranteed dedicated group of 13 testers for 15 consecutive days.
3. **Manual UPI Payment Verification**:
   - Zero gateway fees.
   - User pays ₹699 to official UPI (`jitu1199pal@okaxis`) and submits their 12-digit UTR transaction reference.
   - Status changes to `PAYMENT_PENDING_VERIFICATION`.
   - Authorized Admin (`jitu1199pal@gmail.com`) verifies funds in their merchant bank before approving and issuing a unique token.
4. **Tester Recruitment**:
   - Device model, Android OS version, and 15-day commitment verification.
   - Privacy-first (only City/State collected, no residential address).
5. **Campaign Management (15 Days)**:
   - Tracks day progression from Day 1/15 to Day 15/15.
   - 13-tester group assembly with manual Google Group URL linking.
6. **Daily Reports & Anti-Fraud XP System**:
   - Verified daily test feedback & bug reporting.
   - **Anti-Fraud**: 1 XP awarded ONLY after verified submission; max 1 XP per tester, per campaign, per day; recorded in an immutable ledger.
7. **Secure Admin Panel**:
   - Overview metrics, app requests, payment reviews, tester approvals, and 13-tester assignment.
   - Zero-trust security enforced by email checks and Firestore rules.

---

## 🛠 Tech Stack

- **Platform**: Native Android (API 24 to 36)
- **Language**: Kotlin 2.2
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM + Repository Pattern
- **State**: Kotlin Coroutines & StateFlow
- **Backend / DB**: Firebase Authentication & Cloud Firestore
- **CI/CD**: GitHub Actions workflow for automatic Debug APK artifact generation

---

## 📲 Step-by-Step Setup Guide

### 1. Open Project in Android Studio
1. Clone or download the repository.
2. Open Android Studio (Ladybug / Koala or newer with JDK 17+).
3. Let Gradle sync project dependencies.

### 2. Firebase Configuration
1. Create a Firebase project at [https://console.firebase.google.com](https://console.firebase.google.com).
2. Add an Android app with package: `com.aistudio.testcircle.wvxkpq`.
3. Download `google-services.json` and place it in the `app/` folder.
4. Enable **Authentication** (Google Sign-In).
5. Enable **Cloud Firestore** and deploy `firestore.rules`.

### 3. Build Debug APK Locally
```bash
./gradlew assembleDebug
```
The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🛡️ Security Architecture & Free-Tier Rules

- **Zero-Trust**: Admin rights (`jitu1199pal@gmail.com`) are evaluated on every privileged operation.
- **Payment Integrity**: Users cannot approve their own payments or promote themselves to Admin.
- **Ledger Immutability**: XP transactions cannot be updated or deleted.
- **Free-Tier Compatibility**: Runs 100% on Firebase Free Spark Tier (no paid extensions or gateways required).
