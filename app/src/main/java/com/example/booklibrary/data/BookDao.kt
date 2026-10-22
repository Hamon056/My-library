package com.example.booklibrary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) — здесь описаны все SQL-запросы к таблице книг.
 * Реализацию интерфейса Room генерирует на этапе компиляции (плагин KSP):
 * мы пишем только аннотации и SQL, а Room создаёт класс с готовым кодом.
 *
 * suspend — функция вызывается из корутины и НЕ блокирует интерфейс.
 * Flow    — реактивный поток: Room сам пришлёт новый список в UI,
 *           как только кто-то изменит таблицу. Никаких ручных "обнови список".
 */
@Dao
interface BookDao {

    /** Все книги, отсортированные по названию (группировку делает ViewModel). */
    @Query("SELECT * FROM books ORDER BY title")
    fun getAllBooks(): Flow<List<Book>>

    /** Разовое получение книги по id (например, перед изменением). */
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Int): Book?

    /** Реактивный поток одной книги — источник данных для экрана деталей. */
    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookFlow(id: Int): Flow<Book?>

    /** Добавление книги. autoGenerate = true вернёт новый id. */
    @Insert
    suspend fun insert(book: Book): Long

    /** Обновление существующей книги (Room находит её по primary key). */
    @Update
    suspend fun update(book: Book)

    /** Удаление книги. */
    @Delete
    suspend fun delete(book: Book)

    /** Количество книг в базе — чтобы решить, наполнять ли её демо-данными. */
    @Query("SELECT COUNT(*) FROM books")
    suspend fun count(): Int
}
