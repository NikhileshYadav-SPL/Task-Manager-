package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLimitDao {
    @Query("SELECT * FROM app_limits")
    fun getAllLimitsFlow(): Flow<List<AppLimit>>

    @Query("SELECT * FROM app_limits")
    suspend fun getAllLimits(): List<AppLimit>

    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitByPackage(packageName: String): AppLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLimit(limit: AppLimit)

    @Query("UPDATE app_limits SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateLockedStatus(packageName: String, isLocked: Boolean)

    @Delete
    suspend fun deleteLimit(limit: AppLimit)
}
