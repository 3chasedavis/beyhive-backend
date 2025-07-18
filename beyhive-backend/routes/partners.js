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
router.post('/', upload.single('icon'), async (req, res) => {
  try {
    const { name, description, link } = req.body;
    if (!name || !description || !link) {
      return res.status(400).json({ success: false, message: 'Missing required fields' });
    }
    
    let iconUrl = null;
    
    // If icon file is uploaded, upload to Cloudinary
    if (req.file) {
      const base64Image = req.file.buffer.toString('base64');
      const dataURI = `data:${req.file.mimetype};base64,${base64Image}`;
      iconUrl = await uploadImage(dataURI);
    }
    
    let partners = readPartners();
    partners.push({ name, description, iconUrl, link });
    writePartners(partners);
    
    res.json({ success: true, message: 'Partner added successfully' });
  } catch (error) {
    console.error('Error creating partner:', error);
    res.status(500).json({ success: false, message: 'Failed to create partner' });
  }
});

// PUT / - update an existing partner
router.put('/', upload.single('icon'), async (req, res) => {
  try {
    const { index, name, description, link } = req.body;
    if (index === undefined || !name || !description || !link) {
      return res.status(400).json({ success: false, message: 'Missing required fields' });
    }
    
    let partners = readPartners();
    if (index < 0 || index >= partners.length) {
      return res.status(404).json({ success: false, message: 'Partner not found' });
    }
    
    let iconUrl = partners[index].iconUrl; // Keep existing icon URL
    
    // If new icon is uploaded, upload to Cloudinary
    if (req.file) {
      const base64Image = req.file.buffer.toString('base64');
      const dataURI = `data:${req.file.mimetype};base64,${base64Image}`;
      iconUrl = await uploadImage(dataURI);
    }
    
    partners[index] = { name, description, iconUrl, link };
    writePartners(partners);
    
    res.json({ success: true, message: 'Partner updated successfully' });
  } catch (error) {
    console.error('Error updating partner:', error);
    res.status(500).json({ success: false, message: 'Failed to update partner' });
  }
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