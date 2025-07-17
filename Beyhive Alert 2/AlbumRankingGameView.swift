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
    let userId: String
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
    private var userId: String {
        UIDevice.current.identifierForVendor?.uuidString ?? "default_user"
    }
    init() {
        loadAlbums()
        Task { await fetchCommunityRankings() }
    }
    func loadAlbums() {
        self.albums = [
            Album(id: "1", title: "DANGEROUSLY IN LOVE", artist: "Beyoncé", coverImageName: "dangerous_in_love"),
            Album(id: "2", title: "B'DAY", artist: "Beyoncé", coverImageName: "bday"),
            Album(id: "3", title: "I AM... SASHA FIERCE", artist: "Beyoncé", coverImageName: "sasha_fierce"),
            Album(id: "4", title: "4", artist: "Beyoncé", coverImageName: "four"),
            Album(id: "5", title: "Self-Titled", artist: "Beyoncé", coverImageName: "beyonce"),
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
        if isLiked {
            communityRankings[index].likes.removeAll { $0 == userId }
        } else {
            communityRankings[index].likes.append(userId)
        }
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/album-rankings/\(rankingId)/like") else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        let body = LikeRequestBody(userId: self.userId)
        do {
            request.httpBody = try JSONEncoder().encode(body)
            try await URLSession.shared.data(for: request)
        } catch {
            communityRankings[index] = originalRanking
        }
    }
}

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

struct AlbumRankingGameView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel = AlbumRankingViewModel()
    @State private var showRankAlbums = false
    @State private var showCommunityWall = false
    @State private var showLikes = false
    @State private var rankedAlbums: [Album] = []
    @State private var nickname: String = ""
    var body: some View {
        VStack(spacing: 0) {
            ZStack(alignment: .leading) {
                LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    .frame(height: 110)
                    .ignoresSafeArea(edges: .top)
                HStack {
                    Button(action: { dismiss() }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.black)
                            .padding(.leading, 8)
                    }
                    Text("Album Ranker")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.black)
                        .padding(.leading, 8)
                    Spacer()
                }
                .padding(.top, 40)
            }
            .frame(height: 110)
            VStack(alignment: .leading, spacing: 18) {
                Text("Album Ranker is a space to share your personal ranking of the albums and see how the community feels. Drag to rank, post your list, and like your favorites!")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.black)
                    .padding(.horizontal)
                    .padding(.top, 12)
            }
            .padding(.bottom, 12)
            VStack(spacing: 24) {
                Button(action: { showRankAlbums = true }) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Rank Albums")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Text("drag and drop to create your album ranking")
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
                        Text("see how the community ranks the albums")
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
                Button(action: { showLikes = true }) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Likes")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Text("see all the rankings you have liked")
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
        .sheet(isPresented: $showRankAlbums) {
            AlbumRankingCreationView(
                viewModel: viewModel,
                rankedAlbums: $rankedAlbums,
                nickname: $nickname,
                showLikes: $showLikes
            )
        }
        .sheet(isPresented: $showCommunityWall) {
            CommunityWallView(viewModel: viewModel)
        }
        .sheet(isPresented: $showLikes) {
            LikesView(viewModel: viewModel)
        }
    }
}

struct AlbumRankingCreationView: View {
    @ObservedObject var viewModel: AlbumRankingViewModel
    @Binding var rankedAlbums: [Album]
    @Binding var nickname: String
    @Binding var showLikes: Bool
    @Environment(\.presentationMode) var presentationMode
    let columns: [String] = ["Best", "Great", "Good", "Okay", "Meh", "Worst"]
    @State private var columnAlbums: [String: [Album]] = [:]
    @State private var draggingAlbum: Album? = nil
    @State private var dragOverColumn: String? = nil
    @State private var showSuccess: Bool = false
    private func initializeColumns() {
        if columnAlbums.isEmpty {
            var initial: [String: [Album]] = [:]
            for col in columns { initial[col] = [] }
            if rankedAlbums.isEmpty {
                initial["Best"] = viewModel.albums
            } else {
                initial["Best"] = rankedAlbums
            }
            columnAlbums = initial
        }
    }
    var body: some View {
        ZStack {
            LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                .ignoresSafeArea()
            VStack(spacing: 0) {
                HStack {
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "chevron.left")
                            .font(.title2)
                            .foregroundColor(.blue)
                        Text("Back")
                            .foregroundColor(.blue)
                    }
                    .padding(.leading)
                    Spacer()
                }
                .padding(.top, 12)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .top, spacing: 18) {
                        ForEach(columns, id: \.self) { col in
                            VStack(alignment: .center, spacing: 8) {
                                Text(col)
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.vertical, 8)
                                    .padding(.horizontal, 12)
                                    .background(Color.black.opacity(0.7))
                                    .cornerRadius(10)
                                
                                VStack(spacing: 10) {
                                    if (columnAlbums[col] ?? []).isEmpty {
                                        Text(" ")
                                            .padding()
                                            .frame(width: 120, height: 38)
                                            .background(Color.black.opacity(0.1))
                                            .cornerRadius(10)
                                    } else {
                                        ForEach(columnAlbums[col] ?? [], id: \.id) { album in
                                            Text(album.title)
                                                .font(.system(size: 16, weight: .semibold))
                                                .foregroundColor(.black)
                                                .padding(.horizontal, 8)
                                                .frame(height: 38)
                                                .background(draggingAlbum?.id == album.id ? Color.yellow.opacity(0.5) : Color.white.opacity(0.9))
                                                .cornerRadius(10)
                                                .shadow(color: draggingAlbum?.id == album.id ? Color.yellow.opacity(0.6) : .clear, radius: 6)
                                                .onDrag {
                                                    self.draggingAlbum = album
                                                    return NSItemProvider(object: album.id as NSString)
                                                }
                                                .onDrop(of: ["public.text"], delegate: AlbumColumnDropDelegate(album: album, column: col, columnAlbums: $columnAlbums, draggingAlbum: $draggingAlbum))
                                                .multilineTextAlignment(.center)
                                                .lineLimit(nil)
                                                .minimumScaleFactor(0.7)
                                        }
                                    }
                                }
                                .frame(width: 140)
                                .frame(minHeight: 400)
                                .background(dragOverColumn == col ? Color.blue.opacity(0.2) : Color.black.opacity(0.2))
                                .cornerRadius(14)
                                .onDrop(of: ["public.text"], isTargeted: Binding(get: { dragOverColumn == col }, set: { isTargeted in
                                    dragOverColumn = isTargeted ? col : nil
                                }), perform: { providers in
                                    guard let provider = providers.first else { return false }
                                    _ = provider.loadObject(ofClass: NSString.self) { (id, _) in
                                        if let id = id as? String, let album = self.columnAlbums.values.flatMap({ $0 }).first(where: { $0.id == id }) {
                                            DispatchQueue.main.async {
                                                moveAlbum(album, to: col)
                                            }
                                        }
                                    }
                                    return true
                                })
                            }
                        }
                    }
                    .padding(.top, 24)
                    .padding(.horizontal, 12)
                }
                VStack(spacing: 16) {
                    TextField("Enter your nickname", text: $nickname)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)
                    Button(action: {
                        let ordered = columns.flatMap { columnAlbums[$0] ?? [] }
                        rankedAlbums = ordered
                        Task {
                            await viewModel.submitRanking(nickname: nickname, rankedAlbums: rankedAlbums)
                            showSuccess = true
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                showSuccess = false
                            }
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
                    if showSuccess {
                        Text("Success! Your ranking was posted.")
                            .foregroundColor(.green)
                            .font(.headline)
                            .transition(.opacity)
                    }
                }
                .padding()
                .background(.bar)
            }
        }
        .onAppear(perform: initializeColumns)
    }
    private func moveAlbum(_ album: Album, to column: String) {
        guard let fromColumn = columnAlbums.first(where: { $0.value.contains(album) })?.key, fromColumn != column else { return }
        columnAlbums[fromColumn]?.removeAll { $0.id == album.id }
        columnAlbums[column]?.append(album)
        draggingAlbum = nil
        dragOverColumn = nil
    }
}

struct AlbumColumnDropDelegate: DropDelegate {
    let album: Album
    let column: String
    @Binding var columnAlbums: [String: [Album]]
    @Binding var draggingAlbum: Album?
    func performDrop(info: DropInfo) -> Bool {
        guard let draggingAlbum = draggingAlbum else { return false }
        if let fromColumn = columnAlbums.first(where: { $0.value.contains(draggingAlbum) })?.key {
            columnAlbums[fromColumn]?.removeAll { $0.id == draggingAlbum.id }
            if let idx = columnAlbums[column]?.firstIndex(where: { $0.id == album.id }) {
                columnAlbums[column]?.insert(draggingAlbum, at: idx)
            } else {
                columnAlbums[column]?.append(draggingAlbum)
            }
        }
        self.draggingAlbum = nil
        return true
    }
    func dropEntered(info: DropInfo) {}
    func dropUpdated(info: DropInfo) -> DropProposal? { DropProposal(operation: .move) }
    func dropExited(info: DropInfo) {}
}

struct CommunityWallView: View {
    @ObservedObject var viewModel: AlbumRankingViewModel
    @Environment(\.presentationMode) var presentationMode
    @State private var searchText: String = ""
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: { presentationMode.wrappedValue.dismiss() }) {
                    Image(systemName: "chevron.left")
                        .font(.title2)
                        .foregroundColor(.blue)
                    Text("Back")
                        .foregroundColor(.blue)
                }
                .padding(.leading)
                Spacer()
            }
            .padding(.top, 12)
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                TextField("Search by nickname", text: $searchText)
                    .textFieldStyle(.roundedBorder)
            }
            .padding([.horizontal, .top])
            List(viewModel.communityRankings.filter { ranking in
                searchText.isEmpty || ranking.nickname.lowercased().contains(searchText.lowercased())
            }) { ranking in
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
        }
        .task {
            await viewModel.fetchCommunityRankings()
        }
    }
}

struct LikesView: View {
    @ObservedObject var viewModel: AlbumRankingViewModel
    @Environment(\.presentationMode) var presentationMode
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: { presentationMode.wrappedValue.dismiss() }) {
                    Image(systemName: "chevron.left")
                        .font(.title2)
                        .foregroundColor(.blue)
                    Text("Back")
                        .foregroundColor(.blue)
                }
                .padding(.leading)
                Spacer()
            }
            .padding(.top, 12)
            List(viewModel.communityRankings.filter { $0.likes.contains(UIDevice.current.identifierForVendor?.uuidString ?? "") }) { ranking in
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        Text(ranking.nickname)
                            .font(.headline)
                        Spacer()
                        HStack(spacing: 4) {
                            Image(systemName: "heart.fill")
                                .foregroundColor(.pink)
                            Text("\(ranking.likesCount)")
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
        }
        .onAppear {
            Task {
                await viewModel.fetchCommunityRankings()
            }
        }
    }
} 