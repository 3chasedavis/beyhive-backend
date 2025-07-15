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
        NavigationView {
            VStack(alignment: .leading, spacing: 0) {
                Text("Outfits")
                    .font(.system(size: 22, weight: .heavy))
                    .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                    .padding(.leading, 16)
                    .padding(.top, 12)
                List {
                    ForEach(viewModel.outfits) { outfit in
                        HStack(alignment: .center, spacing: 14) {
                            Image(outfit.imageName)
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 40, height: 40)
                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                .cornerRadius(8)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(outfit.name)
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(Color(red: 0.13, green: 0.15, blue: 0.28))
                                Text(outfit.location)
                                    .font(.system(size: 13))
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            if outfit.isNew {
                                Image(systemName: "kiss") // Replace with your custom lips icon if needed
                                    .resizable()
                                    .frame(width: 20, height: 20)
                                    .foregroundColor(.red)
                            }
                        }
                        .padding(.vertical, 6)
                        .background(Color.white)
                        .cornerRadius(10)
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                    }
                }
                .listStyle(PlainListStyle())
                .padding(.top, 0)
            }
            .navigationBarHidden(true)
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