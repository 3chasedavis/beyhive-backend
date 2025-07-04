# Render.com Deployment Guide

## Service Configuration

### Root Directory
Set the **Root Directory** to: `beyhive-backend`

### Build Command
```
npm install
```

### Start Command
```
npm start
```

### Environment Variables
Make sure these are set in your Render.com dashboard:
- `MONGODB_URI` - Your MongoDB Atlas connection string
- `JWT_SECRET` - Your JWT secret key
- `EMAIL_USER` - Your email for notifications (optional)
- `EMAIL_PASS` - Your email password (optional)
- `NODE_ENV` - Set to `production`

## Important Notes

1. **Root Directory**: The service must use `beyhive-backend` as the root directory, NOT the project root.

2. **Route Registration**: The `/register-device` route is now available at both:
   - `/register-device` (root level)
   - `/api/register-device` (API level)

3. **Static Files**: The backend serves static files from the `beyhive-backend` directory, including:
   - `admin.html` at `/admin.html`
   - `device-tokens.html` at `/device-tokens.html`

4. **Health Check**: Use `/api/health` to verify the service is running.

## Troubleshooting

If device tokens are not being saved:
1. Check the Render.com logs for any errors
2. Verify the MongoDB connection string is correct
3. Test the `/api/health` endpoint
4. Check if the `/register-device` endpoint is accessible

## Deployment Steps

1. Push your code to GitHub
2. In Render.com, create a new Web Service
3. Connect your GitHub repository
4. Set Root Directory to `beyhive-backend`
5. Set Build Command to `npm install`
6. Set Start Command to `npm start`
7. Add your environment variables
8. Deploy! 