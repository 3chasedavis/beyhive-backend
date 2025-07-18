const mongoose = require('mongoose');

const EventSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  title: { type: String, required: true },
  date: { type: String, required: true },
  time: { type: String, required: true },
  location: { type: String, required: true },
  timezone: { type: String, required: true },
  description: { type: String, default: null }
});

module.exports = mongoose.model('Event', EventSchema); 