# Imgur API Setup Guide (FREE Alternative to Cloudinary)

## Why Imgur?
- **Completely FREE** (vs $89/month for Cloudinary)
- **12,500 uploads per day** (more than enough for your app)
- **Fast CDN** for image delivery
- **Simple API** integration

## Setup Steps

### 1. Create Imgur Account
1. Go to https://imgur.com
2. Sign up for a free account
3. Verify your email

### 2. Get API Client ID
1. Go to https://api.imgur.com/oauth2/addclient
2. Fill out the form:
   - **Application name**: Beyhive Alert
   - **Authorization type**: OAuth 2 authorization with a callback URL
   - **Authorization callback URL**: http://localhost:3000/callback
   - **Application website**: https://your-app-domain.com
   - **Email**: your-email@example.com
   - **Description**: Image hosting for Beyhive Alert app outfits and partners

3. Click "Submit"
4. Copy the **Client ID** (not the Client Secret)

### 3. Configure Environment
1. Add to your `.env` file:
```
IMGUR_CLIENT_ID=your-client-id-here
```

### 4. Install Dependencies
```bash
npm install axios form-data
```

### 5. Deploy
Your app will now use Imgur for free image hosting instead of Cloudinary!

## Benefits
- ✅ **FREE forever**
- ✅ **12,500 uploads/day** (vs 25 credits/month on Cloudinary)
- ✅ **Fast global CDN**
- ✅ **Simple integration**
- ✅ **No credit card required**

## Migration Notes
- Existing Cloudinary images will continue to work
- New uploads will go to Imgur
- No changes needed in your iOS app - it just displays the image URLs 