const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const path = require('path');
require('dotenv').config();
const session = require('express-session');

const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/user');
const notificationRoutes = require('./routes/notifications');
const adminRoutes = require('./routes/admin');
const livestreamsRouter = require('./routes/livestreams');
const DeviceToken = require('./models/DeviceToken'); // Add this after requiring User
const eventsRoutes = require('./routes/events');
const outfitsRoutes = require('./routes/outfits');
const survivorRouter = require('./routes/survivor');
const newsRouter = require('./routes/news');
const instagramFeedRouter = require('./routes/instagramFeed');
const albumRankingsRouter = require('./routes/albumRankings'); // Add this line
const partnersRouter = require('./routes/partners'); // Add this line

const app = express();
app.set('trust proxy', 1); // Trust first proxy for correct rate limiting on Render
const PORT = process.env.PORT || 3000;

// Add session middleware for admin authentication
app.use(session({
  secret: process.env.SESSION_SECRET || 'supersecret',
  resave: false,
  saveUninitialized: false,
  cookie: { httpOnly: true, secure: false } // Set secure: true if using HTTPS
}));

// Serve static files from the backend directory
app.use(express.static(__dirname));

// Body parsing middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Remove route-specific express.json() for /api/admin/events and /api/outfits
app.use('/api/admin/events', eventsRoutes);
app.use('/api/outfits', outfitsRoutes);

// Public events endpoint for iOS app
app.use('/api/events', eventsRoutes);
app.use('/api/partners', partnersRouter);

// Security middleware
app.use(helmet());

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 500 // Increased from 100 to 500 requests per windowMs
});
app.use(limiter);

// Limit: max 10 requests per minute per IP for /register-device (increased from 5)
const registerDeviceLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 10, // Increased from 5 to 10
  message: 'Too many device registrations from this IP, please try again later.'
});

// CORS configuration - Allow requests from any origin for public API
app.use(cors({
  origin: true, // Allow all origins for public API access
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
}));

// Database connection
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/beyhive-alert', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
.then(() => console.log('Connected to MongoDB'))
.catch(err => console.error('MongoDB connection error:', err));

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/user', userRoutes);
app.use('/api/notifications', notificationRoutes);
const adminLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 20, // Increased from 10 to 20
  message: 'Too many admin requests from this IP, please try again later.'
});
app.use('/api/admin', adminLimiter, adminRoutes);
app.use('/api/livestreams', livestreamsRouter);
app.use('/api/news', newsRouter);
app.use('/api/instagram-feed', instagramFeedRouter);
app.use('/api/survivor-quiz', survivorRouter);
app.use('/api', albumRankingsRouter); // Add this line

// Do not restore survivorRouter

// Public endpoint to get all device tokens
const User = require('./models/User');
app.get('/api/device-tokens', async (req, res) => {
  try {
    const users = await User.find({}, 'email deviceTokens');
    res.json(users);
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch device tokens' });
  }
});

// Register device token from iOS app
app.post('/register-device', registerDeviceLimiter, async (req, res) => {
  const { deviceToken, preferences } = req.body;
  console.log('[DEBUG] /register-device called with:', { deviceToken, preferences });
  if (!deviceToken) return res.status(400).json({ error: 'Device token required' });
  const DeviceToken = require('./models/DeviceToken');
  // Provide defaults for all known preferences
  const defaultPrefs = {
    beyonceOnStage: true,
    concertStart: true,
    americaHasAProblem: true,
    tyrant: true,
    lastAct: true,
    sixteenCarriages: true,
    amen: true
  };
  const mergedPrefs = { ...defaultPrefs, ...(preferences || {}) };
  const updateResult = await DeviceToken.updateOne(
    { token: deviceToken },
    { $set: { preferences: mergedPrefs } },
    { upsert: true }
  );
  console.log('[DEBUG] updateOne result:', updateResult);
  res.json({ message: 'Device token and preferences registered', preferences: mergedPrefs });
});

// GET notification preferences for a device token
app.get('/device-preferences/:deviceToken', async (req, res) => {
  try {
    const { deviceToken } = req.params;
    const DeviceToken = require('./models/DeviceToken');
    const doc = await DeviceToken.findOne({ token: deviceToken });
    // Provide defaults for all known preferences
    const defaultPrefs = {
      beyonceOnStage: true,
      concertStart: true,
      americaHasAProblem: true,
      tyrant: true,
      lastAct: true,
      sixteenCarriages: true,
      amen: true
    };
    const prefs = { ...defaultPrefs, ...(doc?.preferences || {}) };
    res.json({ preferences: prefs });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch preferences' });
  }
});

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    message: 'Beyhive Alert Backend is running!',
    timestamp: new Date().toISOString()
  });
});

// Serve admin.html at /admin.html with relaxed CSP
app.get('/admin.html', (req, res, next) => {
  res.setHeader('Content-Security-Policy', "default-src 'self' 'unsafe-inline' data: blob:;");
  next();
}, (req, res) => {
  res.sendFile(path.join(__dirname, 'admin.html'));
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ 
    error: 'Something went wrong!',
    message: process.env.NODE_ENV === 'development' ? err.message : 'Internal server error'
  });
});

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

app.listen(PORT, () => {
  console.log(`🚀 Beyhive Alert Backend running on port ${PORT}`);
  console.log(`📧 Email verification: ${process.env.EMAIL_USER ? 'Configured' : 'Not configured'}`);
  console.log(`🗄️  Database: ${process.env.MONGODB_URI ? 'Configured' : 'Using default'}`);
});