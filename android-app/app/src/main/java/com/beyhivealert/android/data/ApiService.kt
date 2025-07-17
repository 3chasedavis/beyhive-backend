package com.beyhivealert.android.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val createdAt: String = "",
    val time: String = "",
    val timezone: String = ""
)

@Serializable
data class EventsResponse(
    val events: List<Event> = emptyList(),
    val success: Boolean = false,
    val message: String = ""
)

@Serializable
data class Livestream(
    val title: String = "",
    val url: String = "",
    val platform: String = "",
    val startTime: String = ""
)

object ApiService {
    private const val BASE_URL = "https://beyhive-backend.onrender.com/api"

    fun fetchEvents(): List<Event> {
        val url = URL("$BASE_URL/events")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return try {
            val response = connection.inputStream.bufferedReader().readText()
            val eventsResponse = Json.decodeFromString<EventsResponse>(response)
            eventsResponse.events
        } catch (e: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }

    fun fetchLivestreams(): List<Livestream> {
        val url = URL("$BASE_URL/livestreams")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return try {
            val response = connection.inputStream.bufferedReader().readText()
            Json.decodeFromString(response)
        } catch (e: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
} 