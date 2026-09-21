package com.example.data.quran

import android.content.Context
import com.example.data.model.Reciter
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object QuranAudioCacheManager {

    private fun getAudioDirectory(context: Context): File {
        val dir = File(context.filesDir, "quran_audio")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getSurahFile(context: Context, surahNumber: Int, reciterId: String): File {
        val surahFormatted = String.format(Locale.US, "%03d", surahNumber)
        val filename = "${reciterId}_surah_${surahFormatted}.mp3"
        return File(getAudioDirectory(context), filename)
    }

    fun isSurahAudioCached(context: Context, surahNumber: Int, reciterId: String): Boolean {
        val file = getSurahFile(context, surahNumber, reciterId)
        return file.exists() && file.length() > 5000
    }

    fun getSurahAudioSource(context: Context, surahNumber: Int, reciterId: String, remoteUrl: String): String {
        val file = getSurahFile(context, surahNumber, reciterId)
        return if (file.exists() && file.length() > 5000) {
            file.absolutePath
        } else {
            remoteUrl
        }
    }

    suspend fun downloadSurahAudio(
        context: Context,
        surahNumber: Int,
        reciterId: String,
        remoteUrl: String,
        onProgress: ((Float) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val targetFile = getSurahFile(context, surahNumber, reciterId)
        if (targetFile.exists() && targetFile.length() > 5000) {
            return@withContext true
        }

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val url = URL(remoteUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 25000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext false
            }

            val fileLength = connection.contentLength
            val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")
            inputStream = connection.inputStream
            outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(8192)
            var totalBytes: Long = 0
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytes += bytesRead
                if (fileLength > 0 && onProgress != null) {
                    val p = (totalBytes.toFloat() / fileLength.toFloat()).coerceIn(0f, 1f)
                    onProgress(p)
                }
            }
            outputStream.flush()

            if (tempFile.exists() && tempFile.length() > 5000) {
                if (targetFile.exists()) targetFile.delete()
                tempFile.renameTo(targetFile)
                true
            } else {
                tempFile.delete()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try { outputStream?.close() } catch (ignored: Exception) {}
            try { inputStream?.close() } catch (ignored: Exception) {}
            connection?.disconnect()
        }
    }

    fun getAyahFile(context: Context, surahNumber: Int, ayahNumber: Int, reciterId: String): File {
        val surahFormatted = String.format(Locale.US, "%03d", surahNumber)
        val ayahFormatted = String.format(Locale.US, "%03d", ayahNumber)
        val filename = "${reciterId}_${surahFormatted}_${ayahFormatted}.mp3"
        return File(getAudioDirectory(context), filename)
    }

    fun isAyahCached(context: Context, surahNumber: Int, ayahNumber: Int, reciterId: String): Boolean {
        val file = getAyahFile(context, surahNumber, ayahNumber, reciterId)
        return file.exists() && file.length() > 1024
    }

    fun isSurahCached(context: Context, surahNumber: Int, reciterId: String, totalVerses: Int): Boolean {
        if (totalVerses <= 0) return false
        // Check if all verses exist
        for (i in 1..totalVerses) {
            if (!isAyahCached(context, surahNumber, i, reciterId)) {
                return false
            }
        }
        return true
    }

    fun getCachedVersesCount(context: Context, surahNumber: Int, reciterId: String, totalVerses: Int): Int {
        var count = 0
        for (i in 1..totalVerses) {
            if (isAyahCached(context, surahNumber, i, reciterId)) {
                count++
            }
        }
        return count
    }

    fun getAudioSource(context: Context, surahNumber: Int, ayahNumber: Int, reciterId: String, remoteUrl: String): String {
        val file = getAyahFile(context, surahNumber, ayahNumber, reciterId)
        return if (file.exists() && file.length() > 1024) {
            file.absolutePath
        } else {
            remoteUrl
        }
    }

    suspend fun downloadAyah(
        context: Context,
        surahNumber: Int,
        ayahNumber: Int,
        reciterId: String,
        remoteUrl: String
    ): Boolean = withContext(Dispatchers.IO) {
        val targetFile = getAyahFile(context, surahNumber, ayahNumber, reciterId)
        if (targetFile.exists() && targetFile.length() > 1024) {
            return@withContext true
        }

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val url = URL(remoteUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext false
            }

            val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")
            inputStream = connection.inputStream
            outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()

            if (tempFile.exists() && tempFile.length() > 1024) {
                if (targetFile.exists()) targetFile.delete()
                tempFile.renameTo(targetFile)
                true
            } else {
                tempFile.delete()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try { outputStream?.close() } catch (ignored: Exception) {}
            try { inputStream?.close() } catch (ignored: Exception) {}
            connection?.disconnect()
        }
    }

    suspend fun deleteSurahCache(context: Context, surahNumber: Int, reciterId: String, totalVerses: Int) = withContext(Dispatchers.IO) {
        for (i in 1..totalVerses) {
            val file = getAyahFile(context, surahNumber, i, reciterId)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun getTotalCacheSizeBytes(context: Context): Long {
        val dir = getAudioDirectory(context)
        return dir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    fun clearAllAudioCache(context: Context) {
        val dir = getAudioDirectory(context)
        dir.listFiles()?.forEach { it.delete() }
    }
}
