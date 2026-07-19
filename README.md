
# Gerenciador de documentos

Sistema de gerenciamento para solicitação, tramitação e emissão de documentos acadêmicos. O projeto gerencia o fluxo de aprovação entre alunos, secretaria e coordenação.

## Rodar localmente

Clone o projeto

```bash
  git clone https://github.com/IsabelaMunzlinger/desafio-2026-2-java.git
```

Abra a pasta local do projeto
```bash
  cd pasta-do-projeto
```

Rode o comando no terminal para fechar algum container aberto no Docker
```bash
  docker compose down -v
```

Rode o comando no terminal para subir o container do banco de dados e da aplicação no Docker

```bash
  docker compose up --build
```
## Roadmap

Entregas do projeto:

-  Modelagem do banco de dados e controle de transições de status.
-  Implementação de segurança com Spring Security e JWT.
-  Containerização da API e Banco de Dados com Docker.
-  Geração de relatórios PDF com JasperReports.

## Funcionalidades

- **Autenticação e Autorização:** Sistema de login com tokens JWT e controle de acesso baseado em perfis (Aluno, Secretaria, Coordenador, Admin).
- **Máquina de Estados de Documentos:** Fluxo de aprovação dinâmico que de acordo com o fluxo de cada setor.
- **Geração de PDF:** Emissão automática de documentos utilizando o JasperReports.
- **Casos teste:** Banco de dados populado automaticamente na primeira execução através de um `DataSeeder`, facilitando os testes.
- **Conteinerizado:** Infraestrutura completa rodando via Docker e Docker Compose, sem necessidade de instalar dependências locais.
## Guia de Testes

Para facilitar a avaliação, o sistema conta com um `DataSeeder` que já cadastra perfis e insere o fluxo base do sistema no banco de dados automaticamente na primeira execução.

Para que seja possível aprovar o pedido pela Secretaria e pela Coordenação, é preciso cadastrar o status de Aprovado pela secretaria e Aprovado pela coordenação, atribuindo a permissão à Secretaria e à Coordenação. Para isso, é preciso acessar o sistema como Administrador para cadastrar.




**Usuários de Teste Disponíveis:**
*(A senha para todos os usuários abaixo é `1234`)*

| Perfil | E-mail de Login | O que testar com este usuário |
| :--- | :--- | :--- |
| **ALUNO** | `aluno@gmail.com` | Criação de novas solicitações e download do PDF quando emitido. |
| **SECRETARIA** | `secretaria@gmail.com` | Visualização da fila de trabalho e avanço da solicitação (Etapa 1). |
| **COORDENADOR** | `coordenador@gmail.com` | Aprovação ou reprovação final do documento (Etapa 2). |
| **ADMIN** | `admin@gmail.com` | Acesso total para manutenção de status e usuários. |
## Telas do sistema

O projeto possui um front-end integrado servido diretamente pelo Spring Boot. Após rodar o projeto, você pode acessar as telas pelo navegador em `http://localhost:8081`:

* **Acesso e Painel:**
    * `/login`: Tela de autenticação inicial.
    * `/menu`: Painel principal após o login.
    * `/estatisticas`: Dashboard com métricas do sistema.
* **Módulo do Aluno:**
    * `/solicitar-documento`: Formulário para novos pedidos.
    * `/meus-pedidos` / `/historico-pedido`: Acompanhamento e histórico de solicitações do aluno.
* **Módulo de Gestão (Secretaria/Coordenação):**
    * `/fila-trabalho`: Fila de documentos aguardando análise e avanço de etapa.
* **Cadastros Base (Admin):**
    * `/cadastros`: Menu de gerenciamento.
    * `/cadastro-usuarios`, `/cadastro-cursos`, `/cadastro-documentos`, `/cadastro-fluxos`, `/cadastro-status`.
## Tech Stack

* **Linguagem:** Java 21
* **Framework Backend:** Spring Boot 3, Spring Security (JWT), Spring Data JPA
* **Interface (Telas):** HTML, Thymeleaf, CSS, JavaScript
* **Banco de Dados:** MySQL 8.0
* **Infraestrutura:** Docker e Docker Compose
* **Geração de Relatórios:** JasperReports

