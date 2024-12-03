package com.bignerdranch.android.moodi.ui

import android.content.Intent
import android.os.Bundle
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


        // 2. Initialize adapter
        /*adapter = MoodEntryAdapter(emptyList()) { moodEntry ->
            navigateToDetail(moodEntry)
        }*/

        initializeViews(view)
        // 3. Setup RecyclerView
        setupRecyclerView()

        // 4. Observe ViewModel
        observeViewModel()


    }


    // NEW: Added this function to group initialization logic
    private fun initializeViews(view: View) {
        rvMoodHistory = view.findViewById(R.id.rvMoodHistory)
        tvMostFrequentMood = view.findViewById(R.id.tvMostFrequentMood)
        tvMoodCounts = view.findViewById(R.id.tvMoodCounts)
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

        // Observe statistics
        viewModel.statistics.observe(viewLifecycleOwner) { (mostFrequent, counts) ->
            tvMostFrequentMood.text = "Most Frequent Mood: ${mostFrequent ?: "N/A"}"
            tvMoodCounts.text = "Mood Counts: ${counts.entries.joinToString(", ") { "${it.key}: ${it.value}" }}"
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
                val sortedEntries = moodEntries.sortedBy { it.timestamp }

                // Define mood categories
                val negativeMoods = listOf("Sad", "Angry", "Anxious")
                val positiveMoods = listOf("Happy", "Excited", "Relaxed")

                // Track mood patterns
                var consecutiveNegative = 0
                var improvement = 0
                var previousMoodScore = Int.MAX_VALUE

                // Analyze mood progression
                for (entry in sortedEntries) {
                    val currentMoodScore = getMoodScore(entry.mood)

                    // Check negative mood streak
                    if (negativeMoods.contains(entry.mood)) {
                        consecutiveNegative++
                        if (consecutiveNegative >= 3) {
                            withContext(Dispatchers.Main) {
                                MessageDialogManager.showMessage(
                                    requireContext(),
                                    "A Caring Note",
                                    "I notice you've been feeling down lately. Remember it's okay to take breaks and reach out to people you trust. Would you like to try some mood-lifting activities?"
                                )
                            }
                            // Reset counter after showing message
                            consecutiveNegative = 0
                        }
                    } else {
                        consecutiveNegative = 0
                    }

                    // Check mood improvement
                    if (previousMoodScore != Int.MAX_VALUE && currentMoodScore > previousMoodScore) {
                        improvement++
                        if (improvement >= 3) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "You're on a roll! Great to see your positivity growing. Keep it up!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            // Reset counter after showing message
                            improvement = 0
                        }
                    } else {
                        improvement = 0
                    }

                    previousMoodScore = currentMoodScore
                }

                // Analyze overall mood distribution
                val total = sortedEntries.size
                if (total > 0) {  // Only analyze if there are entries
                    val positiveCount = sortedEntries.count { positiveMoods.contains(it.mood) }
                    val negativeCount = sortedEntries.count { negativeMoods.contains(it.mood) }

                    withContext(Dispatchers.Main) {
                        when {
                            positiveCount > total / 2 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "You're doing amazing! Remember to celebrate these good vibes!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            negativeCount > total / 2 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Tough times don't last forever. Try doing something you love today—maybe a walk, a hobby, or a favorite meal?",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    // Show mood-specific messages
                    val lastEntry = sortedEntries.lastOrNull()
                    lastEntry?.let { entry ->
                        when {
                            negativeMoods.contains(entry.mood) -> showMotivationalQuote()
                            positiveMoods.contains(entry.mood) -> showPositiveAnimation()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error analyzing mood trends",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    private fun showPositiveAnimation() {
        // Implement animation logic here if needed
    }

}
