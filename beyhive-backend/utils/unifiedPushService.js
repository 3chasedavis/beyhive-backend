// Unified push notification service that sends to both iOS (APNs) and Android (FCM)
// This is ADDITIONAL to existing services - does NOT replace them
const { sendPushNotification } = require('./pushService'); // Existing APNs service
const { sendFCMNotification } = require('./firebaseAdmin'); // New FCM service

// Send notifications to both platforms based on device tokens
async function sendUnifiedNotification(title, message, notifType, deviceTokens) {
  console.log(`📱 Sending unified notification: "${title}" to ${deviceTokens.length} devices`);
  
  // Separate tokens by platform (iOS tokens are typically 64 chars, FCM tokens are longer)
  const iosTokens = [];
  const androidTokens = [];
  
  deviceTokens.forEach(token => {
    // iOS APNs tokens are typically 64 characters, FCM tokens are longer
    if (token.length === 64) {
      iosTokens.push(token);
    } else {
      androidTokens.push(token);
    }
  });
  
  console.log(`🍎 iOS tokens: ${iosTokens.length}, 🤖 Android tokens: ${androidTokens.length}`);
  
  const results = {
    ios: { sent: [], failed: [] },
    android: { sent: [], failed: [] }
  };
  
  // Send to iOS devices using existing APNs service
  if (iosTokens.length > 0) {
    try {
      console.log('🍎 Sending to iOS devices via APNs...');
      const apnsResult = await sendPushNotification(title, message, notifType, iosTokens);
      results.ios = apnsResult;
      console.log(`🍎 APNs completed: ${apnsResult.sent.length} sent, ${apnsResult.failed.length} failed`);
    } catch (error) {
      console.error('🍎 APNs error:', error);
      results.ios.failed = iosTokens;
    }
  }
  
  // Send to Android devices using new FCM service
  if (androidTokens.length > 0) {
    try {
      console.log('🤖 Sending to Android devices via FCM...');
      const fcmResult = await sendFCMNotification(title, message, notifType, androidTokens);
      results.android = fcmResult;
      console.log(`🤖 FCM completed: ${fcmResult.sent.length} sent, ${fcmResult.failed.length} failed`);
    } catch (error) {
      console.error('🤖 FCM error:', error);
      results.android.failed = androidTokens;
    }
  }
  
  // Calculate totals
  const totalSent = results.ios.sent.length + results.android.sent.length;
  const totalFailed = results.ios.failed.length + results.android.failed.length;
  
  console.log(`📱 Unified notification completed: ${totalSent} total sent, ${totalFailed} total failed`);
  
  return results;
}

module.exports = { sendUnifiedNotification };
