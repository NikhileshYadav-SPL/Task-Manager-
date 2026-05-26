package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun FocusModeScreen(viewModel: TrackerViewModel) {
    var isFocusActive by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(25 * 60) } // 25 min default
    var sessionCount by remember { mutableStateOf(0) }
    
    // Simple timer loop
    LaunchedEffect(isFocusActive) {
        while (isFocusActive && timeRemaining > 0) {
            delay(1000)
            timeRemaining -= 1
            if (timeRemaining == 0) {
                isFocusActive = false
                sessionCount++
                timeRemaining = 25 * 60 // reset
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Focus Mode",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Eliminate distractions and track your study sessions.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            // Pomodoro Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                val totalTime = 25 * 60
                val progress = timeRemaining.toFloat() / totalTime
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFF6366F1),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = timeRemaining / 60
                    val seconds = timeRemaining % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = if (isFocusActive) "Session Active" else "Ready to Focus",
                        color = Color(0xFF94A3B8),
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Controls
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { 
                        if (isFocusActive) {
                            // Emergency Stop
                            isFocusActive = false
                            timeRemaining = 25 * 60
                        } else {
                            isFocusActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isFocusActive) Color(0xFFEF4444) else Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = if (isFocusActive) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "Start/Stop",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFocusActive) "Emergency Unlock" else "Start Focus Session",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FocusStatCard(title = "Sessions Today", value = "$sessionCount", icon = Icons.Default.CheckCircle)
                FocusStatCard(title = "Current Streak", value = "${sessionCount * 25}min", icon = Icons.Default.Star)
            }
        }
    }
}

@Composable
fun FocusStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(150.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}
