-- ============================================================================
-- População de Dados Oficiais - LexiCareer Pro
-- ============================================================================

-- 1. Garante que exista o Usuário de Teste (necessário para a chave estrangeira created_by)
-- Senha: password123
INSERT INTO users (id, email, password, name, profession, level, plan, role, is_active, active)
VALUES (1, 'anderson@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Anderson', 'Desenvolvedor', 'INTERMEDIATE', 'FREE', 'ADMIN', true, true)
    ON CONFLICT (email) DO NOTHING;

-- 2. Inserir Categorias
INSERT INTO categories (name, description, slug, plan_type, display_order, is_active, active, created_at, updated_at)
VALUES
    ('Geral', 'Termos gerais e fundamentais', 'geral', 'FREE', 1, true, true, NOW(), NOW()),
    ('Tecnologia', 'Termos de tecnologia e desenvolvimento', 'tecnologia', 'FREE', 2, true, true, NOW(), NOW()),
    ('Negócios', 'Termos de negócios e gestão', 'negocios', 'PREMIUM', 3, true, true, NOW(), NOW()),
    ('Engenharia de Software', 'Termos de engenharia de software', 'engenharia-software', 'FREE', 4, true, true, NOW(), NOW()),
    ('DevOps', 'Termos de DevOps e infraestrutura', 'devops', 'PREMIUM', 5, true, true, NOW(), NOW())
ON CONFLICT (slug) DO NOTHING;

-- 3. Inserir Termos
INSERT INTO terms (name, definition, simple_explanation, example, difficulty_level, category_id, plan_type, created_by, is_active, active, created_at, updated_at)
VALUES
    -- Categoria Geral (para usuários sem interesses configurados)
    ('Profissional', 'Pessoa que trabalha em uma área específica', 'Alguém que tem um trabalho e é pago por isso', 'Um médico é um profissional da saúde', 'BEGINNER', (SELECT id FROM categories WHERE slug = 'geral'), 'FREE', 1, true, true, NOW(), NOW()),
    ('Carreira', 'Caminho profissional de uma pessoa', 'A trajetória de trabalho e evolução de alguém ao longo da vida', 'A carreira de um desenvolvedor pode evoluir de júnior a sênior', 'BEGINNER', (SELECT id FROM categories WHERE slug = 'geral'), 'FREE', 1, true, true, NOW(), NOW()),
    ('Networking', 'Construção de contatos profissionais', 'Conectar-se com pessoas da sua área para criar oportunidades', 'Participar de eventos e conferências para conhecer outros profissionais', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'geral'), 'FREE', 1, true, true, NOW(), NOW()),
    -- Categoria Tecnologia
    ('API', 'Application Programming Interface - Interface de programação de aplicação', 'Uma forma de dois programas se comunicarem entre si, usando regras e protocolos definidos', 'Quando você usa o Postman ou faz uma requisição curl para um servidor, está usando uma API', 'BEGINNER', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'FREE', 1, true, true, NOW(), NOW()),
    ('REST', 'Representational State Transfer - Padrão arquitetural para APIs', 'Um jeito padronizado de criar APIs usando HTTP com métodos GET, POST, PUT, DELETE', 'Uma API REST que retorna dados em JSON quando você faz GET em /api/users', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'FREE', 1, true, true, NOW(), NOW()),
    ('JWT', 'JSON Web Token - Token de autenticação baseado em JSON', 'Um token que contém informações do usuário e é usado para autenticar requisições sem guardar sessão no servidor', 'Após fazer login, o servidor retorna um JWT que você envia em todas as requisições para provar que é você', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'FREE', 1, true, true, NOW(), NOW()),
    ('Docker', 'Plataforma de containerização', 'Um jeito de empacotar sua aplicação com todas as dependências em uma caixa (container) para rodar em qualquer lugar', 'Em vez de instalar Node, Java e PostgreSQL na sua máquina, tudo vem pronto no container do Docker', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'devops'), 'PREMIUM', 1, true, true, NOW(), NOW()),
    ('CI/CD', 'Continuous Integration / Continuous Deployment', 'Automação de testes e deploy de código quando você faz push para o repositório', 'Ao fazer push no GitHub, testes rodam automaticamente e se passarem, o código é publicado em produção', 'ADVANCED', (SELECT id FROM categories WHERE slug = 'devops'), 'PREMIUM', 1, true, true, NOW(), NOW()),
    ('Database', 'Banco de dados - Sistema que armazena dados', 'Um local organizado onde você guarda informações (usuários, produtos, etc) para recuperar depois', 'Um banco PostgreSQL que armazena usuários, termos e suas respostas de quiz', 'BEGINNER', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'FREE', 1, true, true, NOW(), NOW()),
    ('Framework', 'Estrutura de desenvolvimento pré-construída', 'Uma base pronta com funcionalidades comuns para acelerar o desenvolvimento de aplicações', 'Spring Boot para Java, React para Frontend, Next.js para Node.js', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'FREE', 1, true, true, NOW(), NOW()),
    ('ORM', 'Object-Relational Mapping - Mapeamento de objetos para banco de dados', 'Uma tecnologia que converte dados do banco em objetos do seu código e vice-versa', 'Hibernate em Java, SQLAlchemy em Python - você trabalha com objetos em vez de SQL puro', 'ADVANCED', (SELECT id FROM categories WHERE slug = 'tecnologia'), 'PREMIUM', 1, true, true, NOW(), NOW()),
    ('Scalability', 'Escalabilidade - Capacidade de crescer sem perder performance', 'Quando sua aplicação consegue atender mais usuários sem ficar lenta', 'Um serviço que aguenta 100 usuários continua funcionando bem com 10.000 usuários', 'ADVANCED', (SELECT id FROM categories WHERE slug = 'engenharia-software'), 'PREMIUM', 1, true, true, NOW(), NOW()),
    ('Cache', 'Armazenamento temporário de dados', 'Um armazenamento rápido que mantém dados frequentemente acessados para evitar consultas ao banco', 'Redis guardando sessões de usuário para não ter que ir ao banco a cada requisição', 'INTERMEDIATE', (SELECT id FROM categories WHERE slug = 'engenharia-software'), 'FREE', 1, true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- 4. Vincular o Usuário 1 (Anderson) às categorias que inserimos como interesse
INSERT INTO user_interests (user_id, category_id, created_at)
VALUES
    (1, (SELECT id FROM categories WHERE slug = 'tecnologia'), NOW()),
    (1, (SELECT id FROM categories WHERE slug = 'engenharia-software'), NOW())
ON CONFLICT DO NOTHING;