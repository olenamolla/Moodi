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

/**
 * ViewModel for the Trends screen.
 * Manages mood data for calendar visualization and trend analysis.
 */
class MoodTrendsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoodRepository

    // LiveData for mood entries
    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
}