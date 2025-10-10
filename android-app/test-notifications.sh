#!/bin/bash

# Beyhive Alert Notification Testing Script
# This script helps you test Android notifications

echo "🔔 Beyhive Alert Notification Testing"
echo "====================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the android-app directory"
    exit 1
fi

echo "📱 Testing Android Notifications"
echo "==============================="

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected. Please connect a device or start an emulator."
    exit 1
fi

echo "✅ Android device connected"

# Check if app is installed
if ! adb shell pm list packages | grep -q "com.beyhivealert.android"; then
    echo "❌ Beyhive Alert app not installed. Installing now..."
    ./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
else
    echo "✅ Beyhive Alert app is installed"
fi

echo ""
echo "🔔 Notification Testing Options:"
echo "==============================="
echo ""
echo "1. 📱 Test Local Notifications (Immediate)"
echo "2. 🔥 Test Firebase Notifications (Requires backend)"
echo "3. 🧪 Test Notification Permissions"
echo "4. 📊 Check Notification Settings"
echo "5. 🚀 Send Test Notification via ADB"
echo ""

read -p "Choose an option (1-5): " choice

case $choice in
    1)
        echo "📱 Testing Local Notifications..."
        adb shell am start -n com.beyhivealert.android/.MainActivity
        echo "✅ App launched. Check if notification permission dialog appears."
        echo "💡 If no dialog appears, go to Settings > Apps > Beyhive Alert > Notifications"
        ;;
    2)
        echo "🔥 Testing Firebase Notifications..."
        echo "This requires the backend to be running and configured."
        echo "The app will automatically register for notifications when opened."
        adb shell am start -n com.beyhivealert.android/.MainActivity
        echo "✅ App launched. Check logs for FCM token registration."
        echo "📋 FCM Token will be logged. Use this to send test notifications from Firebase Console."
        ;;
    3)
        echo "🧪 Testing Notification Permissions..."
        adb shell am start -n com.beyhivealert.android/.MainActivity
        echo "✅ App launched. Check if notification permission is granted."
        echo "📱 Go to Notifications tab to test the purchase flow."
        ;;
    4)
        echo "📊 Checking Notification Settings..."
        adb shell settings get global notification_listeners
        adb shell dumpsys notification | grep -A 5 "Beyhive"
        echo "✅ Notification settings checked"
        ;;
    5)
        echo "🚀 Sending Test Notification via ADB..."
        adb shell am broadcast -a com.beyhivealert.android.TEST_NOTIFICATION \
            --es title "Test Notification" \
            --es message "This is a test notification from Beyhive Alert!"
        echo "✅ Test notification sent"
        ;;
    *)
        echo "❌ Invalid option. Please choose 1-5."
        exit 1
        ;;
esac

echo ""
echo "🔍 Debug Commands:"
echo "=================="
echo "Monitor logs: adb logcat | grep -E '(BillingManager|FirebaseMessaging|Notification)'"
echo "Check permissions: adb shell dumpsys package com.beyhivealert.android | grep permission"
echo "Test notification: adb shell am broadcast -a com.beyhivealert.android.TEST_NOTIFICATION"
echo ""
echo "📖 For Firebase testing, use the Firebase Console to send test notifications."
echo "🎯 The app is ready for notification testing!"

