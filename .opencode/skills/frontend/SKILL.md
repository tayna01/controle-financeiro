---
name: Frontend
description: Desenvolvimento e correção do front-end React/TypeScript (front-end/) do controle-financeiro — telas, hooks, services, consumo da API e padrões existentes.
---

# Frontend

## Objetivo

Corrigir e desenvolver telas em `front-end/` mantendo os padrões já
consolidados (`pages/` + `hooks/use-*` + `services/` + `components/{layout,
dashboard,ui,...}`), sem introduzir arquitetura paralela.

## Situações de uso

- Corrigir problemas comprovados (ver tabela FIX-* em
  `requisitos/references/status-requisitos.md`): dashboard ignora
  `selectedWallet`, falta `.catch` no carregamento, textos de período
  estáticos, `catch` silencioso em categorias.
- Consumir endpoint novo ou ainda não usado (ex.: `GET /…/summary`).
- Novas telas/fluxos da Parte Final (ex.: relatórios), sempre em conjunto
  com a skill `parte-final`.
- Refatoração de formulário/validação.

## Regras de execução (comuns)

1. Leia o `AGENTS.md` na raiz antes de decidir (idioma pt-BR, alias `@`,
   interceptor 401, chaves `token`/`user` no localStorage).
2. Inspecione o arquivo real antes de propor a mudança e cite os trechos
   atuais. Não presuma comportamento não verificado.
3. Implemente somente o escopo solicitado; escopo maior = perguntar antes.
4. Respeite a arquitetura atual: nenhuma biblioteca nova sem justificar;
   nada de renomear rotas ou reorganizar pastas.

## Regras específicas

- **Carteira**: qualquer chamada dependente de carteira usa
  `selectedWallet.id` de `contexts/wallet-context` e passa o id explícito
  para o service — nunca re-leia o cache de `localStorage`
  (`getOrCreateDefaultWallet`) por conta própria (fonte do bug FIX-DASH-CACHE).
- **Estado de erro**: todo `useEffect` que carrega dados segue o padrão de
  `hooks/use-transactions.ts` (linhas 48–68): `loading` + `loadError` +
  `.catch` que exibe o erro. Proibido `catch` silencioso
  (ex.: `.catch(() => setCategories([]))`); um estado vazio legítimo só
  quando a API respondeu com sucesso e a lista veio vazia.
- **Indicadores financeiros**: use `fetchSummary`
  (`services/transactions.ts:106`) — não recalcula totais em loop sobre
  transações paginadas (FIX-DASH-SUMMARY).
- **Recortes temporais**: títulos e subtítulos que citam período ("do mês",
  "agosto de 2026") derivam do parâmetro realmente enviado à API, nunca
  texto fixo (FIX-TEXTO-MES).
- **Validação**: tudo passa por `lib/validation.ts` (`emailSchema`,
  `passwordSchema`, ...). Estender lá, nunca redefinir por tela.
- **`components/ui/*`**: primitivos shadcn/Radix — trate como biblioteca;
  componha componentes da aplicação sobre eles.
- **Menu de negócio**: `isWalletOwnedBy` compara por `nome`
  (`services/wallets.ts:43`) — ao tocar nesse código, propor comparação por
  id quando o backend expuser o id do dono, e sinalizar antes de mudar.
- **baseURL**: `lib/api.ts` fixa `http://localhost:8080`. A migrção para
  `.env`/`VITE_API_URL` é **[recomendação]** da Parte 2 §7 — só execute com
  aprovação explícita do usuário.

## Testes e critérios de conclusão

- `cd front-end && npm run lint && npm run build` verdes (o build roda
  `tsc -b`, que também valida tipos).
- Para cada mudança: descrever o fluxo verificado manualmente (tela →
  chamada → comportamento) e citar os arquivos alterados.
- Nenhum `catch` silencioso introduzido; nenhum `.catch` removido.
- Requisito atingido citado por id e tabela de status atualizada
  (skill `requisitos`).
