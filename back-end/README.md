# Back-end - Sistema de Gestao Financeira

API REST em Spring Boot 3.5 (Java 21) para gestao financeira pessoal e compartilhada.

## Arquitetura
Camadas: controller, service, repository, entity, dto, security, exception, config. Banco relacional (MySQL/PostgreSQL). Autenticacao JWT.

## Pre-requisitos
- Java 21+
- Maven (ou `./mvnw`)
- MySQL/MariaDB ou PostgreSQL

## Variaveis de ambiente
Ver `.env.example`. Principais: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `PORT`, `AUTH_MAX_REQUESTS`, `AUTH_WINDOW_SECONDS`, `CORS_ALLOWED_ORIGINS`, `APP_DEBUG_TOKENS`, `GEMINI_API_KEY`, `GEMINI_MODEL`.

## Como rodar
```bash
./mvnw spring-boot:run
```
API: `http://localhost:8080`  
Swagger: `http://localhost:8080/swagger-ui.html`

## Testes
```bash
./mvnw test
```
Roda com H2 em memoria.

## Deploy
Imagem Docker (Java 21) publicada e hospedada na Render. Health check: `/actuator/health`.