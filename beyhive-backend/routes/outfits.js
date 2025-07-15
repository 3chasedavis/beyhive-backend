const express = require('express');
const fs = require('fs');
const path = require('path');
const multer = require('multer');
const { uploadImage, deleteImage } = require('../utils/cloudinary');
const router = express.Router();

// Configure multer for handling file uploads
const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024 // 5MB limit
  }
});

const OUTFITS_FILE = path.join(__dirname, '../outfits.json');

// Helper to read outfits
function readOutfits() {
  if (!fs.existsSync(OUTFITS_FILE)) return [];
  return JSON.parse(fs.readFileSync(OUTFITS_FILE, 'utf8'));
}

// Helper to write outfits
function writeOutfits(outfits) {
  fs.writeFileSync(OUTFITS_FILE, JSON.stringify(outfits, null, 2));
}

// GET all outfits
router.get('/', (req, res) => {
  const outfits = readOutfits();
  res.json({
    outfits: outfits,
    success: true,
    message: 'Outfits fetched successfully'
  });
});

// POST new outfit with image upload
router.post('/', upload.single('image'), async (req, res) => {
  try {
    const outfits = readOutfits();
    const { name, location, isNew, section, description } = req.body;
    
    if (!name || !location || !section) {
      return res.status(400).json({ error: 'Name, location, and section required' });
    }

    let imageUrl = null;
    
    // If image file is uploaded, upload to Cloudinary
    if (req.file) {
      const base64Image = req.file.buffer.toString('base64');
      const dataURI = `data:${req.file.mimetype};base64,${base64Image}`;
      imageUrl = await uploadImage(dataURI);
    }

    const newOutfit = {
      id: Date.now().toString(),
      name,
      location,
      imageUrl, // Store Cloudinary URL instead of local asset name
      isNew: !!isNew,
      section,
      description: description || null
    };
    
    outfits.push(newOutfit);
    writeOutfits(outfits);
    res.json(newOutfit);
  } catch (error) {
    console.error('Error creating outfit:', error);
    res.status(500).json({ error: 'Failed to create outfit' });
  }
});

// PUT update outfit
router.put('/:id', upload.single('image'), async (req, res) => {
  try {
    const outfits = readOutfits();
    const idx = outfits.findIndex(o => o.id === req.params.id);
    if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
    
    const { name, location, isNew, section, description } = req.body;
    let imageUrl = outfits[idx].imageUrl; // Keep existing image URL
    
    // If new image is uploaded, upload to Cloudinary
    if (req.file) {
      const base64Image = req.file.buffer.toString('base64');
      const dataURI = `data:${req.file.mimetype};base64,${base64Image}`;
      imageUrl = await uploadImage(dataURI);
    }
    
    outfits[idx] = { 
      ...outfits[idx], 
      name, 
      location, 
      imageUrl, 
      isNew: !!isNew, 
      section, 
      description 
    };
    
    writeOutfits(outfits);
    res.json(outfits[idx]);
  } catch (error) {
    console.error('Error updating outfit:', error);
    res.status(500).json({ error: 'Failed to update outfit' });
  }
});

// DELETE outfit
router.delete('/:id', async (req, res) => {
  try {
    let outfits = readOutfits();
    const idx = outfits.findIndex(o => o.id === req.params.id);
    if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
    
    const removed = outfits.splice(idx, 1);
    writeOutfits(outfits);
    res.json(removed[0]);
  } catch (error) {
    console.error('Error deleting outfit:', error);
    res.status(500).json({ error: 'Failed to delete outfit' });
  }
});

module.exports = router; 