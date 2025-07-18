const mongoose = require('mongoose');

const PartnerSchema = new mongoose.Schema({
  name: { type: String, required: true },
  description: { type: String, required: true },
  iconUrl: { type: String, default: null },
  link: { type: String, required: true }
});

module.exports = mongoose.model('Partner', PartnerSchema); 