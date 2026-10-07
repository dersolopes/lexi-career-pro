# Fase 2 - Retenção e Engajamento

## Objetivo da Fase 2

Implementar funcionalidades focadas em retenção de conhecimento e engajamento do usuário, incluindo revisão espaçada, sistema de quiz, favoritos e personalização por interesses.

## Funcionalidades a Implementar

### 1. Sistema de Revisão Espaçada (Spaced Repetition)

**Objetivo:** Implementar algoritmo de revisão espaçada para otimizar a retenção de termos a longo prazo.

#### 1.1 Backend - Algoritmo de Revisão

- **Criar classe `SpacedRepetitionService`**
  - Implementar algoritmo SM-2 (SuperMemo 2) ou similar
  - Calcular `next_review_at` baseado em:
    - `review_count` (número de revisões)
    - `correct_answers` (acertos totais)
    - `last_review_at` (última revisão)
  - Fórmula base:
    - Intervalo inicial: 1 dia
    - Após acerto: multiplicar intervalo por fator (ex: 2.5)
    - Após erro: resetar intervalo para 1 dia
  - Atualizar `knowledge_level` baseado em desempenho

#### 1.2 Backend - Endpoints

- **TermService - Novos métodos:**
  - `getTermsForReview(Long userId)` - Retorna termos prontos para revisão
  - `markTermAsReviewed(Long userId, Long termId, boolean success)` - Registra revisão
  - `updateReviewSchedule(Long userTermId)` - Recalcula próxima revisão

- **TermController - Novos endpoints:**
  - `GET /api/terms/review` - Lista termos para revisar hoje
  - `POST /api/terms/{termId}/review` - Registra revisão de um termo
  - `GET /api/terms/review/stats` - Estatísticas de revisão

#### 1.3 Banco de Dados

- Aproveitar tabela `user_term` já existente:
  - `next_review_at` - TIMESTAMP para próxima revisão
  - `review_count` - INT contador de revisões
  - `correct_answers` - INT contador de acertos
  - `knowledge_level` - ENUM (NEVER_SEEN, HEARD_NOT_EXPLAINED, KNOWN, USE_IN_WORK)

---

### 2. Sistema de Quiz

**Objetivo:** Criar quizzes interativos para testar conhecimento dos termos.

#### 2.1 Backend - Entidades

- **Criar entidade `QuizQuestion`**
  - `id` (Long, PK)
  - `term` (Term, FK)
  - `questionText` (String)
  - `explanation` (String)
  - `difficultyLevel` (Enum: BEGINNER, INTERMEDIATE, ADVANCED)
  - `createdAt`, `updatedAt`

- **Criar entidade `QuizOption`**
  - `id` (Long, PK)
  - `question` (QuizQuestion, FK)
  - `optionText` (String)
  - `isCorrect` (Boolean)
  - `orderIndex` (Int)

- **Criar entidade `QuizAnswer`**
  - `id` (Long, PK)
  - `user` (User, FK)
  - `question` (QuizQuestion, FK)
  - `selectedOption` (QuizOption, FK)
  - `isCorrect` (Boolean)
  - `answeredAt` (Timestamp)

#### 2.2 Backend - Repositories

- **Criar `QuizQuestionRepository`**
  - `findByTermId(Long termId)`
  - `findByDifficultyLevel(DifficultyLevel level)`

- **Criar `QuizOptionRepository`**
  - `findByQuestionId(Long questionId)`
  - `findByQuestionIdAndIsCorrect(Long questionId, boolean isCorrect)`

- **Criar `QuizAnswerRepository`**
  - `findByUserId(Long userId)`
  - `findByUserIdAndQuestionId(Long userId, Long questionId)`
  - `countByUserIdAndIsCorrect(Long userId, boolean isCorrect)`

#### 2.3 Backend - Service

- **Criar `QuizService`**
  - `generateQuizForTerm(Long termId)` - Gera quiz para um termo
  - `getQuizForTerm(Long termId)` - Retorna quiz pronto para responder
  - `submitAnswer(Long userId, Long questionId, Long optionId)` - Processa resposta
  - `getQuizStats(Long userId)` - Estatísticas de quiz do usuário
  - `getWrongAnswersForReview(Long userId)` - Termos que usuário errou para revisar

#### 2.4 Backend - DTOs

- **Criar `QuizQuestionDTO`**
  - id, questionText, explanation, difficultyLevel
  - List<QuizOptionDTO> options

- **Criar `QuizOptionDTO`**
  - id, optionText, orderIndex (não enviar isCorrect)

- **Criar `QuizAnswerRequest`**
  - questionId, selectedOptionId

- **Criar `QuizAnswerResponse`**
  - isCorrect, correctOptionId, explanation

- **Criar `QuizStatsDTO`**
  - totalAnswered, correctCount, incorrectCount, averageScore

#### 2.5 Backend - Controller

- **Criar `QuizController`**
  - `GET /api/quiz/term/{termId}` - Obter quiz de um termo
  - `POST /api/quiz/answer` - Submeter resposta
  - `GET /api/quiz/stats` - Estatísticas do usuário
  - `GET /api/quiz/review-needed` - Termos para revisar baseados em erros

#### 2.6 Banco de Dados

- Tabelas já definidas em `DATABASE_ARCHITECTURE.md`:
  - `quiz_questions`
  - `quiz_options`
  - `quiz_answers`

---

### 3. Sistema de Favoritos

**Objetivo:** Permitir usuário marcar termos como favoritos para acesso rápido.

#### 3.1 Backend - Service

- **TermService - Novos métodos:**
  - `toggleFavorite(Long userId, Long termId)` - Marcar/desmarcar favorito
  - `getFavoriteTerms(Long userId)` - Listar favoritos
  - `isFavorite(Long userId, Long termId)` - Verificar se é favorito

#### 3.2 Backend - Controller

- **TermController - Novos endpoints:**
  - `POST /api/terms/{termId}/favorite` - Marcar como favorito
  - `DELETE /api/terms/{termId}/favorite` - Remover favorito
  - `GET /api/terms/favorites` - Listar favoritos do usuário

#### 3.3 Banco de Dados

- Aproveitar campo `is_favorite` na tabela `user_term`

---

### 4. Personalização por Profissão e Interesse

**Objetivo:** Personalizar conteúdo baseado em perfil e interesses do usuário.

#### 4.1 Backend - Service

- **Criar `UserInterestService`**
  - `addInterest(Long userId, Long categoryId)` - Adicionar interesse
  - `removeInterest(Long userId, Long categoryId)` - Remover interesse
  - `getUserInterests(Long userId)` - Listar interesses
  - `getRecommendedTerms(Long userId)` - Recomendar termos baseados em interesses
  - `updateUserProfession(Long userId, String profession)` - Atualizar profissão

#### 4.2 Backend - Controller

- **Criar `UserInterestController`**
  - `GET /api/user/interests` - Listar interesses
  - `POST /api/user/interests` - Adicionar interesse
  - `DELETE /api/user/interests/{categoryId}` - Remover interesse
  - `GET /api/user/recommendations` - Termos recomendados

- **UserController - Novos endpoints:**
  - `PUT /api/user/profession` - Atualizar profissão
  - `PUT /api/user/level` - Atualizar nível

#### 4.3 Backend - DTOs

- **Criar `UserInterestRequest`**
  - categoryId

- **Criar `UserInterestResponse`**
  - categoryId, categoryName

- **Criar `UserProfileUpdateRequest`**
  - profession, level

#### 4.4 Banco de Dados

- Aproveitar tabela `user_interests` já existente
- Aproveitar campos `profession` e `level` na tabela `users`

---

### 5. Integração com Dashboard

**Objetivo:** Atualizar dashboard para mostrar métricas da fase 2.

#### 5.1 DashboardService - Novas Métricas

- Adicionar ao `DashboardResponse`:
  - `termsForReviewCount` - Quantidade de termos para revisar hoje
  - `favoriteCount` - Quantidade de favoritos
  - `quizAccuracy` - Precisão nos quizzes (%)
  - `streakDays` - Dias consecutivos de estudo
  - `knowledgeLevelDistribution` - Distribuição por nível de conhecimento

#### 5.2 DashboardController

- Atualizar endpoint `GET /api/dashboard` para incluir novas métricas

---

## Estrutura de Arquivos a Criar

### Backend

```
backend/src/main/java/com/lexicareer/
├── entity/
│   ├── QuizQuestion.java (NOVO)
│   ├── QuizOption.java (NOVO)
│   └── QuizAnswer.java (NOVO)
├── repository/
│   ├── QuizQuestionRepository.java (NOVO)
│   ├── QuizOptionRepository.java (NOVO)
│   └── QuizAnswerRepository.java (NOVO)
├── service/
│   ├── SpacedRepetitionService.java (NOVO)
│   ├── QuizService.java (NOVO)
│   └── UserInterestService.java (NOVO)
├── controller/
│   ├── QuizController.java (NOVO)
│   └── UserInterestController.java (NOVO)
└── dto/
    ├── quiz/
    │   ├── QuizQuestionDTO.java (NOVO)
    │   ├── QuizOptionDTO.java (NOVO)
    │   ├── QuizAnswerRequest.java (NOVO)
    │   ├── QuizAnswerResponse.java (NOVO)
    │   └── QuizStatsDTO.java (NOVO)
    └── user/
        ├── UserInterestRequest.java (NOVO)
        ├── UserInterestResponse.java (NOVO)
        └── UserProfileUpdateRequest.java (NOVO)
```

### Frontend (a ser definido posteriormente)

```
frontend/src/
├── pages/
│   ├── ReviewPage.tsx (NOVO)
│   ├── QuizPage.tsx (NOVO)
│   ├── FavoritesPage.tsx (NOVO)
│   └── InterestsPage.tsx (NOVO)
├── components/
│   ├── QuizCard.tsx (NOVO)
│   ├── ReviewCard.tsx (NOVO)
│   └── InterestSelector.tsx (NOVO)
└── services/
    ├── quizService.ts (NOVO)
    └── userInterestService.ts (NOVO)
```

---

## Ordem de Implementação Sugerida

1. **Sistema de Favoritos** (mais simples)
   - Atualizar TermService
   - Adicionar endpoints no TermController
   - Testar integração

2. **Personalização por Interesse**
   - Criar UserInterestService
   - Criar UserInterestController
   - Criar DTOs
   - Testar recomendações

3. **Sistema de Quiz**
   - Criar entidades (QuizQuestion, QuizOption, QuizAnswer)
   - Criar repositories
   - Criar QuizService
   - Criar QuizController
   - Criar DTOs
   - Implementar lógica de geração de quiz
   - Testar fluxo completo

4. **Revisão Espaçada**
   - Criar SpacedRepetitionService
   - Implementar algoritmo SM-2
   - Atualizar TermService com métodos de revisão
   - Adicionar endpoints no TermController
   - Testar agendamento de revisões

5. **Atualização do Dashboard**
   - Adicionar novas métricas no DashboardService
   - Atualizar DashboardResponse
   - Testar visualização

---

## Critérios de Aceite

### Revisão Espaçada
- [ ] Sistema calcula próxima data de revisão automaticamente
- [ ] Usuário pode ver termos para revisar hoje
- [ ] Revisão atualiza contador e knowledge_level
- [ ] Intervalo aumenta após acertos
- [ ] Intervalo reseta após erros

### Quiz
- [ ] Quiz gerado para cada termo
- [ ] Usuário pode responder quiz
- [ ] Sistema valida resposta e mostra explicação
- [ ] Estatísticas de quiz disponíveis
- [ ] Erros em quiz geram necessidade de revisão

### Favoritos
- [ ] Usuário pode marcar termo como favorito
- [ ] Usuário pode listar favoritos
- [ ] Usuário pode remover favorito
- [ ] Favoritos persistem corretamente

### Personalização
- [ ] Usuário pode adicionar interesses (categorias)
- [ ] Usuário pode remover interesses
- [ ] Sistema recomenda termos baseados em interesses
- [ ] Usuário pode atualizar profissão e nível
- [ ] Termo do dia considera interesses do usuário

### Dashboard
- [ ] Dashboard mostra termos para revisar
- [ ] Dashboard mostra contagem de favoritos
- [ ] Dashboard mostra precisão em quizzes
- [ ] Dashboard mostra streak de dias

---

## Testes Necessários

### Unit Tests
- SpacedRepetitionService: cálculo de intervalos
- QuizService: geração de perguntas, validação de respostas
- UserInterestService: CRUD de interesses, recomendações

### Integration Tests
- Fluxo completo de revisão espaçada
- Fluxo completo de quiz
- Integração quiz → revisão espaçada
- Personalização → termo do dia

### Manual Tests
- Usuário marca favorito e vê na lista
- Usuário adiciona interesse e recebe recomendações
- Usuário responde quiz e vê estatísticas
- Usuário revisa termo e vê próxima data

---

## Próximos Passos Após Fase 2

1. Implementar frontend para todas as funcionalidades
2. Adicionar notificações (email/push) para lembretes de revisão
3. Implementar gamificação (pontos, badges, streaks)
4. Preparar estrutura para Fase 3 (Monetização)

---

## Notas Importantes

- O banco de dados já está preparado para todas as funcionalidades da Fase 2
- As tabelas `user_term`, `quiz_questions`, `quiz_options`, `quiz_answers`, `user_interests` já estão definidas
- Focar em regras de negócio no backend primeiro
- Frontend será implementado após conclusão do backend
- Manter consistência com arquitetura existente (DTOs, Services, Controllers)
- Seguir padrões de código já estabelecidos no projeto
