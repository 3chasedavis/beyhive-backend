const mongoose = require('mongoose');
const fs = require('fs');
require('dotenv').config();

const Outfit = require('./models/Outfit');
const Partner = require('./models/Partner');

async function migrate() {
  await mongoose.connect(process.env.MONGODB_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  console.log('Connected to MongoDB');

  // Migrate Outfits
  const outfitsData = JSON.parse(fs.readFileSync('outfits.json', 'utf8'));
  for (const outfit of outfitsData) {
    await Outfit.updateOne({ id: outfit.id }, outfit, { upsert: true });
  }
  console.log(`Migrated ${outfitsData.length} outfits.`);

  // Migrate Partners
  if (fs.existsSync('partners.json')) {
    const partnersData = JSON.parse(fs.readFileSync('partners.json', 'utf8'));
    for (const partner of partnersData) {
      await Partner.updateOne({ name: partner.name }, partner, { upsert: true });
    }
    console.log(`Migrated ${partnersData.length} partners.`);
  }

  await mongoose.disconnect();
  console.log('Migration complete!');
}

migrate().catch(err => {
  console.error('Migration failed:', err);
  process.exit(1);
}); 