package com.example.booklibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.booklibrary.ui.navigation.AppRoot
import com.example.booklibrary.ui.theme.BookLibraryTheme

/**
 * Единственная Activity приложения.
 *
 * Вся классическая "Activity-разметка" из XML здесь не нужна:
 * setContent { ... } рисует интерфейс декларативно, средствами Jetpack Compose.
 * enableEdgeToEdge() растягивает контент под статус-бар (системные отступы
 * дальше корректно обрабатывают Scaffold и TopAppBar).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookLibraryTheme {
                AppRoot()
            }
        }
    }
}
