-- =============================================================================
-- BLOCO 2: Massa de Dados (SQL para PostgreSQL)
--
-- Este script cria a tabela 'usuarios' e insere os registros necessários
-- para a execução dos testes de UI e API.
-- =============================================================================

-- Cria a tabela de usuários
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    blocked BOOLEAN DEFAULT FALSE
);

-- Insere os usuários para os cenários de teste
INSERT INTO usuarios (username, password, role, blocked) VALUES
('user_valido', '123456', 'USER', FALSE),      -- Para teste de login com sucesso
('user_visitor', '123456', 'VISITOR', FALSE),  -- Para teste de acesso negado (403)
('user_blocked', '123456', 'USER', TRUE);      -- Para teste de usuário bloqueado (423)

-- Adiciona um usuário para o teste de 3 tentativas falhas
INSERT INTO usuarios (username, password, role, blocked) VALUES
('user_to_be_blocked', '123456', 'USER', FALSE);
