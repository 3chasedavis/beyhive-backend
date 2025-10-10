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

        // Check if Spotify credentials are set up
        if (!SPOTIFY_CLIENT_ID || SPOTIFY_CLIENT_ID === 'your_spotify_client_id') {
            // Return mock data if credentials not set up
            const mockData = {
                topTracks: [
                    {
                        id: "1",
                        name: "CUFF IT",
                        popularity: 95,
                        album: "RENAISSANCE",
                        albumImage: null,
                        previewUrl: null,
                        externalUrl: "https://open.spotify.com/track/1xzi1Jcr7mEi9K2RfzLOqM",
                        artists: "Beyoncé"
                    },
                    {
                        id: "2", 
                        name: "BREAK MY SOUL",
                        popularity: 92,
                        album: "RENAISSANCE",
                        albumImage: null,
                        previewUrl: null,
                        externalUrl: "https://open.spotify.com/track/2KcM7XyknVEsWHOVLd3Nkc",
                        artists: "Beyoncé"
                    },
                    {
                        id: "3",
                        name: "ALIEN SUPERSTAR",
                        popularity: 88,
                        album: "RENAISSANCE", 
                        albumImage: null,
                        previewUrl: null,
                        externalUrl: "https://open.spotify.com/track/6r3sv8XH4mXt1nzTz2vWfR",
                        artists: "Beyoncé"
                    },
                    {
                        id: "4",
                        name: "VIRGO'S GROOVE",
                        popularity: 85,
                        album: "RENAISSANCE",
                        albumImage: null,
                        previewUrl: null,
                        externalUrl: "https://open.spotify.com/track/4I7u2jF0AljBaaH2aI34Zt",
                        artists: "Beyoncé"
                    },
                    {
                        id: "5",
                        name: "HEATED",
                        popularity: 82,
                        album: "RENAISSANCE",
                        albumImage: null,
                        previewUrl: null,
                        externalUrl: "https://open.spotify.com/track/5c3I1pHdNaw0ye40Y5b2V4",
                        artists: "Beyoncé"
                    }
                ],
                albums: [
                    {
                        id: "1",
                        name: "RENAISSANCE",
                        releaseDate: "2022-07-29",
                        albumImage: null,
                        externalUrl: "https://open.spotify.com/album/4F2H0NqHwHbo2tX3f7N0sG",
                        totalTracks: 16
                    },
                    {
                        id: "2",
                        name: "COWBOY CARTER",
                        releaseDate: "2024-03-29",
                        albumImage: null,
                        externalUrl: "https://open.spotify.com/album/4XLP22erBmewl3PWTqV2w1",
                        totalTracks: 27
                    }
                ],
                lastUpdated: new Date().toISOString()
            };
            
            chartDataCache = mockData;
            lastCacheTime = now;
            
            return res.json({
                success: true,
                data: mockData,
                cached: false,
                mock: true
            });
        }

        // Fetch new data from Spotify
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
