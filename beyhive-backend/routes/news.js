const express = require('express');
const router = express.Router();

// Example news feed data (replace or update as needed)
const newsFeed = [
  {
    title: "Beyoncé Announces New Tour Date!",
    url: "https://beyonce.com/tour",
    image: "https://yourcdn.com/images/tour.jpg",
    date: "2025-07-15T12:00:00Z"
  },
  {
    title: "Survivor Game Results Are In!",
    url: "https://yourapp.com/survivor-results",
    image: "https://yourcdn.com/images/survivor.jpg",
    date: "2025-07-14T18:00:00Z"
  }
];

router.get('/', (req, res) => {
  res.json(newsFeed);
});

module.exports = router; 