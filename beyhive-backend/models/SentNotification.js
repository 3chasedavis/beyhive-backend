const mongoose = require('mongoose');
const SentNotificationSchema = new mongoose.Schema({
    notifType: { type: String, required: true },
    title: { type: String, required: true },
    message: { type: String, required: true },
    sentAt: { type: Date, default: Date.now }
});
module.exports = mongoose.model('SentNotification', SentNotificationSchema); 