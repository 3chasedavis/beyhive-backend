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
    let time: String? // New: time in HH:mm format
    
    init(id: String = UUID().uuidString, title: String, description: String, date: Date, location: String? = nil, createdAt: Date = Date(), time: String? = nil) {
        self.id = id
        self.title = title
        self.description = description
        self.date = date
        self.location = location
        self.createdAt = createdAt
        self.time = time
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
