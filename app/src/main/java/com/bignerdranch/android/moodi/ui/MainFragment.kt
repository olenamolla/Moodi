package com.bignerdranch.android.moodi.ui

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.moodi.R
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import com.bignerdranch.android.moodi.utils.MessageDialogManager
import com.bignerdranch.android.moodi.viewmodel.MainViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()



    private lateinit var btnHappy: ImageButton
    private lateinit var btnSad: ImageButton
    private lateinit var btnAngry: ImageButton
    private lateinit var btnExcited: ImageButton
    private lateinit var btnAnxious: ImageButton
    private lateinit var btnRelaxed: ImageButton
    private lateinit var btnSubmit: Button
    private lateinit var etNote: EditText

    private var selectedMood: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
        observeViewModel()

    }


    private lateinit var tvAiInsight: TextView
    private lateinit var cardAiInsight: MaterialCardView

    private fun initializeViews(view: View) {
        btnHappy = view.findViewById(R.id.btnHappy)
        btnSad = view.findViewById(R.id.btnSad)
        btnAngry = view.findViewById(R.id.btnAngry)
        btnExcited = view.findViewById(R.id.btnExcited)
        btnAnxious = view.findViewById(R.id.btnAnxious)
        btnRelaxed = view.findViewById(R.id.btnRelaxed)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        etNote = view.findViewById(R.id.etNote)
        tvAiInsight = view.findViewById(R.id.tvAiInsight)
        cardAiInsight = view.findViewById(R.id.cardAiInsight)

        cardAiInsight.setOnClickListener {
            val insightText = tvAiInsight.text.toString()
            if (insightText != "Here you will see AI-powered insight once you choose your mood. Click to expand when insight appears.") {
                showFullInsight(insightText)
            }
        }
    }

    private fun setupClickListeners() {
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
                viewModel.saveMood(mood, note)
            } else {
                Toast.makeText(context, "Please select a mood first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedMood.observe(viewLifecycleOwner) { mood ->
            // Update UI to show selected mood

        }



        viewModel.aiInsight.observe(viewLifecycleOwner) { insight ->
            tvAiInsight.text = insight
            cardAiInsight.visibility = if (insight.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    private fun selectMood(mood: String) {
        selectedMood = mood

    }


    
    private fun saveMood(selectedMoodParam: String, note: String) {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val moodEntry = MoodEntry(
                mood = selectedMoodParam,
                timestamp = System.currentTimeMillis(),
                note = note.ifEmpty { null }
            )
            db.moodDao().insert(moodEntry)
            etNote.text.clear()
            selectedMood = null
            
            // Show encouraging message using new dialog system
            val (title, message) = MessageDialogManager.getCheerfulMessage(selectedMoodParam)
            MessageDialogManager.showMessage(requireContext(), title, message)
        }
    }



    private fun showFullInsight(insight: String) {
        InsightDialog(requireContext(), insight).show()
    }
}
