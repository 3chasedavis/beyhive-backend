const mongoose = require('mongoose');
require('dotenv').config();
const Outfit = require('./models/Outfit');
const Partner = require('./models/Partner');

async function checkCounts() {
  await mongoose.connect(process.env.MONGODB_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  const outfitCount = await Outfit.countDocuments();
  const partnerCount = await Partner.countDocuments();
  console.log(`Outfits in DB: ${outfitCount}`);
  console.log(`Partners in DB: ${partnerCount}`);
  await mongoose.disconnect();
}

checkCounts(); 