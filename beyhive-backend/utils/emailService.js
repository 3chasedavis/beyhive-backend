const nodemailer = require('nodemailer');

class EmailService {
  constructor() {
    this.transporter = nodemailer.createTransport({
      host: process.env.EMAIL_HOST,
      port: process.env.EMAIL_PORT,
      secure: false, // true for 465, false for other ports
      auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS,
      },
    });
  }

  async sendVerificationEmail(email, token) {
    const verificationUrl = `${process.env.FRONTEND_URL}/verify-email?token=${token}`;
    
    const mailOptions = {
      from: `"Beyhive Alert" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: 'Verify your Beyhive Alert account',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <div style="background: linear-gradient(135deg, #FFD700, #FFA500); padding: 20px; text-align: center;">
            <h1 style="color: white; margin: 0;">🐝 Beyhive Alert</h1>
          </div>
          
          <div style="padding: 30px; background: #f9f9f9;">
            <h2 style="color: #333;">Welcome to Beyhive Alert!</h2>
            <p style="color: #666; line-height: 1.6;">
              Thank you for signing up! To complete your registration and start receiving Beyoncé updates, 
              please verify your email address by clicking the button below.
            </p>
            
            <div style="text-align: center; margin: 30px 0;">
              <a href="${verificationUrl}" 
                 style="background: linear-gradient(135deg, #FFD700, #FFA500); 
                        color: white; 
                        padding: 15px 30px; 
                        text-decoration: none; 
                        border-radius: 25px; 
                        font-weight: bold;
                        display: inline-block;">
                Verify Email Address
              </a>
            </div>
            
            <p style="color: #666; font-size: 14px;">
              If the button doesn't work, you can copy and paste this link into your browser:
            </p>
            <p style="color: #999; font-size: 12px; word-break: break-all;">
              ${verificationUrl}
            </p>
            
            <p style="color: #666; font-size: 14px;">
              This link will expire in 24 hours. If you didn't create an account, you can safely ignore this email.
            </p>
          </div>
          
          <div style="background: #333; color: white; padding: 20px; text-align: center; font-size: 12px;">
            <p>© 2024 Beyhive Alert. All rights reserved.</p>
            <p>This is an unofficial fan app and is not affiliated with Beyoncé or her team.</p>
          </div>
        </div>
      `
    };

    try {
      await this.transporter.sendMail(mailOptions);
      console.log(`Verification email sent to ${email}`);
      return true;
    } catch (error) {
      console.error('Email sending failed:', error);
      return false;
    }
  }

  async sendPasswordResetEmail(email, token) {
    const resetUrl = `${process.env.FRONTEND_URL}/reset-password?token=${token}`;
    
    const mailOptions = {
      from: `"Beyhive Alert" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: 'Reset your Beyhive Alert password',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <div style="background: linear-gradient(135deg, #FFD700, #FFA500); padding: 20px; text-align: center;">
            <h1 style="color: white; margin: 0;">🐝 Beyhive Alert</h1>
          </div>
          
          <div style="padding: 30px; background: #f9f9f9;">
            <h2 style="color: #333;">Password Reset Request</h2>
            <p style="color: #666; line-height: 1.6;">
              You requested to reset your password. Click the button below to create a new password.
            </p>
            
            <div style="text-align: center; margin: 30px 0;">
              <a href="${resetUrl}" 
                 style="background: linear-gradient(135deg, #FFD700, #FFA500); 
                        color: white; 
                        padding: 15px 30px; 
                        text-decoration: none; 
                        border-radius: 25px; 
                        font-weight: bold;
                        display: inline-block;">
                Reset Password
              </a>
            </div>
            
            <p style="color: #666; font-size: 14px;">
              If you didn't request a password reset, you can safely ignore this email.
            </p>
            
            <p style="color: #666; font-size: 14px;">
              This link will expire in 1 hour for security reasons.
            </p>
          </div>
          
          <div style="background: #333; color: white; padding: 20px; text-align: center; font-size: 12px;">
            <p>© 2024 Beyhive Alert. All rights reserved.</p>
          </div>
        </div>
      `
    };

    try {
      await this.transporter.sendMail(mailOptions);
      console.log(`Password reset email sent to ${email}`);
      return true;
    } catch (error) {
      console.error('Email sending failed:', error);
      return false;
    }
  }

  async sendWelcomeEmail(email) {
    const mailOptions = {
      from: `"Beyhive Alert" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: 'Welcome to Beyhive Alert! 🐝',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <div style="background: linear-gradient(135deg, #FFD700, #FFA500); padding: 20px; text-align: center;">
            <h1 style="color: white; margin: 0;">🐝 Beyhive Alert</h1>
          </div>
          
          <div style="padding: 30px; background: #f9f9f9;">
            <h2 style="color: #333;">Welcome to the Beyhive! 🎉</h2>
            <p style="color: #666; line-height: 1.6;">
              Your account has been successfully verified! You're now part of the Beyhive Alert community.
            </p>
            
            <div style="background: white; padding: 20px; border-radius: 10px; margin: 20px 0;">
              <h3 style="color: #333; margin-top: 0;">What you can do now:</h3>
              <ul style="color: #666; line-height: 1.8;">
                <li>🎵 Get notified about Beyoncé's latest posts and updates</li>
                <li>🎮 Play the Survivor game during concerts</li>
                <li>📅 Track tour dates and add them to your calendar</li>
                <li>🎤 Follow setlists and track your favorite songs</li>
                <li>📱 Customize your notification preferences</li>
              </ul>
            </div>
            
            <p style="color: #666; font-size: 14px;">
              Thank you for joining Beyhive Alert! Stay tuned for the latest Beyoncé news and updates.
            </p>
          </div>
          
          <div style="background: #333; color: white; padding: 20px; text-align: center; font-size: 12px;">
            <p>© 2024 Beyhive Alert. All rights reserved.</p>
            <p>This is an unofficial fan app and is not affiliated with Beyoncé or her team.</p>
          </div>
        </div>
      `
    };

    try {
      await this.transporter.sendMail(mailOptions);
      console.log(`Welcome email sent to ${email}`);
      return true;
    } catch (error) {
      console.error('Email sending failed:', error);
      return false;
    }
  }
}

module.exports = new EmailService(); 