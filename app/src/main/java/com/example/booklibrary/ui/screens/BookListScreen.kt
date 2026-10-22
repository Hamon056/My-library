package com.example.booklibrary.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import com.example.booklibrary.ui.components.BookCard
import com.example.booklibrary.util.ExportUtils
import com.example.booklibrary.viewmodel.BookListViewModel

/**
 * Экран списка книг (пункт 1 ТЗ): книги сгруппированы по статусу —
 * сверху то, что читается, потом планы, внизу прочитанное.
 * Здесь же поиск (пункт 7) и кнопка экспорта в текстовый файл (пункт 10).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    onAddBook: () -> Unit,
    onOpenBook: (Int) -> Unit
) {
    val vm: BookListViewModel = viewModel(factory = BookListViewModel.Factory)
    val books by vm.books.collectAsStateWithLifecycle()
    val query by vm.query.collectAsStateWithLifecycle()
    val context = LocalContext.current

    /**
     * SAF-лаунчер "Создать файл": открывает системный диалог сохранения.
     * Сюда приходит Uri выбранного места — записываем туда отчёт.
     */
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            val report = ExportUtils.buildReport(vm.books.value)
            val saved = ExportUtils.saveToUri(context, uri, report)
            val message = if (saved) "Файл сохранён" else "Не удалось сохранить файл"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моя библиотека") },
                actions = {
                    IconButton(
                        onClick = { exportLauncher.launch("book_library_export.txt") }
                    ) {
                        Icon(
                            Icons.Filled.FileDownload,
                            contentDescription = "Экспорт в текстовый файл"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить книгу")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ---------- Поле поиска (пункт 7 ТЗ) ----------
            OutlinedTextField(
                value = query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по названию или автору") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { vm.onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Очистить поиск")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (books.isEmpty()) {
                EmptyState(query.isBlank())
            } else {
                BooksGroupedList(books = books, onOpenBook = onOpenBook)
            }
        }
    }
}

/** Список, сгруппированный по статусам: заголовок секции + карточки. */
@Composable
private fun BooksGroupedList(
    books: List<Book>,
    onOpenBook: (Int) -> Unit
) {
    // Порядок секций: "Читаю" наверху — это самое актуальное
    val sections = listOf(ReadingStatus.READING, ReadingStatus.PLANNED, ReadingStatus.FINISHED)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Нижний запас — чтобы FAB не перекрывал последнюю карточку
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        sections.forEach { status ->
            val group = books.filter { it.status == status }
            if (group.isNotEmpty()) {
                // Заголовок секции с количеством книг
                item(key = "header_$status") {
                    Text(
                        text = "${status.label} · ${group.size}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(group, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onClick = { onOpenBook(book.id) }
                    )
                }
            }
        }
    }
}

/** Заглушка для пустой библиотеки или пустого результата поиска. */
@Composable
private fun EmptyState(searching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (searching) "Ничего не найдено.\nПопробуйте изменить запрос."
            else "Библиотека пуста.\nНажмите +, чтобы добавить первую книгу.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
