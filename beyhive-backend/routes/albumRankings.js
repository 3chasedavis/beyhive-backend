const express = require('express');
const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

const router = express.Router();

const RANKINGS_FILE = path.join(__dirname, '../albumRankings.json');

// Helper functions to read and write to the JSON file
function readRankings() {
    if (!fs.existsSync(RANKINGS_FILE)) {
        return [];
    }
    const data = fs.readFileSync(RANKINGS_FILE, 'utf8');
    return JSON.parse(data);
}

function writeRankings(data) {
    fs.writeFileSync(RANKINGS_FILE, JSON.stringify(data, null, 2));
}

// POST /api/album-rankings - Submit a new album ranking
router.post('/album-rankings', (req, res) => {
    const { nickname, ranking, userId } = req.body;

    if (!nickname || !ranking || !userId) {
        return res.status(400).json({ success: false, message: 'Nickname, ranking, and userId are required.' });
    }

    const allRankings = readRankings();

    // Prevent duplicate submissions by the same user
    if (allRankings.some(r => r.userId === userId)) {
        return res.status(409).json({ success: false, message: 'You have already submitted your ranking.' });
    }

    const newRanking = {
        id: uuidv4(),
        nickname,
        ranking,
        userId,
        likes: [],
        createdAt: new Date().toISOString()
    };

    allRankings.push(newRanking);
    writeRankings(allRankings);

    res.status(201).json({ success: true, message: 'Ranking submitted successfully.', ranking: newRanking });
});

// GET /api/album-rankings - Fetch all album rankings
router.get('/album-rankings', (req, res) => {
    const allRankings = readRankings();
    // Sort by most liked, then by newest
    allRankings.sort((a, b) => {
        if (b.likes.length !== a.likes.length) {
            return b.likes.length - a.likes.length;
        }
        return new Date(b.createdAt) - new Date(a.createdAt);
    });
    res.json({ success: true, rankings: allRankings });
});

// POST /api/album-rankings/:id/like - Like or unlike a ranking
router.post('/album-rankings/:id/like', (req, res) => {
    const { id } = req.params;
    const { userId } = req.body;

    if (!userId) {
        return res.status(400).json({ success: false, message: 'userId is required to like a ranking.' });
    }

    const allRankings = readRankings();
    const rankingIndex = allRankings.findIndex(r => r.id === id);

    if (rankingIndex === -1) {
        return res.status(404).json({ success: false, message: 'Ranking not found.' });
    }

    const ranking = allRankings[rankingIndex];
    const likeIndex = ranking.likes.indexOf(userId);

    if (likeIndex > -1) {
        // User has already liked, so unlike
        ranking.likes.splice(likeIndex, 1);
    } else {
        // User has not liked, so add like
        ranking.likes.push(userId);
    }

    allRankings[rankingIndex] = ranking;
    writeRankings(allRankings);

    res.json({ success: true, message: 'Like status updated.', ranking });
});

module.exports = router; 