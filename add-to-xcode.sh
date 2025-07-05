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
echo "   - Make sure 'Add to target' is checked"
echo "   - Click 'Add'"
echo ""

echo "3. 🔧 UPDATE SCHEDULEVIEW.SWIFT:"
echo "   - Open 'ContentView.swift' in Xcode"
echo "   - Find the ScheduleView struct (around line 1469)"
echo "   - Add this line after the existing @State properties:"
echo ""
echo "     @StateObject private var eventsViewModel = EventsViewModel()"
echo ""
echo "   - Find the end of the body property (around line 1630)"
echo "   - Add this before the .alert modifier:"
echo ""
echo "     // Custom Events Section"
echo "     EventsListView(viewModel: eventsViewModel)"
echo "         .padding(.top, 20)"
echo ""

echo "4. 🔒 UPDATE INFO.PLIST:"
echo "   - Open 'Info.plist' in Xcode"
echo "   - Right-click and select 'Add Row'"
echo "   - Add: Key: NSAppTransportSecurity, Type: Dictionary"
echo "   - Add sub-item: Key: NSAllowsArbitraryLoads, Type: Boolean, Value: YES"
echo ""

echo "5. 🏗️ BUILD AND TEST:"
echo "   - Press Cmd+B to build"
echo "   - Press Cmd+R to run"
echo "   - Navigate to Schedule tab"
echo "   - Scroll down to see 'Custom Events' section"
echo ""

echo "📋 Quick Reference - Files to Add:"
echo "=================================="
echo "• Event.swift"
echo "• EventsViewModel.swift"
echo "• EventsListView.swift"
echo ""

echo "🔧 Quick Reference - Code to Add:"
echo "================================="
echo "In ScheduleView struct:"
echo "@StateObject private var eventsViewModel = EventsViewModel()"
echo ""
echo "In ScheduleView body (before .alert):"
echo "EventsListView(viewModel: eventsViewModel)"
echo "    .padding(.top, 20)"
echo ""

echo "🎯 Expected Result:"
echo "=================="
echo "• Users can see events added through admin panel"
echo "• Users can refresh the events list"
echo "• Users CANNOT add/remove events (admin only)"
echo "• Beautiful UI that matches your app design"
echo ""

echo "❓ Need Help?"
echo "============="
echo "• Check the INTEGRATION_GUIDE.md file for detailed instructions"
echo "• Look at the Xcode console for any error messages"
echo "• Verify all files are added to the project target"
echo ""

echo "🎉 Ready to integrate! Follow the steps above." 