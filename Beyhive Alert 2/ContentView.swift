//
//  ContentView.swift
//  Beyhive Alert 2
//
//  Created by Chase Davis on 7/1/25.
//

import SwiftUI
import Foundation
import WebKit
import EventKit
import UserNotifications
import UIKit
import StoreKit
// Import EventsViewModel
// If needed, add: import Events
// Import OutfitsViewModel for outfit dropdowns
import Combine

enum BeyhiveTab: Int, CaseIterable {
    case home, videos, game, trackers, schedule
    
    var icon: String {
        switch self {
        case .home: return "house.fill"
        case .videos: return "video.fill"
        case .game: return "star.circle.fill"
        case .trackers: return "music.mic"
        case .schedule: return "calendar"
        }
    }
}

struct TopBarBackground: View {
    @State private var showSettings = false
    var body: some View {
        ZStack(alignment: .center) {
            Color.yellow.opacity(0.3)
                .ignoresSafeArea(edges: .top)
            
            Text("Beyhive Alert")
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.black)
                .frame(maxWidth: .infinity, alignment: .center)
            
            HStack {
                Image("Bee_Icon")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 80, height: 80)
                    .padding(.leading, 16)
                Spacer()
            }
            HStack {
                Spacer()
                Button(action: {
                    showSettings = true
                }) {
                    Image(systemName: "gearshape.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 28, height: 28)
                        .foregroundColor(.black)
                        .padding(.trailing, 20)
                }
            }
        }
        .frame(height: 50)
        .sheet(isPresented: $showSettings) {
            SettingsView()
        }
    }
}

struct ShareSheet: UIViewControllerRepresentable {
    let activityItems: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

struct SettingsView: View {
    @State private var showPrivacyTerms = false
    @State private var showShareSheet = false
    @State private var username: String = UserDefaults.standard.string(forKey: "username") ?? ""
    @State private var email: String = UserDefaults.standard.string(forKey: "email") ?? ""
    @State private var altEmail: String = UserDefaults.standard.string(forKey: "altEmail") ?? ""
    @State private var saveMessage: String? = nil
    @State private var showDeleteAlert = false
    @StateObject private var storeKitManager = StoreKitManager()
    @State private var isRestoring = false
    @State private var restoreMessage: String? = nil
    var body: some View {
        VStack(spacing: 0) {
            // Light yellow top bar
            ZStack {
                Color(red: 1.0, green: 0.98, blue: 0.8)
                    .ignoresSafeArea(edges: .top)
                HStack {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(Color.yellow)
                        .padding(.leading, 16)
                    Spacer()
                    Text("Settings")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(Color.yellow)
                        .padding(.trailing, 32)
                    Spacer()
                }
                .frame(height: 60)
            }
            .frame(height: 60)
            .background(Color(red: 1.0, green: 0.98, blue: 0.8))
            // Contact Info Section
            VStack(alignment: .leading, spacing: 8) {
                Text("Sign Up / Log In")
                    .font(.system(size: 9, weight: .semibold))
                    .foregroundColor(.black)
                Text("Enter your email and an optional alternate email so we can contact you if you win a prize in the Survivor game. This is optional and only required if you want to claim a prize.")
                    .font(.system(size: 9))
                    .foregroundColor(.gray)
                TextField("Custom Username", text: $username)
                    .font(.system(size: 9))
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                TextField("Email", text: $email)
                    .font(.system(size: 9))
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
                TextField("Alternate Email (optional)", text: $altEmail)
                    .font(.system(size: 9))
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
                Button(action: {
                    UserDefaults.standard.setValue(username, forKey: "username")
                    UserDefaults.standard.setValue(email, forKey: "email")
                    UserDefaults.standard.setValue(altEmail, forKey: "altEmail")
                    saveMessage = "Contact info saved!"
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) { saveMessage = nil }
                }) {
                    Text("Save Changes")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(.white)
                        .padding(6)
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .cornerRadius(8)
                }
                if let msg = saveMessage {
                    Text(msg)
                        .font(.caption2)
                        .foregroundColor(.green)
                }
            }
            .padding(6)
            .background(Color(.systemGray6))
            .cornerRadius(10)
            .padding(.top, 12)
            .padding(.horizontal, 8)
            // Settings options
            VStack(spacing: 0) {
                SettingsRow(title: "Share our app", action: { showShareSheet = true })
                SettingsRow(title: "Privacy Policy & Terms of Service", action: { showPrivacyTerms = true })
                SettingsRow(title: "Restore Purchases", action: { restorePurchases() })
                SettingsRow(title: "Delete My Account", isDestructive: true, action: { showDeleteAlert = true })
            }
            .background(Color.white)
            .cornerRadius(16)
            .padding(.top, 24)
            .padding(.horizontal)
            // Support/help
            Text("For additional help:\nbeyhivealert@gmail.com")
                .font(.system(size: 21, weight: .medium))
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
                .padding(.top, 24)
                .padding(.horizontal)
            // Social icons
            HStack(spacing: 32) {
                Button(action: {
                    if let url = URL(string: "https://www.instagram.com/beyhivealert/") {
                        UIApplication.shared.open(url)
                    }
                }) {
                    Image("Instagramlogo")
                        .resizable()
                        .frame(width: 40, height: 40)
                        .foregroundColor(.pink)
                }
                .buttonStyle(PlainButtonStyle())
                
                Button(action: {
                    if let url = URL(string: "https://www.tiktok.com/@beyhive.alert?_t=ZP-8xgEoOrZaed&_r=1") {
                        UIApplication.shared.open(url)
                    }
                }) {
                    Image("Tiktoklogo")
                        .resizable()
                        .frame(width: 54, height: 54)
                        .foregroundColor(.black)
                }
                .buttonStyle(PlainButtonStyle())
            }
            .padding(.top, 16)
            // Disclaimer
            VStack(alignment: .leading, spacing: 8) {
                Text("Beyhive Alert Affiliation Disclaimer")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.black)
                Text("Beyhive Alert is an aggregation of publicly available information and is committed to accuracy, but is not responsible for inaccurate notifications. Beyhive Alert has no affiliation, association, endorsement, or any connection with Beyoncé, or any subsidiaries or affiliates including but not limited to the COWBOY CARTER Tour. Song titles and setlist information are used for informational purposes only. To support Beyoncé, please visit the official Beyoncé website at https://www.beyonce.com/ and the official COWBOY CARTER Tour website.")
                    .font(.system(size: 12))
                    .foregroundColor(.black)
            }
            .padding(.top, 24)
            .padding(.horizontal)
            Spacer()
        }
        .background(Color(.systemGray6).ignoresSafeArea())

        .sheet(isPresented: $showPrivacyTerms) {
            PrivacyTermsView()
        }
        .sheet(isPresented: $showShareSheet) {
            ShareSheet(activityItems: [
                URL(string: "https://apps.apple.com/us/app/beyhive-alert/id6748089455")!
            ])
        }
        .alert("Are you sure you want to delete your account? This cannot be undone.", isPresented: $showDeleteAlert) {
            Button("Delete", role: .destructive) {
                // Delete user data
                if let bundleID = Bundle.main.bundleIdentifier {
                    UserDefaults.standard.removePersistentDomain(forName: bundleID)
                }
                // Optionally, navigate to a login or confirmation screen
            }
            Button("Cancel", role: .cancel) { }
        }
        // Show restore message if present
        if let msg = restoreMessage {
            Text(msg)
                .font(.caption2)
                .foregroundColor(msg.contains("restored") ? .green : .red)
                .padding(.top, 8)
        }
        if isRestoring {
            ProgressView("Restoring...")
                .padding(.top, 8)
        }
    }
    // Add this function inside SettingsView
    func restorePurchases() {
        isRestoring = true
        restoreMessage = nil
        Task {
            do {
                var found = false
                for await result in Transaction.currentEntitlements {
                    if case .verified(let transaction) = result, transaction.productID == storeKitManager.productID {
                        found = true
                        restoreMessage = "Purchases restored!"
                        await storeKitManager.checkPurchased()
                        break
                    }
                }
                if !found {
                    restoreMessage = "No purchases to restore."
                }
            } catch {
                restoreMessage = error.localizedDescription
            }
            isRestoring = false
        }
    }
}

struct SettingsRow: View {
    let title: String
    var isDestructive: Bool = false
    var action: (() -> Void)? = nil
    var body: some View {
        Button(action: { action?() }) {
            HStack {
                Text(title)
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(isDestructive ? .red : .black)
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(.gray)
            }
            .padding()
            .background(Color.white)
            .contentShape(Rectangle())
        }
        Divider()
            .padding(.leading)
    }
}

struct PrivacyTermsView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var selectedTab = 0
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Tab selector
                HStack(spacing: 0) {
                    Button(action: { selectedTab = 0 }) {
                        Text("Privacy Policy")
                            .font(.headline)
                            .foregroundColor(selectedTab == 0 ? .white : .black)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(selectedTab == 0 ? Color.yellow : Color.clear)
                    }
                    
                    Button(action: { selectedTab = 1 }) {
                        Text("Terms of Service")
                            .font(.headline)
                            .foregroundColor(selectedTab == 1 ? .white : .black)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(selectedTab == 1 ? Color.yellow : Color.clear)
                    }
                }
                .background(Color(.systemGray6))
                
                // Content
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        if selectedTab == 0 {
                            PrivacyPolicyContent()
                        } else {
                            TermsOfServiceContent()
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("Legal")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}

struct PrivacyPolicyContent: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Privacy Policy")
                .font(.system(size: 21, weight: .medium))
                .foregroundColor(.black)
            
            Text("Last updated: July 2025")
                .font(.system(size: 21, weight: .medium))
                .foregroundColor(.gray)
            
            Group {
                VStack(alignment: .leading, spacing: 12) {
                    Text("1. Introduction")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("This Privacy Policy describes how Beyhive Alert collects, uses, and protects your personal information. By using our app, you agree to the collection and use of information in accordance with this policy.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("2. Changes to This Policy")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We may update this Privacy Policy from time to time. We will notify you of material changes by posting the new policy in the app and updating the 'Last updated' date.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("3. Contact Us")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("If you have questions about this Privacy Policy or want to exercise your rights, please contact us at:\n\nbeyhivealert@gmail.com")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("4. Information We Collect")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We collect the following types of information (we will NOT sell your information):")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Contact information (email address for account creation and login)\n• Account preferences and settings\n• Notification preferences and settings\n• App usage data and analytics\n• Device information for push notifications\n• IP address and device identifiers")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("5. How We Use Your Information")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We use your information for the following purposes:")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Provide and maintain the Beyhive Alert service\n• Send you notifications about Beyoncé updates\n• Personalize your experience and content\n• Improve our app and services\n• Communicate with you about your account\n• Ensure app security and prevent fraud\n• Comply with legal obligations")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("6. Legal Basis for Processing")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We process your information based on:")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Consent (for optional features and communications)\n• Contract performance (to provide our services)\n• Legitimate interests (to improve our app)\n• Legal obligations (to comply with laws)")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("7. Information Sharing")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Beyhive Alert does NOT sell your information to advertisers or third parties. We may share information only in these circumstances:")
                        .font(.body)
                    
                    Text("• With your explicit consent\n• With service providers who assist in app operations\n• To comply with legal obligations\n• To protect our rights and safety\n• In connection with business transfers")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("8. Data Security")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We implement appropriate security measures to protect your personal information:")
                        .font(.body)
                    
                    Text("• Encryption of data in transit and at rest\n• Secure authentication systems\n• Regular security assessments\n• Limited access to personal data\n• Secure data storage practices")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("9. Your Rights")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("You have the following rights regarding your personal data:")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Access your personal data\n• Update or correct your information\n• Delete your account and data\n• Opt out of notifications and communications\n• Request data portability\n• Object to processing\n• Withdraw consent")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("10. Data Retention")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We retain your personal information only as long as necessary to provide our services and comply with legal obligations. You may request deletion of your data at any time.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("11. Third-Party Services")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Our app may contain links to third-party services (Instagram, social media, payment processors). We are not responsible for the privacy practices of these external services. Please review their privacy policies.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("12. Children's Privacy")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Our app is not intended for children under 13. We do not knowingly collect personal information from children under 13. If we become aware of such collection, we will delete the information promptly.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("13. International Transfers")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Your information may be transferred to and processed in countries other than your own. We ensure appropriate safeguards are in place to protect your data.")
                        .font(.body)
                }
            }
        }
    }
}

struct TermsOfServiceContent: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Terms of Service")
                .font(.system(size: 21, weight: .medium))
                .foregroundColor(.black)
            
            Text("Last updated: January 2025")
                .font(.system(size: 21, weight: .medium))
                .foregroundColor(.gray)
            
            Group {
                VStack(alignment: .leading, spacing: 12) {
                    Text("1. Acceptance of Terms")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("By downloading, installing, or using Beyhive Alert, you agree to be bound by these Terms of Service and our Privacy Policy. If you do not agree to these terms, please do not use our app. These terms create a legal agreement between you and Beyhive Alert.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("2. NO AFFILIATIONS")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Beyhive Alert has no affiliation, association, endorsement, or any connection whatsoever with Beyoncé or any of her affiliates, including but not limited to the COWBOY CARTER Tour. To support Beyoncé, please visit the official Beyoncé website at https://www.beyonce.com/ and the official COWBOY CARTER Tour website.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("3. Accuracy Disclaimer")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Beyhive Alert is an aggregation of publicly available information and is committed to accuracy. However, Beyhive Alert is not responsible for inaccurate notifications or information of any kind. This includes information from other sources and information created or presented by us.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("4. Description of Service")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Beyhive Alert is an unofficial fan app that provides:")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Beyoncé news and updates\n• Tour information and setlists\n• Notification services\n• Community features for Beyoncé fans\n• Games and interactive features")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("5. User Accounts")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("To use certain features, you must create an account. You are responsible for:")
                        .font(.body)
                    
                    Text("• Maintaining the confidentiality of your account\n• All activities under your account\n• Providing accurate information\n• Notifying us of any security breaches\n• Keeping your contact information current")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("6. Acceptable Use")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("You agree not to:")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text("• Use the app for illegal purposes\n• Violate any applicable laws or regulations\n• Infringe on intellectual property rights\n• Harass, abuse, or harm others\n• Attempt to gain unauthorized access to our systems\n• Transmit harmful code or malware\n• Interfere with app functionality")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("7. Intellectual Property")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("The app and its content are protected by copyright and other intellectual property laws. You may not reproduce, distribute, or create derivative works without permission. All content is provided for personal, non-commercial use only.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("8. Third-Party Content")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("Our app may contain links to third-party websites, social media, or content. We are not responsible for third-party content, accuracy, or practices. Your use of third-party content is at your own risk.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("9. Limitation of Liability")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("TO THE MAXIMUM EXTENT PERMITTED BY LAW, BEYHIVE ALERT SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES, INCLUDING BUT NOT LIMITED TO LOSS OF PROFITS, DATA, USE, GOODWILL, OR OTHER INTANGIBLE LOSSES.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("10. Termination")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("We may terminate or suspend your account at any time for violations of these terms. You may also delete your account at any time. Upon termination, your right to use the app ceases immediately.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("11. Governing Law and Disputes")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("These terms are governed by the laws of the United States. Any disputes will be resolved through binding arbitration, except for claims that may be brought in small claims court.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("12. Class Action Waiver")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("YOU AGREE THAT YOU WILL NOT COMMENCE, MAINTAIN, OR PARTICIPATE IN ANY CLASS ACTION, CLASS ARBITRATION, OR OTHER REPRESENTATIVE ACTION OR PROCEEDING AGAINST BEYHIVE ALERT.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("13. Electronic Communications")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("You consent to receive electronic communications from us. All agreements, notices, and disclosures provided electronically satisfy any legal requirement that such communication be in writing.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("14. Entire Agreement")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("These Terms of Service and our Privacy Policy constitute the complete agreement between you and Beyhive Alert, superseding all prior agreements and communications.")
                        .font(.body)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("15. Contact Information")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Text("For questions about these Terms of Service, contact us at:\n\nbeyhivealert@gmail.com")
                        .font(.body)
                }
            }
        }
    }
}



struct ContentView: View {
    @StateObject private var tilesViewModel = TilesViewModel()
    @StateObject private var eventsViewModel = EventsViewModel()
    @State private var selectedTab: BeyhiveTab = .home
    @State private var isMaintenanceMode = false
    @State private var isLoadingMaintenance = true
    
    var body: some View {
        Group {
            if isLoadingMaintenance {
                ProgressView("Loading...")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.white)
            } else if isMaintenanceMode {
                MaintenanceView()
            } else {
                VStack(spacing: 0) {
                    TopBarBackground()
                    ZStack {
                        Color.white
                        Group {
                            switch selectedTab {
                            case .home: HomeView(selectedTab: $selectedTab)
                            case .videos: LivestreamsView(selectedTab: $selectedTab)
                            case .game: NotificationsView() // Free notifications view
                            case .trackers: TrackersView()
                            case .schedule: ScheduleView()
                            }
                        }
                    }
                    .edgesIgnoringSafeArea(.all)
                    Spacer(minLength: 0)
                    CustomTabBar(selectedTab: $selectedTab, showShadow: true, height: 52) // Bottom bar
                }
                .onAppear {
                    requestNotificationPermissions()
                }
                .font(.system(size: 11)) // Set default font size for all text in ContentView and children (even smaller)
                .preferredColorScheme(.light)
            }
        }
        .task {
            await checkMaintenanceMode()
        }
    }
    
    private func checkMaintenanceMode() async {
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/admin/maintenance-mode") else {
            isLoadingMaintenance = false
            return
        }
        
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(MaintenanceResponse.self, from: data)
            isMaintenanceMode = response.isMaintenanceMode
        } catch {
            print("Error checking maintenance mode: \(error)")
        }
        
        isLoadingMaintenance = false
    }
}

struct MaintenanceResponse: Codable {
    let isMaintenanceMode: Bool
}

struct MaintenanceView: View {
    var body: some View {
        VStack(spacing: 32) {
            Spacer()
            
            Image("Bee_Icon")
                .resizable()
                .scaledToFit()
                .frame(width: 120, height: 120)
                .foregroundColor(.gray)
            
            VStack(spacing: 16) {
                Text("🛠️ Under Maintenance")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(.black)
                
                Text("We're currently updating the app to bring you the best Beyhive experience!")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                
                Text("Please check back later.")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.gray)
            }
            
            Spacer()
            
            Text("🐝 Beyhive Alert")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.gray)
                .padding(.bottom, 32)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.white)
    }
}

struct CustomTabBar: View {
    @Binding var selectedTab: BeyhiveTab
    var showShadow: Bool = true
    var height: CGFloat = 40
    
    // Custom order: Home, Videos, Game (center), Trackers, Schedule
    let tabOrder: [BeyhiveTab] = [.home, .videos, .game, .trackers, .schedule]
    
    var body: some View {
        HStack(spacing: 0) {
            ForEach(tabOrder, id: \ .self) { tab in
                Button(action: {
                    selectedTab = tab
                }) {
                    if tab == .game {
                        ZStack {
                            Circle()
                                .fill(Color.yellow)
                                .frame(width: 60, height: 60)
                            Image("Bee_Icon")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 50, height: 50)
                        }
                        .offset(y: -12)
                    } else if tab == .home {
                        Image(systemName: tab.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 34, height: 34)
                            .foregroundColor(selectedTab == tab ? .pink : .white)
                    } else if tab == .videos {
                        Image(systemName: tab.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 34, height: 34)
                            .foregroundColor(selectedTab == tab ? .blue : .white)
                    } else if tab == .trackers {
                        Image(systemName: tab.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 34, height: 34)
                            .foregroundColor(selectedTab == tab ? .yellow : .white)
                    } else if tab == .schedule {
                        Image(systemName: tab.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 34, height: 34)
                            .foregroundColor(selectedTab == tab ? .yellow : .white)
                    } else {
                        Image(systemName: tab.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 34, height: 34)
                            .foregroundColor(selectedTab == tab ? .yellow : .white)
                    }
                }
                .frame(maxWidth: .infinity)
            }
        }
        .frame(height: height + 20)
        .padding(.vertical, 0)
        .background(
            Color.yellow.opacity(0.3)
                .ignoresSafeArea(edges: .bottom)
        )
        .shadow(radius: showShadow ? 8 : 0)
    }
}

// MARK: - Placeholder Views

struct Article: Identifiable {
    let id = UUID()
    let title: String
    let source: String
    let date: String
    let url: String
}

extension String {
    func daysAgo(from formatter: DateFormatter) -> String? {
        guard let date = formatter.date(from: self) else { return nil }
        let days = Calendar.current.dateComponents([.day], from: date, to: Date()).day ?? 0
        if days == 0 { return "Today" }
        if days == 1 { return "1 day ago" }
        return "\(days) days ago"
    }
}

struct BeyonceNonAffiliationTopBar: View {
    var onClose: () -> Void
    var body: some View {
        ZStack {
            Color(red: 1.0, green: 0.98, blue: 0.8)
                .ignoresSafeArea(edges: .top)
            HStack {
                Button(action: onClose) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(Color.yellow)
                        .padding(.leading, 16)
                }
                Spacer()
                Text("Beyoncé Non-Affiliation")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(Color.yellow)
                    .padding(.trailing, 32)
                Spacer()
            }
            .frame(height: 60)
        }
        .frame(height: 60)
        .background(Color(red: 1.0, green: 0.98, blue: 0.8))
    }
}

struct BeyonceNonAffiliationBanner: View {
    @Binding var showSheet: Bool
    var body: some View {
        Button(action: { showSheet = true }) {
            Text("Beyoncé Non-Affiliation")
                .font(.system(size: 16, weight: .medium))
                .foregroundColor(.black)
                .underline()
                .padding(.top, 16)
                .padding(.bottom, 8)
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(PlainButtonStyle())
        .sheet(isPresented: $showSheet) {
            BeyonceNonAffiliationSheet(onClose: { showSheet = false })
        }
    }
}

struct BeyonceNonAffiliationSheet: View {
    var onClose: () -> Void
    var body: some View {
        VStack(spacing: 0) {
            BeyonceNonAffiliationTopBar(onClose: onClose)
            VStack(alignment: .leading, spacing: 0) {
                Text("Beyhive Alert is not affiliated with Beyoncé or the COWBOY CARTER Tour.")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.gray)
                    .padding(.top, 18)
                    .padding(.horizontal)
                Text("Beyhive Alert gathers and shares publicly available information for fans and strives for accuracy, but cannot guarantee the correctness of every notification. This app is not affiliated, endorsed, or associated in any way with Beyoncé, her team, or any related entities. All song titles and setlist details are provided for informational purposes only. For official updates and to support Beyoncé, please visit the official website at https://www.beyonce.com/ and the COWBOY CARTER Tour page.")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(Color(.darkGray))
                    .lineSpacing(2)
                    .padding(.top, 10)
                    .padding(.horizontal)
                    .padding(.bottom, 18)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: 600, alignment: .leading)
            Spacer()
        }
        .background(Color.white.ignoresSafeArea())
    }
}

struct HomeView: View {
    @Binding var selectedTab: BeyhiveTab
    let pubDateFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: "en_US_POSIX")
        f.dateFormat = "E, d MMM yyyy HH:mm:ss Z"
        return f
    }()
    @State private var showSurvivor = false
    @State private var showAlbumRanker = false
    @State private var showNonAffiliationSheet = false
    @State private var showDailyTrivia = false
    @State private var partners: [Partner] = []
    @State private var isLoadingPartners = true
    @State private var partnersError: String? = nil

    func fetchPartners() async {
        isLoadingPartners = true
        partnersError = nil
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/partners") else {
            partnersError = "Invalid partners URL."
            isLoadingPartners = false
            return
        }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let decoded = try JSONDecoder().decode(PartnersResponse.self, from: data)
            if decoded.success {
                partners = decoded.partners
            } else {
                partnersError = "No partners found."
            }
        } catch {
            partnersError = "Failed to load partners."
        }
        isLoadingPartners = false
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 32) {
                BeyonceNonAffiliationBanner(showSheet: $showNonAffiliationSheet)
                // Welcome Section
                VStack(spacing: 0) {
                    Image("Bee_Icon")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 120, height: 120)
                    Text("Welcome to Beyhive Alert!")
                        .font(.system(size: 18, weight: .medium))
                    Spacer().frame(height: 8)
                    Text("Stay connected to Beyoncé's tour and the Beyhive community.")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(.black.opacity(0.9))
                }
                // Card Buttons Section
                VStack(alignment: .leading, spacing: 16) {
                    HStack(spacing: 8) {
                        Image("Bee_Icon")
                            .resizable()
                            .frame(width: 70, height: 70)
                        Text("Games")
                            .font(.system(size: 32, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Spacer()
                    }
                    .padding(.horizontal)
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 20) {
                            Button(action: { showSurvivor = true }) {
                                VStack(alignment: .center, spacing: 12) {
                                    Image(systemName: "gamecontroller.fill")
                                        .resizable()
                                        .scaledToFit()
                                        .frame(width: 44, height: 44)
                                        .foregroundColor(.black)
                                    Text("Survivor Game")
                                        .font(.system(size: 20, weight: .bold, design: .rounded))
                                        .foregroundColor(.black)
                                    Text("Guess outfits, songs, and more during every show!")
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.black)
                                        .multilineTextAlignment(.center)
                                        .frame(maxWidth: 240)
                                }
                                .frame(width: 340, height: 140)
                                .background(
                                    LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                                )
                                .cornerRadius(28)
                            }
                            Button(action: { showDailyTrivia = true }) {
                                VStack(alignment: .center, spacing: 12) {
                                    Image(systemName: "questionmark.circle.fill")
                                        .resizable()
                                        .scaledToFit()
                                        .frame(width: 44, height: 44)
                                        .foregroundColor(.black)
                                    Text("Daily Trivia")
                                        .font(.system(size: 20, weight: .bold, design: .rounded))
                                        .foregroundColor(.black)
                                    Text("Test your Beyoncé knowledge with a new question every day!")
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.black)
                                        .multilineTextAlignment(.center)
                                        .frame(maxWidth: 240)
                                }
                                .frame(width: 340, height: 140)
                                .background(
                                    LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                                )
                                .cornerRadius(28)
                            }
                            Button(action: { showAlbumRanker = true }) {
                                VStack(alignment: .center, spacing: 12) {
                                    Image(systemName: "music.note.list")
                                        .resizable()
                                        .scaledToFit()
                                        .frame(width: 44, height: 44)
                                        .foregroundColor(.black)
                                    Text("Album Ranker")
                                        .font(.system(size: 20, weight: .bold, design: .rounded))
                                        .foregroundColor(.black)
                                    Text("Rank Beyoncé's albums and see community favorites!")
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.black)
                                        .multilineTextAlignment(.center)
                                        .frame(maxWidth: 240)
                                }
                                .frame(width: 340, height: 140)
                                .background(
                                    LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                                )
                                .cornerRadius(28)
                            }
                        }
                        .padding(.horizontal)
                    }
                }
                // Partners Section (dynamic)
                VStack(alignment: .leading, spacing: 16) {
                    HStack(spacing: 8) {
                        Image("Bee_Icon")
                            .resizable()
                            .frame(width: 70, height: 70)
                        Text("Partners")
                            .font(.system(size: 32, weight: .bold, design: .rounded))
                            .foregroundColor(.black)
                        Spacer()
                    }
                    .padding(.horizontal)
                    if isLoadingPartners {
                        ProgressView("Loading partners...")
                            .padding()
                    } else if let error = partnersError {
                        Text(error)
                            .foregroundColor(.red)
                            .padding()
                    } else {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 20) {
                                ForEach(partners) { partner in
                                    Button(action: {
                                        if let url = URL(string: partner.link) {
                                            UIApplication.shared.open(url)
                                        }
                                    }) {
                                        VStack(alignment: .center, spacing: 12) {
                                            if let iconUrl = partner.iconUrl, !iconUrl.isEmpty, let url = URL(string: iconUrl) {
                                                AsyncImage(url: url) { image in
                                                    image.resizable().aspectRatio(contentMode: .fit)
                                                } placeholder: {
                                                    Rectangle().fill(Color.gray.opacity(0.3)).overlay(ProgressView().scaleEffect(0.8))
                                                }
                                                .frame(width: 44, height: 44)
                                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                            } else {
                                                Image(systemName: "building.2")
                                                    .resizable()
                                                    .scaledToFit()
                                                    .frame(width: 44, height: 44)
                                                    .foregroundColor(.black)
                                            }
                                            Text(partner.name)
                                                .font(.system(size: 20, weight: .bold, design: .rounded))
                                                .foregroundColor(.black)
                                            Text(partner.description)
                                                .font(.system(size: 13, weight: .medium))
                                                .foregroundColor(.black)
                                                .multilineTextAlignment(.center)
                                                .frame(maxWidth: 240)
                                        }
                                        .frame(width: 340, height: 140)
                                        .background(
                                            LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                                        )
                                        .cornerRadius(28)
                                    }
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
                .task {
                    await fetchPartners()
                }
                // Instagram feed section
                InstagramFeedView()
                // Divider
                HStack {
                    Spacer()
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 100, height: 1)
                    Spacer()
                }
                .padding(.horizontal, 40)
                // Recent Articles Section removed
            }
        }
        .background(Color.white.ignoresSafeArea())
        .sheet(isPresented: $showSurvivor) {
            SurvivorView()
        }
        .sheet(isPresented: $showAlbumRanker) {
            AlbumRankingGameView()
        }
        .sheet(isPresented: $showDailyTrivia) {
            DailyTriviaView()
        }
    }
}

struct SurvivorRound: Identifiable {
    let id = UUID()
    let name: String
    var completed: Bool
}

// SurvivorQuizListView definition (place above SurvivorView and SurvivorQuizView)
struct SurvivorQuizListView: View {
    let quizzes: [(title: String, id: String)]
    let onQuizTap: (( (title: String, id: String) ) -> Void)
    var body: some View {
        VStack(spacing: 24) {
            ForEach(quizzes, id: \ .id) { quiz in
                Button(action: { onQuizTap(quiz) }) {
                    HStack {
                        VStack(alignment: .leading, spacing: 6) {
                            Text(quiz.title)
                                .font(.system(size: 22, weight: .bold, design: .rounded))
                                .foregroundColor(.black)
                            Text("Quiz")
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(.black.opacity(0.8))
                        }
                        Spacer()
                        Image(systemName: "chevron.right")
                            .foregroundColor(.black.opacity(0.8))
                            .font(.title2)
                    }
                    .padding()
                    .background(
                        LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    )
                    .cornerRadius(22)
                    .shadow(color: Color.blue.opacity(0.12), radius: 10, x: 0, y: 6)
                }
                .buttonStyle(PlainButtonStyle())
                .padding(.horizontal, 24)
            }
        }
        .padding(.top, 32)
    }
}

// Restore SurvivorQuizView (latest working version, with all quiz logic, async loading, and navigation)
struct SurvivorQuizView: View {
    let quizTitle: String
    @Environment(\.dismiss) private var dismiss
    @State private var answers: [Int: String] = [:]
    @State private var showDropdown: [Int: Bool] = [:]
    @State private var showIncompleteAlert = false
    @State private var isLoading = true
    @State private var hasSubmitted = false
    @State private var submittedAnswers: [Int: String] = [:]
    @State private var showConfirmation = false
    @State private var errorMessage: String? = nil
    @State private var quizOpen: Bool = true
    @State private var quizOpenMessage: String? = nil
    @State private var quizOpenAt: Date? = nil
    @State private var quizCloseAt: Date? = nil
    @StateObject private var outfitsViewModel = OutfitsViewModel()
    @State private var questions: [QuizQuestion] = []

    var userEmail: String {
        UserDefaults.standard.string(forKey: "email") ?? ""
    }
    var userIdForQuiz: String {
        let email = UserDefaults.standard.string(forKey: "email") ?? ""
        if !email.isEmpty {
            return email
        } else {
            return UIDevice.current.identifierForVendor?.uuidString ?? "anonymous"
        }
    }
    var quizId: String {
        switch quizTitle {
        case "Las Vegas Night 1": return "vegas_n1"
        case "Las Vegas Night 2": return "vegas_n2"
        default: return quizTitle.replacingOccurrences(of: " ", with: "_").lowercased()
        }
    }

    struct QuizQuestion: Identifiable, Codable {
        var id: Int { index }
        let index: Int
        let text: String
        let points: Int
        let options: [String]
        let icon: String?
    }

    func isOutfitQuestion(_ q: QuizQuestion) -> Bool {
        let outfitKeywords = ["outfit", "wear", "bodysuit", "jumpsuit"]
        let text = q.text.lowercased()
        return outfitKeywords.contains { text.contains($0) }
    }

    func loadQuizAndOutfits() async {
        isLoading = true
        await outfitsViewModel.fetchOutfits()
        await fetchQuizMetadataAndQuestions()
        isLoading = false
    }

    func fetchQuizMetadataAndQuestions() async {
        isLoading = true
        quizOpen = true
        quizOpenMessage = nil
        quizOpenAt = nil
        quizCloseAt = nil
        questions = []
        guard let url = URL(string: "https://beyhive-backend.onrender.com/api/survivor-quiz/\(quizId)") else {
            isLoading = false
            errorMessage = "Invalid quiz URL."
            return
        }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            if let obj = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
               let quiz = obj["quiz"] as? [String: Any] {
                let openAtStr = quiz["openAt"] as? String
                let closeAtStr = quiz["closeAt"] as? String
                let dateFormatter = ISO8601DateFormatter()
                let now = Date()
                if let openAtStr = openAtStr, let openAt = dateFormatter.date(from: openAtStr) {
                    quizOpenAt = openAt
                    if now < openAt {
                        quizOpen = false
                        quizOpenMessage = "This quiz is not open yet."
                    }
                }
                if let closeAtStr = closeAtStr, let closeAt = dateFormatter.date(from: closeAtStr) {
                    quizCloseAt = closeAt
                    if now > closeAt {
                        quizOpen = false
                        quizOpenMessage = "This quiz has closed."
                    }
                }
                if let qArr = quiz["questions"] as? [[String: Any]] {
                    var parsed: [QuizQuestion] = []
                    for (idx, q) in qArr.enumerated() {
                        let text = q["text"] as? String ?? ""
                        let points = q["points"] as? Int ?? 0
                        let options = q["options"] as? [String] ?? []
                        parsed.append(QuizQuestion(index: idx, text: text, points: points, options: options, icon: nil))
                    }
                    questions = parsed
                }
            }
        } catch {
            errorMessage = "Failed to load quiz metadata/questions."
            isLoading = false
            return
        }
        isLoading = false
    }

    var body: some View {
        NavigationView {
            ZStack {
                LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    .ignoresSafeArea()
                if isLoading {
                    ProgressView("Loading...")
                } else if let msg = quizOpenMessage {
                    VStack(spacing: 24) {
                        Text(msg)
                            .font(.title2)
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                        if let openAt = quizOpenAt {
                            Text("Opens: \(openAt.formatted(date: .abbreviated, time: .shortened))")
                                .foregroundColor(.gray)
                        }
                        if let closeAt = quizCloseAt {
                            Text("Closes: \(closeAt.formatted(date: .abbreviated, time: .shortened))")
                                .foregroundColor(.gray)
                        }
                    }
                } else if hasSubmitted {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 28) {
                            Text(quizTitle)
                                .font(.system(size: 28, weight: .bold, design: .rounded))
                                .foregroundColor(.black)
                                .padding(.top, 24)
                                .padding(.horizontal)
                            Text("Your Answers")
                                .font(.title2)
                                .foregroundColor(.gray)
                                .padding(.horizontal)
                            ForEach(questions) { q in
                                VStack(alignment: .leading, spacing: 10) {
                                    Text(q.text)
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.black)
                                    Text(submittedAnswers[q.index] ?? "No answer")
                                        .foregroundColor(.gray)
                                        .padding(.bottom, 8)
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 18)
                                        .fill(Color.white)
                                        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
                                )
                                .padding(.vertical, 6)
                            }
                            Text("Thank you for playing! Your answers have been submitted. Good luck!")
                                .foregroundColor(.green)
                                .font(.caption)
                                .padding(.horizontal)
                        }
                    }
                } else {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 28) {
                            Text(quizTitle)
                                .font(.system(size: 28, weight: .bold, design: .rounded))
                                .foregroundColor(.black)
                                .padding(.top, 24)
                                .padding(.horizontal)
                            Text("Questions")
                                .font(.title2)
                                .foregroundColor(.gray)
                                .padding(.horizontal)
                            ForEach(questions) { q in
                                VStack(alignment: .leading, spacing: 10) {
                                    if let icon = q.icon {
                                        Image(systemName: icon)
                                            .foregroundColor(.gray)
                                            .font(.title2)
                                    }
                                    Text("Question \(q.index+1)")
                                        .font(.headline)
                                        .foregroundColor(.gray)
                                    Spacer()
                                    Text("\(q.points) pts")
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 4)
                                        .background(Color.red.opacity(0.08))
                                        .cornerRadius(10)
                                    Text("Required")
                                        .font(.caption2)
                                        .foregroundColor(.red)
                                    Text(q.text)
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.black)
                                    if isOutfitQuestion(q) {
                                        if outfitsViewModel.isLoading {
                                            ProgressView("Loading outfits...")
                                        } else if !outfitsViewModel.outfits.isEmpty {
                                            CustomDropdown(
                                                options: outfitsViewModel.outfits.map { (title: $0.name, description: nil, imageName: $0.imageName, imageUrl: $0.imageUrl) } + [(title: "New", description: nil, imageName: nil, imageUrl: nil)],
                                                selection: answers[q.index] ?? "",
                                                onSelect: { answers[q.index] = $0 },
                                                showDropdown: showDropdown[q.index] ?? false,
                                                setShowDropdown: { showDropdown[q.index] = $0 }
                                            )
                                        } else {
                                            Text("No outfits available.")
                                                .foregroundColor(.gray)
                                        }
                                    } else {
                                        CustomDropdown(
                                            options: q.options.map { (title: $0, description: nil, imageName: nil, imageUrl: nil) },
                                            selection: answers[q.index] ?? "",
                                            onSelect: { answers[q.index] = $0 },
                                            showDropdown: showDropdown[q.index] ?? false,
                                            setShowDropdown: { showDropdown[q.index] = $0 }
                                        )
                                    }
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 18)
                                        .fill(Color.white)
                                        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
                                )
                                .padding(.vertical, 6)
                            }
                            Button(action: {
                                let allAnswered = questions.indices.allSatisfy { answers[$0]?.isEmpty == false }
                                if allAnswered {
                                    submitQuizAnswers()
                                } else {
                                    showIncompleteAlert = true
                                }
                            }) {
                                Text("Submit")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.blue)
                                    .cornerRadius(16)
                                    .padding(.horizontal)
                            }
                            .alert(isPresented: $showIncompleteAlert) {
                                Alert(title: Text("Incomplete"), message: Text(errorMessage ?? "Please answer all questions before submitting."), dismissButton: .default(Text("OK")))
                            }
                        }
                    }
                }
            }
            .task {
                await loadQuizAndOutfits()
            }
            .onChange(of: userEmail) { newEmail in
                if !isLoading {
                    Task {
                        await loadQuizAndOutfits()
                    }
                }
            }
        }
    }

    func submitQuizAnswers() {
        let urlString = "https://beyhive-backend.onrender.com/api/survivor-quiz/response"
        guard let url = URL(string: urlString) else {
            errorMessage = "Invalid URL."
            showIncompleteAlert = true
            return
        }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        let answersString = answers.mapKeys { String($0) }
        let body: [String: Any] = [
            "quizId": quizId,
            "userId": userIdForQuiz,
            "answers": answersString
        ]
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    errorMessage = "Quiz submission failed: \(error.localizedDescription)"
                    showIncompleteAlert = true
                    return
                }
                if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode != 200 {
                    let body = String(data: data ?? Data(), encoding: .utf8) ?? "<no body>"
                    errorMessage = "Submission failed (\(httpResponse.statusCode)): \(body)"
                    showIncompleteAlert = true
                    return
                }
                if let data = data,
                   let result = try? JSONDecoder().decode(ServerResponse.self, from: data),
                   result.success {
                    submittedAnswers = answers
                    hasSubmitted = true
                    showConfirmation = true
                } else {
                    let msg = (try? JSONDecoder().decode(ServerResponse.self, from: data ?? Data()))?.message ?? "Submission failed"
                    errorMessage = msg
                    showIncompleteAlert = true
                }
            }
        }.resume()
    }

    // Add fetchQuizResponse if missing
    func fetchQuizResponse() {
        isLoading = true
        let urlString = "https://beyhive-backend.onrender.com/api/survivor-quiz/response/\(quizId)?userId=\(userIdForQuiz)"
        guard let url = URL(string: urlString) else {
            isLoading = false
            errorMessage = "Invalid URL."
            return
        }
        URLSession.shared.dataTask(with: url) { data, response, error in
            DispatchQueue.main.async {
                isLoading = false
                if let error = error {
                    errorMessage = "Failed to load previous answers: \(error.localizedDescription)"
                    hasSubmitted = false
                    return
                }
                if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode != 200 {
                    let body = String(data: data ?? Data(), encoding: .utf8) ?? "<no body>"
                    errorMessage = "Failed to load previous answers (\(httpResponse.statusCode)): \(body)"
                    hasSubmitted = false
                    return
                }
                if let data = data,
                   let result = try? JSONDecoder().decode(FetchResponse.self, from: data),
                   let resp = result.response {
                    // Convert [String: String] to [Int: String]
                    var intAnswers: [Int: String] = [:]
                    for (k, v) in resp.answers {
                        if let idx = Int(k) { intAnswers[idx] = v }
                    }
                    submittedAnswers = intAnswers
                    hasSubmitted = true
                } else {
                    hasSubmitted = false
                }
            }
        }.resume()
    }
}

struct SurvivorView: View {
    @State private var showQuiz = false
    @State private var selectedQuiz: String? = nil
    let quizzes = [
        (title: "Las Vegas Night 1", id: "vegas_n1"),
        (title: "Las Vegas Night 2", id: "vegas_n2")
    ]
    var body: some View {
        VStack(spacing: 0) {
            // Top bar
            ZStack {
                LinearGradient(gradient: Gradient(colors: [Color.red, Color.white, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                    .ignoresSafeArea(edges: .top)
                HStack {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(Color.white)
                        .padding(.leading, 16)
                    Spacer()
                    Text("Survivor Game")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(Color.white)
                        .padding(.trailing, 32)
                    Spacer()
                }
                .frame(height: 60)
            }
            .frame(height: 60)
            // Title
            Text("SURVIVOR GAME")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.black)
                .padding(.top, 16)
            // Quiz List
            SurvivorQuizListView(quizzes: quizzes, onQuizTap: { quiz in
                selectedQuiz = quiz.title
                showQuiz = true
            })
            Spacer()
            // Add user note here
            VStack {
                Text("If one of the quizzes doesn't load for you on the first try, go out of it, click on a different quiz, and then go back into the quiz you originally wanted. It should work!")
                    .font(.footnote)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)
                    .padding(.bottom, 18)
            }
        }
        .sheet(isPresented: $showQuiz) {
            if let quizTitle = selectedQuiz {
                SurvivorQuizView(quizTitle: quizTitle)
            }
        }
    }
}

struct SurvivorQuizPlaceholderView: View {
    let quizTitle: String
    var body: some View {
        VStack(spacing: 32) {
            Text(quizTitle)
                .font(.system(size: 28, weight: .bold, design: .rounded))
                .foregroundColor(.blue)
                .padding(.top, 40)
            Spacer()
            Text("Quiz Coming Soon!")
                .font(.title2)
                .foregroundColor(.gray)
            Spacer()
        }
        .background(Color.white.ignoresSafeArea())
    }
}

// Helper for corner radius on specific corners
fileprivate extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape( RoundedCorner(radius: radius, corners: corners) )
    }
}

fileprivate struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

struct CustomCalendarView: View {
    @Binding var selectedDate: Date
    let events: [Event]
    let showUpcoming: Bool
    
    @State private var currentMonth: Date = Date()
    private let calendar = Calendar.current
    private let daysOfWeek = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
    
    var body: some View {
        VStack(spacing: 8) {
            // Month navigation
            HStack {
                Button(action: { currentMonth = calendar.date(byAdding: .month, value: -1, to: currentMonth) ?? currentMonth }) {
                    Image(systemName: "chevron.left")
                        .foregroundColor(.black)
                        .padding(8)
                }
                Spacer()
                Text(monthYearString(for: currentMonth))
                    .font(.title2)
                    .fontWeight(.bold)
                Spacer()
                Button(action: { currentMonth = calendar.date(byAdding: .month, value: 1, to: currentMonth) ?? currentMonth }) {
                    Image(systemName: "chevron.right")
                        .foregroundColor(.black)
                        .padding(8)
                }
            }
            .padding(.horizontal)
            // Days of week
            HStack {
                ForEach(daysOfWeek, id: \ .self) { day in
                    Text(day)
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                }
            }
            // Date grid
            let days = makeDays(for: currentMonth)
            let columns = Array(repeating: GridItem(.flexible()), count: 7)
            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(days, id: \ .self) { date in
                    ZStack {
                        if let event = eventForDate(date) {
                            let now = Date()
                            if showUpcoming && (event.localStartDate ?? event.date) >= now {
                                // Upcoming event: yellow highlight
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(Color.yellow.opacity(0.3))
                                    .frame(width: 36, height: 36)
                            } else if !showUpcoming && (event.localStartDate ?? event.date) < now {
                                // Past event: gray highlight
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(Color.gray.opacity(0.4))
                                    .frame(width: 36, height: 36)
                            }
                        }
                        if calendar.isDateInToday(date) {
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(Color.red, lineWidth: 2)
                                .frame(width: 36, height: 36)
                        }
                        Text(dateText(for: date))
                            .font(.body)
                            .fontWeight(calendar.isDate(date, inSameDayAs: selectedDate) ? .bold : .regular)
                            .foregroundColor(isInCurrentMonth(date) ? .black : .gray.opacity(0.4))
                    }
                    .frame(width: 36, height: 36)
                    .onTapGesture {
                        if isInCurrentMonth(date) {
                            selectedDate = date
                        }
                    }
                }
            }
            .padding(.horizontal, 8)
        }
        .padding(.vertical, 8)
        .background(Color.white)
    }
    
    private func monthYearString(for date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "LLLL yyyy"
        return formatter.string(from: date)
    }
    private func makeDays(for month: Date) -> [Date] {
        guard let monthInterval = calendar.dateInterval(of: .month, for: month),
              let firstWeek = calendar.dateInterval(of: .weekOfMonth, for: monthInterval.start) else { return [] }
        var days: [Date] = []
        var current = firstWeek.start
        while current < monthInterval.end || calendar.isDate(current, inSameDayAs: monthInterval.end) {
            days.append(current)
            current = calendar.date(byAdding: .day, value: 1, to: current)!
        }
        // Fill up to complete weeks (so grid is always 6 rows)
        while days.count % 7 != 0 { days.append(calendar.date(byAdding: .day, value: 1, to: days.last!)!) }
        return days
    }
    private func dateText(for date: Date) -> String {
        let day = calendar.component(.day, from: date)
        return "\(day)"
    }
    private func isInCurrentMonth(_ date: Date) -> Bool {
        calendar.isDate(date, equalTo: currentMonth, toGranularity: .month)
    }
    private func eventForDate(_ date: Date) -> Event? {
        let event = events.first { calendar.isDate($0.date, inSameDayAs: date) }
        // print("Found event for date \(date): \(event?.title ?? "")") // Commented out to prevent excessive logging
        return event
    }
}

struct ScheduleView: View {
    @State private var selectedDate = Date()
    @State private var showUpcoming = true
    @State private var showingCalendarAlert = false
    @State private var lastAddedEvent: Event?
    @State private var addedEventIDs: Set<String> = []
    @EnvironmentObject var eventsViewModel: EventsViewModel
    
    var filteredEvents: [Event] {
        let now = Date()
        if showUpcoming {
            return eventsViewModel.events.filter {
                ($0.localStartDate ?? $0.date) >= now
            }
        } else {
            return eventsViewModel.events.filter {
                ($0.localStartDate ?? $0.date) < now
            }.reversed()
        }
    }
    
    private func eventDateTimeString(for event: Event) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        formatter.timeZone = .current // User's local time zone
        if let localStart = event.localStartDate {
            return formatter.string(from: localStart)
        } else {
            return formatter.string(from: event.date)
        }
    }
    
    var body: some View {
        VStack(spacing: 24) {
            CustomCalendarView(selectedDate: $selectedDate, events: eventsViewModel.events, showUpcoming: showUpcoming)
            
            // Toggle for Upcoming/Past Events
            HStack(spacing: 16) {
                Button(action: { showUpcoming = true }) {
                    Text("Upcoming Events")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(showUpcoming ? .black : .gray)
                        .padding(.vertical, 8)
                        .padding(.horizontal, 18)
                        .background(Color.yellow.opacity(0.3))
                        .cornerRadius(12)
                }
                
                // Grey line separator
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 1, height: 30)
                
                Button(action: { showUpcoming = false }) {
                    Text("Past Events")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(!showUpcoming ? .black : .gray)
                        .padding(.vertical, 8)
                        .padding(.horizontal, 18)
                        .background(Color.gray.opacity(0.4))
                        .cornerRadius(12)
                }
            }
            .padding(.horizontal)
            // Local time info message
            Text("All events are shown in your local time.")
                .font(.system(size: 18, weight: .medium))
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
                .padding(.top, 4)

            // Events list
            ScrollView {
                VStack(spacing: 16) {
                    ForEach(filteredEvents) { event in
                        let isAdded = addedEventIDs.contains(event.id)
                        Button(action: {
                            if isAdded {
                                removeEventFromCalendar(event: event)
                                addedEventIDs.remove(event.id)
                            } else {
                                addEventToCalendar(event: event)
                                addedEventIDs.insert(event.id)
                            }
                        }) {
                            HStack(alignment: .top, spacing: 16) {
                                VStack(alignment: .leading) {
                                    Text(eventDateTimeString(for: event))
                                        .font(.system(size: 12, weight: .medium))
                                        .foregroundColor(.blue)
                                    Text(event.title)
                                        .font(.system(size: 12, weight: .medium))
                                        .fontWeight(.bold)
                                    if let location = event.location {
                                        let venue = location.components(separatedBy: ",").first ?? location
                                        Text(venue)
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(.gray)
                                    }
                                }
                                Spacer()
                                VStack(alignment: .trailing) {
                                    Image(systemName: isAdded ? "minus.circle.fill" : "plus.circle.fill")
                                        .foregroundColor(isAdded ? .red : .blue)
                                        .font(.title2)
                                    Text(isAdded ? "Remove from Calendar" : "Add to Calendar")
                                        .font(.caption)
                                        .foregroundColor(isAdded ? .red : .blue)
                                }
                            }
                            .padding()
                            .background(Color(.systemGray6))
                            .cornerRadius(14)
                            .shadow(color: .black.opacity(0.06), radius: 4, x: 0, y: 2)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal)
                .padding(.bottom)
            }
        }
        .alert("Event Added!", isPresented: $showingCalendarAlert) {
            Button("OK") { }
        } message: {
            if let event = lastAddedEvent {
                Text("'Beyoncé - \(event.title)' has been added to your calendar.")
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
    
    // Add event to user's calendar using EventKit
    private func addEventToCalendar(event: Event) {
        let eventStore = EKEventStore()
        eventStore.requestFullAccessToEvents { granted, error in
            DispatchQueue.main.async {
                if granted && error == nil {
                    let ekEvent = EKEvent(eventStore: eventStore)
                    ekEvent.title = event.title
                    ekEvent.startDate = event.date
                    ekEvent.endDate = event.date.addingTimeInterval(2 * 60 * 60) // 2 hours duration
                    ekEvent.calendar = eventStore.defaultCalendarForNewEvents
                    do {
                        try eventStore.save(ekEvent, span: .thisEvent)
                        self.lastAddedEvent = event
                        self.showingCalendarAlert = true
                    } catch {
                        print("Error saving event: \(error)")
                    }
                } else {
                    print("Calendar access denied or error: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
        }
    }
    // Remove event from user's calendar using EventKit
    private func removeEventFromCalendar(event: Event) {
        let eventStore = EKEventStore()
        eventStore.requestFullAccessToEvents { granted, error in
            DispatchQueue.main.async {
                if granted && error == nil {
                    let predicate = eventStore.predicateForEvents(withStart: event.date, end: event.date.addingTimeInterval(2 * 60 * 60), calendars: nil)
                    let matchingEvents = eventStore.events(matching: predicate).filter { $0.title == event.title }
                    for ekEvent in matchingEvents {
                        do {
                            try eventStore.remove(ekEvent, span: .thisEvent)
                        } catch {
                            print("Error removing event: \(error)")
                        }
                    }
                } else {
                    print("Calendar access denied or error: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
        }
    }
    // Reminder: Add 'Privacy - Calendars Usage Description' to Info.plist
    // Example value: "This app needs access to your calendar to add events."
}

struct GameTile: Identifiable {
    let id = UUID()
    let title: String
    let imageName: String // Use asset name, SF Symbol, or emoji
    let isCustomImage: Bool
    let isEmoji: Bool
    let isLarge: Bool
    var isOn: Bool
}

struct GameView: View {
    @EnvironmentObject var tilesViewModel: TilesViewModel
    @State private var showConfirmation = false
    @State private var confirmationText = ""
    @State private var isLoading = false
    @State private var showError = false
    @State private var errorMessage = ""
    @State private var pendingToggleIndex: Int? = nil // Track which tile is being toggled
    
    func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            // Handle permission result if needed
        }
    }
    
    func scheduleNotification(for tile: GameTile) {
        let content = UNMutableNotificationContent()
        content.title = "Beyhive Alert"
        content.body = "It's time for \(tile.title)!"
        content.sound = .default
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 5, repeats: false)
        let request = UNNotificationRequest(identifier: tile.title, content: content, trigger: trigger)
        UNUserNotificationCenter.current().add(request)
    }
    
    func removeNotification(for tile: GameTile) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [tile.title])
    }
    
    var body: some View {
        VStack(spacing: 0) {
            Text("Notifications")
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.black)
                .padding(.top, 24)
            Spacer(minLength: 0)
            let columns = [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())]
            LazyVGrid(columns: columns, spacing: 24) {
                ForEach(Array(tilesViewModel.tiles.enumerated()), id: \ .element.id) { idx, tile in
                    Button(action: {
                        // Start loading and track which tile is pending
                        isLoading = true
                        pendingToggleIndex = idx
                        // Prepare new preferences with the toggled value
                        var newTiles = tilesViewModel.tiles
                        newTiles[idx].isOn.toggle()
                        let newPrefs = tilesViewModel.prefsFromTiles(newTiles)
                        updateNotificationPreferences(preferences: newPrefs) { success in
                            DispatchQueue.main.async {
                                isLoading = false
                                pendingToggleIndex = nil
                                if success {
                                    tilesViewModel.tiles = newTiles
                                    confirmationText = newTiles[idx].isOn ? "Enabled for \(tile.title)" : "Disabled for \(tile.title)"
                                    showConfirmation = true
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                                        showConfirmation = false
                                    }
                                } else {
                                    errorMessage = "Failed to save preferences. Please try again."
                                    showError = true
                                }
                            }
                        }
                    }) {
                        VStack(spacing: 8) {
                            if tile.isEmoji {
                                Text(tile.imageName)
                                    .font(.system(size: 48))
                                    .foregroundColor(.black)
                            } else if tile.isCustomImage {
                                Image(tile.imageName)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: tile.isLarge ? 72 : 48, height: tile.isLarge ? 72 : 48)
                            } else {
                                Image(systemName: tile.imageName)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 48, height: 48)
                                    .foregroundColor(.black)
                            }
                            Text(tile.title)
                                .font(.caption2)
                                .foregroundColor(.black)
                                .multilineTextAlignment(.center)
                        }
                        .padding()
                        .background(
                            Group {
                                if isLoading && pendingToggleIndex == idx {
                                    Color.gray.opacity(0.3)
                                } else if tile.isOn {
                                    LinearGradient(
                                        gradient: Gradient(colors: [Color.red, Color.white, Color.blue]),
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    )
                                } else {
                                    Color.gray
                                }
                            }
                        )
                        .cornerRadius(16)
                        .opacity(tile.isOn ? 1.0 : 0.3)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .disabled(isLoading) // Prevent toggling multiple at once
                }
            }
            .padding(.horizontal)
            .padding(.top, 16)
            Spacer()
            HStack(spacing: 8) {
                Image(systemName: "info.circle")
                    .foregroundColor(.gray)
                Text("Tiles in color are on, tiles in gray are off.")
                    .font(.caption2)
                    .foregroundColor(.gray)
            }
            .padding(.top, 16)
            .padding(.bottom, 32)
            if showConfirmation {
                Text(confirmationText)
                    .font(.subheadline)
                    .foregroundColor(.white)
                    .padding(.horizontal, 24)
                    .padding(.vertical, 12)
                    .background(Color.black.opacity(0.8))
                    .cornerRadius(16)
                    .transition(.opacity)
                    .padding(.bottom, 16)
            }
        }
        .background(Color.white.ignoresSafeArea())
        .onAppear {
            if let deviceToken = UIApplication.deviceTokenString {
                tilesViewModel.fetchPreferencesFromBackend(deviceToken: deviceToken) { prefs in
                    DispatchQueue.main.async {
                        tilesViewModel.applyPreferences(prefs)
                    }
                }
            }
            requestNotificationPermission()
        }
        .alert("Error", isPresented: $showError) {
            Button("OK") { }
        } message: {
            Text(errorMessage)
        }
    }
}

// Add this helper to TilesViewModel
extension TilesViewModel {
    func prefsFromTiles(_ tiles: [GameTile]) -> [String: Bool] {
        var prefs: [String: Bool] = [:]
        for tile in tiles {
            switch tile.title {
            case "Beyoncé on Stage":
                prefs["beyonceOnStage"] = tile.isOn
            case "Concert Start":
                prefs["concertStart"] = tile.isOn
            case "AMERICA HAS A PROBLEM starts":
                prefs["americaHasAProblem"] = tile.isOn
            case "TYRANT starts":
                prefs["tyrant"] = tile.isOn
            case "Last Act starts":
                prefs["lastAct"] = tile.isOn
            case "16 CARRIAGES starts":
                prefs["sixteenCarriages"] = tile.isOn
            case "AMEN starts":
                prefs["amen"] = tile.isOn
            default:
                break
            }
        }
        return prefs
    }
}

// Update updateNotificationPreferences to log device token and preferences
func updateNotificationPreferences(preferences: [String: Bool], completion: @escaping (Bool) -> Void) {
    guard let token = UIApplication.deviceTokenString else {
        print("[DEBUG] No device token available yet. Cannot save preferences.")
        completion(false)
        return
    }
    print("[DEBUG] Saving preferences for device token: \(token)")
    print("[DEBUG] Preferences: \(preferences)")
    guard let url = URL(string: "https://beyhive-backend.onrender.com/register-device") else { completion(false); return }
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    let body: [String: Any] = [
        "deviceToken": token,
        "preferences": preferences
    ]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            print("[DEBUG] Error sending preferences: \(error)")
            completion(false)
        } else {
            print("[DEBUG] Preferences sent successfully!")
            completion(true)
        }
    }.resume()
}

// MARK: - News Model
struct NewsItem: Identifiable {
    let id = UUID()
    let title: String
    let source: String
    let date: Date
    let description: String
    let iconName: String
}

// MARK: - Newsfeed View
struct NewsCardView: View {
    let item: NewsItem
    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            Image(systemName: item.iconName)
                .resizable()
                .scaledToFit()
                .frame(width: 36, height: 36)
                .foregroundColor(.yellow)
                .padding(.top, 4)
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(item.title)
                        .font(.caption2)
                        .foregroundColor(.black)
                    Spacer()
                    Text(item.source)
                        .font(.caption2)
                        .foregroundColor(.black.opacity(0.7))
                }
                Text(item.date, style: .date)
                    .font(.caption2)
                    .foregroundColor(.black.opacity(0.6))
                Text(item.description)
                    .font(.caption2)
                    .foregroundColor(.black)
            }
        }
        .padding()
        .background(
            Color(.systemGray6)
        )
        .cornerRadius(18)
        .shadow(color: .black.opacity(0.08), radius: 8, x: 0, y: 4)
    }
}

struct TrackersView: View {
    enum TrackerTab { case setlist, outfit }
    @State private var selectedTab: TrackerTab = .setlist
    let acts: [(title: String, songs: [String])] = [
        ("Act 1 - Intro", [
            "Intro (contains elements of 'AMERIICAN REQUIEM')",
            "AMERIICAN REQUIEM",
            "Blackbird (The Beatles cover) (\"COWBOY CARTER\" version)",
            "The Star-Spangled Banner (John Stafford Smith & Francis Scott Key cover) (includes elements of Jimi Hendrix's instrumental arrangement originally performed at Woodstock)",
            "Freedom (shortened)",
            "YA YA / Why Don't You Love Me",
            "Song played from tape: OH LOUISIANA"
        ]),
        ("Act 2 - Revolution", [
            "PROPAGANDA (contains elements of Those Guys' 'An American Poem' and Death Grips' 'You Might Think He Loves…')",
            "AMERICA HAS A PROBLEM (contains elements of 'AMERICA HAS A PROBLEM (feat. Kendrick Lamar)' & 'SPAGHETTII')",
            "SPAGHETTII (contains elements of 'ESSA TÁ QUENTE', 'WTHELLY', 'Flawless', 'Run the World (Girls)' & 'MY POWER')",
            "Formation (shortened)",
            "MY HOUSE (contains elements of Wisp's 'Your Face' and 'Bow Down')",
            "Diva"
        ]),
        ("Act 3 - Refuge TRAILER", [
            "TRAILER (contains elements of Justice's 'Genesis', JPEGMAFIA's 'don't rely on other men' and 'I Been On')",
            "ALLIIGATOR TEARS (shortened)",
            "JUST FOR FUN (shortened)",
            "PROTECTOR (with Rumi Carter) (contains elements of 'Dangerously In Love 2')",
            "Song played from tape: The First Time Ever I Saw Your Face (Ewan MacColl & Peggy Seeger song) (Roberta Flack version)",
            "FLAMENCO"
        ]),
        ("Act 4 - Marfa", [
            "PEEP SHOW (contains elements of Marian Anderson's 'Deep River', Nancy Sinatra's 'Lightning's Girl')",
            "DESERT EAGLE (extended intro)",
            "RIIVERDANCE (shortened)",
            "II HANDS II HEAVEN (shortened)",
            "TYRANT (shortened; contains elements of 'Haunted')",
            "THIQUE (shortened; contains elements of 'TYRANT', 'Bills, Bills, Bills' & 'Say My Name')",
            "LEVII'S JEANS (shortened; contains elements of 'THIQUE')",
            "SWEET ★ HONEY ★ BUCKIIN' / PURE/HONEY / SUMMER RENAISSANCE (contains elements of 69 Boyz' 'Tootsie Roll')"
        ]),
        ("Act 5 - Tease", [
            "OUTLAW (50FT COWBOY) (contains elements of BigXthaPlug's 'The Largest', Esther Marrow's 'Walk Tall' & '7/11')",
            "TEXAS HOLD 'EM (extended intro; contains elements of 'TEXAS HOLD 'EM (PONY UP REMIX)' & 'CHURCH GIRL')",
            "Crazy in Love (Homecoming version; contains elements of Cassidy's 'I'm a Hustla')",
            "Single Ladies (Put a Ring on It) (shortened; contains elements of 'Get Me Bodied')",
            "Love on Top (shortened; contains elements of 'Freakum Dress')",
            "Irreplaceable (shortened)",
            "If I Were a Boy (shortened; contains elements of 'JOLENE')",
            "DOLLY P",
            "Jolene (Dolly Parton cover) (COWBOY CARTER version; contains elements of 'Daddy Lessons')",
            "Daddy Lessons (shortened)",
            "BODYGUARD",
            "II MOST WANTED (snippet; contains elements of 'Blow')",
            "Dance for You / SMOKE HOUR II (contains elements of 'CUFF IT (WETTER REMIX)')",
            "HEATED (shortened; contains elements of 803Fresh's 'Boots on the Ground')",
            "Before I Let Go (Maze cover)"
        ]),
        ("Act 6 - Renaissance", [
            "HOLY DAUGHTER (contains elements of 'Ghost' & 'I Care')",
            "DAUGHTER (extended outro)",
            "OPERA (contains elements of 'ENERGY' & 'An American Poem')",
            "I'M THAT GIRL (shortened; contains elements of 'APESHIT')",
            "COZY",
            "ALIEN SUPERSTAR (Shortened)",
            "Song played from tape: Déjà Vu (with Blue Ivy Carter) (dance Interlude)"
        ]),
        ("Act 7 - Reclaimation", [
            "LEGACY (contains elements of Michael Jackson's 'I Wanna Be Where You Are' & Those Guys' 'An American Poem')",
            "AMEN (extended intro & outro)"
        ])
    ]
    var gradient: LinearGradient {
        LinearGradient(
            gradient: Gradient(colors: [Color.red, Color.white, Color.blue]),
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }
    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()
            VStack(alignment: .leading, spacing: 0) {
                // Segmented control
                HStack(spacing: 12) {
                    Button(action: { selectedTab = .setlist }) {
                        Text("Song Tracker")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(selectedTab == .setlist ? .black : .gray)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 18)
                            .background(
                                selectedTab == .setlist
                                    ? AnyView(gradient)
                                    : AnyView(Color(.lightGray))
                            )
                    }
                    Button(action: { selectedTab = .outfit }) {
                        Text("Outfit Tracker")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(selectedTab == .outfit ? .black : .gray)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 18)
                            .background(
                                selectedTab == .outfit
                                    ? AnyView(gradient)
                                    : AnyView(Color(.lightGray))
                            )
                    }
                }
                .frame(height: 56)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 40, style: .continuous))
                .padding(.horizontal, 24)
                .padding(.top, 24)
                // Main content
                if selectedTab == .setlist {
                    Text("Trackers")
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.black)
                        .padding(.top, 32)
                        .padding(.horizontal)
                    Text("Track songs and outfits from each show.")
                        .font(.system(size: 21, weight: .medium))
                        .foregroundColor(.black.opacity(0.8))
                        .padding(.horizontal)
                        .padding(.top, 4)
                    Text("Setlist")
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.black)
                        .padding(.leading)
                        .padding(.top, 24)
                    // Setlist Section
                    ScrollView {
                        VStack(alignment: .leading, spacing: 24) {
                            ForEach(acts.indices, id: \ .self) { idx in
                                let act = acts[idx]
                                HStack(alignment: .top, spacing: 0) {
                                    RoundedRectangle(cornerRadius: 6)
                                        .fill(Color.purple.opacity(0.0))
                                        .frame(width: 0, height: 38)
                                    VStack(alignment: .leading, spacing: 0) {
                                        Text(act.title)
                                            .font(.caption2)
                                            .foregroundColor(.black)
                                            .padding(.leading, 10)
                                            .padding(.top, 6)
                                        VStack(alignment: .leading, spacing: 0) {
                                            ForEach(act.songs, id: \ .self) { song in
                                                HStack(alignment: .top, spacing: 8) {
                                                    Image(systemName: "music.note")
                                                        .foregroundColor(.blue)
                                                        .padding(.top, 3)
                                                    Text(song)
                                                        .font(.system(size: 16, weight: .medium, design: .rounded))
                                                        .foregroundColor(.black)
                                                        .padding(.vertical, 4)
                                                }
                                                .padding(.leading, 8)
                                            }
                                        }
                                        .padding(.bottom, 8)
                                    }
                                    .background(
                                        LinearGradient(
                                            gradient: Gradient(colors: [Color.red, Color.white, Color.blue]),
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .cornerRadius(18)
                                    .shadow(color: Color(.black).opacity(0.04), radius: 4, x: 0, y: 2)
                                    .padding(.vertical, 8)
                                    .padding(.horizontal, 0)
                                }
                                .padding(.horizontal, 8)
                            }
                        }
                        .padding(.horizontal)
                        .padding(.top, 8)
                    }
                } else {
                    // Outfit Tracker Section
                    OutfitsView()
                }
            }
        }
    }
}

// Helper extension for IBM Plex Mono font
extension View {
    func ibmPlexMono(size: CGFloat, weight: Font.Weight = .regular) -> some View {
        let fontName: String
        switch weight {
        case .bold:
            fontName = "IBMPlexMono-Bold"
        default:
            fontName = "IBMPlexMono-Regular"
        }
        return self.font(.custom(fontName, size: size))
    }
}





// Add this utility at the top-level (outside any struct)
let euCountryCodes: Set<String> = [
    "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE"
]

func isEUUser() -> Bool {
    let currentRegion = Locale.current.region?.identifier ?? ""
    return euCountryCodes.contains(currentRegion)
}

func requestNotificationPermissions() {
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
        if granted {
            DispatchQueue.main.async {
                UIApplication.shared.registerForRemoteNotifications()
            }
        }
    }
}

struct NotificationPreferencesView: View {
    @State private var isConcertStartOn = false
    @State private var isBeyonceOnStageOn = false
    @State private var isAmericaHasAProblemOn = false
    @State private var isTyrantOn = false
    @State private var isLastActOn = false
    @State private var isSixteenCarriagesOn = false
    @State private var isAmenOn = false

    var body: some View {
        Form {
            Toggle("Concert Start", isOn: $isConcertStartOn.onChange(sendPreferences)).font(.caption2)
            Toggle("Beyoncé on Stage", isOn: $isBeyonceOnStageOn.onChange(sendPreferences)).font(.caption2)
            Toggle("AMERICA HAS A PROBLEM starts", isOn: $isAmericaHasAProblemOn.onChange(sendPreferences)).font(.caption2)
            Toggle("TYRANT starts", isOn: $isTyrantOn.onChange(sendPreferences)).font(.caption2)
            Toggle("Last Act starts", isOn: $isLastActOn.onChange(sendPreferences)).font(.caption2)
            Toggle("16 CARRIAGES starts", isOn: $isSixteenCarriagesOn.onChange(sendPreferences)).font(.caption2)
            Toggle("AMEN starts", isOn: $isAmenOn.onChange(sendPreferences)).font(.caption2)
        }
        .navigationTitle("Notification Preferences")
    }

    func sendPreferences(_ _: Bool) {
        let preferences: [String: Bool] = [
            "concertStart": isConcertStartOn,
            "beyonceOnStage": isBeyonceOnStageOn,
            "americaHasAProblem": isAmericaHasAProblemOn,
            "tyrant": isTyrantOn,
            "lastAct": isLastActOn,
            "sixteenCarriages": isSixteenCarriagesOn,
            "amen": isAmenOn
        ]
        updateNotificationPreferences(preferences: preferences)
    }
}

extension Binding {
    func onChange(_ handler: @escaping (Value) -> Void) -> Binding<Value> {
        Binding(
            get: { self.wrappedValue },
            set: { newValue in
                self.wrappedValue = newValue
                handler(newValue)
            }
        )
    }
}

func updateNotificationPreferences(preferences: [String: Bool]) {
    guard let token = UIApplication.deviceTokenString else {
        print("No device token available yet.")
        return
    }
    guard let url = URL(string: "https://beyhive-backend.onrender.com/register-device") else { return }
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    let body: [String: Any] = [
        "deviceToken": token,
        "preferences": preferences
    ]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            print("Error sending preferences: \(error)")
        } else {
            print("Preferences sent successfully!")
        }
    }.resume()
}

// Helper function for event dates
func dateFrom(_ date: String, time: String, timeZoneID: String) -> Date {
    let formatter = DateFormatter()
    formatter.dateFormat = "yyyy-MM-dd HH:mm"
    formatter.timeZone = TimeZone(identifier: timeZoneID)
    return formatter.date(from: "\(date) \(time)") ?? Date()
}

#if DEBUG
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
            .environmentObject(EventsViewModel())
    }
}
#endif

#if DEBUG
struct ScheduleView_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleView()
            .environmentObject(EventsViewModel())
    }
}
#endif

class TilesViewModel: ObservableObject {
    @Published var tiles: [GameTile] = [
        GameTile(title: "Concert Start", imageName: "Bee_Icon", isCustomImage: true, isEmoji: false, isLarge: true, isOn: true),
        GameTile(title: "Beyoncé on Stage", imageName: "person.fill", isCustomImage: false, isEmoji: false, isLarge: false, isOn: true),
        GameTile(title: "AMERICA HAS A PROBLEM starts", imageName: "americanflag", isCustomImage: true, isEmoji: false, isLarge: false, isOn: true),
        GameTile(title: "TYRANT starts", imageName: "mechanicalbull", isCustomImage: true, isEmoji: false, isLarge: false, isOn: true),
        GameTile(title: "Last Act starts", imageName: "sparkles", isCustomImage: false, isEmoji: false, isLarge: false, isOn: true),
        GameTile(title: "16 CARRIAGES starts", imageName: "Cattalaic", isCustomImage: true, isEmoji: false, isLarge: false, isOn: true),
        GameTile(title: "AMEN starts", imageName: "americanflag", isCustomImage: true, isEmoji: false, isLarge: false, isOn: true)
    ]

    // Fetch preferences from backend and apply to tiles
    func fetchPreferencesFromBackend(deviceToken: String, completion: @escaping ([String: Bool]) -> Void) {
        print("[DEBUG] Fetching preferences for device token: \(deviceToken)")
        guard let url = URL(string: "https://beyhive-backend.onrender.com/device-preferences/\(deviceToken)") else { return }
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("[DEBUG] Error fetching preferences: \(error)")
                completion([:])
                return
            }
            guard let data = data else {
                print("[DEBUG] No data received when fetching preferences.")
                completion([:])
                return
            }
            if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
               let prefs = json["preferences"] as? [String: Bool] {
                print("[DEBUG] Fetched preferences: \(prefs)")
                completion(prefs)
            } else {
                print("[DEBUG] Could not parse preferences from backend response.")
                completion([:])
            }
        }.resume()
    }

    func applyPreferences(_ prefs: [String: Bool]) {
        for i in tiles.indices {
            let title = tiles[i].title
            switch title {
            case "Beyoncé on Stage":
                tiles[i].isOn = prefs["beyonceOnStage"] ?? false
            case "Concert Start":
                tiles[i].isOn = prefs["concertStart"] ?? false
            case "AMERICA HAS A PROBLEM starts":
                tiles[i].isOn = prefs["americaHasAProblem"] ?? false
            case "TYRANT starts":
                tiles[i].isOn = prefs["tyrant"] ?? false
            case "Last Act starts":
                tiles[i].isOn = prefs["lastAct"] ?? false
            case "16 CARRIAGES starts":
                tiles[i].isOn = prefs["sixteenCarriages"] ?? false
            case "AMEN starts":
                tiles[i].isOn = prefs["amen"] ?? false
            default:
                break
            }
        }
    }

    // Add this function to get the current preferences for all tiles
    func currentPreferences() -> [String: Bool] {
        var prefs: [String: Bool] = [:]
        for tile in tiles {
            switch tile.title {
            case "Beyoncé on Stage":
                prefs["beyonceOnStage"] = tile.isOn
            case "Concert Start":
                prefs["concertStart"] = tile.isOn
            case "AMERICA HAS A PROBLEM starts":
                prefs["americaHasAProblem"] = tile.isOn
            case "TYRANT starts":
                prefs["tyrant"] = tile.isOn
            case "Last Act starts":
                prefs["lastAct"] = tile.isOn
            case "16 CARRIAGES starts":
                prefs["sixteenCarriages"] = tile.isOn
            case "AMEN starts":
                prefs["amen"] = tile.isOn
            default:
                break
            }
        }
        return prefs
    }
}

// Add this at the very bottom of the file, outside any struct/class
extension UIApplication {
    private static var _deviceTokenString: String?
    static var deviceTokenString: String? {
        get { _deviceTokenString }
        set { _deviceTokenString = newValue }
    }
}

// Free NotificationsView (no paywall)
struct NotificationsView: View {
    @StateObject private var storeKitManager = StoreKitManager()
    @StateObject private var tilesViewModel = TilesViewModel()
    @State private var isPurchasing = false
    @State private var errorMessage: String?
    var body: some View {
        ZStack {
            GameView()
                .environmentObject(tilesViewModel)
                .blur(radius: storeKitManager.hasPurchased ? 0 : 3)
                .disabled(!storeKitManager.hasPurchased)
            if !storeKitManager.hasPurchased {
                VStack(spacing: 18) {
                    Image(systemName: "lock.fill")
                        .resizable()
                        .frame(width: 48, height: 38)
                        .foregroundColor(.yellow)
                        .padding(.bottom, 8)
                    Text("Unlock Notifications")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(.black)
                    Text("Get access to exclusive notifications for just $1.99.")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                    HStack(spacing: 16) {
                        Button(action: { purchase() }) {
                            Text("Unlock for $1.99")
                                .font(.headline)
                                .foregroundColor(.white)
                                .padding(.vertical, 10)
                                .padding(.horizontal, 24)
                                .background(Color.yellow)
                                .cornerRadius(12)
                        }
                        Button(action: { restore() }) {
                            Text("Restore Purchases")
                                .font(.subheadline)
                                .foregroundColor(.blue)
                        }
                    }
                    if isPurchasing {
                        ProgressView("Processing...")
                    }
                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                }
                .padding()
                .background(Color.white.opacity(0.7))
                .cornerRadius(22)
                .shadow(radius: 16)
                .padding(.horizontal, 24)
            }
        }
        .onAppear {
            Task { await storeKitManager.checkPurchased() }
        }
    }
    func purchase() {
        Task {
            isPurchasing = true
            do {
                if let product = try await Product.products(for: [storeKitManager.productID]).first {
                    let result = try await product.purchase()
                    switch result {
                    case .success:
                        errorMessage = nil
                        Task { await storeKitManager.checkPurchased() }
                    case .userCancelled:
                        errorMessage = "Purchase cancelled."
                    default:
                        errorMessage = "Purchase not completed."
                    }
                } else {
                    errorMessage = "Product not found."
                }
            } catch {
                errorMessage = error.localizedDescription
            }
            isPurchasing = false
        }
    }
    func restore() {
        Task {
            isPurchasing = true
            do {
                for await result in Transaction.currentEntitlements {
                    if case .verified(let transaction) = result, transaction.productID == storeKitManager.productID {
                        errorMessage = nil
                        Task { await storeKitManager.checkPurchased() }
                        isPurchasing = false
                        return
                    }
                }
                errorMessage = "No purchases to restore."
            } catch {
                errorMessage = error.localizedDescription
            }
            isPurchasing = false
        }
    }
}

struct PaywallView: View {
    @State private var isPurchasing = false
    @State private var errorMessage: String?
    @State private var purchased = false
    @Environment(\.dismiss) private var dismiss
    let productID = "com.chasedavis.beyhivealert.notificationssss"
    var body: some View {
        VStack(spacing: 24) {
            Text("Unlock Notifications")
                .font(.largeTitle).bold()
            Text("Get access to notifications and support the app!")
                .multilineTextAlignment(.center)
            if isPurchasing {
                ProgressView("Processing...")
            } else if purchased {
                Text("Thank you for your purchase!")
                    .foregroundColor(.green)
                Button("Continue") { dismiss() }
                    .font(.title2)
                    .padding()
                    .background(Color.yellow)
                    .cornerRadius(12)
            } else {
                Button("Unlock for $1.99") {
                    purchase()
                }
                .font(.title2)
                .padding()
                .background(Color.yellow)
                .cornerRadius(12)
                Button("Restore Purchases") {
                    restore()
                }
                .font(.body)
                .padding(.top, 8)
            }
            if let error = errorMessage {
                Text(error)
                    .foregroundColor(.red)
            }
        }
        .padding()
        .onAppear {
            checkPurchased()
        }
    }
    func purchase() {
        Task {
            isPurchasing = true
            do {
                if let product = try await Product.products(for: [productID]).first {
                    let result = try await product.purchase()
                    switch result {
                    case .success:
                        purchased = true
                        errorMessage = nil
                    case .userCancelled:
                        errorMessage = "Purchase cancelled."
                    default:
                        errorMessage = "Purchase not completed."
                    }
                } else {
                    errorMessage = "Product not found."
                }
            } catch {
                errorMessage = error.localizedDescription
            }
            isPurchasing = false
        }
    }
    func checkPurchased() {
        Task {
            purchased = await isProductPurchased(productID: productID)
        }
    }
    func restore() {
        Task {
            isPurchasing = true
            do {
                for await result in Transaction.currentEntitlements {
                    if case .verified(let transaction) = result, transaction.productID == productID {
                        purchased = true
                        errorMessage = nil
                        return
                    }
                }
                errorMessage = "No purchases to restore."
            } catch {
                errorMessage = error.localizedDescription
            }
            isPurchasing = false
        }
    }
}

// MARK: - Quiz Networking Models

// Helper extension for mapKeys
extension Dictionary {
    func mapKeys<T>(_ transform: (Key) -> T) -> [T: Value] {
        var result: [T: Value] = [:]
        for (key, value) in self {
            result[transform(key)] = value
        }
        return result
    }
}

// Helper to check if a StoreKit product is purchased
func isProductPurchased(productID: String) async -> Bool {
    for await result in Transaction.currentEntitlements {
        if case .verified(let transaction) = result, transaction.productID == productID {
            return true
        }
    }
    return false
}

// MARK: - StoreKit 2 Paywall (Restored Custom Design)

@MainActor
class StoreKitManager: ObservableObject {
    @Published var hasPurchased: Bool = false
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var product: Product?
    let productID = "com.chasedavis.beyhivealert.notificationssss"
    init() {
        Task { await fetchProduct() }
        Task { await checkPurchased() }
        listenForTransactions()
    }
    func fetchProduct() async {
        do {
            let storeProducts = try await Product.products(for: [productID])
            self.product = storeProducts.first
        } catch {
            self.errorMessage = "Failed to load product."
        }
    }
    func checkPurchased() async {
        for await result in StoreKit.Transaction.currentEntitlements {
            if case .verified(let transaction) = result, transaction.productID == productID {
                hasPurchased = true
                return
            }
        }
        hasPurchased = false
    }
    func purchase() async {
        guard let product = product else { return }
        isLoading = true
        do {
            let result = try await product.purchase()
            switch result {
            case .success(let verification):
                if case .verified(_) = verification {
                    hasPurchased = true
                }
            default:
                break
            }
        } catch {
            errorMessage = "Purchase failed: \(error.localizedDescription)"
        }
        isLoading = false
    }
    func restore() async {
        isLoading = true
        do {
            let _ = try await AppStore.sync()
            await checkPurchased()
        } catch {
            errorMessage = "Restore failed: \(error.localizedDescription)"
        }
        isLoading = false
    }
    func listenForTransactions() {
        Task.detached {
            for await verificationResult in StoreKit.Transaction.updates {
                await self.handle(transactionResult: verificationResult)
            }
        }
    }
    @MainActor
    func handle(transactionResult: VerificationResult<StoreKit.Transaction>) async {
        switch transactionResult {
        case .verified(let transaction):
            if transaction.productID == productID {
                hasPurchased = true
            }
            await transaction.finish()
        default:
            break
        }
    }
}

struct NotificationsPaywallView: View {
    var body: some View {
        GameView()
    }
}

// Add missing helpers above SurvivorQuizView
struct CustomDropdown: View {
    let options: [(title: String, description: String?, imageName: String?, imageUrl: String?)]
    let selection: String
    let onSelect: (String) -> Void
    var showDropdown: Bool
    let setShowDropdown: (Bool) -> Void
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button(action: { setShowDropdown(!showDropdown) }) {
                HStack {
                    if let selected = options.first(where: { $0.title == selection }), (selected.imageName != nil || selected.imageUrl != nil), !selection.isEmpty {
                        OutfitIconView(imageName: selected.imageName, imageUrl: selected.imageUrl)
                    }
                    Text(selection.isEmpty ? "Select an answer" : selection)
                        .foregroundColor(selection.isEmpty ? .gray : .black)
                        .font(.body)
                    Spacer()
                    Image(systemName: showDropdown ? "chevron.up" : "chevron.down")
                        .foregroundColor(.gray)
                }
                .padding(.horizontal)
                .frame(height: 44)
                .background(
                    RoundedRectangle(cornerRadius: 14)
                        .stroke(LinearGradient(gradient: Gradient(colors: [Color.red, Color.blue]), startPoint: .leading, endPoint: .trailing), lineWidth: 1.5)
                        .background(RoundedRectangle(cornerRadius: 14).fill(Color.white))
                )
            }
            .buttonStyle(PlainButtonStyle())
            if showDropdown {
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(options.indices, id: \.self) { idx in
                        let option = options[idx]
                        Button(action: {
                            onSelect(option.title)
                            setShowDropdown(false)
                        }) {
                            HStack {
                                if option.imageName != nil || option.imageUrl != nil {
                                    OutfitIconView(imageName: option.imageName, imageUrl: option.imageUrl)
                                }
                                Text(option.title)
                                    .foregroundColor(.black)
                                    .padding(.vertical, 10)
                                Spacer()
                            }
                            .padding(.horizontal)
                        }
                        .background(selection == option.title ? Color.red.opacity(0.08) : Color.clear)
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .background(
                    RoundedRectangle(cornerRadius: 14)
                        .fill(Color.white)
                        .shadow(color: Color.gray.opacity(0.15), radius: 8, x: 0, y: 4)
                )
                .padding(.top, 2)
            }
        }
        .animation(.easeInOut(duration: 0.18), value: showDropdown)
    }
}

struct OutfitIconView: View {
    let imageName: String?
    let imageUrl: String?
    var body: some View {
        Group {
            if let imageUrl = imageUrl, !imageUrl.isEmpty, let url = URL(string: imageUrl) {
                AsyncImage(url: url) { image in
                    image.resizable().aspectRatio(contentMode: .fit)
                } placeholder: {
                    Rectangle().fill(Color.gray.opacity(0.3)).overlay(ProgressView().scaleEffect(0.8))
                }
                .frame(width: 28, height: 28)
                .clipShape(RoundedRectangle(cornerRadius: 6))
            } else if let imageName = imageName, !imageName.isEmpty {
                Image(imageName)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 28, height: 28)
                    .clipShape(RoundedRectangle(cornerRadius: 6))
            } else {
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 28, height: 28)
                    .clipShape(RoundedRectangle(cornerRadius: 6))
            }
        }
    }
}

struct ServerResponse: Codable {
    let success: Bool
    let message: String?
}

struct FetchResponse: Codable {
    let response: QuizResponse?
}
struct QuizResponse: Codable {
    let answers: [String: String]
}

// Add fetchQuizResponse to SurvivorQuizView
// (Place this inside SurvivorQuizView if not already present)
// ... existing code ...

struct PartnersResponse: Codable {
    let success: Bool
    let partners: [Partner]
}

struct Partner: Identifiable, Codable {
    let id = UUID()
    let name: String
    let description: String
    let iconUrl: String?
    let link: String
    enum CodingKeys: String, CodingKey {
        case name, description, iconUrl, link
    }
}
