const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const PARTNERS_FILE = path.join(__dirname, '../partners.json');

function readPartners() {
  if (!fs.existsSync(PARTNERS_FILE)) return [];
  return JSON.parse(fs.readFileSync(PARTNERS_FILE, 'utf8'));
}

function writePartners(partners) {
  fs.writeFileSync(PARTNERS_FILE, JSON.stringify(partners, null, 2));
}

// GET / - returns partners data
router.get('/', (req, res) => {
  let partners = [];
  if (fs.existsSync(PARTNERS_FILE)) {
    try {
      partners = JSON.parse(fs.readFileSync(PARTNERS_FILE, 'utf8'));
    } catch (e) {
      return res.status(500).json({ success: false, message: 'Error reading partners data', error: e.message });
    }
  }
  res.json({ success: true, partners });
});

// POST / - add a new partner
router.post('/', (req, res) => {
  const { name, description, icon, link } = req.body;
  if (!name || !description || !icon || !link) {
    return res.status(400).json({ success: false, message: 'Missing required fields' });
  }
  
  let partners = readPartners();
  partners.push({ name, description, icon, link });
  writePartners(partners);
  
  res.json({ success: true, message: 'Partner added successfully' });
});

// PUT / - update an existing partner
router.put('/', (req, res) => {
  const { index, name, description, icon, link } = req.body;
  if (index === undefined || !name || !description || !icon || !link) {
    return res.status(400).json({ success: false, message: 'Missing required fields' });
  }
  
  let partners = readPartners();
  if (index < 0 || index >= partners.length) {
    return res.status(404).json({ success: false, message: 'Partner not found' });
  }
  
  partners[index] = { name, description, icon, link };
  writePartners(partners);
  
  res.json({ success: true, message: 'Partner updated successfully' });
});

// DELETE / - delete a partner
router.delete('/', (req, res) => {
  const { index } = req.body;
  if (index === undefined) {
    return res.status(400).json({ success: false, message: 'Index is required' });
  }
  
  let partners = readPartners();
  if (index < 0 || index >= partners.length) {
    return res.status(404).json({ success: false, message: 'Partner not found' });
  }
  
  partners.splice(index, 1);
  writePartners(partners);
  
  res.json({ success: true, message: 'Partner deleted successfully' });
});

module.exports = router; 