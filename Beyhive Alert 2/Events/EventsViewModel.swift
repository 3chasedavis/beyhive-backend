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
