package com.bignerdranch.android.moodi.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.bignerdranch.android.moodi.R

class MessageDialogManager {
    companion object {
        private var currentDialog: Dialog? = null

        fun showMessage(context: Context, title: String, message: String) {
            // Dismiss any existing dialog
            currentDialog?.dismiss()

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null)
            
            view.findViewById<TextView>(R.id.tvDialogTitle).text = title
            view.findViewById<TextView>(R.id.tvDialogMessage).text = message
            
            view.findViewById<Button>(R.id.btnDialogOk).setOnClickListener {
                dialog.dismiss()
            }
            
            dialog.setContentView(view)
            dialog.show()
            
            currentDialog = dialog
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
