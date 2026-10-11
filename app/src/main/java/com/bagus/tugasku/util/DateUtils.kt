package com.bagus.tugasku.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Semua deadline disimpan sebagai "tengah malam UTC" (sesuai keluaran Material3 DatePicker).
 * Karena itu pemformatan memakai zona UTC, dan "hari ini" dikonversi ke tengah malam UTC
 * dari tanggal lokal perangkat. Hasilnya benar di zona waktu mana pun.
 */
object DateUtils {

    /** Deadline dianggap "dekat" bila sisa hari <= nilai ini. */
    const val DUE_SOON_DAYS = 2L

    private val utc: TimeZone = TimeZone.getTimeZone("UTC")
    private val localeId: Locale = Locale.forLanguageTag("id-ID")

    fun formatDate(utcMidnightMillis: Long): String =
        SimpleDateFormat("EEE, d MMM yyyy", localeId)
            .apply { timeZone = utc }
            .format(Date(utcMidnightMillis))

    fun todayAsUtcMidnight(): Long {
        val local = Calendar.getInstance()
        return Calendar.getInstance(utc).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

    /** Sisa hari sampai deadline. Negatif berarti sudah lewat. */
    fun daysUntil(deadline: Long, today: Long = todayAsUtcMidnight()): Long =
        TimeUnit.MILLISECONDS.toDays(deadline - today)

    fun deadlineLabel(daysLeft: Long): String = when {
        daysLeft < 0 -> "Terlambat ${-daysLeft} hari"
        daysLeft == 0L -> "Hari ini"
        daysLeft == 1L -> "Besok"
        else -> "$daysLeft hari lagi"
    }
}
