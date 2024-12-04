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

class MoodHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoodRepository
    private val _moods = MutableLiveData<List<MoodEntry>>()
    val moods: LiveData<List<MoodEntry>> = _moods

    // LiveData for statistics
    private val _statistics = MutableLiveData<Pair<String?, Map<String, Int>>>()
    val statistics: LiveData<Pair<String?, Map<String, Int>>> = _statistics

    init {
        val dao = AppDatabase.getDatabase(application).moodDao()
        repository = MoodRepository(dao)
    }

    fun loadMoods() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entries = repository.getAllMoods()
                _moods.postValue(entries)
                calculateStatistics(entries)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun calculateStatistics(_: List<MoodEntry>) {
        val moods = listOf("Happy", "Sad", "Angry", "Excited", "Anxious", "Relaxed")
        val counts = mutableMapOf<String, Int>()

        for (mood in moods) {
            counts[mood] = repository.getMoodCount(mood)
        }
        val mostFrequent = repository.getMostFrequentMood()

        _statistics.postValue(Pair(mostFrequent, counts))
    }
}
