---
name: Parte Final
description: Implementação dos requisitos ainda ausentes da Parte Final do controle-financeiro — tempo real (WebSocket/SSE), relatórios, IA, PWA, deploy e documentação final.
---

# Parte Final

## Objetivo

Conduzir o desenvolvimento dos blocos da Parte Final que **não existem
hoje**: tempo real (1.2), relatórios (1.3), IA (1.4), PWA (1.5) e deploy
público (1.8), fechando a documentação (1.9). Escopo novo — modo
**desenvolvimento**, não correção.

## Situações de uso

O usuário decidir iniciar qualquer bloco da Parte Final, ou pedir um plano
de um deles.

## Estado atual (não considerar concluído)

- PF-INTEGRACAO (1.1): ✅ — frontend já consome API real.
- PF-TEMPOREAL (1.2), PF-RELATORIOS (1.3), PF-IA (1.4), PF-PWA (1.5),
  PF-DEPLOY (1.8): ❌ — nada implementado.
- PF-OWASP (1.6): ✅ — não regressar.
- PF-PADROES (1.7): camadas ✅; testes frontend são [recomendação].
- PF-DOC (1.9): README e Swagger ✅; faltam seções de IA/deploy.

Checklists operacionais por bloco: `references/checklists.md` (relativo a
este diretório). Ids e status: skill `requisitos`.

## Regras de execução

0. Leia o `AGENTS.md` na raiz e o `requisitos/references/status-requisitos.md` antes de planejar.
1. Primeira tarefa de cada bloco = **plano** aprovado pelo usuário: ids
   `PF-*` envolvidos, ordem de commits, arquivos prováveis, riscos.
   Nenhuma implementação antes da aprovação.
2. Um bloco por vez: mínimo funcional antes de "enfeite". Ao término do
   mínimo, parar e reportar — extras só com aprovação.
3. Cada bloco exige cooperação:
   - **Tempo real**: skill `seguranca` (autenticação da conexão +
     segregação por carteira/papel) e `backend` (nova dependência no
     `pom.xml` precisa justificar).
   - **IA**: skill `seguranca` (chave apenas via env) + `backend` (rota) +
     `frontend` (tela) + README com instruções de configuração de chave.
   - **PWA**: `frontend` (`manifest.json`, service worker com pelo menos um
     nível offline) — confirmar política de cache com o usuário.
   - **Deploy**: `seguranca` (segredos, CORS de produção) + README com link.
4. Feature "real, não decorativa": cada bloco se conecta a uma
   funcionalidade existente (ex.: tempo real ao lançamento de transações
   por membros; IA ao fluxo de gastos do usuário). Proibido "hello world".
5. Só elevar status ✅ com evidência de execução funcional (demo contada
   no usuário ou saída de teste/execução citada).

## Critérios de conclusão (por bloco)

- Funcionalidade real integrada (não exemplo isolado).
- Testes relevantes, quando aplicável (backend), e `npm run lint && npm run
  build` verdes.
- README (e Swagger/manifest conforme o caso) atualizados, incluindo as
  variáveis de ambiente no README em tabela como as existentes.
- Tabela de `requisitos` atualizada com evidência e data.
