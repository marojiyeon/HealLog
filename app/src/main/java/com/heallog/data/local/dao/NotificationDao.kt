package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heallog.data.local.entity.NotificationSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification_settings ORDER BY type ASC")
    fun getAllSettings(): Flow<List<NotificationSetting>>

    @Query("SELECT * FROM notification_settings WHERE isEnabled = 1")
    suspend fun getEnabledSettings(): List<NotificationSetting>

    @Query("SELECT * FROM notification_settings WHERE id = :id")
    suspend fun getSettingById(id: Long): NotificationSetting?

    @Query("SELECT * FROM notification_settings WHERE type = :type LIMIT 1")
    fun getSettingByType(type: String): Flow<NotificationSetting?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: NotificationSetting): Long

    @Update
    suspend fun update(setting: NotificationSetting)

    @Query("DELETE FROM notification_settings WHERE id = :id")
    suspend fun deleteById(id: Long)
}
