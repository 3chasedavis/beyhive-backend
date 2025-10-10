# Spotify API Setup Guide

## Overview
The Chart Tracker feature automatically fetches Beyoncé's top tracks and albums from Spotify using their Web API.

## Setup Steps

### 1. Create Spotify App
1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Click "Create App"
3. Fill in:
   - **App name**: "Beyhive Alert Chart Tracker"
   - **App description**: "Chart tracking for Beyoncé's music"
   - **Redirect URI**: `http://localhost:3000/callback` (for testing)
   - **Web API**: ✅ Check this box
4. Click "Save"

### 2. Get Credentials
1. In your app dashboard, click "Settings"
2. Copy your **Client ID** and **Client Secret**

### 3. Set Environment Variables
Add these to your `.env` file:

```env
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
```

### 4. Deploy to Render
Add the environment variables to your Render service:
1. Go to your Render dashboard
2. Select your backend service
3. Go to "Environment" tab
4. Add:
   - `SPOTIFY_CLIENT_ID` = your_client_id
   - `SPOTIFY_CLIENT_SECRET` = your_client_secret

## How It Works

### Automatic Updates
- **Cache Duration**: 6 hours (refreshes automatically)
- **Data Sources**: 
  - Top 10 tracks (by popularity)
  - Latest 10 albums/singles
- **No Manual Updates**: Everything is automatic!

### API Endpoints
- `GET /api/spotify/charts` - Get cached chart data
- `GET /api/spotify/refresh` - Force refresh (admin only)

### Data Included
For each track:
- Name, popularity score, album
- Album artwork, preview URL
- Direct Spotify links

For each album:
- Name, release date, track count
- Album artwork, direct Spotify links

## Testing
1. Start your backend server
2. Visit: `https://your-backend.onrender.com/api/spotify/charts`
3. You should see JSON with Beyoncé's chart data

## Troubleshooting
- **401 Unauthorized**: Check your Client ID and Secret
- **No data**: Verify the app is published (not in development mode)
- **Rate limits**: Spotify allows 100 requests per 100 seconds (we cache for 6 hours)

## Notes
- Uses Client Credentials flow (no user login required)
- Data is cached to avoid hitting rate limits
- Only fetches Beyoncé's official artist data
- Works with free Spotify API tier
