# Booker - RESTful API

## How to run the project

### Requirements
- Docker / Podman
- docker-compose / podman-compose
- A .env file (see `.env.example`)

#### Run the application from your IDE using the `BookerApplication.java` class or execute `mvn spring-boot:run` in the terminal.

When the `dev` profile is active, Spring Boot will detect `compose.yaml`, start the PostgreSQL container automatically, and stop it when the app stops.

For deploy environments, use an external database and set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

##### The application will be available at http://localhost:8080

## API Documentation

This project uses Swagger for API documentation. When running locally, the UI is available at: http://localhost:8080/swagger-ui/index.html#/

## Contributing

Code formatting follows the rules defined in `.editorconfig`. To use it, install the **EditorConfig** extension in your editor.

Commits should follow the Conventional Commits convention to help with visibility and semantic versioning.

## Git Hooks

Set hooks path once after clone:

```bash
git config core.hooksPath .githooks
```

On Linux/macOS, ensure execute permission:

```bash
chmod +x .githooks/pre-commit
```

Pre-commit policy (predictable and fast):
- always runs all unit tests before commit
- excludes integration/e2e tests by naming pattern (`*IT`, `*IntegrationTest`, `*E2ETest`)
- uses Maven parallel workers (`-T 1C`) for maximum local speed

---

Please open pull requests to contribute and describe your changes in detail.

All tests must be passing before merging a pull request.