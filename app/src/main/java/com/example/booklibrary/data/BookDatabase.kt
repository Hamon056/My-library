package com.example.booklibrary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Точка входа в базу данных Room.
 *
 * "Полноценное подключение БД" выглядит так: Room — официальный
 * ORM-слой Android поверх SQLite. При первом обращении создаётся
 * реальный файл базы book_library.db во внутренней памяти приложения
 * (/data/data/com.example.booklibrary/databases/), который сохраняется
 * между запусками — данные не пропадут после закрытия приложения.
 *
 * Экземпляр базы дорогой в создании, поэтому держим ровно один
 * на всё приложение (паттерн Singleton):
 *  - @Volatile      — гарантия, что изменения переменной видны всем потокам;
 *  - synchronized   — два потока не создадут базу одновременно;
 *  - ?:             — второй вызов вернёт уже готовый экземпляр.
 */
@Database(
    entities = [Book::class],   // Все таблицы базы (пока одна — books)
    version = 1,                // Версия схемы: при изменении структуры нужно поднять
    exportSchema = false        // Историю схем для миграций не сохраняем
)
@TypeConverters(ReadingStatusConverter::class)
abstract class BookDatabase : RoomDatabase() {

    /** Room сгенерирует реализацию этого метода на этапе компиляции. */
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,          // Контекст приложения (не Activity!)
                    BookDatabase::class.java,
                    "book_library.db"                    // Имя файла SQLite-базы
                ).build().also { INSTANCE = it }
            }
    }
}
