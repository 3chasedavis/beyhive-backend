const mongoose = require('mongoose');
const fs = require('fs');
require('dotenv').config();

const Event = require('./models/Event');
const Outfit = require('./models/Outfit');
const Partner = require('./models/Partner');

async function exportAll() {
  await mongoose.connect(process.env.MONGODB_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  console.log('Connected to MongoDB');

  // Export Events
  const events = await Event.find();
  fs.writeFileSync('events.json', JSON.stringify(events.map(e => ({
    id: e.id,
    title: e.title,
    date: e.date,
    time: e.time,
    location: e.location,
    timezone: e.timezone,
    description: e.description
  })), null, 2));
  console.log(`Exported ${events.length} events to events.json`);

  // Export Outfits
  const outfits = await Outfit.find();
  fs.writeFileSync('outfits.json', JSON.stringify(outfits.map(o => ({
    id: o.id,
    name: o.name,
    location: o.location,
    imageUrl: o.imageUrl,
    isNew: o.isNew,
    section: o.section,
    description: o.description
  })), null, 2));
  console.log(`Exported ${outfits.length} outfits to outfits.json`);

  // Export Partners
  const partners = await Partner.find();
  fs.writeFileSync('partners.json', JSON.stringify(partners.map(p => ({
    name: p.name,
    description: p.description,
    iconUrl: p.iconUrl,
    link: p.link
  })), null, 2));
  console.log(`Exported ${partners.length} partners to partners.json`);

  await mongoose.disconnect();
  console.log('Export complete!');
}

exportAll().catch(err => {
  console.error('Export failed:', err);
  process.exit(1);
}); 