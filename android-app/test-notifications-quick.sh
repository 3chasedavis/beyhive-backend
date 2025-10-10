#!/bin/bash

# Quick Notification Test Script
echo "🔔 Beyhive Alert Notification Test"
echo "=================================="

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    echo "Please connect your device and enable USB debugging"
    exit 1
fi

echo "✅ Android device connected"

# Install the app
echo "📱 Installing app..."
adb install -r app/build/outputs/apk/release/app-release.apk

if [ $? -eq 0 ]; then
    echo "✅ App installed successfully"
else
    echo "❌ App installation failed"
    exit 1
fi

# Launch the app
echo "🚀 Launching app..."
adb shell am start -n com.beyhivealert.android/.MainActivity

# Wait a moment for the app to start
sleep 3

echo ""
echo "🔍 Monitoring logs for FCM token..."
echo "Look for: 'New FCM token: [TOKEN]'"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor for FCM token
adb logcat | grep -E "(New FCM token|FirebaseMessaging|Notification|BillingManager)"



