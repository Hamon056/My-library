package com.example.booklibrary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.booklibrary.ui.theme.StarGold

/**
 * Рейтинг звёздами 1-5 (пункт 4 ТЗ).
 *
 * [enabled] = false — "только посмотреть" (карточка в списке);
 * [onRatingChange] != null — звёзды кликабельны (экран деталей).
 */
@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp,
    enabled: Boolean = false,
    onRatingChange: ((Int) -> Unit)? = null
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (star in 1..5) {
            val filled = star <= rating
            val clickableModifier = if (enabled && onRatingChange != null) {
                Modifier.clickable { onRatingChange(star) }
            } else {
                Modifier
            }

            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (filled) StarGold else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .size(starSize + 4.dp)
                    .then(clickableModifier)
                    .padding(2.dp)
            )
        }
    }
}
