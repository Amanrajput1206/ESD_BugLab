# ESD Lab 3 — Campus Event Registration API

**Student starter repository.** This project intentionally contains eight planted defects. It is expected that the eight targeted tests in `Lab3BugTests` fail before the defects are fixed. The smoke test should pass. This is intentional for the lab, not a setup failure.

## Stack
- Java 17 (Java 21 also works)
- Spring Boot 3.3.5
- Spring Data JPA
- H2 in-memory database with automatic seed data
- Springdoc OpenAPI / Swagger UI
- JUnit 5 and MockMvc

## Run
From the project directory:

Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

macOS/Linux (requires Maven installed because this lightweight wrapper delegates to `mvn`):
```bash
chmod +x mvnw
./mvnw spring-boot:run
./mvnw test
```

You can also open the project in IntelliJ IDEA and run `EsdLab3Application` or use the Maven panel.

## Swagger UI
After the app starts, open: http://localhost:8080/swagger-ui/index.html

OpenAPI JSON: http://localhost:8080/v3/api-docs

H2 console (optional): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:esdlab3`
- Username: `sa`
- Password: leave blank

## Database and seed data
H2 runs in memory. Tables are created when the application starts and dropped when it stops. The `SeedData` initializer inserts sample events and registrations once per startup. Restart the app for a clean seeded database. Tests clear the tables before each test so they do not depend on seed ordering.

## API endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/events` | Create event |
| GET | `/api/events` | List events |
| GET | `/api/events/{id}` | Get event |
| POST | `/api/events/{eventId}/registrations` | Register a student |
| GET | `/api/events/{eventId}/registrations` | List active registrations |
| DELETE | `/api/registrations/{registrationId}` | Cancel registration |

Example create-event request:
```json
{"title":"ESD Workshop","capacity":2}
```
Example registration request:
```json
{"studentEmail":"student@example.com"}
```

## Lab expectations
1. Reproduce each issue through Swagger UI and/or the supplied tests.
2. Use breakpoints, logs, stack traces, and code inspection to find the root cause.
3. Fix the defect manually; do not use generative AI tools for debugging, fixes, or tests.
4. Re-run tests and record the root cause, fix, and verification evidence.
5. Do not remove or weaken a test merely to make the suite green.

The eight defects cover input validation, business rules, state transitions, repository queries, and HTTP/resource error handling. See `docs/student-lab-instructions.md` for the issue reports. No solution locations are disclosed in the student package.
