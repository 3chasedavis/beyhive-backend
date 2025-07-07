const mongoose = require('mongoose');
const User = require('./models/User');
const DeviceToken = require('./models/DeviceToken');

const MONGO_URI = process.env.MONGO_URI || 'YOUR_MONGODB_URI_HERE';

async function migrate() {
  await mongoose.connect(MONGO_URI);
  const users = await User.find({ deviceTokens: { $exists: true, $not: { $size: 0 } } });
  let migrated = 0;
  for (const user of users) {
    for (const token of user.deviceTokens) {
      // Avoid duplicates
      const exists = await DeviceToken.findOne({ token });
      if (!exists) {
        await DeviceToken.create({ token });
        migrated++;
      }
    }
  }
  console.log(`Migrated ${migrated} device tokens from User to DeviceToken collection.`);
  await mongoose.disconnect();
}

migrate().catch(err => { console.error(err); process.exit(1); }); 