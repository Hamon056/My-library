package com.example.booklibrary

import android.app.Application
import com.example.booklibrary.data.BookDatabase
import com.example.booklibrary.repository.BookRepository

/**
 * Класс приложения (зарегистрирован в AndroidManifest, android:name).
 *
 * Хранит единственные на всё приложение экземпляры базы данных и репозитория.
 * Это простейшая замена DI-фреймворку (Hilt/Koin): для проекта такого размера
 * ручной "внедренец" понятнее и без магии.
 *
 * by lazy = создание откладывается до первого обращения и выполняется один раз.
 */
class BookLibraryApp : Application() {

    val database: BookDatabase by lazy { BookDatabase.getDatabase(this) }

    val repository: BookRepository by lazy { BookRepository(database.bookDao()) }
}
