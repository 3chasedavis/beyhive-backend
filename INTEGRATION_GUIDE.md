# 🐝 Beyhive Alert Events Feature Integration Guide

## Overview
This guide will help you integrate the events feature into your existing iOS app. The events feature allows users to see custom events added through the admin panel. **Only admins can add/remove events through the admin panel** - users can only view them in the app.

## Files Created
✅ **Event.swift** - Event model and response types  
✅ **EventsViewModel.swift** - Network and data management  
✅ **EventsListView.swift** - UI for displaying events  
✅ **InfoPlistUpdates.md** - Required Info.plist changes  

## Step-by-Step Integration

### 1. Add Files to Xcode Project
1. Open your Xcode project (`Beyhive Alert 2.xcodeproj`)
2. Right-click on your project in the navigator
3. Select "Add Files to [Project Name]"
4. Add these files:
   - `Event.swift`
   - `EventsViewModel.swift`
   - `EventsListView.swift`

### 2. Update ScheduleView.swift

**Step 2a: Add the ViewModel property**
Find the `ScheduleView` struct (around line 1469) and add this line after the existing `@State` properties:

```swift
struct ScheduleView: View {
    @State private var selectedDate = Date()
    @State private var showUpcoming = true
    @State private var showingCalendarAlert = false
    @State private var lastAddedEvent: TourEvent?
    @State private var addedEventIDs: Set<UUID> = []
    
    // ADD THIS LINE:
    @StateObject private var eventsViewModel = EventsViewModel()
    
    let events: [TourEvent] = [
        // ... existing events
    ]
```

**Step 2b: Add the EventsListView to the body**
Find the end of the `body` property in `ScheduleView` (around line 1630). Look for this structure:

```swift
        }
        .alert("Event Added!", isPresented: $showingCalendarAlert) {
            Button("OK") { }
        } message: {
            if let event = lastAddedEvent {
                Text("'Beyoncé - \(event.city) @ \(event.venue)' has been added to your calendar.")
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
```

**ADD THE EVENTS LIST VIEW** just before the `.alert` modifier:

```swift
        }
        
        // ADD THIS SECTION:
        // Custom Events Section
        EventsListView(viewModel: eventsViewModel)
            .padding(.top, 20)
        
        .alert("Event Added!", isPresented: $showingCalendarAlert) {
            Button("OK") { }
        } message: {
            if let event = lastAddedEvent {
                Text("'Beyoncé - \(event.city) @ \(event.venue)' has been added to your calendar.")
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
```

### 3. Update Info.plist
Add network permissions to your `Info.plist` file:

1. Open `Info.plist` in Xcode
2. Right-click and select "Add Row"
3. Add these keys:
   - **Key**: `NSAppTransportSecurity`
   - **Type**: Dictionary
   - **Value**: Add a sub-item:
     - **Key**: `NSAllowsArbitraryLoads`
     - **Type**: Boolean
     - **Value**: `YES`

## Final ScheduleView Structure

Your `ScheduleView` should now look like this:

```swift
struct ScheduleView: View {
    @State private var selectedDate = Date()
    @State private var showUpcoming = true
    @State private var showingCalendarAlert = false
    @State private var lastAddedEvent: TourEvent?
    @State private var addedEventIDs: Set<UUID> = []
    
    @StateObject private var eventsViewModel = EventsViewModel() // NEW
    
    let events: [TourEvent] = [
        // ... existing tour events
    ]
    
    // ... existing helper functions
    
    var body: some View {
        VStack(spacing: 24) {
            CustomCalendarView(selectedDate: $selectedDate, events: filteredEvents, showUpcoming: showUpcoming)
            
            // ... existing toggle buttons and tour events list
            
            // NEW: Custom Events Section
            EventsListView(viewModel: eventsViewModel)
                .padding(.top, 20)
        }
        .alert("Event Added!", isPresented: $showingCalendarAlert) {
            Button("OK") { }
        } message: {
            if let event = lastAddedEvent {
                Text("'Beyoncé - \(event.city) @ \(event.venue)' has been added to your calendar.")
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
    
    // ... existing calendar functions
}
```

## Testing the Feature

1. **Build and run** your app
2. **Navigate to the Schedule tab**
3. **Scroll down** to see the "Custom Events" section
4. **Add events** through your admin panel at `https://beyhive-alert-backend.onrender.com/admin`
5. **Refresh** the app to see the new events
6. **Users can only view events** - only admins can add/remove them

## Features Included

✅ **Automatic loading** of events from your backend  
✅ **Pull-to-refresh** functionality  
✅ **Empty state** when no events exist  
✅ **Read-only access** - users can only view events  
✅ **Error handling** for network issues  
✅ **Loading states** during network requests  
✅ **Beautiful UI** that matches your app's design  

## Security Notes

🔒 **User Permissions:**
- Users can only **view** events
- Users cannot add, edit, or remove events
- Only admins can manage events through the admin panel

🔒 **Admin Controls:**
- Events can only be added/removed through the admin panel
- Admin authentication is handled server-side
- No admin credentials are stored in the iOS app

## Troubleshooting

**If events don't load:**
- Check your internet connection
- Verify the backend URL in `EventsViewModel.swift`
- Check the browser console for any backend errors

**If the app crashes:**
- Make sure all files are added to the Xcode project
- Check that Info.plist has the network permissions
- Verify the `@StateObject` property is added correctly

## Support

If you encounter any issues, check:
1. The backend logs at Render
2. The Xcode console for error messages
3. The network tab in Xcode's debugger

The events feature is now fully integrated! 🎉 