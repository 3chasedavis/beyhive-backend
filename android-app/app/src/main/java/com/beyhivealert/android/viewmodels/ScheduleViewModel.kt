package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Event
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val _events = mutableStateOf<List<Event>>(emptyList())
    val events: State<List<Event>> get() = _events

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedEvents = ApiService.fetchEvents()
                println("ViewModel fetched ${fetchedEvents.size} events") // Debug log
                _events.value = fetchedEvents
            } catch (e: Exception) {
                println("ViewModel error: ${e.message}") // Debug log
                _error.value = "Failed to fetch events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseEventDate(dateString: String): Calendar? {
        return try {
            val datePart = dateString.split("T")[0]
            val parts = datePart.split("-")
            if (parts.size == 3) {
                val calendar = Calendar.getInstance()
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                calendar
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getEventsForDate(date: Calendar): List<Event> {
        return _events.value.filter { event ->
            val eventDate = parseEventDate(event.date)
            eventDate != null && isSameDay(eventDate, date)
        }
    }

    fun hasEventOnDate(date: Calendar): Boolean {
        return getEventsForDate(date).isNotEmpty()
    }

    fun getUpcomingEvents(): List<Event> {
        return _events.value.sortedBy { parseEventDate(it.date) }
    }

    fun getPastEvents(): List<Event> {
        return _events.value.sortedByDescending { parseEventDate(it.date) }
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
} 