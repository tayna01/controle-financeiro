Disciplina de Programação para Web

## Atividade Final — Parte 2

## Especificação do Backend

Sistema de Gestão Financeira Compartilhada

Spring Boot • Spring Security • JPA/Hibernate • PostgreSQL/MySql

O diagrama e as especificações apresentados constituem apenas uma base mínima para esta etapa do projeto. Cada aluno deverá propor e implementar funcionalidades adicionais que agreguem identidade, criatividade e personalidade ao seu sistema.

Um bom projeto não se limita a atender aos requisitos solicitados: ele entrega além do esperado.

Ressalta-se que diversos conhecimentos e técnicas exigidos neste documento ainda serão abordados ao longo das aulas e deverão ser incorporados gradualmente ao desenvolvimento do projeto, como por exemplo,segurança,tratamento personalizado de exceções, etc. Neste momento, procurem implementar os CRUDs.

## 1. Visão Geral da Atividade

Nesta segunda parte do projeto, você vai implementar o backend da aplicação de gestão financeira pessoal e compartilhada. O frontend desenvolvido na


Parte 1 com dados mockados agora será conectado a uma API REST real, construída com Spring Boot.

O backend é responsável por toda a lógica de negócio, persistência de dados e segurança da aplicação.

## 1.1 O que você vai entregar

- API REST documentada e funcional

- Autenticação e autorização com JWT

- CRUD completo de categorias, transações, carteiras, entre outras.

- Lógica de compartilhamento de carteiras entre usuários

- README com instruções de execução e variáveis de ambiente

## 1.2 Tecnologias obrigatórias

| Tecnologia | Finalidade |
| --- | --- |
| Spring Boot 3.x | Framework principal da aplicação |
| Spring Security + JWT | Autenticação e autorização |
| Spring Data JPA / Hibernate | Persistência de dados |
| PostgreSQL/Mysql ou outro de | Banco de dados relacional |
| preferência |   |
| Maven | Gerenciamento de dependências |
| SpringDoc / Swagger | Documentação automática da API |

## 1.3 Avaliação


| Item | Peso |
| --- | --- |
| Funcionalidade: todos os endpoints corretos | 40% |
| e integrados ao frontend. Domínio da |   |
| implementação. |   |
| Segurança: autenticação JWT e proteção de | 20% |
| rotas implementadas corretamente |   |
| Qualidade de código: organização em camadas, | 30% |
| sem lógica na controller, etc. Domínio da |   |
| implementação. |   |
| README com instruções claras de execução e | 10% |
| configuração |   |

## 2. Diagrama de Classes

O diagrama abaixo representa a estrutura de classes sugerida para o backend. ELE NÃO É E NÃO DEVE SER DEFINITIVO: você pode e deve adaptá-lo conforme as decisões do seu projeto — adicionar atributos, criar novas classes, incluir novos valores nos enums ou reestruturar relacionamentos. Utilize-o apenas como base.

Cada classe é apresentada com seus atributos mínimos e os relacionamentos com as demais. A linha marcada com (+) ao final de cada tabela indica que campos extras são bem-vindos.

💡 Este diagrama representa a camada de entidades Java (JPA), não o schema SQL diretamente. Os nomes dos atributos seguem a convenção camelCase do Java; o mapeamento para colunas do banco fica por conta das anotações @Column.


## 2.1 Usuario

| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| nome | String | Obrigatório |
| email | String | Obrigatório, único no |
|   |   | sistema |
| senhaCriptografada | String | Obrigatório — nunca |
|   |   | armazene a senha em texto |
|   |   | puro |
| criadoEm | LocalDateTime | Preenchido |
|   |   | automaticamente na |
|   |   | criação |
| atualizadoEm | LocalDateTime | Atualizado a cada |
|   |   | alteração |
| (+) outros atributos | — | Você pode adicionar foto, |
|   |   | telefone, preferências |
|   |   | etc. |

Relacionamentos: um Usuario possui muitas Carteiras (como dono), pertence a muitas CarteiraMembro e pode ter muitas Categorias.

## 2.2 Categoria


| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| usuario | Usuario | Dono da categoria — |
|   |   | @ManyToOne obrigatório |
| nome | String | Obrigatório |
| tipo | TipoTransacao | Enum: RECEITA ou DESPESA |
| cor | String | Opcional — código hex, |
|   |   | ex: #FF5733 |
| icone | String | Opcional — nome do ícone |
|   |   | ou URL |
| (+) outros atributos | — | Você pode adicionar ordem |
|   |   | de exibição, |
|   |   | ativo/inativo etc. |

💡 Cada usuário tem suas próprias categorias. Considere pré-popular categorias padrão ao criar uma conta (ex: Salário, Alimentação, Transporte).

## 2.3 Carteira

| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| dono | Usuario | Quem criou a carteira — |
|   |   | @ManyToOne obrigatório |
| nome | String | Obrigatório |


| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| descricao | String | Opcional |
| criadoEm | LocalDateTime | Preenchido |
|   |   | automaticamente na |
|   |   | criação |
| membros | List<CarteiraMemb | Relacionamento @OneToMany |
|   | ro> | com CarteiraMembro |
| transacoes | List<Transacao> | Relacionamento @OneToMany |
|   |   | com Transacao |
| (+) outros atributos | — | Você pode adicionar |
|   |   | moeda, saldo inicial, cor |
|   |   | etc. |

## 2.4 CarteiraMembro

| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| carteira | Carteira | @ManyToOne — carteira à |
|   |   | qual o membro pertence |
| usuario | Usuario | @ManyToOne — o usuário |
|   |   | membro |
| papel | PapelCarteira | Enum: DONO, EDITOR ou |
|   |   | VISUALIZADOR |
| entradoEm | LocalDateTime | Data de ingresso na |
|   |   | carteira |


| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| (+) outros atributos | — | Você pode adicionar |
|   |   | convite pendente, data de |
|   |   | expiração etc. |

💡 Garanta unicidade do par (carteira, usuario) para evitar membros duplicados. Use @UniqueConstraint na anotação @Table ou uma constraint no banco.

## 2.5 Transacao

| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| carteira | Carteira | @ManyToOne — carteira à |
|   |   | qual pertence |
| categoria | Categoria | @ManyToOne opcional — |
|   |   | pode ser nulo |
| criadoPor | Usuario | @ManyToOne — quem |
|   |   | registrou a transação |
| tipo | TipoTransacao | Enum: RECEITA ou DESPESA |
| valor | BigDecimal | Obrigatório, maior que |
|   |   | zero |
| descricao | String | Opcional |
| data | LocalDate | Data efetiva da transação |
|   |   | — obrigatória |


| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| criadoEm | LocalDateTime | Preenchido |
|   |   | automaticamente na |
|   |   | criação |
| (+) outros atributos | — | Você pode adicionar |
|   |   | anexo, observações, |
|   |   | recorrência etc. |

## 2.6 TokenRedefinicaoSenha

| Atributo | Tipo Java | Observação |
| --- | --- | --- |
| id | Long | Chave primária, gerada |
|   |   | automaticamente |
| usuario | Usuario | @ManyToOne — a quem o |
|   |   | token pertence |
| token | String | Valor único gerado |
|   |   | aleatoriamente (UUID ou |
|   |   | similar) |
| expiraEm | LocalDateTime | Data e hora de expiração |
|   |   | — sugestão: 1 hora |
| utilizado | boolean | Indica se o token já foi |
|   |   | usado para redefinir a |
|   |   | senha |
| (+) outros atributos | — | Você pode adicionar IP de |
|   |   | origem, tipo de |
|   |   | solicitação etc. |


## 2.7 Enumerações

As enumerações abaixo são sugeridas. Você pode renomeá-las, adicionar valores ou criar novas conforme a necessidade do seu projeto.

| Enum | Valores sugeridos | Utilização |
| --- | --- | --- |
| TipoTransacao | RECEITA, DESPESA | Classifica categorias e |
|   |   | transações |
| PapelCarteira | DONO, EDITOR, | Define o nível de acesso |
|   | VISUALIZADOR | de cada membro na |
|   |   | carteira |

## 3. Arquitetura e Organização do Código

A aplicação deve seguir a arquitetura em camadas padrão do Spring Boot. Cada camada tem uma responsabilidade única e bem definida.

| Pacote / Camada | Responsabilidade |
| --- | --- |
| controller | Receber requisições HTTP, chamar o Service, |
|   | retornar ResponseEntity |
| service | Toda a lógica de negócio. Validações, |
|   | regras, orquestração |
| repository | Interfaces JPA que fazem queries no banco |
| entity | Classes JPA mapeadas para tabelas do banco |
| dto | Objetos de transferência de dados (request e |
|   | response) |


| Pacote / Camada | Responsabilidade |
| --- | --- |
| security | Configuração do Spring Security, filtro JWT, |
|   | UserDetailsService |
| exception | Classes de exceção customizadas e handler |
|   | global (ControllerAdvice) |
| config | Beans de configuração (CORS, Swagger, etc.) |

💡 Nunca coloque lógica de negócio na Controller. A Controller deve apenas delegar para o Service e devolver o resultado. Nunca retorne uma entidade JPA diretamente — sempre use DTOs.

## 3.1 Tratamento de erros

Implemente um @ControllerAdvice global que capture exceções e retorne respostas JSON padronizadas. Todos os erros da API devem ter este formato:

{ "timestamp": "2025-01-01T10:00:00", "status": 404, "error": "Not Found", "message": "Usuário não encontrado" }

Mapeie pelo menos as seguintes exceções:

| Exceção | Status HTTP |
| --- | --- |
| ResourceNotFoundException | 404 Not Found |
| BusinessException | 422 Unprocessable Entity |
| AccessDeniedException (Spring) | 403 Forbidden |
| MethodArgumentNotValidException (Bean | 400 Bad Request |
| Validation) |   |


| Exceção | Status HTTP |
| --- | --- |
| DataIntegrityViolationException | 409 Conflict |
| (e-mail duplicado) |   |

## 4. Autenticação e Segurança

A segurança é implementada com Spring Security e tokens JWT. Rotas públicas e protegidas devem ser configuradas explicitamente. Nenhum dado de outro usuário deve ser acessível sem a devida autorização.

## 4.1 Fluxo JWT

- O cliente faz POST /auth/login com e-mail e senha

- O backend valida as credenciais e retorna um accessToken (JWT) com validade de 24h

- O cliente inclui o token em todas as requisições no header: Authorization: Bearer <token>

- Um filtro (OncePerRequestFilter) intercepta cada request, valida o JWT e carrega o usuário no contexto do Spring Security

💡 O token JWT deve conter no payload: sub (userId), email e iat/exp. Use uma biblioteca como jjwt (io.jsonwebtoken).

## 4.2 Rotas públicas e protegidas


| Rota | Acesso |
| --- | --- |
| POST /auth/register | Pública |
| POST /auth/login | Pública |
| POST /auth/forgot-password | Pública |
| POST /auth/reset-password | Pública |
| GET /actuator/health | Pública |
| Qualquer outra rota /api/** | Requer JWT válido |

## 4.3 Regras de autorização por recurso

- Usuário só acessa suas próprias categorias

- Usuário só acessa carteiras das quais é membro (OWNER, EDITOR ou VIEWER)

- Somente o OWNER pode convidar membros, editar dados da carteira ou excluí-la

- EDITOR pode criar, editar e excluir transações da carteira

- VIEWER pode apenas visualizar as transações da carteira

- A verificação deve ser feita no Service, não apenas na Controller

## 5. Endpoints da API

Base path: /api/v1. Todos os endpoints (exceto os de autenticação) exigem o header Authorization: Bearer <token>.


## 5.1 Auth

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | /auth/register | Cria uma nova conta de usuário |
| POST | /auth/login | Autentica e retorna o JWT |
| POST | /auth/forgot-password | Inicia o fluxo de recuperação de |
|   |   | senha |
| POST | /auth/reset-password | Redefine a senha com token |
|   |   | válido |

## POST /auth/register

Request body:

```
{ "name": "João Silva", "email": "joao@email.com", "password": "Senha@123" }
```

Response 201 Created:

```
{ "id": 1, "name": "João Silva", "email": "joao@email.com", "createdAt":
"2025-01-01T10:00:00" }
```

Erros: 409 se e-mail já cadastrado. 400 se campos inválidos (Bean Validation).

## POST /auth/login

Request body:

```
{ "email": "joao@email.com", "password": "Senha@123" }
Response 200 OK:
{ "accessToken": "eyJhbGci...", "tokenType": "Bearer", "expiresIn": 86400 }
Erros: 401 se credenciais inválidas.
```


## POST /auth/forgot-password

```
Request body: { "email": "joao@email.com" }
```

Response 200 OK: Sempre retorna a mesma mensagem neutra, independente de o e-mail existir:

```
{ "message": "Se este e-mail estiver cadastrado, você receberá as instruções em breve." }
```

O backend deve gerar um token único, salvá-lo em PasswordResetToken com expiração de 1 hora, e — como não há envio real de e-mail — apenas retornar o token no corpo da resposta para fins de teste:

```
{ "message": "...", "debugToken": "abc123" }
```

💡 Em produção, o debugToken jamais seria retornado. É um facilitador apenas para os testes da atividade.

## POST /auth/reset-password

Request body:

```
{ "token": "abc123", "newPassword": "NovaSenha@456" }
```

Response 200 OK: { "message": "Senha redefinida com sucesso." }

Erros: 400 se token inválido, expirado ou já utilizado.

## 5.2 User (perfil do usuário autenticado)

| Métod | Rota | Descrição |
| --- | --- | --- |
| o |   |   |
| GET | /api/v1/users/me | Retorna dados do usuário |
|   |   | autenticado |


| Métod | Rota | Descrição |
| --- | --- | --- |
| o |   |   |
| PUT | /api/v1/users/me | Atualiza nome (e outros campos |
|   |   | opcionais) |
| PATCH /api/v1/users/me/password Altera a senha (exige senha |   |   |
|   |   | atual) |

## PATCH /api/v1/users/me/password

Request body:

{ "currentPassword": "Senha@123", "newPassword": "NovaSenha@456" }

Response 200 OK: { "message": "Senha alterada com sucesso." }

Erros: 422 se a senha atual estiver incorreta.

## 5.3 Categories

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | /api/v1/categories | Lista todas as categorias do |
|   |   | usuário autenticado |
| POST | /api/v1/categories | Cria uma nova categoria |
| PUT | /api/v1/categories/{id} | Atualiza uma categoria |
| DELETE | /api/v1/categories/{id} | Remove uma categoria (sem |
|   |   | transações vinculadas) |

Filtros aceitos em GET /categories: ?type=INCOME ou ?type=EXPENSE

Regra: não é possível excluir uma categoria que possui transações vinculadas. Retornar 422 com mensagem explicativa.


## 5.4 Wallets (carteiras)

| Métod | Rota | Descrição |
| --- | --- | --- |
| o |   |   |
| GET | /api/v1/wallets | Lista carteiras onde o |
|   |   | usuário é membro |
| POST | /api/v1/wallets | Cria uma nova carteira |
|   |   | (usuário vira OWNER) |
| GET | /api/v1/wallets/{id} | Detalha uma carteira (se for |
|   |   | membro) |
| PUT | /api/v1/wallets/{id} | Atualiza dados da carteira |
|   |   | (somente OWNER) |
| DELET | /api/v1/wallets/{id} | Remove a carteira e todos os |
| E |   | dados (somente OWNER) |

## 5.5 Wallet Members (membros)

| Métod | Rota | Descrição |
| --- | --- | --- |
| o |   |   |
| GET | /api/v1/wallets/{id}/members | Lista membros da carteira |
| POST | /api/v1/wallets/{id}/members | Adiciona membro por e-mail |
|   |   | (somente OWNER) |
| PATCH /api/v1/wallets/{id}/members/ |   | Altera o papel do membro |
|   | {userId} | (somente OWNER) |
| DELET | /api/v1/wallets/{id}/members/ | Remove membro (somente OWNER) |
| E | {userId} |   |


Request body para adicionar membro:

```
{ "email": "outro@email.com", "role": "EDITOR" }
```

Erros: 404 se o e-mail não estiver cadastrado. 409 se o usuário já for membro. 403 se quem chama não for OWNER.

## 5.6 Transactions (transações)

| Método Rota |   | Descrição |
| --- | --- | --- |
| GET | /api/v1/wallets/{walletId | Lista transações da carteira |
|   | }/transactions | (com filtros) |
| POST | /api/v1/wallets/{walletId | Cria uma transação (OWNER ou |
|   | }/transactions | EDITOR) |
| GET | /api/v1/wallets/{walletId | Detalha uma transação |
|   | }/transactions/{id} |   |
| PUT | /api/v1/wallets/{walletId | Atualiza uma transação (OWNER ou |
|   | }/transactions/{id} | EDITOR) |
| DELETE /api/v1/wallets/{walletId |   | Remove uma transação (OWNER ou |
|   | }/transactions/{id} | EDITOR) |

Filtros aceitos em GET /transactions:

- ?type=INCOME ou ?type=EXPENSE

- ?categoryId=5

- ?startDate=2025-01-01&endDate=2025-01-31

- ?page=0&size=20&sort=date,desc (paginação obrigatória)

Request body para criar/atualizar:


```
{ "type": "EXPENSE", "amount": 150.00, "description": "Supermercado", "date":
"2025-01-15", "categoryId": 3 }
```

## 5.7 Dashboard (resumo financeiro)

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | /api/v1/wallets/{walletId | Retorna resumo financeiro da |
|   | }/summary | carteira |

```
Query params: ?startDate=2025-01-01&endDate=2025-01-31
Response 200 OK (exemplo):
{ "totalIncome": 5000.00, "totalExpense": 3200.00, "balance": 1800.00,
"transactionCount": 42,
"byCategory": [ { "categoryId": 1, "categoryName": "Alimentação", "total": 800.00 }
],
"byMonth": [ { "month": "2025-01", "income": 5000.00, "expense": 3200.00 } ] }
```

## 6. Validações com Bean Validation

Use as anotações do Jakarta Bean Validation (@NotBlank, @Email, @Size, @Min, @NotNull, etc.) nos DTOs de request. O Spring irá retornar automaticamente 400 Bad Request com os erros quando a validação falhar.

| Campo | Anotações sugeridas |
| --- | --- |
| User.name | @NotBlank, @Size(min=2, max=100) |
| User.email | @NotBlank, @Email |


| Campo | Anotações sugeridas |
| --- | --- |
| User.password | @NotBlank, @Size(min=8, max=100) |
| Category.name | @NotBlank, @Size(max=80) |
| Category.type | @NotNull, @Pattern(regexp="INCOME|EXPENSE") |
| Transaction.amount | @NotNull, @DecimalMin("0.01") |
| Transaction.date | @NotNull, @PastOrPresent |
| Transaction.type | @NotNull, @Pattern(regexp="INCOME|EXPENSE") |

## 7. CORS e Integração com o Frontend

Configure o CORS para permitir requisições vindas do frontend React. A configuração deve ser feita via @Bean de WebMvcConfigurer ou @CrossOrigin — nunca desabilite a segurança do CORS em produção.

## Configuração mínima:

- Origens permitidas: http://localhost:3000

- Métodos permitidos: GET, POST, PUT, PATCH, DELETE, OPTIONS

- Headers permitidos: Authorization, Content-Type

- Expor o header Authorization nas respostas

💡 Ao integrar com o frontend, revise as URLs dos serviços mock que foram criados na Parte 1 e substitua-os pelas chamadas reais à API. Use variáveis de ambiente no React (.env) para configurar a baseURL da API.


## 8. README e Configuração

O README.md do repositório deve conter:

- Descrição breve da aplicação e da arquitetura

- Pré-requisitos (Java 21+, PostgreSQL, Maven/Gradle)

- Instruções para criar o banco de dados e configurar as variáveis de ambiente

- Como executar a aplicação localmente

- Link para a documentação Swagger (ex: http://localhost:8080/swagger-ui.html)

- Decisões de projeto relevantes (ex: por que escolheu determinada abordagem)

Variáveis de ambiente esperadas (via application.properties ou .env):

| Variável | Descrição |
| --- | --- |
| DB_URL | URL de conexão com o PostgreSQL (ex: |
|   | jdbc:postgresql://localhost:5432/findb) |
| DB_USERNAME | Usuário do banco |
| DB_PASSWORD | Senha do banco |
| JWT_SECRET | Chave secreta para assinar os tokens JWT |
|   | (mínimo 256 bits) |
| JWT_EXPIRATION | Tempo de expiração do token em milissegundos |
|   | (ex: 86400000) |

## 10. Prazo de Entrega


## À definir!

💡 Dúvidas sobre a especificação devem ser levadas para a aula ou enviadas pelo canal oficial da disciplina. Não interprete ambiguidades a seu favor — pergunte primeiro.
