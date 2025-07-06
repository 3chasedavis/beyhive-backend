const express = require('express');
const router = express.Router();
const Event = require('../models/Event');

// GET all events
router.get('/', async (req, res) => {
  try {
    const events = await Event.find().sort({ date: 1, time: 1 });
    // Map MongoDB _id to id and fill in defaults for all required fields
    const mappedEvents = events.map(e => ({
      id: e._id.toString(),
      title: e.title || '',
      description: e.description || '',
      date: e.date || '',
      location: e.location || '',
      createdAt: e.createdAt || new Date(),
      time: e.time || '',
      timezone: e.timezone || 'America/New_York'
    }));
    res.json({
      events: mappedEvents,
      success: true,
      message: 'Events fetched successfully'
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch events' });
  }
});

// POST new event
router.post('/', async (req, res) => {
  try {
    let { title, date, time, location, timezone, description } = req.body;
    if (!title || !date) return res.status(400).json({ error: 'Title and date required' });
    // Provide defaults for missing fields
    if (!timezone) timezone = 'America/New_York';
    if (!description) description = '';
    const newEvent = new Event({ title, date, time, location, timezone, description });
    await newEvent.save();
    res.json(newEvent);
  } catch (err) {
    res.status(500).json({ error: 'Failed to add event' });
  }
});

// PUT update event
router.put('/:id', async (req, res) => {
  try {
    const { title, date, time, location, timezone, description } = req.body;
    const updated = await Event.findByIdAndUpdate(
      req.params.id,
      { title, date, time, location, timezone, description },
      { new: true }
    );
    if (!updated) return res.status(404).json({ error: 'Event not found' });
    res.json(updated);
  } catch (err) {
    res.status(500).json({ error: 'Failed to update event' });
  }
});

// DELETE event
router.delete('/:id', async (req, res) => {
  try {
    const removed = await Event.findByIdAndDelete(req.params.id);
    if (!removed) return res.status(404).json({ error: 'Event not found' });
    res.json(removed);
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete event' });
  }
});

// GET calendar URL (unchanged)
router.get('/calendar-url', (req, res) => {
  res.json({
    calendarUrl: 'https://beyhive-backend.onrender.com/calendar.html',
    success: true,
    message: 'Calendar URL fetched successfully'
  });
});

module.exports = router; 