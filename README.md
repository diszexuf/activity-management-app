# Веб-приложение для управления рабочими активностями в течение суток

Приложение позволяет создавать и просматривать временные интервалы активности (работа/перерыв) 
в рамках одних суток (0–86400 секунд) с проверкой на пересечения

## Технологии
- **Backend**: Spring Boot 4 (Java 21)
- **Frontend**: Vue 3 (Composition API) + Vuetify 3 + JavaScript
- **База данных**: PostgreSQL 16
- **Миграции**: Liquibase
- **Контейнеризация**: Docker + Docker Compose
- **Документация и генерация кода**: OpenAPI 3.0 (Contract-First подход)

### Contract-First подход

API реализовано по принципу Contract-First для обеспечения согласованности серверной и клиентской части. 
Спецификация OpenAPI (`openapi.yaml`) использовалась для:
- генерации DTO и интерфейсов контроллеров на backend
- генерации типов и клиента на frontend


## Запуск приложения

1. Создайте файл `.env` в корне проекта
```env
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=intervals_db
CORS_FRONTEND_URI=http://localhost:3000
```

После запуска будут доступны:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1

2. Запустите команду
```shell
  docker compose up -d
```

3. Остановка приложения:
```shell
    docker compose down
```

Для полной очистки данных используйте:
```shell
    docker compose down -v
```
## Примеры запросов

Документация к API расположена в корне проекта в файле `openapi.yaml`. 
Ниже примеры запросов:
1. Получение списка всех интервалов (с пагинацией и сортировкой)
```shell
  curl -X GET http://localhost:8080/api/v1/intervals  
```
```shell
  curl -X GET "http://localhost:8080/api/v1/intervals?page=1&size=1&sort=end,desc"
```

Пример успешного ответа:

    {
        "intervals": [
            {
                "id":"de8cca8a-984d-4db5-bef9-de61b12a85b7",
                "start":40103,
                "end":40104,
                "type":"WORK",
                "createdAt":"2026-01-03T12:59:08.193212Z"
            }
        ],
        "totalElements":24
    }

2. Создание нового интервала
```shell
    curl -X POST http://localhost:8080/api/v1/intervals \
      -H "Content-Type: application/json" \
      -d '{
        "start": 86300,
        "end": 86301,
        "type": "WORK"
      }'
```

Пример успешного ответа:

    {
        "id":"df36a5a6-dd36-41fd-8ce2-ea00ade6185f",
        "start":86300,
        "end":86301,
        "type":"WORK",
        "createdAt":"2026-01-04T17:12:47.351429588+05:00"
    }

### Примеры ошибок

- Пересечение интервалов: 409 Conflict
- Некорректные данные (start ≥ end, значения вне [0, 86400]): 400 Bad Request

Все возможные ошибки подробно описаны в OpenAPI-спецификации
