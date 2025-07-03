const express = require('express');
const cors = require('cors');
const apn = require('apn');
const fs = require('fs');
const app = express();
const port = 3000;
const DEVICES_FILE = 'devices.json';
const PURCHASES_FILE = 'purchases.json';

// Store device tokens and preferences
let devices = [];
let purchases = [];

// Load devices from file on startup
if (fs.existsSync(DEVICES_FILE)) {
    try {
        devices = JSON.parse(fs.readFileSync(DEVICES_FILE, 'utf8'));
        console.log(`Loaded ${devices.length} devices from ${DEVICES_FILE}`);
    } catch (e) {
        console.error('Failed to load devices.json:', e);
        devices = [];
    }
}
// Load purchases from file on startup
if (fs.existsSync(PURCHASES_FILE)) {
    try {
        purchases = JSON.parse(fs.readFileSync(PURCHASES_FILE, 'utf8'));
        console.log(`Loaded ${purchases.length} purchases from ${PURCHASES_FILE}`);
    } catch (e) {
        console.error('Failed to load purchases.json:', e);
        purchases = [];
    }
}

function saveDevices() {
    fs.writeFileSync(DEVICES_FILE, JSON.stringify(devices, null, 2));
}
function savePurchases() {
    fs.writeFileSync(PURCHASES_FILE, JSON.stringify(purchases, null, 2));
}

// Apple Push Notification setup
const apnProvider = new apn.Provider({
    token: {
        key: './AuthKey_8M48VV84A3.p8', // Your APNs key file
        keyId: '8M48VV84A3', // Your key ID
        teamId: 'A46AH86Z73' // Your Apple Developer Team ID
    },
    production: false // Set to true for App Store builds
});

app.use(cors());
app.use(express.json());
app.use(express.static('.')); // Serve static files

// Register device with preferences
app.post('/register-device', (req, res) => {
    const { deviceToken, preferences } = req.body;
    if (!deviceToken) return res.status(400).json({ success: false, message: 'Missing deviceToken' });
    let device = devices.find(d => d.deviceToken === deviceToken);
    const now = new Date().toISOString();
    if (device) {
        // Update preferences if provided
        if (preferences) device.preferences = preferences;
        device.lastActive = now;
    } else {
        devices.push({ deviceToken, preferences: preferences || {}, lastActive: now });
        console.log('Device registered:', deviceToken);
        console.log('Total devices:', devices.length);
    }
    saveDevices();
    res.json({ success: true, message: 'Device registered/updated' });
});

// Send notification to users with a specific preference
app.post('/send-notification', async (req, res) => {
    const { title, body, data, preferenceKey } = req.body;
    let targetDevices = devices;
    if (preferenceKey) {
        targetDevices = devices.filter(d => d.preferences && d.preferences[preferenceKey]);
    }
    const tokens = targetDevices.map(d => d.deviceToken);
    if (tokens.length === 0) {
        return res.json({ success: false, message: 'No devices registered for this notification' });
    }
    // Create notification
    const notification = new apn.Notification();
    notification.alert = {
        title: title || 'Beyhive Alert',
        body: body || 'New notification!'
    };
    notification.badge = 1;
    notification.sound = 'default';
    notification.topic = 'com.chasedavis.Beyhive-Alert-2'; // Your app's bundle ID
    notification.payload = data || {};
    try {
        const results = await apnProvider.send(notification, tokens);
        console.log('Notification sent to', tokens.length, 'devices');
        console.log('Results:', results);
        // Check for failed deliveries
        const failed = results.failed || [];
        if (failed.length > 0) {
            console.log('Failed deliveries:', failed);
            // Remove failed tokens (DISABLED: keep all tokens forever)
            // failed.forEach(result => {
            //     const index = devices.findIndex(d => d.deviceToken === result.device);
            //     if (index > -1) {
            //         devices.splice(index, 1);
            //     }
            // });
            // saveDevices();
        }
        res.json({ 
            success: true, 
            message: 'Real push notifications sent!',
            devices: tokens.length,
            results: results
        });
    } catch (error) {
        console.error('Error sending notifications:', error);
        res.json({ success: false, error: error.message });
    }
});

// Get all registered devices and their preferences
app.get('/devices', (req, res) => {
    res.json({ devices, count: devices.length });
});

// Test endpoint for local notifications
app.post('/test-notification', (req, res) => {
    const { title, body } = req.body;
    console.log('Test notification:');
    console.log('Title:', title);
    console.log('Body:', body);
    console.log('To devices:', devices.length);
    res.json({ success: true, message: 'Test notification logged' });
});

// Log purchase endpoint
app.post('/log-purchase', (req, res) => {
    const { deviceToken, timestamp } = req.body;
    if (!deviceToken || !timestamp) {
        return res.status(400).json({ success: false, message: 'Missing deviceToken or timestamp' });
    }
    purchases.push({ deviceToken, timestamp });
    savePurchases();
    res.json({ success: true, message: 'Purchase logged' });
});

// Get all purchases
app.get('/purchases', (req, res) => {
    res.json({ purchases, count: purchases.length });
});

// Endpoint to get active users in the last 7 days
app.get('/active-users', (req, res) => {
    const now = Date.now();
    const sevenDaysMs = 7 * 24 * 60 * 60 * 1000;
    const activeUsers = devices.filter(d => {
        if (!d.lastActive) return false;
        return (now - new Date(d.lastActive).getTime()) < sevenDaysMs;
    });
    res.json({ active: activeUsers.length, total: devices.length });
});

// Ping endpoint to update lastActive
app.post('/ping', (req, res) => {
    const { deviceToken } = req.body;
    if (!deviceToken) return res.status(400).json({ success: false, message: 'Missing deviceToken' });
    let device = devices.find(d => d.deviceToken === deviceToken);
    const now = new Date().toISOString();
    if (device) {
        device.lastActive = now;
        saveDevices();
        return res.json({ success: true, message: 'Ping updated' });
    }
    res.status(404).json({ success: false, message: 'Device not found' });
});

// Endpoint to get online users (last 5 minutes)
app.get('/online-users', (req, res) => {
    const now = Date.now();
    const fiveMinutesMs = 5 * 60 * 1000;
    const onlineUsers = devices.filter(d => {
        if (!d.lastActive) return false;
        return (now - new Date(d.lastActive).getTime()) < fiveMinutesMs;
    });
    res.json({ online: onlineUsers.length, total: devices.length });
});

app.listen(port, () => {
    console.log(`🚀 Real push notification server running on http://localhost:${port}`);
    console.log('📱 Endpoints:');
    console.log('  POST /register-device - Register a device token (with preferences)');
    console.log('  POST /send-notification - Send REAL push notifications (optionally filtered by preference)');
    console.log('  POST /test-notification - Test notification (logs only)');
    console.log('  GET /devices - List all registered devices and preferences');
    console.log('  POST /log-purchase - Log a purchase');
    console.log('  GET /purchases - List all purchases');
    console.log('  GET /active-users - List active users in the last 7 days');
    console.log('  POST /ping - Update lastActive for a device token');
    console.log('  GET /online-users - List online users in the last 5 minutes');
    console.log('');
    console.log('⚠️  Make sure to:');
    console.log('  1. Add your APNs key file (AuthKey_8M48VV84A3.p8)');
    console.log('  2. Update YOUR_TEAM_ID with your Apple Developer Team ID');
    console.log('  3. Update the bundle ID in the notification.topic');
}); 