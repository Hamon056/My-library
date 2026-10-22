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
import kotlinx.coroutines.launch

/**
 * ViewModel экрана добавления книги (пункт 2 ТЗ).
 * Сознательно простая: валидация полей — задача формы (UI),
 * а вот проставление дат по статусу делает репозиторий.
 */
class AddBookViewModel(private val repository: BookRepository) : ViewModel() {

    /**
     * Сохраняет книгу и вызывает [onDone] по завершении — экран в этот
     * момент закрывается и возвращается к списку.
     */
    fun saveBook(book: Book, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.insert(book)
            onDone()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookLibraryApp
                AddBookViewModel(app.repository)
            }
        }
    }
}
