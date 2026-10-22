package com.example.booklibrary.repository

import com.example.booklibrary.data.Book
import com.example.booklibrary.data.BookDao
import com.example.booklibrary.data.DemoData
import com.example.booklibrary.data.ReadingStatus
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий — единая точка доступа к данным для всех ViewModel.
 *
 * Зачем он нужен: ViewModel не должна знать про SQL и Room, а правила
 * "какие даты проставлять при смене статуса" — это бизнес-логика,
 * которую нельзя раскидывать по экранам. Один источник правды: что бы
 * ни меняло книгу (прогресс, статус, оценка), логика живёт только здесь.
 */
class BookRepository(private val dao: BookDao) {

    /** Реактивный поток всех книг: UI обновится сам при любом изменении базы. */
    val allBooks: Flow<List<Book>> = dao.getAllBooks()

    fun getBookFlow(id: Int): Flow<Book?> = dao.getBookFlow(id)

    suspend fun getBookById(id: Int): Book? = dao.getBookById(id)

    /**
     * Добавление новой книги. Даты проставляем сразу:
     *  - "Читаю"      -> startDate = сейчас;
     *  - "Прочитано"  -> обе даты + currentPage = totalPages.
     */
    suspend fun insert(book: Book) {
        val now = System.currentTimeMillis()
        val prepared = when (book.status) {
            ReadingStatus.READING -> book.copy(startDate = book.startDate ?: now)
            ReadingStatus.FINISHED -> book.copy(
                startDate = book.startDate ?: now,
                finishDate = book.finishDate ?: now,
                currentPage = book.totalPages
            )
            ReadingStatus.PLANNED -> book
        }
        dao.insert(prepared)
    }

    /**
     * Обновление прогресса чтения (пункт 3 ТЗ) + авто-смена статуса:
     *  - открыл книгу (страница > 0)          -> статус стал READING, запомнили дату старта;
     *  - дочитал (страница >= totalPages)     -> статус FINISHED, дата финиша, currentPage = totalPages;
     *  - вернул прогресс на 0                 -> снова PLANNED, даты сброшены.
     */
    suspend fun updateProgress(book: Book, newPage: Int) {
        val page = newPage.coerceIn(0, book.totalPages)
        val newStatus = when {
            page >= book.totalPages -> ReadingStatus.FINISHED
            page > 0 -> ReadingStatus.READING
            else -> ReadingStatus.PLANNED
        }
        val now = System.currentTimeMillis()

        val updated = book.copy(
            currentPage = page,
            status = newStatus,
            startDate = if (book.startDate == null && newStatus != ReadingStatus.PLANNED) now
            else if (newStatus == ReadingStatus.PLANNED) null
            else book.startDate,
            finishDate = if (newStatus == ReadingStatus.FINISHED && book.finishDate == null) now
            else if (newStatus != ReadingStatus.FINISHED) null
            else book.finishDate
        )
        dao.update(updated)
    }

    /**
     * Ручная смена статуса (пункт 5 ТЗ: Планирую -> Читаю -> Прочитано):
     *  - PLANNED  — книга ещё не начиналась, даты сбрасываем;
     *  - READING  — фиксируем дату старта (если её не было), финиш отменяем;
     *  - FINISHED — отмечаем все страницы прочитанными и ставим обе даты.
     */
    suspend fun changeStatus(book: Book, newStatus: ReadingStatus) {
        val now = System.currentTimeMillis()
        val updated = when (newStatus) {
            ReadingStatus.PLANNED -> book.copy(status = newStatus, startDate = null, finishDate = null)
            ReadingStatus.READING -> book.copy(
                status = newStatus,
                startDate = book.startDate ?: now,
                finishDate = null
            )
            ReadingStatus.FINISHED -> book.copy(
                status = newStatus,
                currentPage = book.totalPages,
                startDate = book.startDate ?: now,
                finishDate = now
            )
        }
        dao.update(updated)
    }

    /** Оценка книги звёздами 1-5 (пункт 4 ТЗ). */
    suspend fun setRating(book: Book, rating: Int) {
        dao.update(book.copy(rating = rating.coerceIn(0, 5)))
    }

    /** Сохранение заметки/рецензии. */
    suspend fun updateNotes(book: Book, notes: String) {
        dao.update(book.copy(notes = notes.trim()))
    }

    suspend fun delete(book: Book) = dao.delete(book)

    /**
     * Наполняем базу демо-книгами, но ТОЛЬКО при первом запуске:
     * если пользователь уже что-то добавил — базу не трогаем.
     */
    suspend fun seedDemoDataIfEmpty() {
        if (dao.count() == 0) {
            DemoData.createBooks().forEach { dao.insert(it) }
        }
    }
}
