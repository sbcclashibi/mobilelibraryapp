package com.jessejojojohnson.mobilelibraryapp.util

import java.text.SimpleDateFormat
import java.util.Date

fun formatDateSimple(date: Date): String {
    // Pattern: "Month day year, h:mma"
    // Example: "March 1 2020, 3:30pm" (Note: no "st", "nd", etc.)
    // `a` gives AM/PM. We'll manually lowercase it.
    val sdf = SimpleDateFormat("MMMM d yyyy, h:mma", java.util.Locale.getDefault())
    val formattedString = sdf.format(date)

    // For lowercase am/pm, as SimpleDateFormat might produce uppercase AM/PM
    return formattedString.replace("AM", "am").replace("PM", "pm")
}