# Controle Financeiro

Projeto final da disciplina de Programação Web -  sistema de controle financeiro pessoal com acompanhamento de receitas, despesas e gráficos.

## Estrutura

| Pasta | Descrição |
| --- | --- |
| `front-end/` | Aplicação web em React + TypeScript + Vite |
| `back-end/` | API em Spring Boot (Java 21) |

## Stack atual

**Front-end**
| Tecnologia | Uso |
| --- | --- |
| React 19 + TypeScript 6 | Base da aplicação |
| Vite 8 | Build e dev server |
| Tailwind CSS 4 | Estilização utilitária |
| React Router 7 | Navegação entre telas |
| React Hook Form + Zod | Formulários e validação |
| Radix UI | Componentes acessíveis (dialog, toast, label, etc.) |
| Lucide React | Ícones |
| CVA + clsx + tailwind-merge | Variantes de componentes |
| ESLint + typescript-eslint | Qualidade de código |

**Back-end** - Spring Boot 3.5 (JPA, Security, Validation) + PostgreSQL.

## Paleta de cores — Violeta

Definida em `front-end/src/palette.css`, disponível no Tailwind via `bg-primary`, `text-muted`, `bg-surface`, `text-expense`, etc. Tema escuro com a classe `.dark`.

| Variável | Claro | Escuro |
| --- | --- | --- |
| `--primary` | `#7c3aed` | `#a78bfa` |
| `--primary-foreground` | `#ffffff` | `#17121f` |
| `--accent` | `#d946ef` | `#e879f9` |
| `--income` | `#16a34a` | `#4ade80` |
| `--expense` | `#e11d48` | `#fb7185` |
| `--background` | `#faf9fc` | `#17121f` |
| `--surface` | `#ffffff` | `#241c33` |
| `--foreground` | `#18181b` | `#f4f4f5` |
| `--muted` | `#71717a` | `#a1a1aa` |
| `--border` | `#e9e5f0` | `#322a45` |

## Fontes

`system-ui, 'Segoe UI', Roboto, sans-serif` — definida em `front-end/src/index.css`.


## Como rodar

**Front-end**

```bash
cd front-end
npm install
npm run dev
```

**Back-end** (requer PostgreSQL em `localhost:5432`, banco `backend`)

```bash
cd back-end
./mvnw spring-boot:run
```

## Nota

Este README é está em desenvolvimento, conforme as atualizações do código. 
