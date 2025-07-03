#!/bin/bash

echo "🐝 Beyhive Alert Backend Setup"
echo "================================"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed."
    echo ""
    echo "Please install Node.js first:"
    echo ""
    echo "Option 1 - Using Homebrew (recommended):"
    echo "  brew install node"
    echo ""
    echo "Option 2 - Download from nodejs.org:"
    echo "  Visit https://nodejs.org and download the LTS version"
    echo ""
    echo "Option 3 - Using nvm (Node Version Manager):"
    echo "  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash"
    echo "  nvm install --lts"
    echo "  nvm use --lts"
    echo ""
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed."
    echo "Please install npm or reinstall Node.js."
    exit 1
fi

echo "✅ Node.js version: $(node --version)"
echo "✅ npm version: $(npm --version)"

# Check if MongoDB is installed
if ! command -v mongod &> /dev/null; then
    echo "⚠️  MongoDB is not installed."
    echo ""
    echo "Please install MongoDB:"
    echo ""
    echo "Option 1 - Using Homebrew:"
    echo "  brew tap mongodb/brew"
    echo "  brew install mongodb-community"
    echo ""
    echo "Option 2 - Download from mongodb.com:"
    echo "  Visit https://www.mongodb.com/try/download/community"
    echo ""
    echo "Option 3 - Use MongoDB Atlas (cloud):"
    echo "  Visit https://www.mongodb.com/atlas and create a free cluster"
    echo ""
    echo "For now, we'll continue with the setup..."
fi

# Install dependencies
echo ""
echo "📦 Installing dependencies..."
npm install

if [ $? -eq 0 ]; then
    echo "✅ Dependencies installed successfully!"
else
    echo "❌ Failed to install dependencies."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo ""
    echo "📝 Creating .env file..."
    cp env.example .env
    echo "✅ .env file created from template."
    echo "⚠️  Please edit .env file with your configuration before starting the server."
else
    echo "✅ .env file already exists."
fi

echo ""
echo "🎉 Setup complete!"
echo ""
echo "Next steps:"
echo "1. Edit .env file with your configuration"
echo "2. Start MongoDB (if using local installation)"
echo "3. Run 'npm run dev' to start the development server"
echo ""
echo "For detailed instructions, see README.md" 