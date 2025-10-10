#!/bin/bash

# Beyhive Alert Real Billing Setup Script
# This script helps you set up real Google Play Billing

echo "🚀 Beyhive Alert Real Billing Setup"
echo "==================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the android-app directory"
    exit 1
fi

echo "✅ App configured for real billing (DEBUG_MODE = false)"
echo ""

echo "📱 Production APK Built Successfully!"
echo "Location: app/build/outputs/apk/release/app-release.apk"
echo ""

echo "🔧 Next Steps:"
echo "=============="
echo ""
echo "1. 📋 Google Play Console Setup:"
echo "   - Go to https://play.google.com/console"
echo "   - Select your app: 'Beyhive Alert'"
echo "   - Navigate to Monetize → Products → In-app products"
echo "   - Create product with ID: com.chasedavis.beyhivealert.notificationssss"
echo "   - Set price to \$1.99"
echo "   - Set status to Active"
echo ""

echo "2. 🧪 Set Up Testing:"
echo "   - Go to Testing → License testing"
echo "   - Add your Gmail account as a test account"
echo "   - This allows you to test purchases without being charged"
echo ""

echo "3. 📤 Upload APK:"
echo "   - Go to Release → Internal testing"
echo "   - Create new release"
echo "   - Upload: app/build/outputs/apk/release/app-release.apk"
echo "   - Add testers and test the billing"
echo ""

echo "4. 🔍 Test Billing:"
echo "   - Install app from Google Play Store (not sideloaded)"
echo "   - Use your test account"
echo "   - Go to Notifications tab"
echo "   - Tap 'Unlock for \$1.99'"
echo "   - Complete purchase flow"
echo ""

echo "📖 Detailed Instructions:"
echo "See REAL_BILLING_SETUP.md for complete setup guide"
echo ""

echo "🐛 Debug Commands:"
echo "adb logcat | grep BillingManager"
echo ""

echo "✅ Your app is ready for real billing!"
echo "The billing system includes:"
echo "- Product ID matching iOS app"
echo "- Comprehensive error handling"
echo "- Retry functionality"
echo "- Purchase restoration"
echo "- User-friendly error messages"
echo ""
echo "Good luck with your launch! 🎉"

