package com.example.booklibrary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.booklibrary.data.Book
import com.example.booklibrary.data.ReadingStatus
import com.example.booklibrary.ui.theme.StatusFinished
import com.example.booklibrary.ui.theme.StatusPlanned
import com.example.booklibrary.ui.theme.StatusReading

/**
 * Карточка книги в общем списке (пункт 1 ТЗ).
 * Показывает главное с одного взгляда: название, автора, жанр,
 * прогресс чтения и оценку. Клик по карточке открывает экран деталей.
 */
@Composable
fun BookCard(book: Book, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Название + автор
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            // Жанр + статус с цветной точкой
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = book.genre,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.size(10.dp))
                StatusDot(status = book.status)
                Spacer(Modifier.size(6.dp))
                Text(
                    text = book.status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor(book.status)
                )
            }

            // Прогресс чтения: полоска + "стр. X из Y"
            if (book.status != ReadingStatus.PLANNED && book.totalPages > 0) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { book.progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = statusColor(book.status)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "стр. ${book.currentPage} из ${book.totalPages}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Оценка (если проставлена)
            if (book.rating > 0) {
                Spacer(Modifier.height(6.dp))
                RatingBar(rating = book.rating, starSize = 14.dp)
            }
        }
    }
}

/** Цвет статуса: серый / синий / зелёный (определены в палитре темы). */
fun statusColor(status: ReadingStatus) = when (status) {
    ReadingStatus.PLANNED -> StatusPlanned
    ReadingStatus.READING -> StatusReading
    ReadingStatus.FINISHED -> StatusFinished
}

/** Маленький цветной кружок перед названием статуса. */
@Composable
private fun StatusDot(status: ReadingStatus) {
    Surface(
        shape = RoundedCornerShape(50),
        color = statusColor(status),
        modifier = Modifier.size(8.dp)
    ) {}
}
