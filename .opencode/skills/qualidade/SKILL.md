---
name: Qualidade
description: Testes, revisão de código e validação de requisitos do controle-financeiro — auditar mudanças, reproducir bugs e decidir se um requisito pode ser marcado como concluído.
---

# Qualidade

## Objetivo

Validar de forma independente o trabalho das outras skills: revisar
mudanças, reproduzir bugs com evidência e declarar se um requisito avança
(✅), regredirá (⚠️) ou permanece (❌) na tabela de status.

## Situações de uso

- Revisão após uma implementação (antes de atualizar
  `requisitos/references/status-requisitos.md`).
- Auditoria "algo quebrou?" — verificar integração frontend↔backend de ponta
  a ponta.
- Fechamento de tarefa: triagem do que é [explícito] vs [recomendação] e do
  que ainda falta para a Parte Final.

## Contexto

- Backend: `cd back-end && ./mvnw test` (H2 em memória — não precisa de
  MySQL); teste único: `./mvnw test -Dtest=TransacaoServiceTest`.
  Todo `@SpringBootTest` novo exige `@ActiveProfiles("test")`.
- Testes existentes: `IntegracaoApiTests` (fluxo ponta a ponta),
  `service/*Test` (Transacao, Categoria, Carteira, Autenticacao),
  `security/RateLimitFilterTest`.
- Formato de erro da API: `{timestamp, status, error, message}` — os testes
  que esperam erro devem validar esse corpo.
- Frontend: verificação = `npm run lint` + `npm run build` (não há runner de
  testes). Adicionar Vitest é **[recomendação]** — só com aprovação do usuário.
- Base de comparação: `requisitos/references/status-requisitos.md`.

## Regras de execução

1. Revise antes de editar: esta skill propõe, não aplica. Edição só quando a
   tarefa pedida for explicitamente "aplicar a revisão".
2. Todo bug reportado precisa de: passos de reprodução, evidência
   (`arquivo:linha`), severidade e id do requisito atingido.
3. Proibido inventar correção sem problema comprovado (não "conserto por
   precaução"). Divergência suspeita ainda não reproduzida = citar como
   **não confirmado**.
4. Para cada correção, quando possível, aponte o teste que falharia sem ela
   (backend) ou o cenário manual que a detectaria (frontend).
5. Compare a mudança com a arquitetura vigente (camadas no backend,
   hooks/services no frontend) e sinalize desvios.
6. Só eleve um status para ✅ nos requisitos da Parte Final com evidência de
   execução funcional (não vale "código parece correto").

## Critérios de conclusão

- Relatório com: itens verificados, achados (com evidência), pendências
  classificadas (confirmado / não confirmado) e recomendação de status
  para a tabela de `requisitos`.
- Nenhuma pendência "assumida como ok".
- Comandos executados citados com resultado (`mvnw test` verde, `npm run
  build` verde, ...).
