# 🧩 E-COMMERCE MS

## 🎯 Objetivo

Este microserviço é responsável por gerenciar os principais recursos de um sistema de e-commerce, como autenticação, pedidos, produtos e relatórios administrativos. 

Ele centraliza funcionalidades essenciais da plataforma, incluindo:

- Autenticação e gerenciamento de sessão dos usuários.
- Criação, pagamento e consulta de pedidos realizados.
- Gerenciamento completo do catálogo de produtos.
- Relatórios administrativos, como maiores compradores, receita mensal, ticket médio.

---

## 🛠 Tecnologias Utilizadas

A aplicação foi desenvolvida utilizando as seguintes bibliotecas e frameworks Java:

### ☕ Spring Framework 3

- **spring-boot-starter-web**: Criação de APIs REST com suporte a controllers, JSON, etc.
- **spring-boot-starter-data-jpa**: Integração com o banco de dados via JPA (Hibernate).
- **spring-boot-starter-oauth2-resource-server**: Autenticação e autorização baseada em JWT e OAuth2.
- **spring-boot-starter-security**: Segurança da aplicação com autenticação, autorização e filtros.
- **spring-boot-starter-validation**: Validação de dados com Bean Validation (Hibernate Validator).
- **springdoc-openapi-starter-webmvc-ui**: Geração automática da documentação OpenAPI/Swagger.

### 🛢 Banco de Dados

- **flyway-core**: Versionamento de banco de dados com scripts de migração.
- **flyway-mysql**: Extensão Flyway específica para MySQL.
- **mysql-connector-j**: Driver JDBC para comunicação com banco de dados MySQL.
- **h2**: Banco de dados em memória usado para testes unitários e de integração.

### 🧰 Utilitários

- **lombok**: Redução de boilerplate com anotações como `@Getter`, `@Setter`, `@Builder`, etc.

### ✅ Testes

- **spring-boot-starter-test**: Framework de testes com suporte a JUnit, Mockito, etc.
- **spring-security-test**: Suporte a testes de segurança (mock de autenticação, autorização, etc).

---

## 📦 Clonando o Repositório

Clone o repositório do GitHub para sua máquina local:

```bash
git clone https://github.com/luanfulnazari/e-commerce-ms.git
```

---

## 🔐 Variáveis de Ambiente

### 📝 Como configurar o arquivo `.env`

Caso não esteja presente, crie um arquivo chamado `.env` na raiz do projeto e insira as variáveis de ambiente listadas abaixo: 

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DB=ecommerce
MYSQL_USER=ecommerce_user
MYSQL_PASSWORD=ecommerce_pass
JWT_ISSUER=e-commerce-service
JWT_EXPIRES_IN_SECONDS=300
JWT_REFRESH_TOKEN_EXPIRES_IN_DAYS=1
JWT_PRIVATE_KEY=classpath:private.key
JWT_PUBLIC_KEY=classpath:public.key
```

---

## ▶️ Executando a Aplicação

> ⚠️ É **estritamente necessário** que o arquivo `.env` esteja presente na raiz do projeto e corretamente configurado com todas as variáveis de ambiente mencionadas acima. Sem ele, a aplicação **não iniciará corretamente**.

### 🐳 Usando Docker

O `docker-compose.yml` localizado na raiz do monorepo já está configurado com todas as dependências necessárias. A imagem da aplicação será construída a partir do arquivo **Dockerfile** localizado na raiz do projeto.

Para executar a Aplicação + MySQL:

```bash
docker-compose up
```

### 💻 Localmente

Para realizar o build da aplicação:

```bash
mvn clean install
```

Para iniciar apenas o container do MySQL:

```bash
docker-compose up mysql
```

Com o servidor do MySQL em execução, para subir a aplicação via terminal:

```bash
mvn spring-boot:run
```

Ou iniciar diretamente via IDE de preferência (IntelliJ, Eclipse, VS Code).


---

## 🌐 Endpoints

| Método | Rota                          | Descrição                                 | Acesso  | Papéis Requeridos |
|--------|-------------------------------|-------------------------------------------|---------|-------------------|
| POST   | `/v1/auth/signup`             | Criar um novo usuário                     | Público | -                 |
| POST   | `/v1/auth/signin`             | Autenticar usuário                        | Público | -                 |
| POST   | `/v1/auth/signout`            | Fazer logout do usuário                   | Privado | `ADMIN` / `USER`  |
| POST   | `/v1/auth/refresh`            | Atualizar token de autenticação           | Público | -                 |
| POST   | `/v1/users/{id}/promote`      | Promover usuário a administrador pelo ID  | Privado | `ADMIN`           |
| GET    | `/v1/users`                   | Listar usuários com paginação             | Privado | `ADMIN`           |
| GET    | `/v1/users/{id}`              | Buscar usuário pelo ID                    | Privado | `ADMIN`           |
| GET    | `/v1/users/me`                | Buscar usuário autenticado                | Privado | `ADMIN` / `USER`  |
| POST   | `/v1/orders`                  | Criar um novo pedido                      | Privado | `ADMIN` / `USER`  |
| POST   | `/v1/orders/{id}/pay`         | Realizar pagamento de pedido pelo ID      | Privado | `ADMIN` / `USER`  |
| GET    | `/v1/orders/my`               | Listar pedidos do usuário autenticado     | Privado | `ADMIN` / `USER`  |
| POST   | `/v1/products`                | Criar um novo produto                     | Privado | `ADMIN`           |
| PUT    | `/v1/products/{id}`           | Atualizar produto pelo ID                 | Privado | `ADMIN`           |
| DELETE | `/v1/products/{id}`           | Remover produto pelo ID                   | Privado | `ADMIN`           |
| GET    | `/v1/products`                | Listar produtos com paginação             | Privado | `ADMIN` / `USER`  |
| GET    | `/v1/products/{id}`           | Buscar produto pelo ID                    | Privado | `ADMIN` / `USER`  |
| GET    | `/v1/reports/top-buyers`      | Listar top compradores                    | Privado | `ADMIN`           |
| GET    | `/v1/reports/average-ticket`  | Média de ticket por usuário com paginação | Privado | `ADMIN`           |
| GET    | `/v1/reports/monthly-revenue` | Receita mensal                            | Privado | `ADMIN`           |

---

## 🧪 Executando Testes via Swagger

A aplicação conta com uma documentação interativa da API gerada automaticamente pelo **SpringDoc OpenAPI**, acessível via navegador.


#### 1. Acesse o Swagger UI

- Com a aplicação rodando, abra o navegador e acesse:

```bash
http://localhost:8080/swagger-ui/index.html
```

#### 2. Explorando a API

- Todos os endpoints estão agrupados por controladores (ex: AuthResource, ProductResource, etc).
- Clique sobre cada método (GET, POST, etc) para expandir e visualizar detalhes.

#### 3. Executando chamadas

- Clique no botão **"Try it out"** em qualquer endpoint.
- Preencha os parâmetros ou corpo da requisição conforme necessário.
- Clique em **"Execute"** para enviar a requisição e visualizar a resposta.

#### 4. Autenticação (quando necessária)

Alguns endpoints exigem autenticação via **JWT**. Para utilizá-los no Swagger UI:

- Realize o login usando o endpoint `/v1/auth/signin`.
- Copie o `accessToken` retornado na resposta.
- No Swagger UI, clique em **"Authorize"** no canto superior direito.
- No campo **Value**, insira o token copiado (sem o prefixo `Bearer`).
- Clique novamente em **"Authorize"** para autenticar.

🔄 Se o token expirar:

- Clique em **"Authorize"** e depois em **"Logout"** para limpar o token antigo.
- Utilize o endpoint `/v1/auth/refresh`, passando o `refreshToken` recebido na autenticação inicial.
- Copie o novo `accessToken` gerado.
- Repita novamente os passos da primeira autenticação com o novo token.

---

## 📦 Dump da Base de Dados

A base inicial do banco de dados é populada via **migrations Flyway**, garantindo um ambiente consistente para desenvolvimento e testes. 

São inseridos **6 usuários** no dump inicial, sendo **5 usuários** com papel `USER` e **1 usuário** com papel `ADMIN`. 

Além disso, para cada usuário, é criado **1 pedido** com status **PAGO** no banco para validação dos relatórios e funcionalidades da aplicação.

---

## 🔑 Credenciais de Acesso

| Tipo de Usuário  | Email              | Senha     | Papel  |
|------------------|--------------------|-----------|--------|
| Usuário Padrão   | user1@example.com  | password  | USER   |
| Usuário Padrão   | user2@example.com  | password  | USER   |
| Usuário Padrão   | user3@example.com  | password  | USER   |
| Usuário Padrão   | user4@example.com  | password  | USER   |
| Usuário Padrão   | user5@example.com  | password  | USER   |
| Administrador    | admin@example.com  | password  | ADMIN  |

---

## 🛡️ Cobertura de Testes

A aplicação conta com uma **sólida suíte de testes**, abrangendo:

- **Testes unitários** para os serviços, utilitários e regras de negócio.
- **Testes de integração** focados na **camada MVC**, utilizando o `@WebMvcTest`.
- **Testes de integração** focados na **camada de persistência**, utilizando o `@DataJpaTest`.

📊 A cobertura atual é de **100% das classes e 100% das linhas de código**.

