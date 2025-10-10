#!/bin/bash

# Beyhive Alert Android Billing Test Script
# This script helps test the billing functionality

echo "🧪 Beyhive Alert Android Billing Test"
echo "====================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the android-app directory"
    exit 1
fi

echo "📱 Building the app..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "🔍 Billing Configuration Check:"
echo "==============================="

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

if [[ "$PRODUCT_ID" == *"notificationssss"* ]]; then
    echo "✅ Product ID matches iOS app"
else
    echo "❌ Product ID mismatch with iOS app"
fi

echo ""
echo "🚀 Next Steps:"
echo "=============="
echo "1. Install the app on a device with Google Play Services"
echo "2. Go to Google Play Console and create the in-app product:"
echo "   Product ID: $PRODUCT_ID"
echo "   Name: Beyhive Notifications"
echo "   Price: \$1.99"
echo "3. Add your test account to License Testing"
echo "4. Test the purchase flow in the Notifications screen"
echo ""
echo "📋 Test Scenarios:"
echo "- Successful purchase"
echo "- User cancellation"
echo "- Network error (airplane mode)"
echo "- Already owned item"
echo ""
echo "🔧 Debug Commands:"
echo "adb logcat | grep BillingManager"
echo ""
echo "📖 See BILLING_SETUP.md for detailed instructions"

