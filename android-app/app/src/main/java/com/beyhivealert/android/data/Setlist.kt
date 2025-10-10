package com.beyhivealert.android.data

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val name: String,
    val order: Int,
    val notes: String? = null
)

@Serializable
data class Setlist(
    val id: String,
    val title: String,
    val songs: List<Song>,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SetlistsResponse(
    val setlists: List<Setlist> = emptyList(),
    val success: Boolean = false,
    val message: String? = null
)

