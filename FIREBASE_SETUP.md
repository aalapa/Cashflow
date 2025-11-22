# Firebase Setup Instructions

## 🔥 Setting Up Firebase for Multi-User Sync

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "CashFlow" (or your choice)
4. Disable Google Analytics (optional)
5. Click "Create project"

### Step 2: Add Android App to Firebase

1. In Firebase Console, click the Android icon (or "Add app")
2. Enter package name: `com.cashflow.app`
3. Enter app nickname: "CashFlow" (optional)
4. Click "Register app"
5. **Download `google-services.json`**
6. Place it in: `app/` directory (same level as `build.gradle.kts`)

### Step 3: Enable Authentication

1. In Firebase Console, go to **Authentication**
2. Click "Get started"
3. Enable **Email/Password** provider:
   - Click "Email/Password"
   - Toggle "Enable"
   - Click "Save"

### Step 4: Set Up Firestore Database

1. In Firebase Console, go to **Firestore Database**
2. Click "Create database"
3. Choose **Start in test mode** (for development)
4. Select a location (choose closest to you)
5. Click "Enable"

### Step 5: Security Rules (Important!)

Go to **Firestore Database** → **Rules** and update:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own household data
    match /households/{householdId} {
      allow read, write: if request.auth != null && request.auth.uid == householdId;
      
      match /accounts/{accountId} {
        allow read, write: if request.auth != null && request.auth.uid == householdId;
      }
      
      match /income/{incomeId} {
        allow read, write: if request.auth != null && request.auth.uid == householdId;
      }
      
      match /bills/{billId} {
        allow read, write: if request.auth != null && request.auth.uid == householdId;
      }
      
      match /transactions/{transactionId} {
        allow read, write: if request.auth != null && request.auth.uid == householdId;
      }
    }
  }
}
```

### Step 6: Build the App

The app should now build successfully! The `google-services.json` file will be automatically processed by the Google Services plugin.

## 📱 How It Works

### Authentication Flow:
1. **First Launch**: User sees login screen
2. **Sign Up**: Creates account with email/password
3. **Sign In**: Existing users sign in
4. **Auto-Login**: If already signed in, skips auth screen

### Data Sync:
- **Local First**: Room database stores data locally (works offline)
- **Cloud Sync**: Firebase syncs data when online
- **Multi-User**: Each user has their own household (identified by user ID)
- **Real-Time**: Changes sync automatically across devices

### For Multiple Users Sharing:
Currently, each user has their own separate data. To share between users:

**Option A: Shared Household (Future Enhancement)**
- Create a "household" concept
- Multiple users can join same household
- All members see same data

**Option B: Export/Import (Current)**
- One user exports data
- Share JSON file
- Other user imports

## 🚀 Next Steps

1. Place `google-services.json` in `app/` directory
2. Build and run the app
3. Create an account
4. Start using the app!

## ⚠️ Important Notes

- **Test Mode**: Firestore starts in test mode (30 days free, then requires upgrade)
- **Production**: Update security rules before production use
- **Backup**: Always keep local backups via Export feature
- **Offline**: App works offline, syncs when online

