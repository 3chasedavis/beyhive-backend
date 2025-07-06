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
    let timezone: String? // New: IANA timezone string (e.g., "America/Los_Angeles")

    init(id: String = UUID().uuidString, title: String, description: String, date: Date, location: String? = nil, createdAt: Date = Date(), time: String? = nil, timezone: String? = nil) {
        self.id = id
        self.title = title
        self.description = description
        self.date = date
        self.location = location
        self.createdAt = createdAt
        self.time = time
        self.timezone = timezone
    }
    
    enum CodingKeys: String, CodingKey {
        case id, title, description, date, location, createdAt, time, timezone
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(String.self, forKey: .id)
        title = try container.decode(String.self, forKey: .title)
        description = try container.decode(String.self, forKey: .description)
        
        // Custom date decoding
        let dateString = try container.decode(String.self, forKey: .date)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        if let decodedDate = dateFormatter.date(from: dateString) {
            date = decodedDate
        } else {
            print("[Event Decoding Error] Invalid date format: \(dateString)")
            throw DecodingError.dataCorruptedError(forKey: .date, in: container, debugDescription: "Invalid date format: \(dateString)")
        }
        
        location = try container.decodeIfPresent(String.self, forKey: .location)
        time = try container.decodeIfPresent(String.self, forKey: .time)
        timezone = try container.decodeIfPresent(String.self, forKey: .timezone)
        
        // Handle createdAt date (optional, can be missing or null)
        if let createdAtString = try? container.decodeIfPresent(String.self, forKey: .createdAt) {
            let createdAtFormatter = DateFormatter()
            createdAtFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
            createdAt = createdAtFormatter.date(from: createdAtString) ?? Date()
        } else {
            createdAt = Date()
        }
    }

    // Computed property: event start time as Date in user's local timezone
    var localStartDate: Date? {
        guard let time = time, let timezone = timezone else { return nil }
        let dateTimeString = "\(dateString)T\(time)"
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm"
        formatter.timeZone = TimeZone(identifier: timezone)
        return formatter.date(from: dateTimeString)
    }
    // Helper to get the date string in yyyy-MM-dd format
    var dateString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
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
