# Firebase Setup Guide (FREE Alternative to Cloudinary)

## Why Firebase?
- ✅ **Completely FREE** (vs $89/month for Cloudinary)
- ✅ **5GB free storage** (plenty for outfit images)
- ✅ **1GB/day free downloads**
- ✅ **Google's infrastructure** (very reliable)
- ✅ **No browser freezing issues**
- ✅ **Simple setup**

## Setup Steps (5 minutes)

### 1. Create Firebase Project
1. Go to https://console.firebase.google.com/
2. Click "Create a project"
3. Name it: `beyhive-alert`
4. Click "Continue" (skip Google Analytics)
5. Click "Create project"

### 2. Enable Storage
1. In your Firebase project, click "Storage" in the left sidebar
2. Click "Get started"
3. Choose "Start in test mode" (for now)
4. Click "Done"

### 3. Get Your Config
1. Click the gear icon (⚙️) next to "Project Overview"
2. Click "Project settings"
3. Scroll down to "Your apps" section
4. Click the web icon (</>)
5. Name it: `Beyhive Alert Web`
6. Click "Register app"
7. Copy the config object (looks like this):

```javascript
const firebaseConfig = {
  apiKey: "AIzaSyC...",
  authDomain: "beyhive-alert.firebaseapp.com",
  projectId: "beyhive-alert",
  storageBucket: "beyhive-alert.appspot.com",
  messagingSenderId: "123456789",
  appId: "1:123456789:web:abcdef123456"
};
```

### 4. Add to Environment
Add these to your `.env` file:
```
FIREBASE_API_KEY=AIzaSyC...
FIREBASE_AUTH_DOMAIN=beyhive-alert.firebaseapp.com
FIREBASE_PROJECT_ID=beyhive-alert
FIREBASE_STORAGE_BUCKET=beyhive-alert.appspot.com
FIREBASE_MESSAGING_SENDER_ID=123456789
FIREBASE_APP_ID=1:123456789:web:abcdef123456
```

### 5. Install Dependencies
```bash
npm install firebase
```

### 6. Deploy
Your app will now use Firebase for free image hosting!

## Benefits
- ✅ **FREE forever** (5GB storage)
- ✅ **No credit card required**
- ✅ **Google's infrastructure**
- ✅ **Fast global CDN**
- ✅ **Simple setup**

## Migration Notes
- Existing Cloudinary images will continue to work
- New uploads will go to Firebase
- No changes needed in your iOS app 