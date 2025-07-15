import SwiftUI

// MARK: - Models
struct Album: Identifiable, Codable, Equatable {
    let id: String
    let title: String
    let artist: String
    let coverImageName: String
}

struct AlbumRanking: Identifiable, Codable {
    let id: String
    let nickname: String
    let ranking: [Album]
    var likes: [String]
    let createdAt: String
    let userId: String // <-- Add this line
    
    var likesCount: Int { likes.count }
}

// MARK: - View Models
@MainActor
class AlbumRankingViewModel: ObservableObject {
    @Published var albums: [Album] = []
    @Published var communityRankings: [AlbumRanking] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var hasSubmitted: Bool = false
    
    // User's unique identifier (can be device ID or another stable ID)
    private var userId: String {
        UIDevice.current.identifierForVendor?.uuidString ?? "default_user"
    }
    
    init() {
        loadAlbums()
        Task {
            await fetchCommunityRankings()
        }
    }
    
    func loadAlbums() {
        // In a real app, this would come from a server or a local JSON file
        self.albums = [
            Album(id: "1", title: "DANGEROUSLY IN LOVE", artist: "Beyoncé", coverImageName: "dangerous_in_love"),
            Album(id: "2", title: "B'DAY", artist: "Beyoncé", coverImageName: "bday"),
            Album(id: "3", title: "I AM... SASHA FIERCE", artist: "Beyoncé", coverImageName: "sasha_fierce"),
            Album(id: "4", title: "4", artist: "Beyoncé", coverImageName: "four"),
            Album(id: "5", title: "BEYONCÉ", artist: "Beyoncé", coverImageName: "beyonce"),
            Album(id: "6", title: "LEMONADE", artist: "Beyoncé", coverImageName: "lemonade"),
            Album(id: "7", title: "RENAISSANCE", artist: "Beyoncé", coverImageName: "renaissance"),
            Album(id: "8", title: "COWBOY CARTER", artist: "Beyoncé", coverImageName: "cowboy_carter")
        ]
    }
    
    func fetchCommunityRankings() async {
        isLoading = true
        errorMessage = nil
        
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/album-rankings") else {
            errorMessage = "Invalid URL"
            isLoading = false
            return
        }
        
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(CommunityRankingsResponse.self, from: data)
            self.communityRankings = response.rankings
            
            // Check if user has already submitted
            self.hasSubmitted = communityRankings.contains(where: { $0.userId == self.userId })
            
        } catch {
            errorMessage = "Failed to fetch rankings: \(error.localizedDescription)"
        }
        
        isLoading = false
    }
    
    func submitRanking(nickname: String, rankedAlbums: [Album]) async {
        isLoading = true
        errorMessage = nil
        
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/album-rankings") else {
            errorMessage = "Invalid URL"
            isLoading = false
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body = SubmissionRequestBody(nickname: nickname, ranking: rankedAlbums, userId: self.userId)
        
        do {
            request.httpBody = try JSONEncoder().encode(body)
            let (data, response) = try await URLSession.shared.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 201 else {
                throw URLError(.badServerResponse)
            }
            
            let newRanking = try JSONDecoder().decode(SubmissionResponseBody.self, from: data)
            communityRankings.insert(newRanking.ranking, at: 0)
            hasSubmitted = true
            
        } catch {
            errorMessage = "Failed to submit ranking: \(error.localizedDescription)"
        }
        
        isLoading = false
    }
    
    func toggleLike(for rankingId: String) async {
        guard let index = communityRankings.firstIndex(where: { $0.id == rankingId }) else { return }
        
        let originalRanking = communityRankings[index]
        let isLiked = originalRanking.likes.contains(userId)
        
        // Optimistic UI update
        if isLiked {
            communityRankings[index].likes.removeAll { $0 == userId }
        } else {
            communityRankings[index].likes.append(userId)
        }
        
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/album-rankings/\(rankingId)/like") else {
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body = LikeRequestBody(userId: self.userId)
        
        do {
            request.httpBody = try JSONEncoder().encode(body)
            try await URLSession.shared.data(for: request)
        } catch {
            // Revert optimistic update on failure
            communityRankings[index] = originalRanking
        }
    }
}

// MARK: - API Response Structs
struct CommunityRankingsResponse: Codable {
    let success: Bool
    let rankings: [AlbumRanking]
}

struct SubmissionRequestBody: Codable {
    let nickname: String
    let ranking: [Album]
    let userId: String
}

struct SubmissionResponseBody: Codable {
    let success: Bool
    let message: String
    let ranking: AlbumRanking
}

struct LikeRequestBody: Codable {
    let userId: String
}

// MARK: - Main Game View
struct AlbumRankingGameView: View {
    @StateObject private var viewModel = AlbumRankingViewModel()
    @State private var showRankAlbums = false
    @State private var showCommunityWall = false
    @State private var showFavorites = false
    @State private var rankedAlbums: [Album] = []
    @State private var nickname: String = ""
    
    var body: some View {
        VStack(spacing: 0) {
            // Header
            ZStack(alignment: .leading) {
                LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    .frame(height: 110)
                    .ignoresSafeArea(edges: .top)
                HStack {
                    Text("Album Ranker")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.black)
                        .padding(.leading, 24)
                    Spacer()
                }
                .padding(.top, 40)
            }
            .frame(height: 110)
            
            // Intro
            VStack(alignment: .leading, spacing: 18) {
                Text("Album Ranker is a space to share your personal ranking of Beyoncé's albums and see how the community feels. Drag to rank, post your list, and like your favorites!")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.black)
                    .padding(.horizontal)
                    .padding(.top, 12)
            }
            .padding(.bottom, 12)
            
            // Main Buttons
            VStack(spacing: 24) {
                Button(action: { showRankAlbums = true }) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Rank Albums")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Text("drag and drop to create your Beyoncé album ranking")
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(.black.opacity(0.7))
                    }
                    .padding(.vertical, 18)
                    .padding(.leading, 24)
                    .frame(maxWidth: .infinity, minHeight: 70, alignment: .leading)
                    .background(
                        LinearGradient(gradient: Gradient(colors: [Color.red.opacity(0.18), Color.white.opacity(0.18), Color.blue.opacity(0.18)]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    )
                    .cornerRadius(22)
                }
                Button(action: { showCommunityWall = true }) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Read Community Ranks")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Text("see how the community ranks Beyoncé's albums")
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(.black.opacity(0.7))
                    }
                    .padding(.vertical, 18)
                    .padding(.leading, 24)
                    .frame(maxWidth: .infinity, minHeight: 70, alignment: .leading)
                    .background(
                        LinearGradient(gradient: Gradient(colors: [Color.red.opacity(0.18), Color.white.opacity(0.18), Color.blue.opacity(0.18)]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    )
                    .cornerRadius(22)
                }
                Button(action: { showFavorites = true }) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Favorites")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Text("reread your favorite community rankings")
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(.black.opacity(0.7))
                    }
                    .padding(.vertical, 18)
                    .padding(.leading, 24)
                    .frame(maxWidth: .infinity, minHeight: 70, alignment: .leading)
                    .background(
                        LinearGradient(gradient: Gradient(colors: [Color.red.opacity(0.18), Color.white.opacity(0.18), Color.blue.opacity(0.18)]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    )
                    .cornerRadius(22)
                }
            }
            .padding(.horizontal)
            .padding(.top, 12)
            Spacer()
        }
        .background(Color.white.ignoresSafeArea())
        // Navigation
        .sheet(isPresented: $showRankAlbums) {
            AlbumRankingCreationView(
                viewModel: viewModel,
                rankedAlbums: $rankedAlbums,
                nickname: $nickname,
                showCommunityWall: .constant(false)
            )
        }
        .sheet(isPresented: $showCommunityWall) {
            CommunityWallView(viewModel: viewModel)
        }
        // Favorites sheet can be implemented later
    }
}

// MARK: - Ranking Creation View
struct AlbumRankingCreationView: View {
    @ObservedObject var viewModel: AlbumRankingViewModel
    @Binding var rankedAlbums: [Album]
    @Binding var nickname: String
    @Binding var showCommunityWall: Bool
    
    let tiers: [String] = ["S", "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"]
    @State private var tierAlbums: [String: [Album]] = [:]
    @State private var draggingAlbum: Album? = nil
    @State private var dragOverTier: String? = nil
    
    private func initializeTiers() {
        if tierAlbums.isEmpty {
            var initial: [String: [Album]] = [:]
            for tier in tiers { initial[tier] = [] }
            if rankedAlbums.isEmpty {
                initial["S"] = viewModel.albums
            } else {
                initial["S"] = rankedAlbums
            }
            tierAlbums = initial
        }
    }
    
    var body: some View {
        ZStack {
            LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                .ignoresSafeArea()
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 12) {
                        ForEach(tiers, id: \.self) { tier in
                            HStack(alignment: .top, spacing: 8) {
                                Text(tier)
                                    .font(.system(size: 22, weight: .bold))
                                    .frame(width: 44, alignment: .center)
                                    .foregroundColor(.white)
                                    .padding(.vertical, 8)
                                    .background(Color.black.opacity(0.7))
                                    .cornerRadius(10)
                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 10) {
                                        ForEach(tierAlbums[tier] ?? [], id: \.id) { album in
                                            VStack(spacing: 4) {
                                                Image(album.coverImageName)
                                                    .resizable()
                                                    .scaledToFit()
                                                    .frame(width: 60, height: 60)
                                                    .background(Color.white.opacity(0.7))
                                                    .cornerRadius(8)
                                                Text(album.title)
                                                    .font(.caption)
                                                    .foregroundColor(.black)
                                                    .frame(width: 70)
                                                    .multilineTextAlignment(.center)
                                            }
                                            .padding(6)
                                            .background(draggingAlbum?.id == album.id ? Color.yellow.opacity(0.5) : Color.white.opacity(0.9))
                                            .cornerRadius(12)
                                            .shadow(color: draggingAlbum?.id == album.id ? Color.yellow.opacity(0.6) : .clear, radius: 8)
                                            .onDrag {
                                                self.draggingAlbum = album
                                                return NSItemProvider(object: album.id as NSString)
                                            }
                                            .onDrop(of: ["public.text"], delegate: AlbumDropDelegate(album: album, tier: tier, tierAlbums: $tierAlbums, draggingAlbum: $draggingAlbum))
                                        }
                                    }
                                    .padding(.vertical, 6)
                                }
                                .onDrop(of: ["public.text"], isTargeted: Binding(get: { dragOverTier == tier }, set: { _ in }), perform: { providers in
                                    guard let provider = providers.first else { return false }
                                    _ = provider.loadObject(ofClass: NSString.self) { (id, _) in
                                        if let id = id as? String, let album = self.tierAlbums.values.flatMap({ $0 }).first(where: { $0.id == id }) {
                                            DispatchQueue.main.async {
                                                moveAlbum(album, to: tier)
                                            }
                                        }
                                    }
                                    return true
                                })
                                .background(dragOverTier == tier ? Color.blue.opacity(0.15) : Color.clear)
                                .cornerRadius(14)
                            }
                            .padding(.horizontal, 8)
                        }
                    }
                    .padding(.top, 16)
                }
                VStack(spacing: 16) {
                    TextField("Enter your nickname", text: $nickname)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)
                    Button(action: {
                        let ordered = tiers.flatMap { tierAlbums[$0] ?? [] }
                        rankedAlbums = ordered
                        Task {
                            await viewModel.submitRanking(nickname: nickname, rankedAlbums: rankedAlbums)
                        }
                    }) {
                        Text("Post to Community Wall")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(12)
                    }
                    .disabled(nickname.isEmpty || viewModel.isLoading)
                    .padding(.horizontal)
                    if let error = viewModel.errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                }
                .padding()
                .background(.bar)
            }
        }
        .onAppear(perform: initializeTiers)
    }
    
    private func moveAlbum(_ album: Album, to tier: String) {
        guard let fromTier = tierAlbums.first(where: { $0.value.contains(album) })?.key, fromTier != tier else { return }
        tierAlbums[fromTier]?.removeAll { $0.id == album.id }
        tierAlbums[tier]?.append(album)
        draggingAlbum = nil
        dragOverTier = nil
    }
}

// Drop delegate for album-on-album drop (optional, for reordering within a tier)
struct AlbumDropDelegate: DropDelegate {
    let album: Album
    let tier: String
    @Binding var tierAlbums: [String: [Album]]
    @Binding var draggingAlbum: Album?
    
    func performDrop(info: DropInfo) -> Bool {
        guard let draggingAlbum = draggingAlbum else { return false }
        if let fromTier = tierAlbums.first(where: { $0.value.contains(draggingAlbum) })?.key {
            tierAlbums[fromTier]?.removeAll { $0.id == draggingAlbum.id }
            if let idx = tierAlbums[tier]?.firstIndex(where: { $0.id == album.id }) {
                tierAlbums[tier]?.insert(draggingAlbum, at: idx)
            } else {
                tierAlbums[tier]?.append(draggingAlbum)
            }
        }
        self.draggingAlbum = nil
        return true
    }
    
    func dropEntered(info: DropInfo) {
        // Optional: highlight
    }
    
    func dropUpdated(info: DropInfo) -> DropProposal? {
        DropProposal(operation: .move)
    }
    
    func dropExited(info: DropInfo) {
        // Optional: unhighlight
    }
}

// MARK: - Community Wall View
struct CommunityWallView: View {
    @ObservedObject var viewModel: AlbumRankingViewModel
    
    var body: some View {
        List(viewModel.communityRankings) { ranking in
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text(ranking.nickname)
                        .font(.headline)
                    Spacer()
                    Button(action: {
                        Task {
                            await viewModel.toggleLike(for: ranking.id)
                        }
                    }) {
                        HStack(spacing: 4) {
                            Image(systemName: ranking.likes.contains(UIDevice.current.identifierForVendor?.uuidString ?? "") ? "heart.fill" : "heart")
                                .foregroundColor(.pink)
                            Text("\(ranking.likesCount)")
                        }
                    }
                }
                
                ForEach(Array(ranking.ranking.enumerated()), id: \.element.id) { index, album in
                    HStack {
                        Text("\(index + 1).")
                            .fontWeight(.bold)
                        Text(album.title)
                    }
                    .font(.subheadline)
                }
            }
            .padding(.vertical)
        }
        .listStyle(.plain)
        .onAppear {
            Task {
                await viewModel.fetchCommunityRankings()
            }
        }
    }
}

// MARK: - Helper Views
struct AlbumRow: View {
    let album: Album
    
    var body: some View {
        HStack(spacing: 16) {
            // Placeholder for album art
            Image(systemName: "music.note")
                .resizable()
                .scaledToFit()
                .frame(width: 50, height: 50)
                .background(Color.gray.opacity(0.2))
                .cornerRadius(8)
            
            VStack(alignment: .leading) {
                Text(album.title)
                    .font(.headline)
                Text(album.artist)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 8)
    }
}

struct AlbumRankingGameView_Previews: PreviewProvider {
    static var previews: some View {
        AlbumRankingGameView()
    }
} 