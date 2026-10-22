package com.example.booklibrary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booklibrary.BookLibraryApp
import com.example.booklibrary.data.Book
import com.example.booklibrary.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel экрана библиотеки (пункты ТЗ 1, 7, 10).
 *
 * ViewModel переживает поворот экрана: список книг и строка поиска не
 * сбрасываются при перевороте. Все обращения к базе — через репозиторий.
 *
 * stateIn(...) превращает холодный Flow в горячий StateFlow:
 *   - WhileSubscribed(5000): поток активен, пока экран на экране
 *     (+5 секунд запас на поворот конфигурации);
 *   - emptyList — значение до первого прихода данных из базы.
 */
class BookListViewModel(private val repository: BookRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /**
     * Список книг = поток из базы + фильтр поиска (пункт 7 ТЗ).
     * combine пересобирает результат при изменении ЛЮБОГО источника:
     * добавили книгу в базу или ввели символ в поиск — список обновится.
     *
     * (Альтернатива — SQL-запрос с LIKE в DAO; для учебного проекта
     * фильтр в памяти нагляднее, и он охватывает группировку статусов.)
     */
    val books: StateFlow<List<Book>> =
        combine(repository.allBooks, _query) { list, q ->
            if (q.isBlank()) list
            else list.filter {
                it.title.contains(q, ignoreCase = true) ||
                    it.author.contains(q, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    init {
        // Первый запуск: наполняем пустую базу демо-книгами
        viewModelScope.launch { repository.seedDemoDataIfEmpty() }
    }

    companion object {
        /** Фабрика: как создать ViewModel, когда дойдёт очередь (лениво и один раз). */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookLibraryApp
                BookListViewModel(app.repository)
            }
        }
    }
}
