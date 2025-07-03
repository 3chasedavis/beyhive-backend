const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const DATA_FILE = path.join(__dirname, '../livestreams.json');

// GET livestreams
router.get('/', (req, res) => {
  fs.readFile(DATA_FILE, (err, data) => {
    if (err) return res.json([]);
    try {
      res.json(JSON.parse(data));
    } catch {
      res.json([]);
    }
  });
});

// POST livestreams
router.post('/', (req, res) => {
  fs.writeFile(DATA_FILE, JSON.stringify(req.body, null, 2), err => {
    if (err) return res.status(500).send('Error saving');
    res.send('OK');
  });
});

module.exports = router; 