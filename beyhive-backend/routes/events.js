const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const EVENTS_FILE = path.join(__dirname, '../events.json');

function readEvents() {
  if (!fs.existsSync(EVENTS_FILE)) return [];
  return JSON.parse(fs.readFileSync(EVENTS_FILE, 'utf8'));
}

function writeEvents(events) {
  fs.writeFileSync(EVENTS_FILE, JSON.stringify(events, null, 2));
}

// GET all events
router.get('/', (req, res) => {
  const events = readEvents();
  res.json({ events });
});

// POST create a new event
router.post('/', (req, res) => {
  const events = readEvents();
  const event = req.body;
  events.push(event);
  writeEvents(events);
  res.json({ success: true, event });
});

// PUT update an event by id
router.put('/:id', (req, res) => {
  const events = readEvents();
  const idx = events.findIndex(e => e.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Event not found' });
  events[idx] = { ...events[idx], ...req.body };
  writeEvents(events);
  res.json({ success: true, event: events[idx] });
});

// DELETE an event by id
router.delete('/:id', (req, res) => {
  let events = readEvents();
  const idx = events.findIndex(e => e.id === req.params.id);
  if (idx === -1) return res.status(404).json({ error: 'Event not found' });
  events.splice(idx, 1);
  writeEvents(events);
  res.json({ success: true });
});

module.exports = router; 