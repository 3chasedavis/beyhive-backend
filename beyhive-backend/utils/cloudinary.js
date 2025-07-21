const cloudinary = require('cloudinary').v2;

// Configure Cloudinary
cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET
});

// Check if Cloudinary is available (not in maintenance mode)
function isCloudinaryAvailable() {
  return process.env.CLOUDINARY_MAINTENANCE_MODE !== 'true' && 
         process.env.CLOUDINARY_CLOUD_NAME && 
         process.env.CLOUDINARY_API_KEY && 
         process.env.CLOUDINARY_API_SECRET;
}

// Upload image to Cloudinary
async function uploadImage(file, folder = 'beyhive-outfits') {
  if (!isCloudinaryAvailable()) {
    throw new Error('Cloudinary is currently unavailable. Please try again later.');
  }
  
  try {
    const result = await cloudinary.uploader.upload(file, {
      folder: folder,
      resource_type: 'auto'
    });
    return result.secure_url;
  } catch (error) {
    console.error('Cloudinary upload error:', error);
    
    // Check if it's a quota limit error
    if (error.message && error.message.includes('quota')) {
      throw new Error('Image upload quota limit reached. Please contact support.');
    }
    
    throw error;
  }
}

// Delete image from Cloudinary
async function deleteImage(publicId) {
  if (!isCloudinaryAvailable()) {
    throw new Error('Cloudinary is currently unavailable. Please try again later.');
  }
  
  try {
    const result = await cloudinary.uploader.destroy(publicId);
    return result;
  } catch (error) {
    console.error('Cloudinary delete error:', error);
    throw error;
  }
}

module.exports = { uploadImage, deleteImage, isCloudinaryAvailable }; 