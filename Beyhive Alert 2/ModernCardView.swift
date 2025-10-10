//
//  ModernCardView.swift
//  Beyhive Alert 2
//
//  Created by Chase Davis
//

import SwiftUI

struct ModernCardView: View {
    let icon: String
    let title: String
    let description: String
    let gradientColors: [Color]
    let iconColor: Color
    let textColor: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                // Left icon section
                VStack {
                    Image(systemName: icon)
                        .font(.system(size: 32, weight: .semibold))
                        .foregroundColor(iconColor)
                        .frame(width: 50, height: 50)
                        .background(
                            Circle()
                                .fill(Color.white.opacity(0.2))
                        )
                }
                
                // Content section
                VStack(alignment: .leading, spacing: 8) {
                    Text(title.uppercased())
                        .font(.system(size: 18, weight: .bold, design: .rounded))
                        .foregroundColor(textColor)
                    
                    Text(description)
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(textColor.opacity(0.9))
                        .multilineTextAlignment(.leading)
                        .lineLimit(3)
                }
                
                Spacer()
            }
            .padding(16)
            .frame(width: 320, height: 140)
            .background(
                LinearGradient(
                    gradient: Gradient(colors: gradientColors),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .cornerRadius(20)
            .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct ModernSectionHeader: View {
    let title: String
    let iconName: String
    
    var body: some View {
        HStack(spacing: 4) {
            Image(iconName)
                .resizable()
                .scaledToFit()
                .frame(width: 50, height: 50)
            
            Text(title)
                .font(.system(size: 32, weight: .bold, design: .rounded))
                .foregroundColor(.black)
                .shadow(color: .black.opacity(0.1), radius: 1, x: 0, y: 1)
            
            Spacer()
        }
        .padding(.horizontal)
    }
}

struct ModernPartnerCard: View {
    let partner: Partner
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                // Left icon section
                VStack {
                    if let iconUrl = partner.iconUrl, !iconUrl.isEmpty, let url = URL(string: iconUrl) {
                        AsyncImage(url: url) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                        } placeholder: {
                            Circle()
                                .fill(Color.gray.opacity(0.3))
                                .overlay(
                                    ProgressView()
                                        .scaleEffect(0.8)
                                )
                        }
                        .frame(width: 50, height: 50)
                        .clipShape(Circle())
                    } else {
                        Image(systemName: "building.2")
                            .font(.system(size: 32, weight: .semibold))
                            .foregroundColor(.black)
                            .frame(width: 50, height: 50)
                            .background(
                                Circle()
                                    .fill(Color.white.opacity(0.2))
                            )
                    }
                }
                
                // Content section
                VStack(alignment: .leading, spacing: 8) {
                    Text(partner.name.uppercased())
                        .font(.system(size: 18, weight: .bold, design: .rounded))
                        .foregroundColor(.black)
                    
                    Text(partner.description)
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.black.opacity(0.9))
                        .multilineTextAlignment(.leading)
                        .lineLimit(3)
                }
                
                Spacer()
            }
            .padding(16)
            .frame(width: 320, height: 100)
            .background(
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.yellow.opacity(0.4), // Light yellow
                        Color.yellow.opacity(0.2)  // Very light yellow
                    ]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .cornerRadius(20)
            .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - Predefined Card Styles
extension ModernCardView {
    // Game cards with vibrant gradients
    static func survivorCard(action: @escaping () -> Void) -> ModernCardView {
        ModernCardView(
            icon: "gamecontroller.fill",
            title: "Survivor Game",
            description: "Guess outfits, songs, and more during every show!",
            gradientColors: [
                Color.red,
                Color.white,
                Color.blue
            ],
            iconColor: .black,
            textColor: .black,
            action: action
        )
    }
    
    static func triviaCard(action: @escaping () -> Void) -> ModernCardView {
        ModernCardView(
            icon: "questionmark.circle.fill",
            title: "Daily Trivia",
            description: "Test your Beyoncé knowledge with a new question every day!",
            gradientColors: [
                Color.red,
                Color.white,
                Color.blue
            ],
            iconColor: .black,
            textColor: .black,
            action: action
        )
    }
    
    static func albumRankerCard(action: @escaping () -> Void) -> ModernCardView {
        ModernCardView(
            icon: "music.note.list",
            title: "Album Ranker",
            description: "Rank Beyoncé's albums and see community favorites!",
            gradientColors: [
                Color.red,
                Color.white,
                Color.blue
            ],
            iconColor: .black,
            textColor: .black,
            action: action
        )
    }
    
    static func dailyGamesCard(action: @escaping () -> Void) -> ModernCardView {
        ModernCardView(
            icon: "gamecontroller.fill",
            title: "Daily Games",
            description: "Play all your favorite Beyoncé games in one place!",
            gradientColors: [
                Color.yellow,
                Color.orange,
                Color.pink
            ],
            iconColor: .black,
            textColor: .black,
            action: action
        )
    }
}

#if DEBUG
struct ModernCardView_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            ModernCardView.survivorCard { }
            ModernCardView.triviaCard { }
            ModernCardView.albumRankerCard { }
        }
        .padding()
        .background(Color.gray.opacity(0.1))
    }
}
#endif
