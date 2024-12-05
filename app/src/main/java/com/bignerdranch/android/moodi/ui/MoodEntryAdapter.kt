// app/src/main/java/com/bignerdranch/android/moodi/MoodEntryAdapter.kt
package com.bignerdranch.android.moodi.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.moodi.R
import com.bignerdranch.android.moodi.data.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodEntryAdapter(
    private var moodEntries: List<MoodEntry>,
    private val onItemClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodEntryAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodViewHolder(view)
    }

    // Add function to update list
    fun updateMoods(newMoods: List<MoodEntry>) {
        moodEntries = newMoods
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        holder.bind(moodEntry)
    }

    override fun getItemCount(): Int = moodEntries.size



    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMoodIcon: ImageView = itemView.findViewById(R.id.ivMoodIcon)
        private val tvMoodType: TextView = itemView.findViewById(R.id.tvMoodType)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        fun bind(moodEntry: MoodEntry) {
            tvMoodType.text = moodEntry.mood
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = Date(moodEntry.timestamp)
            tvTimestamp.text = sdf.format(date)

            // Set mood icon based on mood type
            ivMoodIcon.setImageResource(getMoodIcon(moodEntry.mood))

            itemView.setOnClickListener {
                onItemClick(moodEntry)
            }
        }

        private fun getMoodIcon(mood: String): Int {
            return when (mood) {
                "Happy" -> R.drawable.ic_happy
                "Sad" -> R.drawable.ic_sad
                "Angry" -> R.drawable.ic_angry
                "Excited" -> R.drawable.ic_excited
                "Anxious" -> R.drawable.ic_anxious
                "Relaxed" -> R.drawable.ic_relaxed
                else -> R.drawable.ic_neutral
            }
        }
    }
}