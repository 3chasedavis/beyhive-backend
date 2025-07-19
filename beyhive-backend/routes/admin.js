const express = require('express');
const fetch = require('node-fetch');
const router = express.Router();
const Notification = require('../models/Notification');
const bodyParser = require('body-parser');
const { sendPushNotification } = require('../utils/pushService'); // You need to implement this util
const User = require('../models/User');
const SentNotification = require('../models/SentNotification');
const DeviceToken = require('../models/DeviceToken');
// REMOVE: const admin = require('firebase-admin');
const session = require('express-session');
const path = require('path');

const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'chase3870';
console.log('ADMIN_PASSWORD at startup:', ADMIN_PASSWORD); // DEBUG: Remove after verifying

const fs = require('fs');
const updateRequiredPath = require('path').join(__dirname, '../update-required.json');

// Maintenance mode endpoints (public - no authentication required)
router.get('/maintenance-mode', (req, res) => {
  const maintenanceFile = path.join(__dirname, '../maintenance.json');
  if (fs.existsSync(maintenanceFile)) {
    const data = JSON.parse(fs.readFileSync(maintenanceFile, 'utf8'));
    res.json({ isMaintenanceMode: data.isMaintenanceMode || false });
  } else {
    res.json({ isMaintenanceMode: false });
  }
});

router.post('/maintenance-mode', (req, res) => {
  const maintenanceFile = path.join(__dirname, '../maintenance.json');
  const { isMaintenanceMode } = req.body;
  const data = { isMaintenanceMode: isMaintenanceMode || false };
  fs.writeFileSync(maintenanceFile, JSON.stringify(data, null, 2));
  res.json({ success: true, isMaintenanceMode: data.isMaintenanceMode });
});

// Middleware to require admin session
function requireAdminSession(req, res, next) {
    if (req.session && req.session.isAdmin) {
        return next();
    }
    res.status(401).json({ error: 'Not authenticated' });
}

// Admin login route
router.post('/login', (req, res) => {
    const { password } = req.body;
    if (password === ADMIN_PASSWORD) {
        req.session.isAdmin = true;
        return res.json({ success: true });
    }
    res.json({ success: false });
});

// Check authentication status
router.get('/check-auth', (req, res) => {
    res.json({ authenticated: !!(req.session && req.session.isAdmin) });
});

// Protect all admin routes below this line
router.use(requireAdminSession);

// GET: Show the notification form
router.get('/notify', (req, res) => {
  res.send(`
    <html>
      <head>
        <title>Send Beyoncé On Stage Notification</title>
        <style>
          body { font-family: sans-serif; background: #fffbe7; padding: 40px; }
          form { background: #fff; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px #0001; max-width: 400px; margin: auto; }
          input, textarea { width: 100%; margin-bottom: 16px; padding: 8px; border-radius: 6px; border: 1px solid #ccc; }
          button { background: #ffd600; color: #222; border: none; padding: 12px 24px; border-radius: 8px; font-weight: bold; cursor: pointer; }
        </style>
      </head>
      <body>
        <form method="POST" action="/admin/notify">
          <h2>Send Beyoncé On Stage Notification</h2>
          <label>Admin JWT Token:<br><input name="jwt" type="text" required></label>
          <label>Title:<br><input name="title" type="text" value="Beyoncé is on stage!" required></label>
          <label>Message:<br><textarea name="message" rows="3" required>The show has started. Tune in now!</textarea></label>
          <button type="submit">Send Notification</button>
        </form>
      </body>
    </html>
  `);
});

// POST: Handle the form and call the API
router.post('/notify', async (req, res) => {
  const { jwt, title, message } = req.body;
  try {
    const response = await fetch('http://localhost:3000/api/notifications/send/sabrinaOnStage', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ title, message, data: {} })
    });
    const result = await response.json();
    res.send(`<html><body style='font-family:sans-serif;'><h2>Result</h2><pre>${JSON.stringify(result, null, 2)}</pre><a href='/admin/notify'>Back</a></body></html>`);
  } catch (err) {
    res.send(`<html><body style='font-family:sans-serif;'><h2>Error</h2><pre>${err}</pre><a href='/admin/notify'>Back</a></body></html>`);
  }
});

// Serve the admin HTML page
router.get('/notifications', async (req, res) => {
    res.setHeader('Content-Security-Policy', "script-src 'self' 'unsafe-inline'");
    res.sendFile(require('path').join(__dirname, '../admin.html'));
});

// API to get notifications
router.get('/notifications/api', async (req, res) => {
    const notifications = await Notification.find().sort({ receivedAt: -1 }).limit(100);
    res.json(notifications);
});

// Send notification to selected group
router.post('/notifications/send', async (req, res) => {
  try {
    const { title, message, notifType } = req.body;
    if (!notifType) return res.status(400).json({ error: 'Notification type required' });
    let tokens = [];
    if (notifType === 'everyone') {
      tokens = await DeviceToken.find().distinct('token');
    } else {
      tokens = await DeviceToken.find({ [`preferences.${notifType}`]: true }).distinct('token');
    }
    if (!tokens.length) return res.status(400).json({ error: 'No tokens found for this group' });
    try {
      const result = await sendPushNotification(title, message, notifType, tokens);
      return res.json({ success: true, result });
    } catch (err) {
      return res.status(500).json({ error: err.message });
    }
  } catch (err) {
    return res.status(500).json({ error: err.message || 'Unknown error' });
  }
});

// API to get total user count
router.get('/usercount', async (req, res) => {
    const count = await User.countDocuments();
    res.json({ count });
});

// API to get active user count (last 30 days)
router.get('/activeusercount', async (req, res) => {
    const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
    const count = await User.countDocuments({ lastLogin: { $gte: thirtyDaysAgo } });
    res.json({ count });
});

// API to get notification history
router.get('/notificationhistory', async (req, res) => {
    const history = await SentNotification.find().sort({ sentAt: -1 }).limit(20);
    res.json(history);
});

// List all device tokens (admin only)
router.get('/device-tokens', async (req, res) => {
    const password = req.query.password || req.body.password;
    if (password !== ADMIN_PASSWORD) {
        return res.status(401).json({ error: 'Unauthorized' });
    }
    console.log('Fetching device tokens...');
    const tokens = await DeviceToken.find({}, '-_id token createdAt').sort({ createdAt: -1 });
    console.log('Found tokens:', tokens.length);
    res.json(tokens);
});

// API to get total registered devices (unique device tokens)
router.get('/registereddevices', async (req, res) => {
    const count = await DeviceToken.countDocuments();
    res.json({ count });
});

// API to get total device tokens (across all users)
router.get('/devicetokencount', async (req, res) => {
    const users = await User.find({}, 'deviceTokens');
    const allTokens = users.reduce((acc, user) => acc.concat(user.deviceTokens || []), []);
    res.json({ count: allTokens.length });
});

// API to get currently online users (lastLogin within 5 minutes)
router.get('/onlinenow', async (req, res) => {
    const fiveMinutesAgo = new Date(Date.now() - 5 * 60 * 1000);
    const count = await User.countDocuments({ lastLogin: { $gte: fiveMinutesAgo } });
    res.json({ count });
});

// GET update-required status
router.get('/update-required', (req, res) => {
  if (!fs.existsSync(updateRequiredPath)) {
    return res.json({ updateRequired: false, minVersion: '' });
  }
  const data = JSON.parse(fs.readFileSync(updateRequiredPath, 'utf8'));
  res.json(data);
});

// POST update-required status
router.post('/update-required', (req, res) => {
  const { updateRequired, minVersion } = req.body;
  const data = {
    updateRequired: !!updateRequired,
    minVersion: minVersion || ''
  };
  fs.writeFileSync(updateRequiredPath, JSON.stringify(data, null, 2));
  res.json({ success: true, ...data });
});

module.exports = router; 