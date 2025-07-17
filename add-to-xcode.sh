#!/bin/bash

echo "🐝 Beyhive Alert - Xcode Integration Helper"
echo "============================================"
echo ""

# Check if we're in the right directory
if [ ! -d "Beyhive Alert 2" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    echo "   (where 'Beyhive Alert 2' folder is located)"
    exit 1
fi

echo "✅ Found iOS project directory"
echo ""

# Check if files exist
echo "📁 Checking for required files..."
if [ -f "Beyhive Alert 2/Event.swift" ]; then
    echo "✅ Event.swift found"
else
    echo "❌ Event.swift not found"
fi

if [ -f "Beyhive Alert 2/EventsViewModel.swift" ]; then
    echo "✅ EventsViewModel.swift found"
else
    echo "❌ EventsViewModel.swift not found"
fi

if [ -f "Beyhive Alert 2/EventsListView.swift" ]; then
    echo "✅ EventsListView.swift found"
else
    echo "❌ EventsListView.swift not found"
fi

if [ -f "Beyhive Alert 2/InstagramFeedView.swift" ]; then
    echo "✅ InstagramFeedView.swift found"
else
    echo "❌ InstagramFeedView.swift not found"
fi

if [ -d "Beyhive Alert 2/Assets.xcassets/beyonceupdatespfp.imageset" ]; then
    echo "✅ beyonceupdatespfp asset found"
else
    echo "❌ beyonceupdatespfp asset not found"
fi

if [ -d "Beyhive Alert 2/Assets.xcassets/arioncepfp.imageset" ]; then
    echo "✅ arioncepfp asset found"
else
    echo "❌ arioncepfp asset not found"
fi

echo ""
echo "🚀 Xcode Integration Steps:"
echo "==========================="
echo ""
echo "1. 📱 OPEN XCODE:"
echo "   - Open 'Beyhive Alert 2.xcodeproj'"
echo ""
echo "2. 📂 ADD FILES TO PROJECT:"
echo "   - Right-click on your project in the navigator (left sidebar)"
echo "   - Select 'Add Files to [Project Name]'"
echo "   - Navigate to your project folder"
echo "   - Select these files:"
echo "     • Event.swift"
echo "     • EventsViewModel.swift"
echo "     • EventsListView.swift"
echo "     • InstagramFeedView.swift"
echo "   - Make sure 'Add to target' is checked"
echo "   - Click 'Add'"
echo ""
echo "3. 🖼️ ADD INSTAGRAM PROFILE IMAGES TO ASSETS:"
echo "   - In Xcode, open Assets.xcassets"
echo "   - Drag and drop your profile images for beyonceupdatespfp and arioncepfp into the asset catalog"
echo "   - Name them exactly: 'beyonceupdatespfp' and 'arioncepfp'"
echo ""
echo "4. 🏠 ADD INSTAGRAM FEED TO HOMEPAGE:"
echo "   - Open 'ContentView.swift' in Xcode"
echo "   - Find the HomeView struct"
echo "   - Add 'InstagramFeedView()' right after the games section and before the divider"
echo ""
echo "5. 🔒 UPDATE INFO.PLIST:"
echo "   - Open 'Info.plist' in Xcode"
echo "   - Right-click and select 'Add Row'"
echo "   - Add: Key: NSAppTransportSecurity, Type: Dictionary"
echo "   - Add sub-item: Key: NSAllowsArbitraryLoads, Type: Boolean, Value: YES"
echo ""
echo "6. 🏗️ BUILD AND TEST:"
echo "   - Press Cmd+B to build"
echo "   - Press Cmd+R to run"
echo "   - Navigate to the Home tab to see the Instagram feeds under the games section"
echo ""
echo "📋 Quick Reference - Files to Add:"
echo "=================================="
echo "• Event.swift"
echo "• EventsViewModel.swift"
echo "• EventsListView.swift"
echo "• InstagramFeedView.swift"
echo "• beyonceupdatespfp (asset)"
echo "• arioncepfp (asset)"
echo ""
echo "🔧 Quick Reference - Code to Add:"
echo "================================="
echo "In HomeView body (after games section):"
echo "InstagramFeedView()"
echo ""
echo "🎯 Expected Result:"
echo "==================="
echo "• Users can see events added through admin panel"
echo "• Users can refresh the events list"
echo "• Users CANNOT add/remove events (admin only)"
echo "• Instagram feeds for Beyoncé Updates and Arionce are shown under games"
echo "• Beautiful UI that matches your app design"
echo ""
echo "❓ Need Help?"
echo "============="
echo "• Check the INTEGRATION_GUIDE.md file for detailed instructions"
echo "• Look at the Xcode console for any error messages"
echo "• Verify all files are added to the project target"
echo ""
echo "🎉 Ready to integrate! Follow the steps above." 