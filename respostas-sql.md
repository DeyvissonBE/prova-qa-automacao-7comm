# Respostas para as Perguntas de SQL (Parte E - Análise de Código)

Esta seção aborda a análise e correção dos 5 trechos de código SQL apresentados na prova, com foco no dialeto PostgreSQL.

---

### 1. Sobre o Exemplo 1 (COUNT sem GROUP BY)

**Análise do Problema:**
A query `SELECT u.username, COUNT(p.id) FROM usuarios u JOIN pedidos p ON u.id = p.usuario_id;` é sintaticamente inválida na maioria dos bancos de dados SQL, incluindo o PostgreSQL. Ela mistura uma coluna de agregação (`COUNT`) com uma coluna não agregada (`u.username`) no `SELECT` sem agrupar os resultados. Isso gera o erro: `column "u.username" must appear in the GROUP BY clause or be used in an aggregate function`.

**Ação (Query Corrigida):**
Para contar corretamente o número de pedidos por usuário, é necessário adicionar a cláusula `GROUP BY`.

```sql
SELECT
    u.username,
    COUNT(p.id) AS total_pedidos
FROM
    usuarios u
JOIN
    pedidos p ON u.id = p.usuario_id
GROUP BY
    u.username;
```

---

### 2. Sobre o Exemplo 2 (Booleano como String)

**Análise do Problema:**
A query `WHERE bloqueado = 'false'` tenta comparar uma coluna do tipo `BOOLEAN` com uma `string`. Embora alguns bancos de dados possam fazer a conversão implícita, no PostgreSQL isso é propenso a erros e considerado má prática. O correto é usar os literais booleanos `true` ou `false`.

**Ação (Query Corrigida):**
A comparação deve ser feita diretamente com o valor booleano, sem aspas.

```sql
-- Forma 1 (Padrão e clara)
SELECT username FROM usuarios WHERE bloqueado = false;

-- Forma 2 (Alternativa comum)
SELECT username FROM usuarios WHERE NOT bloqueado;
```

---

### 3. Sobre o Exemplo 3 (Lógica de Bloqueio/CASE)

**Análise:**
O trecho `CASE WHEN tentativas_falhas >= 3 THEN true ELSE false END` demonstra a lógica de negócio para determinar o status de bloqueio de um usuário.

**Ação (Estratégia de Teste e Limpeza):**
-   **Cenário de Teste:** O teste de automação deve simular o comportamento de um usuário real. Ele deve:
    1.  Executar 3 tentativas de login com senha incorreta para um usuário específico.
    2.  Na quarta tentativa, verificar se a API retorna o status `423 Locked` ou se a UI exibe a mensagem "Usuário Bloqueado".
    3.  (Opcional) Fazer uma consulta `SELECT blocked FROM usuarios WHERE username = '...'` para validar se o estado foi persistido corretamente no banco de dados.
-   **Limpeza (Teardown):** Para garantir a idempotência dos testes, após a execução, um `UPDATE` deve ser executado para resetar o estado do usuário, garantindo que o teste possa ser rodado novamente.
    ```sql
    UPDATE usuarios SET tentativas_falhas = 0, bloqueado = false WHERE username = 'usuario_de_teste';
    ```

---

### 4. Sobre o Exemplo 4 (Sintaxe Oracle `(+)`)

**Análise do Problema:**
A sintaxe `a.usuario_id(+)` é uma forma proprietária e obsoleta de realizar um *Outer Join* no banco de dados Oracle. Esta sintaxe é completamente inválida no PostgreSQL e resultará em um erro de sintaxe imediato.

**Ação (Query Corrigida com Padrão ANSI):**
A query deve ser reescrita utilizando a sintaxe `LEFT JOIN` do padrão SQL ANSI, que é universalmente aceita e muito mais legível.

```sql
SELECT
    u.username,
    p.produto
FROM
    usuarios u
LEFT JOIN
    pedidos p ON u.id = p.usuario_id;
```
Isso retornará todos os usuários, mesmo aqueles que não têm pedidos.

---

### 5. Sobre o Exemplo 5 (Dados Órfãos)

**Análise:**
A query `SELECT * FROM pedidos WHERE usuario_id NOT IN (SELECT id FROM usuarios);` tem o objetivo de encontrar "registros órfãos": pedidos cujo `usuario_id` não corresponde a nenhum `id` na tabela de usuários. Isso indica um problema de **Integridade Referencial** no banco de dados.

**Ação (Causa e Correção no Banco):**
-   **Causa:** A causa mais provável é a ausência de uma *Foreign Key (Chave Estrangeira)* na coluna `pedidos.usuario_id` que aponte para `usuarios.id`, ou a existência de uma FK sem a regra de deleção em cascata.
-   **Correção no Banco:** A solução definitiva é aplicar a restrição de integridade referencial no momento da criação da tabela `pedidos`, utilizando `ON DELETE CASCADE`. Isso garante que, se um usuário for deletado, todos os seus pedidos associados também sejam deletados automaticamente, prevenindo a existência de registros órfãos.
    ```sql
    CREATE TABLE pedidos (
        id SERIAL PRIMARY KEY,
        usuario_id INT NOT NULL,
        produto VARCHAR(100),
        -- ... outras colunas ...
        CONSTRAINT fk_usuario
            FOREIGN KEY(usuario_id) 
            REFERENCES usuarios(id)
            ON DELETE CASCADE
    );