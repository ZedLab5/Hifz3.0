package com.example.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PrayerWidgetUpdater {

    suspend fun update(context: Context) {
        try {
            PrayerWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            update(context)
        }
    }
}
