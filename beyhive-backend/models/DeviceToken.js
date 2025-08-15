const mongoose = require('mongoose');

const deviceTokenSchema = new mongoose.Schema({
  token: { type: String, required: true, unique: true },
  preferences: { type: Object, default: {} },
  platform: { type: String, enum: ['ios', 'android'], required: true }, // Added platform field
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('DeviceToken', deviceTokenSchema); 