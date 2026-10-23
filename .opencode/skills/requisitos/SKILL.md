---
name: Requisitos do Projeto
description: Mapa rastreável dos requisitos das três partes da disciplina (Partes 1, 2 e Final) do controle-financeiro — consultar antes de qualquer implementação ou classificação de status.
---

# Requisitos do Projeto

## Objetivo

Manter um único mapa vivo de requisitos × status do qual todas as outras skills
do projeto consultam antes e depois de qualquer mudança de código.

## Situações de uso

- Antes de qualquer implementação ou correção em qualquer camada — para saber
  qual requisito a tarefa atinge e qual é o escopo autorizado.
- Quando o usuário citar um requisito da disciplina ("Parte 2", "requisito da
  Parte Final", "OWASP", ...) — para traduzir a menção em um id rastreável.
- Ao encerrar uma tarefa — para atualizar a tabela de status com evidência.

## Fontes autoritativas

Os requisitos são os três documentos oficiais, convertidos em Markdown:

- `docs/extraidos/Atividade Final - Parte 1.md` — frontend (login, cadastro,
  recuperação e alteração de senha, dashboard).
- `docs/extraidos/Atividade Final - Parte 2.md` — backend (Spring Boot, JWT,
  entidades, endpoints, validações, CORS, README).
- `docs/extraidos/Atividade Final - Parte Final.md` — integração, tempo real
  (WebSocket/SSE), relatórios, IA, PWA, OWASP, testes, deploy, documentação.
- `AGENTS.md` na raiz — convenções do repositório (idioma, camadas, execução).

Todos os caminhos acima são relativos à raiz do repositório.

## Contexto: tabela de status viva

Os ids, tipos e status atuais ficam em `references/status-requisitos.md`
(resolvido relativo a este diretório). Ao usar esta skill, leia esse arquivo.
Resumo do estado da arte no momento da criação (detalhes no arquivo):

- Partes 1 e 2: essencialmente implementadas; correções pontuais comprovadas
  no dashboard (dashboard não consome `GET /api/v1/wallets/{id}/summary`,
  falta `.catch` no carregamento, textos com recorte temporal estático).
- Parte Final: 1.2 (tempo real), 1.3 (relatórios), 1.4 (IA), 1.5 (PWA) e 1.8
  (deploy público) **ausentes** — modo desenvolvimento, não modo correção.

## Regras de execução

1. Leia o `AGENTS.md` na raiz antes de qualquer decisão — esta skill não
   repete suas convenções.
2. Distingua sempre a natureza do item:
   - **[explícito]** — exigência dos documentos da disciplina; não negociável
     sem o usuário fornecer nova instrução da matéria.
   - **[recomendação]** — boa prática sugerida (pelos docs ou por análise
     técnica); só se converte em trabalho com aprovação do usuário.
3. Nunca afirme status de requisito sem evidência: cite `arquivo:linha`
   (código), `ClasseTeste.metodo` (teste) ou saída de comando executada.
   Se algo só foi deduzido por leitura estática e não executado, classifique
   como **não confirmado** — não presuma que funciona.
4. A Partes 1 e 2 operam em modo **correção** (somente problemas comprovados);
   a Parte Final em modo **desenvolvimento** (escopo novo). Não trate o
   projeto como concluído: a conformidade atual não cobre a Parte Final.
5. Toda sugestão de mudança deve citar o id do requisito que ela atinge
   (ex.: "esta correção cobre `P2-SUMMARY`"). Se não atingir nenhum id,
   classifique como manutenção técnica e justifique.
6. Ao encerrar uma tarefa, atualize `references/status-requisitos.md`:
   novo status, evidência e data. Se a atualização indicaria ✅ para um
   requisito [explícito] da Parte Final, exija antes evidência de execução
   (funcionalidade real, não "hello world").
7. Os documentos em `docs/extraidos/` são apenas leitura: nunca os edite.

## Critérios de conclusão

- Dado qualquer id (`P1-LOGIN`, `P2-SUMMARY`, `PF-IA`, ...), é possível saber
  em uma leitura: fonte, tipo, status e evidência.
- A tabela nunca contradiz o código sem justificativa registrada.
- Toda task encerrada está refletida na tabela com data e evidência.
