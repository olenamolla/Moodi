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
import com.bignerdranch.android.moodi.utils.OpenAIAssistant
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _aiInsight = MutableLiveData<String>()
    val aiInsight: LiveData<String> = _aiInsight
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

                // Generate AI Insight
                val insight = try {
                    OpenAIAssistant.generateMoodInsight(mood, note)
                } catch (e: Exception) {
                    "Unable to generate insight. Error: ${e.localizedMessage}"
                }
                _aiInsight.postValue(insight)
            } catch (e: Exception) {
                _saveSuccess.postValue(false)
                _aiInsight.postValue("Error saving mood: ${e.localizedMessage}")
            }
        }
    }
}
