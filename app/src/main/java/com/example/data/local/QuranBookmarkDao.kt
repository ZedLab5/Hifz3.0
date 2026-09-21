package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranBookmarkDao {

    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Query("SELECT * FROM quran_bookmarks WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    fun getBookmarksBySurah(surahNumber: Int): Flow<List<QuranBookmarkEntity>>

    @Query("SELECT * FROM quran_bookmarks WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    fun getBookmark(surahNumber: Int, verseNumber: Int): Flow<QuranBookmarkEntity?>

    @Query("SELECT * FROM quran_bookmarks WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getBookmarkOnce(surahNumber: Int, verseNumber: Int): QuranBookmarkEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber)")
    fun isBookmarked(surahNumber: Int, verseNumber: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber)")
    suspend fun isBookmarkedOnce(surahNumber: Int, verseNumber: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: QuranBookmarkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<QuranBookmarkEntity>)

    @Update
    suspend fun updateBookmark(bookmark: QuranBookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: QuranBookmarkEntity)

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber")
    suspend fun deleteBookmarkByAyah(surahNumber: Int, verseNumber: Int)

    @Query("DELETE FROM quran_bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("UPDATE quran_bookmarks SET note = :note WHERE id = :id")
    suspend fun updateBookmarkNote(id: Long, note: String)

    @Query("UPDATE quran_bookmarks SET colorTag = :colorTag WHERE id = :id")
    suspend fun updateBookmarkColorTag(id: Long, colorTag: String)

    @Query("SELECT COUNT(*) FROM quran_bookmarks")
    fun getBookmarkCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quran_bookmarks")
    suspend fun getBookmarkCountOnce(): Int

    @Query("DELETE FROM quran_bookmarks")
    suspend fun clearAllBookmarks()
}
