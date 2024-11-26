// app/src/main/java/com/bignerdranch/android/moodi/data/MoodDao.kt
package com.bignerdranch.android.moodi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    suspend fun getAllMoods(): List<MoodEntry>

    @Query("SELECT COUNT(*) FROM mood_entries WHERE mood = :mood")
    suspend fun getMoodCount(mood: String): Int

    @Query("SELECT mood FROM mood_entries GROUP BY mood ORDER BY COUNT(mood) DESC LIMIT 1")
    suspend fun getMostFrequentMood(): String?
}