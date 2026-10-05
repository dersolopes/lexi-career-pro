# LexiCareer Pro

LexiCareer Pro é uma plataforma de aprendizado contínuo de vocabulário profissional, com foco em tecnologia, carreira e desenvolvimento de habilidades de comunicação técnica.

## Visão geral

A ideia principal do produto é transformar o estudo em um hábito recorrente. Em vez de exigir longas sessões de estudo, a aplicação entrega pequenas doses de conhecimento ao longo do dia, com foco em: 
- escopo profissional
- retenção de conceitos
- contexto prático
- revisão constante
- crescimento contínuo de carreira

## Objetivo do MVP

Validar a hipótese de que pessoas querem aprender termos e conceitos profissionais em pequenas doses diárias.

### Funcionalidades principais do MVP
- cadastro e login
- perfil do usuário
- categorias de conteúdo
- termos com definição, exemplo e contexto
- termo do dia
- marcação como estudado
- favoritos
- histórico de estudo
- dashboard básico
- revisão simples

## Stack tecnológica

### Backend
- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT
- Validation

### Frontend
- React
- Vite
- TypeScript
- Axios
- Tailwind CSS

### Infraestrutura
- Docker
- Docker Compose
- GitHub
- AWS / Render / Railway (futuro)

## Estrutura do projeto

```text
lexi-career-pro/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── database/
│   └── schema.sql
├── docs/
│   ├── DATABASE_ARCHITECTURE.md
│   └── API.md
├── .gitignore
├── docker-compose.yml
├── README.md
└── LICENSE
```

## Arquitetura proposta

```text
Frontend (React) -> API (Spring Boot) -> PostgreSQL
                                      |
                                      +-> JWT / Security
                                      +-> regras de negócio
```

## Modelo de dados principal

Entidades-chave:
- User
- Category
- Term
- UserTerm
- UserInterest
- NotificationPreference
- DailyTermLog
- QuizQuestion
- QuizOption
- QuizAnswer

## Roadmap inicial

### Fase 1 - MVP
- autenticação
- CRUD de categorias e termos
- termo do dia
- dashboard básico
- histórico de termos estudados
- busca por termo

### Fase 2 - Retenção
- revisão espaçada
- quiz por termo
- favoritos
- personalização por profissão e interesse

### Fase 3 - Monetização
- plano free e premium
- conteúdo premium
- trilhas profissionais

### Fase 4 - Expansão
- IA para explicações e exemplos
- notificações por e-mail/push
- gamificação
- comunidade

## Requisitos de ambiente

### Backend
- Java 21+
- Maven
- PostgreSQL 15+

### Frontend
- Node.js 18+
- npm ou pnpm

## Como rodar localmente

### 1) Banco PostgreSQL
Crie o banco `lexicareer_db` e execute o schema localizado em `database/schema.sql`.

### 2) Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3) Frontend
```bash
cd frontend
npm install
npm run dev
```

## Status do projeto

Em desenvolvimento.

## Objetivo de negócio

A plataforma foi pensada para transformar o estudo em um hábito recorrente, ajudando o usuário a:
- aprender novas palavras todos os dias
- compreender conceitos do seu contexto profissional
- revisar o que já foi visto
- evoluir em vocabulário técnico e profissional

## Próximo passo oficial

A sequência correta agora é:
1. criar a estrutura do backend Spring Boot
2. definir entidades, repositories e serviços
3. implementar autenticação JWT
4. criar os endpoints do MVP
5. conectar com PostgreSQL
6. depois criar o frontend React

## Licença

A licença do projeto será definida em uma etapa posterior.
