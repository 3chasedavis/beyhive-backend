# 🔔 Android Notification Testing Guide

## 🚀 Quick Notification Testing

Your app has Firebase messaging set up and ready for testing! Here are multiple ways to test notifications:

## 📱 **Method 1: Test Notification Permissions**

1. **Open the app** on your device/emulator
2. **Check if permission dialog appears** (Android 13+)
3. **Go to Notifications tab** (bee icon)
4. **Test the purchase flow** - this will unlock notification settings

## 🔥 **Method 2: Test Firebase Notifications**

### Step 1: Get FCM Token
```bash
# Monitor logs to get the FCM token
adb logcat | grep "New FCM token"
```

### Step 2: Send Test Notification
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Go to **Messaging** → **Send your first message**
4. Enter title: "Beyhive Alert Test"
5. Enter message: "This is a test notification!"
6. Click **Send test message**
7. Enter the FCM token from Step 1
8. Click **Test**

## 🧪 **Method 3: Test via ADB Commands**

### Test Notification Permission
```bash
# Launch app
adb shell am start -n com.beyhivealert.android/.MainActivity

# Check notification settings
adb shell dumpsys notification | grep -A 5 "Beyhive"
```

### Send Test Notification
```bash
# Send a test notification
adb shell am broadcast -a com.beyhivealert.android.TEST_NOTIFICATION \
    --es title "Test Notification" \
    --es message "This is a test notification from Beyhive Alert!"
```

## 🔍 **Method 4: Monitor Notification Logs**

```bash
# Monitor all notification-related logs
adb logcat | grep -E "(BillingManager|FirebaseMessaging|Notification)"

# Monitor FCM token registration
adb logcat | grep "New FCM token"

# Monitor notification permissions
adb logcat | grep "Notification permission"
```

## 📊 **Method 5: Check Notification Settings**

### Via ADB
```bash
# Check if notifications are enabled
adb shell settings get global notification_listeners

# Check app notification settings
adb shell dumpsys package com.beyhivealert.android | grep notification
```

### Via Device Settings
1. Go to **Settings** → **Apps** → **Beyhive Alert**
2. Tap **Notifications**
3. Ensure notifications are enabled
4. Check notification categories

## 🎯 **Expected Behavior**

### When Notifications Work:
1. ✅ Permission dialog appears (Android 13+)
2. ✅ FCM token is registered and logged
3. ✅ Notifications appear in status bar
4. ✅ Tapping notification opens the app
5. ✅ Notification settings can be toggled

### When Notifications Don't Work:
1. ❌ No permission dialog
2. ❌ "Billing unavailable" error
3. ❌ No FCM token in logs
4. ❌ Notifications don't appear

## 🚀 **Quick Test Commands**

```bash
# Run the notification test script
./test-notifications.sh

# Launch app and check logs
adb shell am start -n com.beyhivealert.android/.MainActivity && adb logcat | grep -E "(BillingManager|FirebaseMessaging|Notification)"

# Check if app is installed
adb shell pm list packages | grep beyhive

# Send test notification
adb shell am broadcast -a com.beyhivealert.android.TEST_NOTIFICATION --es title "Test" --es message "Hello!"
```

## 🔧 **Troubleshooting**

### Common Issues:

1. **No Permission Dialog**:
   - Check Android version (13+ required for permission dialog)
   - Go to Settings → Apps → Beyhive Alert → Notifications

2. **FCM Token Not Generated**:
   - Check Firebase configuration
   - Verify Google Services JSON file
   - Check network connection

3. **Notifications Not Appearing**:
   - Check notification permissions
   - Verify notification channel is created
   - Check if app is in background

4. **"Billing unavailable" Error**:
   - This is expected on emulator with old Google Play Services
   - Use real device or update emulator

## ✅ **Your App is Ready!**

The notification system is fully configured with:
- ✅ Firebase messaging integration
- ✅ Notification channel setup
- ✅ Permission handling
- ✅ Backend token registration
- ✅ Test notification capability

**Start testing now!** 🎉

