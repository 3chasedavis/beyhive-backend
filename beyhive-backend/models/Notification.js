const mongoose = require('mongoose');
const NotificationSchema = new mongoose.Schema({
    data: { type: Object, required: true },
    receivedAt: { type: Date, default: Date.now }
});
module.exports = mongoose.model('Notification', NotificationSchema); 