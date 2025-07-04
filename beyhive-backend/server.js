const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const path = require('path');
require('dotenv').config();

const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/user');
const notificationRoutes = require('./routes/notifications');
const adminRoutes = require('./routes/admin');
const livestreamsRouter = require('./routes/livestreams');
const DeviceToken = require('./models/DeviceToken'); // Add this after requiring User

const app = express();
const PORT = process.env.PORT || 3000;

// Serve static files from the backend directory
app.use(express.static(__dirname));

// Security middleware
app.use(helmet());

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});
app.use(limiter);

// CORS configuration - Allow requests from any origin for public API
app.use(cors({
  origin: true, // Allow all origins for public API access
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
}));

// Body parsing middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

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
app.use('/api/admin', adminRoutes);
app.use('/api/livestreams', livestreamsRouter);

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

// Register device token from iOS app (both root and API paths)
app.post('/register-device', async (req, res) => {
  const { deviceToken } = req.body;
  if (!deviceToken) return res.status(400).json({ error: 'Device token required' });
  try {
    const result = await DeviceToken.updateOne(
      { token: deviceToken },
      { $set: {} },
      { upsert: true }
    );
    console.log('DeviceToken upsert result:', result);
    res.json({ message: 'Device token registered', result });
  } catch (err) {
    console.error('DeviceToken upsert error:', err);
    res.status(500).json({ error: 'Failed to register device token', details: err.message });
  }
});

// Also register under /api for compatibility
app.post('/api/register-device', async (req, res) => {
  const { deviceToken } = req.body;
  if (!deviceToken) return res.status(400).json({ error: 'Device token required' });
  try {
    const result = await DeviceToken.updateOne(
      { token: deviceToken },
      { $set: {} },
      { upsert: true }
    );
    console.log('DeviceToken upsert result (API):', result);
    res.json({ message: 'Device token registered', result });
  } catch (err) {
    console.error('DeviceToken upsert error (API):', err);
    res.status(500).json({ error: 'Failed to register device token', details: err.message });
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
app.get('/admin.html', require('helmet')({ contentSecurityPolicy: false }), (req, res) => {
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