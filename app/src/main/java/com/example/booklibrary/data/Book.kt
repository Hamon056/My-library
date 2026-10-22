package com.example.booklibrary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * Сущность "Книга" — главная таблица базы данных.
 *
 * По этому классу Room создаст таблицу "books" со столбцами,
 * повторяющими поля data-класса (модель данных из ТЗ).
 *
 * Даты хранятся как Long (миллисекунды с 01.01.1970) — стандартный
 * способ: SQLite не имеет типа "дата", а Long легко сравнивать и сортировать.
 * null у startDate/finishDate означает "книга ещё не начата/не закончена".
 */
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                    // Room сам присвоит id при вставке (0 = "сгенерируй")
    val title: String,                  // Название книги
    val author: String,                 // Автор
    val genre: String,                  // Жанр
    val totalPages: Int,                // Всего страниц
    val currentPage: Int = 0,           // Текущая страница (0 — не начата)
    val rating: Int = 0,                // Оценка 1-5 (0 — не оценена)
    val status: ReadingStatus = ReadingStatus.PLANNED,  // Статус чтения
    val startDate: Long? = null,        // Дата начала чтения
    val finishDate: Long? = null,       // Дата окончания
    val notes: String = "",             // Заметки/рецензия
    val isbn: String? = null            // ISBN (опционально)
) {
    /**
     * Прогресс чтения от 0.0 до 1.0 — удобно кормить индикатору в UI.
     * Вычисляемое свойство: в таблицу не попадает, считается на лету.
     */
    val progress: Float
        get() = if (totalPages <= 0) 0f
        else (currentPage.toFloat() / totalPages).coerceIn(0f, 1f)
}

/**
 * Конвертеры учат Room работать с типами, которых нет в SQLite.
 * Статус храним строкой ("READING") — так базу удобно читать в отладчике.
 */
class ReadingStatusConverter {

    @TypeConverter
    fun fromStatus(status: ReadingStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ReadingStatus =
        // Если в базе вдруг окажется неизвестное значение — считаем книгу запланированной
        ReadingStatus.entries.firstOrNull { it.name == value } ?: ReadingStatus.PLANNED
}
