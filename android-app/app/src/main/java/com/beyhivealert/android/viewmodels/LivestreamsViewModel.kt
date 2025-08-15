package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Livestream
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Date

data class CountdownResponse(
    val isCountdownEnabled: Boolean
)

data class EventsResponse(
    val events: List<Event>
)

data class Event(
    val id: String,
    val date: Date,
    val localStartDate: Date? = null
)

class LivestreamsViewModel : ViewModel() {
    private val _livestreams = mutableStateOf<List<Livestream>>(emptyList())
    val livestreams: State<List<Livestream>> get() = _livestreams

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    private val _isCountdownEnabled = mutableStateOf(false)
    val isCountdownEnabled: State<Boolean> get() = _isCountdownEnabled

    private val _currentTime = mutableStateOf(System.currentTimeMillis())
    val currentTime: State<Long> get() = _currentTime

    private val _nextShowDate = mutableStateOf<Long?>(null)
    val nextShowDate: State<Long?> get() = _nextShowDate



    init {
        fetchLivestreams()
        fetchCountdownMode()
        fetchNextShowDate()
        
        // Start timer for countdown updates
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis()
                delay(1000) // Update every second
            }
        }
        
        // Check countdown mode periodically
        viewModelScope.launch {
            while (true) {
                delay(30000) // Check every 30 seconds
                fetchCountdownMode()
            }
        }
    }

    fun fetchLivestreams() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedLivestreams = ApiService.fetchLivestreams()
                println("ViewModel fetched ${fetchedLivestreams.size} livestreams") // Debug log
                _livestreams.value = fetchedLivestreams
            } catch (e: Exception) {
                println("ViewModel error: ${e.message}") // Debug log
                _error.value = "Failed to fetch livestreams: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun fetchCountdownMode() {
        viewModelScope.launch {
            try {
                val response = ApiService.fetchCountdownMode()
                _isCountdownEnabled.value = response.isCountdownEnabled
                println("✅ Countdown mode: ${response.isCountdownEnabled}")
            } catch (e: Exception) {
                println("❌ Error fetching countdown mode: ${e.message}")
            }
        }
    }
    
    fun fetchNextShowDate() {
        viewModelScope.launch {
            try {
                val events = ApiService.fetchEvents()
                // Find the next upcoming event
                val now = System.currentTimeMillis()
                val upcomingEvents = events.filter { event ->
                    // Parse the date string to timestamp
                    try {
                        val eventTime = event.date.toLongOrNull() ?: 0L
                        eventTime > now
                    } catch (e: Exception) {
                        false
                    }
                }
                _nextShowDate.value = upcomingEvents.firstOrNull()?.let { event ->
                    event.date.toLongOrNull() ?: 0L
                } ?: 0L
                println("✅ Next show date: ${_nextShowDate.value}")
            } catch (e: Exception) {
                println("❌ Error fetching events: ${e.message}")
            }
        }
    }
    
    val countdownString: String
        get() {
            val nextShow = _nextShowDate.value ?: return ""
            val timeInterval = nextShow - _currentTime.value
            
            if (timeInterval <= 0) return ""
            
            val days = (timeInterval / (24 * 60 * 60 * 1000)).toInt()
            val hours = ((timeInterval % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)).toInt()
            val minutes = ((timeInterval % (60 * 60 * 1000)) / (60 * 1000)).toInt()
            val seconds = ((timeInterval % (60 * 1000)) / 1000).toInt()
            
            return when {
                days > 0 -> "${days}d ${hours}h ${minutes}m ${seconds}s"
                hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
                minutes > 0 -> "${minutes}m ${seconds}s"
                else -> "${seconds}s"
            }
        }
    
} 