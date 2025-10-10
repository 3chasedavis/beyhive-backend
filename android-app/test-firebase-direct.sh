#!/bin/bash

# Direct Firebase API Test Script
echo "🔥 Testing Firebase API Directly"
echo "================================="

# You'll need to get your Firebase Server Key from the Firebase Console
# Go to Project Settings > Cloud Messaging > Server Key
echo "⚠️  You need to get your Firebase Server Key first:"
echo "1. Go to Firebase Console"
echo "2. Project Settings > Cloud Messaging"
echo "3. Copy the Server Key"
echo ""

read -p "Enter your Firebase Server Key: " SERVER_KEY

if [ -z "$SERVER_KEY" ]; then
    echo "❌ No server key provided. Exiting."
    exit 1
fi

# FCM token from the logs
FCM_TOKEN="d9X58LXPQqS57mrMXbbgWu:APA91bFZzipFtod1k0uqRHRyWf8jXBAROskU1bKWXp-P2n6_TEl95_PUmfVx9knp9-z3-TMw6ro4EUBWFU2yvO4lMRCUPYlSjRbWRMOtccXB5Sc1QXiI1rs"

echo "📱 FCM Token: $FCM_TOKEN"
echo ""

# Send notification via Firebase API
echo "📤 Sending notification via Firebase API..."

curl -X POST "https://fcm.googleapis.com/fcm/send" \
  -H "Authorization: key=$SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d "{
    \"to\": \"$FCM_TOKEN\",
    \"notification\": {
      \"title\": \"Beyhive Alert Test\",
      \"body\": \"This is a test notification from Firebase API!\",
      \"icon\": \"bee_icon\"
    },
    \"data\": {
      \"type\": \"test\",
      \"source\": \"firebase_api\"
    }
  }"

echo ""
echo ""
echo "✅ Test notification sent via Firebase API!"
echo "Check your device for the notification"



