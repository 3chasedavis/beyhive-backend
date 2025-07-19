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
        guard let time = time, let timezone = timezone else { 
            print("❌ Missing time or timezone: time='\(time ?? "nil")', timezone='\(timezone ?? "nil")'")
            return nil 
        }
        
        // Create a date string with the event date and time
        let dateTimeString = "\(dateString)T\(time)"
        print("🕐 Creating datetime string: \(dateTimeString)")
        
        // Parse the date and time in the event's timezone
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm"
        
        guard let eventTimezone = TimeZone(identifier: timezone) else {
            print("❌ Invalid timezone identifier: \(timezone)")
            return nil
        }
        
        formatter.timeZone = eventTimezone
        
        guard let eventDateInEventTimezone = formatter.date(from: dateTimeString) else {
            print("❌ Failed to parse date: \(dateTimeString) with timezone: \(timezone)")
            return nil
        }
        
        print("✅ Parsed date in event timezone: \(eventDateInEventTimezone)")
        
        // Convert to user's local timezone using Calendar
        let calendar = Calendar.current
        let userTimezone = TimeZone.current
        
        // Create a new date in the user's timezone
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute], from: eventDateInEventTimezone)
        components.timeZone = userTimezone
        
        guard let localDate = calendar.date(from: components) else {
            print("❌ Failed to create local date")
            return nil
        }
        
        print("🔄 Converted to local time: \(localDate)")
        
        return localDate
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
}

struct EventDeleteResponse: Codable {
    let success: Bool
    let message: String?
}
