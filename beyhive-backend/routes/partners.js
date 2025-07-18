const express = require('express');
const router = express.Router();
const Partner = require('../models/Partner');

// GET all partners
router.get('/', async (req, res) => {
  try {
    const partners = await Partner.find();
    res.json({ success: true, partners });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch partners' });
  }
});

// POST create a new partner
router.post('/', async (req, res) => {
  try {
    const partner = new Partner(req.body);
    await partner.save();
    res.json({ success: true, partner });
  } catch (err) {
    res.status(500).json({ error: 'Failed to create partner' });
  }
});

// PUT update a partner by id
router.put('/:id', async (req, res) => {
  try {
    const updated = await Partner.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json({ success: true, partner: updated });
  } catch (err) {
    res.status(500).json({ error: 'Failed to update partner' });
  }
});

// DELETE a partner by id
router.delete('/:id', async (req, res) => {
  try {
    await Partner.findByIdAndDelete(req.params.id);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete partner' });
  }
});

module.exports = router; 