const express = require('express');
const { body, validationResult } = require('express-validator');
const { authenticateToken, requireEmailVerification } = require('../middleware/auth');
const User = require('../models/User');

const router = express.Router();

// Get current user profile
router.get('/profile', authenticateToken, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('-password');
    res.json({ user });
  } catch (error) {
    console.error('Get profile error:', error);
    res.status(500).json({ error: 'Server error while fetching profile' });
  }
});

// Update user profile
router.put('/profile', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const { email } = req.body;
    const errors = validationResult(req);
    
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    // Check if email is being changed and if it's already taken
    if (email && email !== req.user.email) {
      const existingUser = await User.findOne({ email });
      if (existingUser) {
        return res.status(400).json({ error: 'Email already in use' });
      }
    }

    const user = await User.findById(req.user._id);
    if (email) user.email = email;
    
    await user.save();

    res.json({
      message: 'Profile updated successfully',
      user: {
        id: user._id,
        email: user.email,
        isEmailVerified: user.isEmailVerified
      }
    });

  } catch (error) {
    console.error('Update profile error:', error);
    res.status(500).json({ error: 'Server error while updating profile' });
  }
});

// Get notification preferences
router.get('/notifications', authenticateToken, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('notificationPreferences');
    res.json({ notificationPreferences: user.notificationPreferences });
  } catch (error) {
    console.error('Get notifications error:', error);
    res.status(500).json({ error: 'Server error while fetching notification preferences' });
  }
});

// Update notification preferences
router.put('/notifications', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const { notificationPreferences } = req.body;

    if (!notificationPreferences || typeof notificationPreferences !== 'object') {
      return res.status(400).json({ error: 'Invalid notification preferences' });
    }

    const user = await User.findById(req.user._id);
    
    // Update only the provided preferences
    Object.keys(notificationPreferences).forEach(key => {
      if (user.notificationPreferences.hasOwnProperty(key)) {
        user.notificationPreferences[key] = notificationPreferences[key];
      }
    });

    await user.save();

    res.json({
      message: 'Notification preferences updated successfully',
      notificationPreferences: user.notificationPreferences
    });

  } catch (error) {
    console.error('Update notifications error:', error);
    res.status(500).json({ error: 'Server error while updating notification preferences' });
  }
});

// Get app settings
router.get('/settings', authenticateToken, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('appSettings');
    res.json({ appSettings: user.appSettings });
  } catch (error) {
    console.error('Get settings error:', error);
    res.status(500).json({ error: 'Server error while fetching app settings' });
  }
});

// Update app settings
router.put('/settings', authenticateToken, requireEmailVerification, async (req, res) => {
  try {
    const { appSettings } = req.body;

    if (!appSettings || typeof appSettings !== 'object') {
      return res.status(400).json({ error: 'Invalid app settings' });
    }

    const user = await User.findById(req.user._id);
    
    // Update only the provided settings
    Object.keys(appSettings).forEach(key => {
      if (user.appSettings.hasOwnProperty(key)) {
        user.appSettings[key] = appSettings[key];
      }
    });

    await user.save();

    res.json({
      message: 'App settings updated successfully',
      appSettings: user.appSettings
    });

  } catch (error) {
    console.error('Update settings error:', error);
    res.status(500).json({ error: 'Server error while updating app settings' });
  }
});

// Delete account
router.delete('/account', authenticateToken, async (req, res) => {
  try {
    await User.findByIdAndDelete(req.user._id);
    res.json({ message: 'Account deleted successfully' });
  } catch (error) {
    console.error('Delete account error:', error);
    res.status(500).json({ error: 'Server error while deleting account' });
  }
});

// Logout (client-side token removal, but we can track it)
router.post('/logout', authenticateToken, async (req, res) => {
  try {
    // Update last login to track logout
    const user = await User.findById(req.user._id);
    user.lastLogin = Date.now();
    await user.save();

    res.json({ message: 'Logged out successfully' });
  } catch (error) {
    console.error('Logout error:', error);
    res.status(500).json({ error: 'Server error during logout' });
  }
});

// Register or update device token for push notifications
router.post('/device-token', authenticateToken, async (req, res) => {
  try {
    const { deviceToken } = req.body;
    if (!deviceToken || typeof deviceToken !== 'string') {
      return res.status(400).json({ error: 'Device token is required.' });
    }
    const user = await User.findById(req.user._id);
    if (!user.deviceTokens.includes(deviceToken)) {
      user.deviceTokens.push(deviceToken);
      await user.save();
    }
    res.json({ message: 'Device token registered successfully.' });
  } catch (error) {
    console.error('Register device token error:', error);
    res.status(500).json({ error: 'Server error while registering device token.' });
  }
});

module.exports = router; 