// app/src/main/java/com/bignerdranch/android/moodi/MoodTrendsActivity.kt
package com.bignerdranch.android.moodi

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class MoodTrendsActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_trends)

        calendarView = findViewById(R.id.calendarView)
        db = AppDatabase.getDatabase(this)

        loadMoodEntries()
    }

    private fun loadMoodEntries() {
        lifecycleScope.launch {
            val moodEntries = withContext(Dispatchers.IO) {
                db.moodDao().getAllMoods()
            }
            decorateCalendar(moodEntries)
        }
    }

    private fun decorateCalendar(moodEntries: List<MoodEntry>) {
        val dateMoodMap = moodEntries.associate { 
            val calendar = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            CalendarDay.from(calendar) to it
        }

        moodEntries.forEach { entry ->
            val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            val day = CalendarDay.from(calendar)
            val color = getColorForMood(entry.mood)
            calendarView.addDecorator(SimpleDayDecorator(day, color))
        }
    }

    private fun getColorForMood(mood: String): Int {
        return when (mood) {
            "Happy" -> Color.parseColor("#FFD700") // Gold
            "Sad" -> Color.parseColor("#1E90FF") // Dodger Blue
            "Angry" -> Color.parseColor("#FF4500") // Orange Red
            "Excited" -> Color.parseColor("#FF69B4") // Hot Pink
            "Anxious" -> Color.parseColor("#8A2BE2") // Blue Violet
            "Relaxed" -> Color.parseColor("#32CD32") // Lime Green
            else -> Color.parseColor("#A9A9A9") // Dark Gray
        }
    }

    inner class SimpleDayDecorator(private val day: CalendarDay, private val color: Int) : DayViewDecorator {
        override fun shouldDecorate(dayToDecorate: CalendarDay?): Boolean {
            return dayToDecorate == day
        }

        override fun decorate(view: DayViewFacade) {
          view.addSpan(DotSpan(5f, color))
        }
        /*override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : com.prolificinteractive.materialcalendarview.DayViewSpan {
                override fun drawBackground(canvas: android.graphics.Canvas, paint: android.graphics.Paint, startX: Int, startY: Int, endX: Int, endY: Int, view: android.view.View) {
                    paint.color = color
                    canvas.drawCircle((startX + endX) / 2f, (startY + endY) / 2f, (endX - startX) / 4f, paint)
                }

                override fun drawText(canvas: android.graphics.Canvas, text: CharSequence, paint: android.graphics.Paint, startX: Int, baseline: Int, endX: Int, startY: Int, endY: Int, view: android.view.View) {
                    // Let the default text rendering occur
                }
            })
        }*/
    }
}