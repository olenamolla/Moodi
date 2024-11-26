// app/src/main/java/com/bignerdranch/android/moodi/data/MoodEntry.kt
package com.bignerdranch.android.moodi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String,
    val timestamp: Long,
    val note: String?
)