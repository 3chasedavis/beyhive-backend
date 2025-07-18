const mongoose = require('mongoose');

const OutfitSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  name: { type: String, required: true },
  location: { type: String, required: true },
  imageUrl: { type: String, default: null },
  isNew: { type: Boolean, default: false },
  section: { type: String, required: true },
  description: { type: String, default: null }
});

module.exports = mongoose.model('Outfit', OutfitSchema); 