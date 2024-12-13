package com.bignerdranch.android.moodi.ui

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import com.bignerdranch.android.moodi.utils.MoodMessageManager
import com.bignerdranch.android.moodi.utils.MessageDialogManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.moodi.R
import com.bignerdranch.android.moodi.data.MoodEntry
import com.bignerdranch.android.moodi.utils.OpenAIAssistant
import com.bignerdranch.android.moodi.viewmodel.MoodHistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodHistoryFragment : Fragment() {

    // Use by viewModels() delegate to create ViewModel
    private val viewModel: MoodHistoryViewModel by viewModels()
    private lateinit var rvMoodHistory: RecyclerView
    private lateinit var moodEntries: List<MoodEntry>
    private lateinit var messageDialog: AlertDialog
    private lateinit var adapter: MoodEntryAdapter
    private lateinit var tvMostFrequentMood: TextView
    private lateinit var tvMoodCounts: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initializeViews(view)
        // 3. Setup RecyclerView
        setupRecyclerView()

        // 4. Observe ViewModel
        observeViewModel()


    }


    // NEW: Added this function to group initialization logic
    private fun initializeViews(view: View) {
        rvMoodHistory = view.findViewById(R.id.rvMoodHistory)

        initializeMessageDialog()

        // NEW: Initialize moodEntries with empty list
        moodEntries = emptyList()

        adapter = MoodEntryAdapter(moodEntries) { moodEntry ->
            navigateToDetail(moodEntry)
        }
    }


    private fun setupRecyclerView() {
        rvMoodHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MoodHistoryFragment.adapter  // Use the initialized adapter
        }
    }

    private fun observeViewModel() {
        // Observe moods from ViewModel
        viewModel.moods.observe(viewLifecycleOwner) { entries ->
            if (entries != null) {
                moodEntries = entries  // NEW: Update moodEntries when new data arrives
                adapter.updateMoods(entries)
                analyzeTrends(entries)
            }
        }



        // Load initial data
        viewModel.loadMoods()
    }

    private fun navigateToDetail(moodEntry: MoodEntry) {
        val intent = Intent(requireContext(), MoodDetailActivity::class.java).apply {
            putExtra("mood_id", moodEntry.id)
        }
        startActivity(intent)
    }


    private fun analyzeTrends(moodEntries: List<MoodEntry>) {
        lifecycleScope.launch {
            try {
                // Get today's entries only
                val calendar = Calendar.getInstance()
                val startOfDay = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.timeInMillis

                val todaysMoods = moodEntries.filter { it.timestamp >= startOfDay }

                if (todaysMoods.isNotEmpty()) {
                    // Create a summary of today's moods
                    val moodSummary = todaysMoods
                        .groupBy { it.mood }
                        .map { (mood, entries) -> "$mood: ${entries.size} times" }
                        .joinToString(", ")

                    // Get AI-generated insight
                    val prompt = """
                    Based on today's mood entries: $moodSummary.
                    Please provide a caring, personalized response that:
                    1. Acknowledges the emotional pattern
                    2. Offers specific, helpful suggestions or encouragement
                    3. Keeps a supportive and understanding tone
                    Format the response with a brief title and a detailed message.
                    Separate title and message with '|' character.
                """.trimIndent()

                    try {
                        val aiResponse = OpenAIAssistant.generateMoodInsight(prompt)
                        val (title, message) = aiResponse.split("|").let {
                            Pair(it[0].trim(), it[1].trim())
                        }

                        withContext(Dispatchers.Main) {
                            MessageDialogManager.showMessage(requireContext(), title, message)
                        }
                    } catch (e: Exception) {
                        Log.e("MoodHistory", "Error generating AI insight: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    private fun initializeMessageDialog() {
        messageDialog = AlertDialog.Builder(requireContext())
            .setTitle("Mood Insight")
            .setPositiveButton("Thanks!") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun showMotivationalMessage(mood: String) {
        val message = MoodMessageManager.getMessageForMood(mood)
        messageDialog.setMessage(message)
        messageDialog.show()
    }

    private fun showMotivationalQuote() {
        val message = MoodMessageManager.getMessageForMood("MOTIVATIONAL")
        messageDialog.setMessage(message)
        messageDialog.show()
    }



}
