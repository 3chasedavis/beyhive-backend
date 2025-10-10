#!/bin/bash

# Beyhive Alert - Notification Testing Script
echo "🧪 Beyhive Alert Notification Testing"
echo "====================================="
echo ""

# Check if we're in the right directory
if [ ! -f "beyhive-backend/package.json" ]; then
    echo "❌ Please run this script from the Beyhive Alert 2 root directory"
    exit 1
fi

# Navigate to backend directory
cd beyhive-backend

echo "📱 Testing notification system..."
echo ""

# Run the comprehensive notification test
node test-notifications-comprehensive.js

echo ""
echo "✅ Notification testing completed!"
echo ""
echo "📋 Next steps:"
echo "1. Check your Android device for test notifications"
echo "2. If no notifications appear, check:"
echo "   - Device has notification permission enabled"
echo "   - App is not in battery optimization mode"
echo "   - FCM token was registered in backend logs"
echo "3. For production, make sure Google Play Console billing is set up"
echo ""





