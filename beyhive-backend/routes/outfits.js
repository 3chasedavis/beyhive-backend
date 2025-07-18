const express = require('express');
const router = express.Router();
const Outfit = require('../models/Outfit');

// GET all outfits
router.get('/', async (req, res) => {
  try {
    const outfits = await Outfit.find();
    res.json({ outfits });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch outfits' });
  }
});

// POST create a new outfit
router.post('/', async (req, res) => {
  try {
    const outfit = new Outfit(req.body);
    await outfit.save();
    res.json({ success: true, outfit });
  } catch (err) {
    res.status(500).json({ error: 'Failed to create outfit' });
  }
});

// PUT update an outfit by id
router.put('/:id', async (req, res) => {
  try {
    const updated = await Outfit.findOneAndUpdate({ id: req.params.id }, req.body, { new: true });
    res.json({ success: true, outfit: updated });
  } catch (err) {
    res.status(500).json({ error: 'Failed to update outfit' });
  }
});

// DELETE an outfit by id
router.delete('/:id', async (req, res) => {
  try {
    await Outfit.findOneAndDelete({ id: req.params.id });
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete outfit' });
  }
});

module.exports = router; 