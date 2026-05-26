package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUsageInfo
import kotlinx.coroutines.delay
import kotlin.random.Random

// ==============================================
// 5. SYSTEM RESOURCES TAB LAYOUT
// ==============================================
@Composable
fun SystemResourcesTabContent() {
    var cpuUsage by remember { mutableStateOf(42f) }
    var ramUsage by remember { mutableStateOf(58f) }
    var thermalLoad by remember { mutableStateOf(34f) }
    var networkLatency by remember { mutableStateOf(24) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            cpuUsage = (cpuUsage + Random.nextInt(-5, 5)).coerceIn(10f, 90f)
            ramUsage = (ramUsage + Random.nextInt(-2, 2)).coerceIn(40f, 85f)
            thermalLoad = (thermalLoad + Random.nextFloat() * 2 - 1).coerceIn(28f, 45f)
            networkLatency = (networkLatency + Random.nextInt(-5, 5)).coerceIn(15, 120)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "LIVE SYSTEM RESOURCE MONITOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFA78BFA),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CircularStatRing(title = "CPU", value = "${cpuUsage.toInt()}%", progress = cpuUsage / 100f, color = Color(0xFF38BDF8))
                        CircularStatRing(title = "RAM", value = "${ramUsage.toInt()}%", progress = ramUsage / 100f, color = Color(0xFFEC4899))
                        CircularStatRing(title = "Heat", value = "${thermalLoad.toInt()}°C", progress = (thermalLoad - 20) / 30f, color = Color(0xFFEF4444))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NetworkPing, contentDescription = "Internet Quality", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Internet Quality Monitor", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Network Latency", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("${networkLatency}ms Ping", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Packet Loss", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("0.0%", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrivacyTip, contentDescription = "Privacy Scanner", tint = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Privacy & Permission Scanner", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("3 Apps with dangerous background location privileges detected.", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* Action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Review Permissions")
                    }
                }
            }
        }
    }
}

@Composable
fun CircularStatRing(title: String, value: String, progress: Float, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFF334155),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
    }
}


// ==============================================
// 6. WEEKLY & MONTHLY ANALYTICS DASHBOARD
// ==============================================
@Composable
fun AnalyticsTabContent(stats: List<AppUsageInfo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PRODUCTIVITY SCORE ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFEC4899),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("78/100", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Focus Quality Index", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Box(modifier = Modifier.size(60.dp).background(Color(0xFF334155), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Trend", tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Top Distraction: YouTube (-12 pts)", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Category, contentDescription = "Category", tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("App Category Classification", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CategoryBarRow("Social", 0.6f, Color(0xFFEC4899), "4h 12m")
                    CategoryBarRow("Gaming", 0.2f, Color(0xFF8B5CF6), "1h 30m")
                    CategoryBarRow("Productivity", 0.15f, Color(0xFF10B981), "55m")
                    CategoryBarRow("Education", 0.05f, Color(0xFFF59E0B), "15m")
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoGraph, contentDescription = "Prediction", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI Behavioral Prediction", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• You will likely exceed the Instagram limit by 8:15 PM.\n• Battery may fully drain within 2 hours based on current YouTube thermal load.", color = Color.LightGray, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
fun CategoryBarRow(title: String, ratio: Float, color: Color, timeStr: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.White, fontSize = 12.sp)
            Text(timeStr, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF334155))) {
            Box(modifier = Modifier.fillMaxWidth(ratio).height(6.dp).clip(RoundedCornerShape(3.dp)).background(color))
        }
    }
}
