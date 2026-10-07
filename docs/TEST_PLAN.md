# 📋 Plano de Testes - LexiCareer Pro

## 1. Visão Geral

Este documento define a estratégia de testes para o LexiCareer Pro, cobrindo testes unitários, integração, API, E2E e testes manuais para garantir qualidade do software.

**Stack Tecnológica:**
- Backend: Java 21, Spring Boot 3.3.4, JPA, Security, JWT
- Frontend: React 18, TypeScript, Vite, Tailwind CSS
- Banco: PostgreSQL 15+

---

## 2. Estratégia de Testes

### 2.1 Pirâmide de Testes

```
        /\
       /E2E\        10% - Testes end-to-end críticos
      /------\
     / Integração\   30% - Testes de integração e API
    /------------\
   /   Unitários   \   60% - Testes unitários
  /________________\
```

### 2.2 Ferramentas de Teste

**Backend:**
- JUnit 5 (framework de testes)
- Mockito (mocking)
- Spring Boot Test (integração)
- TestContainers (PostgreSQL em testes)
- AssertJ (asserções melhoradas)

**Frontend:**
- Vitest (framework de testes unitários)
- React Testing Library (testes de componentes)
- Playwright (testes E2E)
- MSW (Mock Service Worker - API mocking)

**Qualidade:**
- ESLint (linting frontend)
- Checkstyle (linting backend)
- SonarQube (análise estática - futuro)

---

## 3. Testes Unitários - Backend

### 3.1 Estrutura de Diretórios

```
backend/src/test/java/com/lexicareer/
├── controller/
│   ├── AuthControllerTest.java
│   ├── UserControllerTest.java
│   ├── CategoryControllerTest.java
│   ├── TermControllerTest.java
│   └── DashboardControllerTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── UserServiceTest.java
│   ├── CategoryServiceTest.java
│   ├── TermServiceTest.java
│   ├── DashboardServiceTest.java
│   ├── SpacedRepetitionServiceTest.java
│   ├── QuizServiceTest.java
│   └── UserInterestServiceTest.java
├── repository/
│   ├── UserRepositoryTest.java
│   ├── CategoryRepositoryTest.java
│   ├── TermRepositoryTest.java
│   └── UserTermRepositoryTest.java
└── util/
    ├── JwtUtilTest.java
    └── PasswordUtilTest.java
```

### 3.2 Cobertura Esperada por Camada

| Camada | Cobertura Mínima |
|--------|------------------|
| Controllers | 80% |
| Services | 90% |
| Repositories | 70% |
| Utils | 95% |

### 3.3 Casos de Teste - Controllers

#### AuthControllerTest
- **POST /api/auth/register**
  - [ ] Sucesso com dados válidos
  - [ ] Erro com email duplicado
  - [ ] Erro com senha fraca
  - [ ] Erro com campos obrigatórios faltando
  - [ ] Erro com email inválido

- **POST /api/auth/login**
  - [ ] Sucesso com credenciais válidas
  - [ ] Erro com email não cadastrado
  - [ ] Erro com senha incorreta
  - [ ] Erro com campos vazios
  - [ ] Retorna token JWT válido

#### UserControllerTest
- **GET /api/user/profile**
  - [ ] Sucesso com token válido
  - [ ] Erro sem token
  - [ ] Erro com token inválido
  - [ ] Erro com token expirado

- **PUT /api/user/profile**
  - [ ] Sucesso atualizando nome
  - [ ] Sucesso atualizando profissão
  - [ ] Sucesso atualizando nível
  - [ ] Erro com dados inválidos
  - [ ] Erro sem autenticação

#### CategoryControllerTest
- **GET /api/categories**
  - [ ] Lista todas categorias
  - [ ] Filtra por plano (FREE/PREMIUM)
  - [ ] Retorna ordenado por display_order
  - [ ] Inclui categorias hierárquicas

- **POST /api/categories** (admin)
  - [ ] Sucesso criando categoria
  - [ ] Erro com nome duplicado
  - [ ] Erro sem permissão admin
  - [ ] Erro com slug inválido

#### TermControllerTest
- **GET /api/terms**
  - [ ] Lista todos termos
  - [ ] Filtra por categoria
  - [ ] Filtra por plano
  - [ ] Filtra por dificuldade
  - [ ] Busca por nome (search)
  - [ ] Paginação funciona

- **GET /api/terms/{id}**
  - [ ] Sucesso com ID válido
  - [ ] Erro com ID inexistente
  - [ ] Retorna termos relacionados

- **GET /api/terms/daily**
  - [ ] Retorna termo do dia
  - [ ] Considera interesses do usuário
  - [ ] Não repete termo do mesmo dia
  - [ ] Respeita plano do usuário

- **POST /api/terms/{id}/study**
  - [ ] Marca termo como estudado
  - [ ] Atualiza status para STUDIED
  - [ ] Registra timestamp
  - [ ] Erro sem autenticação

- **POST /api/terms/{id}/favorite**
  - [ ] Marca como favorito
  - [ ] Remove favorito se já marcado
  - [ ] Erro com termo inexistente

- **GET /api/terms/favorites**
  - [ ] Lista favoritos do usuário
  - [ ] Retorna vazio se não houver
  - [ ] Ordena por data de favorito

#### DashboardControllerTest
- **GET /api/dashboard**
  - [ ] Retorna métricas básicas
  - [ ] Retorna termos aprendidos
  - [ ] Retorna favoritos
  - [ ] Retorna streak de dias
  - [ ] Retorna termos para revisar

### 3.4 Casos de Teste - Services

#### AuthServiceTest
- **registerUser()**
  - [ ] Criptografa senha corretamente
  - [ ] Valida email format
  - [ ] Valida força da senha
  - [ ] Gera token JWT
  - [ ] Lança exceção para email duplicado

- **loginUser()**
  - [ ] Verifica senha com BCrypt
  - [ ] Gera token com claims corretos
  - [ ] Lança exceção para credenciais inválidas
  - [ ] Valida usuário ativo

- **validateToken()**
  - [ ] Valida token assinado corretamente
  - [ ] Rejeita token expirado
  - [ ] Rejeita token malformado
  - [ ] Extrai userId do token

#### TermServiceTest
- **getDailyTerm()**
  - [ ] Seleciona termo não estudado hoje
  - [ ] Prioriza categorias de interesse
  - [ ] Respeita plano do usuário
  - [ ] Retorna null se não houver termos disponíveis

- **markAsStudied()**
  - [ ] Atualiza status para STUDIED
  - [ ] Cria registro em user_term
  - [ ] Registra timestamp
  - [ ] Não duplica registro

- **toggleFavorite()**
  - [ ] Adiciona favorito se não existe
  - [ ] Remove favorito se existe
  - [ ] Retorna estado atualizado

#### SpacedRepetitionServiceTest
- **calculateNextReview()**
  - [ ] Primeira revisão: 1 dia
  - [ ] Segunda revisão: 3 dias
  - [ ] Terceira revisão: 7 dias
  - [ ] Após erro: reseta para 1 dia
  - [ ] Intervalo cresce exponencialmente

- **updateKnowledgeLevel()**
  - [ ] NEVER_SEEN → HEARD_NOT_EXPLAINED (1 acerto)
  - [ ] HEARD_NOT_EXPLAINED → KNOWN (3 acertos)
  - [ ] KNOWN → USE_IN_WORK (5 acertos)
  - [ ] Reseta após erro consecutivo

#### QuizServiceTest
- **generateQuizForTerm()**
  - [ ] Cria pergunta com 4 opções
  - [ ] 1 opção correta, 3 incorretas
  - [ ] Embaralha ordem das opções
  - [ ] Lança erro se termo não tem quiz

- **submitAnswer()**
  - [ ] Valida resposta correta
  - [ ] Registra resposta em quiz_answers
  - [ ] Atualiza estatísticas do usuário
  - [ ] Dispara revisão se errado
  - [ ] Retorna explicação

- **getQuizStats()**
  - [ ] Calcula total respondido
  - [ ] Calcula acertos
  - [ ] Calcula erros
  - [ ] Calcula precisão (%)

#### UserInterestServiceTest
- **addInterest()**
  - [ ] Adiciona categoria de interesse
  - [ ] Não duplica interesse
  - [ ] Valida categoria existe
  - [ ] Valida usuário existe

- **removeInterest()**
  - [ ] Remove interesse existente
  - [ ] Não erro se não existe
  - [ ] Atualiza recomendações

- **getRecommendedTerms()**
  - [ ] Retorna termos de categorias de interesse
  - [ ] Exclui termos já estudados
  - [ ] Ordena por relevância
  - [ ] Respeita plano do usuário

### 3.5 Casos de Teste - Repositories

#### UserRepositoryTest
- **findByEmail()**
  - [ ] Encontra usuário por email
  - [ ] Retorna null se não existe
  - [ ] Case insensitive

- **existsByEmail()**
  - [ ] Retorna true se email existe
  - [ ] Retorna false se não existe

#### TermRepositoryTest
- **findByCategoryId()**
  - [ ] Lista termos por categoria
  - [ ] Retorna vazio se não houver
  - [ ] Ordena por nome

- **findByPlanType()**
  - [ ] Filtra por plano FREE
  - [ ] Filtra por plano PREMIUM
  - [ ] Respeita is_active

- **searchByName()**
  - [ ] Busca parcial por nome
  - [ ] Case insensitive
  - [ ] Retorna resultados ordenados

---

## 4. Testes de Integração - Backend

### 4.1 Estrutura

```
backend/src/test/java/com/lexicareer/integration/
├── AuthIntegrationTest.java
├── UserFlowIntegrationTest.java
├── TermFlowIntegrationTest.java
├── QuizFlowIntegrationTest.java
└── SpacedRepetitionIntegrationTest.java
```

### 4.2 Configuração

- Usar **@SpringBootTest** com contexto completo
- Usar **TestContainers** para PostgreSQL real
- Usar **@Transactional** para rollback após cada teste
- Mockar serviços externos (email, etc.)

### 4.3 Casos de Teste

#### AuthIntegrationTest
- [ ] Fluxo completo: registro → login → acesso protegido
- [ ] Token expira e é renovado
- [ ] Logout invalida token

#### UserFlowIntegrationTest
- [ ] Usuário se registra → completa perfil → adiciona interesses
- [ ] Usuário atualiza perfil → persiste corretamente
- [ ] Usuário deleta conta → dados removidos (cascade)

#### TermFlowIntegrationTest
- [ ] Usuário recebe termo do dia → estuda → marca como estudado
- [ ] Usuário busca termo → favorita → lista favoritos
- [ ] Usuário filtra por categoria → resultados corretos

#### QuizFlowIntegrationTest
- [ ] Usuário recebe quiz → responde → vê resultado
- [ ] Usuário erra quiz → termo entra para revisão
- [ ] Usuário acerta quiz → knowledge_level aumenta

#### SpacedRepetitionIntegrationTest
- [ ] Usuário estuda termo → agenda revisão
- [ ] Revisão agendada aparece na lista
- [ ] Usuário revisa e acerta → nova data agendada
- [ ] Usuário revisa e erra → intervalo reseta

---

## 5. Testes de API - Backend

### 5.1 Estratégia

- Testar contratos de API
- Validar status codes
- Validar response schemas
- Testar rate limiting (quando implementado)

### 5.2 Ferramentas

- **Spring MockMvc** para testes de controllers
- **Postman/Newman** para testes manuais automatizados
- **Swagger/OpenAPI** para documentação e validação

### 5.3 Casos de Teste por Endpoint

#### Autenticação
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/auth/register | POST | 201 created, 400 bad request, 409 conflict |
| /api/auth/login | POST | 200 ok, 401 unauthorized, 400 bad request |

#### Usuário
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/user/profile | GET | 200 ok, 401 unauthorized, 403 forbidden |
| /api/user/profile | PUT | 200 ok, 400 bad request, 401 unauthorized |

#### Categorias
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/categories | GET | 200 ok, retorna array |
| /api/categories/{id} | GET | 200 ok, 404 not found |

#### Termos
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/terms | GET | 200 ok, suporta paginação |
| /api/terms/{id} | GET | 200 ok, 404 not found |
| /api/terms/daily | GET | 200 ok, 401 unauthorized |
| /api/terms/{id}/study | POST | 200 ok, 401 unauthorized, 404 not found |
| /api/terms/{id}/favorite | POST | 200 ok, 401 unauthorized |
| /api/terms/favorites | GET | 200 ok, 401 unauthorized |

#### Quiz
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/quiz/term/{id} | GET | 200 ok, 404 not found |
| /api/quiz/answer | POST | 200 ok, 400 bad request |
| /api/quiz/stats | GET | 200 ok, 401 unauthorized |

#### Dashboard
| Endpoint | Método | Casos |
|----------|--------|-------|
| /api/dashboard | GET | 200 ok, 401 unauthorized |

---

## 6. Testes Unitários - Frontend

### 6.1 Estrutura de Diretórios

```
frontend/src/
├── components/
│   ├── __tests__/
│   │   ├── Button.test.tsx
│   │   ├── Card.test.tsx
│   │   ├── TermCard.test.tsx
│   │   ├── QuizCard.test.tsx
│   │   └── ReviewCard.test.tsx
├── pages/
│   ├── __tests__/
│   │   ├── LoginPage.test.tsx
│   │   ├── RegisterPage.test.tsx
│   │   ├── DashboardPage.test.tsx
│   │   ├── TermDetailPage.test.tsx
│   │   └── QuizPage.test.tsx
├── services/
│   ├── __tests__/
│   │   ├── authService.test.ts
│   │   ├── termService.test.ts
│   │   └── quizService.test.ts
└── store/
    └── __tests__/
        └── authStore.test.ts
```

### 6.2 Casos de Teste - Componentes

#### TermCard.test.tsx
- [ ] Renderiza nome do termo
- [ ] Renderiza definição
- [ ] Renderiza categoria
- [ ] Clique navega para detalhes
- [ ] Mostra badge de dificuldade
- [ ] Mostra ícone de favorito se favoritado

#### QuizCard.test.tsx
- [ ] Renderiza pergunta
- [ ] Renderiza 4 opções
- [ ] Seleção de opção funciona
- [ ] Botão submit habilita após seleção
- [ ] Mostra feedback após resposta
- [ ] Mostra explicação

#### ReviewCard.test.tsx
- [ ] Renderiza termo para revisar
- [ ] Mostra última data de revisão
- [ ] Botão "Eu sei" funciona
- [ ] Botão "Não sei" funciona
- [ ] Atualiza knowledge_level

### 6.3 Casos de Teste - Páginas

#### LoginPage.test.tsx
- [ ] Renderiza form de login
- [ ] Valida email obrigatório
- [ ] Valida senha obrigatória
- [ ] Submit com dados válidos chama API
- [ ] Redireciona após sucesso
- [ ] Mostra erro em falha de autenticação

#### RegisterPage.test.tsx
- [ ] Renderiza form de registro
- [ ] Valida todos campos obrigatórios
- [ ] Valida confirmação de senha
- [ ] Submit com dados válidos chama API
- [ ] Redireciona para login após sucesso
- [ ] Mostra erro em email duplicado

#### DashboardPage.test.tsx
- [ ] Renderiza métricas do usuário
- [ ] Mostra termo do dia
- [ ] Mostra termos para revisar
- [ ] Mostra streak de dias
- [ ] Navega para seções

### 6.4 Casos de Teste - Services

#### authService.test.ts
- [ ] login() retorna token
- [ ] login() lança erro em falha
- [ ] register() cria usuário
- [ ] register() lança erro em duplicidade
- [ ] logout() limpa token
- [ ] getToken() retorna token armazenado

#### termService.test.ts
- [ ] getTerms() retorna lista
- [ ] getTermById() retorna termo
- [ ] getDailyTerm() retorna termo do dia
- [ ] markAsStudied() chama API
- [ ] toggleFavorite() chama API
- [ ] getFavorites() retorna favoritos

---

## 7. Testes E2E - Frontend

### 7.1 Ferramentas

- **Playwright** para testes E2E
- Configurar em `frontend/e2e/`

### 7.2 Estrutura

```
frontend/e2e/
├── auth.spec.ts
├── dashboard.spec.ts
├── terms.spec.ts
├── quiz.spec.ts
└── review.spec.ts
```

### 7.3 Casos de Teste

#### auth.spec.ts
- [ ] Usuário navega para /register
- [ ] Preenche formulário válido
- [ ] Submete e é redirecionado para /login
- [ ] Faz login com credenciais
- [ ] É redirecionado para /dashboard
- [ ] Token é armazenado no localStorage

#### dashboard.spec.ts
- [ ] Usuário logado acessa /dashboard
- [ ] Vê termo do dia
- [ ] Vê métricas (termos aprendidos, streak)
- [ ] Vê termos para revisar (se houver)
- [ ] Clica em termo do dia
- [ ] Navega para página de detalhes

#### terms.spec.ts
- [ ] Usuário acessa /terms
- [ ] Vê lista de termos
- [ ] Filtra por categoria
- [ ] Busca por nome
- [ ] Clica em termo
- [ ] Vê detalhes completos
- [ ] Marca como favorito
- [ ] Favorito persiste

#### quiz.spec.ts
- [ ] Usuário acessa quiz de um termo
- [ ] Vê pergunta e opções
- [ ] Seleciona opção
- [ ] Submete resposta
- [ ] Vê se acertou ou errou
- [ ] Vê explicação
- [ ] Estatísticas atualizam

#### review.spec.ts
- [ ] Usuário acessa /review
- [ ] Vê termos para revisar
- [ ] Clica em "Eu sei"
- [ ] Vê próxima data de revisão
- [ ] Clica em "Não sei"
- [ ] Vê que intervalo reseta

---

## 8. Testes Manuais

### 8.1 Checklist de Testes Manuais

#### Autenticação
- [ ] Registro com email válido funciona
- [ ] Registro com email duplicado mostra erro
- [ ] Login com credenciais corretas funciona
- [ ] Login com senha errada mostra erro
- [ ] Logout funciona e limpa sessão
- [ ] Token expirado redireciona para login

#### Perfil do Usuário
- [ ] Usuário pode editar nome
- [ ] Usuário pode editar profissão
- [ ] Usuário pode editar nível
- [ ] Alterações persistem após refresh
- [ ] Validações funcionam

#### Categorias e Termos
- [ ] Lista de categorias carrega
- [ ] Categorias hierárquicas mostram relação
- [ ] Lista de termos carrega
- [ ] Filtro por categoria funciona
- [ ] Busca por termo funciona
- [ ] Paginação funciona
- [ ] Termo do dia aparece
- [ ] Termo do dia muda diariamente

#### Estudo de Termos
- [ ] Usuário pode marcar termo como estudado
- [ ] Status atualiza corretamente
- [ ] Histórico de estudo registra
- [ ] Usuário pode favoritar termo
- [ ] Lista de favoritos mostra
- [ ] Usuário pode remover favorito

#### Quiz
- [ ] Quiz carrega para termo
- [ ] Pergunta é clara
- [ ] Opções são distintas
- [ ] Resposta correta é validada
- [ ] Explicação aparece após resposta
- [ ] Estatísticas atualizam em tempo real

#### Revisão Espaçada
- [ ] Lista de revisão mostra termos prontos
- [ ] Data de próxima revisão é calculada
- [ ] Revisão bem-sucedida aumenta intervalo
- [ ] Revisão falha reseta intervalo
- [ ] Knowledge level atualiza

#### Dashboard
- [ ] Métricas carregam corretamente
- [ ] Streak de dias é preciso
- [ ] Termos aprendidos contam corretamente
- [ ] Precisão de quiz é calculada
- [ ] Gráficos (se houver) renderizam

#### Responsividade
- [ ] Layout funciona em desktop (1920x1080)
- [ ] Layout funciona em tablet (768x1024)
- [ ] Layout funciona em mobile (375x667)
- [ ] Menu mobile funciona
- [ ] Touch gestures funcionam

#### Performance
- [ ] Carregamento inicial < 3s
- [ ] Navegação entre páginas < 1s
- [ ] Busca de termos < 500ms
- [ ] Quiz responde instantaneamente
- [ ] Sem memory leaks em navegação

### 8.2 Testes de Usabilidade

- [ ] Fluxo de registro é intuitivo
- [ ] Fluxo de login é claro
- [ ] Navegação é óbvia
- [ ] Mensagens de erro são úteis
- [ ] Feedback visual é claro
- [ ] Acessibilidade (teclado, screen reader)

---

## 9. Testes de Performance

### 9.1 Backend

#### Carga
- [ ] 100 requisições simultâneas /api/terms
- [ ] 50 requisições simultâneas /api/auth/login
- [ ] 30 requisições simultâneas /api/quiz/answer
- [ ] Tempo de resposta < 200ms (p95)

#### Stress
- [ ] 1000 requisições simultâneas
- [ ] Teste de endurance (1 hora)
- [ ] Verificar memory leaks
- [ ] Verificar connection pool

### 9.2 Frontend

#### Carregamento
- [ ] First Contentful Paint < 1.5s
- [ ] Time to Interactive < 3s
- [ ] Largest Contentful Paint < 2.5s
- [ ] Cumulative Layout Shift < 0.1

#### Runtime
- [ ] 60fps em animações
- [ ] Sem jank em scroll
- [ ] Input latency < 50ms

---

## 10. Testes de Segurança

### 10.1 Backend

- [ ] SQL injection prevenido
- [ ] XSS prevenido
- [ ] CSRF token implementado
- [ ] Rate limiting em endpoints sensíveis
- [ ] Senhas criptografadas (BCrypt)
- [ ] JWT assinado corretamente
- [ ] JWT expira corretamente
- [ ] Headers de segurança configurados
- [ ] CORS configurado corretamente
- [ ] Validação de input em todos endpoints

### 10.2 Frontend

- [ ] Sanitização de HTML
- [ ] Token armazenado seguro (httpOnly cookie ou localStorage seguro)
- [ ] Não expõe dados sensíveis em console
- [ ] HTTPS obrigatório em produção
- [ ] Validação client-side + server-side

---

## 11. Testes de Banco de Dados

### 11.1 Integridade

- [ ] Constraints funcionam (NOT NULL, UNIQUE, FK)
- [ ] Cascade delete funciona
- [ ] Triggers (se houver) funcionam
- [ ] Índices estão sendo usados
- [ ] Queries otimizadas (EXPLAIN ANALYZE)

### 11.2 Migrações

- [ ] Schema.sql é idempotente
- [ ] Rollback funciona
- [ ] Dados de seed carregam corretamente
- [ ] Backup/restore funciona

---

## 12. Cobertura de Código

### 12.1 Metas

| Tipo | Cobertura Mínima |
|------|------------------|
| Backend (lines) | 80% |
| Backend (branches) | 70% |
| Frontend (lines) | 75% |
| Frontend (branches) | 65% |

### 12.2 Ferramentas

- **JaCoCo** para cobertura backend
- **Vitest coverage** para frontend
- **SonarQube** para análise contínua (futuro)

---

## 13. CI/CD - Integração Contínua

### 13.1 Pipeline de Testes

```yaml
# .github/workflows/test.yml
on: [push, pull_request]

jobs:
  backend-test:
    - Checkout code
    - Setup Java 21
    - Run unit tests (mvn test)
    - Run integration tests (mvn verify)
    - Generate coverage report
    - Upload coverage to Codecov

  frontend-test:
    - Checkout code
    - Setup Node.js 18
    - Install dependencies
    - Run unit tests (npm test)
    - Run E2E tests (npm run test:e2e)
    - Generate coverage report
    - Upload coverage to Codecov

  security-scan:
    - Run dependency check
    - Run SAST scan
    - Run container scan
```

### 13.2 Gatilhos

- Testes unitários: em todo push
- Testes integração: em todo push para main
- Testes E2E: em todo pull request
- Testes performance: semanalmente

---

## 14. Testes de Aceite - Usuário

### 14.1 Cenários do MVP

#### Cenário 1: Novo Usuário
1. Usuário acessa aplicação
2. Clica em "Criar conta"
3. Preenche nome, email, senha
4. Submete formulário
5. É redirecionado para login
6. Faz login
7. Completa perfil (profissão, nível)
8. Seleciona categorias de interesse
9. Vê dashboard com termo do dia
10. Estuda termo do dia

#### Cenário 2: Estudo Diário
1. Usuário logado acessa dashboard
2. Vê termo do dia
3. Clica para ver detalhes
4. Lê definição e exemplo
5. Marca como estudado
6. Vê que termo foi marcado
7. Acessa quiz do termo
8. Responde quiz
9. Vê resultado e explicação

#### Cenário 3: Revisão
1. Usuário acessa lista de revisão
2. Vê termos prontos para revisar
3. Seleciona um termo
4. Clica em "Eu sei"
5. Vê nova data de revisão
6. Knowledge level aumenta

#### Cenário 4: Favoritos
1. Usuário busca termo
2. Marca como favorito
3. Acessa lista de favoritos
4. Vê termo na lista
5. Remove favorito
6. Termo some da lista

---

## 15. Plano de Implementação

### Fase 1: Configuração (Semana 1)
- [ ] Adicionar dependências de teste no backend
- [ ] Configurar Vitest no frontend
- [ ] Configurar TestContainers
- [ ] Configurar Playwright
- [ ] Criar estrutura de diretórios de teste

### Fase 2: Testes Unitários Backend (Semana 2-3)
- [ ] Escrever testes de Services
- [ ] Escrever testes de Controllers
- [ ] Escrever testes de Repositories
- [ ] Escrever testes de Utils
- [ ] Alcançar 80% de cobertura

### Fase 3: Testes Unitários Frontend (Semana 3-4)
- [ ] Escrever testes de componentes
- [ ] Escrever testes de páginas
- [ ] Escrever testes de services
- [ ] Escrever testes de store
- [ ] Alcançar 75% de cobertura

### Fase 4: Testes Integração (Semana 4-5)
- [ ] Escrever testes de fluxos backend
- [ ] Escrever testes de API
- [ ] Configurar TestContainers
- [ ] Validar integração com banco

### Fase 5: Testes E2E (Semana 5-6)
- [ ] Escrever testes E2E críticos
- [ ] Configurar Playwright
- [ ] Integrar no CI/CD
- [ ] Testar em múltiplos browsers

### Fase 6: Testes Manuais e Performance (Semana 6)
- [ ] Executar checklist manual
- [ ] Executar testes de performance
- [ ] Executar testes de segurança
- [ ] Documentar bugs encontrados

### Fase 7: CI/CD (Semana 7)
- [ ] Configurar GitHub Actions
- [ ] Integrar testes no pipeline
- [ ] Configurar reports de cobertura
- [ ] Configurar notificações

---

## 16. Responsabilidades

| Papel | Responsabilidade |
|-------|------------------|
| Desenvolvedor Backend | Escrever testes unitários e integração backend |
| Desenvolvedor Frontend | Escrever testes unitários e E2E frontend |
| QA | Executar testes manuais, validar cenários de aceite |
| DevOps | Configurar CI/CD, monitorar testes em produção |

---

## 17. Métricas e KPIs

### 17.1 Qualidade de Código
- Cobertura de código > 80%
- Densidade de bugs < 1 por 1000 LOC
- Tempo de correção de bugs < 2 dias

### 17.2 Qualidade de Testes
- Testes passando > 95%
- Testes flaky < 5%
- Tempo de execução de testes < 10 minutos

### 17.3 Qualidade de Produto
- Bugs em produção < 5 por sprint
- Satisfação do usuário > 4/5
- Retenção de usuários > 60%

---

## 18. Ferramentas Recomendadas

### Backend
```xml
<!-- Adicionar ao pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

### Frontend
```json
// Adicionar ao package.json
{
  "devDependencies": {
    "vitest": "^1.0.0",
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.0.0",
    "@testing-library/user-event": "^14.0.0",
    "@playwright/test": "^1.40.0",
    "msw": "^2.0.0"
  }
}
```

---

## 19. Recursos e Documentação

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Playwright Docs](https://playwright.dev/)
- [TestContainers](https://www.testcontainers.org/)

---

## 20. Conclusão

Este plano de testes fornece uma abordagem estruturada para garantir a qualidade do LexiCareer Pro. A implementação deve seguir a ordem sugerida, começando por testes unitários e evoluindo para testes de integração e E2E. A meta é alcançar alta cobertura de código enquanto mantém testes rápidos e confiáveis.

**Próximos passos:**
1. Configurar ambiente de testes
2. Começar com testes unitários do backend (Services)
3. Evoluir para testes de integração
4. Implementar testes do frontend
5. Configurar CI/CD
6. Executar testes manuais de aceite
