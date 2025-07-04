#!/bin/bash

echo "🚀 Testing Beyhive Alert Backend..."

# Check if we're in the right directory
if [ ! -d "beyhive-backend" ]; then
    echo "❌ Error: beyhive-backend directory not found"
    echo "Please run this script from the project root"
    exit 1
fi

cd beyhive-backend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Check if .env exists
if [ ! -f ".env" ]; then
    echo "⚠️  Warning: .env file not found"
    echo "Creating .env from example..."
    cp env.example .env
    echo "Please update .env with your actual values"
fi

echo "🔧 Starting backend server..."
echo "The server will start on http://localhost:3000"
echo "Press Ctrl+C to stop"
echo ""

# Start the server
npm start 