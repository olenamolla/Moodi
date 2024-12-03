package com.bignerdranch.android.moodi.utils

class MoodMessageManager {
    companion object {
        private val motivationalMessages = mapOf(
            "SAD" to listOf(
                "Remember that every cloud has a silver lining",
                "This too shall pass. Take care of yourself today",
                "You're stronger than you know"
            ),
            "ANXIOUS" to listOf(
                "Take deep breaths. You've got this",
                "One step at a time. You're doing great",
                "Focus on what you can control"
            ),
            "HAPPY" to listOf(
                "Your happiness is contagious!",
                "Keep spreading that positive energy",
                "What a wonderful day to be happy!"
            ),
            "EXCITED" to listOf(
                "Channel that excitement into something amazing!",
                "Your enthusiasm lights up the room",
                "Great things happen when you're excited"
            ),
            "RELAXED" to listOf(
                "Peaceful mind, peaceful life",
                "Enjoy this moment of tranquility",
                "You deserve this peaceful time"
            )
        )

        fun getMessageForMood(mood: String): String {
            return motivationalMessages[mood.uppercase()]?.random() 
                ?: "Take care of yourself today!"
        }
    }
}
