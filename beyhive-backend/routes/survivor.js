const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const SURVIVOR_FILE = path.join(__dirname, '../survivor.json');
const RESPONSES_FILE = path.join(__dirname, '../quizResponses.json');

function readResponses() {
  if (!fs.existsSync(RESPONSES_FILE)) return [];
  return JSON.parse(fs.readFileSync(RESPONSES_FILE, 'utf8'));
}
function writeResponses(responses) {
  fs.writeFileSync(RESPONSES_FILE, JSON.stringify(responses, null, 2));
}

// GET / - returns quiz data
router.get('/', (req, res) => {
  let quizzes = [];
  if (fs.existsSync(SURVIVOR_FILE)) {
    try {
      quizzes = JSON.parse(fs.readFileSync(SURVIVOR_FILE, 'utf8'));
    } catch (e) {
      return res.status(500).json({ success: false, message: 'Error reading quiz data', error: e.message });
    }
  }
  res.json({ success: true, quizzes });
});

// GET /:quizId - returns a single quiz by id
router.get('/:quizId', (req, res) => {
  const { quizId } = req.params;
  let quizzes = [];
  if (fs.existsSync(SURVIVOR_FILE)) {
    try {
      quizzes = JSON.parse(fs.readFileSync(SURVIVOR_FILE, 'utf8'));
    } catch (e) {
      return res.status(500).json({ success: false, message: 'Error reading quiz data', error: e.message });
    }
  }
  const quiz = quizzes.find(q => q.id === quizId);
  if (!quiz) {
    return res.status(404).json({ success: false, message: 'Quiz not found' });
  }
  res.json({ success: true, quiz });
});

// PUT /:quizId - update openAt/closeAt for a quiz
router.put('/:quizId', (req, res) => {
  const { quizId } = req.params;
  const { openAt, closeAt } = req.body;

  console.log(`[DEBUG] Updating quiz. ID: ${quizId}`);
  console.log(`[DEBUG] Received openAt: ${openAt}, closeAt: ${closeAt}`);

  let quizzes = [];
  if (fs.existsSync(SURVIVOR_FILE)) {
    try {
      quizzes = JSON.parse(fs.readFileSync(SURVIVOR_FILE, 'utf8'));
    } catch (e) {
      return res.status(500).json({ success: false, message: 'Error reading quiz data', error: e.message });
    }
  }
  const idx = quizzes.findIndex(q => q.id === quizId);
  if (idx === -1) {
    return res.status(404).json({ success: false, message: 'Quiz not found' });
  }
  
  console.log('[DEBUG] Found quiz to update:', quizzes[idx]);

  if (openAt) {
    quizzes[idx].openAt = openAt;
  } else {
    quizzes[idx].openAt = null;
  }
  
  if (closeAt) {
    quizzes[idx].closeAt = closeAt;
  } else {
    quizzes[idx].closeAt = null;
  }
  
  console.log('[DEBUG] Updated quiz data:', quizzes[idx]);

  fs.writeFileSync(SURVIVOR_FILE, JSON.stringify(quizzes, null, 2));
  res.json({ success: true, quiz: quizzes[idx] });
});

// POST: Save a user's quiz response
router.post('/response', (req, res) => {
  const { quizId, userId, answers } = req.body;
  if (!quizId || !userId || !answers) {
    return res.status(400).json({ success: false, message: 'Missing required fields' });
  }
  let responses = readResponses();
  // Check if a response already exists for this quizId/userId
  const alreadySubmitted = responses.some(r => r.quizId === quizId && r.userId === userId);
  if (alreadySubmitted) {
    return res.status(409).json({ success: false, message: 'You have already submitted answers for this quiz.' });
  }
  responses.push({
    quizId,
    userId,
    answers,
    submittedAt: new Date().toISOString()
  });
  writeResponses(responses);
  res.json({ success: true, message: 'Your answers have been submitted successfully.' });
});

// GET: Retrieve a user's quiz response
router.get('/response/:quizId', (req, res) => {
  const { quizId } = req.params;
  const { userId } = req.query;
  if (!quizId || !userId) {
    return res.status(400).json({ success: false, message: 'Missing quizId or userId' });
  }
  const responses = readResponses();
  const response = responses.find(r => r.quizId === quizId && r.userId === userId);
  if (!response) {
    return res.json({ success: true, response: null });
  }
  res.json({ success: true, response });
});

module.exports = router; 