import Foundation

@MainActor
class OutfitsViewModel: ObservableObject {
    @Published var outfits: [Outfit] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let baseURL = "https://beyhive-backend.onrender.com"

    func fetchOutfits() async {
        isLoading = true
        errorMessage = nil
        guard let url = URL(string: "\(baseURL)/api/outfits") else {
            errorMessage = "Invalid URL"
            isLoading = false
            return
        }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let decoded = try JSONDecoder().decode(OutfitsResponse.self, from: data)
            self.outfits = decoded.outfits
        } catch {
            errorMessage = "Failed to load outfits: \(error.localizedDescription)"
        }
        isLoading = false
    }
}

struct OutfitsResponse: Codable {
    let outfits: [Outfit]
} 
