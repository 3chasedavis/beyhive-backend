#!/bin/bash

# Test Backend Notification Script
echo "🔔 Testing Backend Notification"
echo "==============================="

# FCM token from the logs
FCM_TOKEN="d9X58LXPQqS57mrMXbbgWu:APA91bFZzipFtod1k0uqRHRyWf8jXBAROskU1bKWXp-P2n6_TEl95_PUmfVx9knp9-z3-TMw6ro4EUBWFU2yvO4lMRCUPYlSjRbWRMOtccXB5Sc1QXiI1rs"

echo "📱 FCM Token: $FCM_TOKEN"
echo ""

# Test notification data
TITLE="Beyhive Alert Test"
MESSAGE="This is a test notification from the backend!"
DATA='{"type":"test","source":"backend"}'

echo "📤 Sending test notification..."
echo "Title: $TITLE"
echo "Message: $MESSAGE"
echo ""

# Send notification via backend API
curl -X POST "https://beyhive-backend.onrender.com/send-notification" \
  -H "Content-Type: application/json" \
  -d "{
    \"deviceToken\": \"$FCM_TOKEN\",
    \"title\": \"$TITLE\",
    \"message\": \"$MESSAGE\",
    \"data\": $DATA
  }"

echo ""
echo ""
echo "✅ Test notification sent!"
echo "Check your device for the notification"



