// Test script for unified notifications (iOS + Android)
// This is for testing only - does NOT affect production
require('dotenv').config();

const { sendUnifiedNotification } = require('./utils/unifiedPushService');

async function testUnifiedNotifications() {
  console.log('🧪 Testing unified notification system...\n');
  
  // Test tokens (replace with real ones from your database)
  const testTokens = [
    // iOS token (64 characters)
    '1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef',
    // Android FCM token (longer)
    'fMEP0JqHqX0:APA91bHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0fMEP0JqHqX0'
  ];
  
  try {
    console.log('📱 Sending test notification to both platforms...');
    const result = await sendUnifiedNotification(
      '🧪 Test Notification',
      'This is a test of the unified notification system!',
      'test',
      testTokens
    );
    
    console.log('\n✅ Test completed successfully!');
    console.log('📊 Results:', JSON.stringify(result, null, 2));
    
  } catch (error) {
    console.error('\n❌ Test failed:', error.message);
  }
}

// Only run if called directly
if (require.main === module) {
  testUnifiedNotifications();
}

module.exports = { testUnifiedNotifications };
