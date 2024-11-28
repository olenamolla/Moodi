package com.bignerdranch.android.moodi.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bignerdranch.android.moodi.R
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.bignerdranch.android.moodi.data.MoodEntry
import com.bignerdranch.android.moodi.viewmodel.MoodTrendsViewModel
import java.util.Calendar

/**
 * Fragment for displaying mood trends in a calendar view.
 * Shows mood entries with different colors for each mood type.
 */
class MoodTrendsFragment : Fragment() {

    private val viewModel: MoodTrendsViewModel by viewModels()
    private lateinit var calendarView: MaterialCalendarView
    //private lateinit var db: AppDatabase

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
        setupCalendarView()
        observeViewModel()


        //db = AppDatabase.getDatabase(requireContext())

        //loadMoodEntries()
    }

    /**
     * Sets up the calendar view with initial configuration
     */
    private fun setupCalendarView() {
        //calendarView = view?.findViewById(R.id.calendarView) ?: return
        calendarView.apply {
            // Calendar configuration
            setShowOtherDates(MaterialCalendarView.SHOW_ALL)
            // Add any other calendar configurations
        }
    }

    /**
     * Observes LiveData from ViewModel and updates UI
     */
    private fun observeViewModel() {
        // Observe mood entries
        viewModel.moodEntries.observe(viewLifecycleOwner) { entries ->
            if (entries != null) {
                decorateCalendar(entries)
            }
        }

        // Initial load
        viewModel.loadMoodEntries()
    }


    /*private fun loadMoodEntries() {
        lifecycleScope.launch {
            val moodEntries = withContext(Dispatchers.IO) {
                db.moodDao().getAllMoods()
            }
            decorateCalendar(moodEntries)
        }
    }*/

    /**
     * Decorates calendar with mood entries
     * @param moodEntries List of mood entries to display
     */
    private fun decorateCalendar(moodEntries: List<MoodEntry>) {
        try {
            calendarView.removeDecorators() // Clear existing decorations

            moodEntries.forEach { entry ->
                try {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = entry.timestamp
                    }
                    val day = CalendarDay.from(calendar)
                    val color = getColorForMood(entry.mood)
                    calendarView.addDecorator(SimpleDayDecorator(day, color))
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Continue with next entry if one fails
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle general decoration error
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