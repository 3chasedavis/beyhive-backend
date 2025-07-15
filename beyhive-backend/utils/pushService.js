// APNs push notification service using apn and .env variables
const apn = require('apn');
const path = require('path');

// Check if APN credentials are available
const hasAPNCredentials = process.env.APN_KEY_FILE && process.env.APN_KEY_ID && process.env.APN_TEAM_ID;

// APNs provider setup (sandbox) - only create if credentials are available
let apnProvider = null;
if (hasAPNCredentials) {
  try {
    apnProvider = new apn.Provider({
      token: {
        key: process.env.APN_KEY_FILE, // Use the environment variable directly
        keyId: process.env.APN_KEY_ID, // e.g., 8M48VV84A3
        teamId: process.env.APN_TEAM_ID, // e.g., A46AH86Z73
      },
      production: true, // false = sandbox, true = production
    });
    console.log('APN provider initialized successfully');
  } catch (error) {
    console.warn('APN provider initialization failed:', error.message);
    apnProvider = null;
  }
} else {
  console.warn('APN credentials not found - push notifications will be disabled');
}

// Send push notification to a device token
async function sendPushNotification(title, message, notifType, deviceTokens) {
  if (!apnProvider) {
    console.warn('APN provider not available - skipping push notification');
    return { sent: [], failed: [] };
  }

  if (!Array.isArray(deviceTokens)) {
    deviceTokens = [deviceTokens];
  }
  
  const notification = new apn.Notification();
  notification.alert = { title, body: message };
  notification.topic = process.env.APN_BUNDLE_ID; // e.g., com.chasedavis.Beyhive-Alert-2
  notification.payload = { notifType };
  notification.sound = 'default';

  try {
    const result = await apnProvider.send(notification, deviceTokens);
    console.log('APNs result:', result);
    return result;
  } catch (err) {
    console.error('APNs error:', err);
    throw err;
  }
}

module.exports = { sendPushNotification }; 