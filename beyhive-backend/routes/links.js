const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

const LINKS_FILE = path.join(__dirname, '..', 'links.json');

// Helper function to load links from JSON file
function loadLinks() {
    if (fs.existsSync(LINKS_FILE)) {
        try {
            const data = fs.readFileSync(LINKS_FILE, 'utf8');
            return JSON.parse(data);
        } catch (error) {
            console.error('Error reading links file:', error);
            return [];
        }
    }
    return [];
}

// Helper function to save links to JSON file
function saveLinks(links) {
    try {
        fs.writeFileSync(LINKS_FILE, JSON.stringify(links, null, 2));
        return true;
    } catch (error) {
        console.error('Error saving links file:', error);
        return false;
    }
}

// GET /api/links - Get all links
router.get('/', (req, res) => {
    try {
        const links = loadLinks();
        res.json(links);
    } catch (error) {
        console.error('Error fetching links:', error);
        res.status(500).json({ error: 'Failed to fetch links' });
    }
});

// POST /api/links - Create a new link
router.post('/', (req, res) => {
    try {
        const { title, url } = req.body;
        
        if (!title || !url) {
            return res.status(400).json({ 
                success: false, 
                message: 'Title and URL are required' 
            });
        }
        
        const links = loadLinks();
        const newLink = {
            id: Date.now().toString(), // Simple ID generation
            title: title,
            url: url,
            createdAt: new Date().toISOString()
        };
        
        links.push(newLink);
        
        if (saveLinks(links)) {
            res.json({ 
                success: true, 
                message: 'Link created successfully',
                link: newLink
            });
        } else {
            res.status(500).json({ 
                success: false, 
                message: 'Failed to save link' 
            });
        }
    } catch (error) {
        console.error('Error creating link:', error);
        res.status(500).json({ 
            success: false, 
            message: 'Failed to create link' 
        });
    }
});

// PUT /api/links/:id - Update a link
router.put('/:id', (req, res) => {
    try {
        const { id } = req.params;
        const { title, url } = req.body;
        
        if (!title || !url) {
            return res.status(400).json({ 
                success: false, 
                message: 'Title and URL are required' 
            });
        }
        
        const links = loadLinks();
        const linkIndex = links.findIndex(link => link.id === id);
        
        if (linkIndex === -1) {
            return res.status(404).json({ 
                success: false, 
                message: 'Link not found' 
            });
        }
        
        links[linkIndex] = {
            ...links[linkIndex],
            title: title,
            url: url,
            updatedAt: new Date().toISOString()
        };
        
        if (saveLinks(links)) {
            res.json({ 
                success: true, 
                message: 'Link updated successfully',
                link: links[linkIndex]
            });
        } else {
            res.status(500).json({ 
                success: false, 
                message: 'Failed to save link' 
            });
        }
    } catch (error) {
        console.error('Error updating link:', error);
        res.status(500).json({ 
            success: false, 
            message: 'Failed to update link' 
        });
    }
});

// DELETE /api/links/:id - Delete a link
router.delete('/:id', (req, res) => {
    try {
        const { id } = req.params;
        const links = loadLinks();
        const linkIndex = links.findIndex(link => link.id === id);
        
        if (linkIndex === -1) {
            return res.status(404).json({ 
                success: false, 
                message: 'Link not found' 
            });
        }
        
        links.splice(linkIndex, 1);
        
        if (saveLinks(links)) {
            res.json({ 
                success: true, 
                message: 'Link deleted successfully' 
            });
        } else {
            res.status(500).json({ 
                success: false, 
                message: 'Failed to save links' 
            });
        }
    } catch (error) {
        console.error('Error deleting link:', error);
        res.status(500).json({ 
            success: false, 
            message: 'Failed to delete link' 
        });
    }
});

module.exports = router;
