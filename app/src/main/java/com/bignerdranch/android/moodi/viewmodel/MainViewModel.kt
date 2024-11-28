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

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoodRepository

    private val _selectedMood = MutableLiveData<String?>()
    val selectedMood: LiveData<String?> = _selectedMood

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    init {
        val dao = AppDatabase.getDatabase(application).moodDao()
        repository = MoodRepository(dao)
    }

    fun selectMood(mood: String) {
        _selectedMood.value = mood
    }

    fun saveMood(mood: String, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val moodEntry = MoodEntry(
                    mood = mood,
                    timestamp = System.currentTimeMillis(),
                    note = note.ifEmpty { null }
                )
                repository.insertMood(moodEntry)
                _selectedMood.postValue(null)
                _saveSuccess.postValue(true)
            } catch (e: Exception) {
                _saveSuccess.postValue(false)
            }
        }
    }
}