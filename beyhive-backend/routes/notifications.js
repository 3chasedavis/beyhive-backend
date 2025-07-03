const express = require('express');
const { authenticateToken, requireEmailVerification } = require('../middleware/auth');
const User = require('../models/User');
const DeviceToken = require('../models/DeviceToken');

const router = express.Router();

// Get all notification preferences for a user
router.get('/preferences', authenticateToken, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('notificationPreferences');
    res.json({
      notificationPreferences: user.notificationPreferences
    });
  } catch (error) {
    console.error('Get notification preferences error:', error);
    res.status(500).json({ error: 'Server error while fetching notification preferences' });
  }
});

// Update specific notification preference
router.put('/preferences/:type', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const { type } = req.params;
    const { enabled } = req.body;

    if (typeof enabled !== 'boolean') {
      return res.status(400).json({ error: 'Enabled must be a boolean value' });
    }

    const user = await User.findById(req.user._id);
    
    // Check if the notification type exists
    if (!user.notificationPreferences.hasOwnProperty(type)) {
      return res.status(400).json({ error: 'Invalid notification type' });
    }

    // Update the specific preference
    user.notificationPreferences[type] = enabled;
    await user.save();

    res.json({
      message: `${type} notification preference updated successfully`,
      notificationPreferences: user.notificationPreferences
    });

  } catch (error) {
    console.error('Update notification preference error:', error);
    res.status(500).json({ error: 'Server error while updating notification preference' });
  }
});

// Update multiple notification preferences at once
router.put('/preferences', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const { preferences } = req.body;

    if (!preferences || typeof preferences !== 'object') {
      return res.status(400).json({ error: 'Preferences object is required' });
    }

    const user = await User.findById(req.user._id);
    
    // Update only valid preferences
    Object.keys(preferences).forEach(key => {
      if (user.notificationPreferences.hasOwnProperty(key) && typeof preferences[key] === 'boolean') {
        user.notificationPreferences[key] = preferences[key];
      }
    });

    await user.save();

    res.json({
      message: 'Notification preferences updated successfully',
      notificationPreferences: user.notificationPreferences
    });

  } catch (error) {
    console.error('Update notification preferences error:', error);
    res.status(500).json({ error: 'Server error while updating notification preferences' });
  }
});

// Get users who should receive a specific notification type
router.get('/users/:type', async (req, res) => {
  try {
    const { type } = req.params;

    // Validate notification type
    const validTypes = [
      'concertStart', 'sabrinaOnStage', 'featherStart', 'spinTheBottle',
      'finalSet', 'junoStart', 'espressoFinale', 'pushNotifications', 'emailNotifications'
    ];

    if (!validTypes.includes(type)) {
      return res.status(400).json({ error: 'Invalid notification type' });
    }

    // Find users who have this notification type enabled
    const users = await User.find({
      [`notificationPreferences.${type}`]: true,
      isEmailVerified: true
    }).select('email notificationPreferences');

    res.json({
      count: users.length,
      users: users.map(user => ({
        id: user._id,
        email: user.email,
        preferences: user.notificationPreferences
      }))
    });

  } catch (error) {
    console.error('Get users for notification error:', error);
    res.status(500).json({ error: 'Server error while fetching users for notification' });
  }
});

// Send a notification to all users who have it enabled
router.post('/send/:type', authenticateToken, async (req, res) => {
  // Only allow admins
  if (!req.user || !req.user.isAdmin) {
    return res.status(403).json({ error: 'Admin access required' });
  }
  try {
    const { type } = req.params;
    const { title, message, data } = req.body;

    if (!title || !message) {
      return res.status(400).json({ error: 'Title and message are required' });
    }

    // Validate notification type
    const validTypes = [
      'concertStart', 'sabrinaOnStage', 'featherStart', 'spinTheBottle',
      'finalSet', 'junoStart', 'espressoFinale'
    ];

    if (!validTypes.includes(type)) {
      return res.status(400).json({ error: 'Invalid notification type' });
    }

    // Find users who have this notification type enabled and have a device token
    const users = await User.find({
      [`notificationPreferences.${type}`]: true,
      isEmailVerified: true,
      deviceTokens: { $exists: true, $not: { $size: 0 } }
    });

    // Here you would integrate with your push notification service
    // For now, we'll just log the notification
    console.log(`Sending ${type} notification to ${users.length} users:`);
    console.log(`Title: ${title}`);
    console.log(`Message: ${message}`);
    console.log(`Data:`, data);
    
    users.forEach(user => {
      console.log(`- ${user.email} (tokens: ${user.deviceTokens?.length || 0})`);
    });

    res.json({
      message: `Notification sent to ${users.length} users`,
      sentTo: users.length,
      notificationType: type
    });

  } catch (error) {
    console.error('Send notification error:', error);
    res.status(500).json({ error: 'Server error while sending notification' });
  }
});

// Get notification statistics
router.get('/stats', authenticateToken, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('notificationPreferences');
    
    const enabledCount = Object.values(user.notificationPreferences).filter(Boolean).length;
    const totalCount = Object.keys(user.notificationPreferences).length;

    res.json({
      enabledCount,
      totalCount,
      disabledCount: totalCount - enabledCount,
      preferences: user.notificationPreferences
    });

  } catch (error) {
    console.error('Get notification stats error:', error);
    res.status(500).json({ error: 'Server error while fetching notification statistics' });
  }
});

// Reset all notification preferences to defaults
router.post('/reset', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const user = await User.findById(req.user._id);
    
    // Reset to default values
    user.notificationPreferences = {
      concertStart: true,
      sabrinaOnStage: true,
      featherStart: true,
      spinTheBottle: true,
      finalSet: true,
      junoStart: true,
      espressoFinale: true,
      pushNotifications: true,
      emailNotifications: true
    };

    await user.save();

    res.json({
      message: 'Notification preferences reset to defaults',
      notificationPreferences: user.notificationPreferences
    });

  } catch (error) {
    console.error('Reset notification preferences error:', error);
    res.status(500).json({ error: 'Server error while resetting notification preferences' });
  }
});

// Anonymous device token registration endpoint for FCM
router.post('/device-token', async (req, res) => {
  const { token, preferences } = req.body;
  if (!token) return res.status(400).json({ error: 'Token required' });
  await DeviceToken.updateOne(
    { token },
    { $set: { preferences: preferences || {} } },
    { upsert: true }
  );
  res.json({ message: 'Token and preferences registered' });
});

module.exports = router; 