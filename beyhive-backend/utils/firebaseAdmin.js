// Firebase Admin SDK service for Android FCM notifications
// This is ADDITIONAL to existing APNs - does NOT replace it
const admin = require('firebase-admin');

// Initialize Firebase Admin if credentials are available
let firebaseAdmin = null;
if (process.env.FIREBASE_ADMIN_CREDENTIALS) {
  try {
    // Use service account credentials from environment
    const serviceAccount = JSON.parse(process.env.FIREBASE_ADMIN_CREDENTIALS);
    
    firebaseAdmin = admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      projectId: process.env.FIREBASE_PROJECT_ID
    });
    
    console.log('✅ Firebase Admin initialized successfully for FCM');
  } catch (error) {
    console.warn('⚠️ Firebase Admin initialization failed:', error.message);
    firebaseAdmin = null;
  }
} else {
  console.warn('⚠️ Firebase Admin credentials not found - Android FCM will be disabled');
}

// Send FCM notification to Android devices
async function sendFCMNotification(title, messageBody, notifType, deviceTokens) {
  if (!firebaseAdmin) {
    console.warn('⚠️ Firebase Admin not available - skipping FCM notification');
    return { sent: [], failed: [] };
  }

  if (!Array.isArray(deviceTokens)) {
    deviceTokens = [deviceTokens];
  }

  const fcmMessage = {
    notification: {
      title: title,
      body: messageBody
    },
    data: {
      notifType: notifType,
      click_action: 'FLUTTER_NOTIFICATION_CLICK'
    },
    android: {
      priority: 'high',
      notification: {
        sound: 'default',
        priority: 'high',
        channel_id: 'beyhive_alerts'
      }
    }
  };

  try {
    const results = await Promise.allSettled(
      deviceTokens.map(token => 
        admin.messaging().send({
          ...fcmMessage,
          token: token
        })
      )
    );

    const sent = [];
    const failed = [];

    results.forEach((result, index) => {
      if (result.status === 'fulfilled') {
        sent.push(deviceTokens[index]);
      } else {
        failed.push(deviceTokens[index]);
        console.error(`FCM failed for token ${deviceTokens[index]}:`, result.reason);
      }
    });

    console.log(`FCM results: ${sent.length} sent, ${failed.length} failed`);
    return { sent, failed };
  } catch (error) {
    console.error('FCM error:', error);
    throw error;
  }
}

module.exports = { sendFCMNotification };
