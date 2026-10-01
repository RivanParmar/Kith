<p align="center">
<img width="120" height="120" alt="Black and White Minimalist Simple Bold Creative Studio Logo" src="https://github.com/user-attachments/assets/5f464eae-4843-4033-aee1-846fe35b776a" />

</p>

<h1 align="center">Kith: The Peer-to-Peer Learning Economy</h1>

<p align="center">
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-2.0+-blue.svg?logo=kotlin" alt="Kotlin"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Compose-Ready-4285F4?logo=android" alt="Compose"></a>
  <a href="https://supabase.com/"><img src="https://img.shields.io/badge/Supabase-Powered-3ECF8E?logo=supabase" alt="Supabase"></a>
</p>

---

> **Reviewers:** Don't want to build from source? Download the [latest APK release here](https://github.com/RivanParmar/Kith/releases/latest).

## 🎯 What it does

AI gives you answers in seconds, but it doesn't teach you *how* to think. 

Kith replaces passive AI answer machines with an active, student-powered learning economy. By gamifying the tutoring process with a peer-to-peer XP currency, Kith transforms students from passive knowledge consumers into verified, active educators.

## 🔄 Core Workflow

Kith is driven by a self-sustaining peer economy. The core loop operates in 8 steps:

`Ask` ➔ `Spend XP` ➔ `Peer Accepts` ➔ `Solve` ➔ `Verify` ➔ `XP Transfer` ➔ `Rate` ➔ `Reputation`

1. **Ask:** Student posts a complex concept to their community.
2. **Spend XP:** Requester attaches a guaranteed XP bounty.
3. **Peer Accepts:** A knowledgeable student accepts the request.
4. **Solve:** The peer breaks down the solution (Text/Photo/PDF/Audio).
5. **Verify:** The original requester reviews the answer.
6. **XP Transfer:** The escrowed XP is transferred to the solver's wallet.
7. **Rate:** The requester rates the quality of the explanation.
8. **Reputation:** Top solvers climb the community leaderboard.

## ✨ Core Features

*   **The XP Economy:** P2P wallet system, local escrow, and verified XP ledger transfers.
*   **Offline-First Sync:** Room DB acts as a local source of truth, with background WorkManager queues handling offline posts and syncing.
*   **Verified Communities:** Secure join flows for specific university and study groups.
*   **Gamified Trust:** Public academic track records and dynamic community leaderboards.
*   **App Polish & Media:** Dynamic Colors (Material You), Dark Mode, and seamless local URI interception for Supabase uploads.
*   **Monetization & Security:** RevenueCat paywall integration and strict Supabase Auth with Row Level Security (RLS) policies.

## 🛠 Tech Stack & Requirements

### Compatibility
*   **Minimum SDK:** 26 (Android 8.0)
*   **Target SDK:** 34 (Android 14)
*   **Kotlin:** 2.0.0+
*   **Java Development Kit (JDK):** 17
*   **IDE:** Android Studio Koala Feature Drop (or newer)

### Technologies
*   **UI:** Jetpack Compose, Material 3, Dynamic Colors.
*   **Architecture:** Clean Architecture, MVVM, UDF (Unidirectional Data Flow).
*   **DI & Async:** Dagger Hilt, Kotlin Coroutines & Flows.
*   **Backend & DB:** Supabase (Postgres, Auth, Storage), Room Database.
*   **Monetization:** RevenueCat.

## 🏗️ Architecture

Kith follows Clean Architecture principles to separate concerns, helping keep responsibilities separated and the codebase maintainable as the project grows.

```text
                    ┌─────────────────┐
                    │  Jetpack        │
                    │  Compose UI     │
                    └────────┬────────┘
                             │
                       StateFlow
                             │
                    ┌────────▼────────┐
                    │   ViewModel     │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │    Domain       │
                    │   Use Cases     │
                    └────────┬────────┘
                             │
                       Repository
                      /          \
                     ↓            ↓
                  Room DB      Supabase
                    ↑
                    │
               WorkManager
               Sync Queue
```

## 📂 Project Structure
The codebase is organized into a modular Android structure to enforce boundary separation:
```
├── .idea
├── app
├── build-logic
├── core
│   ├── common
│   ├── data
│   ├── database
│   ├── datastore-proto
│   ├── datastore
│   ├── designsystem
│   ├── domain
│   ├── model
│   ├── navigation
│   ├── network
│   ├── notifications
│   ├── push
│   └── ui
├── feature
│   ├── auth
│   ├── browse
│   ├── community
│   ├── home
│   ├── leaderboard
│   ├── onboarding
│   ├── paywall
│   ├── post
│   └── profile
├── gradle
├── sync
├── .gitignore
├── LICENSE
├── README.md
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

## ⚙️ Configuration & 🚀 Running the Project
To build and run Kith locally, follow these steps:

1. **Clone the repository:**
```bash
git clone https://github.com/RivanParmar/Kith.git
cd Kith
```

2. **Open in Android Studio** (Koala or newer).

3. **Configure local secrets.** Create a `local.properties` file in the project root:
```properties
sdk.dir=...
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_your_key_here
```
   > ⚠️ Never commit this file to version control.

4. **Sync Gradle:** Click "Sync Project with Gradle Files".

5. **Run:** Select the `debug` build variant and press Shift + F10.

