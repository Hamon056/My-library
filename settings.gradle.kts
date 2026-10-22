// Настройки всего проекта: где брать плагины и библиотеки.
pluginManagement {
    repositories {
        google {
            content {
                // Плагины Android/Google/AndroidX ищем только в Google Maven
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()      // AndroidX, Room, Compose
        mavenCentral() // Kotlin, Coroutines, Vico
    }
}

rootProject.name = "BookLibrary"
include(":app")
