package com.example.booklibrary.data

/**
 * Демо-книги для первого запуска (пользователь выбрал "с демо-книгами").
 *
 * Даты считаются ОТ текущего момента (не хардкод), поэтому график
 * активности за 14 дней всегда показывает живые данные: часть книг
 * читалась в последние дни, часть — давно, три ещё в процессе.
 *
 * Сеется один раз: только если база пуста (см. BookRepository.seedDemoDataIfEmpty).
 */
object DemoData {

    private const val DAY_MS = 86_400_000L // мс в сутках

    private fun daysAgo(days: Int): Long = System.currentTimeMillis() - days * DAY_MS

    fun createBooks(): List<Book> = listOf(

        // ---------- Прочитано ----------
        Book(
            title = "Мастер и Маргарита",
            author = "Михаил Булгаков",
            genre = "Классика",
            totalPages = 480, currentPage = 480, rating = 5,
            status = ReadingStatus.FINISHED,
            startDate = daysAgo(40), finishDate = daysAgo(14),
            notes = "Аннотации Понтия Пилата — отдельный шедевр. Советую читать не спеша, по главе в день.",
            isbn = "978-5-389-07691-6"
        ),
        Book(
            title = "Капитанская дочка",
            author = "Александр Пушкин",
            genre = "Классика",
            totalPages = 240, currentPage = 240, rating = 4,
            status = ReadingStatus.FINISHED,
            startDate = daysAgo(60), finishDate = daysAgo(45),
            notes = "Школьная классика, которая заходит уже совсем иначе."
        ),
        Book(
            title = "1984",
            author = "Джордж Оруэлл",
            genre = "Антиутопия",
            totalPages = 328, currentPage = 328, rating = 5,
            status = ReadingStatus.FINISHED,
            startDate = daysAgo(25), finishDate = daysAgo(5),
            notes = "Двойная мысль и телекраны актуальнее, чем когда-либо."
        ),
        Book(
            title = "Гарри Поттер и философский камень",
            author = "Джоан Роулинг",
            genre = "Фэнтези",
            totalPages = 432, currentPage = 432, rating = 4,
            status = ReadingStatus.FINISHED,
            startDate = daysAgo(20), finishDate = daysAgo(9)
        ),
        Book(
            title = "Три товарища",
            author = "Эрих Мария Ремарк",
            genre = "Роман",
            totalPages = 320, currentPage = 320, rating = 5,
            status = ReadingStatus.FINISHED,
            startDate = daysAgo(35), finishDate = daysAgo(21),
            notes = "Дружба, любовь и потеря. Один из самых честных романов XX века."
        ),

        // ---------- Читаю ----------
        Book(
            title = "Дюна",
            author = "Фрэнк Герберт",
            genre = "Фантастика",
            totalPages = 656, currentPage = 214, rating = 0,
            status = ReadingStatus.READING,
            startDate = daysAgo(6),
            notes = "Мир, политика и пески. Словарь терминов в конце сильно помогает."
        ),
        Book(
            title = "Преступление и наказание",
            author = "Фёдор Достоевский",
            genre = "Классика",
            totalPages = 672, currentPage = 150, rating = 0,
            status = ReadingStatus.READING,
            startDate = daysAgo(10)
        ),

        // ---------- Планирую ----------
        Book(
            title = "Этюд в багровых тонах",
            author = "Артур Конан Дойл",
            genre = "Детектив",
            totalPages = 256, rating = 0,
            status = ReadingStatus.PLANNED,
            isbn = "978-5-17-090451-2"
        ),
        Book(
            title = "Краткая история времени",
            author = "Стивен Хокинг",
            genre = "Наука",
            totalPages = 256, rating = 0,
            status = ReadingStatus.PLANNED,
            notes = "Обещал себе разобраться с чёрными дырами."
        ),
        Book(
            title = "Автостопом по галактике",
            author = "Дуглас Адамс",
            genre = "Фантастика",
            totalPages = 384, rating = 0,
            status = ReadingStatus.PLANNED
        )
    )
}
