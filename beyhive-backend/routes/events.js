const express = require('express');
const router = express.Router();
const Event = require('../models/Event');

// GET all events
router.get('/', async (req, res) => {
  try {
    const events = await Event.find();
    // Map to plain JS objects and ensure 'id' is present
    const formatted = events.map(e => ({
      id: e.id,
      title: e.title,
      date: e.date,
      time: e.time,
      location: e.location,
      timezone: e.timezone,
      description: e.description
    }));
    res.json({ events: formatted });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch events' });
  }
});

// POST create a new event
router.post('/', async (req, res) => {
  try {
    const event = new Event(req.body);
    await event.save();
    res.json({ success: true, event });
  } catch (err) {
    res.status(500).json({ error: 'Failed to create event' });
  }
});

// PUT update an event by id
router.put('/:id', async (req, res) => {
  try {
    const updated = await Event.findOneAndUpdate({ id: req.params.id }, req.body, { new: true });
    res.json({ success: true, event: updated });
  } catch (err) {
    res.status(500).json({ error: 'Failed to update event' });
  }
});

// DELETE an event by id
router.delete('/:id', async (req, res) => {
  try {
    await Event.findOneAndDelete({ id: req.params.id });
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete event' });
  }
});

module.exports = router; 