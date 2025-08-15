package com.beyhivealert.android.data

import kotlinx.serialization.Serializable

@Serializable
data class Outfit(
    val id: String,
    val name: String,
    val location: String,
    val imageName: String? = null, // Asset name (for backward compatibility)
    val imageUrl: String? = null, // Remote URL (new format)
    val isNew: Boolean,
    val section: String, // e.g. "Houston", "Washington", "Los Angeles", "Other"
    val description: String? = null
)

@Serializable
data class OutfitsResponse(
    val outfits: List<Outfit>
)
