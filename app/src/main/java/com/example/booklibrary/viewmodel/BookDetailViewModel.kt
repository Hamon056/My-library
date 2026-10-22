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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel экрана деталей конкретной книги
 * (пункты ТЗ 3, 4, 5 — прогресс, оценка, статус).
 *
 * book — реактивный Flow ОДНОЙ книги: любое изменение (ползунок, звёзды,
 * статус) сохраняется в базу, Room присылает свежий объект, экран сам
 * перерисовывается. Хранить копию в локальной переменной не нужно.
 */
class BookDetailViewModel(
    private val repository: BookRepository,
    bookId: Int
) : ViewModel() {

    val book: StateFlow<Book?> = repository.getBookFlow(bookId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Флаг "книга удалена" — экран по нему закрывается сам. */
    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted.asStateFlow()

    /** Обновить прогресс: ползунок страниц. */
    fun updateProgress(newPage: Int) {
        val current = book.value ?: return
        viewModelScope.launch { repository.updateProgress(current, newPage) }
    }

    /** Сменить статус (Планирую / Читаю / Прочитано). */
    fun changeStatus(newStatus: ReadingStatus) {
        val current = book.value ?: return
        viewModelScope.launch { repository.changeStatus(current, newStatus) }
    }

    /** Поставить оценку (звёзды). */
    fun setRating(rating: Int) {
        val current = book.value ?: return
        viewModelScope.launch { repository.setRating(current, rating) }
    }

    /** Сохранить заметку/рецензию. */
    fun saveNotes(notes: String) {
        val current = book.value ?: return
        viewModelScope.launch { repository.updateNotes(current, notes) }
    }

    /** Удалить книгу: после записи в базу просим экран закрыться. */
    fun deleteBook() {
        val current = book.value ?: return
        viewModelScope.launch {
            repository.delete(current)
            _deleted.value = true
        }
    }

    companion object {
        /**
         * Фабрика с параметром (id книги): ViewModel нужна "своя" для каждой книги,
         * поэтому фабрику создаём функцией, а не константой.
         */
        fun factory(bookId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookLibraryApp
                BookDetailViewModel(app.repository, bookId)
            }
        }
    }
}
