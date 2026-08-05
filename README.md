# Controle Financeiro

Aplicação web de controle financeiro pessoal e compartilhado: o usuário cadastra receitas e despesas, visualiza resumos e gráficos e, futuramente, compartilha carteiras com outras pessoas.

Este repositório reúne as duas partes do projeto final da disciplina de Programação Web:

| Parte | Pasta | Descrição |
| --- | --- | --- |
| Parte 1 — Frontend | `front-end/` | React + TypeScript + Vite, com dados mockados |
| Parte 2 — Backend | `back-end/` | API REST em Spring Boot (em desenvolvimento) |

## Funcionalidades (Parte 1)

- **Login** — e-mail e senha, mostrar/ocultar senha, estado de carregamento, mensagens de erro claras e redirecionamento automático para o dashboard quando já autenticado.
- **Cadastro** — fluxo em duas etapas com indicador de força da senha em tempo real, confirmação de senha e validação de e-mail duplicado.
- **Recuperação de senha** — etapa 1 (`/recuperar-senha`) com mensagem neutra (não revela se o e-mail existe) e etapa 2 (`/redefinir-senha/:token`) para definir a nova senha.
- **Alteração de senha** — área autenticada (`/app/perfil/senha`), exige a senha atual e aplica as mesmas regras de força do cadastro.
- **Dashboard** — resumo financeiro (saldo, receitas e despesas), gráfico de despesas por categoria (Recharts) e lista de lançamentos recentes, com estado de carregamento simulado.

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
| Prettier + ESLint | Formatação e qualidade de código |

**Back-end** — Spring Boot 3.5 (JPA, Security, Validation) + MySQL/MariaDB (em desenvolvimento).

## Estrutura do código

O frontend segue uma separação por responsabilidade, para que um colega encontre cada coisa sem precisar perguntar:

```
front-end/src/
├── components/        # Componentes reutilizáveis
│   ├── dashboard/     #   específicos do dashboard
│   ├── layout/        #   sidebar, header e layout autenticado
│   └── ui/            #   primitivos (button, input, card, ...)
├── contexts/          # Contexto de autenticação
├── lib/               # Schemas de validação e utilitários
├── pages/             # Telas (Login, Register, Dashboard, ...)
└── services/          # Lógica de serviço (auth e transações mock)
```

## Decisões de projeto

- **Mock com delay simulado** — a comunicação com o backend é feita por Promises com `setTimeout` em `src/services/`, preparando o código para quando a API real existir.
- **Sessão persistida no `localStorage`** — o token (mock) e os dados do usuário são guardados no `localStorage`; ao recarregar a página a sessão continua.
- **Rotas protegidas** — as telas autenticadas ficam sob o prefixo `/app/` e são envolvidas por `RequireAuth`; usuário deslogado é redirecionado para `/login`.
- **Regras de senha centralizadas** — os schemas (`emailSchema`, `passwordSchema`) ficam em `src/lib/validation.ts` e são compartilhados por todas as telas, evitando duplicação. A senha exige mínimo 8 caracteres, uma letra maiúscula, uma minúscula, um número e um símbolo.
- **Identidade visual própria** — paleta violeta com tema claro/escuro, definida em `src/palette.css` (variáveis consumidas pelo Tailwind) e acessível por `ThemeToggle`.

### Paleta (Violeta)

| Variável | Claro | Escuro |
| --- | --- | --- |
| `--primary` | `#7c3aed` | `#a78bfa` |
| `--accent` | `#d946ef` | `#e879f9` |
| `--income` | `#16a34a` | `#4ade80` |
| `--expense` | `#e11d48` | `#fb7185` |
| `--background` | `#faf9fc` | `#17121f` |
| `--surface` | `#ffffff` | `#241c33` |

Fonte: `system-ui, 'Segoe UI', Roboto, sans-serif` (definida em `src/index.css`).

## Como executar

**Front-end**

```bash
cd front-end
npm install
npm run dev
```

A aplicação abre em `http://localhost:5173`.

**Back-end** (requer MySQL/MariaDB via XAMPP em `localhost:3306`, banco `financeiro` criado automaticamente)

```bash
cd back-end
./mvnw spring-boot:run
```

## Rotas

| Rota | Acesso | Descrição |
| --- | --- | --- |
| `/login` | Público | Entrar na conta |
| `/cadastro` | Público | Criar nova conta |
| `/recuperar-senha` | Público | Solicitar redefinição de senha |
| `/redefinir-senha/:token` | Público | Definir nova senha |
| `/app/dashboard` | Autenticado | Resumo financeiro |
| `/app/perfil` | Autenticado | Perfil do usuário |
| `/app/perfil/senha` | Autenticado | Alterar senha |
| `/app/transacoes`, `/app/categorias` | Autenticado | Placeholders (Parte 2) |

## Credenciais de teste

Usuários simulados em `front-end/src/services/auth.ts`:

| E-mail | Senha |
| --- | --- |
| `usuario@exemplo.com` | `123456` |
| `teste@exemplo.com` | `123456` |

Para testar a recuperação de senha: em `/recuperar-senha`, o mock exibe um link com token de exemplo (`/redefinir-senha/token-demo`); qualquer token não vazio é aceito.
