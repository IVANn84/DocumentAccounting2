### Здесь будут перечислены обновления проекта

06.08.2024
- реализована возможность добавлять адрес для пользователя с использованием external API(сервиса DaData), пока без фронтенда
- юзер вводит любое вменяемое описание своего местонахождения, dadata возвращает ответ, из которого во внутренню БД попадут: основные данные, широта и долгота, будет создано представление для вывода
- сделана конфигурация транзакций через @EnableTransactionManagement для работы с разными менеджерами транзакций
- вызов внешнего API закрыт во вложенную транзакцию, что позволяет вызывать эти методы без опаски полного отката других транзакций(что вызывают эту) и сохранения внутренних данных
- добавлена поддержка LIQUIBASE, начальная инициализация таблиц и данных отрефакторена на работу с LIQUIBASE
- инициализация первичных юзеров переделана через ApplicationListener в отдельной транзкции - по причине необходимости сразу шифровать пароль и хранить в БД только зашифрованный пароль

01.08.2024
- Полный рефакторинг бекенда, улучшение читаемости тестов, соблюдены принципы SOLID
- Убрано много грязного или ненужного кода
- Убраны комментарии
- Сделан java-doc на всю малоочевидную логику

29.07.2024
- настройка конфигурационных файлов (./k8s/*) для локального запуска в класстере minikube

25.07.2024
- полностью настроена работа с файлами(pdf, png, docx, xls и др.)
- сделан фронт для этого со всеми нужными элементами
- проект настроен для локального разработки и деплоймента

17.07.2024
- первичная сборка приложения
- работает вся аутентификация, фронт, есть только 1 рабочая сущность по теме - документы(дополнительные сущности - юзеры, типы документов, роли юзеров)
- базовый функционал УЖЕ ПРИСУТСВУЕТ
- грязный код, много комментариев, есть проблемы с SOLID