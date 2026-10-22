package com.example.booklibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import com.example.booklibrary.ui.components.RatingBar
import com.example.booklibrary.ui.components.statusColor
import com.example.booklibrary.util.DateUtils
import com.example.booklibrary.viewmodel.BookDetailViewModel

/**
 * Экран деталей книги: прогресс чтения (ползунок), оценка звёздами,
 * смена статуса, заметки и удаление. Все действия сразу сохраняются в базу.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(bookId: Int, onBack: () -> Unit) {
    val vm: BookDetailViewModel = viewModel(
        key = "detail_$bookId",
        factory = BookDetailViewModel.factory(bookId)
    )
    val book by vm.book.collectAsStateWithLifecycle()
    val deleted by vm.deleted.collectAsStateWithLifecycle()

    // После удаления закрываем экран сами
    LaunchedEffect(deleted) {
        if (deleted) onBack()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали книги") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить книгу")
                    }
                }
            )
        }
    ) { padding ->
        val current = book
        if (current == null) {
            // Книга ещё грузится из базы (или уже удалена)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        BookDetailContent(
            book = current,
            padding = padding,
            onUpdateProgress = vm::updateProgress,
            onChangeStatus = vm::changeStatus,
            onSetRating = vm::setRating,
            onSaveNotes = vm::saveNotes
        )
    }

    // ---------- Диалог подтверждения удаления ----------
    if (showDeleteDialog) {
        val title = book?.title ?: ""
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить книгу?") },
            text = { Text("«$title» будет удалена из библиотеки навсегда.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        vm.deleteBook()
                    }
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    padding: PaddingValues,
    onUpdateProgress: (Int) -> Unit,
    onChangeStatus: (ReadingStatus) -> Unit,
    onSetRating: (Int) -> Unit,
    onSaveNotes: (String) -> Unit
) {
    // Ползунок — локальное состояние экрана; в базу уходит по отпусканию пальца
    var sliderPage by remember(book.id) { mutableIntStateOf(book.currentPage) }
    // Заметка — тоже локально, чтобы редактирование не сбрасывалось при перерисовке
    var notes by remember(book.id) { mutableStateOf(book.notes) }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ---------- Шапка ----------
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = book.author,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = book.genre,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor(book.status).copy(alpha = 0.15f)
            ) {
                Text(
                    text = book.status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor(book.status),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        HorizontalDivider()

        // ---------- Статус (пункт 5 ТЗ) ----------
        Text(
            text = "Статус чтения",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReadingStatus.entries.forEach { candidate ->
                FilterChip(
                    selected = book.status == candidate,
                    onClick = { onChangeStatus(candidate) },
                    label = { Text(candidate.label) }
                )
            }
        }

        // ---------- Прогресс (пункт 3 ТЗ) ----------
        Text(
            text = "Прогресс чтения",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        LinearProgressIndicator(
            progress = { book.progress },
            modifier = Modifier.fillMaxWidth(),
            color = statusColor(book.status)
        )
        Text(
            text = "стр. ${book.currentPage} из ${book.totalPages} " +
                "(${(book.progress * 100).toInt()}%)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = sliderPage.toFloat(),
            onValueChange = { sliderPage = it.toInt() },
            onValueChangeFinished = { onUpdateProgress(sliderPage) },
            valueRange = 0f..book.totalPages.coerceAtLeast(1).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // ---------- Оценка (пункт 4 ТЗ) ----------
        Text(
            text = if (book.rating > 0) "Ваша оценка: ${book.rating} из 5"
            else "Поставьте оценку",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        RatingBar(
            rating = book.rating,
            starSize = 30.dp,
            enabled = true,
            onRatingChange = onSetRating
        )

        HorizontalDivider()

        // ---------- Информация ----------
        InfoRow("Начата:", DateUtils.formatDate(book.startDate))
        InfoRow("Завершена:", DateUtils.formatDate(book.finishDate))
        if (book.isbn != null) InfoRow("ISBN:", book.isbn ?: "")

        HorizontalDivider()

        // ---------- Заметки ----------
        Text(
            text = "Заметки и рецензия",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Что думаете о книге?") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onSaveNotes(notes) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить заметку")
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
