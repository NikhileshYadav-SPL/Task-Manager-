package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_limits")
data class AppLimit(
    @PrimaryKey val packageName: String,
    val appName: String,
    val limitMinutes: Int,
    val isEnabled: Boolean = true,
    val isLocked: Boolean = false
)

@Entity(tableName = "battery_logs")
data class BatteryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val startPct: Int,
    val endPct: Int,
    val consumedPct: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long
)
