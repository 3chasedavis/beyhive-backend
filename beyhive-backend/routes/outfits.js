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

// POST outfits (overwrite entire list)
router.post('/', (req, res) => {
  fs.writeFile(OUTFITS_FILE, JSON.stringify(req.body, null, 2), err => {
    if (err) return res.status(500).send('Error saving');
    res.send('OK');
  });
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