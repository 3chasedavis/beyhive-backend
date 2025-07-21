import SwiftUI

struct InstagramFeedItem: Identifiable {
    let id = UUID()
    let title: String
    let author: String
    let authorUsername: String
    let authorProfileImageURL: String
    let postImageURL: String
    let postURL: String
    let publishedDate: Date
    let description: String
}

struct InstagramFeedSection {
    let title: String
    let username: String
    let profileImageAsset: String
    let profileURL: String
    let items: [InstagramFeedItem]
}

struct InstagramFeedView: View {
    @State private var sections: [InstagramFeedSection] = []
    @State private var isLoading = true
    @State private var errorMessage: String? = nil

    let feeds: [(title: String, url: String, username: String, profileImageAsset: String, profileURL: String)] = [
        ("Beyoncé Updates", "https://rss.app/feeds/tsqXwAfrzfpjLSzb.xml", "@beyonceupdatesz", "beyonceupdatespfp", "https://instagram.com/beyonceupdatesz"),
        ("Arionce", "https://rss.app/feeds/IbhOSjEvEbRhT8Mu.xml", "@arionce.lifee", "arioncepfp", "https://instagram.com/arionce.lifee")
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            ForEach(sections, id: \ .title) { section in
                VStack(alignment: .leading, spacing: 20) {
                    HStack(spacing: 16) {
                        Image(section.profileImageAsset)
                            .resizable()
                            .frame(width: 48, height: 48)
                            .clipShape(Circle())
                        VStack(alignment: .leading, spacing: 2) {
                            Text(section.title)
                                .font(.title2)
                                .fontWeight(.bold)
                            Text(section.username)
                                .font(.caption)
                                .foregroundColor(.gray)
                                .lineLimit(1)
                                .truncationMode(.tail)
                                .minimumScaleFactor(0.6)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    ForEach(section.items) { item in
                        InstagramFeedCard(item: item, username: section.username, profileImageAsset: section.profileImageAsset, profileURL: section.profileURL)
                    }
                }
                .padding(.horizontal)
            }
            if isLoading {
                ProgressView("Loading Feeds...")
                    .padding()
            } else if let errorMessage = errorMessage {
                Text(errorMessage)
                    .foregroundColor(.red)
                    .padding()
            }
        }
        .onAppear {
            loadFeeds()
        }
    }

    func loadFeeds() {
        isLoading = true
        errorMessage = nil
        let group = DispatchGroup()
        var sectionResults: [InstagramFeedSection] = Array(repeating: InstagramFeedSection(title: "", username: "", profileImageAsset: "", profileURL: "", items: []), count: feeds.count)
        for (i, feed) in feeds.enumerated() {
            guard let url = URL(string: feed.url) else { continue }
            group.enter()
            fetchFeed(from: url) { items in
                sectionResults[i] = InstagramFeedSection(title: feed.title, username: feed.username, profileImageAsset: feed.profileImageAsset, profileURL: feed.profileURL, items: Array(items.prefix(2)))
                group.leave()
            }
        }
        group.notify(queue: .main) {
            self.sections = sectionResults
            self.isLoading = false
        }
    }

    func fetchFeed(from url: URL, completion: @escaping ([InstagramFeedItem]) -> Void) {
        URLSession.shared.dataTask(with: url) { data, response, error in
            var items: [InstagramFeedItem] = []
            defer { DispatchQueue.main.async { completion(items) } }
            guard let data = data, error == nil else { return }
            let parser = XMLParser(data: data)
            let delegate = SimpleRSSParser()
            parser.delegate = delegate
            if parser.parse() {
                items = delegate.items
            }
        }.resume()
    }
}

struct InstagramFeedCard: View {
    let item: InstagramFeedItem
    let username: String
    let profileImageAsset: String
    let profileURL: String
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 12) {
                Image(profileImageAsset)
                    .resizable()
                    .frame(width: 36, height: 36)
                    .clipShape(Circle())
                Text(username)
                    .font(.body)
                    .fontWeight(.semibold)
                    .lineLimit(1)
                    .truncationMode(.tail)
                Spacer()
                Button(action: {
                    if let url = URL(string: profileURL) {
                        UIApplication.shared.open(url)
                    }
                }) {
                    Text("See Profile")
                        .font(.body)
                        .padding(8)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }
            AsyncImage(url: URL(string: item.postImageURL)) { image in
                image.resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.gray
            }
            .frame(height: 120)
            .clipped()
            Button(action: {
                if let url = URL(string: item.postURL) {
                    UIApplication.shared.open(url)
                }
            }) {
                Text("View more on Instagram")
                    .font(.body)
                    .foregroundColor(.blue)
            }
            Text(item.description)
                .font(.body)
        }
        .padding(8)
        .background(Color.white)
        .cornerRadius(10)
        .shadow(radius: 2)
    }
}

// Simple RSS parser for demo purposes
class SimpleRSSParser: NSObject, XMLParserDelegate {
    var items: [InstagramFeedItem] = []
    var currentElement = ""
    var currentTitle = ""
    var currentDescription = ""
    var currentLink = ""
    var currentPubDate = ""
    var currentAuthor = ""
    var currentImageURL = ""
    var currentProfileImageURL = ""
    var currentAuthorUsername = ""

    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String] = [:]) {
        currentElement = elementName
        if elementName == "item" {
            currentTitle = ""
            currentDescription = ""
            currentLink = ""
            currentPubDate = ""
            currentAuthor = ""
            currentImageURL = ""
            currentProfileImageURL = ""
            currentAuthorUsername = ""
        }
    }

    func parser(_ parser: XMLParser, foundCharacters string: String) {
        switch currentElement {
        case "title": currentTitle += string
        case "description": currentDescription += string
        case "link": currentLink += string
        case "pubDate": currentPubDate += string
        case "author": currentAuthor += string
        default: break
        }
    }

    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        if elementName == "item" {
            // Extract image URL from <img src=...> in description
            var imageURL = ""
            let desc = currentDescription.trimmingCharacters(in: .whitespacesAndNewlines)
            if let imgTagRange = desc.range(of: "<img src=\"", options: .caseInsensitive),
               let endQuoteRange = desc[imgTagRange.upperBound...].range(of: "\"", options: .caseInsensitive) {
                imageURL = String(desc[imgTagRange.upperBound..<endQuoteRange.lowerBound])
            }
            // Remove HTML tags from description
            let cleanDesc = desc.replacingOccurrences(of: "<[^>]+>", with: "", options: .regularExpression)
            let formatter = DateFormatter()
            formatter.locale = Locale(identifier: "en_US_POSIX")
            formatter.dateFormat = "E, d MMM yyyy HH:mm:ss Z"
            let date = formatter.date(from: currentPubDate) ?? Date()
            let item = InstagramFeedItem(
                title: currentTitle.trimmingCharacters(in: .whitespacesAndNewlines),
                author: currentAuthor.isEmpty ? "Instagram User" : currentAuthor,
                authorUsername: currentAuthorUsername,
                authorProfileImageURL: currentProfileImageURL,
                postImageURL: imageURL,
                postURL: currentLink.trimmingCharacters(in: .whitespacesAndNewlines),
                publishedDate: date,
                description: cleanDesc
            )
            items.append(item)
        }
    }
} 