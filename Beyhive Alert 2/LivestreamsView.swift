import SwiftUI
import Foundation

struct Livestream: Identifiable, Codable {
    var id: UUID { UUID() }
    let title: String
    let platform: String
    let url: String
}

struct CountdownResponse: Codable {
    let isCountdownEnabled: Bool
}

class LivestreamsViewModel: ObservableObject {
    @Published var livestreams: [Livestream] = []
    @Published var isCountdownEnabled = false
    @Published var currentTime = Date()
    @Published var nextShowDate: Date? = nil
    
    // Timer for countdown updates
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    func fetchLivestreams() {
        print("fetchLivestreams called")
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/livestreams") else { return }
        URLSession.shared.dataTask(with: url) { data, _, _ in
            if let data = data {
                let responseString = String(data: data, encoding: .utf8) ?? "No data"
                print("📡 Livestreams response: \(responseString)")
                if let decoded = try? JSONDecoder().decode([Livestream].self, from: data) {
                    DispatchQueue.main.async {
                        self.livestreams = decoded
                        print("✅ Loaded \(decoded.count) livestreams")
                        for (index, stream) in decoded.enumerated() {
                            print("   Stream \(index + 1): title='\(stream.title)', platform='\(stream.platform)', url='\(stream.url)'")
                        }
                    }
                } else {
                    print("❌ Failed to decode livestreams data")
                }
            }
        }.resume()
    }
    
    func fetchCountdownMode() {
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/admin/countdown-mode") else { return }
        URLSession.shared.dataTask(with: url) { data, _, error in
            if let error = error {
                print("❌ Error fetching countdown mode: \(error)")
                return
            }
            if let data = data {
                do {
                    let response = try JSONDecoder().decode(CountdownResponse.self, from: data)
                    DispatchQueue.main.async {
                        self.isCountdownEnabled = response.isCountdownEnabled
                        print("✅ Countdown mode: \(response.isCountdownEnabled)")
                    }
                } catch {
                    print("❌ Error decoding countdown response: \(error)")
                }
            }
        }.resume()
    }
    
    func fetchNextShowDate() {
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/events") else { return }
        URLSession.shared.dataTask(with: url) { data, _, error in
            if let error = error {
                print("❌ Error fetching events: \(error)")
                return
            }
            if let data = data {
                do {
                    let response = try JSONDecoder().decode(EventsResponse.self, from: data)
                    DispatchQueue.main.async {
                        // Find the next upcoming event
                        let now = Date()
                        let upcomingEvents = response.events.filter { event in
                            if let localStartDate = event.localStartDate {
                                return localStartDate > now
                            } else {
                                return event.date > now
                            }
                        }
                        self.nextShowDate = upcomingEvents.first?.localStartDate ?? upcomingEvents.first?.date
                        print("✅ Next show date: \(self.nextShowDate?.description ?? "none")")
                    }
                } catch {
                    print("❌ Error decoding events response: \(error)")
                }
            }
        }.resume()
    }
    
    var countdownString: String {
        guard let nextShow = nextShowDate else { return "" }
        let timeInterval = nextShow.timeIntervalSince(currentTime)
        
        if timeInterval <= 0 { return "" }
        
        let days = Int(timeInterval) / (24 * 60 * 60)
        let hours = Int(timeInterval) % (24 * 60 * 60) / (60 * 60)
        let minutes = Int(timeInterval) % (60 * 60) / 60
        let seconds = Int(timeInterval) % 60
        
        if days > 0 {
            return "\(days)d \(hours)h \(minutes)m \(seconds)s"
        } else if hours > 0 {
            return "\(hours)h \(minutes)m \(seconds)s"
        } else if minutes > 0 {
            return "\(minutes)m \(seconds)s"
        } else {
            return "\(seconds)s"
        }
    }
}

struct EventsResponse: Codable {
    let events: [Event]
}

struct LivestreamsView: View {
    @StateObject var viewModel = LivestreamsViewModel()
    var selectedTab: Binding<BeyhiveTab>? = nil // Add this binding for tab navigation

    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            Text("Livestreams")
                .font(.title2).bold()
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.top, 16)

            // Countdown Timer (only show when enabled and no livestreams)
            if viewModel.isCountdownEnabled && viewModel.livestreams.isEmpty && !viewModel.countdownString.isEmpty {
                VStack(spacing: 8) {
                    Text("Next Show")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.gray)
                    Text(viewModel.countdownString)
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.red)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 10)
                        .background(Color.red.opacity(0.1))
                        .cornerRadius(12)
                }
                .padding(.horizontal)
                .onReceive(viewModel.timer) { _ in
                    viewModel.currentTime = Date()
                }
            }

            if viewModel.livestreams.isEmpty {
                Spacer()
                Text("Check back during the next show for livestreams!")
                    .font(.headline)
                    .foregroundColor(Color(.systemGray))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                    .padding(.bottom, 8)
                VStack(spacing: 12) {
                    HStack(spacing: 4) {
                        Image("Bee_Icon")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 40, height: 40)
                        Text("Links")
                            .font(.title2).bold()
                            .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                    }
                    .frame(maxWidth: .infinity, alignment: .center)
                }
                .padding(.bottom, 8)
                VStack(spacing: 16) {
                    // Replace Link with Button for games navigation
                    Button(action: {
                        selectedTab?.wrappedValue = .home
                    }) {
                        HStack {
                            Text("Play our games now!")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                            Spacer()
                            Text("Go")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white)
                                .padding(.horizontal, 18)
                                .padding(.vertical, 8)
                                .background(Color.white.opacity(0.2))
                                .cornerRadius(16)
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(
                            LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .leading, endPoint: .trailing)
                        )
                        .cornerRadius(22)
                    }
                    Link(destination: URL(string: "https://x.com/beyhivealertapp?s=21")!) {
                        HStack {
                            Text("Follow us on Twitter/X")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                            Spacer()
                            Text("Open")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white)
                                .padding(.horizontal, 18)
                                .padding(.vertical, 8)
                                .background(Color.white.opacity(0.2))
                                .cornerRadius(16)
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(
                            LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .leading, endPoint: .trailing)
                        )
                        .cornerRadius(22)
                    }
                }
                .padding(.horizontal, 12)
                .padding(.top, 8)
                Spacer()
            } else {
                ScrollView {
                    VStack(spacing: 16) {
                        ForEach(viewModel.livestreams) { stream in
                            HStack {
                                // Use custom icons for TikTok and Instagram
                                Group {
                                    if stream.platform.lowercased() == "tiktok" {
                                        Image("Tiktoklogo")
                                            .resizable()
                                            .frame(width: 40, height: 40)
                                    } else if stream.platform.lowercased() == "instagram" {
                                        Image("Instagramlogo")
                                            .resizable()
                                            .frame(width: 40, height: 40)
                                    } else {
                                        Image(stream.platform.lowercased())
                                            .resizable()
                                            .frame(width: 40, height: 40)
                                    }
                                }
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(stream.title.isEmpty ? stream.platform : stream.title)
                                        .font(.headline)
                                        .foregroundColor(.black)
                                    if !stream.title.isEmpty {
                                        Text(stream.platform)
                                            .font(.caption)
                                            .foregroundColor(.gray)
                                    }
                                }
                                Spacer()
                                Button(action: {
                                    var link = stream.url
                                    if !link.lowercased().hasPrefix("http") {
                                        link = "https://" + link
                                    }
                                    if let url = URL(string: link) {
                                        UIApplication.shared.open(url)
                                    }
                                }) {
                                    Text("Watch")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 20)
                                        .padding(.vertical, 10)
                                        .background(Color(red: 1.0, green: 0.95, blue: 0.4)) // Light yellow
                                        .cornerRadius(20)
                                }
                            }
                            .padding()
                            .background(Color.white)
                            .cornerRadius(20)
                            .shadow(color: Color.black.opacity(0.12), radius: 8, x: 0, y: 3)
                            .padding(.horizontal)
                        }
                    }
                    .padding(.top, 8)
                }
            }
        }
        .background(Color.white.ignoresSafeArea())
        .onAppear {
            print("LivestreamsView appeared")
            viewModel.fetchLivestreams()
            viewModel.fetchCountdownMode()
            viewModel.fetchNextShowDate()
        }
    }
} 