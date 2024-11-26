// app/src/main/java/com/bignerdranch/android/moodi/MoodHistoryActivity.kt
package com.bignerdranch.android.moodi

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast


class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var rvMoodHistory: RecyclerView
    private lateinit var tvMostFrequentMood: TextView
    private lateinit var tvMoodCounts: TextView
    private lateinit var adapter: MoodEntryAdapter
    private var moodEntries: List<MoodEntry> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_history)

        rvMoodHistory = findViewById(R.id.rvMoodHistory)
        tvMostFrequentMood = findViewById(R.id.tvMostFrequentMood)
        tvMoodCounts = findViewById(R.id.tvMoodCounts)

        rvMoodHistory.layoutManager = LinearLayoutManager(this)
        adapter = MoodEntryAdapter(moodEntries) { moodEntry ->
            // Handle item click to open Detail View
            val intent = Intent(this, MoodDetailActivity::class.java).apply {
                putExtra("mood_id", moodEntry.id)
            }
            startActivity(intent)
        }
        rvMoodHistory.adapter = adapter

        loadMoods()
    }

    private fun loadMoods() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            moodEntries = withContext(Dispatchers.IO) {
                db.moodDao().getAllMoods()
            }
            adapter = MoodEntryAdapter(moodEntries) { moodEntry ->
                val intent = Intent(this@MoodHistoryActivity, MoodDetailActivity::class.java).apply {
                    putExtra("mood_id", moodEntry.id)
                }
                startActivity(intent)
            }
            rvMoodHistory.adapter = adapter
            calculateStatistics()
        }
    }

    private suspend fun calculateStatistics() {
        val db = AppDatabase.getDatabase(this)
        val moods = listOf("Happy", "Sad", "Angry", "Excited", "Anxious", "Relaxed")
        val counts = mutableMapOf<String, Int>()
        for (mood in moods) {
            counts[mood] = db.moodDao().getMoodCount(mood)
        }
        val mostFrequent = db.moodDao().getMostFrequentMood()

        withContext(Dispatchers.Main) {
            tvMostFrequentMood.text = "Most Frequent Mood: ${mostFrequent ?: "N/A"}"
            tvMoodCounts.text = "Mood Counts: ${counts.entries.joinToString(", ") { "${it.key}: ${it.value}" }}"
        }

        analyzeTrends()
    }

    private suspend fun analyzeTrends() {
      val db = AppDatabase.getDatabase(this)
      val moodEntries = db.moodDao().getAllMoods().sortedBy { it.timestamp }
      
      // Check for more than 3 consecutive negative moods
      val negativeMoods = listOf("Sad", "Angry", "Anxious")
      var consecutiveNegative = 0
      var improvement = 0
      var previousMoodScore = Int.MAX_VALUE // Assuming higher score is better

      for (entry in moodEntries) {
          val currentMoodScore = getMoodScore(entry.mood)
          if (negativeMoods.contains(entry.mood)) {
              consecutiveNegative++
              if (consecutiveNegative >= 3) {
                  withContext(Dispatchers.Main) {
                      Toast.makeText(this@MoodHistoryActivity, 
                          "It's concerning that you've been feeling down for a while. Consider taking a break or reaching out to someone you trust.", 
                          Toast.LENGTH_LONG).show()
                  }
              }
          } else {
              consecutiveNegative = 0
          }

          // Check for improvement
          if (previousMoodScore != Int.MAX_VALUE && currentMoodScore > previousMoodScore) {
              improvement++
              if (improvement >= 3) {
                  withContext(Dispatchers.Main) {
                      Toast.makeText(this@MoodHistoryActivity, 
                          "You're on a roll! Great to see your positivity growing. Keep it up!", 
                          Toast.LENGTH_LONG).show()
                  }
              }
          } else {
              improvement = 0
          }

          previousMoodScore = currentMoodScore
      }
      // Check for consistent positive or negative moods
      val positiveMoods = listOf("Happy", "Excited", "Relaxed")
      val total = moodEntries.size
      val positiveCount = moodEntries.count { positiveMoods.contains(it.mood) }
      val negativeCount = moodEntries.count { negativeMoods.contains(it.mood) }
      
      if (positiveCount > total / 2) {
          withContext(Dispatchers.Main) {
              Toast.makeText(this@MoodHistoryActivity, 
                  "You're doing amazing! Remember to celebrate these good vibes!", 
                  Toast.LENGTH_LONG).show()
          }
      } else if (negativeCount > total / 2) {
          withContext(Dispatchers.Main) {
              Toast.makeText(this@MoodHistoryActivity, 
                  "Tough times don’t last forever. Try doing something you love today—maybe a walk, a hobby, or a favorite meal?", 
                  Toast.LENGTH_LONG).show()
          }
      }

      // Cheer-Up Features
      for (entry in moodEntries) {
          if (negativeMoods.contains(entry.mood)) {
              showMotivationalQuote()
          } else if (positiveMoods.contains(entry.mood)) {
              showPositiveAnimation()
          }
      }
}

private fun getMoodScore(mood: String): Int {
    return when (mood) {
        "Happy" -> 5
        "Excited" -> 4
        "Relaxed" -> 3
        "Neutral" -> 2
        "Anxious" -> 1
        "Sad" -> 1
        "Angry" -> 1
        else -> 2
    }
}

private fun showMotivationalQuote() {
    val quotes = listOf(
        "Every storm runs out of rain. Hang in there!",
        "Keep your face always toward the sunshine—and shadows will fall behind you.",
        "The sun himself is weak when he first rises, and gathers strength and courage as the day gets on."
    )
    val randomQuote = quotes.random()
    Toast.makeText(this, randomQuote, Toast.LENGTH_LONG).show()
}

private fun showPositiveAnimation() {
    // Implement a simple animation or display an emoji
    Toast.makeText(this, "🎉 You're doing great! 🎉", Toast.LENGTH_SHORT).show()
}

}