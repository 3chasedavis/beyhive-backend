#!/bin/bash

echo "Testing Beyhive Backend Endpoints"
echo "=================================="

BASE_URL="https://beyhive-backend.onrender.com"

echo "1. Testing health endpoint..."
curl -s "$BASE_URL/api/health" | jq '.'

echo -e "\n2. Testing livestreams endpoint..."
curl -s "$BASE_URL/api/livestreams" | jq '.'

echo -e "\n3. Testing device token registration..."
curl -s -X POST "$BASE_URL/register-device" \
  -H "Content-Type: application/json" \
  -d '{"deviceToken":"test-token-123","username":"testuser","email":"test@example.com"}' | jq '.'

echo -e "\n4. Testing device tokens endpoint..."
curl -s "$BASE_URL/api/device-tokens" | jq '.'

echo -e "\nBackend test completed!" 