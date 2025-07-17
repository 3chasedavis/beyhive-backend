const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const PARTNERS_FILE = path.join(__dirname, '../partners.json');

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

module.exports = router; 