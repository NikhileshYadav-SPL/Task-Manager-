package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.getGeminiInsights
import kotlinx.coroutines.launch

@Composable
fun InsightsScreen(viewModel: TrackerViewModel) {
    val scrollState = rememberScrollState()
    
    // AI Report State
    var diagnosticSummary by remember { mutableStateOf("Ready to scan system telemetry.") }
    var isScanning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val appUsageStats by viewModel.appUsageStats.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AI Diagnostics",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Powered by Gemini Intelligence",
                    color = Color(0xFFA78BFA),
                    fontSize = 14.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Generate button
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFFEC4899))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Smart Daily Report Engine", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Run a full structural diagnostic on historical network patterns, battery strain, and active screen telemetry to generate actionable power optimizations.",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isScanning = true
                            diagnosticSummary = "Scanning system background tasks..."
                            // Simulate or real AI call
                            val usageDataSummary = appUsageStats.take(3).joinToString("; ") { 
                                "${it.appName}: ${it.batteryUsagePct}% drain, ${it.dataBytes / 1024 / 1024}MB" 
                            }
                            val prompt = "Analyze this system telemetry and give 3 short optimization bullets. Data: $usageDataSummary"
                            
                            coroutineScope.launch {
                                val result = getGeminiInsights(prompt)
                                diagnosticSummary = result
                                isScanning = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isScanning) "Scanning..." else "Generate System Report", color = Color.White)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Output Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = "Results", tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Diagnostic Blueprint", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (isScanning) {
                        CircularProgressIndicator(color = Color(0xFF6366F1), modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Text(
                            text = diagnosticSummary,
                            color = Color(0xFFE2E8F0),
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Notification Analyzer Mock
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Notifications", tint = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Notification Analyzer", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Top Interruption Sources", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    NotificationRow("Instagram", "147 alerts", 0xFFE1306C)
                    NotificationRow("WhatsApp", "38 alerts", 0xFF25D366)
                    NotificationRow("Snapchat", "12 alerts", 0xFFFFFC00)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // Digital Addiction Risk Score
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = "Addiction", tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Digital Addiction Detection", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Risk Level: HIGH", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Instagram was opened 73 times today.\n• Late-night scrolling increased by 42% this week.\n• Rapid app switching detected (Focus Stability Index: 22/100).", color = Color(0xFFE2E8F0), fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Smart Charging Protection
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BatteryChargingFull, contentDescription = "Charging", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Smart Charging Protection", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Advanced thermal defense active. Battery protection enabled at 80%. Adaptive overnight charging detected. No unsafe charger patterns found.", color = Color(0xFFE2E8F0), fontSize = 14.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Optimize Thermal Limits", color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Sleep Impact Monitor
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bedtime, contentDescription = "Sleep", tint = Color(0xFFA78BFA))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sleep Impact Monitor", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Overnight Activity: Detected 1h 45m of blue-light exposure between 11PM - 1AM, causing critical sleep disruption trends.", color = Color(0xFFE2E8F0), fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun NotificationRow(name: String, alerts: String, colorVal: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(colorVal), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, color = Color.White, fontSize = 15.sp)
        }
        Text(alerts, color = Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
