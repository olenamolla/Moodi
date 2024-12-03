package com.bignerdranch.android.moodi.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.Calendar




/**
 * Fragment for displaying mood trends in a calendar view.
 * Shows mood entries with different colors for each mood type.
 */
class MoodTrendsFragment : Fragment() {

    private val viewModel: MoodTrendsViewModel by viewModels()
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var pieChartDaily: PieChart
    private lateinit var pieChartWeekly: PieChart
    private lateinit var pieChartMonthly: PieChart


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_trends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        //calendarView = view.findViewById(R.id.calendarView)
        setupCalendarView()
        setupCharts()
        observeViewModel()


    }

    private fun initializeViews(view: View) {
        calendarView = view.findViewById(R.id.calendarView)
        pieChartDaily = view.findViewById(R.id.pieChartDaily)
        pieChartWeekly = view.findViewById(R.id.pieChartWeekly)
        pieChartMonthly = view.findViewById(R.id.pieChartMonthly)
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
        viewModel.moodEntries.observe(viewLifecycleOwner) { entries ->
            if (entries != null) {
                val prevailingMoods = viewModel.calculatePrevailingMoods(entries)
                decorateCalendar(prevailingMoods)
                viewModel.calculateStatistics(entries)
            }
        }

        viewModel.dailyMoodStats.observe(viewLifecycleOwner) { stats ->
            Log.d("MoodTrends", "Daily stats: $stats")  // Add logging
            updatePieChart(pieChartDaily, stats)
        }

        viewModel.weeklyMoodStats.observe(viewLifecycleOwner) { stats ->
            Log.d("MoodTrends", "Weekly stats: $stats")  // Add logging
            updatePieChart(pieChartWeekly, stats)
        }

        viewModel.monthlyMoodStats.observe(viewLifecycleOwner) { stats ->
            Log.d("MoodTrends", "Monthly stats: $stats")  // Add logging
            updatePieChart(pieChartMonthly, stats)
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


    private fun decorateCalendar(prevailingMoods: Map<Long, String>) {
        calendarView.removeDecorators()

        prevailingMoods.forEach { (date, mood) ->
            val calendar = Calendar.getInstance().apply { timeInMillis = date }
            val color = getMoodColor(mood)
            calendarView.addDecorator(DayViewDecorator(calendar, color))
        }
    }

    private fun getMoodColor(mood: String): Int {
        return MOOD_COLORS[mood] ?: Color.WHITE
    }

    private fun getMoodColors(): List<Int> {
        return listOf(
            Color.YELLOW,  // Happy
            Color.BLUE,    // Sad
            Color.RED,     // Angry
            Color.GREEN,   // Excited
            Color.GRAY,    // Anxious
            Color.CYAN     // Relaxed
        )
    }

    private fun setupCharts() {
        listOf(pieChartDaily, pieChartWeekly, pieChartMonthly).forEach { chart ->
            setupModernChart(chart)
        }
        
        // Add titles with more context
        pieChartDaily.centerText = "Today's\nMoods"
        pieChartWeekly.centerText = "This Week's\nTrends"
        pieChartMonthly.centerText = "Monthly\nOverview"
        
        // Enable animations with correct easing type
        listOf(pieChartDaily, pieChartWeekly, pieChartMonthly).forEach { chart ->
            chart.animateY(1400, Easing.EaseInOutQuad)
        }
    }

    private fun setupModernChart(chart: PieChart) {
        chart.apply {
            setUsePercentValues(true)
            description.isEnabled = false

            // Modern transparent hole
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f

            // Center text styling
            setDrawCenterText(true)
            centerText = "Mood\nDistribution"
            setCenterTextSize(16f)
            setCenterTextColor(Color.parseColor("#424242")) // Dark gray

            // Modern legend styling
            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.VERTICAL
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                setDrawInside(false)
                textSize = 12f
                form = Legend.LegendForm.CIRCLE
                formSize = 12f
                xEntrySpace = 8f
                yEntrySpace = 8f
            }

            // Additional modern touches
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            dragDecelerationFrictionCoef = 0.95f
        }
    }

    private fun updatePieChart(chart: PieChart, stats: Map<String, Float>) {
        if (stats.isEmpty()) {
            chart.setNoDataText("No mood data available")
            chart.invalidate()
            return
        }

        val entries = stats.map { (mood, percentage) ->
            PieEntry(percentage, mood)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = entries.map { entry -> MOOD_COLORS[entry.label] ?: Color.WHITE }  // Match colors to moods
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            valueFormatter = PercentFormatter()
            sliceSpace = 3f
            selectionShift = 5f
            valueLinePart1OffsetPercentage = 80f
            valueLinePart1Length = 0.2f
            valueLinePart2Length = 0.4f
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.parseColor("#424242"))
        }

        chart.apply {
            data = pieData
            animateY(1400)
            setHighlightPerTapEnabled(true)
            invalidate()
        }
    }

    private fun getModernMoodColors(): List<Int> {
        return MOOD_COLORS.values.toList()
    }

    companion object {
        private val MOOD_COLORS = mapOf(
            "Happy" to Color.parseColor("#FFB300"),    // Amber
            "Excited" to Color.parseColor("#2196F3"),  // Blue
            "Anxious" to Color.parseColor("#F06292"),  // Pink
            "Angry" to Color.parseColor("#4CAF50"),    // Green
            "Sad" to Color.parseColor("#78909C"),      // Blue Grey
            "Relaxed" to Color.parseColor("#26A69A")   // Teal
        )
    }
}
