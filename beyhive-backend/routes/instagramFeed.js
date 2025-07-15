const express = require('express');
const router = express.Router();

const instagramFeed = [
  {
    title: "Beyoncé Official",
    url: "https://www.instagram.com/beyonce/",
    image: "https://upload.wikimedia.org/wikipedia/commons/0/0c/Beyonce_-_The_Formati...jpg", // Placeholder image
    description: "Official Beyoncé Instagram"
  },
  {
    title: "Beyoncé Updates",
    url: "https://www.instagram.com/beyonceupdatesz/?hl=en",
    image: "https://upload.wikimedia.org/wikipedia/commons/0/0c/Beyonce_-_The_Formati...jpg", // Placeholder image
    description: "Beyoncé Updates Instagram"
  },
  {
    title: "Arionce",
    url: "https://www.instagram.com/arionce.lifee/",
    image: "https://upload.wikimedia.org/wikipedia/commons/0/0c/Beyonce_-_The_Formati...jpg", // Placeholder image
    description: "Arionce Instagram"
  }
];

router.get('/', (req, res) => {
  res.json(instagramFeed);
});

module.exports = router; 