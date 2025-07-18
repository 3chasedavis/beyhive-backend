const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const OUTFITS_FILE = path.join(__dirname, '../outfits.json');

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

// POST create a new outfit
router.post('/', (req, res) => {
  const outfits = readOutfits();
  const outfit = req.body;
  outfits.push(outfit);
  writeOutfits(outfits);
  res.json({ success: true, outfit });
});

// PUT update an outfit by id
router.put('/:id', (req, res) => {
  const outfits = readOutfits();
  const idx = outfits.findIndex(o => o.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Outfit not found' });
  outfits[idx] = { ...outfits[idx], ...req.body };
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