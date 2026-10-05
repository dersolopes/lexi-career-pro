-- ============================================================================
-- LexiCareer Pro - PostgreSQL Database Schema
-- ============================================================================
-- Database: lexicareer_db
-- Version: 1.0.0
-- Created: 2026-10-05
-- ============================================================================

-- Drop existing objects (use with caution in production)
DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- ============================================================================
-- USERS TABLE
-- ============================================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    profession VARCHAR(255),
    level VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE'
        CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE'
        CHECK (plan IN ('FREE', 'PREMIUM')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_plan ON users(plan);
CREATE INDEX idx_users_is_active ON users(is_active);

-- ============================================================================
-- CATEGORIES TABLE
-- ============================================================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE'
        CHECK (plan_type IN ('FREE', 'PREMIUM')),
    display_order INT DEFAULT 0,
    slug VARCHAR(255) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_plan_type ON categories(plan_type);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_is_active ON categories(is_active);

-- ============================================================================
-- TERMS TABLE
-- ============================================================================
CREATE TABLE terms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    definition TEXT NOT NULL,
    simple_explanation TEXT NOT NULL,
    example TEXT NOT NULL,
    difficulty_level VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE'
        CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE'
        CHECK (plan_type IN ('FREE', 'PREMIUM')),
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_terms_category_id ON terms(category_id);
CREATE INDEX idx_terms_plan_type ON terms(plan_type);
CREATE INDEX idx_terms_difficulty_level ON terms(difficulty_level);
CREATE INDEX idx_terms_name ON terms(LOWER(name));
CREATE INDEX idx_terms_is_active ON terms(is_active);
CREATE UNIQUE INDEX idx_terms_name_category ON terms(LOWER(name), category_id)
    WHERE is_active = TRUE;

-- ============================================================================
-- USER_INTERESTS TABLE
-- ============================================================================
CREATE TABLE user_interests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, category_id)
);

CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
CREATE INDEX idx_user_interests_category_id ON user_interests(category_id);

-- ============================================================================
-- USER_TERM TABLE (Central relationship)
-- ============================================================================
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
    review_count INT DEFAULT 0 CHECK (review_count >= 0),
    correct_answers INT DEFAULT 0 CHECK (correct_answers >= 0),
    total_quiz_attempts INT DEFAULT 0 CHECK (total_quiz_attempts >= 0),
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

-- ============================================================================
-- TERM_RELATIONS TABLE
-- ============================================================================
CREATE TABLE term_relations (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    related_term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    relation_type VARCHAR(50) NOT NULL DEFAULT 'RELATED'
        CHECK (relation_type IN ('RELATED', 'PREREQUISITE', 'SIMILAR', 'OPPOSITE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(term_id, related_term_id),
    CHECK (term_id <> related_term_id)
);

CREATE INDEX idx_term_relations_term_id ON term_relations(term_id);
CREATE INDEX idx_term_relations_related_term_id ON term_relations(related_term_id);
CREATE INDEX idx_term_relations_type ON term_relations(relation_type);

-- ============================================================================
-- NOTIFICATION_PREFERENCE TABLE
-- ============================================================================
CREATE TABLE notification_preference (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    enabled BOOLEAN DEFAULT TRUE,
    preferred_time TIME DEFAULT '09:00:00',
    frequency VARCHAR(50) NOT NULL DEFAULT 'DAILY'
        CHECK (frequency IN ('DAILY', 'WEEKLY', 'NEVER')),
    terms_per_day INT NOT NULL DEFAULT 1
        CHECK (terms_per_day > 0 AND terms_per_day <= 5),
    send_email BOOLEAN DEFAULT FALSE,
    send_push BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_preference_user_id ON notification_preference(user_id);
CREATE INDEX idx_notification_preference_enabled ON notification_preference(enabled);

-- ============================================================================
-- DAILY_TERM_LOG TABLE
-- ============================================================================
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

-- ============================================================================
-- QUIZ_QUESTIONS TABLE
-- ============================================================================
CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    explanation TEXT,
    difficulty_level VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE'
        CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_questions_term_id ON quiz_questions(term_id);
CREATE INDEX idx_quiz_questions_is_active ON quiz_questions(is_active);

-- ============================================================================
-- QUIZ_OPTIONS TABLE
-- ============================================================================
CREATE TABLE quiz_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(question_id, order_index)
);

CREATE INDEX idx_quiz_options_question_id ON quiz_options(question_id);
CREATE INDEX idx_quiz_options_order ON quiz_options(question_id, order_index);

-- ============================================================================
-- QUIZ_ANSWERS TABLE
-- ============================================================================
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

-- ============================================================================
-- SAMPLE DATA - CATEGORIES
-- ============================================================================
INSERT INTO categories (name, description, slug, plan_type, display_order, parent_id) VALUES
-- Geral (FREE)
('Geral', 'Termos gerais e de uso comum', 'geral', 'FREE', 1, NULL),
('Negócios', 'Termos de negócios e gestão', 'negócios', 'FREE', 2, 1),
('Comunicação', 'Termos de comunicação profissional', 'comunicacao', 'FREE', 3, 1),

-- Tecnologia (FREE main, PREMIUM subcategories)
('Tecnologia', 'Vocabulário de Tecnologia da Informação', 'tecnologia', 'FREE', 0, NULL),
('Engenharia de Software', 'Termos de Engenharia de Software', 'engenharia-software', 'FREE', 1, 4),

-- Premium Tech subcategories
('Backend', 'Desenvolvimento Backend', 'backend', 'PREMIUM', 1, 5),
('Frontend', 'Desenvolvimento Frontend', 'frontend', 'PREMIUM', 2, 5),
('Arquitetura', 'Arquitetura de Software', 'arquitetura', 'PREMIUM', 3, 5),
('Cloud', 'Computação em Nuvem', 'cloud', 'PREMIUM', 4, 4),
('DevOps', 'DevOps e Infrastructure', 'devops', 'PREMIUM', 5, 4),
('Segurança', 'Segurança da Informação', 'seguranca', 'PREMIUM', 6, 4);

-- ============================================================================
-- SAMPLE DATA - TERMS (FREE TIER - Geral)
-- ============================================================================
INSERT INTO terms (name, definition, simple_explanation, example, difficulty_level, category_id, plan_type) VALUES
-- Negócios (FREE)
('ROI', 'Return on Investment - Retorno sobre o Investimento',
 'Métrica que mede o quanto de lucro você obtém em relação ao dinheiro investido',
 'Se você investe R$ 1.000 e ganha R$ 1.500 em vendas, seu ROI é de 50%',
 'INTERMEDIATE', 2, 'FREE'),

('MVP', 'Minimum Viable Product - Produto Mínimo Viável',
 'A versão mais simples de um produto que permite aprender dos usuários antes de desenvolver mais',
 'Uma aplicação com apenas login e exibição de termos é um MVP de plataforma educacional',
 'INTERMEDIATE', 2, 'FREE'),

('Stakeholder', 'Partes interessadas em um projeto ou empresa',
 'Qualquer pessoa ou grupo que é afetado pelas decisões ou ações de um projeto',
 'Clientes, funcionários, investidores e fornecedores são stakeholders de uma empresa',
 'BEGINNER', 2, 'FREE'),

-- Comunicação (FREE)
('Briefing', 'Reunião ou documento com informações essenciais sobre um projeto',
 'Um resumo rápido das principais informações que precisam ser conhecidas',
 'Antes de começar o design, faz-se um briefing com o cliente para entender suas necessidades',
 'BEGINNER', 3, 'FREE'),

('Deadline', 'Prazo final para conclusão de uma tarefa',
 'A data/hora máxima até a qual algo precisa estar pronto',
 'O deadline do projeto é 31 de dezembro e não pode ser prorrogado',
 'BEGINNER', 3, 'FREE');

-- ============================================================================
-- SAMPLE DATA - TERMS (FREE TIER - Engenharia de Software)
-- ============================================================================
INSERT INTO terms (name, definition, simple_explanation, example, difficulty_level, category_id, plan_type) VALUES
-- Engenharia de Software (FREE)
('API', 'Application Programming Interface - Interface de Programação de Aplicações',
 'Um conjunto de regras que permite que dois programas se comuniquem entre si',
 'Uma API do Google Maps permite que sites mostrem mapas. Um app se conecta à API de pagamento para processar transações',
 'BEGINNER', 5, 'FREE'),

('REST', 'Representational State Transfer',
 'Um estilo de arquitetura para criar APIs usando os verbos HTTP (GET, POST, PUT, DELETE)',
 'GET /users para obter usuários, POST /users para criar, PUT /users/1 para atualizar',
 'INTERMEDIATE', 5, 'FREE'),

('Banco de Dados', 'Sistema organizado para armazenar e recuperar dados',
 'Um local onde as informações de um aplicativo são guardadas de forma organizada',
 'Um banco de dados de usuários armazena nome, email e senha de cada pessoa cadastrada',
 'BEGINNER', 5, 'FREE'),

('Variável', 'Um espaço na memória que armazena um valor',
 'Um recipiente que guarda um dado que pode ser lido ou modificado durante a execução',
 'const nome = "João"; a variável nome armazena o texto "João"',
 'BEGINNER', 5, 'FREE'),

('Debug', 'Processo de encontrar e corrigir erros no código',
 'Usar ferramentas para rastrear o que está acontecendo e identificar problemas',
 'Usar breakpoints no VS Code para parar a execução e ver o valor das variáveis',
 'INTERMEDIATE', 5, 'FREE');

-- ============================================================================
-- SAMPLE DATA - TERMS (PREMIUM TIER - Backend)
-- ============================================================================
INSERT INTO terms (name, definition, simple_explanation, example, difficulty_level, category_id, plan_type) VALUES
-- Backend (PREMIUM)
('Idempotência', 'Propriedade de uma operação que produz o mesmo resultado se executada uma ou múltiplas vezes',
 'Não importa quantas vezes você faz a operação, o resultado final é sempre o mesmo',
 'Uma requisição PUT para atualizar um usuário é idempotente: chamar 10 vezes com os mesmos dados resulta no mesmo estado',
 'INTERMEDIATE', 6, 'PREMIUM'),

('Circuit Breaker', 'Padrão que interrompe chamadas para um serviço que está falhando para evitar cascata de erros',
 'Um mecanismo que detecta quando um serviço está com problema e deixa de enviar requisições para ele',
 'Se uma API externa está indisponível, o Circuit Breaker retorna um erro imediatamente em vez de tentar 20 vezes',
 'INTERMEDIATE', 6, 'PREMIUM'),

('Microserviços', 'Arquitetura onde a aplicação é dividida em pequenos serviços independentes',
 'Em vez de um único programa gigante, você tem vários programas pequenos que conversam entre si',
 'Um serviço para usuários, outro para pedidos, outro para pagamentos - todos independentes e escaláveis',
 'ADVANCED', 6, 'PREMIUM'),

('Cache', 'Armazenamento temporário de dados frequentemente acessados para melhorar performance',
 'Guardar uma informação que demora para ser calculada, para não precisar calcular novamente',
 'Cache de resultados de uma query lenta: ao invés de buscar do banco, busca da memória (muito mais rápido)',
 'INTERMEDIATE', 6, 'PREMIUM'),

('Rate Limiting', 'Técnica para controlar quantas requisições um cliente pode fazer em um período',
 'Limitar o número de vezes que alguém pode fazer algo para evitar abuso',
 'Uma API permite apenas 100 requisições por minuto. Se ultrapassar, retorna erro 429',
 'INTERMEDIATE', 6, 'PREMIUM');

-- ============================================================================
-- SAMPLE DATA - TERMS (PREMIUM TIER - Arquitetura)
-- ============================================================================
INSERT INTO terms (name, definition, simple_explanation, example, difficulty_level, category_id, plan_type) VALUES
-- Arquitetura (PREMIUM)
('CQRS', 'Command Query Responsibility Segregation',
 'Separar a lógica de escrita (commands) da lógica de leitura (queries)',
 'Uma tabela para registrar todas as transações e outra para mostrar o saldo atual (separadas)',
 'ADVANCED', 7, 'PREMIUM'),

('Event Sourcing', 'Padrão de persistência onde o estado da aplicação é determinado por uma sequência de eventos',
 'Guardar tudo que aconteceu (eventos) e reconstruir o estado atual a partir desses eventos',
 'Em vez de guardar apenas "saldo = R$ 500", guardar todos os eventos: "depósito R$ 1000", "saque R$ 500"',
 'ADVANCED', 7, 'PREMIUM'),

('DDD', 'Domain-Driven Design - Abordagem de design focada no domínio do negócio',
 'Estruturar o código ao redor dos conceitos do negócio, não da tecnologia',
 'Em um e-commerce, as classes refletem conceitos do domínio: Pedido, Cliente, Produto',
 'ADVANCED', 7, 'PREMIUM'),

('Saga', 'Padrão para gerenciar transações distribuídas entre múltiplos serviços',
 'Uma sequência coordenada de passos que devem ser executados em diferentes serviços',
 'Um pedido: 1) Reservar estoque, 2) Processar pagamento, 3) Enviar notificação - tudo orquestrado',
 'ADVANCED', 7, 'PREMIUM');

-- ============================================================================
-- Fin do Script
-- ============================================================================
-- Tabelas criadas com sucesso!
-- Para conectar à aplicação Spring Boot, use:
-- spring.datasource.url=jdbc:postgresql://localhost:5432/lexicareer_db
-- spring.datasource.username=postgres
-- spring.datasource.password=your_password
