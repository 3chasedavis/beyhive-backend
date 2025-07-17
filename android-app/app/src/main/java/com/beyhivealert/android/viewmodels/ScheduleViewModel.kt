package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Event
import kotlinx.coroutines.launch
import java.time.LocalDate

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
                _events.value = ApiService.fetchEvents()
            } catch (e: Exception) {
                _error.value = "Failed to fetch events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseEventDate(dateString: String): LocalDate? {
        return try {
            val datePart = dateString.split("T")[0]
            LocalDate.parse(datePart)
        } catch (e: Exception) {
            null
        }
    }

    fun getEventsForDate(date: LocalDate): List<Event> {
        return _events.value.filter { event ->
            parseEventDate(event.date) == date
        }
    }

    fun hasEventOnDate(date: LocalDate): Boolean {
        return getEventsForDate(date).isNotEmpty()
    }

    fun getUpcomingEvents(): List<Event> {
        return _events.value.sortedBy { parseEventDate(it.date) }
    }

    fun getPastEvents(): List<Event> {
        return _events.value.sortedByDescending { parseEventDate(it.date) }
    }
} 