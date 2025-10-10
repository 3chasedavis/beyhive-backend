//
//  UpdateView.swift
//  Beyhive Alert 2
//
//  Created by Chase Davis
//

import SwiftUI

struct UpdateView: View {
    let currentVersion: String
    let latestVersion: String
    let updateMessage: String?
    let onUpdateTap: () -> Void
    
    var body: some View {
        ZStack {
            // Background gradient matching our app theme
            LinearGradient(
                gradient: Gradient(colors: [
                    Color(red: 0.92, green: 0.87, blue: 0.65), // Light yellow/cream
                    Color(red: 0.95, green: 0.90, blue: 0.70)  // Slightly darker yellow
                ]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            
            VStack(spacing: 40) {
                Spacer()
                
                // App Logo Section
                VStack(spacing: 20) {
                    // Bee Icon
                    Image("Bee_Icon")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 120, height: 120)
                    
                    // App Name
                    Text("Beyhive Alert")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.black)
                        .shadow(color: .white, radius: 2, x: 1, y: 1)
                }
                
                // Update Message Section
                VStack(spacing: 16) {
                    Text("Update Required")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.black)
                    
                    Text(updateMessage ?? "A new version of the app is available. Please update to continue using the app.")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.black.opacity(0.8))
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 40)
                    
                    // Version info
                    VStack(spacing: 4) {
                        Text("Current Version: \(currentVersion)")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.black.opacity(0.6))
                        
                        Text("Latest Version: \(latestVersion)")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.black.opacity(0.6))
                    }
                    .padding(.top, 8)
                }
                
                // Update Button
                Button(action: onUpdateTap) {
                    HStack(spacing: 12) {
                        Image(systemName: "arrow.down.circle.fill")
                            .font(.system(size: 20, weight: .semibold))
                        
                        Text("Update Now")
                            .font(.system(size: 18, weight: .semibold))
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 40)
                    .padding(.vertical, 16)
                    .background(
                        LinearGradient(
                            gradient: Gradient(colors: [
                                Color(red: 0.82, green: 0.18, blue: 0.18), // Red
                                Color(red: 0.13, green: 0.59, blue: 0.95)  // Blue
                            ]),
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .cornerRadius(25)
                    .shadow(color: .black.opacity(0.2), radius: 8, x: 0, y: 4)
                }
                .scaleEffect(1.0)
                .animation(.easeInOut(duration: 0.1), value: true)
                
                Spacer()
            }
        }
        .preferredColorScheme(.light)
    }
}

// MARK: - Update Manager
class UpdateManager: ObservableObject {
    @Published var needsUpdate = false
    @Published var currentVersion = ""
    @Published var latestVersion = ""
    @Published var updateMessage = ""
    
    private let backendURL = "https://beyhive-backend.onrender.com/api/app/version-check"
    
    init() {
        getCurrentVersion()
        checkForUpdates()
    }
    
    private func getCurrentVersion() {
        if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
            currentVersion = version
        } else {
            currentVersion = "1.0.0"
        }
    }
    
    func checkForUpdates() {
        guard let url = URL(string: backendURL) else { return }
        
        URLSession.shared.dataTask(with: url) { [weak self] data, response, error in
            DispatchQueue.main.async {
                guard let data = data,
                      let response = try? JSONDecoder().decode(UpdateResponse.self, from: data) else {
                    print("❌ Failed to check for updates")
                    return
                }
                
                self?.latestVersion = response.latestVersion
                self?.updateMessage = response.updateMessage ?? "A new version of the app is available. Please update to continue using the app."
                
                // Check if update is needed
                if self?.shouldUpdate(current: self?.currentVersion ?? "", latest: response.latestVersion) == true {
                    self?.needsUpdate = true
                }
            }
        }.resume()
    }
    
    private func shouldUpdate(current: String, latest: String) -> Bool {
        return current.compare(latest, options: .numeric) == .orderedAscending
    }
    
    func openAppStore() {
        // Replace with your actual App Store URL
        let appStoreURL = "https://apps.apple.com/app/beyhive-alert/id[YOUR_APP_ID]"
        
        if let url = URL(string: appStoreURL) {
            UIApplication.shared.open(url)
        }
    }
}

// MARK: - Update Response Model
struct UpdateResponse: Codable {
    let latestVersion: String
    let updateMessage: String?
    let forceUpdate: Bool?
    let minimumVersion: String?
}

// MARK: - Preview
#if DEBUG
struct UpdateView_Previews: PreviewProvider {
    static var previews: some View {
        UpdateView(
            currentVersion: "1.0.0",
            latestVersion: "1.1.0",
            updateMessage: "New features and bug fixes! Update now to get the latest Beyoncé tour updates and improved notifications."
        ) {
            print("Update tapped!")
        }
    }
}
#endif
