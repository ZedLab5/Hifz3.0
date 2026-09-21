package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {

    // Daily Prayer Tracking
    @Query("SELECT * FROM prayer_records WHERE date = :date")
    fun getPrayerRecordsForDate(date: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date = :date")
    suspend fun getPrayerRecordsForDateOnce(date: String): List<PrayerRecordEntity>

    @Query("SELECT * FROM prayer_records")
    fun getAllPrayerRecords(): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getPrayerRecordsInRange(startDate: String, endDate: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date = :date AND prayerName = :prayerName LIMIT 1")
    suspend fun getPrayerRecord(date: String, prayerName: String): PrayerRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayer(record: PrayerRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerRecords(records: List<PrayerRecordEntity>)

    @Query("DELETE FROM prayer_records WHERE date = :date AND prayerName = :prayerName")
    suspend fun deletePrayerRecord(date: String, prayerName: String)

    @Query("DELETE FROM prayer_records WHERE date = :date")
    suspend fun clearPrayerRecordsForDate(date: String)

    @Query("SELECT COUNT(*) FROM prayer_records WHERE date = :date AND isCompleted = 1")
    fun getCompletedPrayerCountForDate(date: String): Flow<Int>

    // Qada (Missed / Made-up Prayers) Tracking
    @Query("SELECT * FROM qada_records")
    fun getAllQadaRecords(): Flow<List<QadaRecordEntity>>

    @Query("SELECT * FROM qada_records WHERE prayerType = :prayerType LIMIT 1")
    suspend fun getQadaRecord(prayerType: String): QadaRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQadaRecord(record: QadaRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQadaRecords(records: List<QadaRecordEntity>)

    @Query("SELECT SUM(totalMissed) FROM qada_records")
    fun getTotalMissedQadaCount(): Flow<Int?>

    @Query("SELECT SUM(completedMadeUp) FROM qada_records")
    fun getTotalMadeUpQadaCount(): Flow<Int?>

    @Query("DELETE FROM prayer_records")
    suspend fun clearAllPrayerRecords()

    @Query("DELETE FROM qada_records")
    suspend fun clearAllQadaRecords()
}
