package com.bignerdranch.android.moodi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.moodi.data.AppDatabase
import com.bignerdranch.android.moodi.data.MoodEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

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


        // Initialize views
        btnHappy = view.findViewById(R.id.btnHappy)
        btnSad = view.findViewById(R.id.btnSad)
        btnAngry = view.findViewById(R.id.btnAngry)
        btnExcited = view.findViewById(R.id.btnExcited)
        btnAnxious = view.findViewById(R.id.btnAnxious)
        btnRelaxed = view.findViewById(R.id.btnRelaxed)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        etNote = view.findViewById(R.id.etNote)

        setupClickListeners()
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
                saveMood(mood, note)
            } else {
                Toast.makeText(context, "Please select a mood first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectMood(mood: String) {
        selectedMood = mood
        // Update UI to show selected mood (you can add this functionality later)
    }

    private fun saveMood(mood: String, note: String) {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val moodEntry = MoodEntry(
                mood = mood,
                timestamp = System.currentTimeMillis(),
                note = note.ifEmpty { null }
            )
            db.moodDao().insert(moodEntry)
            etNote.text.clear()
            selectedMood = null
            Toast.makeText(context, "Mood saved!", Toast.LENGTH_SHORT).show()
        }
    }
}