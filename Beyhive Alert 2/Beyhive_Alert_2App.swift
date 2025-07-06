//
//  Beyhive_Alert_2App.swift
//  Beyhive Alert 2
//
//  Created by Chase Davis on 7/1/25.
//

import SwiftUI

@main
struct Beyhive_Alert_2App: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @StateObject private var eventsViewModel = EventsViewModel()
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup {
            SplashScreenView()
                .environmentObject(eventsViewModel)
                .onChange(of: scenePhase) { oldPhase, newPhase in
                    if newPhase == .active {
                        Task {
                            await eventsViewModel.refreshEvents()
                        }
                    }
                }
        }
    }
}

struct SplashScreenView: View {
    @State private var isActive = false
    @EnvironmentObject var eventsViewModel: EventsViewModel

    var body: some View {
        if isActive {
            ContentView()
                .environmentObject(eventsViewModel)
        } else {
            ZStack {
                LinearGradient(
                    gradient: Gradient(colors: [Color.red, Color.white, Color.blue]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                VStack(spacing: 24) {
                    Image("Bee_Icon")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 180, height: 180)
                        .shadow(radius: 16)
                    Text("Beyhive Alert")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.black)
                        .shadow(radius: 4)
                }
            }
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    withAnimation {
                        isActive = true
                    }
                }
            }
        }
    }
}
