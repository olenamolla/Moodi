package com.bignerdranch.android.moodi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.android.moodi.ui.theme.MoodiTheme
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnHappy: ImageButton
    private lateinit var btnSad: ImageButton
    private lateinit var btnAngry: ImageButton
    private lateinit var btnExcited: ImageButton
    private lateinit var btnAnxious: ImageButton
    private lateinit var btnRelaxed: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.activity_main)

        // Initializing buttons
        btnHappy = findViewById(R.id.btnHappy)
        btnSad = findViewById(R.id.btnSad)
        btnAngry = findViewById(R.id.btnAngry)
        btnExcited = findViewById(R.id.btnExcited)
        btnAnxious = findViewById(R.id.btnAnxious)
        btnRelaxed = findViewById(R.id.btnRelaxed)

        // Set click listeners
        btnHappy.setOnClickListener {
            onMoodSelected("Happy")
        }

        btnSad.setOnClickListener {
            onMoodSelected("Sad")
        }

        btnAngry.setOnClickListener {
            onMoodSelected("Angry")
        }

        btnExcited.setOnClickListener {
            onMoodSelected("Excited")
        }

        btnAnxious.setOnClickListener {
            onMoodSelected("Anxious")
        }

        btnRelaxed.setOnClickListener {
            onMoodSelected("Relaxed")
        }
    }


    private fun onMoodSelected(mood: String) {
        // Display a confirmation message
        Toast.makeText(this, "You are feeling $mood!", Toast.LENGTH_SHORT).show()
        // TODO: Save the mood selection (we'll implement this in the next days)
    }
}

