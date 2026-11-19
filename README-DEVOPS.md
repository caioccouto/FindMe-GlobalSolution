# FindMe — Cloud Computing e DevOps

## 1. Sobre o projeto

O FindMe é uma API REST desenvolvida em Java com Spring Boot, voltada ao gerenciamento de casos de desaparecimento, usuários e alertas.

Nesta etapa do projeto, a aplicação foi preparada para execução em containers Docker e implantação em uma máquina virtual na Microsoft Azure, utilizando MySQL como banco de dados relacional.

O objetivo é demonstrar a integração entre aplicação, banco de dados, containers e infraestrutura em nuvem.

## 2. Tecnologias utilizadas

- Java 21
- Spring Boot
- Maven
- MySQL 8.4
- Docker
- Docker Compose
- Ubuntu Server 24.04
- Microsoft Azure
- Azure CLI
- Swagger / OpenAPI

## 3. Arquitetura da solução

A solução utiliza dois containers Docker independentes, conectados por uma rede interna criada pelo Docker Compose.

```text
             USUÁRIO
                |
                v
       Navegador / Swagger
                |
                v
       IP PÚBLICO DA VM
          Porta 8080
                |
                v
       +------------------+
       |    Azure VM      |
       |   Ubuntu 24.04   |
       |                  |
       |  +------------+  |
       |  | FindMe API |  |
       |  | Java 21    |  |
       |  | Porta 8080 |  |
       |  +------------+  |
       |        |         |
       |        v         |
       |  +------------+  |
       |  |   MySQL    |  |
       |  |    8.4     |  |
       |  |  Porta 3306|  |
       |  +------------+  |
       |        |         |
       |        v         |
       | Volume persistente|
       +------------------+
```

A API se comunica com o MySQL através da rede interna do Docker Compose, utilizando o nome do serviço `mysql` e a porta `3306`.

O banco de dados não possui porta publicada para acesso externo.

## 4. Estrutura do projeto

```text
FindMe-GlobalSolution/
├── Controllers/
├── Exceptions/
├── Models/
├── src/
│   ├── main/
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 5. Configuração do ambiente

É necessário ter instalado:

- Docker
- Docker Compose
- Git

Clone o repositório:

```bash
git clone https://github.com/caioccouto/FindMe-GlobalSolution.git
```

Acesse a pasta:

```bash
cd FindMe-GlobalSolution
```

Crie o arquivo de variáveis de ambiente:

```bash
cp .env.example .env
```

Edite o arquivo `.env` e configure as credenciais do banco de dados e o segredo JWT.

As variáveis utilizadas incluem:

```env
DB_NAME=findme
DB_USER=findme_user
DB_PASSWORD=SUA_SENHA
MYSQL_ROOT_PASSWORD=SUA_SENHA_ROOT
JWT_SECRET=SEU_SEGREDO_JWT
```

**Importante:** o arquivo `.env` contém informações sensíveis e não deve ser enviado ao GitHub.

## 6. Execução com Docker Compose

Para construir a imagem da aplicação e iniciar os containers:

```bash
sudo docker compose up -d --build
```

Para verificar os containers:

```bash
sudo docker compose ps
```

Para visualizar os logs da API:

```bash
sudo docker compose logs -f app
```

Para visualizar os logs do banco:

```bash
sudo docker compose logs -f mysql
```

Para interromper os containers:

```bash
sudo docker compose down
```

O banco utiliza um volume Docker nomeado para preservar os dados mesmo após a recriação dos containers.

## 7. Acesso à API

Após iniciar a aplicação, o Swagger pode ser acessado em:

```text
http://localhost:8080/swagger-ui.html
```

Na implantação em nuvem, substitua `localhost` pelo IP público da máquina virtual Azure:

```text
http://IP_PUBLICO_DA_VM:8080/swagger-ui.html
```

A documentação OpenAPI também pode ser acessada em:

```text
http://IP_PUBLICO_DA_VM:8080/v3/api-docs
```

## 8. Banco de dados

O banco utilizado é o MySQL 8.4.

O projeto possui três tabelas principais:

- `usuario`: armazena os usuários da plataforma.
- `caso`: armazena os casos de desaparecimento.
- `alerta`: armazena os alertas relacionados aos casos.

Para acessar o banco diretamente pelo container:

```bash
sudo docker compose exec mysql mysql -u root -p
```

Após informar a senha, execute:

```sql
USE findme;

SHOW TABLES;

SELECT * FROM usuario;
SELECT * FROM caso;
SELECT * FROM alerta;
```

## 9. Implantação na Microsoft Azure

A aplicação foi implantada em uma máquina virtual Ubuntu Server 24.04 na Microsoft Azure.

O provisionamento da infraestrutura foi realizado utilizando Azure CLI.

As etapas de implantação incluem:

1. Criação do Resource Group.
2. Provisionamento da máquina virtual Ubuntu.
3. Configuração do acesso SSH.
4. Liberação da porta 8080 para acesso à API.
5. Instalação do Docker e Docker Compose.
6. Clonagem do repositório GitHub.
7. Configuração das variáveis de ambiente.
8. Construção da imagem Docker da API.
9. Inicialização dos containers pelo Docker Compose.

A aplicação e o banco de dados são executados separadamente na máquina virtual.

## 10. Verificações realizadas

Foram realizados testes para validar a implantação e o funcionamento da solução:

- Verificação do estado dos containers com `docker compose ps`.
- Verificação dos logs da aplicação.
- Acesso à API através do Swagger pelo IP público da VM.
- Verificação do usuário de execução do container da API.
- Verificação do diretório de trabalho do container.
- Verificação das tabelas e registros diretamente no MySQL.
- Testes de criação, consulta, atualização e exclusão de casos.
- Verificação da persistência dos dados no banco.

A aplicação utiliza o usuário não privilegiado `appuser` no container Java e o diretório de trabalho `/app`.

## 11. Persistência de dados

O MySQL utiliza um volume nomeado configurado no Docker Compose.

Esse volume permite manter os dados do banco de dados mesmo quando os containers são interrompidos ou recriados.

A persistência foi verificada por meio de consultas SQL executadas diretamente no container MySQL.

## 12. Integrantes

- Caio Couto — RM563452
- Guilherme Eustáquio — RM566784
- Matheus Tavares — RM566844

**Turma:** 2TDSPS

## 13. Links da entrega

- **GitHub:** https://github.com/caioccouto/FindMe-GlobalSolution.git
- **Vídeo de demonstração:** https://youtu.be/zfAsOGaLNaA?si=SOXPTSpJkSW4AllJ

---

Projeto desenvolvido para a Global Solution 2026/2.