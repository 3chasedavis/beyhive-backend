# Beyhive Alert Backend 🐝

A Node.js/Express backend for the Beyhive Alert iOS app, providing user authentication, email verification, and notification management.

## Features

- ✅ User authentication (signup/login)
- ✅ Email verification
- ✅ Password reset functionality
- ✅ JWT token-based authentication
- ✅ User profile management
- ✅ Notification preferences management
- ✅ App settings management
- ✅ Secure password hashing
- ✅ Rate limiting
- ✅ CORS support
- ✅ MongoDB database integration

## Prerequisites

- Node.js (v14 or higher)
- MongoDB (local or cloud)
- Email service (Gmail, SendGrid, etc.)

## Installation

1. **Clone and navigate to the backend directory:**
   ```bash
   cd beyhive-backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Set up environment variables:**
   ```bash
   cp env.example .env
   ```
   
   Edit `.env` with your configuration:
   ```env
   # Server Configuration
   PORT=3000
   NODE_ENV=development

   # MongoDB Connection
   MONGODB_URI=mongodb://localhost:27017/beyhive-alert

   # JWT Secret (generate a random string)
   JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

   # Email Configuration (Gmail example)
   EMAIL_HOST=smtp.gmail.com
   EMAIL_PORT=587
   EMAIL_USER=your-email@gmail.com
   EMAIL_PASS=your-app-password

   # Frontend URL (for CORS)
   FRONTEND_URL=http://localhost:3000
   ```

4. **Start the server:**
   ```bash
   # Development mode (with auto-restart)
   npm run dev
   
   # Production mode
   npm start
   ```

## Email Setup

### Gmail Setup
1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a password for "Mail"
3. Use this password in your `.env` file

### Other Email Services
You can use any SMTP service. Update the email configuration in `.env`:
- **SendGrid**: `smtp.sendgrid.net:587`
- **Mailgun**: `smtp.mailgun.org:587`
- **Outlook**: `smtp-mail.outlook.com:587`

## API Endpoints

### Authentication

#### POST `/api/auth/signup`
Create a new user account.
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### POST `/api/auth/login`
Login with email and password.
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### POST `/api/auth/verify-email`
Verify email with token.
```json
{
  "token": "verification-token"
}
```

#### POST `/api/auth/resend-verification`
Resend verification email.
```json
{
  "email": "user@example.com"
}
```

#### POST `/api/auth/forgot-password`
Request password reset.
```json
{
  "email": "user@example.com"
}
```

#### POST `/api/auth/reset-password`
Reset password with token.
```json
{
  "token": "reset-token",
  "newPassword": "newpassword123"
}
```

### User Management

#### GET `/api/user/profile`
Get current user profile (requires authentication).

#### PUT `/api/user/profile`
Update user profile (requires authentication).
```json
{
  "email": "newemail@example.com"
}
```

#### GET `/api/user/notifications`
Get notification preferences (requires authentication).

#### PUT `/api/user/notifications`
Update notification preferences (requires authentication).
```json
{
  "notificationPreferences": {
    "concertStart": true,
    "sabrinaOnStage": false,
    "featherStart": true
  }
}
```

#### GET `/api/user/settings`
Get app settings (requires authentication).

#### PUT `/api/user/settings`
Update app settings (requires authentication).
```json
{
  "appSettings": {
    "theme": "dark",
    "language": "en",
    "timezone": "America/New_York"
  }
}
```

#### DELETE `/api/user/account`
Delete user account (requires authentication).

#### POST `/api/user/logout`
Logout user (requires authentication).

### Notifications

#### GET `/api/notifications/preferences`
Get all notification preferences (requires authentication).

#### PUT `/api/notifications/preferences/:type`
Update specific notification preference (requires authentication).
```json
{
  "enabled": true
}
```

#### PUT `/api/notifications/preferences`
Update multiple notification preferences (requires authentication).
```json
{
  "preferences": {
    "concertStart": true,
    "sabrinaOnStage": false
  }
}
```

#### GET `/api/notifications/users/:type`
Get users who should receive a specific notification type.

#### POST `/api/notifications/send/:type`
Send notification to users (requires authentication).
```json
{
  "title": "Concert Starting!",
  "message": "Beyoncé is about to take the stage!",
  "data": {
    "venue": "Madison Square Garden",
    "time": "8:00 PM"
  }
}
```

#### GET `/api/notifications/stats`
Get notification statistics (requires authentication).

#### POST `/api/notifications/reset`
Reset notification preferences to defaults (requires authentication).

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Database Schema

### User Model
```javascript
{
  email: String (required, unique),
  password: String (required, hashed),
  isEmailVerified: Boolean (default: false),
  emailVerificationToken: String,
  emailVerificationExpires: Date,
  resetPasswordToken: String,
  resetPasswordExpires: Date,
  notificationPreferences: {
    concertStart: Boolean (default: true),
    sabrinaOnStage: Boolean (default: true),
    featherStart: Boolean (default: true),
    spinTheBottle: Boolean (default: true),
    finalSet: Boolean (default: true),
    junoStart: Boolean (default: true),
    espressoFinale: Boolean (default: true),
    pushNotifications: Boolean (default: true),
    emailNotifications: Boolean (default: true)
  },
  appSettings: {
    theme: String (enum: ['light', 'dark', 'auto'], default: 'auto'),
    language: String (default: 'en'),
    timezone: String (default: 'UTC')
  },
  lastLogin: Date,
  createdAt: Date,
  updatedAt: Date
}
```

## Security Features

- Password hashing with bcrypt
- JWT token authentication
- Rate limiting (100 requests per 15 minutes)
- CORS protection
- Helmet.js security headers
- Input validation with express-validator
- Email verification required for sensitive operations

## Deployment

### Local Development
```bash
npm run dev
```

### Production Deployment
1. Set `NODE_ENV=production` in your environment
2. Use a strong JWT secret
3. Set up a production MongoDB instance
4. Configure email service
5. Set up reverse proxy (nginx) if needed
6. Use PM2 or similar process manager

### Environment Variables for Production
```env
NODE_ENV=production
PORT=3000
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/beyhive-alert
JWT_SECRET=your-very-long-random-secret-key
EMAIL_HOST=smtp.sendgrid.net
EMAIL_PORT=587
EMAIL_USER=your-email@domain.com
EMAIL_PASS=your-email-password
FRONTEND_URL=https://your-app-domain.com
```

## Testing the API

### Health Check
```bash
curl http://localhost:3000/api/health
```

### Signup Example
```bash
curl -X POST http://localhost:3000/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Login Example
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Ensure MongoDB is running
   - Check connection string in `.env`
   - Verify network access for cloud MongoDB

2. **Email Not Sending**
   - Check email credentials in `.env`
   - Verify SMTP settings
   - Check firewall/network restrictions

3. **JWT Token Issues**
   - Ensure JWT_SECRET is set
   - Check token expiration
   - Verify Authorization header format

4. **CORS Errors**
   - Update FRONTEND_URL in `.env`
   - Check CORS configuration in server.js

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

MIT License - see LICENSE file for details

## Support

For support, email: beyhivealert@gmail.com

---

**Note**: This is an unofficial fan app and is not affiliated with Beyoncé or her team. 