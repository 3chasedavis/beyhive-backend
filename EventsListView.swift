//
//  EventsListView.swift
//  Beyhive Alert 2
//
//  Created by Events Feature Setup Script
//

import SwiftUI

struct EventsListView: View {
    @EnvironmentObject var eventsViewModel: EventsViewModel
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
                        await eventsViewModel.refreshEvents()
                    }
                }) {
                    Image(systemName: "arrow.clockwise")
                        .foregroundColor(.blue)
                        .font(.system(size: 18))
                }
            }
            .padding(.horizontal)
            
            if eventsViewModel.isLoading {
                ProgressView("Loading events...")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if eventsViewModel.events.isEmpty {
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
                        ForEach(eventsViewModel.events) { event in
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
                        await eventsViewModel.removeEvent(event)
                    }
                }
            }
        } message: {
            if let event = eventToRemove {
                Text("Are you sure you want to remove '\(event.title)'? This action cannot be undone.")
            }
        }
        .alert("Error", isPresented: $eventsViewModel.showError) {
            Button("OK") { }
        } message: {
            if let errorMessage = eventsViewModel.errorMessage {
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

#if DEBUG
struct EventsListView_Previews: PreviewProvider {
    static var previews: some View {
        EventsListView()
            .environmentObject(EventsViewModel())
    }
}
#endif
