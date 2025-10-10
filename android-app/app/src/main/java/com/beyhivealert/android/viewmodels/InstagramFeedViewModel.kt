package com.beyhivealert.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.InstagramFeedItem
import com.beyhivealert.android.data.InstagramFeedSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class InstagramFeedViewModel : ViewModel() {
    
    private val _sections = MutableStateFlow<List<InstagramFeedSection>>(emptyList())
    val sections: StateFlow<List<InstagramFeedSection>> = _sections
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val feeds = listOf(
        FeedConfig(
            title = "Beyoncé Updates",
            url = "https://rss.app/feeds/tsqXwAfrzfpjLSzb.xml",
            username = "@beyonceupdatesz",
            profileImageAsset = "beyonceupdatespfp",
            profileURL = "https://instagram.com/beyonceupdatesz"
        ),
        FeedConfig(
            title = "Arionce",
            url = "https://rss.app/feeds/IbhOSjEvEbRhT8Mu.xml",
            username = "@arionce.lifee",
            profileImageAsset = "arioncepfp",
            profileURL = "https://instagram.com/arionce.lifee"
        )
    )
    
    fun loadFeeds() {
        println("=== INSTAGRAM FEED VIEWMODEL DEBUG ===")
        println("loadFeeds() called")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                println("=== RSS FEED LOADING DEBUG ===")
                println("Starting to load ${feeds.size} feeds")
                
                val sectionResults = mutableListOf<InstagramFeedSection>()
                
                feeds.forEach { feed ->
                    println("Fetching feed: ${feed.title} from ${feed.url}")
                    val items = fetchFeed(feed.url)
                    println("Fetched ${items.size} items for ${feed.title}")
                    
                    sectionResults.add(
                        InstagramFeedSection(
                            title = feed.title,
                            username = feed.username,
                            profileImageAsset = feed.profileImageAsset,
                            profileURL = feed.profileURL,
                            items = items.take(2) // Show only 2 items per feed
                        )
                    )
                }
                
                println("Total sections created: ${sectionResults.size}")
                _sections.value = sectionResults
                println("=== RSS FEED LOADING COMPLETE ===")
            } catch (e: Exception) {
                println("=== RSS FEED ERROR ===")
                println("Error: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Failed to load feeds: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
            private suspend fun fetchFeed(urlString: String): List<InstagramFeedItem> {
                return withContext(Dispatchers.IO) {
                    try {
                        println("Fetching RSS feed from: $urlString")
                        val url = URL(urlString)
                        val connection = url.openConnection()
                        connection.connectTimeout = 15000
                        connection.readTimeout = 15000
                        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
                        
                        println("DEBUG: About to connect to URL...")
                        val inputStream = connection.getInputStream()
                        println("DEBUG: Successfully opened input stream")
                        val content = inputStream.bufferedReader().readText()
                        println("DEBUG: Raw RSS content length: ${content.length}")
                        println("DEBUG: First 200 chars of content: ${content.take(200)}")
            
            // Split content by </item> to get individual items
            val items = mutableListOf<InstagramFeedItem>()
            
            // Split by </item> and filter out empty strings
            val itemStrings = content.split("</item>")
                .filter { it.contains("<item>") }
                .map { it.substringAfter("<item>") }
            
            println("DEBUG: Found ${itemStrings.size} item strings")
            
            itemStrings.forEach { itemContent ->
                val itemData = mutableMapOf<String, String>()
                
                // Extract title
                val titleMatch = Regex("<title><!\\[CDATA\\[(.*?)\\]\\]></title>", RegexOption.DOT_MATCHES_ALL).find(itemContent)
                if (titleMatch != null) {
                    itemData["title"] = titleMatch.groupValues[1].trim()
                }
                
                // Extract description
                val descMatch = Regex("<description><!\\[CDATA\\[(.*?)\\]\\]></description>", RegexOption.DOT_MATCHES_ALL).find(itemContent)
                if (descMatch != null) {
                    itemData["description"] = descMatch.groupValues[1].trim()
                }
                
                // Extract link
                val linkMatch = Regex("<link>(.*?)</link>").find(itemContent)
                if (linkMatch != null) {
                    itemData["link"] = linkMatch.groupValues[1].trim()
                }
                
                // Extract pubDate
                val dateMatch = Regex("<pubDate>(.*?)</pubDate>").find(itemContent)
                if (dateMatch != null) {
                    itemData["pubDate"] = dateMatch.groupValues[1].trim()
                }
                
                // Extract creator (author)
                val creatorMatch = Regex("<dc:creator><!\\[CDATA\\[(.*?)\\]\\]></dc:creator>", RegexOption.DOT_MATCHES_ALL).find(itemContent)
                if (creatorMatch != null) {
                    itemData["author"] = creatorMatch.groupValues[1].trim()
                }
                
                println("DEBUG: Parsed item: $itemData")
                if (itemData.isNotEmpty()) {
                    items.add(createFeedItem(itemData))
                }
            }
            
            inputStream.close()
            println("Successfully parsed ${items.size} items from RSS feed using regex")
            items
                    } catch (e: Exception) {
                        println("=== RSS FEED ERROR ===")
                        println("Error fetching RSS feed: ${e.message}")
                        println("Error type: ${e.javaClass.simpleName}")
                        println("Error details: ${e.toString()}")
                        e.printStackTrace()
                        println("=== END RSS FEED ERROR ===")
                        emptyList()
                    }
                }
    }
    
    private fun createFeedItem(itemData: Map<String, String>): InstagramFeedItem {
        val description = itemData["description"] ?: ""
        val imageURL = extractImageURL(description)
        val cleanDescription = description.replace(Regex("<[^>]+>"), "")
        
        val formatter = SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US)
        val date = try {
            formatter.parse(itemData["pubDate"] ?: "") ?: Date()
        } catch (e: Exception) {
            Date()
        }
        
        return InstagramFeedItem(
            title = itemData["title"]?.trim() ?: "",
            author = itemData["author"] ?: "Instagram User",
            authorUsername = "",
            authorProfileImageURL = "",
            postImageURL = imageURL,
            postURL = itemData["link"]?.trim() ?: "",
            publishedDate = date,
            description = cleanDescription
        )
    }
    
    private fun extractImageURL(description: String): String {
        val imgTagRegex = Regex("<img src=\"([^\"]+)\"", RegexOption.IGNORE_CASE)
        val match = imgTagRegex.find(description)
        return match?.groupValues?.get(1) ?: ""
    }
    
    private data class FeedConfig(
        val title: String,
        val url: String,
        val username: String,
        val profileImageAsset: String,
        val profileURL: String
    )
} 