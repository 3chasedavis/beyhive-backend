const express = require('express');
const fs = require('fs');
const path = require('path');
const multer = require('multer');
const { uploadImage } = require('../utils/localStorage');
const router = express.Router();

const OUTFITS_FILE = path.join(__dirname, '../outfits.json');
const upload = multer({ storage: multer.memoryStorage() });

function readOutfits() {
  if (!fs.existsSync(OUTFITS_FILE)) return [];
  return JSON.parse(fs.readFileSync(OUTFITS_FILE, 'utf8'));
}

function writeOutfits(outfits) {
  fs.writeFileSync(OUTFITS_FILE, JSON.stringify(outfits, null, 2));
}

// GET all outfits
router.get('/', (req, res) => {
  const outfits = readOutfits();
  res.json({ outfits, success: true, message: null });
});

// POST create a new outfit with image upload
router.post('/', upload.single('image'), async (req, res) => {
  const outfits = readOutfits();
  let imageUrl = null;
  
  if (req.file) {
    try {
      const base64Image = `data:${req.file.mimetype};base64,${req.file.buffer.toString('base64')}`;
      imageUrl = await uploadImage(base64Image, 'beyhive-outfits');
    } catch (error) {
      console.error('Local storage upload failed:', error);
      // Continue without image rather than failing the entire request
      imageUrl = null;
    }
  }
  
  const outfit = {
    ...req.body,
    imageUrl: imageUrl || null,
    isNew: req.body.isNew === 'true' || req.body.isNew === true,
  };
  outfits.unshift(outfit); // Add to beginning instead of end
  writeOutfits(outfits);
  res.json({ success: true, outfit });
});

// PUT update an outfit by id with image upload
router.put('/:id', upload.single('image'), async (req, res) => {
  const outfits = readOutfits();
  const idx = outfits.findIndex(o => o.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
  
  let imageUrl = outfits[idx].imageUrl;
  if (req.file) {
    try {
      const base64Image = `data:${req.file.mimetype};base64,${req.file.buffer.toString('base64')}`;
      imageUrl = await uploadImage(base64Image, 'beyhive-outfits');
    } catch (error) {
      console.error('Local storage upload failed:', error);
      // Keep existing image URL if upload fails
      imageUrl = outfits[idx].imageUrl;
    }
  }
  
  outfits[idx] = {
    ...outfits[idx],
    ...req.body,
    imageUrl: imageUrl || null,
    isNew: req.body.isNew === 'true' || req.body.isNew === true,
  };
  writeOutfits(outfits);
  res.json({ success: true, outfit: outfits[idx] });
});

// DELETE an outfit by id
router.delete('/:id', (req, res) => {
  let outfits = readOutfits();
  const idx = outfits.findIndex(o => o.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
  outfits.splice(idx, 1);
  writeOutfits(outfits);
  res.json({ success: true });
});

module.exports = router; 