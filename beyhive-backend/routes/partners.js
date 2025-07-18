const express = require('express');
const fs = require('fs');
const path = require('path');
const multer = require('multer');
const { uploadImage } = require('../utils/cloudinary');
const router = express.Router();

const PARTNERS_FILE = path.join(__dirname, '../partners.json');
const upload = multer({ storage: multer.memoryStorage() });

function readPartners() {
  if (!fs.existsSync(PARTNERS_FILE)) return [];
  return JSON.parse(fs.readFileSync(PARTNERS_FILE, 'utf8'));
}

function writePartners(partners) {
  fs.writeFileSync(PARTNERS_FILE, JSON.stringify(partners, null, 2));
}

// GET all partners
router.get('/', (req, res) => {
  const partners = readPartners();
  res.json({ success: true, partners });
});

// POST create a new partner with image upload
router.post('/', upload.single('icon'), async (req, res) => {
  const partners = readPartners();
  let iconUrl = null;
  if (req.file) {
    const base64Image = `data:${req.file.mimetype};base64,${req.file.buffer.toString('base64')}`;
    iconUrl = await uploadImage(base64Image);
  }
  const partner = {
    ...req.body,
    iconUrl: iconUrl || null,
  };
  partners.push(partner);
  writePartners(partners);
  res.json({ success: true, partner });
});

// PUT update a partner by index with image upload
router.put('/:index', upload.single('icon'), async (req, res) => {
  const partners = readPartners();
  const idx = parseInt(req.params.index, 10);
  if (isNaN(idx) || idx < 0 || idx >= partners.length) return res.status(404).json({ error: 'Partner not found' });
  let iconUrl = partners[idx].iconUrl;
  if (req.file) {
    const base64Image = `data:${req.file.mimetype};base64,${req.file.buffer.toString('base64')}`;
    iconUrl = await uploadImage(base64Image);
  }
  partners[idx] = {
    ...partners[idx],
    ...req.body,
    iconUrl: iconUrl || null,
  };
  writePartners(partners);
  res.json({ success: true, partner: partners[idx] });
});

// DELETE a partner by index
router.delete('/:index', (req, res) => {
  let partners = readPartners();
  const idx = parseInt(req.params.index, 10);
  if (isNaN(idx) || idx < 0 || idx >= partners.length) return res.status(404).json({ error: 'Partner not found' });
  partners.splice(idx, 1);
  writePartners(partners);
  res.json({ success: true });
});

module.exports = router; 