const fs = require('fs');
const path = require('path');

// Local storage utility - saves images as base64 in JSON files
// This is completely free and doesn't require any external services

// Upload image to local storage (base64 in JSON)
async function uploadImage(file, folder = 'beyhive-outfits') {
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

    // Convert to base64 for storage in JSON
    const base64String = imageBuffer.toString('base64');
    const mimeType = file.startsWith('data:') ? file.split(';')[0].split(':')[1] : 'image/jpeg';
    
    // Create a data URL for storage
    const dataUrl = `data:${mimeType};base64,${base64String}`;
    
    return dataUrl;
    
  } catch (error) {
    console.error('Local storage upload error:', error);
    throw new Error('Failed to upload image to local storage');
  }
}

// Delete image from local storage (not needed for base64 storage)
async function deleteImage(imageUrl) {
  // For base64 storage, we don't need to delete individual images
  // They're stored directly in the JSON files
  return true;
}

module.exports = { uploadImage, deleteImage }; 