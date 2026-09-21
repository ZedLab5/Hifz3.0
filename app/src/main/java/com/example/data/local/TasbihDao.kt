package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {

    @Query("SELECT * FROM tasbih_records")
    fun getAllTasbihRecords(): Flow<List<TasbihRecordEntity>>

    @Query("SELECT * FROM tasbih_records WHERE dhikrName = :name LIMIT 1")
    fun getTasbihRecordFlow(name: String): Flow<TasbihRecordEntity?>

    @Query("SELECT * FROM tasbih_records WHERE dhikrName = :name LIMIT 1")
    suspend fun getTasbihRecord(name: String): TasbihRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbihRecord(record: TasbihRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbihRecords(records: List<TasbihRecordEntity>)

    @Update
    suspend fun updateTasbihRecord(record: TasbihRecordEntity)

    @Delete
    suspend fun deleteTasbihRecord(record: TasbihRecordEntity)

    @Query("DELETE FROM tasbih_records WHERE dhikrName = :name")
    suspend fun deleteTasbihByName(name: String)

    @Query("SELECT SUM(totalAllTime) FROM tasbih_records")
    fun getTotalAllTimeDhikrCount(): Flow<Int?>

    @Query("SELECT SUM(totalAllTime) FROM tasbih_records")
    suspend fun getTotalAllTimeDhikrCountOnce(): Int?

    @Query("UPDATE tasbih_records SET currentCount = 0 WHERE dhikrName = :name")
    suspend fun resetTasbihCount(name: String)

    @Query("DELETE FROM tasbih_records")
    suspend fun clearAllTasbihRecords()
}
