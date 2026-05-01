# Task Time Tracker API
> REST-сервис для учёта рабочего времени сотрудников. Позволяет управлять задачами и фиксировать временные отрезки, которые сотрудники тратят на их выполнение.

## Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Язык программатирования | Java | 25 (совместим с 17+) |
| Фреймворк | Spring Boot | 3.2.5 |
| ORM / Маппер | MyBatis | 3.0.3 |
| Аутентификация | JWT + Spring Security | 0.12.5 |
| Документация API | SpringDoc OpenAPI | 2.5.0 |
| БД (разработка) | H2 | 2.2.224 |
| БД (интеграционные тесты) | PostgreSQL (Testcontainers) | 15-alpine |
| Сборка | Maven | 3.9.11 |
| Тестирование | JUnit 5, Mockito, Testcontainers | — |

---

## Архитектура проекта

Проект построен по принципам **чистой архитектуры** и **разделения ответственности**:

```
task-time-tracker/
├── src/main/java/.../task_time_tracker/
│   ├── config/                    # Конфигурации (Security, OpenAPI, WebMvc)
│   ├── controller/                # REST контроллеры
│   ├── dto/                       # Data Transfer Objects с валидацией
│   ├── exception/                 # Глобальный обработчик исключений
│   ├── mapper/                    # MyBatis мапперы для работы с БД
│   ├── model/                     # Сущности базы данных
│   ├── security/                  # JWT сервисы и фильтры
│   ├── service/                   # Бизнес-логика
│   └── validation/                # Кастомные валидаторы
├── src/test/java/.../
│   ├── BaseIntegrationTest.java   # Базовый класс с Testcontainers
│   ├── controller/                # Тесты контроллеров
│   ├── service/                   # Тесты сервисов
│   ├── mapper/                    # Тесты мапперов (Unit + IT)
│   ├── security/                  # Тесты безопасности
│   └── validation/                # Тесты валидаторов
├── src/main/resources/
│   ├── application.properties     # Настройки приложения
│   ├── schema.sql                 # Схема БД + тестовый пользователь
│   └── mapper/*.xml               # MyBatis SQL мапперы
└── pom.xml
```

## Установка и запуск

## Требования

- Java 17 или выше
- Maven 3.8+
- Docker (только для запуска интеграционных тестов)

## Запуск приложения

```bash
# Клонировать репозиторий
git clone https://github.com/your-repo/task-time-tracker.git
cd task-time-tracker

# Собрать проект
mvn clean compile

# Запустить приложение
mvn spring-boot:run
```

Приложение запустится на `http://localhost:8080`

## Данные для входа (dev)

```
Логин: admin
Пароль: admin123
```

*Пользователь создаётся автоматически при старте через `schema.sql`*

## Остановка приложения

```
Ctrl + C
```
---

## Тестирование

<img width="813" height="226" alt="image" src="https://github.com/user-attachments/assets/8d907d72-cd77-4922-9226-e95d50d1b9da" />


### Запуск всех тестов

```bash
mvn test
```

### Запуск Unit-тестов (быстрые, на H2)

```bash
mvn test -Dtest=*Test
```

### Запуск интеграционных тестов (Testcontainers, PostgreSQL)

```bash
mvn test -Dtest=*IT
```

### Результаты

```
[INFO] Tests run: 57, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

##Документация API

### Swagger UI (интерактивная документация)

После запуска приложения откройте в браузере:

```
http://localhost:8080/swagger-ui.html
```
<img width="1236" height="1044" alt="image" src="https://github.com/user-attachments/assets/3fe0f532-610d-42ad-aed1-c17fb3278055" />

<details>
<summary><b>Как работать со Swagger UI</b></summary>

1. **Получить JWT токен:**
    - Метод: `POST /auth/login`
    - Тело: `{"username": "admin", "password": "admin123"}`
    - Скопируйте полученный `token`

2. **Авторизоваться:**
    - Нажмите кнопку **"Authorize"** (справа сверху)
    - Введите: `Bearer {ваш_токен}`
    - Нажмите **"Authorize"**

3. **Тестировать эндпоинты:**
    - Выберите нужный эндпоинт
    - Нажмите **"Try it out"**
    - Заполните параметры
    - Нажмите **"Execute"**
</details>
---

### Эндпоинты API

<details>
<summary><b>Authentication (аутентификация)</b></summary>

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/auth/login` | Получить JWT токен |
| POST | `/auth/logout` | Выход из системы |

**Пример запроса:**
```json
POST /auth/login
{
    "username": "admin",
    "password": "admin123"
}
```

**Пример ответа:**
```json
{
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "username": "admin",
    "role": "ADMIN"
}
```
</details>

<details>
<summary><b>Tasks (управление задачами)</b></summary>

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/tasks` | Создать задачу |
| GET | `/api/tasks/{id}` | Получить задачу по ID |
| PATCH | `/api/tasks/{id}/status` | Изменить статус задачи |

**Примеры:**
```json
POST /api/tasks
Authorization: Bearer <token>
{
    "name": "Разработка API",
    "description": "Создать REST-сервис",
    "status": "NEW"
}

PATCH /api/tasks/1/status
{
    "status": "IN_PROGRESS"
}
```
</details>

<details>
<summary><b>Time Records (учёт времени)</b></summary>

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/time-records` | Создать запись о времени |
| GET | `/api/time-records/employee/{employeeId}` | Получить записи сотрудника за период |

**Примеры:**
```json
POST /api/time-records
Authorization: Bearer <token>
{
    "employeeId": 1,
    "taskId": 1,
    "startTime": "2024-06-01T09:00:00",
    "endTime": "2024-06-01T12:30:00",
    "description": "Разработка функционала"
}

GET /api/time-records/employee/1?startDate=2024-06-01T00:00:00&endDate=2024-06-30T23:59:59
```
</details>

---

## Testcontainers (интеграционные тесты)

Для проверки реальной работы с PostgreSQL в тестах используется Testcontainers:

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
class TimeRecordMapperIT extends BaseIntegrationTest {
    
    @Test
    void insert_ShouldGenerateId() {
        // Тест выполняется на реальной PostgreSQL в Docker!
    }
}
```

**Особенности:**
- Автоматически поднимает PostgreSQL контейнер
- Каждый тест запускается в изолированной транзакции
- Контейнер переиспользуется между запусками (`withReuse(true)`)
- Для работы нужен установленный Docker

---

## JWT Аутентификация

**Как это работает:**
1. Пользователь отправляет логин/пароль на `/auth/login`
2. Сервер проверяет учётные данные и возвращает JWT токен
3. Клиент добавляет токен в заголовок: `Authorization: Bearer <token>`
4. `JwtAuthenticationFilter` проверяет токен для каждого защищённого запроса
---

## Обработка исключений

Глобальный обработчик (`GlobalExceptionHandler`) обрабатывает:

| Исключение | HTTP статус | Ситуация |
|------------|-------------|----------|
| `EntityNotFoundException` | 404 | Сущность не найдена в БД |
| `InvalidStatusTransitionException` | 400 | Недопустимый переход статуса (DONE → ...) |
| `MethodArgumentNotValidException` | 400 | Ошибка валидации DTO |
| `ConstraintViolationException` | 400 | Нарушение SQL ограничений |
| `AuthenticationException` | 401 | Отсутствует или неверный JWT |
| `Exception` | 500 | Непредвиденная ошибка |

---

## 📊 Статусная модель задач

```
NEW ──────► IN_PROGRESS ──────► DONE
  │              │
  └──────────────┘
```

- `NEW` → `IN_PROGRESS` ✅
- `IN_PROGRESS` → `NEW` ✅
- `IN_PROGRESS` → `DONE` ✅
- `DONE` → (любой) ❌ запрещён

---

## Postman коллекция

**Файл:** [`postman/Task_Time_Tracker_API.postman_collection.json`](postman/Task_Time_Tracker_API.postman_collection.json)

### Импорт коллекции

1. Скачайте файл коллекции из репозитория
2. Откройте **Postman**
3. Нажмите **Import** → **Upload Files**
4. Выберите скачанный JSON файл
5. Нажмите **Import**

### Тестирование API

1. Выполните запрос `POST /auth/login` с тестовыми данными
2. Токен автоматически сохранится в переменную `{{token}}`
3. Тестируйте защищённые эндпоинты — токен будет подставляться автоматически

---

## Контакты

**Автор:** KsushkaPushka

**GitHub:** [https://github.com/KsushkaPushka](https://github.com/KsushkaPushka)

**Тестовое задание для компании:** CDEK

