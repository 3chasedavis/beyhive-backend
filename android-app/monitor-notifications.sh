#!/bin/bash

# Monitor Notifications Script
echo "👀 Monitoring Notifications"
echo "============================"
echo "This will show all notification-related logs in real-time"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor for notification logs
adb logcat | grep -E "(FirebaseMessaging|Notification|BillingManager|FCM token|New FCM token)"



