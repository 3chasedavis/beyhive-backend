import SwiftUI
import Foundation

struct Livestream: Identifiable, Codable {
    var id: UUID { UUID() }
    let platform: String
    let url: String
}

class LivestreamsViewModel: ObservableObject {
    @Published var livestreams: [Livestream] = []

    func fetchLivestreams() {
        print("fetchLivestreams called")
        guard let url = URL(string: "http://localhost:3000/api/livestreams") else { return }
        URLSession.shared.dataTask(with: url) { data, _, _ in
            if let data = data {
                print(String(data: data, encoding: .utf8) ?? "No data") // Debug print statement
                if let decoded = try? JSONDecoder().decode([Livestream].self, from: data) {
                    DispatchQueue.main.async {
                        self.livestreams = decoded
                    }
                }
            }
        }.resume()
    }
}

struct LivestreamsView: View {
    @StateObject var viewModel = LivestreamsViewModel()

    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            Text("Livestreams")
                .font(.title2).bold()
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.top, 16)

            if viewModel.livestreams.isEmpty {
                Spacer()
                Text("There are no available livestreams at this time, check back later.")
                    .font(.headline)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding()
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
                                Text(stream.platform)
                                    .font(.headline)
                                    .foregroundColor(.black)
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
        }
    }
} 