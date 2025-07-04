# 🚀 Deploy Beyhive Alert to Render - Step by Step

## Step 1: Set Up MongoDB Atlas (Free Database)

1. **Go to [MongoDB Atlas](https://www.mongodb.com/atlas)**
2. **Sign up for free account**
3. **Create a new cluster**:
   - Choose "FREE" tier (M0)
   - Select your preferred region
   - Click "Create"
4. **Set up database access**:
   - Go to "Database Access"
   - Click "Add New Database User"
   - Username: `beyhive-admin`
   - Password: Generate a strong password (save it!)
   - Role: "Read and write to any database"
5. **Set up network access**:
   - Go to "Network Access"
   - Click "Add IP Address"
   - Click "Allow Access from Anywhere" (0.0.0.0/0)
6. **Get your connection string**:
   - Go to "Database" → "Connect"
   - Choose "Connect your application"
   - Copy the connection string
   - Replace `<password>` with your actual password
   - Replace `<dbname>` with `beyhive-alert`

## Step 2: Deploy to Render

1. **Go to [Render.com](https://render.com)**
2. **Sign up with your GitHub account**
3. **Click "New +" → "Web Service"**
4. **Connect your GitHub repository**: `3chasedavis/beyhive-backend`
5. **Configure the service**:
   - **Name**: `beyhive-backend`
   - **Environment**: `Node`
   - **Region**: Choose closest to your users
   - **Branch**: `main`
   - **Root Directory**: `beyhive-backend`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
6. **Click "Create Web Service"**

## Step 3: Set Environment Variables

In your Render dashboard, go to "Environment" and add these variables:

```
NODE_ENV=production
PORT=3000
MONGODB_URI=mongodb+srv://beyhive-admin:YOUR_PASSWORD@cluster.mongodb.net/beyhive-alert
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-2025
FRONTEND_URL=https://your-app-name.onrender.com
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=your-email@gmail.com
EMAIL_PASS=your-app-password
```

**Replace**:
- `YOUR_PASSWORD` with your MongoDB password
- `your-app-name` with your actual Render app name
- `your-email@gmail.com` with your Gmail
- `your-app-password` with your Gmail app password

## Step 4: Get Your App URL

After deployment, Render will give you a URL like:
`https://beyhive-backend.onrender.com`

## Step 5: Test Your Deployment

1. **Health Check**: Visit `https://your-app-name.onrender.com/api/health`
2. **Admin Panel**: Visit `https://your-app-name.onrender.com/admin.html`

## Step 6: Update iOS App

Once you have your Render URL, update your iOS app to use it instead of localhost.

---

## Quick Commands (if you want to test locally first)

```bash
# Test your backend locally
cd beyhive-backend
npm install
npm start

# Test endpoints
curl http://localhost:3000/api/health
```

## Need Help?

- Check Render logs in the dashboard
- Verify all environment variables are set
- Make sure MongoDB connection string is correct
- Test endpoints one by one

Your app will be live at: `https://your-app-name.onrender.com` 