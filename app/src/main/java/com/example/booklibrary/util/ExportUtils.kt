package com.example.booklibrary.util

import android.content.Context
import android.net.Uri
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экспорт библиотеки в текстовый файл (пункт 10 ТЗ).
 *
 * Как выбирается место сохранения: на Android 10+ приложение НЕ имеет права
 * писать в произвольную папку (Scoped Storage). Поэтому файл создаёт сам
 * пользователь через системный диалог "Сохранить как" — это SAF
 * (Storage Access Framework): экран получает Uri выбранного места и передаёт
 * сюда, а мы пишем текст через contentResolver.openOutputStream.
 */
object ExportUtils {

    /** Собирает текстовый отчёт: книги по статусам + статистика. */
    fun buildReport(books: List<Book>): String {
        val sb = StringBuilder()
        // BOM помогает старым Windows-редакторам распознать UTF-8
        sb.append("\uFEFF")
        sb.appendLine("МОЯ БИБЛИОТЕКА")
        sb.appendLine(
            "Отчёт сформирован: " +
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        )
        sb.appendLine("Всего книг: ${books.size}")

        // ---------- Книги по статусам ----------
        for (status in ReadingStatus.entries) {
            val group = books.filter { it.status == status }
            sb.appendLine()
            sb.appendLine("====== ${status.label.uppercase(Locale("ru"))} (${group.size}) ======")

            if (group.isEmpty()) {
                sb.appendLine("(нет книг)")
                continue
            }

            group.forEachIndexed { index, book ->
                sb.appendLine("${index + 1}. ${book.title} — ${book.author} [${book.genre}]")
                when (status) {
                    ReadingStatus.PLANNED -> sb.appendLine("   страниц: ${book.totalPages}")
                    ReadingStatus.READING -> sb.appendLine(
                        "   стр. ${book.currentPage} из ${book.totalPages}" +
                            " · начата ${DateUtils.formatDate(book.startDate)}"
                    )
                    ReadingStatus.FINISHED -> sb.appendLine(
                        "   ${book.totalPages} стр. · оценка: " +
                            (if (book.rating > 0) "${book.rating}/5" else "нет") +
                            " · читалась с ${DateUtils.formatDate(book.startDate)}" +
                            " по ${DateUtils.formatDate(book.finishDate)}"
                    )
                }
                if (book.isbn != null) sb.appendLine("   ISBN: ${book.isbn}")
                if (book.notes.isNotBlank()) sb.appendLine("   Заметка: ${book.notes}")
            }
        }

        // ---------- Статистика ----------
        val pagesRead = books.sumOf {
            if (it.status == ReadingStatus.FINISHED) it.totalPages else it.currentPage
        }
        val ratedBooks = books.filter { it.rating > 0 }
        val avgRating = if (ratedBooks.isEmpty()) 0.0
        else ratedBooks.sumOf { it.rating }.toDouble() / ratedBooks.size
        val genres = books.groupBy { it.genre }
            .map { (genre, list) -> "$genre — ${list.size}" }
            .joinToString("; ")

        sb.appendLine()
        sb.appendLine("====== СТАТИСТИКА ======")
        sb.appendLine("Прочитано страниц: $pagesRead")
        sb.appendLine("Средняя оценка: " + (if (ratedBooks.isEmpty()) "—" else String.format(Locale.getDefault(), "%.1f", avgRating)))
        sb.appendLine("Книги по жанрам: $genres")

        return sb.toString()
    }

    /**
     * Записывает текст в файл, выбранный пользователем через SAF.
     * Возвращает true при успехе — экран покажет соответствующий Toast.
     */
    fun saveToUri(context: Context, uri: Uri, text: String): Boolean {
        return try {
            val stream = context.contentResolver.openOutputStream(uri) ?: return false
            stream.use { it.write(text.toByteArray(Charsets.UTF_8)) }
            true
        } catch (e: Exception) {
            false
        }
    }
}
