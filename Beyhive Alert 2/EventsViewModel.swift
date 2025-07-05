//
//  EventsViewModel.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//

import Foundation
import SwiftUI
import Combine
import UIKit

@MainActor
class EventsViewModel: ObservableObject {
    @Published var events: [Event] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var showError = false
    
    private let baseURL = "https://beyhive-alert-backend.onrender.com"
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        Task {
            await fetchEvents()
        }
        NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)
            .sink { [weak self] _ in
                Task { await self?.refreshEvents() }
            }
            .store(in: &cancellables)
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
                let decoder = JSONDecoder()
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyy-MM-dd"
                decoder.dateDecodingStrategy = .formatted(dateFormatter)
                
                let eventsResponse = try decoder.decode(EventsResponse.self, from: data)
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
