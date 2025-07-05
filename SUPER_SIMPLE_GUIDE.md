# 🐝 SUPER SIMPLE GUIDE - Add Events to Your App

## Step 1: Open Xcode
1. Find your project folder on your computer
2. Double-click on `Beyhive Alert 2.xcodeproj`
3. Wait for Xcode to open

## Step 2: Add the Files (EASY!)
1. In Xcode, look at the left sidebar
2. Right-click on "Beyhive Alert 2" (the blue project icon)
3. Click "Add Files to 'Beyhive Alert 2'"
4. In the file picker that opens:
   - Navigate to your project folder
   - Select these 3 files (hold Cmd to select multiple):
     - `Event.swift`
     - `EventsViewModel.swift`
     - `EventsListView.swift`
5. Make sure "Add to target" is checked
6. Click "Add"

## Step 3: Add 2 Lines of Code (EASY!)
1. In Xcode, find `ContentView.swift` in the left sidebar
2. Click on it to open it
3. Press Cmd+F and search for "ScheduleView"
4. You'll see something like this:
   ```swift
   struct ScheduleView: View {
       @State private var selectedDate = Date()
       @State private var showUpcoming = true
       // ... more lines
   ```

5. **Add this line** after the existing `@State` lines:
   ```swift
   @StateObject private var eventsViewModel = EventsViewModel()
   ```

6. **Find the end of the body** - look for this:
   ```swift
   }
   .alert("Event Added!", isPresented: $showingCalendarAlert) {
   ```

7. **Add this before the `.alert` line**:
   ```swift
   // Custom Events Section
   EventsListView(viewModel: eventsViewModel)
       .padding(.top, 20)
   ```

## Step 4: Update Info.plist (EASY!)
1. Find `Info.plist` in the left sidebar
2. Click on it
3. Right-click in the empty area
4. Click "Add Row"
5. Type: `NSAppTransportSecurity`
6. Set Type to: `Dictionary`
7. Click the arrow next to it to expand
8. Right-click inside the dictionary
9. Click "Add Row"
10. Type: `NSAllowsArbitraryLoads`
11. Set Type to: `Boolean`
12. Set Value to: `YES`

## Step 5: Test It!
1. Press Cmd+B to build
2. Press Cmd+R to run
3. Go to the Schedule tab
4. Scroll down - you should see "Custom Events"

## 🆘 If Something Goes Wrong:

**Error: "Cannot find EventsViewModel"**
- Make sure you added all 3 files to Xcode
- Make sure "Add to target" was checked

**Error: "Network request failed"**
- Make sure you updated Info.plist correctly
- Check your internet connection

**App crashes:**
- Make sure you added the code in the right place
- Check the Xcode console for error messages

## 📞 Need More Help?
- Take a screenshot of any error messages
- Tell me exactly what step you're stuck on
- I'll help you fix it!

## 🎯 What You Should See:
- A "Custom Events" section at the bottom of the Schedule tab
- Events you add through the admin panel will appear here
- Users can only view events (no delete button)
- Beautiful design that matches your app

**You can do this! It's just adding 3 files and 2 lines of code!** 🚀 