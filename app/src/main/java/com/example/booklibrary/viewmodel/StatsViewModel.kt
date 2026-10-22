package com.example.booklibrary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booklibrary.BookLibraryApp
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import com.example.booklibrary.repository.BookRepository
import com.example.booklibrary.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Данные для строки "Книги по жанрам". */
data class GenreStat(val genre: String, val count: Int)

/** Активность одного дня: сколько страниц "приходится" на этот день. */
data class DayActivity(val dayStart: Long, val pages: Float)

/** Всё, что нужно экрану статистики (пункт 8 ТЗ), собранное в один объект. */
data class StatsUiState(
    val totalBooks: Int = 0,
    val planned: Int = 0,
    val reading: Int = 0,
    val finished: Int = 0,
    val pagesRead: Int = 0,
    val averageRating: Double = 0.0,
    val genreStats: List<GenreStat> = emptyList(),
    val dailyActivity: List<DayActivity> = emptyList()
)

/**
 * ViewModel экрана статистики (пункты ТЗ 8 и 9).
 *
 * Все метрики считаются из ОДНОГО потока allBooks с помощью map():
 * добавили/изменили книгу — вся статистика и график пересчитались сами.
 *
 * Альтернатива из "мира SQL" — агрегатные запросы в DAO вида
 * SELECT genre, COUNT(*) FROM books GROUP BY genre. Здесь счёт в памяти:
 * данных немного, а вся математика видна в одном месте и легко читается.
 */
class StatsViewModel(repository: BookRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = repository.allBooks
        .map { books -> buildUiState(books) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    private fun buildUiState(books: List<Book>): StatsUiState {
        val ratedBooks = books.filter { it.rating > 0 }

        return StatsUiState(
            totalBooks = books.size,
            planned = books.count { it.status == ReadingStatus.PLANNED },
            reading = books.count { it.status == ReadingStatus.READING },
            finished = books.count { it.status == ReadingStatus.FINISHED },
            // Прочитанные страницы: у завершённых — вся книга, у читаемых — текущая позиция
            pagesRead = books.sumOf {
                if (it.status == ReadingStatus.FINISHED) it.totalPages else it.currentPage
            },
            averageRating = if (ratedBooks.isEmpty()) 0.0
            else ratedBooks.sumOf { it.rating }.toDouble() / ratedBooks.size,
            genreStats = books.groupBy { it.genre }
                .map { (genre, list) -> GenreStat(genre, list.size) }
                .sortedByDescending { it.count },
            dailyActivity = buildDailyActivity(books)
        )
    }

    /**
     * ГРАФИК ПРОГРЕССА (пункт 9 ТЗ): страницы в день за последние 14 дней.
     *
     * В модели данных нет журнала "сколько страниц читал в какой день",
     * поэтому используем честное упрощение: страницы каждой книги
     * распределяются РАВНОМЕРНО по периоду её чтения
     * (startDate -> finishDate, а для читаемых сейчас — до сегодняшнего дня).
     *
     * Хочешь точности — заведи таблицу reading_sessions и складывай туда
     * сессии чтения; функция ниже станет выборкой из неё (подробности в гайде).
     */
    private fun buildDailyActivity(books: List<Book>, days: Int = 14): List<DayActivity> {
        val dayMs = 86_400_000L
        val today = DateUtils.startOfToday()
        val now = System.currentTimeMillis()
        val result = ArrayList<DayActivity>(days)

        for (i in days - 1 downTo 0) {           // от 13 дней назад до сегодня
            val dayStart = today - i * dayMs
            val dayEnd = dayStart + dayMs
            var pages = 0f

            for (book in books) {
                val start = book.startDate ?: continue   // не начинали — не участвует
                val finish = book.finishDate ?: now      // читается сейчас — до сегодня
                if (finish <= start) continue

                // Страниц в день = объём книги, растянутый на весь период чтения
                val daysInPeriod = ((finish - start) / dayMs + 1).coerceAtLeast(1)
                val pagesPerDay = book.totalPages.toFloat() / daysInPeriod

                // День попадает в период чтения книги?
                if (dayEnd > start && dayStart <= finish) {
                    pages += pagesPerDay
                }
            }
            result.add(DayActivity(dayStart, pages))
        }
        return result
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookLibraryApp
                StatsViewModel(app.repository)
            }
        }
    }
}
