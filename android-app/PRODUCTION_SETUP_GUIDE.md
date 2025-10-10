# 🚀 Beyhive Alert Android Production Setup Guide

## 🎉 Congratulations! Your app is ready for production!

This guide will help you set up both billing and push notifications for production on the Google Play Console.

## 📋 Pre-Setup Checklist

- [x] ✅ Billing product ID fixed: `com.chasedavis.beyhivealert.notifications`
- [x] ✅ Debug mode disabled for production
- [x] ✅ Firebase messaging service configured
- [x] ✅ Notification permissions handled for Android 13+
- [x] ✅ Release signing configuration ready

## 🔧 Step 1: Google Play Console Setup

### 1.1 Create In-App Product

1. **Go to Google Play Console**
   - Visit: https://play.google.com/console
   - Select your app: "Beyhive Alert"

2. **Navigate to Monetization**
   - Go to **Monetize** → **Products** → **In-app products**
   - Click **Create product**

3. **Configure Product Details**
   ```
   Product ID: com.chasedavis.beyhivealert.notifications
   Name: Beyhive Notifications
   Description: Unlock real-time tour notifications for Beyoncé's events
   Price: $1.99 (or your preferred price)
   Status: Active
   ```

4. **Save and Activate**
   - Click **Save**
   - Set status to **Active**

### 1.2 Set Up Testing

1. **Go to Testing**
   - Navigate to **Testing** → **License testing**
   - Click **Add testers**

2. **Add Test Accounts**
   - Add your Gmail addresses
   - These accounts can test purchases without being charged
   - Example: `your-email@gmail.com`

## 🔧 Step 2: Build and Upload APK

### 2.1 Build Release APK

```bash
cd android-app
./gradlew assembleRelease
```

The signed APK will be created at: `app/build/outputs/apk/release/app-release.apk`

### 2.2 Upload to Google Play Console

1. **Go to Release Management**
   - Navigate to **Release** → **Production** (or **Internal testing** first)

2. **Create New Release**
   - Click **Create new release**
   - Upload your APK: `app-release.apk`
   - Add release notes:
     ```
     - Fixed billing product ID
     - Improved notification handling
     - Enhanced Firebase messaging
     - Ready for production
     ```

3. **Review and Release**
   - Click **Review release**
   - Complete the release process

## 🔧 Step 3: Test Billing

### 3.1 Install from Play Store

1. **Install App**
   - Install the app from Google Play Store (not sideloaded)
   - Use a test account from License Testing

2. **Test Purchase Flow**
   - Open the app
   - Go to **Notifications** tab (bee icon)
   - Tap **"Unlock for $1.99"**
   - Complete the purchase flow

3. **Verify Purchase**
   - Check that notifications content unlocks
   - Verify purchase is recorded in Google Play Console
   - Test "Restore Purchases" functionality

## 🔧 Step 4: Test Push Notifications

### 4.1 Firebase Console Setup

1. **Go to Firebase Console**
   - Visit: https://console.firebase.google.com
   - Select your project

2. **Get Server Key**
   - Go to **Project Settings** → **Cloud Messaging**
   - Copy the **Server Key** (needed for backend)

3. **Test Notifications**
   - Go to **Messaging** → **Send your first message**
   - Enter title: "Beyhive Alert Test"
   - Enter message: "This is a test notification!"
   - Click **Send test message**

### 4.2 Get FCM Token

1. **Install App and Check Logs**
   ```bash
   adb logcat | grep "New FCM token"
   ```

2. **Copy Token**
   - Look for: `New FCM token: [TOKEN]`
   - Copy the token

3. **Send Test Notification**
   - In Firebase Console, paste the token
   - Click **Test**
   - Verify notification appears on device

## 🧪 Step 5: Comprehensive Testing

### 5.1 Run Test Script

```bash
cd android-app
./test-production-setup.sh
```

### 5.2 Manual Testing Checklist

- [ ] **App Installation**
  - [ ] App installs from Play Store
  - [ ] App launches without crashes
  - [ ] All screens load properly

- [ ] **Notification Permissions**
  - [ ] Permission dialog appears (Android 13+)
  - [ ] Notifications can be enabled/disabled
  - [ ] Settings page works correctly

- [ ] **Billing Flow**
  - [ ] Purchase button appears
  - [ ] Purchase flow launches
  - [ ] Purchase completes successfully
  - [ ] Content unlocks after purchase
  - [ ] Restore purchases works

- [ ] **Push Notifications**
  - [ ] FCM token is generated
  - [ ] Token is sent to backend
  - [ ] Test notifications are received
  - [ ] Notification preferences work

## 🔍 Step 6: Monitoring and Debugging

### 6.1 Monitor Logs

```bash
# Monitor billing
adb logcat | grep -E "(BillingManager|BillingClient)"

# Monitor notifications
adb logcat | grep -E "(FirebaseMessaging|Notification)"

# Monitor FCM tokens
adb logcat | grep "New FCM token"
```

### 6.2 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Billing unavailable" | Install from Play Store, not sideloaded |
| "Product not found" | Check product ID matches exactly |
| No FCM token | Check Firebase configuration |
| Notifications not working | Check permissions and backend |
| Purchase fails | Check test account setup |

## 📊 Step 7: Production Monitoring

### 7.1 Google Play Console Analytics

- Monitor purchase success rates
- Check for billing errors
- Review user feedback
- Track app performance

### 7.2 Firebase Analytics

- Monitor notification delivery rates
- Track user engagement
- Check for crashes
- Monitor performance

## 🎯 Step 8: Go Live Checklist

- [ ] ✅ Product created in Google Play Console
- [ ] ✅ Release APK built and signed
- [ ] ✅ App uploaded to Play Console
- [ ] ✅ Internal testing completed
- [ ] ✅ Billing tested with real purchases
- [ ] ✅ Push notifications working
- [ ] ✅ Error handling verified
- [ ] ✅ Analytics monitoring set up

## 🚀 Your App is Production Ready!

Once you complete all the steps above, your Beyhive Alert Android app will be ready for production with:

- ✅ **Working Billing**: Users can purchase notifications for $1.99
- ✅ **Push Notifications**: Real-time tour updates and alerts
- ✅ **Permission Handling**: Proper Android 13+ notification permissions
- ✅ **Error Handling**: Robust error handling and user feedback
- ✅ **Production Signing**: Properly signed for Google Play Store

## 📞 Support

If you encounter any issues:

1. Check the logs using the commands above
2. Verify Google Play Console configuration
3. Test with different devices/accounts
4. Review Firebase Console for notification issues
5. Check backend logs for API errors

**Good luck with your production launch! 🎉**



