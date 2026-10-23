# Status dos requisitos — controle-financeiro

Tabela viva mantida pela skill `requisitos`. Fontes: `docs/extraidos/*.md`
(raiz do repositório). Última revisão: 2026-10-22.

Tipos: **[explícito]** = exigência da disciplina · **[recomendação]** = boa
prática (só com aprovação do usuário). Sinais: ✅ concluído · ⚠️ parcial /
problema comprovado · ❌ ausente · 🔎 não confirmado em execução.

## Parte 1 — Frontend (Parte 1)

| Id | Requisito | Tipo | Status | Evidência / pendência |
| --- | --- | --- | --- | --- |
| P1-LOGIN | Login com e-mail/senha, mostrar/ocultar, loading, erros, redirecionamento de autenticado | [explícito] | ✅ | `pages/Login.tsx`, `components/ui/password-input.tsx` |
| P1-CADASTRO | Cadastro com força de senha em tempo real, confirmação cross-field, e-mail duplicado | [explícito] | ✅ | `pages/Register.tsx`, `components/password-strength.tsx`; 409 via `ConflictException` |
| P1-REDEFINIR | Recuperação em 2 etapas (`/recuperar-senha`, `/redefinir-senha/:token`), mensagem neutra | [explícito] | ✅ | `App.tsx` linhas 20–21; backend retorna `debugToken` (testado) |
| P1-ALTERAR | Alteração de senha em `/app/perfil/senha`, rota protegida, exige senha atual | [explícito] | ✅ | `pages/ChangePassword.tsx`; 422 se senha atual incorreta |
| P1-DASHBOARD | Dashboard: navegação, 3+ indicadores, gráfico, lançamentos recentes, loading, logout | [explícito] | ⚠️ | Implementação aplicada em 2026-10-22: indicadores via `fetchSummary` (`pages/Dashboard.tsx`); lint/build verdes; execução runtime 🔎 |
| P1-VALIDACAO | Senha mínima 6 no spec da Parte 1; projeto usa 8 (spec da Parte 2 §6) | [explícito] | ✅ | `lib/validation.ts` (min 8) alinhado ao backend — desvio justificado |

## Parte 2 — Backend (Parte 2)

| Id | Requisito | Tipo | Status | Evidência / pendência |
| --- | --- | --- | --- | --- |
| P2-STACK | Spring Boot 3.x + Security + JWT + JPA + MySQL + Maven + Swagger | [explícito] | ✅ | `pom.xml` (3.5.14, jjwt, springdoc) |
| P2-ENTIDADES | Usuario, Categoria, Carteira, CarteiraMembro, Transacao, TokenRedefinicaoSenha + enums | [explícito] | ✅ | `entity/` completo |
| P2-CAMADAS | Controller delega; lógica no service; DTOs obrigatórios; erro `{timestamp, status, error, message}` | [explícito] | ✅ | `exception/GlobalExceptionHandler.java` + `ApiError` |
| P2-AUTH | Rotas públicas `/auth/**` e `/actuator/health`; demais autenticadas com JWT | [explícito] | ✅ | `security/SecurityConfig.java` linhas 57–62 |
| P2-CATEGORIES | CRUD categorias; 422 ao excluir com transações vinculadas; filtro `?type` | [explícito] | ✅ | `CategoriaService` linha 70 |
| P2-WALLETS | CRUD carteiras + membros; OWNER convida/edita/exclui; EDITOR e VISUALIZADOR conforme papel | [explícito] | ✅ | `CarteiraController`, regras no `CarteiraService` |
| P2-TRANSACTIONS | CRUD transações com filtros (type, categoryId, datas) e paginação | [explícito] | ✅ | `TransacaoController` linhas 39–48 |
| P2-SUMMARY | `GET /api/v1/wallets/{id}/summary` consumido pelo frontend | [explícito] | ⚠️ | Endpoint ✅ (`SummaryResponse`, agregação em `TransacaoService`); frontend agora consome: `fetchSummary(walletId)` chamado por `pages/Dashboard.tsx` (2026-10-22); lint/build verdes; execução runtime 🔎 |
| P2-BEANVALIDATION | Bean Validation nos DTOs; 400 padronizado | [explícito] | ✅ | DTOs anotados; `@Valid` nos controllers |
| P2-CORS | CORS com origem do frontend, métodos, headers Authorization/Content-Type; `.env` no React p/ baseURL | [explícito] | ⚠️ | CORS ✅ em `localhost:5173`; `baseURL` hardcoded em `lib/api.ts` (`.env` é [recomendação] §7 — converter somente com aprovação) |
| P2-README | README com variáveis de ambiente e execução | [explícito] | ✅ | `README.md` |

## Parte Final (Parte Final)

| Id | Requisito | Tipo | Status | Evidência / pendência |
| --- | --- | --- | --- | --- |
| PF-INTEGRACAO | 1.1 Frontend+backend integrados, nada mockado | [explícito] | ✅ | `services/*.ts` chamam API real; sem mocks no frontend |
| PF-TEMPOREAL | 1.2 WebSocket/SSE em ponto real do sistema | [explícito] | ❌ | Sem dependência/código coisa nenhuma (`pom.xml`, front) |
| PF-RELATORIOS | 1.3 Ao menos um relatório/extrato | [explícito] | ❌ | Nada no código |
| PF-IA | 1.4 API de IA integrada a funcionalidade real | [explícito] | ❌ | Sem SDK/chave; nada no README |
| PF-PWA | 1.5 Manifest + service worker com funcionamento offline | [explícito] | ❌ | `front-end/public/` só `favicon.svg` |
| PF-OWASP | 1.6 OWASP Top 10 (risco por risco) | [explícito] | ✅ | Rate limit em `/auth/*` (`RateLimitFilter` + teste), CSP/HSTS/XFO, controle por papel no service, validação de entrada, segredos via env |
| PF-PADROES | 1.7 Camadas/DTOs (requisito) + testes automatizados (apenas sugestão) | [explícito]/[recomendação] | ⚠️ | Camadas ✅; testes backend ✅ (7 classes, H2) · frontend ❌ (sem runner — Vitest é [recomendação]) |
| PF-DEPLOY | 1.8 Deploy público + link no README | [explícito] | ❌ | Sem Docker/compose/CI; README sem link |
| PF-DOC | 1.9 README atualizado + Swagger acessível | [explícito] | ✅ parte 1/⚠️ restante | README e Swagger ✅; faltam seções de deploy e IA |

## Problemas pontuais comprovados (modo correção — Partes 1/2)

| Id | Problema | Evidência | Prioridade |
| --- | --- | --- | --- |
| FIX-DASH-SUMMARY | Dashboard calcula indicadores client-side com `size:100`; não usa `/summary`; quebra com >100 lançamentos | `pages/Dashboard.tsx:19`, `services/transactions.ts:73` — **corrigido 2026-10-22**: indicadores via `fetchSummary(walletId)`; gráfico/recentes seguem a lista separada; lint/build verdes; runtime 🔎 | Alta |
| FIX-DASH-CACHE | `fetchTransactions()` resolve carteira pelo cache `localStorage`, ignorando `selectedWallet` | `services/wallets.ts:101`, `Dashboard.tsx:28` — **corrigido 2026-10-22**: `fetchTransactions(walletId)` e `fetchSummary(walletId)` exigem id explícito da carteira selecionada; runtime 🔎 | Alta |
| FIX-DASH-CATCH | Carregamento sem `.catch`: falha de API deixa skeleton infinito | `Dashboard.tsx:19–24` — **corrigido 2026-10-22**: `Dashboard.tsx` com `.catch` + falha associada por carteira (`failure.walletId`) e estado para `!walletId`; runtime 🔎 | Alta |
| FIX-TEXTO-MES | Textos "do mês" estáticos sem recorte de período real | `Dashboard.tsx:44`, `expense-chart.tsx:44,48` | Média |
| FIX-CATEG-CATCH | `catch(() => setCategories([]))` silencia falha ao carregar categorias | `hooks/use-transactions.ts:71` | Média |
| FIX-DONO-NOME | Dono identificado por `nome` (não por id) — quebra com nomes duplicados | `services/wallets.ts:43–52` | Média |

## Histórico de revisão

| Data | Observação |
| --- | --- |
| 2026-10-22 | Tabela criada a partir das verificações das sessões de análise (leitura estática; agregado do `/summary` não executado em runtime). |
| 2026-10-22 | Correções FIX-DASH-SUMMARY/FIX-DASH-CACHE/FIX-DASH-CATCH implementadas no dashboard (`fetchSummary` + carteira selecionada + `.catch`); FIX-TEXTO-MES, FIX-CATEG-CATCH e FIX-DONO-NOME permanecem abertos; lint e build verdes; verificação em runtime pendente. |
