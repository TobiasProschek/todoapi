# ✅ todo API - Ktor Application

A modern RESTful todo API built with [Ktor](https://ktor.io/) and Kotlin, integrating MongoDB, robust error handling, and automatic API documentation.

---

## 🚦 Status Workflow

`TODO → IN_PROGRESS → DONE`

### 📌 Business Rules

- New todos **cannot** be created with `DONE` status
- Status transitions follow logical order
- `updatedAt` is automatically updated on changes

---

## 🌐 API Endpoints

| Method | Endpoint          | Description       | Status Codes  |
| ------ | ----------------- | ----------------- | ------------- |
| GET    | `/api/todos`      | Get all todos     | 200           |
| POST   | `/api/todos`      | Create a new todo | 201, 400      |
| GET    | `/api/todos/{id}` | Get todo by ID    | 200, 400, 404 |
| PUT    | `/api/todos/{id}` | Update a todo     | 200, 400, 404 |
| DELETE | `/api/todos/{id}` | Delete a todo     | 204, 400, 404 |

---

## 🔁 Example Requests

### Create Todo

```http
POST /api/todos
Content-Type: application/json

{
  "title": "Learn Ktor",
  "description": "Build a REST API with Ktor framework",
  "status": "TODO"
}
```

### Update Todo

```http
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Learn Ktor",
  "description": "Build a REST API with Ktor framework",
  "status": "IN_PROGRESS"
}
```

---

## 🏗️ Project Structure

```config
src/main/kotlin/com/proschek/
├── config/               # MongoDB configuration
├── exception/            # Custom error handling
├── model/                # Data models and DTOs
├── plugins/              # Ktor plugins (Routing, HTTP, Serialization)
├── repository/           # MongoDB access layer
├── routes/               # Todo route handlers
└── utils/                # UUID validation and helpers
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- MongoDB 4.4+
- Gradle 8+

---

## 🧪 Testing

```bash
./gradlew test
./gradlew test jacocoTestReport
```

Test coverage includes unit, integration, and validation tests.

---

## ⚙️ Configuration

| Variable    | Description               | Default                           |
| ----------- | ------------------------- | --------------------------------- |
| PORT        | Server port               | 8080                              |
| MONGODB_URI | MongoDB connection string | mongodb://localhost:27017/todoapp |
| LOG_LEVEL   | Logging level             | INFO                              |

---

## 🔍 Error Handling

```json
{
  "error": "TodoNotFoundException",
  "message": "Todo not Found",
  "timestamp": "2024-12-04T14:32:45.584Z"
}
```

Custom exceptions:

- `TodoNotFoundException` (404)
- `TodoInvalidDataException` (400)
- `TodoMongoException` (500)

---

## 📖 Documentation

- [Swagger UI](http://localhost:8080/swagger)
- [OpenAPI Spec](http://localhost:8080/openapi)

---

## 🔧 Code Quality

```bash
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew detekt
./gradlew build
```

---

Happy coding! 🎉 Built with ❤️ using Kotlin and Ktor.
