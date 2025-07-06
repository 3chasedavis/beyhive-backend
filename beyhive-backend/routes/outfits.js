const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

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

// POST new outfit
router.post('/', (req, res) => {
  const outfits = readOutfits();
  const { name, location, imageName, isNew, section, description } = req.body;
  if (!name || !location || !imageName || !section) return res.status(400).json({ error: 'Name, location, imageName, and section required' });
  const newOutfit = {
    id: Date.now().toString(),
    name,
    location,
    imageName,
    isNew: !!isNew,
    section,
    description: description || null
  };
  outfits.push(newOutfit);
  writeOutfits(outfits);
  res.json(newOutfit);
});

// PUT update outfit
router.put('/:id', (req, res) => {
  const outfits = readOutfits();
  const idx = outfits.findIndex(o => o.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
  const { name, location, imageUrl, isNew } = req.body;
  outfits[idx] = { ...outfits[idx], name, location, imageUrl, isNew: !!isNew };
  writeOutfits(outfits);
  res.json(outfits[idx]);
});

// DELETE outfit
router.delete('/:id', (req, res) => {
  let outfits = readOutfits();
  const idx = outfits.findIndex(o => o.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
  const removed = outfits.splice(idx, 1);
  writeOutfits(outfits);
  res.json(removed[0]);
});

module.exports = router; 