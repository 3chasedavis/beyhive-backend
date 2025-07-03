# 🚀 Beyhive Alert Backend Deployment Guide

This guide will help you deploy your Beyhive Alert backend to various hosting platforms.

## Quick Start Options

### Option 1: Railway (Recommended for beginners)
**Cost**: Free tier available, then $5/month
**Setup Time**: 5 minutes

1. **Sign up** at [railway.app](https://railway.app)
2. **Connect your GitHub** repository
3. **Deploy**:
   ```bash
   # Add this to your package.json scripts
   "deploy": "railway up"
   ```
4. **Set environment variables** in Railway dashboard
5. **Get your URL** (e.g., `https://beyhive-backend-production.up.railway.app`)

### Option 2: Render
**Cost**: Free tier available, then $7/month
**Setup Time**: 10 minutes

1. **Sign up** at [render.com](https://render.com)
2. **Create a new Web Service**
3. **Connect your GitHub** repository
4. **Configure**:
   - Build Command: `npm install`
   - Start Command: `npm start`
5. **Set environment variables**
6. **Deploy**

### Option 3: Heroku
**Cost**: $5/month (no free tier anymore)
**Setup Time**: 15 minutes

1. **Install Heroku CLI**:
   ```bash
   brew install heroku/brew/heroku
   ```
2. **Login and create app**:
   ```bash
   heroku login
   heroku create beyhive-alert-backend
   ```
3. **Add MongoDB addon**:
   ```bash
   heroku addons:create mongolab:sandbox
   ```
4. **Deploy**:
   ```bash
   git add .
   git commit -m "Initial deployment"
   git push heroku main
   ```

### Option 4: DigitalOcean App Platform
**Cost**: $5/month
**Setup Time**: 20 minutes

1. **Sign up** at [digitalocean.com](https://digitalocean.com)
2. **Create App** from GitHub repository
3. **Configure** environment variables
4. **Deploy**

## Environment Variables Setup

### Required Variables
```env
NODE_ENV=production
PORT=3000
MONGODB_URI=your-mongodb-connection-string
JWT_SECRET=your-very-long-random-secret-key
FRONTEND_URL=https://your-app-domain.com
```

### Email Configuration (Optional but recommended)
```env
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=your-email@gmail.com
EMAIL_PASS=your-app-password
```

## MongoDB Setup

### Option 1: MongoDB Atlas (Recommended)
1. **Sign up** at [mongodb.com/atlas](https://mongodb.com/atlas)
2. **Create a free cluster**
3. **Get connection string**:
   ```
   mongodb+srv://username:password@cluster.mongodb.net/beyhive-alert
   ```

### Option 2: Railway MongoDB
1. **Add MongoDB service** in Railway
2. **Connect** to your web service
3. **Use provided connection string**

## Email Service Setup

### Gmail (Free)
1. **Enable 2-factor authentication**
2. **Generate App Password**:
   - Google Account → Security → 2-Step Verification → App passwords
3. **Use app password** in EMAIL_PASS

### SendGrid (Free tier: 100 emails/day)
1. **Sign up** at [sendgrid.com](https://sendgrid.com)
2. **Create API key**
3. **Configure**:
   ```env
   EMAIL_HOST=smtp.sendgrid.net
   EMAIL_PORT=587
   EMAIL_USER=apikey
   EMAIL_PASS=your-sendgrid-api-key
   ```

## Update iOS App

After deploying, update your `NetworkService.swift`:

```swift
private let baseURL = "https://your-backend-url.com/api"
```

## Testing Your Deployment

### Health Check
```bash
curl https://your-backend-url.com/api/health
```

### Test Signup
```bash
curl -X POST https://your-backend-url.com/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## Security Checklist

- [ ] Use HTTPS (automatic on most platforms)
- [ ] Set strong JWT_SECRET (32+ characters)
- [ ] Use environment variables (never commit secrets)
- [ ] Enable rate limiting (already configured)
- [ ] Set up CORS properly
- [ ] Use secure MongoDB connection

## Monitoring

### Railway
- Built-in logs and metrics
- Automatic restarts on crashes

### Render
- Built-in monitoring
- Custom domains

### Heroku
- Heroku logs: `heroku logs --tail`
- Add-ons for monitoring

## Troubleshooting

### Common Issues

1. **Build fails**
   - Check Node.js version (use 16+)
   - Verify package.json scripts

2. **Database connection fails**
   - Check MONGODB_URI format
   - Verify network access

3. **Email not sending**
   - Check email credentials
   - Verify SMTP settings

4. **CORS errors**
   - Update FRONTEND_URL
   - Check CORS configuration

### Debug Commands

```bash
# Check logs
heroku logs --tail  # Heroku
railway logs        # Railway

# Check environment
heroku config       # Heroku
railway variables   # Railway

# Restart service
heroku restart      # Heroku
railway service restart  # Railway
```

## Cost Comparison

| Platform | Free Tier | Paid Tier | Pros | Cons |
|----------|-----------|-----------|------|------|
| Railway | ✅ | $5/month | Easy setup, good free tier | Limited resources |
| Render | ✅ | $7/month | Good free tier, easy setup | Slower cold starts |
| Heroku | ❌ | $5/month | Reliable, good ecosystem | No free tier |
| DigitalOcean | ❌ | $5/month | Good performance | More complex setup |

## Next Steps

1. **Deploy** using one of the options above
2. **Test** all endpoints
3. **Update** iOS app with new backend URL
4. **Monitor** logs and performance
5. **Set up** custom domain (optional)

## Support

If you need help with deployment:
- Check platform documentation
- Review logs for error messages
- Test endpoints individually
- Verify environment variables

---

**Remember**: Always use environment variables for sensitive data and never commit secrets to your repository! 