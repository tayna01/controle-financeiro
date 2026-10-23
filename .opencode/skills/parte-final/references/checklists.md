# Checklists operacionais — Parte Final

Complemento da skill `parte-final`. Cada bloco traz: objetivo, opção técnica
sugerida para ESTE repositório, pré-requisitos, passos mínimos e critérios de
aceite. As opções são **sugestões** — decisão final com o usuário.

## PF-TEMPOREAL — WebSocket/SSE em ponto real (1.2) [explícito]

- **Objetivo**: atualização em tempo real em algum fluxo real da carteira
  compartilhada.
- **Opção sugerida**: SSE (`SseEmitter`, sem dependência extra) ou
  STOMP/WebSocket (`spring-boot-starter-websocket`). Evento mínimo:
  "transação criada na carteira X" → membros conectados atualizam
  dashboard/lista sem recarregar.
- **Pré-requisitos**: do backend, evento publicado em
  `TransacaoService.create/update/delete`; do frontend, assinatura por
  carteira selecionada; autenticação e papel verificados no servidor
  (skill `seguranca`).
- **Passos mínimos**: 1) decisão SSE vs WebSocket aprovada; 2) endpoint de
  assinatura autenticado por carteira; 3) publicação no service; 4)
  reativação do hook/dados no frontend; 5) teste de integração quando
  possível.
- **Aceite**: um membro `EDITOR` lança transação e o `VISUALIZADOR` vê sem
  recarregar; conexões de usuário sem acesso à carteira são rejeitadas.

## PF-RELATORIOS — ao menos um relatório (1.3) [explícito]

- **Objetivo**: relatório real (extrato de transações, resumo mensal ou por
  categoria).
- **Opção sugerida**: extrato com filtros + exportação CSV (e/ou PDF)
  servida pelo backend (endpoint autenticado por carteira), tela nova em
  `/app/relatorios`.
- **Pré-requisitos**: filtros já existentes em `TransacaoController`;
  `SummaryResponse.byCategory/byMonth` já agregados no backend (hoje sem
  consumo — FIX-DASH-SUMMARY resolve junto).
- **Aceite**: relatório gerado a partir de dados do banco, com os mesmos
  controles de acesso da carteira (VISUALIZADOR pode gerar; membro externo
  não).

## PF-IA — API de IA integrada (1.4) [explícito]

- **Objetivo**: recurso real com IA (categorização automática de
  transações, assistente de gastos, insights em linguagem natural).
- **Opção sugerida**: insight/consultor financeiro sobre os dados do
  `/summary` (Gemini, plano gratuito) OU categorização sugerida ao criar
  transação. Provedor livre; documentar chave no README.
- **Pré-requisitos**: chave via variável de ambiente (ex.: `AI_API_KEY`) —
  nunca no repositório (skill `seguranca`); decisão de timeout e fallback
  quando a API de IA estiver indisponível (não quebrar o fluxo principal).
- **Passos mínimos**: 1) bloqueio escolhido e aprovado; 2) rota backend +
  DTOs; 3) chamada de provedor isolada em classe de serviço própria; 4)
  tela/componente no frontend; 5) README com variáveis e instruções.
- **Aceite**: funcionalidade usável end-to-end com chave configurada;
  falha de IA não corrompe dados; README explica como configurar.

## PF-PWA — instalável e offline mínimo (1.5) [explícito]

- **Objetivo**: aplicação instalável com pelo menos um nível offline.
- **Opção sugerida**: `vite-plugin-pwa` (manifest + service worker + cache
  de assets); alternativa manual (`manifest.json` + SW próprio).
- **Passos mínimos**: manifest (nome, ícones, cores da identidade violeta);
  SW com precache de assets estáticos e/ou fallback da última tela;
  validação de instalabilidade (Chrome/Lighthouse).
- **Aceite**: instalável no navegador; assets servidos offline; nada de
  cache de dados autenticados sem revisão (skill `seguranca`).
- **Aprovação**: política de cache escolhida pelo usuário antes de codar.

## PF-DEPLOY — ambiente público (1.8) [explícito]

- **Objetivo**: link público funcional no README.
- **Opção sugerida**: frontend Vercel/Netlify + backend Render/Railway/Fly
  + banco gerenciado; hoje só há MySQL local — migrar dialect/URL via
  variáveis de ambiente (sem trocar de ORM).
- **Passos mínimos**: 1) contas/recursos decididos; 2) variáveis de
  ambiente no provedor (secrets, nunca em arquivo commitado); 3) CORS de
  produção revisado (skill `seguranca`); 4) README com o link e pré-
  requisitos atualizados; 5) smoke test listado no ambiente.
- **Aceite**: link no README funcional; login/lançamento/compartilhamento
  operam no ambiente público.

## PF-DOC — documentação final (1.9) [explícito]

- **Passos mínimos**: README com descrição, arquitetura, execução local,
  variáveis de ambiente (incluindo IA/deploy), link do deploy e decisões
  de projeto novas; Swagger atualizado.
- **Aceite**: um leitor novo roda o projeto só com o README.

## Ordem sugerida de execução (a ratificar com o usuário)

1. Resolver os FIX-* comprovados (`frontend`) — base íntegra.
2. PF-TEMPOREAL (eleva o valor do compartilhamento já existente).
3. PF-RELATORIOS (reaproveita `/summary` após FIX-DASH-SUMMARY).
4. PF-IA (independente; precisa de decisão de provedor/chave).
5. PF-PWA (baixo risco, alto valor na apresentação).
6. PF-DEPLOY por último (ambiente estável), depois PF-DOC final.
