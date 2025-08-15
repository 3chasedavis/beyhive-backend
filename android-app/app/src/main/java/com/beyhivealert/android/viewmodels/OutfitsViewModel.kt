package com.beyhivealert.android.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Outfit
import kotlinx.coroutines.launch

class OutfitsViewModel : ViewModel() {
    private val _outfits = mutableStateOf<List<Outfit>>(emptyList())
    val outfits: State<List<Outfit>> get() = _outfits

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    init {
        fetchOutfits()
    }

    fun fetchOutfits() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val fetchedOutfits = ApiService.fetchOutfits()
                _outfits.value = fetchedOutfits
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load outfits: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
