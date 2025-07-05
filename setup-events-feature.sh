#!/bin/bash

echo "🐝 Beyhive Alert Events Feature Setup"
echo "====================================="
echo ""

# Check if we're in the right directory
if [ ! -d "Beyhive Alert 2" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    echo "   (where 'Beyhive Alert 2' folder is located)"
    exit 1
fi

echo "✅ Found iOS project directory"
echo ""

# Copy files to the project
echo "📁 Copying event files to project..."
cp -r "Beyhive Alert 2/Events/"* "Beyhive Alert 2/"

echo "✅ Files copied successfully!"
echo ""

echo "📋 Next Steps:"
echo "==============="
echo ""
echo "1. Open your Xcode project"
echo "2. Right-click on your project in the navigator"
echo "3. Select 'Add Files to [Project Name]'"
echo "4. Add these files:"
echo "   - Event.swift"
echo "   - EventsViewModel.swift" 
echo "   - EventsListView.swift"
echo ""
echo "5. Update your ScheduleView.swift:"
echo "   - Add: @StateObject private var eventsViewModel = EventsViewModel()"
echo "   - Add EventsListView at the bottom of the VStack"
echo ""
echo "6. Update Info.plist with network permissions (see InfoPlistUpdates.md)"
echo ""
echo "7. Build and test your app!"
echo ""
echo "🎉 Events feature setup complete!"
