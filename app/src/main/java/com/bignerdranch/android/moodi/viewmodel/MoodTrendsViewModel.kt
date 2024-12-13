package com.bignerdranch.android.moodi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import com.bignerdranch.android.moodi.repository.MoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for the Trends screen.
 * Manages mood data for calendar visualization and trend analysis.
 */
class MoodTrendsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoodRepository

    // Add new LiveData for statistics
    private val _dailyMoodStats = MutableLiveData<Map<String, Float>>()
    val dailyMoodStats: LiveData<Map<String, Float>> = _dailyMoodStats

    private val _weeklyMoodStats = MutableLiveData<Map<String, Float>>()
    val weeklyMoodStats: LiveData<Map<String, Float>> = _weeklyMoodStats

    private val _monthlyMoodStats = MutableLiveData<Map<String, Float>>()
    val monthlyMoodStats: LiveData<Map<String, Float>> = _monthlyMoodStats

    // LiveData for mood entries
    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()


    init {
        val dao = AppDatabase.getDatabase(application).moodDao()
        repository = MoodRepository(dao)
    }

    /**
     * Loads mood entries from repository and updates LiveData
     */
    fun loadMoodEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val entries = repository.getAllMoods()
                _moodEntries.postValue(entries)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun calculatePrevailingMoods(entries: List<MoodEntry>): Map<Long, String> {
        return entries
            .groupBy { entry ->
                // Group by day (remove time component)
                Calendar.getInstance().apply {
                    timeInMillis = entry.timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            .mapValues { (_, dayEntries) ->
                // Find most frequent mood for each day
                dayEntries
                    .groupBy { it.mood }
                    .maxByOrNull { (_, moodEntries) -> moodEntries.size }
                    ?.key ?: ""
            }
    }

    fun calculateStatistics(entries: List<MoodEntry>) {
        viewModelScope.launch {
            // Calculate daily stats (today only)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dailyStats = calculateMoodPercentages(
                entries.filter { it.timestamp >= today }
            )
            _dailyMoodStats.value = dailyStats

            // Calculate weekly stats (last 7 days)
            val weekAgo = today - (7 * 24 * 60 * 60 * 1000)
            val weeklyStats = calculateMoodPercentages(
                entries.filter { it.timestamp >= weekAgo }
            )
            _weeklyMoodStats.value = weeklyStats

            // Calculate monthly stats (last 30 days)
            val monthAgo = today - (30L * 24 * 60 * 60 * 1000)
            val monthlyStats = calculateMoodPercentages(
                entries.filter { it.timestamp >= monthAgo }
            )
            _monthlyMoodStats.value = monthlyStats
        }
    }

    private fun calculateMoodPercentages(entries: List<MoodEntry>): Map<String, Float> {
        if (entries.isEmpty()) return emptyMap()

        val total = entries.size.toFloat()
        return entries
            .groupBy { it.mood }
            .mapValues { (_, moodEntries) ->
                (moodEntries.size / total) * 100
            }
    }
}
