package com.example.mangashelf.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun convertUnixToYear(unixTimestamp: Long): String =
        SimpleDateFormat("YYYY", Locale.getDefault()).format(Date(unixTimestamp * 1000))

    fun convertUnixToReadableDate(unixTimestamp: Long): String =
        SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date(unixTimestamp * 1000))
}