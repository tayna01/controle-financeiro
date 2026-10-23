---
name: Segurança
description: Segurança entre as camadas do controle-financeiro — JWT, papéis das carteiras, rate limiting, OWASP, headers e gestão de segredos; auditar ou estender sem enfraquecer o que existe.
---

# Segurança

## Objetivo

Aplicar e auditar as regras de segurança já existentes e as exigidas pela
Parte Final (OWASP mínimo), atravessando backend e frontend sem inventar
arquitetura paralela.

## Situações de uso

- Qualquer mudança em `SecurityConfig`, `JwtAuthenticationFilter`,
  `RateLimitFilter`, `JwtService`, interceptors do axios.
- Nova rota sensível, novo fluxo de autenticação, integração de IA (chave
  de API), WebSocket/SSE (autorização por papel e por carteira).
- Revisão de correções de outras skills que toquem acesso a dados de
  outros usuários (broken access control).

## Contexto: o que já existe (não é tempo de reescrever)

- Rotas públicas: `/auth/**`, `/actuator/health`, Swagger. Demais rotas
  exigem JWT válido (`SecurityConfig.java` linhas 57–62, stateless).
- Headers: CSP, HSTS, `X-Frame-Options: DENY`, `X-Content-Type-Options`,
  Referrer-Policy.
- CORS restrito a `http://localhost:5173` (rever quando o deploy definir
  origens de produção — nunca `*` com credenciais).
- Rate limiting em `/auth/*` configurável (`AUTH_MAX_REQUESTS`,
  `AUTH_WINDOW_SECONDS`), com teste (`RateLimitFilterTest`).
- Autorização por papel **no service** (`CarteiraService`): DONO
  convida/edita/exclui; EDITOR gerencia transações; VISUALIZADOR só lê.
- Sessão no frontend: `token`, `user` (`auth.ts`) e `walletId`
  (`wallets.ts`) no localStorage; interceptor desconecta em 401 fora de
  `/auth/`.
- CSRF desabilitado por ser API stateless com Bearer token — decisão
  vigente, só reavaliar com o usuário.

## Regras de execução

1. Toda decisão de segurança se classifica: **[explícito]** (Parte Final
   §1.6) ou **[recomendação]** — e [recomendação] só vira trabalho com
   aprovação do usuário.
2. Proibido enfraquecer para "facilitar" feature: não desabilitar rate
   limit, CSP, nem abrir origens do CORS sem justificativa e aprovação.
3. Segredos: nunca hardcoded, nunca em exemplo real no README/environment
   committed. Chaves de IA/do deploy entram via variável de ambiente
   (`{env:VAR}` em config, `.env` ignorado pelo git).
4. WebSocket/SSE: antes de implementar, definir como a conexão autentica
   (token na query/handshake ou mecanismo equivalente) e como o papel da
   carteira é verificado no servidor — documentar a decisão.
5. Validar sempre o lado "do outro": mudança de rota no backend exige
   conferir consumo no frontend e vice-versa.

## Testes e critérios de conclusão

- Comportamento de segurança trocado por teste quando lógico
  (ex.: `RateLimitFilterTest`, testes de `CarteiraService` para 403 via
  `AccessDeniedException`).
- Requisito OWASP atingido citado por id (PF-OWASP) e refletido no README
  quando a prática mudar.
- Nenhum segredo adicionado ao repositório; nenhuma superfície nova
  (endpoint/rota/conexão) sem classificação pública/protegida explícita.
