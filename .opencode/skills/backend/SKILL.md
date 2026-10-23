---
name: Backend
description: Desenvolvimento e correção da API Spring Boot (back-end/) do controle-financeiro — entidades, DTOs, services, controllers, segurança JWT e testes com H2.
---

# Backend

## Objetivo

Trabalhar em `back-end/` garantindo as camadas rígidas, os nomes em
português e o contrato já publicado ao frontend, sem quebrar o schema
existente do MySQL.

## Situações de uso

- Corrigir bug em endpoint, regra de negócio ou agregação (ex.:
  `SummaryResponse`).
- Novo endpoint da Parte Final (ex.: WebSocket/SSE, relatório, IA),
  sempre em conjunto com `parte-final` e `seguranca` quando tocar
  segurança.
- Refatoração de service, nova exceção, ajuste de DTO.

## Regras de execução (comuns)

1. Leia o `AGENTS.md` na raiz antes de decidir (camada rígida, DTOs,
   handler global, perfil de teste).
2. Inspecione o código real antes de propor mudança e cite os trechos
   atuais (`arquivo:linha`). Comportamento não verificado = **não
   confirmado**.
3. Implemente somente o escopo solicitado; ampliação = perguntar antes.
4. Respeite a arquitetura atual (`controller/dto/entity/exception/
   repository/security/service/config`); nenhuma dependência nova sem
   justificar no `pom.xml`.

## Regras específicas

- **Camadas**: controller só recebe e delega; toda lógica em `service/`;
  nenhuma entidade JPA retornada — sempre DTO; erros sempre via exceções
  capturadas pelo `@ControllerAdvice`
  (`ResourceNotFoundException`→404, `BusinessException`→422,
  `ConflictException`→409, formato `{timestamp, status, error, message}`).
- **Autorização**: verificação de papel/pertencimento no **service**, não
  na controller (padrão de `CarteiraService`).
- **Segurança**: qualquer mudança de rota pública/protegida passa pela
  skill `seguranca` (nunca afrouxar `SecurityConfig` sem justificar).
- **Nomes**: entidades, campos e rotas em português conforme o código
  existente; contratos já expostos ao frontend **não renomeiam** — pequena
  compatibilidade direta ou mapeamento no DTO.
- **Migração de schema**: `ddl-auto=update` sincroniza automaticamente;
  proibido introduzir `NOT NULL`/unique sem estratégia segura para dados
  existentes (sinalizar impacto no MySQL local do usuário).
- **Teste primeiro**: cada correção de bug vem acompanhada de um teste que
  falha antes do fix (unitário em `service/` ou em `IntegracaoApiTests`).
  Novo `@SpringBootTest` exige `@ActiveProfiles("test")`.

## Testes e critérios de conclusão

- `cd back-end && ./mvnw test` verde (executar sempre; teste único só se
  o escopo for local: `./mvnw test -Dtest=TransacaoServiceTest`).
- Swagger (`/swagger-ui.html`) reflete o recurso tocado (notas/anotações
  atualizadas em `OpenApiConfig` quando aplicável).
- Nenhuma quebra de contrato para o frontend: se o DTO mudou, a mudança
  correspondente em `front-end/src/services/` foi apontada (a executar
  pela skill `frontend` ou já feita).
- Requisito atingido citado por id; tabela de `requisitos` atualizada.
