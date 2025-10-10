const express = require('express');
const fs = require('fs');
const path = require('path');
const router = express.Router();

// File to store version info
const VERSION_FILE = path.join(__dirname, '..', 'version.json');

// Load version info from file or use defaults
function loadVersionInfo() {
    try {
        if (fs.existsSync(VERSION_FILE)) {
            return JSON.parse(fs.readFileSync(VERSION_FILE, 'utf8'));
        }
    } catch (error) {
        console.error('Error loading version info:', error);
    }
    
    // Default version info
    return {
        latestVersion: "1.1.0",
        minimumVersion: "1.0.0",
        updateMessage: "🐝 New features and bug fixes! Update now to get the latest Beyoncé tour updates and improved notifications.",
        forceUpdate: false,
        releaseNotes: [
            "✨ New album ranking game",
            "🎵 Enhanced music notifications", 
            "🐝 Improved bee icon animations",
            "🔧 Bug fixes and performance improvements"
        ]
    };
}

// Save version info to file
function saveVersionInfo(versionInfo) {
    try {
        fs.writeFileSync(VERSION_FILE, JSON.stringify(versionInfo, null, 2));
        return true;
    } catch (error) {
        console.error('Error saving version info:', error);
        return false;
    }
}

// Version check endpoint
router.get('/version-check', (req, res) => {
    try {
        const versionInfo = loadVersionInfo();
        res.json(versionInfo);
    } catch (error) {
        console.error('Error in version check:', error);
        res.status(500).json({ 
            error: 'Failed to check version',
            latestVersion: "1.0.0",
            forceUpdate: false
        });
    }
});

// Update version endpoint (for admin panel)
router.post('/update-version', (req, res) => {
    try {
        const { latestVersion, updateMessage, forceUpdate } = req.body;
        
        if (!latestVersion) {
            return res.status(400).json({ error: 'Version number is required' });
        }
        
        const versionInfo = loadVersionInfo();
        versionInfo.latestVersion = latestVersion;
        versionInfo.updateMessage = updateMessage || versionInfo.updateMessage;
        versionInfo.forceUpdate = forceUpdate || false;
        versionInfo.lastUpdated = new Date().toISOString();
        
        if (saveVersionInfo(versionInfo)) {
            res.json({ 
                success: true, 
                message: `Version updated to ${latestVersion}`,
                versionInfo 
            });
        } else {
            res.status(500).json({ error: 'Failed to save version info' });
        }
    } catch (error) {
        console.error('Error updating version:', error);
        res.status(500).json({ error: 'Failed to update version' });
    }
});

// App configuration endpoint
router.get('/config', (req, res) => {
    try {
        const config = {
            maintenanceMode: false, // This should match your maintenance endpoint
            features: {
                notifications: true,
                games: true,
                events: true,
                livestreams: true
            },
            socialLinks: {
                instagram: "https://instagram.com/beyhivealert",
                twitter: "https://twitter.com/beyhivealert"
            }
        };

        res.json(config);
    } catch (error) {
        console.error('Error getting app config:', error);
        res.status(500).json({ error: 'Failed to get app configuration' });
    }
});

module.exports = router;
