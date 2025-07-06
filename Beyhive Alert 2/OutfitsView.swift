import SwiftUI
import UIKit

struct Outfit: Identifiable, Codable {
    let id: String
    let name: String
    let location: String
    let imageName: String // Asset name
    let isNew: Bool
    let section: String   // e.g. "Houston", "Washington", "Los Angeles", "Other"
    let description: String?
}

struct OutfitsView: View {
    @StateObject private var viewModel = OutfitsViewModel()

    var body: some View {
        let sections = Dictionary(grouping: viewModel.outfits, by: { $0.section })
        let sectionOrder = ["Houston", "Washington", "Los Angeles", "Other"]
        NavigationView {
            List {
                ForEach(sectionOrder, id: \.self) { section in
                    if let sectionOutfits = sections[section] {
                        Section(header: Text(section).font(.title2).bold()) {
                            ForEach(sectionOutfits) { outfit in
                                HStack(alignment: .center, spacing: 16) {
                                    Image(outfit.imageName)
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                        .frame(width: 48, height: 48)
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(outfit.name)
                                            .font(.system(size: 20, weight: .bold))
                                            .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                                        Text(outfit.location)
                                            .font(.system(size: 16))
                                            .foregroundColor(.gray)
                                        if let desc = outfit.description {
                                            Text(desc)
                                                .font(.caption)
                                                .foregroundColor(.secondary)
                                        }
                                    }
                                    Spacer()
                                    if outfit.isNew {
                                        Image(systemName: "kiss") // Replace with your custom lips icon if needed
                                            .resizable()
                                            .frame(width: 24, height: 24)
                                            .foregroundColor(.red)
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Outfits")
            .task {
                await viewModel.fetchOutfits()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)) { _ in
                Task {
                    await viewModel.fetchOutfits()
                }
            }
        }
    }
}

struct OutfitsView_Previews: PreviewProvider {
    static var previews: some View {
        OutfitsView()
            .environmentObject(EventsViewModel())
    }
} 