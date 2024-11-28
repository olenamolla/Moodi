package com.bignerdranch.android.moodi

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MoodTrendsFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_trends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        db = AppDatabase.getDatabase(requireContext())

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
            "Sad" -> Color.parseColor("#4682B4") // Steel Blue
            "Angry" -> Color.parseColor("#FF4500") // Orange Red
            "Excited" -> Color.parseColor("#32CD32") // Lime Green
            "Anxious" -> Color.parseColor("#9370DB") // Medium Purple
            "Relaxed" -> Color.parseColor("#87CEEB") // Sky Blue
            else -> Color.GRAY
        }
    }
}