const { initializeApp } = require('firebase/app');
const { getStorage, ref, uploadBytes, getDownloadURL, deleteObject } = require('firebase/storage');

// Firebase configuration
const firebaseConfig = {
  apiKey: process.env.FIREBASE_API_KEY,
  authDomain: process.env.FIREBASE_AUTH_DOMAIN,
  projectId: process.env.FIREBASE_PROJECT_ID,
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.FIREBASE_APP_ID
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

// Upload image to Firebase Storage
async function uploadImage(file, folder = 'beyhive-outfits') {
  if (!process.env.FIREBASE_API_KEY) {
    throw new Error('Firebase not configured');
  }

  try {
    // Convert base64 to buffer if needed
    let imageBuffer;
    if (file.startsWith('data:')) {
      // Handle base64 data URL
      const base64Data = file.split(',')[1];
      imageBuffer = Buffer.from(base64Data, 'base64');
    } else {
      // Assume it's already a buffer
      imageBuffer = file;
    }

    // Generate unique filename
    const timestamp = Date.now();
    const filename = `${folder}/${timestamp}-${Math.random().toString(36).substring(7)}.jpg`;
    
    // Create storage reference
    const storageRef = ref(storage, filename);
    
    // Upload the file
    const snapshot = await uploadBytes(storageRef, imageBuffer, {
      contentType: 'image/jpeg'
    });
    
    // Get download URL
    const downloadURL = await getDownloadURL(snapshot.ref);
    return downloadURL;
    
  } catch (error) {
    console.error('Firebase upload error:', error);
    throw new Error('Failed to upload image to Firebase');
  }
}

// Delete image from Firebase Storage
async function deleteImage(imageUrl) {
  if (!process.env.FIREBASE_API_KEY) {
    throw new Error('Firebase not configured');
  }

  try {
    // Extract the path from the URL
    const urlParts = imageUrl.split('/');
    const pathIndex = urlParts.findIndex(part => part === 'o');
    if (pathIndex === -1) {
      throw new Error('Invalid Firebase URL');
    }
    
    const encodedPath = urlParts[pathIndex + 1];
    const path = decodeURIComponent(encodedPath);
    
    const storageRef = ref(storage, path);
    await deleteObject(storageRef);
    return true;
  } catch (error) {
    console.error('Firebase delete error:', error);
    throw new Error('Failed to delete image from Firebase');
  }
}

module.exports = { uploadImage, deleteImage }; 