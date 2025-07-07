const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const DATA_FILE = path.join(__dirname, '../survivor.json');

function readGames() {
  if (!fs.existsSync(DATA_FILE)) return [];
  return JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
}
function writeGames(games) {
  fs.writeFileSync(DATA_FILE, JSON.stringify(games, null, 2));
}

// GET all games
router.get('/games', (req, res) => {
  res.json(readGames());
});

// POST new game
router.post('/games', (req, res) => {
  const games = readGames();
  const { name, status, isNew } = req.body;
  const id = Date.now().toString();
  const newGame = { id, name, status, isNew: !!isNew, questions: [], leaderboard: [] };
  games.push(newGame);
  writeGames(games);
  res.json(newGame);
});

// PUT update game
router.put('/games/:id', (req, res) => {
  const games = readGames();
  const idx = games.findIndex(g => g.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Game not found' });
  const { name, status, isNew, leaderboard } = req.body;
  if (name !== undefined) games[idx].name = name;
  if (status !== undefined) games[idx].status = status;
  if (isNew !== undefined) games[idx].isNew = isNew;
  if (leaderboard !== undefined) games[idx].leaderboard = leaderboard;
  writeGames(games);
  res.json(games[idx]);
});

// DELETE game
router.delete('/games/:id', (req, res) => {
  let games = readGames();
  games = games.filter(g => g.id !== req.params.id);
  writeGames(games);
  res.json({ success: true });
});

// POST add question
router.post('/games/:id/questions', (req, res) => {
  const games = readGames();
  const idx = games.findIndex(g => g.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Game not found' });
  const { text, points, choices, correctAnswer } = req.body;
  const qid = Date.now().toString();
  const question = { id: qid, text, points, choices, correctAnswer };
  games[idx].questions.push(question);
  writeGames(games);
  res.json(question);
});

// PUT update question
router.put('/games/:id/questions/:qid', (req, res) => {
  const games = readGames();
  const idx = games.findIndex(g => g.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Game not found' });
  const qidx = games[idx].questions.findIndex(q => q.id === req.params.qid);
  if (qidx === -1) return res.status(404).json({ error: 'Question not found' });
  const { text, points, choices, correctAnswer } = req.body;
  if (text !== undefined) games[idx].questions[qidx].text = text;
  if (points !== undefined) games[idx].questions[qidx].points = points;
  if (choices !== undefined) games[idx].questions[qidx].choices = choices;
  if (correctAnswer !== undefined) games[idx].questions[qidx].correctAnswer = correctAnswer;
  writeGames(games);
  res.json(games[idx].questions[qidx]);
});

// DELETE question
router.delete('/games/:id/questions/:qid', (req, res) => {
  const games = readGames();
  const idx = games.findIndex(g => g.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Game not found' });
  games[idx].questions = games[idx].questions.filter(q => q.id !== req.params.qid);
  writeGames(games);
  res.json({ success: true });
});

module.exports = router; 