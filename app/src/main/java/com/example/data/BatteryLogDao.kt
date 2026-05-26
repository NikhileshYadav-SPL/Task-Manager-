package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryLogDao {
    @Query("SELECT * FROM battery_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<BatteryLog>>

    @Query("SELECT * FROM battery_logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<BatteryLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BatteryLog)

    @Query("DELETE FROM battery_logs")
    suspend fun clearLogs()
}
