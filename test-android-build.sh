#!/bin/bash

# Beyhive Alert - Android Build and Test Script
echo "🤖 Beyhive Alert Android Build & Test"
echo "====================================="
echo ""

# Check if we're in the right directory
if [ ! -f "android-app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the Beyhive Alert 2 root directory"
    exit 1
fi

# Navigate to Android app directory
cd android-app

echo "🔨 Building Android app..."
echo ""

# Clean and build the app
./gradlew clean
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Android app built successfully!"
    echo ""
    echo "📱 Next steps:"
    echo "1. Install the APK on your Android device:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "2. Or open in Android Studio and run on device"
    echo ""
    echo "3. Grant notification permission when prompted"
    echo ""
    echo "4. Check app logs for FCM token registration:"
    echo "   adb logcat | grep -i 'FCM token'"
    echo ""
    echo "5. Run notification test:"
    echo "   cd .. && ./test-notifications.sh"
    echo ""
else
    echo ""
    echo "❌ Android build failed!"
    echo "Check the error messages above and fix any issues."
    echo ""
fi





