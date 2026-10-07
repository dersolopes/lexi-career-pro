# 📖 Guia de Testes - Fase 1 (MVP)

## Visão Geral

Este guia fornece instruções passo a passo para testar todas as funcionalidades do MVP (Fase 1) do LexiCareer Pro usando o Insomnia.

**Funcionalidades da Fase 1:**
- Autenticação (registro e login)
- Gerenciamento de perfil do usuário
- Categorias de conteúdo
- Termos com definição, exemplo e contexto
- Termo do dia
- Marcação como estudado
- Favoritos
- Histórico de estudo
- Dashboard básico
- Busca por termo

---

## Pré-requisitos

1. **Backend rodando**: `http://localhost:8080`
2. **PostgreSQL rodando**: `localhost:5432`
3. **Insomnia instalado** com a collection importada
4. **Variáveis de ambiente configuradas**:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (preenchido após login)

---

## 1. Autenticação

### 1.1 Registrar Novo Usuário

**Endpoint:** `POST /api/auth/register`

**Passo a passo:**
1. Abra a requisição `Auth → Register`
2. No body JSON, altere os dados:
   ```json
   {
     "email": "usuario@teste.com",
     "password": "Senha123!",
     "name": "Usuário de Teste"
   }
   ```
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém `token`, `email`, `name`
- ❌ Status `409 Conflict`: Email já existe (troque o email)

**Testes adicionais:**
- [ ] Testar com email inválido (sem @)
- [ ] Testar com senha fraca (menos de 6 caracteres)
- [ ] Testar sem campo `name`
- [ ] Testar sem campo `password`

---

### 1.2 Fazer Login

**Endpoint:** `POST /api/auth/login`

**Passo a passo:**
1. Abra a requisição `Auth → Login`
2. No body JSON, use as credenciais:
   ```json
   {
     "email": "usuario@teste.com",
     "password": "Senha123!"
   }
   ```
3. Clique em **Send**
4. **Copie o token** da resposta

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém `token` JWT válido
- ❌ Status `401 Unauthorized`: Senha incorreta
- ❌ Status `404 Not Found`: Email não existe

**Após sucesso:**
1. Clique no ícone de engrenagem (Manage Environments)
2. Cole o token na variável `jwt_token`
3. Clique em **Done**

**Testes adicionais:**
- [ ] Testar com email não cadastrado
- [ ] Testar com senha incorreta
- [ ] Testar sem senha
- [ ] Testar sem email

---

## 2. Perfil do Usuário

### 2.1 Obter Dados do Usuário Atual

**Endpoint:** `GET /api/user/me`

**Passo a passo:**
1. Abra `User → Get Current User`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém: `id`, `email`, `name`, `profession`, `level`, `plan`, `createdAt`

**Testes adicionais:**
- [ ] Testar sem token (deve dar 401)
- [ ] Testar com token inválido (deve dar 401)

---

### 2.2 Atualizar Profissão

**Endpoint:** `PUT /api/user/profession`

**Passo a passo:**
1. Abra `User → Update Profession`
2. No body JSON:
   ```json
   {
     "profession": "Desenvolvedor de Software"
   }
   ```
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Profissão atualizada no banco

**Verificação:**
1. Execute `GET /api/user/me` novamente
2. Confirme que `profession` foi atualizado

**Testes adicionais:**
- [ ] Testar com profissão vazia
- [ ] Testar sem autenticação

---

### 2.3 Atualizar Nível

**Endpoint:** `PUT /api/user/level`

**Passo a passo:**
1. Abra `User → Update Level`
2. No body JSON:
   ```json
   {
     "level": "INTERMEDIATE"
   }
   ```
   - Valores possíveis: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Nível atualizado no banco

**Verificação:**
1. Execute `GET /api/user/me`
2. Confirme que `level` foi atualizado

**Testes adicionais:**
- [ ] Testar com nível inválido (ex: "EXPERT")
- [ ] Testar sem autenticação

---

## 3. Categorias

### 3.1 Criar Categoria

**Endpoint:** `POST /api/categories`

**Passo a passo:**
1. Abra `Categories → Create Category`
2. No body JSON:
   ```json
   {
     "name": "Desenvolvimento de Software",
     "description": "Termos relacionados a desenvolvimento de software",
     "planType": "FREE",
     "displayOrder": 1,
     "slug": "desenvolvimento-software"
   }
   ```
3. Clique em **Send**
4. **Copie o `id`** da resposta (será usado nos testes de termos)

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém `id`, `name`, `description`, `planType`

**Testes adicionais:**
- [ ] Testar com nome duplicado
- [ ] Testar sem `name`
- [ ] Testar com `planType` inválido

---

### 3.2 Listar Categorias

**Endpoint:** `GET /api/categories`

**Passo a passo:**
1. Abra `Categories → List Categories`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de categorias com todas as criadas
- ✅ Apenas categorias do plano do usuário (FREE/PREMIUM)

**Testes adicionais:**
- [ ] Verificar se `displayOrder` está sendo respeitado
- [ ] Testar sem autenticação

---

### 3.3 Obter Categoria por ID

**Endpoint:** `GET /api/categories/{id}`

**Passo a passo:**
1. Abra `Categories → Get Category by ID`
2. Substitua `{id}` pelo ID da categoria criada (ex: `1`)
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém dados da categoria específica

**Testes adicionais:**
- [ ] Testar com ID inexistente (deve dar 404)
- [ ] Testar sem autenticação

---

### 3.4 Atualizar Categoria

**Endpoint:** `PUT /api/categories/{id}`

**Passo a passo:**
1. Abra `Categories → Update Category`
2. Substitua `{id}` pelo ID da categoria
3. No body JSON:
   ```json
   {
     "name": "Desenvolvimento de Software",
     "description": "Descrição atualizada",
     "planType": "FREE",
     "displayOrder": 1,
     "slug": "desenvolvimento-software"
   }
   ```
4. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Descrição atualizada

**Testes adicionais:**
- [ ] Testar com ID inexistente
- [ ] Testar sem autenticação

---

### 3.5 Deletar Categoria

**Endpoint:** `DELETE /api/categories/{id}`

**Passo a passo:**
1. Abra `Categories → Delete Category`
2. Substitua `{id}` pelo ID da categoria
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `204 No Content`
- ✅ Categoria removida do banco

**Testes adicionais:**
- [ ] Tentar deletar categoria com termos (deve dar erro)
- [ ] Testar sem autenticação

---

## 4. Termos

### 4.1 Criar Termo

**Endpoint:** `POST /api/terms`

**Passo a passo:**
1. Abra `Terms → Create Term`
2. No body JSON (substitua `categoryId` pelo ID da categoria):
   ```json
   {
     "name": "API",
     "definition": "Application Programming Interface - um conjunto de regras para construção de software",
     "simpleExplanation": "Uma interface que permite que diferentes sistemas se comuniquem",
     "example": "O Facebook usa APIs para permitir que aplicativos de terceiros acessem dados de usuários",
     "difficultyLevel": "BEGINNER",
     "categoryId": 1,
     "planType": "FREE"
   }
   ```
3. Clique em **Send**
4. **Copie o `id`** do termo criado

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém `id`, `name`, `definition`, etc.

**Testes adicionais:**
- [ ] Testar com `categoryId` inexistente
- [ ] Testar sem campos obrigatórios
- [ ] Testar com `difficultyLevel` inválido

---

### 4.2 Obter Termo por ID

**Endpoint:** `GET /api/terms/{id}`

**Passo a passo:**
1. Abra `Terms → Get Term by ID`
2. Substitua `{id}` pelo ID do termo criado
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém todos os dados do termo
- ✅ Inclui nome da categoria

**Testes adicionais:**
- [ ] Testar com ID inexistente (404)
- [ ] Testar sem autenticação

---

### 4.3 Buscar Termos

**Endpoint:** `GET /api/terms/search?keyword={keyword}`

**Passo a passo:**
1. Abra `Terms → Search Terms`
2. Altere o parâmetro `keyword` (ex: `api`, `software`, `interface`)
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos que contêm a keyword
- ✅ Busca case-insensitive

**Testes adicionais:**
- [ ] Buscar termo que não existe (array vazio)
- [ ] Buscar com keyword vazia
- [ ] Testar sem autenticação

---

### 4.4 Obter Termo do Dia

**Endpoint:** `GET /api/terms/daily`

**Passo a passo:**
1. Abra `Terms → Get Daily Term`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Retorna um termo aleatório
- ✅ Termo não foi estudado hoje pelo usuário
- ✅ Termo é do plano do usuário
- ✅ Termo é de categoria de interesse (se houver)

**Testes adicionais:**
- [ ] Chamar múltiplas vezes (deve retornar mesmo termo no mesmo dia)
- [ ] Testar sem autenticação

---

### 4.5 Marcar Termo como Estudado

**Endpoint:** `POST /api/terms/{id}/studied?knowledgeLevel={level}`

**Passo a passo:**
1. Abra `Terms → Mark as Studied`
2. Substitua `{id}` pelo ID do termo
3. Altere o parâmetro `knowledgeLevel` (opcional):
   - `NEVER_SEEN`
   - `HEARD_NOT_EXPLAINED`
   - `KNOWN`
   - `USE_IN_WORK`
4. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Termo marcado como estudado no banco
- ✅ `user_term.status` = `STUDIED`
- ✅ `user_term.knowledgeLevel` atualizado

**Verificação:**
1. Execute `GET /api/terms/daily` novamente
2. O termo estudado não deve aparecer novamente hoje

**Testes adicionais:**
- [ ] Marcar mesmo termo duas vezes (não deve duplicar)
- [ ] Testar com termo inexistente
- [ ] Testar sem autenticação

---

### 4.6 Favoritar Termo

**Endpoint:** `POST /api/terms/{id}/favorite`

**Passo a passo:**
1. Abra `Terms → Toggle Favorite`
2. Substitua `{id}` pelo ID do termo
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Termo marcado como favorito
- ✅ Se já era favorito, remove o favorito (toggle)

**Testes adicionais:**
- [ ] Executar duas vezes (deve alternar)
- [ ] Testar com termo inexistente
- [ ] Testar sem autenticação

---

### 4.7 Verificar se é Favorito

**Endpoint:** `GET /api/terms/{id}/favorite`

**Passo a passo:**
1. Abra `Terms → Check if Favorite`
2. Substitua `{id}` pelo ID do termo
3. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response: `true` ou `false`

**Testes adicionais:**
- [ ] Testar com termo não favoritado (false)
- [ ] Testar com termo favoritado (true)

---

### 4.8 Listar Favoritos

**Endpoint:** `GET /api/terms/favorites`

**Passo a passo:**
1. Abra `Terms → Get Favorite Terms`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Array de termos favoritados
- ✅ Vazio se não houver favoritos

**Testes adicionais:**
- [ ] Favoritar múltiplos termos e verificar lista
- [ ] Remover favorito e verificar se some da lista

---

## 5. Dashboard

### 5.1 Obter Dashboard

**Endpoint:** `GET /api/dashboard`

**Passo a passo:**
1. Abra `Dashboard → Get Dashboard`
2. Clique em **Send**

**Resultados esperados:**
- ✅ Status: `200 OK`
- ✅ Response contém:
  - `termsLearned`: quantidade de termos estudados
  - `favoriteCount`: quantidade de favoritos
  - `streakDays`: dias consecutivos de estudo
  - `termsForReviewCount`: termos para revisar

**Testes adicionais:**
- [ ] Estudar alguns termos e verificar contador
- [ ] Favoritar termos e verificar contador
- [ ] Testar sem autenticação

---

## 6. Cenários de Teste Completos

### Cenário 1: Fluxo de Novo Usuário

1. **Registrar usuário**
   - `POST /api/auth/register` com dados válidos
   - Verificar status 200 e token recebido

2. **Fazer login**
   - `POST /api/auth/login` com credenciais
   - Copiar token para variável de ambiente

3. **Completar perfil**
   - `PUT /api/user/profession` com profissão
   - `PUT /api/user/level` com nível
   - `GET /api/user/me` para verificar

4. **Adicionar interesses**
   - Criar categoria
   - `POST /api/user/interests` com categoryId

5. **Estudar termo do dia**
   - `GET /api/terms/daily`
   - `POST /api/terms/{id}/studied`

6. **Ver dashboard**
   - `GET /api/dashboard`
   - Verificar métricas atualizadas

---

### Cenário 2: Busca e Estudo de Termos

1. **Criar categoria e termos**
   - `POST /api/categories`
   - `POST /api/terms` (múltiplos)

2. **Buscar termos**
   - `GET /api/terms/search?keyword=api`

3. **Estudar termo específico**
   - `GET /api/terms/{id}`
   - `POST /api/terms/{id}/studied`

4. **Favoritar termo**
   - `POST /api/terms/{id}/favorite`
   - `GET /api/terms/favorites`

5. **Verificar progresso**
   - `GET /api/dashboard`

---

### Cenário 3: Testes de Erro

1. **Autenticação**
   - [ ] Login com senha errada (401)
   - [ ] Registro com email duplicado (409)
   - [ ] Acesso sem token (401)

2. **Validação**
   - [ ] Criar termo sem campos obrigatórios (400)
   - [ ] Atualizar com dados inválidos (400)
   - [ ] Acessar recurso inexistente (404)

3. **Permissões**
   - [ ] Tentar acessar dados de outro usuário (deve falhar)
   - [ ] Tentar deletar categoria de outro usuário (deve falhar)

---

## 7. Checklist de Validação

### Autenticação
- [ ] Registro funciona com dados válidos
- [ ] Login retorna token JWT
- [ ] Token expira corretamente
- [ ] Senhas são criptografadas

### Perfil
- [ ] Usuário pode ver seus dados
- [ ] Usuário pode atualizar profissão
- [ ] Usuário pode atualizar nível
- [ ] Alterações persistem

### Categorias
- [ ] Listar categorias funciona
- [ ] Criar categoria funciona
- [ ] Atualizar categoria funciona
- [ ] Deletar categoria funciona
- [ ] Filtros por plano funcionam

### Termos
- [ ] Criar termo funciona
- [ ] Buscar termo funciona
- [ ] Termo do dia funciona
- [ ] Marcar como estudado funciona
- [ ] Favoritar funciona
- [ ] Listar favoritos funciona

### Dashboard
- [ ] Dashboard mostra métricas corretas
- [ ] Contadores atualizam em tempo real
- [ ] Streak de dias é calculado corretamente

---

## 8. Dicas e Troubleshooting

### Problema: Token expirou
**Solução:** Faça login novamente e atualize a variável `jwt_token`

### Problema: 401 Unauthorized
**Solução:** Verifique se o token está na variável de ambiente e se não expirou

### Problema: 404 Not Found
**Solução:** Verifique se o ID do recurso existe no banco

### Problema: 409 Conflict
**Solução:** Recurso já existe (email duplicado, nome duplicado, etc.)

### Problema: 400 Bad Request
**Solução:** Verifique se o body JSON está correto e todos campos obrigatórios estão presentes

### Problema: Termo do dia retorna null
**Solução:** Crie termos e categorias primeiro, ou adicione interesses ao usuário

---

## 9. Próximos Passos

Após concluir os testes da Fase 1:

1. **Documentar bugs encontrados**
2. **Corrigir issues identificadas**
3. **Preparar dados de seed para testes**
4. **Iniciar implementação da Fase 2** (Revisão Espaçada, Quiz, Personalização)

---

## 10. Referências

- [Plano de Testes Completo](./TEST_PLAN.md)
- [Arquitetura do Banco](./DATABASE_ARCHITECTURE.md)
- [Documentação da Fase 2](./FASE2.md)
- [Collection do Insomnia](../insomnia-collection.json)
