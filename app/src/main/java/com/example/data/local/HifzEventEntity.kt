package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hifz_events",
    indices = [
        Index(value = ["surahNumber", "ayahNumber"]),
        Index(value = ["timestampUtcMs"])
    ]
)
data class HifzEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val type: String, // "MARKED", "UNMARKED", "MISSED", "REVIEWED"
    val mode: String, // "MEMORIZED", "PRACTICE", "DRILL"
    val timestampUtcMs: Long = System.currentTimeMillis()
)
