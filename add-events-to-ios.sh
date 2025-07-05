#!/bin/bash

# Beyhive Alert iOS Events Feature Setup Script
# This script adds the events feature to the iOS app

echo "🐝 Setting up Events Feature for Beyhive Alert iOS App..."
echo "=================================================="

# Create the Events directory
mkdir -p "Beyhive Alert 2/Events"

# 1. Create Event Model
echo "📝 Creating Event Model..."
cat > "Beyhive Alert 2/Events/Event.swift" << 'EOF'
//
//  Event.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//

import Foundation

struct Event: Identifiable, Codable {
    let id: String
    let title: String
    let description: String
    let date: Date
    let location: String?
    let createdAt: Date
    
    init(id: String = UUID().uuidString, title: String, description: String, date: Date, location: String? = nil, createdAt: Date = Date()) {
        self.id = id
        self.title = title
        self.description = description
        self.date = date
        self.location = location
        self.createdAt = createdAt
    }
}

// MARK: - Event Response Models
struct EventsResponse: Codable {
    let events: [Event]
    let success: Bool
    let message: String?
}

struct EventDeleteResponse: Codable {
    let success: Bool
    let message: String?
}
EOF

# 2. Create Events View Model
echo "📱 Creating Events View Model..."
cat > "Beyhive Alert 2/Events/EventsViewModel.swift" << 'EOF'
//
//  EventsViewModel.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//

import Foundation
import SwiftUI

@MainActor
class EventsViewModel: ObservableObject {
    @Published var events: [Event] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var showError = false
    
    private let baseURL = "https://beyhive-alert-backend.onrender.com"
    
    init() {
        Task {
            await fetchEvents()
        }
    }
    
    func fetchEvents() async {
        isLoading = true
        errorMessage = nil
        
        guard let url = URL(string: "\(baseURL)/api/events") else {
            errorMessage = "Invalid URL"
            showError = true
            isLoading = false
            return
        }
        
        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                errorMessage = "Invalid response"
                showError = true
                isLoading = false
                return
            }
            
            if httpResponse.statusCode == 200 {
                let eventsResponse = try JSONDecoder().decode(EventsResponse.self, from: data)
                self.events = eventsResponse.events.sorted { $0.date < $1.date }
            } else {
                errorMessage = "Failed to fetch events (Status: \(httpResponse.statusCode))"
                showError = true
            }
        } catch {
            errorMessage = "Error fetching events: \(error.localizedDescription)"
            showError = true
        }
        
        isLoading = false
    }
    
    func removeEvent(_ event: Event) async {
        guard let url = URL(string: "\(baseURL)/api/events/\(event.id)") else {
            errorMessage = "Invalid URL"
            showError = true
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "DELETE"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Add admin password if needed
        let adminPassword = "your-admin-password" // Replace with actual admin password
        request.setValue(adminPassword, forHTTPHeaderField: "X-Admin-Password")
        
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                errorMessage = "Invalid response"
                showError = true
                return
            }
            
            if httpResponse.statusCode == 200 {
                // Remove from local array
                events.removeAll { $0.id == event.id }
            } else {
                let deleteResponse = try? JSONDecoder().decode(EventDeleteResponse.self, from: data)
                errorMessage = deleteResponse?.message ?? "Failed to remove event (Status: \(httpResponse.statusCode))"
                showError = true
            }
        } catch {
            errorMessage = "Error removing event: \(error.localizedDescription)"
            showError = true
        }
    }
    
    func refreshEvents() async {
        await fetchEvents()
    }
}
EOF

# 3. Create Events List View
echo "📋 Creating Events List View..."
cat > "Beyhive Alert 2/Events/EventsListView.swift" << 'EOF'
//
//  EventsListView.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//

import SwiftUI

struct EventsListView: View {
    @ObservedObject var viewModel: EventsViewModel
    @State private var showingRemoveAlert = false
    @State private var eventToRemove: Event?
    
    var body: some View {
        VStack(spacing: 16) {
            // Header
            HStack {
                Text("Custom Events")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(.black)
                Spacer()
                Button(action: {
                    Task {
                        await viewModel.refreshEvents()
                    }
                }) {
                    Image(systemName: "arrow.clockwise")
                        .foregroundColor(.blue)
                        .font(.system(size: 18))
                }
            }
            .padding(.horizontal)
            
            if viewModel.isLoading {
                ProgressView("Loading events...")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if viewModel.events.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "calendar.badge.plus")
                        .font(.system(size: 48))
                        .foregroundColor(.gray)
                    Text("No custom events yet")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.gray)
                    Text("Events added in the admin panel will appear here")
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding()
            } else {
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(viewModel.events) { event in
                            EventRowView(event: event) {
                                eventToRemove = event
                                showingRemoveAlert = true
                            }
                        }
                    }
                    .padding(.horizontal)
                }
            }
        }
        .alert("Remove Event", isPresented: $showingRemoveAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Remove", role: .destructive) {
                if let event = eventToRemove {
                    Task {
                        await viewModel.removeEvent(event)
                    }
                }
            }
        } message: {
            if let event = eventToRemove {
                Text("Are you sure you want to remove '\(event.title)'? This action cannot be undone.")
            }
        }
        .alert("Error", isPresented: $viewModel.showError) {
            Button("OK") { }
        } message: {
            if let errorMessage = viewModel.errorMessage {
                Text(errorMessage)
            }
        }
    }
}

struct EventRowView: View {
    let event: Event
    let onRemove: () -> Void
    
    private var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(event.title)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.black)
                    
                    if let location = event.location {
                        Text(location)
                            .font(.system(size: 14))
                            .foregroundColor(.gray)
                    }
                    
                    Text(dateFormatter.string(from: event.date))
                        .font(.system(size: 12))
                        .foregroundColor(.blue)
                }
                
                Spacer()
                
                Button(action: onRemove) {
                    Image(systemName: "trash.circle.fill")
                        .foregroundColor(.red)
                        .font(.system(size: 24))
                }
                .buttonStyle(PlainButtonStyle())
            }
            
            if !event.description.isEmpty {
                Text(event.description)
                    .font(.system(size: 14))
                    .foregroundColor(.gray)
                    .lineLimit(3)
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

#Preview {
    EventsListView(viewModel: EventsViewModel())
}
EOF

# 4. Create the integration patch for ScheduleView
echo "🔧 Creating ScheduleView integration patch..."
cat > "Beyhive Alert 2/Events/ScheduleViewPatch.swift" << 'EOF'
//
//  ScheduleViewPatch.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//
//  INSTRUCTIONS: Add this code to the bottom of your ScheduleView, just before the closing brace
//

// Add this property to ScheduleView struct:
// @StateObject private var eventsViewModel = EventsViewModel()

// Add this view at the bottom of the VStack in ScheduleView body, after the existing events list:
/*
            // Custom Events Section
            EventsListView(viewModel: eventsViewModel)
                .padding(.top, 20)
*/
EOF

# 5. Create Info.plist update instructions
echo "📋 Creating Info.plist update instructions..."
cat > "Beyhive Alert 2/Events/InfoPlistUpdates.md" << 'EOF'
# Info.plist Updates Required

Add the following keys to your Info.plist file to ensure proper network access:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

This allows the app to make network requests to your backend server.
EOF

# 6. Create the main setup script
echo "🚀 Creating main setup script..."
cat > "setup-events-feature.sh" << 'EOF'
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
EOF

chmod +x setup-events-feature.sh

echo "✅ All files created successfully!"
echo ""
echo "📋 Summary of created files:"
echo "============================"
echo "• Event.swift - Event model and response types"
echo "• EventsViewModel.swift - Network and data management"
echo "• EventsListView.swift - UI for displaying events"
echo "• ScheduleViewPatch.swift - Integration instructions"
echo "• InfoPlistUpdates.md - Required Info.plist changes"
echo "• setup-events-feature.sh - Main setup script"
echo ""
echo "🚀 To complete the setup, run:"
echo "   ./setup-events-feature.sh"
echo ""
echo "Then follow the instructions to add the files to Xcode!" 