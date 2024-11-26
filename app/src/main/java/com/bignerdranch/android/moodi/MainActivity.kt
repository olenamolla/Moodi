// app/src/main/java/com/bignerdranch/android/moodi/MainActivity.kt
package com.bignerdranch.android.moodi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var btnHappy: ImageButton
    private lateinit var btnSad: ImageButton
    private lateinit var btnAngry: ImageButton
    private lateinit var btnExcited: ImageButton
    private lateinit var btnAnxious: ImageButton
    private lateinit var btnRelaxed: ImageButton
    private lateinit var btnSubmit: Button
    private lateinit var etNote: EditText

    private var selectedMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view first
        setContentView(R.layout.activity_main)

        // Initializing Room Database
        val db = AppDatabase.getDatabase(this)

        // Initialize buttons after setting the content view
        val btnViewHistory: Button = findViewById(R.id.btnViewHistory)
        btnViewHistory.setOnClickListener {
            val intent = Intent(this, MoodHistoryActivity::class.java)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        NotificationUtils.createNotificationChannel(this)

        val dailyWorkRequest = androidx.work.PeriodicWorkRequestBuilder<MoodNotificationWorker>(24, java.util.concurrent.TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MoodNotificationWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )

        val btnViewTrends: Button = findViewById(R.id.btnViewTrends)
        btnViewTrends.setOnClickListener {
            val intent = Intent(this, MoodTrendsActivity::class.java)
            startActivity(intent)
        }

        // Initializing other buttons and input field
        btnHappy = findViewById(R.id.btnHappy)
        btnSad = findViewById(R.id.btnSad)
        btnAngry = findViewById(R.id.btnAngry)
        btnExcited = findViewById(R.id.btnExcited)
        btnAnxious = findViewById(R.id.btnAnxious)
        btnRelaxed = findViewById(R.id.btnRelaxed)
        btnSubmit = findViewById(R.id.btnSubmit)
        etNote = findViewById(R.id.etNote)

        // Set click listeners
        btnHappy.setOnClickListener { selectMood("Happy") }
        btnSad.setOnClickListener { selectMood("Sad") }
        btnAngry.setOnClickListener { selectMood("Angry") }
        btnExcited.setOnClickListener { selectMood("Excited") }
        btnAnxious.setOnClickListener { selectMood("Anxious") }
        btnRelaxed.setOnClickListener { selectMood("Relaxed") }

        btnSubmit.setOnClickListener {
            val mood = selectedMood
            val note = etNote.text.toString()
            if (mood != null) {
                saveMood(db, mood, note)
            } else {
                Toast.makeText(this, "Please select a mood first.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun selectMood(mood: String) {
        selectedMood = mood
        Toast.makeText(this, "You selected: $mood", Toast.LENGTH_SHORT).show()
        // Optionally, you can highlight the selected mood button
    }

    private fun saveMood(db: AppDatabase, mood: String, note: String) {
        val moodEntry = MoodEntry(
            mood = mood,
            timestamp = System.currentTimeMillis(),
            note = if (note.isBlank()) null else note
        )
        lifecycleScope.launch(Dispatchers.IO) {
            db.moodDao().insert(moodEntry)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Mood saved!", Toast.LENGTH_SHORT).show()
                etNote.text.clear()
                selectedMood = null
                suggestActivity(mood)
            }
        }
    }

    private fun calculateInitialDelay(): Long {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set Notification Time to 8 PM
        dueDate.set(Calendar.HOUR_OF_DAY, 20)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        return dueDate.timeInMillis - currentDate.timeInMillis
    }

    private fun suggestActivity(mood: String) {
        when (mood) {
            "Anxious", "Sad" -> {
                val activities = listOf(
                    "Feeling stressed? Try a quick breathing exercise!",
                    "Feeling sad? Listen to your favorite song!",
                    "Feeling anxious? Take a short walk outside!"
                )
                val suggestion = activities.random()
                Toast.makeText(this, suggestion, Toast.LENGTH_LONG).show()
            }
            // Add more suggestions for other moods if desired
        }
    }
}