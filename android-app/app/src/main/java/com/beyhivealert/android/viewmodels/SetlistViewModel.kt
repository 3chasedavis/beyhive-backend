package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Setlist
import kotlinx.coroutines.launch

class SetlistViewModel : ViewModel() {
    private val _setlists = mutableStateOf<List<Setlist>>(emptyList())
    val setlists: State<List<Setlist>> get() = _setlists

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    init {
        fetchSetlists()
    }

    fun fetchSetlists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val fetchedSetlists = ApiService.fetchSetlists()
                println("ViewModel fetched ${fetchedSetlists.size} setlists") // Debug log
                println("First setlist: ${fetchedSetlists.firstOrNull()}") // Debug log
                _setlists.value = fetchedSetlists
            } catch (e: Exception) {
                println("ViewModel error: ${e.message}") // Debug log
                _errorMessage.value = "Failed to fetch setlists: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

