package com.bignerdranch.android.moodi

import android.graphics.drawable.ColorDrawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SimpleDayDecorator(private val date: CalendarDay, private val color: Int) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(ColorDrawable(color))
    }
}