const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const EVENTS_FILE = path.join(__dirname, '../events.json');

// Helper to read events
function readEvents() {
  if (!fs.existsSync(EVENTS_FILE)) return [];
  return JSON.parse(fs.readFileSync(EVENTS_FILE, 'utf8'));
}
// Helper to write events
function writeEvents(events) {
  fs.writeFileSync(EVENTS_FILE, JSON.stringify(events, null, 2));
}

// GET all events
router.get('/', (req, res) => {
  const events = readEvents();
  res.json({
    events: events,
    success: true,
    message: 'Events fetched successfully'
  });
});

// POST new event
router.post('/', (req, res) => {
  const events = readEvents();
  const { title, date, description } = req.body;
  if (!title || !date) return res.status(400).json({ error: 'Title and date required' });
  const newEvent = { id: Date.now().toString(), title, date, description };
  events.push(newEvent);
  writeEvents(events);
  res.json(newEvent);
});

// PUT update event
router.put('/:id', (req, res) => {
  const events = readEvents();
  const idx = events.findIndex(e => e.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Event not found' });
  const { title, date, description } = req.body;
  events[idx] = { ...events[idx], title, date, description };
  writeEvents(events);
  res.json(events[idx]);
});

// DELETE event
router.delete('/:id', (req, res) => {
  let events = readEvents();
  const idx = events.findIndex(e => e.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Event not found' });
  const removed = events.splice(idx, 1);
  writeEvents(events);
  res.json(removed[0]);
});

module.exports = router; 