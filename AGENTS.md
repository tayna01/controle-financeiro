## Idioma obrigatório

- Sempre responda em português brasileiro (pt-BR).
- Escreva planos, explicações, análises e documentações em português brasileiro.
- Ao utilizar ferramentas ou consultar documentação em inglês, apresente os resultados em português.
- Não mude para inglês por causa do idioma do modelo ou das instruções internas.
- Preserve nomes técnicos, identificadores de código e comandos conforme as convenções de desenvolvimento.
- Antes de enviar uma resposta, confira se ela está em português brasileiro.

# AGENTS.md

Dois aplicativos independentes e um README na raiz do repositório em português — sem scripts na raiz. Execute todos os comandos dentro de `front-end/` (React 19 + TS + Vite) ou `back-end/` (Spring Boot 3.5 / Java 21 / Maven wrapper). Nomes no código, rotas e campos da API estão em português (`transacao`, `carteira`, `/cadastro`, ...) — siga o idioma existente, não renomeie para inglês aos poucos.

## Backend (`back-end/`)

- Execução: `./mvnw spring-boot:run` (Windows PowerShell: `./mvnw.cmd`). API na porta `:8080`; Swagger em `http://localhost:8080/swagger-ui.html`.
- Para rodar a aplicação é necessário um MySQL/MariaDB local (ex.: XAMPP, `root`/senha vazia por padrão). O banco de dados é criado e o schema sincronizado automaticamente (`ddl-auto=update` — não há scripts de migration).
- `./mvnw test` roda com H2 em memória — não precisa de MySQL. Todo teste Spring é anotado com `@ActiveProfiles("test")` para carregar `src/test/resources/application-test.properties`; qualquer novo `@SpringBootTest` também precisa dessa anotação, senão falha tentando conectar ao MySQL.
- Teste único: `./mvnw test -Dtest=TransacaoServiceTest`.
- A configuração fica em variáveis de ambiente com valores padrão de desenvolvimento em `src/main/resources/application.properties`: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `PORT`, `AUTH_MAX_REQUESTS`, `AUTH_WINDOW_SECONDS`.
- A camada é rígida: os controllers apenas recebem a requisição e delegam; toda a lógica de negócio fica em `service/`. Os controllers nunca retornam entidades JPA — apenas DTOs. Todos os erros passam pelo handler global `@ControllerAdvice` e retornam `{timestamp, status, error, message}` — o frontend exibe `response.data.message` ao usuário.

## Frontend (`front-end/`)

- `npm run dev` (porta 5173), `npm run lint`. Não há executor de testes — a verificação é `npm run lint` + `npm run build` (o build roda `tsc -b`, então serve também como checagem de tipos).
- `src/lib/api.ts` fixa `baseURL: 'http://localhost:8080'` — sem proxy do Vite. O backend precisa estar em execução para qualquer página autenticada funcionar; o CORS é tratado no backend (`SecurityConfig`).
- Um interceptor de resposta faz logout automático (limpa o localStorage, redireciona para `/login`) em qualquer 401 vindo de uma rota que não seja `/auth/` — lembre disso quando uma chamada falhar silenciosamente após o login.
- O estado de autenticação fica direto no localStorage sob as chaves `token` e `user` (não há camada de persistência de contexto além disso).
- Os imports usam o alias `@` → `src/`.
- `src/components/ui/*` são primitivos do shadcn/ui (Radix) — trate como biblioteca de componentes; construa componentes específicos da aplicação sobre eles em vez de editá-los casualmente.
- Os schemas do Zod (`emailSchema`, `passwordSchema`, ...) são compartilhados a partir de `src/lib/validation.ts` — estenda lá em vez de redefinir validação em cada tela.
- As rotas ficam em `src/App.tsx`; as páginas autenticadas ficam sob `/app/*` dentro de `RequireAuth` + `WalletProvider` (carteiras selecionadas globalmente via contexto de carteira).
