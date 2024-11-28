package com.bignerdranch.android.moodi.repository

import com.bignerdranch.android.moodi.data.MoodDao
import com.bignerdranch.android.moodi.data.MoodEntry

//This repository centralizes all database operations. Instead of accessing the DAO directly from
// fragments/activities, we go through this repository.

class MoodRepository(private val moodDao: MoodDao) {
    suspend fun getAllMoods() = moodDao.getAllMoods()

    suspend fun insertMood(moodEntry: MoodEntry) = moodDao.insert(moodEntry)

    suspend fun getMoodCount(mood: String): Int = moodDao.getMoodCount(mood)

    suspend fun getMostFrequentMood(): String? = moodDao.getMostFrequentMood()
}