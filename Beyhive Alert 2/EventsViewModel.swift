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
    
    func refreshEvents() async {
        await fetchEvents()
    }
}
