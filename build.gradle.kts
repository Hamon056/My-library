// Корневой build-файл: объявляет плагины для всех модулей проекта.
// Версии плагинов задаются в gradle/libs.versions.toml (version catalog).
// apply false = плагин подключается к корню, но применяется только в модулях.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
