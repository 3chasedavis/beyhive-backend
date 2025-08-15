package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.ScheduleViewModel
import com.beyhivealert.android.data.Event
import java.util.*

@Composable
fun ScheduleScreen() {
    val viewModel: ScheduleViewModel = viewModel()
    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showUpcoming by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Schedule",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Calendar Section
        CalendarView(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onMonthChange = { currentMonth = it },
            onDateSelected = { selectedDate = it },
            hasEvent = { viewModel.hasEventOnDate(it) },
            showUpcoming = showUpcoming
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Event Filters
        EventFilters(
            showUpcoming = showUpcoming,
            onFilterChanged = { showUpcoming = it }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Information Text
        Text(
            text = "All events are shown in your local time.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error loading events:",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = error ?: "Unknown error",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.fetchEvents() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        } else if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events scheduled",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Events List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    currentMonth: Calendar,
    selectedDate: Calendar,
    onMonthChange: (Calendar) -> Unit,
    onDateSelected: (Calendar) -> Unit,
    hasEvent: (Calendar) -> Boolean,
    showUpcoming: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // White background like iOS
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Month Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, -1)
                        onMonthChange(newMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, "Previous Month")
                }
                
                Text(
                    text = "${getMonthName(currentMonth.get(Calendar.MONTH))} ${currentMonth.get(Calendar.YEAR)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { 
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, 1)
                        onMonthChange(newMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, "Next Month")
                }
            }
            
            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Day headers
                items(7) { dayOfWeek ->
                    Text(
                        text = getDayName(dayOfWeek),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray
                    )
                }
                
                // Calendar days
                val firstDayOfMonth = getFirstDayOfMonth(currentMonth)
                val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                
                // Empty cells for days before the first day of the month
                items(firstDayOfMonth) {
                    Box(modifier = Modifier.size(32.dp))
                }
                
                // Days of the month
                items(daysInMonth) { day ->
                    val dayCalendar = Calendar.getInstance()
                    dayCalendar.set(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH), day + 1)
                    
                    val isSelected = isSameDay(dayCalendar, selectedDate)
                    val isCurrentDate = isSameDay(dayCalendar, Calendar.getInstance())
                    val hasEventOnDay = hasEvent(dayCalendar)
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Color.Transparent
                                    hasEventOnDay && showUpcoming -> Color(0xFFFFF8DC) // Light yellow for upcoming events
                                    hasEventOnDay && !showUpcoming -> Color.LightGray // Gray for past events
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isCurrentDate) 2.dp else 0.dp,
                                color = if (isCurrentDate) Color.Red else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onDateSelected(dayCalendar) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${day + 1}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventFilters(
    showUpcoming: Boolean,
    onFilterChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between buttons
    ) {
        // Upcoming Events Button
        Button(
            onClick = { onFilterChanged(true) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showUpcoming) Color(0xFFFFF8DC) else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp) // Fully rounded corners
        ) {
            Text(
                text = "Upcoming Events",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (showUpcoming) Color.Black else Color.Gray
            )
        }
        
        // Past Events Button
        Button(
            onClick = { onFilterChanged(false) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!showUpcoming) Color(0xFFFFF8DC) else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp) // Fully rounded corners
        ) {
            Text(
                text = "Past Events",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (!showUpcoming) Color.Black else Color.Gray
            )
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            if (event.description.isNotEmpty()) {
                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.date} ${event.time}",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (event.location.isNotEmpty()) {
                Text(
                    text = event.location,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Helper functions for Calendar operations
private fun getMonthName(month: Int): String {
    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return monthNames[month]
}

private fun getDayName(dayOfWeek: Int): String {
    val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    return dayNames[dayOfWeek]
}

private fun getFirstDayOfMonth(calendar: Calendar): Int {
    val temp = calendar.clone() as Calendar
    temp.set(Calendar.DAY_OF_MONTH, 1)
    return temp.get(Calendar.DAY_OF_WEEK) - 1
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
           cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
} 