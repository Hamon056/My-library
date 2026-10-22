package com.example.booklibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booklibrary.ui.theme.StatusFinished
import com.example.booklibrary.ui.theme.StarGold
import com.example.booklibrary.util.DateUtils
import com.example.booklibrary.viewmodel.DayActivity
import com.example.booklibrary.viewmodel.StatsViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

/**
 * Экран статистики (пункты 8 и 9 ТЗ): карточки-показатели,
 * столбчатый график активности чтения и распределение по жанрам.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {
    val vm: StatsViewModel = viewModel(factory = StatsViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Статистика чтения") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Карточки-показатели ----------
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Книг всего", state.totalBooks.toString(), Modifier.weight(1f))
                StatCard("Прочитано", state.finished.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Читаю", state.reading.toString(), Modifier.weight(1f))
                StatCard("В планах", state.planned.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Страниц прочитано",
                    value = state.pagesRead.toString(),
                    modifier = Modifier.weight(1f),
                    valueColor = StatusFinished
                )
                StatCard(
                    label = "Средняя оценка",
                    value = if (state.averageRating > 0.0)
                        String.format(java.util.Locale.getDefault(), "%.1f", state.averageRating)
                    else "—",
                    modifier = Modifier.weight(1f),
                    valueColor = StarGold
                )
            }

            // ---------- График активности (пункт 9 ТЗ) ----------
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Активность чтения — страниц в день",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "За последние 14 дней",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    DailyActivityChart(
                        data = state.dailyActivity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            // ---------- Книги по жанрам (пункт 8 ТЗ) ----------
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Книги по жанрам",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    val maxCount = state.genreStats.maxOfOrNull { it.count } ?: 1
                    state.genreStats.forEach { genreStat ->
                        Column {
                            Text(
                                text = "${genreStat.genre} · ${genreStat.count}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { genreStat.count.toFloat() / maxCount },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (state.genreStats.isEmpty()) {
                        Text(
                            "Пока нет книг",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/** Компактная карточка-показатель. */
@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Столбчатый график (библиотека Vico, официально рекомендованная для Compose).
 *
 * Данные кладутся в CartesianChartModelProducer через runTransaction —
 * Vico сама анимирует переход между старыми и новыми значениями.
 * startAxis — вертикальная ось (числа страниц), bottomAxis — дни,
 * подписи дней форматирует CartesianValueFormatter (индекс -> "7.10").
 */
@Composable
private fun DailyActivityChart(data: List<DayActivity>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(
                    x = data.indices.map { it.toFloat() },
                    y = data.map { it.pages }
                )
            }
        }
    }

    val dayFormatter = remember(data) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, data.size - 1)
            DateUtils.shortDay(data[index].dayStart)
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = dayFormatter)
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
