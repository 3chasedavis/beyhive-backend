// APNs push notification service using apn and .env variables
const apn = require('apn');
const path = require('path');

// APNs provider setup (sandbox)
const apnProvider = new apn.Provider({
  token: {
    key: path.join(__dirname, '..', process.env.APN_KEY_FILE), // e.g., AuthKey_8M48VV84A3.p8
    keyId: process.env.APN_KEY_ID, // e.g., 8M48VV84A3
    teamId: process.env.APN_TEAM_ID, // e.g., A46AH86Z73
  },
  production: false, // false = sandbox, true = production
});

// Send push notification to a device token
async function sendPushNotification(title, message, notifType, deviceTokens) {
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