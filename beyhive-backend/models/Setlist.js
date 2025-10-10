const mongoose = require('mongoose');

const SetlistSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  title: { type: String, required: true }, // e.g., "Act 1 - Intro", "Act 2 - Revolution"
  songs: [{ 
    name: { type: String, required: true },
    order: { type: Number, required: true },
    notes: { type: String, default: null } // Optional notes about the song
  }],
  isActive: { type: Boolean, default: true }, // Whether this setlist is currently being used
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
}, {
  timestamps: true
});

// Update the updatedAt field before saving
SetlistSchema.pre('save', function(next) {
  this.updatedAt = new Date();
  next();
});

module.exports = mongoose.model('Setlist', SetlistSchema);


