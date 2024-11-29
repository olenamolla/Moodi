package com.bignerdranch.android.moodi.ui



import android.graphics.drawable.ColorDrawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar

class DayViewDecorator(private val calendar: Calendar, private val color: Int) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.calendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                day.calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(ColorDrawable(color))
    }
}