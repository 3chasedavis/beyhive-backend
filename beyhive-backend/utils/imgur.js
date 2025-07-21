const axios = require('axios');
const FormData = require('form-data');

// Imgur API configuration
const IMGUR_CLIENT_ID = process.env.IMGUR_CLIENT_ID;
const IMGUR_API_URL = 'https://api.imgur.com/3';

// Upload image to Imgur
async function uploadImage(file, title = 'Beyhive Alert Image') {
  if (!IMGUR_CLIENT_ID) {
    throw new Error('Imgur Client ID not configured');
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

    const formData = new FormData();
    formData.append('image', imageBuffer, {
      filename: 'beyhive-image.jpg',
      contentType: 'image/jpeg'
    });
    formData.append('title', title);
    formData.append('description', 'Uploaded via Beyhive Alert app');

    const response = await axios.post(`${IMGUR_API_URL}/image`, formData, {
      headers: {
        'Authorization': `Client-ID ${IMGUR_CLIENT_ID}`,
        ...formData.getHeaders()
      }
    });

    if (response.data && response.data.data) {
      return response.data.data.link; // Returns the direct image URL
    } else {
      throw new Error('Invalid response from Imgur API');
    }
  } catch (error) {
    console.error('Imgur upload error:', error);
    throw new Error('Failed to upload image to Imgur');
  }
}

// Delete image from Imgur (if needed)
async function deleteImage(imageHash) {
  if (!IMGUR_CLIENT_ID) {
    throw new Error('Imgur Client ID not configured');
  }

  try {
    await axios.delete(`${IMGUR_API_URL}/image/${imageHash}`, {
      headers: {
        'Authorization': `Client-ID ${IMGUR_CLIENT_ID}`
      }
    });
    return true;
  } catch (error) {
    console.error('Imgur delete error:', error);
    throw new Error('Failed to delete image from Imgur');
  }
}

module.exports = { uploadImage, deleteImage }; 