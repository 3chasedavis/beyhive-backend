package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
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
import com.beyhivealert.android.components.TopBar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.ScheduleViewModel
import com.beyhivealert.android.data.Event

@Composable
fun ScheduleScreen() {
    val viewModel: ScheduleViewModel = viewModel()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showUpcoming by remember { mutableStateOf(true) }
    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(
            title = "Schedule",
            onSettingsClick = { /* TODO: Show settings */ }
        )
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error ?: "Unknown error", color = Color.Red)
            }
            return
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar Section
            item {
                CalendarView(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    onMonthChange = { currentMonth = it },
                    onDateSelected = { selectedDate = it },
                    hasEvent = { viewModel.hasEventOnDate(it) }
                )
            }
            // Event Filters
            item {
                EventFilters(
                    showUpcoming = showUpcoming,
                    onFilterChanged = { showUpcoming = it }
                )
            }
            // Events List
            item {
                EventsList(
                    events = if (showUpcoming) viewModel.getUpcomingEvents() else viewModel.getPastEvents()
                )
            }
        }
    }
}

@Composable
fun CalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    hasEvent: (LocalDate) -> Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    onClick = { onMonthChange(currentMonth.minusMonths(1)) }
                ) {
                    Icon(Icons.Default.ArrowBack, "Previous Month")
                }
                
                Text(
                    text = currentMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { onMonthChange(currentMonth.plusMonths(1)) }
                ) {
                    Icon(Icons.Default.ArrowForward, "Next Month")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Days of Week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar Grid
            val firstDayOfMonth = currentMonth.atDay(1)
            val lastDayOfMonth = currentMonth.atEndOfMonth()
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            
            val daysInMonth = lastDayOfMonth.dayOfMonth
            val totalCells = firstDayOfWeek + daysInMonth
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp)
            ) {
                items(totalCells) { index ->
                    val dayOfMonth = index - firstDayOfWeek + 1
                    val date = if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                        currentMonth.atDay(dayOfMonth)
                    } else null
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .then(
                                if (date != null) {
                                    Modifier.clickable { onDateSelected(date) }
                                } else Modifier
                            )
                            .then(
                                if (date == selectedDate) {
                                    Modifier.border(2.dp, Color(0xFFE91E63))
                                } else Modifier
                            )
                            .then(
                                if (date != null && hasEvent(date)) {
                                    Modifier.background(Color(0xFFFFEB3B))
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Text(
                                text = dayOfMonth.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (date == selectedDate) FontWeight.Bold else FontWeight.Normal,
                                color = if (date == selectedDate) Color(0xFFE91E63) else Color.Black
                            )
                        }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterButton(
            text = "Upcoming Events",
            isSelected = showUpcoming,
            onClick = { onFilterChanged(true) }
        )
        FilterButton(
            text = "Past Events",
            isSelected = !showUpcoming,
            onClick = { onFilterChanged(false) }
        )
    }
    
    Text(
        text = "All events are shown in your local time.",
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFFEB3B) else Color(0xFFE0E0E0)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EventsList(events: List<com.beyhivealert.android.data.Event>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (events.isEmpty()) {
            Text(
                text = "No events",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            events.forEach { event ->
                EventItem(
                    title = event.title ?: "",
                    date = event.date.split("T")[0],
                    location = event.location ?: ""
                )
            }
        }
    }
}

@Composable
fun EventItem(title: String, date: String, location: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun hasEvent(date: LocalDate): Boolean {
    // Mock function - in real app, check against actual events
    val eventDates = listOf(4, 7, 10, 11, 13, 25)
    return eventDates.contains(date.dayOfMonth)
} 