# Plano — Backend (Parte 2)

## Contexto atual
- `back-end/`: esqueleto Spring Boot 3.5.14 (Java 21, Maven) **já pronto** — `pom.xml` com JPA/Security/Validation/Web/Actuator/SpringDoc/jjwt/PostgreSQL, `application.properties` com variáveis de ambiente, pacotes criados (`entity`, `repository`, `service`, `controller`, `dto`, `security`, `exception`, `config`). Só falta a classe principal `SistemaFinanceiroApplication`.
- `front-end/`: 100% mockado; telas de Transações e Categorias são placeholders; não há client HTTP.
- Ambiente: Java 21 + Maven OK, **MySQL/MariaDB (XAMPP) rodando em `localhost:3306`** via phpMyAdmin, usuário `root` sem senha, banco `financeiro`.
- Decisões: API em **inglês** (spec), ordem **CRUDs primeiro, JWT depois**, banco **MySQL (`financeiro`)** — criado automaticamente via `createDatabaseIfNotExist=true`.

## Fase 0 — Verificação de setup
- Compilar o esqueleto (`mvn compile`) e subir o app para validar contexto.
- Confirmar conexão MySQL (`root` sem senha) e banco `financeiro` (criado automaticamente; acessível pelo phpMyAdmin).
- Padronizar URL do banco em `financeiro` (README diz `backend`; `application.properties` diz `financeiro` — corrigir README depois).
- Definir `JWT_SECRET` de desenvolvimento (≥256 bits) com default no `application.properties` para rodar sem env.

## Fase 1 — Base do backend (sem segurança)
- **Entidades JPA** (`br.com.financeiro.entity`):
  - `Usuario` (nome, email único, senhaCriptografada — BCrypt já na fase de auth, criadoEm/atualizadoEm via `@PrePersist`/`@PreUpdate`)
  - `Categoria` (usuario @ManyToOne, nome, `TipoTransacao`, cor, icone)
  - `Carteira` (dono @ManyToOne, nome, descricao, criadoEm)
  - `CarteiraMembro` (carteira + usuario @ManyToOne, `PapelCarteira`, entradoEm, `@UniqueConstraint(carteira, usuario)`)
  - `Transacao` (carteira @ManyToOne, categoria @ManyToOne opcional, criadoPor, `TipoTransacao`, valor BigDecimal, descricao, data LocalDate, criadoEm)
  - `TokenRedefinicaoSenha` (usuario, token UUID único, expiraEm, utilizado)
- **Enums**: `TipoTransacao {RECEITA, DESPESA}` e `PapelCarteira {DONO, EDITOR, VISUALIZADOR}`
- **Repositories** JPA + métodos de consulta (`findByUsuario`, `findByCarteira`, etc.)
- **Exceptions + @ControllerAdvice**: `ResourceNotFoundException` (404), `BusinessException` (422), `AccessDeniedException` (403), `MethodArgumentNotValidException` (400), `DataIntegrityViolationException` (409), com corpo JSON `{timestamp, status, error, message}`
- **Config**: `WebMvcConfigurer` para CORS (origem `http://localhost:5173` — port do Vite, não 3000 do spec)

## Fase 2 — CRUD Categorias (`/api/v1/categories`)
- GET (com filtro `?type=INCOME|EXPENSE`), POST, PUT `/{id}`, DELETE `/{id}`
- Regra: excluir categoria com transações → **422** com mensagem
- DTOs request/response com Bean Validation (`@NotBlank`, `@Size`, `@Pattern`)

## Fase 3 — CRUD Carteiras + Membros (`/api/v1/wallets`)
- Wallets: GET (lista), POST (criador vira DONO), GET `/{id}`, PUT `/{id}`, DELETE `/{id}` (cascade)
- Members: GET, POST `/{email, role}`, PATCH `/{userId}` (mudar papel), DELETE `/{userId}`
- Erros: 404 e-mail inexistente, 409 já membro, 403 não-DONO
- **Autorização será adicionada na Fase 7**; nesta fase os CRUDs funcionam sem papel

## Fase 4 — CRUD Transações (`/api/v1/wallets/{id}/transactions`) + Dashboard
- GET com filtros: `type`, `categoryId`, `startDate/endDate`, paginação obrigatória `page/size/sort` (Spring Data Pageable)
- POST, GET `/{id}`, PUT `/{id}`, DELETE `/{id}`
- GET `/summary`: `totalIncome`, `totalExpense`, `balance`, `transactionCount`, `byCategory`, `byMonth` (queries de agregação JPQL)
- Validações: `@DecimalMin("0.01")`, `@PastOrPresent`, `@NotNull`

## Fase 5 — Autenticação JWT
- `POST /auth/register` — cria usuário, senha BCrypt, **seed de categorias padrão** (Salário, Alimentação, Transporte, Moradia, Lazer, Saúde, Investimentos)
- `POST /auth/login` — retorna `{accessToken, tokenType, expiresIn}` (jjwt, payload sub/email/iat/exp, 24h)
- `JwtService` (gerar/validar), `JwtAuthenticationFilter` (`OncePerRequestFilter`), `UserDetailsService`, `SecurityConfig` (rotas públicas: auth + actuator/health; demais `/api/**` exigem JWT)
- `GET/PUT /api/v1/users/me` e `PATCH /api/v1/users/me/password` (422 se senha atual incorreta)

## Fase 6 — Recuperação de senha
- `POST /auth/forgot-password` — resposta neutra + `debugToken` (UUID, expiração 1h, conforme spec)
- `POST /auth/reset-password` — valida token (400 se inválido/expirado/utilizado), atualiza senha

## Fase 7 — Autorização por papel (DONO/EDITOR/VISUALIZADOR)
- Regras no **Service** (não na controller): acesso só a carteiras das quais é membro; só DONO convida/edita/exclui carteira; EDITOR gerencia transações; VISUALIZADOR só lê
- 403 via `AccessDeniedException`

## Fase 8 — Integração frontend
- Criar `front-end/src/services/api.ts`: client fetch com `VITE_API_URL` (default `http://localhost:8080/api/v1`), anexa token do localStorage, trata 401 (logout)
- Substituir mocks: `auth.ts` (login/register/forgot/reset/changePassword reais), `transactions.ts` (fetch real + create/update/delete)
- Implementar telas placeholder: **Transações** (listar, criar, editar, excluir com Dialog) e **Categorias** (CRUD)
- Ajustar `SessionUser`/tipos ao formato da API (inglês → mapear no frontend)

## Fase 9 — README + Swagger
- Atualizar `README.md` (execução, variáveis `DB_URL/DB_USERNAME/DB_PASSWORD/JWT_SECRET/JWT_EXPIRATION`, link Swagger `http://localhost:8080/swagger-ui.html`, decisões, banco `financeiro`)
- Swagger já incluso via springdoc (documentar com `@Operation`/`@Tag` conforme necessário)

---

**Pontos de atenção**: nunca retornar entidade JPA nos controllers (sempre DTO); lógica de negócio só no Service; validação nos DTOs; erros padronizados; paginação obrigatória em transações.
