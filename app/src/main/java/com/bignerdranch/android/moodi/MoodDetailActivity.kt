// app/src/main/java/com/bignerdranch/android/moodi/MoodDetailActivity.kt
package com.bignerdranch.android.moodi

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope


class MoodDetailActivity : AppCompatActivity() {

    private lateinit var ivMoodIconDetail: ImageView
    private lateinit var tvMoodTypeDetail: TextView
    private lateinit var tvTimestampDetail: TextView
    private lateinit var tvNoteDetail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_detail)

        ivMoodIconDetail = findViewById(R.id.ivMoodIconDetail)
        tvMoodTypeDetail = findViewById(R.id.tvMoodTypeDetail)
        tvTimestampDetail = findViewById(R.id.tvTimestampDetail)
        tvNoteDetail = findViewById(R.id.tvNoteDetail)

        val moodId = intent.getIntExtra("mood_id", -1)
        if (moodId != -1) {
            loadMoodDetails(moodId)
        } else {
            finish() // Close activity if invalid ID
        }
    }

    private fun loadMoodDetails(moodId: Int) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val moodEntry = withContext(Dispatchers.IO) {
                db.moodDao().getAllMoods().find { it.id == moodId }
            }
            moodEntry?.let {
                bindingMoodDetails(it)
            }
        }
    }

    private fun bindingMoodDetails(moodEntry: MoodEntry) {
        tvMoodTypeDetail.text = moodEntry.mood
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = Date(moodEntry.timestamp)
        tvTimestampDetail.text = sdf.format(date)
        tvNoteDetail.text = moodEntry.note ?: "No additional notes."

        ivMoodIconDetail.setImageResource(getMoodIcon(moodEntry.mood))
    }

    private fun getMoodIcon(mood: String): Int {
        return when (mood) {
            "Happy" -> R.drawable.ic_happy
            "Sad" -> R.drawable.ic_sad
            "Angry" -> R.drawable.ic_angry
            "Excited" -> R.drawable.ic_excited
            "Anxious" -> R.drawable.ic_anxious
            "Relaxed" -> R.drawable.ic_relaxed
            else -> R.drawable.ic_neutral
        }
    }
}