package com.example.booklibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import com.example.booklibrary.viewmodel.AddBookViewModel

/** Быстрый выбор жанра: клик по чипу подставляет значение в поле. */
private val suggestedGenres = listOf(
    "Классика", "Фантастика", "Фэнтези", "Детектив",
    "Роман", "Антиутопия", "Наука", "История"
)

/**
 * Экран добавления книги (пункт 2 ТЗ).
 * Обязательные поля: название, автор, число страниц. Остальное — по желанию.
 * Статус выбирается чипами; даты по статусу проставит репозиторий.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBookScreen(onDone: () -> Unit) {
    val vm: AddBookViewModel = viewModel(factory = AddBookViewModel.Factory)

    // Состояния полей формы. remember { mutableStateOf(...) } — "память" композабла
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ReadingStatus.PLANNED) }
    var error by remember { mutableStateOf<String?>(null) }

    fun save() {
        // ---------- Валидация ----------
        val pages = totalPages.toIntOrNull()
        when {
            title.isBlank() || author.isBlank() ->
                error = "Заполните название и автора"
            pages == null || pages < 1 || pages > 100_000 ->
                error = "Укажите число страниц (от 1 до 100 000)"
            else -> {
                error = null
                vm.saveBook(
                    Book(
                        title = title.trim(),
                        author = author.trim(),
                        genre = genre.trim().ifEmpty { "Не указан" },
                        totalPages = pages,
                        isbn = isbn.trim().ifEmpty { null },
                        status = status
                    ),
                    onDone = onDone
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая книга") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()               // поле не прячется под клавиатурой
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Автор *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Жанр: поле + быстрые чипы ----------
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestedGenres.forEach { suggested ->
                    FilterChip(
                        selected = genre == suggested,
                        onClick = { genre = suggested },
                        label = { Text(suggested) }
                    )
                }
            }

            OutlinedTextField(
                value = totalPages,
                onValueChange = { totalPages = it.filter { ch -> ch.isDigit() } },
                label = { Text("Всего страниц *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN (необязательно)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Статус чтения ----------
            Text(
                text = "Статус",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReadingStatus.entries.forEach { candidate ->
                    FilterChip(
                        selected = status == candidate,
                        onClick = { status = candidate },
                        label = { Text(candidate.label) }
                    )
                }
            }

            // ---------- Ошибка валидации ----------
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = { save() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить книгу")
            }
        }
    }
}
