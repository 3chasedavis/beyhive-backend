const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

try {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
  console.log("✅ Firebase Admin initialized successfully!");
} catch (err) {
  console.error("❌ Failed to initialize Firebase Admin:", err);
} 