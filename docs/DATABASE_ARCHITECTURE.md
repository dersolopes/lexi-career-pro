# 📊 Arquitetura de Banco de Dados - LexiCareer Pro

## 1. Visão Geral

O banco de dados foi modelado com foco em:
- Escalabilidade: estrutura preparada para crescimento
- Normalização: evitar redundância
- Performance: índices estratégicos
- Auditoria: rastreamento de mudanças
- Flexibilidade: pronto para premium e novos recursos

---

## 2. Diagrama ER (Entity-Relationship)

```text
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ id (PK)         │
│ email (UNIQUE)  │
│ password        │
│ name            │
│ profession      │
│ level           │
│ plan            │
│ created_at      │
│ updated_at      │
└────────┬────────┘
         │
         ├──────────────────────┬──────────────────────┐
         │                      │                      │
         ▼                      ▼                      ▼
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  USER_INTERESTS  │   │  NOTIFICATION   │   │    USER_TERM     │
├──────────────────┤   │    PREFERENCE   │   ├──────────────────┤
│ id (PK)          │   ├──────────────────┤   │ id (PK)          │
│ user_id (FK)     │   │ id (PK)          │   │ user_id (FK)     │
│ category_id (FK) │   │ user_id (FK)     │   │ term_id (FK)     │
│ created_at       │   │ enabled          │   │ status           │
└──────────────────┘   │ preferred_time   │   │ knowledge_level  │
                          │ frequency        │   │ is_favorite      │
                          │ created_at       │   │ last_review_at   │
                          │ updated_at       │   │ next_review_at   │
                          └──────────────────┘   │ review_count     │
                                                 │ correct_answers  │
                                                 │ created_at       │
                                                 │ updated_at       │
                                                 └──────────────────┘

┌──────────────────┐
│    CATEGORIES    │
├──────────────────┤
│ id (PK)          │
│ name             │
│ description      │
│ parent_id (FK)   │
│ plan_type        │
│ display_order    │
│ slug             │
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│      TERMS       │
├──────────────────┤
│ id (PK)          │
│ name             │
│ definition       │
│ simple_expl      │
│ example          │
│ difficulty_level │
│ category_id (FK) │
│ plan_type        │
│ created_by       │
│ is_active        │
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │
         ▼
┌────────────────────┐
│   TERM_RELATIONS    │
├────────────────────┤
│ id (PK)            │
│ term_id (FK)       │
│ related_term_id    │
│ relation_type      │
│ created_at         │
└────────────────────┘

┌─────────────────────────┐
│     QUIZ_QUESTIONS      │
├─────────────────────────┤
│ id (PK)                │
│ term_id (FK)           │
│ question_text          │
│ explanation            │
│ difficulty_level       │
│ created_at             │
│ updated_at             │
└────────────┬──────────┘
             │
             ▼
┌─────────────────────────┐
│      QUIZ_OPTIONS       │
├─────────────────────────┤
│ id (PK)                │
│ question_id (FK)        │
│ option_text            │
│ is_correct             │
│ order_index            │
│ created_at             │
└─────────────────────────┘

┌─────────────────────────┐
│      QUIZ_ANSWERS       │
├─────────────────────────┤
│ id (PK)                │
│ user_id (FK)           │
│ question_id (FK)        │
│ selected_option_id(FK)  │
│ is_correct             │
│ answered_at            │
└─────────────────────────┘

┌─────────────────────────┐
│      DAILY_TERM_LOG     │
├─────────────────────────┤
│ id (PK)                │
│ user_id (FK)           │
│ term_id (FK)           │
│ shown_at               │
│ studied_at             │
│ study_duration        │
│ created_at             │
└─────────────────────────┘
```

---

## 3. Tabelas Detalhadas

### 3.1 USERS
Armazena informações do usuário.

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    profession VARCHAR(255),
    level VARCHAR(50) CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE' CHECK (plan IN ('FREE', 'PREMIUM')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_plan ON users(plan);
```

---

### 3.2 CATEGORIES
Armazena categorias de termos (hierárquicas).

```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE' CHECK (plan_type IN ('FREE', 'PREMIUM')),
    display_order INT DEFAULT 0,
    slug VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_plan_type ON categories(plan_type);
CREATE INDEX idx_categories_slug ON categories(slug);
```

---

### 3.3 TERMS
Armazena os termos/palavras para aprendizado.

```sql
CREATE TABLE terms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    definition TEXT NOT NULL,
    simple_explanation TEXT NOT NULL,
    example TEXT NOT NULL,
    difficulty_level VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE' 
        CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE' CHECK (plan_type IN ('FREE', 'PREMIUM')),
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_terms_category_id ON terms(category_id);
CREATE INDEX idx_terms_plan_type ON terms(plan_type);
CREATE INDEX idx_terms_difficulty_level ON terms(difficulty_level);
CREATE INDEX idx_terms_name ON terms(name);
CREATE UNIQUE INDEX idx_terms_name_category ON terms(LOWER(name), category_id);
```

---

### 3.4 USER_INTERESTS
Rastreia categorias de interesse do usuário.

```sql
CREATE TABLE user_interests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, category_id)
);

CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
CREATE INDEX idx_user_interests_category_id ON user_interests(category_id);
```

---

### 3.5 USER_TERM
Relacionamento central do progresso do usuário em cada termo.

```sql
CREATE TABLE user_term (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW' 
        CHECK (status IN ('NEW', 'PRESENTED', 'STUDIED', 'IN_REVIEW', 'MASTERED')),
    knowledge_level VARCHAR(50) DEFAULT NULL 
        CHECK (knowledge_level IN ('NEVER_SEEN', 'HEARD_NOT_EXPLAINED', 'KNOWN', 'USE_IN_WORK', NULL)),
    is_favorite BOOLEAN DEFAULT FALSE,
    last_review_at TIMESTAMP,
    next_review_at TIMESTAMP,
    review_count INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    total_quiz_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, term_id)
);

CREATE INDEX idx_user_term_user_id ON user_term(user_id);
CREATE INDEX idx_user_term_term_id ON user_term(term_id);
CREATE INDEX idx_user_term_status ON user_term(status);
CREATE INDEX idx_user_term_next_review_at ON user_term(next_review_at);
CREATE INDEX idx_user_term_is_favorite ON user_term(is_favorite);
CREATE INDEX idx_user_term_user_status ON user_term(user_id, status);
```

---

### 3.6 TERM_RELATIONS
Relacionamentos entre termos.

```sql
CREATE TABLE term_relations (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    related_term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    relation_type VARCHAR(50) DEFAULT 'RELATED' 
        CHECK (relation_type IN ('RELATED', 'PREREQUISITE', 'SIMILAR', 'OPPOSITE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(term_id, related_term_id),
    CHECK (term_id <> related_term_id)
);

CREATE INDEX idx_term_relations_term_id ON term_relations(term_id);
CREATE INDEX idx_term_relations_related_term_id ON term_relations(related_term_id);
CREATE INDEX idx_term_relations_type ON term_relations(relation_type);
```

---

### 3.7 NOTIFICATION_PREFERENCE
Preferências de notificação do usuário.

```sql
CREATE TABLE notification_preference (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    enabled BOOLEAN DEFAULT TRUE,
    preferred_time TIME DEFAULT '09:00:00',
    frequency VARCHAR(50) DEFAULT 'DAILY' CHECK (frequency IN ('DAILY', 'WEEKLY', 'NEVER')),
    terms_per_day INT DEFAULT 1 CHECK (terms_per_day > 0 AND terms_per_day <= 5),
    send_email BOOLEAN DEFAULT FALSE,
    send_push BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_preference_user_id ON notification_preference(user_id);
```

---

### 3.8 DAILY_TERM_LOG
Log do termo do dia para cada usuário.

```sql
CREATE TABLE daily_term_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    shown_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    studied_at TIMESTAMP,
    study_duration_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, DATE(shown_at))
);

CREATE INDEX idx_daily_term_log_user_id ON daily_term_log(user_id);
CREATE INDEX idx_daily_term_log_shown_at ON daily_term_log(shown_at);
CREATE INDEX idx_daily_term_log_user_shown ON daily_term_log(user_id, shown_at);
```

---

### 3.9 QUIZ_QUESTIONS
Perguntas de quiz associadas a termos.

```sql
CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    explanation TEXT,
    difficulty_level VARCHAR(50) DEFAULT 'INTERMEDIATE' 
        CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_questions_term_id ON quiz_questions(term_id);
```

---

### 3.10 QUIZ_OPTIONS
Opções de resposta para as perguntas.

```sql
CREATE TABLE quiz_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    order_index INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_options_question_id ON quiz_options(question_id);
CREATE INDEX idx_quiz_options_order ON quiz_options(question_id, order_index);
```

---

### 3.11 QUIZ_ANSWERS
Respostas do usuário em quizzes.

```sql
CREATE TABLE quiz_answers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    selected_option_id BIGINT REFERENCES quiz_options(id) ON DELETE SET NULL,
    is_correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_answers_user_id ON quiz_answers(user_id);
CREATE INDEX idx_quiz_answers_question_id ON quiz_answers(question_id);
CREATE INDEX idx_quiz_answers_answered_at ON quiz_answers(answered_at);
CREATE INDEX idx_quiz_answers_user_correct ON quiz_answers(user_id, is_correct);
```

---

## 4. Estratégia de Índices

| Tabela | Índice | Motivo |
|--------|--------|--------|
| users | email | Busca por login |
| users | plan | Filtrar por plano |
| categories | plan_type | Mostrar categorias por plano |
| categories | slug | URL-friendly lookup |
| terms | category_id | Listar termos por categoria |
| terms | plan_type | Filtrar por plano |
| terms | name | Busca por termo |
| user_term | user_id | Histórico do usuário |
| user_term | status | Listar termos por estado |
| user_term | next_review_at | Encontra termos para revisar |
| daily_term_log | user_id + shown_at | Verificar termo do dia |
| quiz_answers | user_id | Histórico de quiz |

---

## 5. Constraints e Validações

1. Um usuário não pode ter o mesmo termo duplicado — `UNIQUE(user_id, term_id)`
2. Um usuário deve ter apenas uma preferência de notificação — `UNIQUE(user_id)`
3. Termos duplicados não podem estar na mesma categoria — `UNIQUE(LOWER(name), category_id)`
4. Não pode deletar categoria se houver termos — `ON DELETE RESTRICT`
5. Termo relacionado não pode ser a si mesmo — `CHECK (term_id <> related_term_id)`
6. Um usuário por dia recebe apenas um termo do dia — `UNIQUE(user_id, DATE(shown_at))`

---

## 6. Queries Estratégicas do MVP

### Obter termo do dia para um usuário
```sql
SELECT t.*
FROM terms t
LEFT JOIN daily_term_log dtl ON t.id = dtl.term_id
    AND dtl.user_id = $1
    AND DATE(dtl.shown_at) = CURRENT_DATE
LEFT JOIN user_interests ui ON t.category_id = ui.category_id AND ui.user_id = $1
WHERE dtl.id IS NULL
    AND t.plan_type = $2
    AND t.is_active = TRUE
    AND ui.category_id IS NOT NULL
ORDER BY RANDOM()
LIMIT 1;
```

### Obter termos para revisar
```sql
SELECT ut.*, t.name, t.definition
FROM user_term ut
JOIN terms t ON ut.term_id = t.id
WHERE ut.user_id = $1
  AND ut.next_review_at <= CURRENT_TIMESTAMP
  AND ut.status IN ('STUDIED', 'IN_REVIEW')
ORDER BY ut.next_review_at ASC
LIMIT 10;
```

### Dashboard do usuário
```sql
SELECT
    COUNT(DISTINCT ut.id) AS terms_learned,
    COUNT(DISTINCT CASE WHEN ut.is_favorite = TRUE THEN ut.id END) AS favorite_count,
    COALESCE(AVG(CAST(qa.is_correct AS INT)) * 100, 0) AS average_score
FROM user_term ut
LEFT JOIN quiz_answers qa ON qa.user_id = $1
WHERE ut.user_id = $1;
```

---

## 7. Preparação para Crescimento

### Recursos Futuros Já Suportados:
- Planos FREE/PREMIUM na estrutura
- Spaced Repetition (campos next_review_at, review_count)
- Quiz
- Relacionamentos entre termos
- Categorias hierárquicas
- Notificações
- Auditoria
- Gamificação

### Fácil de Adicionar:
- Trilhas profissionais
- Achievements/Badges
- Comentários
- IA
- E-mail marketing
- Pagamentos

---

## 8. Próxima Etapa

A próxima etapa será:
- criar o script `schema.sql` completo
- iniciar as entidades JPA do backend em Spring Boot
- definir os primeiros DTOs e repositories

