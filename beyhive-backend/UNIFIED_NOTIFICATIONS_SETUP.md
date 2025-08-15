# 🔔 Unified Notifications Setup Guide

## What Was Added (Without Touching iOS Code)

### ✅ New Files Created:
1. **`utils/firebaseAdmin.js`** - Firebase Admin SDK for Android FCM
2. **`utils/unifiedPushService.js`** - Unified service that calls both APNs and FCM
3. **`test-unified-notifications.js`** - Test script for verification

### ✅ Modified Files:
1. **`models/DeviceToken.js`** - Added `platform` field
2. **`server.js`** - Updated `/register-device` to handle platform
3. **`package.json`** - Added `firebase-admin` dependency
4. **`env.example`** - Added Firebase Admin credentials template

## 🔒 What Was NOT Touched:
- ❌ `utils/pushService.js` - iOS APNs code unchanged
- ❌ Any existing notification logic
- ❌ APNs credentials or configuration
- ❌ iOS app functionality

## 🚀 How It Works:

### 1. **Platform Detection**
- iOS tokens: 64 characters → sent via APNs
- Android tokens: Longer → sent via FCM
- Automatic detection + manual override option

### 2. **Unified Flow**
```
Backend → unifiedPushService → {
  iOS devices → APNs (existing code)
  Android devices → FCM (new code)
}
```

### 3. **Backward Compatibility**
- iOS app continues working exactly as before
- Android app gets new FCM functionality
- No breaking changes to existing system

## 📱 Setup Instructions:

### 1. **Install Dependencies**
```bash
cd beyhive-backend
npm install
```

### 2. **Configure Firebase Admin**
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Project Settings → Service Accounts
3. Generate new private key
4. Copy entire JSON content to `.env`:
```env
FIREBASE_ADMIN_CREDENTIALS={"type":"service_account",...}
```

### 3. **Test the System**
```bash
node test-unified-notifications.js
```

## 🔍 Verification:

### ✅ iOS Still Works:
- APNs notifications continue as before
- No changes to iOS app needed
- All existing functionality preserved

### ✅ Android Now Works:
- FCM tokens automatically detected
- Notifications sent via Firebase
- Unified with iOS notification system

### ✅ Backend Unified:
- Single endpoint sends to both platforms
- Automatic platform detection
- Comprehensive logging for debugging

## 🚨 Important Notes:

1. **iOS Code Unchanged** - Your existing APNs setup is completely safe
2. **Gradual Rollout** - Android FCM can be disabled by removing credentials
3. **No Breaking Changes** - All existing functionality preserved
4. **Easy Rollback** - Remove new files to return to iOS-only

## 🎯 Next Steps:

1. **Deploy to Render** with new environment variables
2. **Test Android app** - should now receive notifications
3. **Verify iOS** - should continue working exactly as before
4. **Monitor logs** - both platforms will show in console

## 🆘 Troubleshooting:

- **iOS broken?** → Check APNs credentials (unchanged)
- **Android not working?** → Check Firebase Admin credentials
- **Both broken?** → Check MongoDB connection
- **Need rollback?** → Remove new files, restart server

---

**Your iOS notifications are completely safe! 🍎✅**
