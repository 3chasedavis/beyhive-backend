const express = require('express');
const router = express.Router();
const cheerio = require('cheerio');
const axios = require('axios');

// Cache for Billboard data (refresh every 24 hours since Billboard updates weekly)
let billboardCache = null;
let lastBillboardCacheTime = null;
const BILLBOARD_CACHE_DURATION = 24 * 60 * 60 * 1000; // 24 hours

// Fetch Billboard Hot 100 data
async function fetchBillboardHot100() {
    try {
        console.log('Fetching Billboard Hot 100 data...');
        
        // Billboard Hot 100 URL
        const url = 'https://www.billboard.com/charts/hot-100/';
        
        const response = await axios.get(url, {
            headers: {
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
            },
            timeout: 10000
        });

        const $ = cheerio.load(response.data);
        const songs = [];

        // Parse Billboard Hot 100 chart
        $('.chart-element__wrapper').each((index, element) => {
            if (index >= 100) return; // Limit to top 100

            const rank = $(element).find('.chart-element__rank__number').text().trim();
            const title = $(element).find('.chart-element__information__song').text().trim();
            const artist = $(element).find('.chart-element__information__artist').text().trim();
            const lastWeek = $(element).find('.chart-element__information__delta__text').text().trim();

            if (rank && title && artist) {
                songs.push({
                    rank: parseInt(rank),
                    title: title,
                    artist: artist,
                    lastWeek: lastWeek,
                    movement: getMovement(lastWeek)
                });
            }
        });

        return songs;
    } catch (error) {
        console.error('Error fetching Billboard data:', error);
        throw error;
    }
}

// Get movement direction from last week text
function getMovement(lastWeekText) {
    if (!lastWeekText) return 'new';
    if (lastWeekText.includes('NEW')) return 'new';
    if (lastWeekText.includes('RE-ENTRY')) return 're-entry';
    if (lastWeekText.includes('↑')) return 'up';
    if (lastWeekText.includes('↓')) return 'down';
    return 'stable';
}

// Filter for Beyoncé songs
function filterBeyonceSongs(songs) {
    const beyonceVariations = [
        'beyoncé', 'beyonce', 'beyonce knowles', 'beyoncé knowles',
        'beyonce carter', 'beyoncé carter', 'destiny\'s child', 'destinys child'
    ];

    return songs.filter(song => {
        const artistLower = song.artist.toLowerCase();
        return beyonceVariations.some(variation => artistLower.includes(variation));
    });
}

// GET /api/billboard/hot100 - Get full Billboard Hot 100
router.get('/hot100', async (req, res) => {
    try {
        // Check cache
        const now = Date.now();
        if (billboardCache && lastBillboardCacheTime && (now - lastBillboardCacheTime) < BILLBOARD_CACHE_DURATION) {
            return res.json({
                success: true,
                data: billboardCache,
                cached: true,
                lastUpdated: new Date(lastBillboardCacheTime).toISOString()
            });
        }

        // Fetch new data
        const songs = await fetchBillboardHot100();
        
        // Update cache
        billboardCache = {
            songs: songs,
            chartDate: new Date().toISOString().split('T')[0], // Current date
            totalSongs: songs.length
        };
        lastBillboardCacheTime = now;

        res.json({
            success: true,
            data: billboardCache,
            cached: false,
            lastUpdated: new Date().toISOString()
        });
    } catch (error) {
        console.error('Error in /hot100 endpoint:', error);
        res.status(500).json({
            success: false,
            error: 'Failed to fetch Billboard Hot 100 data',
            message: error.message
        });
    }
});

// GET /api/billboard/beyonce - Get only Beyoncé songs from Hot 100
router.get('/beyonce', async (req, res) => {
    try {
        // Check cache
        const now = Date.now();
        if (billboardCache && lastBillboardCacheTime && (now - lastBillboardCacheTime) < BILLBOARD_CACHE_DURATION) {
            const beyonceSongs = filterBeyonceSongs(billboardCache.songs);
            return res.json({
                success: true,
                data: {
                    songs: beyonceSongs,
                    chartDate: billboardCache.chartDate,
                    totalBeyonceSongs: beyonceSongs.length
                },
                cached: true,
                lastUpdated: new Date(lastBillboardCacheTime).toISOString()
            });
        }

        // Fetch new data
        const songs = await fetchBillboardHot100();
        const beyonceSongs = filterBeyonceSongs(songs);
        
        // Update cache
        billboardCache = {
            songs: songs,
            chartDate: new Date().toISOString().split('T')[0],
            totalSongs: songs.length
        };
        lastBillboardCacheTime = now;

        res.json({
            success: true,
            data: {
                songs: beyonceSongs,
                chartDate: billboardCache.chartDate,
                totalBeyonceSongs: beyonceSongs.length
            },
            cached: false,
            lastUpdated: new Date().toISOString()
        });
    } catch (error) {
        console.error('Error in /beyonce endpoint:', error);
        res.status(500).json({
            success: false,
            error: 'Failed to fetch Beyoncé Billboard data',
            message: error.message
        });
    }
});

// GET /api/billboard/refresh - Force refresh cache (admin only)
router.get('/refresh', async (req, res) => {
    try {
        const songs = await fetchBillboardHot100();
        const beyonceSongs = filterBeyonceSongs(songs);
        
        billboardCache = {
            songs: songs,
            chartDate: new Date().toISOString().split('T')[0],
            totalSongs: songs.length
        };
        lastBillboardCacheTime = Date.now();

        res.json({
            success: true,
            message: 'Billboard data refreshed successfully',
            data: {
                totalSongs: songs.length,
                beyonceSongs: beyonceSongs.length,
                chartDate: billboardCache.chartDate
            }
        });
    } catch (error) {
        console.error('Error refreshing Billboard data:', error);
        res.status(500).json({
            success: false,
            error: 'Failed to refresh Billboard data',
            message: error.message
        });
    }
});

module.exports = router;
