# Controle Financeiro

Aplicação web de controle financeiro pessoal e compartilhado: o usuário cadastra receitas e despesas, visualiza resumos e gráficos e compartilha carteiras com outras pessoas para que possam visualizar e colaborar.

Este repositório reúne as duas partes do projeto final da disciplina de Programação Web:

| Parte | Pasta | Descrição |
| --- | --- | --- |
| Parte 1 — Frontend | `front-end/` | React + TypeScript + Vite |
| Parte 2 — Backend | `back-end/` | API REST em Spring Boot |

## Funcionalidades

- **Login** — e-mail e senha, mostrar/ocultar senha, estado de carregamento, mensagens de erro claras e redirecionamento automático para o dashboard quando já autenticado.
- **Cadastro** — fluxo em duas etapas com indicador de força da senha em tempo real, confirmação de senha e validação de e-mail duplicado.
- **Recuperação de senha** — etapa 1 (`/recuperar-senha`) com mensagem neutra (não revela se o e-mail existe) e etapa 2 (`/redefinir-senha/:token`) para definir a nova senha.
- **Alteração de senha** — área autenticada (`/app/perfil/senha`), exige a senha atual e aplica as mesmas regras de força do cadastro.
- **Dashboard** — resumo financeiro (saldo, receitas e despesas), gráfico de despesas por categoria (Recharts) e lista de lançamentos recentes, com estado de carregamento.
- **Transações** — CRUD completo com filtros (tipo, categoria, período) e paginação.
- **Categorias** — CRUD completo com seleção de cor e tipo (receita/despesa).
- **Compartilhamento de carteiras** — convite de membros por e-mail com papéis (dono, editor, visualizador).

## Pré-requisitos

**Front-end:**
- Node.js 18+
- npm

**Back-end:**
- Java 21+
- MySQL ou MariaDB (via XAMPP ou similar)
- Maven (ou o wrapper `./mvnw` incluso)

## Variáveis de ambiente

O backend lê as configurações de `src/main/resources/application.properties`:

| Variável | Descrição | Valor padrão |
| --- | --- | --- |
| `spring.datasource.url` | URL de conexão com o MySQL | `jdbc:mysql://localhost:3306/financeiro` |
| `spring.datasource.username` | Usuário do banco | `root` |
| `spring.datasource.password` | Senha do banco | *(vazio)* |
| `jwt.secret` | Chave secreta para assinar tokens JWT (mínimo 256 bits) | `chave_secreta_do_sistema_financeiro_32_caracteres_minimo` |
| `jwt.expiration` | Tempo de expiração do token em milissegundos | `86400000` (24h) |

## Como executar

**Front-end:**

```bash
cd front-end
npm install
npm run dev
```

A aplicação fica disponível em `http://localhost:5173`.

**Back-end:**

```bash
cd back-end
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8080`.

**Documentação Swagger:** `http://localhost:8080/swagger-ui.html`

## Estrutura do código

### Front-end

```
front-end/src/
├── components/        # Componentes reutilizáveis
│   ├── dashboard/     #   cards, gráfico e lista de transações
│   ├── layout/        #   sidebar, header e layout autenticado
│   └── ui/            #   primitivos (button, input, card, ...)
├── contexts/          # Contexto de autenticação
├── lib/               # Schemas de validação e utilitários
├── pages/             # Telas (Login, Register, Dashboard, ...)
└── services/          # Chamadas à API (auth, categorias, transações, carteiras)
```

### Back-end

```
back-end/src/main/java/br/com/financeiro/
├── config/            # Beans de configuração (CORS, OpenAPI, Security)
├── controller/        # Controllers REST (recebe request, delega ao service)
├── dto/               # Objetos de transferência de dados (request/response)
├── entity/            # Entidades JPA mapeadas para tabelas do banco
├── exception/         # Exceções customizadas e handler global (@ControllerAdvice)
├── repository/        # Interfaces JPA (queries no banco)
├── security/          # Configuração JWT, filtro de autenticação
└── service/           # Lógica de negócio (validações, regras, orquestração)
```

## Tecnologias

**Front-end**

| Tecnologia | Uso |
| --- | --- |
| React 19 + TypeScript | Base da aplicação |
| Vite | Build e dev server |
| Tailwind CSS 4 | Estilização utilitária |
| React Router 7 | Navegação entre telas |
| React Hook Form + Zod | Formulários e validação |
| shadcn/ui | Componentes de UI (Radix + Tailwind) |
| Recharts | Gráficos |
| Lucide React | Ícones |

**Back-end**

| Tecnologia | Uso |
| --- | --- |
| Spring Boot 3.5 | Framework principal |
| Spring Security + JWT | Autenticação e autorização |
| Spring Data JPA / Hibernate | Persistência de dados |
| MySQL / MariaDB | Banco de dados relacional |
| Maven | Gerenciamento de dependências |
| SpringDoc / Swagger | Documentação automática da API |
| Lombok | Redução de boilerplate |

## Rotas

| Rota | Acesso | Descrição |
| --- | --- | --- |
| `/login` | Público | Entrar na conta |
| `/cadastro` | Público | Criar nova conta |
| `/recuperar-senha` | Público | Solicitar redefinição de senha |
| `/redefinir-senha/:token` | Público | Definir nova senha |
| `/app/dashboard` | Autenticado | Resumo financeiro |
| `/app/transacoes` | Autenticado | Gerenciar transações |
| `/app/categorias` | Autenticado | Gerenciar categorias |
| `/app/perfil` | Autenticado | Perfil do usuário |
| `/app/perfil/senha` | Autenticado | Alterar senha |
| `/app/configuracoes` | Autenticado | Configurações |

## Decisões de projeto

- **Sessão persistida no `localStorage`** — o token JWT e os dados do usuário são armazenados no `localStorage`; ao recarregar a página a sessão continua ativa.
- **Rotas protegidas** — as telas autenticadas ficam sob o prefixo `/app/` e são envolvidas por `RequireAuth`; usuário deslogado é redirecionado para `/login`.
- **Regras de senha centralizadas** — os schemas (`emailSchema`, `passwordSchema`) ficam em `src/lib/validation.ts` e são compartilhados por todas as telas, evitando duplicação.
- **Carteira automática** — ao primeiro acesso, o sistema cria automaticamente uma "Carteira Pessoal" para o usuário.
- **Camada de serviço no backend** — toda lógica de negócio fica isolada no `service/`, nunca na `controller/`. Entidades JPA nunca são retornadas diretamente; sempre se usam DTOs.
- **Tratamento global de erros** — `@ControllerAdvice` padroniza todas as respostas de erro da API no formato `{timestamp, status, error, message}`.
- **Identidade visual própria** — paleta violeta com tema claro/escuro, definida em `src/palette.css`.
