package com.example.booklibrary.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booklibrary.ui.screens.AddBookScreen
import com.example.booklibrary.ui.screens.BookDetailScreen
import com.example.booklibrary.ui.screens.BookListScreen
import com.example.booklibrary.ui.screens.StatsScreen

/**
 * Все маршруты приложения в одном месте.
 *  library / stats      — "корни" навигации, между ними переключается нижняя панель;
 *  add_book             — экран добавления (открывается с FAB);
 *  detail/{bookId}      — экран книги: bookId подставляется в строку маршрута.
 */
object Routes {
    const val LIBRARY = "library"
    const val STATS = "stats"
    const val ADD_BOOK = "add_book"
    const val DETAIL = "detail/{bookId}"

    fun detail(bookId: Int) = "detail/$bookId"
}

/** Описание вкладки нижней панели: маршрут, иконка, подпись. */
private data class TopLevelTab(val route: String, val icon: ImageVector, val label: String)

private val tabs = listOf(
    TopLevelTab(Routes.LIBRARY, Icons.Filled.CollectionsBookmark, "Библиотека"),
    TopLevelTab(Routes.STATS, Icons.Filled.BarChart, "Статистика")
)

/**
 * Корень интерфейса: нижняя панель + NavHost, который "перекладывает"
 * экраны (composable) друг за другом по мере навигации.
 *
 * Панель видна только на корневых вкладках: на экранах добавления и
 * деталей она скрыта — там пользователь находится "внутри" задачи.
 */
@Composable
fun AppRoot() {
    val navController = rememberNavController()

    // Наблюдаем текущий экран, чтобы подсветить вкладку и решить, показывать ли панель
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == Routes.LIBRARY || currentRoute == Routes.STATS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // Возвращаемся к корню, чтобы бэкстек не разрастался
                                    popUpTo(Routes.LIBRARY) { saveState = true }
                                    launchSingleTop = true   // не открывать вкладку дважды
                                    restoreState = true      // помнить состояние вкладки
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIBRARY,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LIBRARY) {
                BookListScreen(
                    onAddBook = { navController.navigate(Routes.ADD_BOOK) },
                    onOpenBook = { id -> navController.navigate(Routes.detail(id)) }
                )
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.ADD_BOOK) {
                AddBookScreen(onDone = { navController.popBackStack() })
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { entry ->
                val bookId = entry.arguments?.getInt("bookId") ?: 0
                BookDetailScreen(bookId = bookId, onBack = { navController.popBackStack() })
            }
        }
    }
}
