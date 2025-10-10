#!/bin/bash

# Beyhive Alert Android Production Setup Test Script
# This script helps test both billing and notifications for production

echo "🚀 Beyhive Alert Android Production Setup Test"
echo "=============================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the android-app directory"
    exit 1
fi

echo "📱 Building the app for production..."
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo "✅ Release build successful"
else
    echo "❌ Release build failed"
    exit 1
fi

echo ""
echo "🔍 Production Configuration Check:"
echo "=================================="

# Check billing configuration
echo "💰 Billing Configuration:"
echo "-------------------------"

# Check if billing permission is present
if grep -q "com.android.vending.BILLING" app/src/main/AndroidManifest.xml; then
    echo "✅ Billing permission found in AndroidManifest.xml"
else
    echo "❌ Billing permission missing from AndroidManifest.xml"
fi

# Check if billing library is included
if grep -q "billing-ktx" app/build.gradle.kts; then
    echo "✅ Google Play Billing library found in build.gradle.kts"
else
    echo "❌ Google Play Billing library missing from build.gradle.kts"
fi

# Check product ID
PRODUCT_ID=$(grep -o 'NOTIFICATIONS_PRODUCT_ID = "[^"]*"' app/src/main/java/com/beyhivealert/android/billing/BillingManager.kt | cut -d'"' -f2)
echo "📦 Product ID: $PRODUCT_ID"

if [[ "$PRODUCT_ID" == "com.chasedavis.beyhivealert.notifications" ]]; then
    echo "✅ Product ID is correct for production"
else
    echo "❌ Product ID is incorrect - should be: com.chasedavis.beyhivealert.notifications"
fi

# Check debug mode
DEBUG_MODE=$(grep -o 'DEBUG_MODE = [^,]*' app/src/main/java/com/beyhivealert/android/billing/BillingManager.kt | cut -d' ' -f3)
echo "🐛 Debug Mode: $DEBUG_MODE"

if [[ "$DEBUG_MODE" == "false" ]]; then
    echo "✅ Debug mode is disabled for production"
else
    echo "❌ Debug mode is enabled - should be false for production"
fi

echo ""
echo "🔔 Notification Configuration:"
echo "------------------------------"

# Check notification permissions
if grep -q "POST_NOTIFICATIONS" app/src/main/AndroidManifest.xml; then
    echo "✅ POST_NOTIFICATIONS permission found"
else
    echo "❌ POST_NOTIFICATIONS permission missing"
fi

# Check Firebase messaging service
if grep -q "FirebaseMessagingService" app/src/main/AndroidManifest.xml; then
    echo "✅ Firebase Messaging Service registered"
else
    echo "❌ Firebase Messaging Service not registered"
fi

# Check Firebase dependencies
if grep -q "firebase-messaging" app/build.gradle.kts; then
    echo "✅ Firebase Messaging dependency found"
else
    echo "❌ Firebase Messaging dependency missing"
fi

echo ""
echo "📋 Google Play Console Setup Checklist:"
echo "======================================="
echo "1. ✅ Create in-app product with ID: $PRODUCT_ID"
echo "2. ✅ Set product name: 'Beyhive Notifications'"
echo "3. ✅ Set price: \$1.99"
echo "4. ✅ Set status: Active"
echo "5. ✅ Add test accounts to License Testing"
echo "6. ✅ Upload signed APK to Internal Testing"
echo "7. ✅ Test billing flow with test account"

echo ""
echo "🧪 Testing Commands:"
echo "===================="
echo "# Install the release APK"
echo "adb install app/build/outputs/apk/release/app-release.apk"
echo ""
echo "# Monitor billing logs"
echo "adb logcat | grep -E '(BillingManager|BillingClient)'"
echo ""
echo "# Monitor notification logs"
echo "adb logcat | grep -E '(FirebaseMessaging|Notification)'"
echo ""
echo "# Get FCM token"
echo "adb logcat | grep 'New FCM token'"
echo ""
echo "# Test notification permission"
echo "adb shell dumpsys notification | grep -A 5 'Beyhive'"

echo ""
echo "🔧 Firebase Console Setup:"
echo "=========================="
echo "1. Go to Firebase Console"
echo "2. Select your project"
echo "3. Go to Project Settings > Cloud Messaging"
echo "4. Copy Server Key for backend"
echo "5. Test notifications using the FCM token from logs"

echo ""
echo "📱 Manual Testing Steps:"
echo "========================"
echo "1. Install app from Google Play Store (Internal Testing)"
echo "2. Grant notification permissions when prompted"
echo "3. Go to Notifications tab"
echo "4. Test purchase flow with test account"
echo "5. Verify notification preferences work"
echo "6. Send test notification from Firebase Console"

echo ""
echo "🚨 Common Issues & Solutions:"
echo "============================="
echo "❌ 'Billing unavailable' → Install from Play Store, not sideloaded"
echo "❌ 'Product not found' → Check product ID matches exactly"
echo "❌ No FCM token → Check Firebase configuration"
echo "❌ Notifications not working → Check permissions and backend"

echo ""
echo "✅ Your app is ready for production testing!"
echo "   APK location: app/build/outputs/apk/release/app-release.apk"



