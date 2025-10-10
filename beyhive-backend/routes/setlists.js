const express = require('express');
const router = express.Router();
const Setlist = require('../models/Setlist');

// GET all setlists
router.get('/', async (req, res) => {
  try {
    const setlists = await Setlist.find().sort({ 'songs.order': 1 });
    res.json({
      success: true,
      setlists: setlists
    });
  } catch (error) {
    console.error('Error fetching setlists:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch setlists'
    });
  }
});

// GET active setlists only
router.get('/active', async (req, res) => {
  try {
    const setlists = await Setlist.find({ isActive: true }).sort({ 'songs.order': 1 });
    res.json({
      success: true,
      setlists: setlists
    });
  } catch (error) {
    console.error('Error fetching active setlists:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch active setlists'
    });
  }
});

// GET single setlist by ID
router.get('/:id', async (req, res) => {
  try {
    const setlist = await Setlist.findOne({ id: req.params.id });
    if (!setlist) {
      return res.status(404).json({
        success: false,
        error: 'Setlist not found'
      });
    }
    res.json({
      success: true,
      setlist: setlist
    });
  } catch (error) {
    console.error('Error fetching setlist:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch setlist'
    });
  }
});

// POST create new setlist
router.post('/', async (req, res) => {
  try {
    const { id, title, songs, isActive } = req.body;
    
    // Validate required fields
    if (!id || !title || !songs || !Array.isArray(songs)) {
      return res.status(400).json({
        success: false,
        error: 'Missing required fields: id, title, songs'
      });
    }
    
    // Check if setlist with this ID already exists
    const existingSetlist = await Setlist.findOne({ id });
    if (existingSetlist) {
      return res.status(400).json({
        success: false,
        error: 'Setlist with this ID already exists'
      });
    }
    
    // Validate songs array
    for (let i = 0; i < songs.length; i++) {
      const song = songs[i];
      if (!song.name || typeof song.order !== 'number') {
        return res.status(400).json({
          success: false,
          error: `Invalid song at index ${i}: name and order are required`
        });
      }
    }
    
    const setlist = new Setlist({
      id,
      title,
      songs: songs.map((song, index) => ({
        name: song.name,
        order: song.order || index + 1,
        notes: song.notes || null
      })),
      isActive: isActive !== undefined ? isActive : true
    });
    
    await setlist.save();
    
    res.status(201).json({
      success: true,
      setlist: setlist,
      message: 'Setlist created successfully'
    });
  } catch (error) {
    console.error('Error creating setlist:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to create setlist'
    });
  }
});

// PUT update setlist
router.put('/:id', async (req, res) => {
  try {
    const { title, songs, isActive } = req.body;
    
    const setlist = await Setlist.findOne({ id: req.params.id });
    if (!setlist) {
      return res.status(404).json({
        success: false,
        error: 'Setlist not found'
      });
    }
    
    // Update fields if provided
    if (title) setlist.title = title;
    if (songs && Array.isArray(songs)) {
      // Validate songs array
      for (let i = 0; i < songs.length; i++) {
        const song = songs[i];
        if (!song.name || typeof song.order !== 'number') {
          return res.status(400).json({
            success: false,
            error: `Invalid song at index ${i}: name and order are required`
          });
        }
      }
      setlist.songs = songs.map((song, index) => ({
        name: song.name,
        order: song.order || index + 1,
        notes: song.notes || null
      }));
    }
    if (isActive !== undefined) setlist.isActive = isActive;
    
    await setlist.save();
    
    res.json({
      success: true,
      setlist: setlist,
      message: 'Setlist updated successfully'
    });
  } catch (error) {
    console.error('Error updating setlist:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to update setlist'
    });
  }
});

// DELETE setlist
router.delete('/:id', async (req, res) => {
  try {
    const setlist = await Setlist.findOneAndDelete({ id: req.params.id });
    if (!setlist) {
      return res.status(404).json({
        success: false,
        error: 'Setlist not found'
      });
    }
    
    res.json({
      success: true,
      message: 'Setlist deleted successfully'
    });
  } catch (error) {
    console.error('Error deleting setlist:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to delete setlist'
    });
  }
});

// POST toggle setlist active status
router.post('/:id/toggle', async (req, res) => {
  try {
    const setlist = await Setlist.findOne({ id: req.params.id });
    if (!setlist) {
      return res.status(404).json({
        success: false,
        error: 'Setlist not found'
      });
    }
    
    setlist.isActive = !setlist.isActive;
    await setlist.save();
    
    res.json({
      success: true,
      setlist: setlist,
      message: `Setlist ${setlist.isActive ? 'activated' : 'deactivated'} successfully`
    });
  } catch (error) {
    console.error('Error toggling setlist:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to toggle setlist'
    });
  }
});

module.exports = router;


