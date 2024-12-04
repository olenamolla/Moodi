package com.bignerdranch.android.moodi.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import com.bignerdranch.android.moodi.R

class InsightDialog(context: Context, private val insight: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_insight)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        findViewById<TextView>(R.id.tvFullInsight).text = insight
        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            dismiss()
        }
    }
}