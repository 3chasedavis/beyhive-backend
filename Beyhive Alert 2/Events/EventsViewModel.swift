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
    
    private let baseURL = "https://beyhive-backend.onrender.com"
    
    init() {
#if DEBUG
        if ProcessInfo.processInfo.environment["XCODE_RUNNING_FOR_PREVIEWS"] == "1" {
            // Load mock data for preview
            self.events = [
                Event(id: "1", title: "Preview Event", description: "This is a preview event.", date: Date(), location: "Preview Venue, Preview City, PC", createdAt: Date(), time: "19:00", timezone: "America/New_York")
            ]
            return
        }
#endif
        Task {
            await fetchEvents()
        }
    }
    
    func fetchEvents() async {
        print("🔄 Starting to fetch events from backend...")
        isLoading = true
        errorMessage = nil
        
        guard let url = URL(string: "\(baseURL)/api/events") else {
            print("❌ Invalid URL")
            errorMessage = "Invalid URL"
            showError = true
            isLoading = false
            return
        }
        
        print("📡 Making request to: \(url)")
        
        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                print("❌ Invalid response")
                errorMessage = "Invalid response"
                showError = true
                isLoading = false
                return
            }
            
            print("📊 Response status: \(httpResponse.statusCode)")
            
            if httpResponse.statusCode == 200 {
                let eventsResponse = try JSONDecoder().decode(EventsResponse.self, from: data)
                self.events = eventsResponse.events.sorted { $0.date < $1.date }
                print("✅ Successfully loaded \(self.events.count) events from backend")
            } else {
                print("❌ Failed to fetch events (Status: \(httpResponse.statusCode))")
                errorMessage = "Failed to fetch events (Status: \(httpResponse.statusCode))"
                showError = true
            }
        } catch {
            print("❌ Error fetching events: \(error.localizedDescription)")
            errorMessage = "Error fetching events: \(error.localizedDescription)"
            showError = true
        }
        
        isLoading = false
        print("🏁 Finished fetching events")
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
