const mongoose = require('mongoose');
const fs = require('fs');
require('dotenv').config();

const Event = require('./models/Event');

async function migrate() {
  await mongoose.connect(process.env.MONGODB_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  console.log('Connected to MongoDB');

  // Migrate Events
  const eventsData = JSON.parse(fs.readFileSync('events.json', 'utf8'));
  for (const event of eventsData) {
    await Event.updateOne({ id: event.id }, event, { upsert: true });
  }
  console.log(`Migrated ${eventsData.length} events.`);

  await mongoose.disconnect();
  console.log('Migration complete!');
}

migrate().catch(err => {
  console.error('Migration failed:', err);
  process.exit(1);
}); 