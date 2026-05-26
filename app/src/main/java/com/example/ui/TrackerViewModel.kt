package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLimit
import com.example.data.AppUsageInfo
import com.example.data.BatteryLog
import com.example.data.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TrackerTab {
    SCREEN_TIME,
    DATA_USAGE,
    BATTERY_TELEMETRY,
    SYSTEM_RESOURCES,
    ANALYTICS
}

class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TrackerRepository(application)

    // UI Tab State
    private val _activeTab = MutableStateFlow(TrackerTab.SCREEN_TIME)
    val activeTab: StateFlow<TrackerTab> = _activeTab.asStateFlow()

    // Usage lists and stats
    private val _appUsageStats = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    val appUsageStats: StateFlow<List<AppUsageInfo>> = _appUsageStats.asStateFlow()

    // Saved database limits observed reactively
    val savedLimits: StateFlow<List<AppLimit>> = repository.allLimitsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Battery History Logs observed reactively
    val batteryLogs: StateFlow<List<BatteryLog>> = repository.allBatteryLogsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Has permission
    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    // Dialog & overlay states
    private val _selectedAppForLimit = MutableStateFlow<AppUsageInfo?>(null)
    val selectedAppForLimit: StateFlow<AppUsageInfo?> = _selectedAppForLimit.asStateFlow()

    private val _lockedAppShowOverlay = MutableStateFlow<AppUsageInfo?>(null)
    val lockedAppShowOverlay: StateFlow<AppUsageInfo?> = _lockedAppShowOverlay.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.populateFakeInitialData()
            } catch (e: Exception) {
                Log.e("TrackerViewModel", "Error populating data", e)
            }
            refreshStats()
        }
    }

    fun selectTab(tab: TrackerTab) {
        _activeTab.value = tab
    }

    fun refreshStats() {
        val hasPerm = try { repository.hasUsageStatsPermission() } catch (e: Exception) { false }
        _isPermissionGranted.value = hasPerm
        
        // Fetch stats from repository (combining real queries and fallbacks/simulators)
        viewModelScope.launch {
            try {
                val stats = repository.getAppUsageStats()
                
                // Check limits against current times to auto-lock apps
                for (app in stats) {
                    if (app.isLimitEnabled && app.screenTimeMs >= app.limitMinutes * 60 * 1000L) {
                        if (!app.isLocked) {
                            repository.updateLockedStatus(app.packageName, true)
                        }
                    } else {
                        if (app.isLocked && app.limitMinutes > 0 && app.screenTimeMs < app.limitMinutes * 60 * 1000L) {
                            repository.updateLockedStatus(app.packageName, false)
                        }
                    }
                }

                // Sync updated locked configurations back and emit
                val updatedStats = repository.getAppUsageStats()
                _appUsageStats.value = updatedStats
            } catch (e: Exception) {
                Log.e("TrackerViewModel", "Error refreshing stats", e)
            }
        }
    }

    fun openSelectedAppLimitConfig(app: AppUsageInfo) {
        _selectedAppForLimit.value = app
    }

    fun closeSelectedAppLimitConfig() {
        _selectedAppForLimit.value = null
    }

    fun saveAppLimit(packageName: String, appName: String, limitMinutes: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.insertOrUpdateLimit(packageName, appName, limitMinutes, isEnabled)
            } catch (e: Exception) { Log.e("TrackerVM", "Error", e) }
            refreshStats()
            closeSelectedAppLimitConfig()
        }
    }

    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            try {
                repository.deleteLimitByPackage(packageName)
                // also ensure unlocking
                repository.updateLockedStatus(packageName, false)
            } catch (e: Exception) { Log.e("TrackerVM", "Error", e) }
            refreshStats()
            closeSelectedAppLimitConfig()
        }
    }

    fun checkPermissionGranted(): Boolean {
        val hasPerm = repository.hasUsageStatsPermission()
        _isPermissionGranted.value = hasPerm
        return hasPerm
    }

    fun getPermissionIntent() = repository.getUsagePermissionIntent()

    fun attemptOpenApp(app: AppUsageInfo) {
        // If app is locked, trigger the dedicated Access Blocked prompt
        if (app.isLocked) {
            _lockedAppShowOverlay.value = app
        } else {
            // Simulated launched successfully banner
            Log.d("TrackerViewModel", "Launched app simulation: ${app.packageName}")
        }
    }

    fun dismissLockOverlay() {
        _lockedAppShowOverlay.value = null
    }

    fun bypassLockForNow(packageName: String) {
        viewModelScope.launch {
            try {
                // Set limit flag or temporarily increment limit with bypass
                val limits = savedLimits.value
                val limit = limits.find { it.packageName == packageName }
                if (limit != null) {
                    // simple bypass: double the minutes limit or disable it
                    repository.insertOrUpdateLimit(packageName, limit.appName, limit.limitMinutes + 15, limit.isEnabled)
                    repository.updateLockedStatus(packageName, false)
                }
            } catch (e: Exception) { Log.e("TrackerVM", "Error", e) }
            _lockedAppShowOverlay.value = null
            refreshStats()
        }
    }

    fun getCurrentBatteryLevel() = try { repository.getCurrentBatteryLevel() } catch (e: Exception) { 75 }

    fun simulateBatteryLog(packageName: String, appName: String, startPct: Int, endPct: Int, durationMins: Int) {
        viewModelScope.launch {
            try {
                repository.insertBatteryLog(packageName, appName, startPct, endPct, durationMins * 60L)
            } catch (e: Exception) { Log.e("TrackerVM", "Error", e) }
            refreshStats()
        }
    }
}
