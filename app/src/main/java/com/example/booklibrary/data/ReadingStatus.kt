package com.example.booklibrary.data

/**
 * Статус чтения книги (пункт 5 ТЗ: Планирую -> Читаю -> Прочитано).
 *
 * [label] — человекочитаемое название для отображения в интерфейсе.
 * В базу данных Room сохраняет статус как строку ("READING" и т.п.),
 * конвертация описана в [ReadingStatusConverter] (файл Book.kt).
 */
enum class ReadingStatus(val label: String) {
    PLANNED("Планирую"),
    READING("Читаю"),
    FINISHED("Прочитано")
}
