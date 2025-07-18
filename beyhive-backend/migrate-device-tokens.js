const mongoose = require('mongoose');
require('dotenv').config();

// Old cluster connection
const OLD_URI = 'mongodb+srv://thechaseter52478:Chase105@cluster0.c9tyzlg.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0';
// New cluster connection
const NEW_URI = 'mongodb+srv://beyhivealert:GLj5T0Znqdg6OjBg@cluster0.mjpsyvy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0';

const oldDeviceTokenSchema = new mongoose.Schema({}, { strict: false });
const newDeviceTokenSchema = new mongoose.Schema({}, { strict: false });

async function migrateDeviceTokens() {
  // Connect to old cluster
  const oldConn = await mongoose.createConnection(OLD_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  const OldDeviceToken = oldConn.model('DeviceToken', oldDeviceTokenSchema, 'devicetokens');

  // Connect to new cluster
  const newConn = await mongoose.createConnection(NEW_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  const NewDeviceToken = newConn.model('DeviceToken', newDeviceTokenSchema, 'devicetokens');

  // Fetch all device tokens from old cluster
  const tokens = await OldDeviceToken.find();
  console.log(`Found ${tokens.length} device tokens in old cluster.`);

  // Insert into new cluster (upsert by token)
  let migrated = 0;
  for (const token of tokens) {
    const plain = token.toObject();
    delete plain._id;
    await NewDeviceToken.updateOne({ token: plain.token }, plain, { upsert: true });
    migrated++;
  }
  console.log(`Migrated ${migrated} device tokens to new cluster.`);

  await oldConn.close();
  await newConn.close();
  console.log('Migration complete!');
}

migrateDeviceTokens().catch(err => {
  console.error('Migration failed:', err);
  process.exit(1);
}); 