package com.example.booklibrary.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Форматирование дат: в базе лежит Long (миллисекунды), пользователю
 * нужен красивый "7 октября 2026".
 *
 * Используем SimpleDateFormat — работает на всех версиях Android от API 24.
 * (Современная альтернатива — java.time + coreLibraryDesugaring, см. гайд.)
 */
object DateUtils {

    private val fullFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    private val shortFormat = SimpleDateFormat("d.MM", Locale.getDefault())

    /** "7 октября 2026"; для null (дата не проставлена) — прочерк. */
    fun formatDate(timestamp: Long?): String =
        timestamp?.let { fullFormat.format(Date(it)) } ?: "—"

    /** Короткая подпись дня для оси X графика: "7.10". */
    fun shortDay(timestamp: Long): String = shortFormat.format(Date(timestamp))

    /** Начало сегодняшнего дня (00:00:00.000) — точка отсчёта графика. */
    fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
