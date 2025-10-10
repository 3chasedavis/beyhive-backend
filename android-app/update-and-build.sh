#!/bin/bash

echo "🧹 Cleaning Android project..."
./gradlew clean

echo "📱 Building Android project..."
./gradlew build

echo "✅ Build completed! Check for any errors above."
echo ""
echo "📋 Summary of updates made:"
echo "• Target SDK updated to 35 (required by Google Play)"
echo "• Compile SDK updated to 35"
echo "• Google Play Billing Library updated to 6.2.1"
echo "• Android Gradle Plugin updated to 8.4.0"
echo "• Kotlin updated to 1.9.22"
echo "• Gradle updated to 8.6"
echo "• Compose BOM updated to 2024.02.00"
echo "• Compose compiler updated to 1.5.8"
echo ""
echo "🚀 Your app should now meet Google Play's August 31, 2025 requirements!"






