# BookLibrary — Моя библиотека

Учебное Android-приложение на Kotlin + Jetpack Compose для учёта прочитанных
и планируемых к прочтению книг. Данные хранятся в полноценной базе
(SQLite через Room), всё реализовано по ТЗ: список с группировкой, добавление,
прогресс чтения, оценки звёздами, статусы, поиск, статистика, график
активности (Vico) и экспорт в текстовый файл.

## Быстрый старт

1. Установите **Android Studio Ladybug (2024.2.1) или новее**.
2. Распакуйте этот архив.
3. **File → Open** → выберите папку `BookLibrary` → **Trust Project**.
4. Дождитесь Gradle Sync (первый раз скачивает зависимости — нужен интернет).
5. Запустите ▶ на эмуляторе (Device Manager → Create Device → Pixel 8, API 35)
   или на своём телефоне (USB-отладка).

Подробная инструкция с пояснением каждого файла — в PDF-гайде к проекту.

## Структура (по слоям MVVM)

```
app/src/main/java/com/example/booklibrary/
├── BookLibraryApp.kt        # Application: единые экземпляры БД и репозитория
├── MainActivity.kt          # Единственная Activity, весь UI — Compose
├── data/                    # Слой данных (Room)
│   ├── Book.kt              #   сущность + конвертер enum
│   ├── BookDao.kt           #   SQL-запросы
│   ├── BookDatabase.kt      #   база (Singleton)
│   ├── ReadingStatus.kt     #   статусы: Планирую / Читаю / Прочитано
│   └── DemoData.kt          #   10 демо-книг для первого запуска
├── repository/
│   └── BookRepository.kt    # бизнес-логика статусов и дат
├── viewmodel/               # MVVM-модели экранов
├── ui/
│   ├── theme/               # палитра "тёплая библиотека", светлая/тёмная
│   ├── components/          # RatingBar, BookCard
│   ├── navigation/          # маршруты и нижняя панель
│   └── screens/             # 4 экрана: список, добавление, детали, статистика
└── util/                    # форматирование дат, экспорт в файл
```

## Стек

Kotlin 2.0 · Jetpack Compose (BOM 2024.12) · Room 2.6 · Navigation Compose ·
Coroutines/Flow · MVVM · Vico 2.0 (график) · minSdk 24 / targetSdk 35
