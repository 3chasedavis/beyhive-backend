const express = require('express');
const router = express.Router();

// Spotify API configuration
const SPOTIFY_CLIENT_ID = process.env.SPOTIFY_CLIENT_ID || 'your_spotify_client_id';
const SPOTIFY_CLIENT_SECRET = process.env.SPOTIFY_CLIENT_SECRET || 'your_spotify_client_secret';
const SPOTIFY_REFRESH_TOKEN = process.env.SPOTIFY_REFRESH_TOKEN || 'your_refresh_token';

// Cache for API responses (refresh every 6 hours)
let chartDataCache = null;
let lastCacheTime = null;
const CACHE_DURATION = 6 * 60 * 60 * 1000; // 6 hours in milliseconds

// Get access token from Spotify
async function getAccessToken() {
    try {
        const response = await fetch('https://accounts.spotify.com/api/token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Basic ${Buffer.from(`${SPOTIFY_CLIENT_ID}:${SPOTIFY_CLIENT_SECRET}`).toString('base64')}`
            },
            body: `grant_type=client_credentials`
        });

        if (!response.ok) {
            throw new Error(`Spotify API error: ${response.status}`);
        }

        const data = await response.json();
        return data.access_token;
    } catch (error) {
        console.error('Error getting Spotify access token:', error);
        throw error;
    }
}

// Search for Beyoncé's artist ID
async function getBeyonceArtistId(accessToken) {
    try {
        const response = await fetch('https://api.spotify.com/v1/search?q=Beyonc%C3%A9&type=artist&limit=1', {
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (!response.ok) {
            throw new Error(`Spotify search error: ${response.status}`);
        }

        const data = await response.json();
        return data.artists.items[0]?.id;
    } catch (error) {
        console.error('Error searching for Beyoncé:', error);
        throw error;
    }
}

// Get Beyoncé's top tracks
async function getBeyonceTopTracks(accessToken, artistId) {
    try {
        const response = await fetch(`https://api.spotify.com/v1/artists/${artistId}/top-tracks?market=US`, {
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (!response.ok) {
            throw new Error(`Spotify tracks error: ${response.status}`);
        }

        const data = await response.json();
        return data.tracks.map(track => ({
            id: track.id,
            name: track.name,
            popularity: track.popularity,
            album: track.album.name,
            albumImage: track.album.images[0]?.url,
            previewUrl: track.preview_url,
            externalUrl: track.external_urls.spotify,
            artists: track.artists.map(artist => artist.name).join(', ')
        }));
    } catch (error) {
        console.error('Error getting top tracks:', error);
        throw error;
    }
}

// Get Beyoncé's albums
async function getBeyonceAlbums(accessToken, artistId) {
    try {
        const response = await fetch(`https://api.spotify.com/v1/artists/${artistId}/albums?market=US&limit=10&include_groups=album,single`, {
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (!response.ok) {
            throw new Error(`Spotify albums error: ${response.status}`);
        }

        const data = await response.json();
        return data.items.map(album => ({
            id: album.id,
            name: album.name,
            releaseDate: album.release_date,
            albumImage: album.images[0]?.url,
            externalUrl: album.external_urls.spotify,
            totalTracks: album.total_tracks
        }));
    } catch (error) {
        console.error('Error getting albums:', error);
        throw error;
    }
}

// Fetch and cache chart data
async function fetchChartData() {
    try {
        const accessToken = await getAccessToken();
        const artistId = await getBeyonceArtistId(accessToken);
        
        const [topTracks, albums] = await Promise.all([
            getBeyonceTopTracks(accessToken, artistId),
            getBeyonceAlbums(accessToken, artistId)
        ]);

        return {
            topTracks,
            albums,
            lastUpdated: new Date().toISOString()
        };
    } catch (error) {
        console.error('Error fetching chart data:', error);
        throw error;
    }
}

// GET /api/spotify/charts - Get Beyoncé's chart data
router.get('/charts', async (req, res) => {
    try {
        // Check if we have cached data that's still fresh
        const now = Date.now();
        if (chartDataCache && lastCacheTime && (now - lastCacheTime) < CACHE_DURATION) {
            return res.json({
                success: true,
                data: chartDataCache,
                cached: true
            });
        }

        // Fetch new data
        const chartData = await fetchChartData();
        
        // Update cache
        chartDataCache = chartData;
        lastCacheTime = now;

        res.json({
            success: true,
            data: chartData,
            cached: false
        });
    } catch (error) {
        console.error('Error in /charts endpoint:', error);
        res.status(500).json({
            success: false,
            error: 'Failed to fetch chart data',
            message: error.message
        });
    }
});

// GET /api/spotify/refresh - Force refresh cache (admin only)
router.get('/refresh', async (req, res) => {
    try {
        const chartData = await fetchChartData();
        chartDataCache = chartData;
        lastCacheTime = Date.now();

        res.json({
            success: true,
            message: 'Chart data refreshed successfully',
            data: chartData
        });
    } catch (error) {
        console.error('Error refreshing chart data:', error);
        res.status(500).json({
            success: false,
            error: 'Failed to refresh chart data',
            message: error.message
        });
    }
});

module.exports = router;
