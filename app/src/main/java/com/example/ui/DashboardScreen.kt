package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppUsageInfo
import com.example.data.BatteryLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: TrackerViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val appUsageStats by viewModel.appUsageStats.collectAsStateWithLifecycle()
    val savedLimits by viewModel.savedLimits.collectAsStateWithLifecycle()
    val batteryLogs by viewModel.batteryLogs.collectAsStateWithLifecycle()
    val isPermissionGranted by viewModel.isPermissionGranted.collectAsStateWithLifecycle()
    
    val selectedAppForLimit by viewModel.selectedAppForLimit.collectAsStateWithLifecycle()
    val lockedAppShowOverlay by viewModel.lockedAppShowOverlay.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // Trigger check on startup
    LaunchedEffect(Unit) {
        viewModel.refreshStats()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Sleek dark midnight slate canvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Custom Visual Header & App Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DEVICES TELEMETRY",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981), // Energetic green
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Power & Data Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshStats() },
                            modifier = Modifier
                                .background(Color(0xFF334155), CircleShape)
                                .testTag("refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Telemetry Data",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission warning banner if missing
                    if (!isPermissionGranted) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                            border = BorderStroke(1.dp, Color(0xFF991B1B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Usage Permission Required",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Grant access to compute real-time app timers, screen statistics, and power drain rates.",
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        context.startActivity(viewModel.getPermissionIntent())
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Grant", fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Navigation Custom Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TabButton(
                            title = "Power & Screen",
                            isSelected = activeTab == TrackerTab.SCREEN_TIME,
                            icon = Icons.Default.Lock,
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(TrackerTab.SCREEN_TIME) }
                        )
                        TabButton(
                            title = "Data Space",
                            isSelected = activeTab == TrackerTab.DATA_USAGE,
                            icon = Icons.Default.Share,
                            accentColor = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(TrackerTab.DATA_USAGE) }
                        )
                        TabButton(
                            title = "Power Log",
                            isSelected = activeTab == TrackerTab.BATTERY_TELEMETRY,
                            icon = Icons.Default.Favorite,
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(TrackerTab.BATTERY_TELEMETRY) }
                        )
                        TabButton(
                            title = "System",
                            isSelected = activeTab == TrackerTab.SYSTEM_RESOURCES,
                            icon = Icons.Default.Memory,
                            accentColor = Color(0xFFA78BFA),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(TrackerTab.SYSTEM_RESOURCES) }
                        )
                        TabButton(
                            title = "Analytics",
                            isSelected = activeTab == TrackerTab.ANALYTICS,
                            icon = Icons.Default.Analytics,
                            accentColor = Color(0xFFEC4899),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(TrackerTab.ANALYTICS) }
                        )
                    }
                }
            }

            // Main Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (activeTab) {
                    TrackerTab.SCREEN_TIME -> ScreenTimeTabContent(
                        stats = appUsageStats,
                        onAppClick = { viewModel.attemptOpenApp(it) },
                        onConfigureLimit = { viewModel.openSelectedAppLimitConfig(it) }
                    )
                    TrackerTab.DATA_USAGE -> DataUsageTabContent(
                        stats = appUsageStats
                    )
                    TrackerTab.BATTERY_TELEMETRY -> BatteryTelemetryTabContent(
                        stats = appUsageStats,
                        logs = batteryLogs,
                        currentBatteryVal = viewModel.getCurrentBatteryLevel(),
                        onLogManually = { pkg, name, start, end, dur ->
                            viewModel.simulateBatteryLog(pkg, name, start, end, dur)
                        },
                        onConfigureAppLimit = { pkg ->
                            val app = appUsageStats.find { it.packageName == pkg }
                            if (app != null) {
                                viewModel.openSelectedAppLimitConfig(app)
                                viewModel.selectTab(TrackerTab.SCREEN_TIME)
                            }
                        }
                    )
                    TrackerTab.SYSTEM_RESOURCES -> SystemResourcesTabContent()
                    TrackerTab.ANALYTICS -> AnalyticsTabContent(stats = appUsageStats)
                }
            }
        }

        // 1. Edit App Limit Config Dialog
        selectedAppForLimit?.let { app ->
            ConfigureLimitDialog(
                app = app,
                onDismiss = { viewModel.closeSelectedAppLimitConfig() },
                onSave = { limitMin, enabled ->
                    viewModel.saveAppLimit(app.packageName, app.appName, limitMin, enabled)
                },
                onDelete = {
                    viewModel.removeAppLimit(app.packageName)
                }
            )
        }

        // 2. Lock Overlaid Preventative Shield (Strict Usage Blocking)
        lockedAppShowOverlay?.let { app ->
            PreventativeLockShield(
                app = app,
                onDismiss = { viewModel.dismissLockOverlay() },
                onBypass = { viewModel.bypassLockForNow(app.packageName) }
            )
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color(0xFF1E293B) else Color.Transparent
    val tint = if (isSelected) accentColor else Color(0xFF94A3B8)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==============================================
// 1. SCREEN TIME TAB LAYOUT
// ==============================================
@Composable
fun ScreenTimeTabContent(
    stats: List<AppUsageInfo>,
    onAppClick: (AppUsageInfo) -> Unit,
    onConfigureLimit: (AppUsageInfo) -> Unit
) {
    if (stats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
        return
    }

    val totalTimeMs = stats.sumOf { it.screenTimeMs }
    val totalTimeStr = formatScreenTime(totalTimeMs)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High Contrast Metrics Gauge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL DAILY FOREGROUND ACTIVE TIME",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalTimeStr,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Simple progress gauge representing app limit status over arbitrary 4h average quota
                    val averageQuota = 4 * 60 * 60 * 1000L
                    val completionPct = (totalTimeMs.toFloat() / averageQuota).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { completionPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quota Status: ${(completionPct * 100).toInt()}% of daily average limit used.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App Screen Timers & Lockouts",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(
                    text = "${stats.size} Apps tracked",
                    color = Color(0xFF10B981),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(stats) { app ->
            AppTimeCard(
                app = app,
                onAppClick = { onAppClick(app) },
                onConfigureLimit = { onConfigureLimit(app) }
            )
        }
    }
}

@Composable
fun AppTimeCard(
    app: AppUsageInfo,
    onAppClick: () -> Unit,
    onConfigureLimit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_time_card_${app.packageName}")
            .clickable { onAppClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (app.isLocked) Color(0xFF2D1616) else Color(0xFF1E293B)
        ),
        border = BorderStroke(
            1.dp, 
            if (app.isLocked) Color(0xFF991B1B) else Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Beautiful letter avatars with bright backgrounds
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = getRandomAppColor(app.packageName),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.appName.firstOrNull()?.toString()?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = app.appName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = app.packageName,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatScreenTime(app.screenTimeMs),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    if (app.isLimitEnabled) {
                        Text(
                            text = "Limit: ${app.limitMinutes}m",
                            fontSize = 11.sp,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "No limit",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge block
                if (app.isLocked) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF7F1D1D), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Indicator",
                            tint = Color(0xFFFECAA6),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACCESS LOCKED",
                            color = Color(0xFFFECAA6),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (app.isLimitEnabled) {
                    val remainingMs = (app.limitMinutes * 60 * 1000L) - app.screenTimeMs
                    val remainingMin = (remainingMs / (60 * 1000L)).coerceAtLeast(0)
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF022C22), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Active Timer Indicator",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${remainingMin}m REMAINING",
                            color = Color(0xFF34D399),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(1.dp))
                }

                // Interactive configuration option
                IconButton(
                    onClick = onConfigureLimit,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF334155), CircleShape)
                        .testTag("configs_button_${app.packageName}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configure Lock limit setting",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==============================================
// 2. DATA USAGE TAB LAYOUT
// ==============================================
@Composable
fun DataUsageTabContent(
    stats: List<AppUsageInfo>
) {
    if (stats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF38BDF8))
        }
        return
    }

    val totalBytes = stats.sumOf { it.dataBytes }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High contrast summary metric card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL COMPUMETRIC NETWORK CONSUMPTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatBytes(totalBytes),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8) // Electric cyan blue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aggregated data queries captured since device startup.",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        item {
            Text(
                text = "Granular Network Consumption",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Subordinate app list sorted by bytes consumed descending
        val sortedApps = stats.sortedByDescending { it.dataBytes }
        val maxAppBytes = sortedApps.firstOrNull()?.dataBytes?.coerceAtLeast(1L) ?: 1L

        items(sortedApps) { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(getRandomAppColor(app.packageName), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app.appName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(app.appName, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(
                                    text = app.packageName,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        
                        Text(
                            text = formatBytes(app.dataBytes),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Relative scale bar
                    val relativeRatio = app.dataBytes.toFloat() / maxAppBytes
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF334155))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(relativeRatio)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF38BDF8))
                        )
                    }
                }
            }
        }
    }
}

// ==============================================
// 3. BATTERY TELEMETRY TAB LAYOUT
// ==============================================
@Composable
fun BatteryTelemetryTabContent(
    stats: List<AppUsageInfo>,
    logs: List<BatteryLog>,
    currentBatteryVal: Int,
    onLogManually: (String, String, Int, Int, Int) -> Unit,
    onConfigureAppLimit: (String) -> Unit
) {
    var showBatterySimDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        
        // Circular Wave Ring Indicator Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE PHONE BATTERY POWER STATE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(4.dp, Color(0xFFF59E0B), CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$currentBatteryVal%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Remaining",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Discharging. Total system drain since full chargingcycle estimated.",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // HEAVY POWER DRAINING CRITICAL WARNING BANNER (Required by user!)
        // "finally give me the at last give me the warning symbol to the app which uses more battery consumption for those identified as the highest power-draining sources."
        val maxDrainApp = stats.find { it.isHighestPowerDrain }
        maxDrainApp?.let { app ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Excessive Drain Alarm Warning icon",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CRITICAL POWER DRAIN LIMIT WARNING",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "${app.appName} is identified as the highest power-draining source, consuming an estimated ${String.format("%.1f", app.batteryUsagePct)}% of battery total capacity. Restricting this package via timer helps prolong device lifespan.",
                            fontSize = 12.sp,
                            color = Color(0xFFFECACA),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onConfigureAppLimit(app.packageName) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Set Timer icon",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Limit ${app.appName}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Battery Sessions Timeline section ("went to win this much percentage is completed")
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Battery Drainage Session Logs",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Button(
                    onClick = { showBatterySimDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Log", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Log", fontSize = 11.sp)
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No recorded battery sessions yet. Use 'Test Log' above to simulate a session.", color = Color(0xFF64748B), fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        // List of Battery Logs containing went from X% to Y%
        items(logs) { log ->
            val isHeavy = stats.find { it.packageName == log.packageName }?.isHighestPowerDrain == true
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (isHeavy) Color(0xFFEF4444) else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(getRandomAppColor(log.packageName), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = log.appName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(log.appName, fontWeight = FontWeight.Bold, color = Color.White)
                                if (isHeavy) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Power warning",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${log.startPct}% ➔ ${log.endPct}%",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isHeavy) Color(0xFFEF4444) else Color(0xFFF59E0B),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Drained ${log.consumedPct}% in ${log.durationSeconds / 60}m",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }

    // Interactive simulator dialog to log current session
    if (showBatterySimDialog) {
        BatterySimulateDialog(
            apps = stats,
            onDismiss = { showBatterySimDialog = false },
            onLogSession = { pkg, name, start, end, dur ->
                onLogManually(pkg, name, start, end, dur)
                showBatterySimDialog = false
            }
        )
    }
}

// ==============================================
// 4. PREVENTATIVE LOCKED OVERLAY SHIELD
// ==============================================
@Composable
fun PreventativeLockShield(
    app: AppUsageInfo,
    onDismiss: () -> Unit,
    onBypass: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0E0E)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock Shield Active Flag icon",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACCESS RESTRICTED",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFEF4444),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The screen timer limit of ${app.limitMinutes}m configured for ${app.appName} has been reached for today.",
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFECACA),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFF7F1D1D), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text("Stay Locked", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onBypass,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Bypass (+15m)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==============================================
// 5. EDIT APP LIMIT CONFIGURE DIALOG
// ==============================================
@Composable
fun ConfigureLimitDialog(
    app: AppUsageInfo,
    onDismiss: () -> Unit,
    onSave: (Int, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var isEnabled by remember { mutableStateOf(app.isLimitEnabled) }
    var limitInput by remember { mutableStateOf(app.limitMinutes.coerceAtLeast(15).toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Limit for ${app.appName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close description", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enabled Toggle option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = "Timer icon", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Lock Limit Stat", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Activate lockout shield when limit expires", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input timer value
                if (isEnabled) {
                    Text("Daily Allowed Screen Time (Minutes)", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Buttons Save & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (app.isLimitEnabled) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .background(Color(0xFF3F1F1F), RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete configuration", tint = Color(0xFFF87171))
                        }
                    }

                    Button(
                        onClick = {
                            val mins = limitInput.toIntOrNull() ?: 30
                            onSave(mins, isEnabled)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Save Limit Config", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ==============================================
// 6. SIMULATE BATTERY SESSIONS DIALOG
// ==============================================
@Composable
fun BatterySimulateDialog(
    apps: List<AppUsageInfo>,
    onDismiss: () -> Unit,
    onLogSession: (String, String, Int, Int, Int) -> Unit
) {
    var selectedAppPkg by remember { mutableStateOf(apps.firstOrNull()?.packageName ?: "") }
    var startPercent by remember { mutableStateOf("95") }
    var endPercent by remember { mutableStateOf("91") }
    var durationMins by remember { mutableStateOf("20") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Ammeter Battery Log test",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Track custom app discharge periods (from level X% to Y%) to test overall warning signals.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // App selector
                Text("App Package", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        apps.forEach { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedAppPkg = app.packageName }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAppPkg == app.packageName,
                                    onClick = { selectedAppPkg = app.packageName },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF59E0B))
                                )
                                Text(app.appName, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start %", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        OutlinedTextField(
                            value = startPercent,
                            onValueChange = { startPercent = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End %", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        OutlinedTextField(
                            value = endPercent,
                            onValueChange = { endPercent = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mins used", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        OutlinedTextField(
                            value = durationMins,
                            onValueChange = { durationMins = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val app = apps.find { it.packageName == selectedAppPkg }
                            val startInt = startPercent.toIntOrNull() ?: 95
                            val endInt = endPercent.toIntOrNull() ?: 90
                            val durInt = durationMins.toIntOrNull() ?: 15
                            if (app != null && startInt > endInt) {
                                onLogSession(app.packageName, app.appName, startInt, endInt, durInt)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                    ) {
                        Text("Inject Session Log", color = Color.White)
                    }
                }
            }
        }
    }
}

// Helper utilities for aesthetic visual representation
fun formatScreenTime(timeMs: Long): String {
    val hrs = timeMs / (3600 * 1000)
    val mins = (timeMs % (3600 * 1000)) / (60 * 1000)
    val secs = (timeMs % (60 * 1000)) / 1000
    
    return when {
        hrs > 0 -> "${hrs}h ${mins}m"
        mins > 0 -> "${mins}m"
        else -> "${secs}s"
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024f
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024f
    if (mb < 1024) return String.format("%.1f MB", mb)
    val gb = mb / 1024f
    return String.format("%.2f GB", gb)
}

fun getRandomAppColor(packageName: String): Color {
    val colors = listOf(
        Color(0xFFEF4444), // red
        Color(0xFF3B82F6), // blue
        Color(0xFF10B981), // green
        Color(0xFFF59E0B), // amber
        Color(0xFF8B5CF6), // purple
        Color(0xFFEC4899), // pink
        Color(0xFF06B6D4)  // cyan
    )
    val index = packageName.hashCode().coerceAtLeast(0) % colors.size
    return colors[index]
}
