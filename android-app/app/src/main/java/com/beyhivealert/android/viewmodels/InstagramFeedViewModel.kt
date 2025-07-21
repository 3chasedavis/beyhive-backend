package com.beyhivealert.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.InstagramFeedItem
import com.beyhivealert.android.data.InstagramFeedSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val sectionResults = mutableListOf<InstagramFeedSection>()
                
                feeds.forEach { feed ->
                    val items = fetchFeed(feed.url)
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
                
                _sections.value = sectionResults
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load feeds: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun fetchFeed(urlString: String): List<InstagramFeedItem> {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val inputStream = connection.getInputStream()
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()
            parser.setInput(inputStream, null)
            
            val items = mutableListOf<InstagramFeedItem>()
            var currentItem: MutableMap<String, String>? = null
            var currentElement = ""
            
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentElement = parser.name
                        if (currentElement == "item") {
                            currentItem = mutableMapOf()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        currentItem?.let { item ->
                            when (currentElement) {
                                "title" -> item["title"] = parser.text
                                "description" -> item["description"] = parser.text
                                "link" -> item["link"] = parser.text
                                "pubDate" -> item["pubDate"] = parser.text
                                "author" -> item["author"] = parser.text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item" && currentItem != null) {
                            items.add(createFeedItem(currentItem))
                            currentItem = null
                        }
                    }
                }
                eventType = parser.next()
            }
            
            inputStream.close()
            items
        } catch (e: Exception) {
            emptyList()
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