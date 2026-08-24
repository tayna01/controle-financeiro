## Aplicação de Controle Financeiro Pessoal e Compartilhado

## Frontend com React - Telas de Autenticação e Dashboard

## 1. Visão geral da atividade

Você vai construir, do zero, uma aplicação web de controle financeiro pessoal e compartilhado. A proposta é simples: um sistema onde o usuário cadastra receitas e despesas, visualiza resumos e compartilha sua carteira com outras pessoas para que elas possam visualizar e colaborar também. Ao longo do ano o projeto vai crescer e a cada parte entregue, você adiciona uma camada nova sobre o que já existe.

Na primeira parte será desenvolvido algumas telas do front-end. O backend em Spring Boot será desenvolvido nas próximas semanas. Por isso, toda comunicação com serviços externos será simulada localmente com dados fictícios (mock). Essa não é uma limitação: é uma prática real de mercado que permite que times de frontend e backend trabalhem em paralelo.

Não há um layout imposto. Você escolhe as cores, tipografia, nome do sistema e identidade visual. O que importa é que as funcionalidades estejam corretas, o código esteja organizado e a experiência do usuário seja

agradável e coerente.

## O que você vai entregar nesta parte

- Tela de login

- Tela de cadastro de novo usuário

- Fluxo de recuperação de senha (2 etapas)

- Tela de alteração de senha (área autenticada)


- Dashboard inicial com resumo financeiro (área autenticada)

Todas as telas devem funcionar de forma integrada: navegação sem recarregar a página, proteção de rotas autenticadas, persistência de sessão e feedback visual adequado para cada ação do usuário.

## Organização do código

O projeto deve ter uma estrutura que faça sentido e que você consiga explicar. Componentes reutilizáveis separados de páginas, lógica de serviço separada da interface, constantes e utilitários em seus próprios arquivos. Um colega deve conseguir navegar pelo repositório sem precisar perguntar onde está cada coisa.

## Entregáveis e avaliação

| Item | Peso |
| --- | --- |
| Funcionalidade: todas as telas navegáveis e com | 35% |
| comportamentos corretos |   |
| Validações: todos os formulários com regras e | 20% |
| mensagens de erro adequadas |   |
| Qualidade de código: organização, legibilidade, sem | 20% |
| duplicação desnecessária |   |
| Experiência do usuário: feedback de | 15% |
| loading/erro/sucesso, responsividade |   |
| README com instruções de execução e decisões de | 10% |
| projeto |   |

O conceito de funcionalidade considera o conjunto, não tela por tela.

## 2. Login


A tela de login será o primeiro contato do usuário com a aplicação. O design é livre, mas a experiência precisa ser clara: o usuário deve entender imediatamente o que fazer, receber um retorno visual enquanto aguarda e saber exatamente o que deu errado quando algo falha.

## O que implementar

- Formulário com campo de e-mail e senha

- Botão de submissão com estado de carregamento (spinner ou texto alternativo enquanto aguarda a resposta)

- Exibição clara de erros de validação e de falha na autenticação

- Opção de mostrar/ocultar o conteúdo do campo de senha

- Link para a tela de recuperação de senha

- Link para a tela de cadastro

## Comportamento esperado

Ao submeter o formulário com credenciais válidas, o usuário é redirecionado para o dashboard. Com credenciais inválidas, uma mensagem de erro deve aparecer sem apagar o que foi digitado. Se o usuário já estiver autenticado e tentar acessar /login, deve ser redirecionado automaticamente para o dashboard. A sessão deve persistir entre recarregamentos da página (use localStorage para guardar o token).

## Validações

| Campo | Regra | Mensagem sugerida |
| --- | --- | --- |
| E-mail | Obrigatório + formato válido | Campo obrigatório / |
|   |   | E-mail inválido |
| Senha | Obrigatória + mínimo 6 | Campo obrigatório / |
|   | caracteres | Mínimo 6 caracteres |


## 3. Cadastro de Novo Usuário

A tela de cadastro permite que novos usuários criem sua conta. Além das validações básicas de formulário, dê ao usuário um feedback em tempo real - especialmente sobre a força da senha escolhida. Uma boa tela de cadastro orienta o usuário enquanto ele preenche, não só depois que ele clica no botão.

## O que implementar

- Formulário com os dados necessários para criar uma conta (defina você quais campos fazem sentido para o seu sistema — nome, e-mail e senha são o mínimo)

- Indicador visual de força da senha em tempo real (enquanto o usuário digita)

- Confirmação de senha com validação de igualdade entre os campos

- Tratamento do caso em que o e-mail informado já está cadastrado

- Link para a tela de login para quem já tem conta

## Comportamento esperado

Ao cadastrar com sucesso, o usuário deve receber um retorno positivo e ser direcionado para o login (ou direto para o dashboard — você decide, mas o comportamento deve ser consistente). Se o e-mail já existir, o erro deve ser exibido sem apagar os demais campos. As senhas que não coincidem devem gerar erro imediatamente, de preferência ao sair do segundo campo.

## Indicador de força da senha

Implemente um componente que avalie a senha em tempo real e mostre ao usuário se ela é fraca, média ou forte. Os critérios de avaliação são livres


\- use o que fizer sentido para o seu projeto. O visual também é livre: pode ser uma barra colorida, ícones, texto, ou qualquer combinação. O importante é que seja reativo e útil.

## Validações mínimas

| Campo | Regra mínima |
| --- | --- |
| Nome (ou | Obrigatório |
| equivalente) |   |
| E-mail | Obrigatório + formato válido + único |
| Senha | Obrigatória + critérios de força a sua escolha |
| Confirmação de senha Deve ser igual à senha (validação cross-field) |   |

## 4. Recuperação de senha

O fluxo padrão na web é: o usuário informa o e-mail, recebe um link por e-mail (com um token único), clica no link e define uma nova senha. Nesta atividade você vai simular esse fluxo completo — sem enviar e-mail de verdade, mas respeitando toda a lógica.

## O que implementar

## Etapa 1 — Solicitar recuperação

- Tela acessível em /recuperar-senha

- Campo para o usuário informar o e-mail cadastrado

- Ao submeter, exibir uma mensagem de confirmação neutra — não revelar se o e-mail existe ou não (isso é uma boa prática de segurança: um atacante não deve conseguir descobrir quais e-mails estão cadastrados no sistema)

- Link para voltar ao login


## Etapa 2 — Redefinir a senha

- Tela acessível em /redefinir-senha/:token — o token vem como parâmetro na URL

- Campos para nova senha e confirmação

- Ao submeter com sucesso, redirecionar para o login com mensagem de confirmação

- Se o token estiver ausente ou inválido, exibir mensagem de erro adequada

## Comportamento esperado

As duas etapas podem ser páginas separadas ou a mesma página com estados diferentes - você decide. O que importa é que o fluxo seja coerente: o usuário sai da etapa 1 com a expectativa de receber um link, e ao acessar a URL com o token (que você pode gerar e colocar manualmente para testar), consegue definir uma nova senha. No mock, qualquer token não vazio pode ser considerado válido.

## Validações

| Campo | Regra |
| --- | --- |
| E-mail (etapa 1) | Obrigatório + formato válido |
| Nova senha (etapa 2) Mesmas regras de força adotadas no cadastro |   |
| Confirmação (etapa | Deve ser igual à nova senha |
| 2) |   |

## Critérios de avaliação

| Aspecto | O que será observado |
| --- | --- |
| Fluxo completo | Ambas as etapas funcionam e estão conectadas de |
|   | forma coerente |
| Mensagem neutra na | Não revela se o e-mail existe ou não |
| etapa 1 |   |


| Leitura do token na | useParams() usado corretamente para ler o |
| --- | --- |
| URL | parâmetro de rota |
| Validação e feedback Regras aplicadas; sucesso e erro comunicados |   |
|   | claramente ao usuário |

## 5. Alteração de senha

Diferente da recuperação de senha, que serve para quem perdeu o acesso, a alteração de senha é uma funcionalidade da área autenticada. Por isso, antes de aceitar a mudança, o sistema deve confirmar que é realmente o dono da conta quem está fazendo a solicitação, pedindo a senha atual.

## O que implementar

- Tela acessível em /app/perfil/senha — rota protegida, somente para usuários logados

- Campo para a senha atual

- Campo para a nova senha (com as mesmas regras de força do cadastro)

- Campo de confirmação da nova senha

- Feedback claro de sucesso ou erro

## Comportamento esperado

Se a senha atual estiver incorreta, exibir erro específico nesse campo. Se a nova senha não atender aos critérios de força ou a confirmação não coincidir, exibir os erros correspondentes. Em caso de sucesso, exibir confirmação e - a seu critério - manter o usuário na tela ou redirecionar para outra parte do sistema.

A rota deve ser protegida: qualquer tentativa de acessar /app/perfil/senha sem estar autenticado deve redirecionar para o login.


## Validações

| Campo | Regra |
| --- | --- |
| Senha atual | Obrigatória + verificada contra o mock (simular |
|   | comparação com a senha do usuário logado) |
| Nova senha | Obrigatória + mesmos critérios de força do |
|   | cadastro |
| Confirmação | Deve ser igual à nova senha |

## Critérios de avaliação

| Aspecto | O que será observado |
| --- | --- |
| Proteção da rota | Usuário não autenticado é redirecionado para o |
|   | login |
| Verificação da senha | Erro específico e descritivo quando a senha atual |
| atual | está incorreta |
| Validações | Força da nova senha e confirmação funcionando |
|   | corretamente |
| Feedback | Mensagem de sucesso clara após a alteração |

## 6. Dashboard

O dashboard é a tela principal da área autenticada. É o lugar onde o usuário chega logo após o login e onde ele encontra uma visão geral da sua situação financeira. Nesta parte do projeto os dados são todos mockados, mas o dashboard já deve ter a estrutura e os componentes que, futuramente, receberão dados reais da API.

## O que implementar

Estrutura da página


- Uma área de navegação lateral ou superior que será o ponto de acesso para as demais seções da aplicação (transações, categorias, perfil, etc.) - mesmo que essas páginas ainda não existam

- Área de conteúdo principal onde o resumo e os gráficos são exibidos

- Identificação do usuário logado em algum ponto da interface (nome, avatar ou inicial)

- Opção de logout

## Resumo financeiro

- Pelo menos três indicadores numéricos: saldo atual, total de receitas e total de despesas (do período - você define se é mês atual, últimos 30 dias ou outra janela)

- etc

## Gráfico

- Um gráfico - o tipo e o que ele representa são escolha sua

- Dados mockados, mas realistas (não use valores absurdos ou todos iguais)

- Legenda ou título que explique o que o gráfico mostra

## Lista de lançamentos recentes

- Os últimos lançamentos (receitas e despesas) em formato de lista ou tabela

- Cada item deve mostrar pelo menos: descrição, valor e data

- Diferenciação visual entre receitas e despesas (cor, ícone ou outro recurso)

## Comportamento esperado


O dashboard deve carregar os dados ao montar o componente (usando o mock com delay simulado) e exibir um indicador de carregamento enquanto aguarda. Isso prepara o código para quando os dados vierem de uma API real e a espera for verdadeira.

*Critérios de avaliação*

| Aspecto | O que será observado |
| --- | --- |
| Estrutura de layout | Navegação presente e área de conteúdo bem |
|   | definida; responsivo |
| Indicadores | Três ou mais métricas corretas, derivadas dos |
| financeiros | dados mock |
| Gráfico | Recharts implementado com dados reais do mock; |
|   | legível e com legenda |
| Lista de lançamentos Itens com as informações mínimas; receitas e |   |
|   | despesas diferenciadas visualmente |
| Estado de | Loading visível enquanto o mock resolve a Promise |
| carregamento |   |
| Logout funcional | Limpa o estado de autenticação e redireciona para |
|   | o login |

O dashboard é a tela mais aberta em termos de criatividade. Use-a para mostrar sua identidade no projeto. Um dashboard bem pensado, com hierarquia visual clara e dados que fazem sentido juntos, faz muita diferença na avaliação de experiência do usuário.

## 7. Prazo de Entrega


À definir!!

## 8. Recomendação de Tecnologias

[Vite: https://vite.dev/](https://vite.dev/)

[TypeScript: https://www.typescriptlang.org/](https://www.typescriptlang.org/)

Tailwindcss: https://tailwindcss.com/ [URL 🔗](https://tailwindcss.com/)

[MUI: https://mui.com/material-ui/](https://mui.com/material-ui/)

Chakra-UI: https://chakra-ui.com/ [URL 🔗](https://chakra-ui.com/)

[Shadcn: https://ui.shadcn.com/](https://ui.shadcn.com/)

Primereact: https://primereact.org/ [URL 🔗](https://primereact.org/)
