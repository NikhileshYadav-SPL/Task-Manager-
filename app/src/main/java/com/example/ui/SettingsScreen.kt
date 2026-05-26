package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun SettingsScreen(viewModel: TrackerViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "System Controls",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            
            // Battery Health
            SettingsCard(
                title = "Battery Lifespan Health",
                icon = Icons.Default.BatterySaver,
                iconTint = Color(0xFF10B981)
            ) {
                Text(
                    "Estimated Battery Health: 91%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Frequent overnight charging detected. Recommendation: Enable Adaptive Charging.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 0.91f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(0xFF10B981),
                    trackColor = Color(0xFF334155)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Cloud Sync
            SettingsCard(
                title = "Secure Cloud Sync",
                icon = Icons.Default.CloudSync,
                iconTint = Color(0xFF38BDF8)
            ) {
                Text(
                    "Last Backup: Today at 02:41 AM",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* MOCK: Firebase integration */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Force Telemetry Sync", color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Background Detector Warning
            SettingsCard(
                title = "Background Activity Logs",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFEF4444)
            ) {
                Text(
                    "Spotify ran silently for 2h 13m\nChrome initiated 48 hidden network requests",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { /* Mock Lock */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Instantly Restrict Anomalies")
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Usage Permissions
            SettingsCard(
                title = "System Usage Permissions",
                icon = Icons.Default.Security,
                iconTint = Color(0xFFF59E0B)
            ) {
                Text(
                    "To generate real-time metrics, Android requires global Usage State clearances.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = { context.startActivity(viewModel.getPermissionIntent()) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify Usage Permissions", color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // HUD Settings
            SettingsCard(
                title = "Floating Telemetry HUD",
                icon = Icons.Default.AspectRatio,
                iconTint = Color(0xFFA78BFA)
            ) {
                Text(
                    "Enable a futuristic live overlay for continuous system monitoring.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                val context = androidx.compose.ui.platform.LocalContext.current
                OutlinedButton(
                    onClick = { 
                        if (android.provider.Settings.canDrawOverlays(context)) {
                            context.startService(android.content.Intent(context, TelemetryWidgetService::class.java))
                        } else {
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                android.net.Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA78BFA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Launch Cyberpunk UI Overlay")
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
