package com.beyhivealert.android.data

data class InstagramFeedItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val authorUsername: String,
    val authorProfileImageURL: String,
    val postImageURL: String,
    val postURL: String,
    val publishedDate: java.util.Date,
    val description: String
)

data class InstagramFeedSection(
    val title: String,
    val username: String,
    val profileImageAsset: String,
    val profileURL: String,
    val items: List<InstagramFeedItem>
) 