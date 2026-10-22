package com.example.booklibrary.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Палитра "тёплая библиотека": коричневые тона книжной кожи,
 * бумажный фон и бирюзовый акцент. Пары цветов подобраны по
 * правилам Material 3: у каждого цвета есть парный "on-цвет",
 * чтобы текст оставался читаемым.
 */

// ---------- Светлая тема ----------
val PrimaryLight = Color(0xFF6D4C41)          // коричневая кожа переплёта
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFF6DBD2) // обложка пастельного оттенка
val OnPrimaryContainerLight = Color(0xFF381F18)

val SecondaryLight = Color(0xFF00695C)        // бирюзовый акцент
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFB4E6DF)
val OnSecondaryContainerLight = Color(0xFF00201B)

val TertiaryLight = Color(0xFF7A5900)         // тёмное золото (звёзды рейтинга)
val BackgroundLight = Color(0xFFFBF5EF)       // тёплая бумага
val OnBackgroundLight = Color(0xFF221B18)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF221B18)
val SurfaceVariantLight = Color(0xFFF0E4DD)
val OnSurfaceVariantLight = Color(0xFF52443E)
val OutlineLight = Color(0xFF84746D)

// ---------- Тёмная тема ----------
val PrimaryDark = Color(0xFFE8BCAF)
val OnPrimaryDark = Color(0xFF442A21)
val PrimaryContainerDark = Color(0xFF5C3B31)
val OnPrimaryContainerDark = Color(0xFFFFDAD2)

val SecondaryDark = Color(0xFF80D4C7)
val OnSecondaryDark = Color(0xFF003731)
val SecondaryContainerDark = Color(0xFF005048)
val OnSecondaryContainerDark = Color(0xFF9CF1E4)

val TertiaryDark = Color(0xFFE9C34A)
val BackgroundDark = Color(0xFF1A1311)
val OnBackgroundDark = Color(0xFFF0E4DD)
val SurfaceDark = Color(0xFF1A1311)
val OnSurfaceDark = Color(0xFFF0E4DD)
val SurfaceVariantDark = Color(0xFF52443E)
val OnSurfaceVariantDark = Color(0xFFD7C2BA)
val OutlineDark = Color(0xFF9F8D85)

// ---------- Статусы чтения (одинаковы в обеих темах) ----------
val StatusPlanned = Color(0xFF9E9E9E)   // серый: книга ждёт своей очереди
val StatusReading = Color(0xFF1E88E5)   // синий: чтение в процессе
val StatusFinished = Color(0xFF43A047)  // зелёный: прочитано

val StarGold = Color(0xFFFFB300)        // золотые звёзды оценки
