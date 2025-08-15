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

@Serializable
data class MaintenanceResponse(
    val isMaintenanceMode: Boolean
)

@Serializable
data class CountdownResponse(
    val isCountdownEnabled: Boolean
)

@Serializable
data class Partner(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String? = null,
    val link: String = "",
    val createdAt: String = ""
)

@Serializable
data class PartnersResponse(
    val partners: List<Partner> = emptyList(),
    val success: Boolean = false,
    val message: String = ""
)

object ApiService {
    private const val BASE_URL = "https://beyhive-backend.onrender.com/api"

    fun fetchEvents(): List<Event> {
        val url = URL("$BASE_URL/events")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        
        return try {
            println("Attempting to fetch events from: $url") // Debug log
            val responseCode = connection.responseCode
            println("Events API Response Code: $responseCode") // Debug log
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Events API Response: $response") // Debug log
                val eventsResponse = Json.decodeFromString<EventsResponse>(response)
                println("Parsed events: ${eventsResponse.events}") // Debug log
                eventsResponse.events
            } else {
                println("Events API Error Response Code: $responseCode") // Debug log
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Events API Error Response: $errorStream") // Debug log
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching events: ${e.message}") // Debug log
            e.printStackTrace()
            emptyList()
        } finally {
            connection.disconnect()
        }
    }

    fun fetchLivestreams(): List<Livestream> {
        val url = URL("$BASE_URL/livestreams")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        
        return try {
            println("Attempting to fetch livestreams from: $url") // Debug log
            val responseCode = connection.responseCode
            println("Livestreams API Response Code: $responseCode") // Debug log
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Livestreams API Response: $response") // Debug log
                val livestreams = Json.decodeFromString<List<Livestream>>(response)
                println("Parsed livestreams: $livestreams") // Debug log
                livestreams
            } else {
                println("Livestreams API Error Response Code: $responseCode") // Debug log
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Livestreams API Error Response: $errorStream") // Debug log
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching livestreams: ${e.message}") // Debug log
            e.printStackTrace()
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
    

    
    fun fetchCountdownMode(): CountdownResponse {
        val url = URL("$BASE_URL/admin/countdown-mode")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return try {
            val response = connection.inputStream.bufferedReader().readText()
            Json.decodeFromString(response)
        } catch (e: Exception) {
            CountdownResponse(false)
        } finally {
            connection.disconnect()
        }
    }
    
    fun fetchOutfits(): List<Outfit> {
        val url = URL("$BASE_URL/outfits")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return try {
            val response = connection.inputStream.bufferedReader().readText()
            println("Outfits API Response: $response") // Debug log
            val outfitsResponse = Json.decodeFromString<OutfitsResponse>(response)
            println("Parsed outfits: ${outfitsResponse.outfits}") // Debug log
            outfitsResponse.outfits
        } catch (e: Exception) {
            println("Error fetching outfits: ${e.message}") // Debug log
            e.printStackTrace()
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
    
    fun fetchPartners(): List<Partner> {
        val url = URL("$BASE_URL/partners")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        
        return try {
            println("Attempting to fetch partners from: $url") // Debug log
            val responseCode = connection.responseCode
            println("Partners API Response Code: $responseCode") // Debug log
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Partners API Response: $response") // Debug log
                val partnersResponse = Json.decodeFromString<PartnersResponse>(response)
                println("Parsed partners: ${partnersResponse.partners}") // Debug log
                partnersResponse.partners
            } else {
                println("Partners API Error Response Code: $responseCode") // Debug log
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Partners API Error Response: $errorStream") // Debug log
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching partners: ${e.message}") // Debug log
            e.printStackTrace()
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
    
    fun fetchNotificationPreferences(token: String): Map<String, Boolean> {
        val url = URL("$BASE_URL/device-preferences/$token")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        
        return try {
            println("Attempting to fetch notification preferences from: $url")
            val responseCode = connection.responseCode
            println("Notification Preferences API Response Code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Notification Preferences API Response: $response")
                
                // For now, return default preferences until we implement proper JSON parsing
                // TODO: Implement proper JSON parsing for notification preferences
                mapOf(
                    "beyonceOnStage" to true,
                    "concertStart" to true,
                    "americaHasAProblem" to true,
                    "tyrant" to true,
                    "lastAct" to true,
                    "sixteenCarriages" to true,
                    "amen" to true
                )
            } else {
                println("Notification Preferences API Error Response Code: $responseCode")
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Notification Preferences API Error Response: $errorStream")
                emptyMap()
            }
        } catch (e: Exception) {
            println("Error fetching notification preferences: ${e.message}")
            e.printStackTrace()
            emptyMap()
        } finally {
            connection.disconnect()
        }
    }
    
    fun updateNotificationPreferences(token: String, preferences: Map<String, Boolean>): Boolean {
        val url = URL("$BASE_URL/register-device")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        
        return try {
            val body = mapOf(
                "deviceToken" to token,
                "preferences" to preferences,
                "platform" to "android"
            )
            
            val jsonBody = org.json.JSONObject(body).toString()
            println("Sending notification preferences update: $jsonBody")
            
            connection.outputStream.use { os ->
                os.write(jsonBody.toByteArray())
                os.flush()
            }
            
            val responseCode = connection.responseCode
            println("Update Notification Preferences API Response Code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Update Notification Preferences API Response: $response")
                true
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Update Notification Preferences API Error Response: $errorStream")
                false
            }
        } catch (e: Exception) {
            println("Error updating notification preferences: ${e.message}")
            e.printStackTrace()
            false
        } finally {
            connection.disconnect()
        }
    }
    
    fun fetchMaintenanceMode(): MaintenanceResponse {
        val url = URL("$BASE_URL/admin/maintenance-mode")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BeyhiveAlert-Android/1.0")
        
        return try {
            println("🔧 [DEBUG] Checking maintenance mode at ${System.currentTimeMillis()} ...")
            val responseCode = connection.responseCode
            println("Maintenance Mode API Response Code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Maintenance Mode API Response: $response")
                
                val jsonObject = org.json.JSONObject(response)
                MaintenanceResponse(
                    isMaintenanceMode = jsonObject.getBoolean("isMaintenanceMode")
                )
            } else {
                println("Maintenance Mode API Error Response Code: $responseCode")
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                println("Maintenance Mode API Error Response: $errorStream")
                MaintenanceResponse(isMaintenanceMode = false)
            }
        } catch (e: Exception) {
            println("❌ [DEBUG] Error checking maintenance mode: ${e.message}")
            e.printStackTrace()
            MaintenanceResponse(isMaintenanceMode = false)
        } finally {
            connection.disconnect()
        }
    }
} 