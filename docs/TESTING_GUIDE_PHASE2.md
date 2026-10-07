# 📖 Guia de Testes - Fase 2 (Retenção e Engajamento)

## Visão Geral

Este guia fornece instruções passo a passo para testar todas as funcionalidades da Fase 2 do LexiCareer Pro usando o Insomnia.

**Funcionalidades da Fase 2:**
- Sistema de Revisão Espaçada (Spaced Repetition)
- Sistema de Quiz
- Sistema de Favoritos (expandido)
- Personalização por Profissão e Interesse
- Integração com Dashboard (métricas avançadas)

---

## Pré-requisitos

1. **Backend rodando**: `http://localhost:8080`
2. **PostgreSQL rodando**: `localhost:5432`
3. **Insomnia instalado** com a collection importada
4. **Variáveis de ambiente configuradas**:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (preenchido após login)
5. **Dados de teste preparados**:
   - Usuário registrado e logado
   - Categorias criadas
   - Termos criados
   - Termos estudados (para testar revisão)

---

## 1. Sistema de Revisão Espaçada

### 1.1 Obter Termos para Revisão

**Endpoint:** `GET /api/terms/review`

**Conceito:** Retorna termos que estão prontos para revisão baseado no algoritmo de spaced repetition.

**Passo a passo:**
1. Abra `Terms → Get Terms for Review`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos com `nextReviewAt <= NOW`
- ✅ Apenas termos com status `STUDIED` ou `IN_REVIEW`
- ✅ Ordenados por `nextReviewAt` (mais urgentes primeiro)

**Preparação para teste:**
1. Estude alguns termos (`POST /api/terms/{id}/studied`)
2. Espere ou manipule `nextReviewAt` no banco para testar

**Testes adicionais:**
- [ ] Testar sem termos para revisar (array vazio)
- [ ] Testar sem autenticação (401)
- [ ] Verificar ordenação por urgência

---

### 1.2 Marcar Termo como Revisado

**Endpoint:** `POST /api/terms/{id}/review?success={boolean}`

**Conceito:** Registra que o usuário revisou um termo e calcula a próxima data de revisão baseado no algoritmo SM-2.

**Passo a passo:**
1. Abra `Terms → Mark Term as Reviewed`
2. Substitua `{id}` pelo ID do termo
3. Altere o parâmetro `success`:
   - `true`: usuário acertou/lembrou
   - `false`: usuário errou/esqueceu
4. Clique em **Send**

**Resultados esperados (success=true):**
- ✅ Status: `200 OK`
- ✅ `reviewCount` incrementado
- ✅ `correctAnswers` incrementado
- ✅ `nextReviewAt` calculado (intervalo aumenta)
- ✅ `knowledgeLevel` pode aumentar

**Resultados esperados (success=false):**
- ✅ Status: `200 OK`
- ✅ `reviewCount` incrementado
- ✅ `correctAnswers` NÃO incrementado
- ✅ `nextReviewAt` resetado para 1 dia
- ✅ `knowledgeLevel` pode diminuir

**Algoritmo esperado (SM-2 simplificado):**
- 1ª revisão: +1 dia
- 2ª revisão: +3 dias
- 3ª revisão: +7 dias
- 4ª revisão: +14 dias
- Após erro: reset para +1 dia

**Testes adicionais:**
- [ ] Marcar revisão com sucesso=true múltiplas vezes
- [ ] Marcar revisão com sucesso=false e verificar reset
- [ ] Verificar crescimento exponencial do intervalo
- [ ] Testar com termo inexistente (404)

---

### 1.3 Verificar Progresso de Revisão

**Passo a passo para verificar:**
1. Execute `GET /api/terms/review` antes da revisão
2. Execute `POST /api/terms/{id}/review?success=true`
3. Execute `GET /api/terms/review` novamente
4. O termo revisado não deve aparecer mais na lista

**Teste de intervalo:**
1. Marque termo como estudado
2. Marque como revisado com sucesso=true
3. Verifique no banco se `nextReviewAt` está no futuro
4. Repita e veja o intervalo crescer

---

## 2. Sistema de Quiz

### 2.1 Obter Quiz para um Termo

**Endpoint:** `GET /api/quiz/term/{termId}`

**Conceito:** Retorna uma pergunta de quiz associada a um termo, com 4 opções de resposta.

**Passo a passo:**
1. Abra `Quiz → Get Quiz for Term`
2. Substitua `{termId}` pelo ID do termo
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `id`: ID da pergunta
  - `questionText`: texto da pergunta
  - `explanation`: explicação da resposta
  - `difficultyLevel`: dificuldade
  - `options`: array de 4 opções
    - `id`: ID da opção
    - `optionText`: texto da opção
    - `orderIndex`: ordem (0-3)
    - **NÃO** inclui `isCorrect` (cliente não deve saber)

**Preparação para teste:**
1. Crie um termo
2. Crie uma pergunta de quiz para o termo (via banco ou endpoint admin)
3. Crie 4 opções (1 correta, 3 incorretas)

**Testes adicionais:**
- [ ] Testar com termo sem quiz (deve retornar erro ou null)
- [ ] Verificar se opções estão embaralhadas
- [ ] Verificar se `isCorrect` não está exposto

---

### 2.2 Submeter Resposta do Quiz

**Endpoint:** `POST /api/quiz/answer`

**Conceito:** Processa a resposta do usuário, valida se está correta e atualiza estatísticas.

**Passo a passo:**
1. Abra `Quiz → Submit Answer`
2. No body JSON:
   ```json
   {
     "questionId": 1,
     "selectedOptionId": 2
   }
   ```
3. Clique em **Send**

**Resultados esperados (resposta correta):**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `isCorrect`: true
  - `correctOptionId`: ID da opção correta
  - `explanation`: explicação
- ✅ `quiz_answers` registro criado no banco
- ✅ Estatísticas do usuário atualizadas

**Resultados esperados (resposta incorreta):**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `isCorrect`: false
  - `correctOptionId`: ID da opção correta
  - `explanation`: explicação
- ✅ Termo marcado para revisão

**Testes adicionais:**
- [ ] Responder corretamente e verificar estatísticas
- [ ] Responder incorretamente e verificar se entra para revisão
- [ ] Responder mesma pergunta múltiplas vezes
- [ ] Testar com `questionId` inexistente
- [ ] Testar com `selectedOptionId` inexistente

---

### 2.3 Obter Estatísticas de Quiz

**Endpoint:** `GET /api/quiz/stats`

**Conceito:** Retorna estatísticas de desempenho do usuário em quizzes.

**Passo a passo:**
1. Abra `Quiz → Get Quiz Stats`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `totalAnswered`: total de perguntas respondidas
  - `correctCount`: total de acertos
  - `incorrectCount`: total de erros
  - `averageScore`: média de acertos (%)

**Teste completo:**
1. Responda 5 quizzes corretamente
2. Responda 2 quizzes incorretamente
3. Execute `GET /api/quiz/stats`
4. Verifique:
   - `totalAnswered`: 7
   - `correctCount`: 5
   - `incorrectCount`: 2
   - `averageScore`: ~71.4%

**Testes adicionais:**
- [ ] Testar sem quizzes respondidos (todos zeros)
- [ ] Testar sem autenticação (401)

---

### 2.4 Obter Termos para Revisão (Erros de Quiz)

**Endpoint:** `GET /api/quiz/review-needed`

**Conceito:** Retorna termos que o usuário errou em quizzes e precisa revisar.

**Passo a passo:**
1. Abra `Quiz → Get Wrong Answers for Review`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos com erros recentes
- ✅ Ordenados por frequência de erro

**Teste completo:**
1. Responda alguns quizzes incorretamente
2. Execute `GET /api/quiz/review-needed`
3. Verifique se os termos errados aparecem
4. Revise os termos
5. Execute novamente e verifique se sumiram

**Testes adicionais:**
- [ ] Testar sem erros (array vazio)
- [ ] Testar sem autenticação (401)

---

## 3. Sistema de Favoritos (Expandido)

### 3.1 Toggle Favorito

**Endpoint:** `POST /api/terms/{id}/favorite`

**Conceito:** Marca ou desmarca um termo como favorito (toggle).

**Passo a passo:**
1. Abra `Terms → Toggle Favorite`
2. Substitua `{id}` pelo ID do termo
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Primeira chamada: marca como favorito
- ✅ Segunda chamada: remove favorito

**Teste completo:**
1. Execute `POST /api/terms/{id}/favorite`
2. Execute `GET /api/terms/{id}/favorite` → deve retornar `true`
3. Execute `POST /api/terms/{id}/favorite` novamente
4. Execute `GET /api/terms/{id}/favorite` → deve retornar `false`

---

### 3.2 Listar Favoritos

**Endpoint:** `GET /api/terms/favorites`

**Conceito:** Lista todos os termos favoritados pelo usuário.

**Passo a passo:**
1. Abra `Terms → Get Favorite Terms`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos favoritados
- ✅ Vazio se não houver favoritos

**Teste completo:**
1. Favorite 3 termos diferentes
2. Execute `GET /api/terms/favorites`
3. Verifique se os 3 termos aparecem
4. Remova 1 favorito
5. Execute novamente e verifique se só 2 aparecem

---

## 4. Personalização por Profissão e Interesse

### 4.1 Adicionar Interesse

**Endpoint:** `POST /api/user/interests`

**Conceito:** Adiciona uma categoria como interesse do usuário para personalizar recomendações.

**Passo a passo:**
1. Abra `User Interests → Add Interest`
2. No body JSON:
   ```json
   {
     "categoryId": 1
   }
   ```
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `categoryId`: ID da categoria
  - `categoryName`: nome da categoria
  - `categoryDescription`: descrição
- ✅ Interesse criado no banco

**Testes adicionais:**
- [ ] Tentar adicionar mesmo interesse duas vezes (deve falhar ou ignorar)
- [ ] Testar com `categoryId` inexistente
- [ ] Testar sem autenticação (401)

---

### 4.2 Listar Interesses

**Endpoint:** `GET /api/user/interests`

**Conceito:** Lista todas as categorias de interesse do usuário.

**Passo a passo:**
1. Abra `User Interests → Get User Interests`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de interesses
- ✅ Cada interesse contém `categoryId`, `categoryName`, `categoryDescription`

**Teste completo:**
1. Adicione 3 interesses diferentes
2. Execute `GET /api/user/interests`
3. Verifique se os 3 aparecem
4. Remova 1 interesse
5. Execute novamente e verifique se só 2 aparecem

---

### 4.3 Remover Interesse

**Endpoint:** `DELETE /api/user/interests/{categoryId}`

**Conceito:** Remove uma categoria dos interesses do usuário.

**Passo a passo:**
1. Abra `User Interests → Remove Interest`
2. Substitua `{categoryId}` pelo ID da categoria
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `204 No Content`
- ✅ Interesse removido do banco

**Testes adicionais:**
- [ ] Tentar remover interesse que não existe (não deve dar erro)
- [ ] Testar sem autenticação (401)

---

### 4.4 Obter Recomendações

**Endpoint:** `GET /api/user/interests/recommendations`

**Conceito:** Retorna termos recomendados baseados nos interesses do usuário.

**Passo a passo:**
1. Abra `User Interests → Get Recommendations`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos
- ✅ Apenas termos de categorias de interesse
- ✅ Exclui termos já estudados
- ✅ Respeita plano do usuário

**Teste completo:**
1. Adicione interesse em "Desenvolvimento de Software"
2. Crie termos nessa categoria
3. Execute `GET /api/user/interests/recommendations`
4. Verifique se termos dessa categoria aparecem
5. Estude um dos termos
6. Execute novamente e verifique se o termo estudado não aparece

**Testes adicionais:**
- [ ] Testar sem interesses (array vazio)
- [ ] Testar sem autenticação (401)

---

### 4.5 Atualizar Profissão

**Endpoint:** `PUT /api/user/profession`

**Conceito:** Atualiza a profissão do usuário para personalização futura.

**Passo a passo:**
1. Abra `User → Update Profession`
2. No body JSON:
   ```json
   {
     "profession": "Desenvolvedor Full Stack"
   }
   ```
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Profissão atualizada no banco

**Testes adicionais:**
- [ ] Testar com profissão vazia
- [ ] Testar sem autenticação (401)

---

### 4.6 Atualizar Nível

**Endpoint:** `PUT /api/user/level`

**Conceito:** Atualiza o nível de conhecimento do usuário.

**Passo a passo:**
1. Abra `User → Update Level`
2. No body JSON:
   ```json
   {
     "level": "ADVANCED"
   }
   ```
   - Valores possíveis: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Nível atualizado no banco

**Testes adicionais:**
- [ ] Testar com nível inválido (400)
- [ ] Testar sem autenticação (401)

---

## 5. Integração com Dashboard

### 5.1 Obter Dashboard (Métricas Avançadas)

**Endpoint:** `GET /api/dashboard`

**Conceito:** Retorna métricas completas do usuário incluindo dados da Fase 2.

**Passo a passo:**
1. Abra `Dashboard → Get Dashboard`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `termsLearned`: termos estudados
  - `favoriteCount`: favoritos
  - `streakDays`: dias consecutivos
  - `termsForReviewCount`: termos para revisar
  - `quizAccuracy`: precisão em quizzes (%)
  - `knowledgeLevelDistribution`: distribuição por nível

**Teste completo:**
1. Estude 5 termos
2. Favorite 2 termos
3. Responda 10 quizzes (7 corretos, 3 errados)
4. Execute `GET /api/dashboard`
5. Verifique:
   - `termsLearned`: 5
   - `favoriteCount`: 2
   - `quizAccuracy`: 70%
   - `termsForReviewCount`: termos prontos para revisão

---

## 6. Cenários de Teste Completos

### Cenário 1: Fluxo de Revisão Espaçada

1. **Preparação**
   - Registrar usuário e fazer login
   - Criar categoria e termos
   - Estudar alguns termos (`POST /api/terms/{id}/studied`)

2. **Primeira revisão**
   - `GET /api/terms/review` → ver termos prontos
   - `POST /api/terms/{id}/review?success=true` → revisar com sucesso
   - Verificar no banco: `nextReviewAt` = +1 dia

3. **Segunda revisão**
   - Manipular `nextReviewAt` para hoje (via banco)
   - `GET /api/terms/review` → ver termo
   - `POST /api/terms/{id}/review?success=true` → revisar
   - Verificar no banco: `nextReviewAt` = +3 dias

4. **Revisão com erro**
   - Manipular `nextReviewAt` para hoje
   - `POST /api/terms/{id}/review?success=false` → erro
   - Verificar no banco: `nextReviewAt` resetado para +1 dia

---

### Cenário 2: Fluxo de Quiz

1. **Preparação**
   - Criar termo com quiz associado
   - Criar 4 opções (1 correta, 3 incorretas)

2. **Responder quiz corretamente**
   - `GET /api/quiz/term/{termId}` → obter quiz
   - `POST /api/quiz/answer` com opção correta
   - Verificar `isCorrect: true`
   - `GET /api/quiz/stats` → verificar acerto

3. **Responder quiz incorretamente**
   - `POST /api/quiz/answer` com opção incorreta
   - Verificar `isCorrect: false`
   - `GET /api/quiz/review-needed` → termo aparece
   - `GET /api/terms/review` → termo aparece para revisão

4. **Verificar estatísticas**
   - `GET /api/quiz/stats` → ver precisão
   - `GET /api/dashboard` → ver quizAccuracy

---

### Cenário 3: Personalização por Interesse

1. **Configurar perfil**
   - `PUT /api/user/profession` → definir profissão
   - `PUT /api/user/level` → definir nível

2. **Adicionar interesses**
   - Criar categorias (ex: "DevOps", "Frontend", "Backend")
   - `POST /api/user/interests` → adicionar "DevOps"
   - `POST /api/user/interests` → adicionar "Backend"

3. **Obter recomendações**
   - Criar termos em cada categoria
   - `GET /api/user/interests/recommendations`
   - Verificar se só termos de DevOps e Backend aparecem

4. **Testar termo do dia**
   - `GET /api/terms/daily`
   - Verificar se termo é de categoria de interesse

5. **Remover interesse**
   - `DELETE /api/user/interests/{categoryId}`
   - `GET /api/user/interests/recommendations`
   - Verificar se termos dessa categoria somem

---

### Cenário 4: Integração Completa

1. **Setup inicial**
   - Registrar usuário
   - Completar perfil (profissão, nível)
   - Adicionar interesses

2. **Estudo e quiz**
   - `GET /api/terms/daily` → obter termo
   - `GET /api/quiz/term/{termId}` → obter quiz
   - `POST /api/quiz/answer` → responder (correto)
   - `POST /api/terms/{id}/studied` → marcar como estudado

3. **Revisão espaçada**
   - Manipular `nextReviewAt` para hoje
   - `GET /api/terms/review` → ver termo
   - `POST /api/terms/{id}/review?success=true` → revisar

4. **Favoritos**
   - `POST /api/terms/{id}/favorite` → favoritar
   - `GET /api/terms/favorites` → listar

5. **Dashboard final**
   - `GET /api/dashboard` → ver todas métricas
   - Verificar:
     - `termsLearned` incrementado
     - `quizAccuracy` calculado
     - `termsForReviewCount` atualizado
     - `favoriteCount` correto

---

## 7. Checklist de Validação

### Revisão Espaçada
- [ ] Sistema calcula próxima data de revisão automaticamente
- [ ] Usuário pode ver termos para revisar hoje
- [ ] Revisão atualiza contador e knowledge_level
- [ ] Intervalo aumenta após acertos
- [ ] Intervalo reseta após erros
- [ ] Algoritmo SM-2 funciona corretamente

### Quiz
- [ ] Quiz gerado para cada termo
- [ ] Usuário pode responder quiz
- [ ] Sistema valida resposta e mostra explicação
- [ ] Estatísticas de quiz disponíveis
- [ ] Erros em quiz geram necessidade de revisão
- [ ] Opções não expõem resposta correta

### Favoritos
- [ ] Usuário pode marcar termo como favorito
- [ ] Usuário pode listar favoritos
- [ ] Usuário pode remover favorito
- [ ] Favoritos persistem corretamente
- [ ] Toggle funciona corretamente

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
- [ ] Métricas atualizam em tempo real

---

## 8. Testes de Integração entre Funcionalidades

### Quiz → Revisão Espaçada
1. Responder quiz incorretamente
2. Verificar se termo aparece em `GET /api/terms/review`
3. Verificar se termo aparece em `GET /api/quiz/review-needed`

### Revisão Espaçada → Knowledge Level
1. Revisar termo com sucesso 5 vezes
2. Verificar se `knowledgeLevel` aumentou para `USE_IN_WORK`
3. Revisar com erro
4. Verificar se `knowledgeLevel` diminuiu

### Interesses → Termo do Dia
1. Adicionar interesse em categoria X
2. Criar termos em categoria X e Y
3. `GET /api/terms/daily`
4. Verificar se termo é da categoria X

### Quiz → Dashboard
1. Responder 10 quizzes (7 corretos)
2. `GET /api/dashboard`
3. Verificar `quizAccuracy` = 70%

---

## 9. Dicas e Troubleshooting

### Problema: Quiz não retorna para termo
**Solução:** Verifique se há pergunta de quiz criada para o termo no banco

### Problema: Revisão não retorna termos
**Solução:** Estude termos primeiro e manipule `nextReviewAt` para hoje

### Problema: Recomendações vazias
**Solução:** Adicione interesses ao usuário e crie termos nessas categorias

### Problema: Estatísticas de quiz incorretas
**Solução:** Verifique se `quiz_answers` está sendo populado corretamente

### Problema: Intervalo de revisão não cresce
**Solução:** Verifique se `success=true` está sendo enviado e se o algoritmo está implementado

---

## 10. Validação de Algoritmo SM-2

### Teste Manual do Algoritmo

**Fórmula esperada:**
- Intervalo inicial: 1 dia
- Fator de crescimento: 2.5
- Após erro: reset para 1 dia

**Passo a passo:**
1. Estude termo → `nextReviewAt` = hoje + 1 dia
2. Revisar com sucesso → `nextReviewAt` = hoje + 3 dias
3. Revisar com sucesso → `nextReviewAt` = hoje + 7 dias
4. Revisar com sucesso → `nextReviewAt` = hoje + 14 dias
5. Revisar com erro → `nextReviewAt` = hoje + 1 dia (reset)

**Verificação no banco:**
```sql
SELECT id, term_id, review_count, correct_answers, next_review_at, knowledge_level
FROM user_term
WHERE user_id = {userId};
```

---

## 11. Performance e Escalabilidade

### Testes de Performance
- [ ] `GET /api/terms/review` com 1000 termos < 500ms
- [ ] `GET /api/quiz/stats` com 1000 respostas < 300ms
- [ ] `GET /api/user/interests/recommendations` < 500ms
- [ ] Cálculo de `nextReviewAt` não bloqueia requisição

### Testes de Concorrência
- [ ] Múltiplos usuários revisando simultaneamente
- [ ] Múltiplos usuários respondendo quiz simultaneamente
- [ ] Race conditions em `toggleFavorite`

---

## 12. Próximos Passos

Após concluir os testes da Fase 2:

1. **Documentar bugs encontrados**
2. **Otimizar algoritmo de spaced repetition**
3. **Adicionar mais perguntas de quiz**
4. **Implementar notificações para revisão**
5. **Preparar para Fase 3** (Monetização)

---

## 13. Referências

- [Plano de Testes Completo](./TEST_PLAN.md)
- [Guia de Testes Fase 1](./TESTING_GUIDE_PHASE1.md)
- [Documentação da Fase 2](./FASE2.md)
- [Arquitetura do Banco](./DATABASE_ARCHITECTURE.md)
- [Collection do Insomnia](../insomnia-collection.json)
