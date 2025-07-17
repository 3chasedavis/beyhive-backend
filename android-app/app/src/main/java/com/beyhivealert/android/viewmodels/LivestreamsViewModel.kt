package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Livestream
import kotlinx.coroutines.launch

class LivestreamsViewModel : ViewModel() {
    private val _livestreams = mutableStateOf<List<Livestream>>(emptyList())
    val livestreams: State<List<Livestream>> get() = _livestreams

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    init {
        fetchLivestreams()
    }

    fun fetchLivestreams() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _livestreams.value = ApiService.fetchLivestreams()
            } catch (e: Exception) {
                _error.value = "Failed to fetch livestreams: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 