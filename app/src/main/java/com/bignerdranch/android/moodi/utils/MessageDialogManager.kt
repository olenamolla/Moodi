package com.bignerdranch.android.moodi.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.bignerdranch.android.moodi.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MessageDialogManager {
    companion object {
        private var currentDialog: Dialog? = null

        fun showMessage(context: Context, title: String, message: String) {
            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .create()

            // Make dialog larger and add scrolling
            dialog.setOnShowListener {
                val messageView = dialog.findViewById<TextView>(android.R.id.message)
                messageView?.apply {
                    textSize = 16f  // Larger text
                    val padding = context.resources.getDimensionPixelSize(R.dimen.dialog_padding)
                    setPadding(padding, padding, padding, padding)
                }

                // Set dialog width to 90% of screen width
                val window = dialog.window
                val displayMetrics = context.resources.displayMetrics
                val width = (displayMetrics.widthPixels * 0.9).toInt()
                window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            }

            dialog.show()
        }

        fun getCheerfulMessage(mood: String): Pair<String, String> {
            return when (mood.lowercase()) {
                "sad" -> Pair(
                    "A Gentle Reminder",
                    "Every cloud has a silver lining. Tomorrow brings new opportunities and fresh perspectives. Would you like to try some mood-lifting activities?"
                )
                "anxious" -> Pair(
                    "Take a Breath",
                    "Anxiety is temporary, but your strength is permanent. Try taking 3 deep breaths right now - breathe in for 4 counts, hold for 4, out for 4."
                )
                "angry" -> Pair(
                    "Finding Peace",
                    "It's okay to feel angry. Consider channeling this energy into something productive, like exercise or creative expression."
                )
                "happy" -> Pair(
                    "Wonderful!",
                    "Your happiness can brighten someone else's day too! Consider sharing your positive energy with others."
                )
                "excited" -> Pair(
                    "Amazing Energy!",
                    "Channel this excitement into something meaningful. What inspired goal would you like to work towards today?"
                )
                "relaxed" -> Pair(
                    "Perfect Balance",
                    "This peaceful moment is perfect for reflection. What are three things you're grateful for right now?"
                )
                else -> Pair(
                    "Reflection Time",
                    "Every emotion has its purpose. What is your emotion trying to tell you today?"
                )
            }
        }
    }
}
