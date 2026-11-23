# Front-end - Sistema de Gestao Financeira

SPA em React 19 + TypeScript + Vite.

## Como rodar
```bash
npm install
npm run dev
```
App: `http://localhost:5173`

## Variaveis de ambiente
Ver `.env.example`. Principal: `VITE_API_URL` (URL do back-end).

## Estrutura de pastas
- `components/` - componentes reutilizaveis
- `pages/` - telas
- `hooks/` - logica de tela
- `services/` - chamadas HTTP
- `contexts/` - contexto de autenticacao e carteira
- `lib/` - utilitarios e validacao
- `types/` - tipos

## Testes
```bash
npm run test
```

## Build e lint
```bash
npm run lint
npm run build
```

## Deploy
Hospedado na Vercel. Rewrite de SPA configurado em `vercel.json`. PWA habilitado.