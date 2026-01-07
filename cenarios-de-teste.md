<<<<<<< HEAD
# Planejamento de Cenários de Teste

## Parte B - Testes de UI (Frontend)

1.  **CT-UI-01 (Caminho Feliz):**
    *   **Descrição:** Login com usuário e senha válidos do perfil "USER".
    *   **Pré-condição:** Usuário `user_valido` existe no banco e não está bloqueado.
    *   **Passos:** Navegar para `/login`, inserir credenciais de `user_valido`, clicar em "Entrar".
    *   **Resultado Esperado:** Redirecionamento para a página `/dashboard`.

2.  **CT-UI-02 (Acesso Negado):**
    *   **Descrição:** Tentativa de login com usuário do perfil "VISITOR".
    *   **Pré-condição:** Usuário `user_visitor` existe no banco.
    *   **Passos:** Navegar para `/login`, inserir credenciais de `user_visitor`, clicar em "Entrar".
    *   **Resultado Esperado:** Mensagem "Acesso Negado" é exibida na tela. Não há redirecionamento.

3.  **CT-UI-03 (Bloqueio de Usuário):**
    *   **Descrição:** Um usuário do perfil "USER" é bloqueado após 3 tentativas de login com senha incorreta.
    *   **Pré-condição:** Usuário `user_to_be_blocked` existe no banco e não está bloqueado.
    *   **Passos:**
        1.  Navegar para `/login`, inserir usuário `user_to_be_blocked` e senha errada, clicar em "Entrar".
        2.  Repetir o passo 1 mais duas vezes (total de 3 falhas).
        3.  Tentar fazer login uma quarta vez com qualquer senha.
    *   **Resultado Esperado:** Mensagem "Usuário Bloqueado" é exibida na tela.

## Parte C - Testes de API (Backend)

4.  **CT-API-01 (Status 200 - Sucesso):**
    *   **Endpoint:** `POST /api/login`
    *   **Payload:** `{ "username": "user_valido", "password": "123456" }`
    *   **Resultado Esperado:** Status Code `200 OK` e um corpo de resposta JSON contendo um `token`.

5.  **CT-API-02 (Status 401 - Não Autorizado):**
    *   **Endpoint:** `POST /api/login`
    *   **Payload:** `{ "username": "user_valido", "password": "senha_errada" }`
    *   **Resultado Esperado:** Status Code `401 Unauthorized`.

6.  **CT-API-03 (Status 403 - Proibido):**
    *   **Endpoint:** `POST /api/login`
    *   **Payload:** `{ "username": "user_visitor", "password": "123456" }`
    *   **Resultado Esperado:** Status Code `403 Forbidden`.

7.  **CT-API-04 (Status 423 - Bloqueado):**
    *   **Endpoint:** `POST /api/login`
    *   **Payload:** `{ "username": "user_blocked", "password": "123456" }`
    *   **Resultado Esperado:** Status Code `423 Locked`.

8.  **CT-API-05 (Bloqueio Progressivo):**
    *   **Descrição:** Simula o bloqueio via API.
    *   **Endpoint:** `POST /api/login`
    *   **Passos:**
        1.  Enviar 3x o payload `{ "username": "user_to_be_blocked", "password": "senha_errada" }`. O resultado esperado é `401`.
        2.  Enviar uma 4ª vez.
    *   **Resultado Esperado:** Status Code `423 Locked` na quarta chamada.
=======
# Prova Técnica – Analista de Automação de Testes
**Stack:** Java, Selenium, RestAssured, PostgreSQL

Este documento contém as respostas das partes teóricas, o planejamento de testes,
os cenários funcionais e as análises SQL solicitadas no desafio técnico.

> Observação: o enunciado do desafio não forneceu um sistema real, URLs ou credenciais.
> A automação será utilizada para demonstrar estrutura, estratégia e boas práticas,
> conforme solicitado.

---

## Parte 0 – Teoria e Conceitos Fundamentais

### 1. Diferença entre teste de unidade, integração e E2E, e prioridade no CI/CD

**Teste de unidade**
valida a menor unidade de código (funções ou métodos) de forma isolada,
normalmente com uso de mocks ou stubs para dependências externas como banco de dados,
serviços ou rede. São rápidos, baratos e fornecem feedback imediato.

**Teste de integração** 
valida a comunicação entre componentes ou camadas do sistema,
como API e banco de dados, verificando contratos, fluxo de dados e regras de negócio
quando os módulos interagem entre si.

**Teste de ponta a ponta (E2E)**
valida o fluxo completo do usuário final,
incluindo interface, backend, banco de dados e integrações externas,
simulando o comportamento real do sistema em um ambiente próximo ao de produção.

**Prioridade no pipeline CI/CD:**
deve-se priorizar testes de unidade,
seguido por testes de integração e, por último, testes E2E.
Essa estratégia segue a pirâmide de testes, pois testes unitários são mais rápidos
e estáveis, enquanto testes E2E são mais custosos e sujeitos a flakiness.



### 2. Vantagens da automação de testes e riscos quando mal implementada

A automação de testes traz como principais vantagens a execução rápida e repetível
dos testes, maior cobertura de cenários regressivos, redução de erros humanos
e feedback contínuo em pipelines de CI/CD, contribuindo para entregas mais seguras
e frequentes.

Além disso, a automação permite reutilização de código de teste, padronização
dos cenários e melhor rastreabilidade dos resultados ao longo do tempo.

Por outro lado, quando mal implementada, a automação pode gerar riscos como
testes frágeis (flaky), alto custo de manutenção, falsos positivos,
dependência excessiva da UI e baixa confiabilidade dos resultados,
impactando negativamente a produtividade do time.


### 3. Estratégia de testes em uma arquitetura de microserviços

Em uma arquitetura de microserviços, a estratégia de testes deve priorizar a
validação isolada de cada serviço, garantindo que regras de negócio, contratos
e integrações sejam testados de forma independente.

Testes de unidade e testes de integração de API devem ser priorizados,
incluindo validação de contratos (contract testing), para assegurar que
as comunicações entre serviços permaneçam compatíveis ao longo do tempo.

Testes E2E devem existir, porém em menor quantidade, focando nos fluxos
críticos do negócio, pois em ambientes distribuídos esses testes tendem
a ser mais lentos, complexos e suscetíveis a instabilidades.


### 4. Como garantir idempotência e consistência em testes automatizados

Para garantir **idempotência**, os testes devem poder ser executados repetidas vezes
sem causar efeitos colaterais acumulativos (ex.: duplicar registros, alterar estado
de forma permanente). Para isso, é importante:

- Criar dados de teste com identificadores únicos (ex.: prefixo/sufixo com timestamp).
- Usar seeds idempotentes (ex.: UPSERT / `ON CONFLICT DO NOTHING`) quando aplicável.
- Evitar dependência de ordem de execução entre testes.

Para garantir **consistência**, os testes devem controlar o estado do ambiente antes
e depois da execução, por exemplo:

- Preparar o estado inicial (setup) e limpar os dados criados (teardown).
- Executar transações com rollback (quando possível) ou usar banco efêmero/isolado.
- Em CI, preferir ambientes isolados por pipeline (ex.: containers) para reduzir flakiness.


### 5. Diferença entre mock, stub e spy

**Mock** é um objeto simulado utilizado para controlar e verificar comportamentos
esperados durante o teste, como chamadas de métodos, quantidade de execuções
ou parâmetros recebidos. É muito usado para validar interações.

**Stub** é um objeto simples que retorna respostas pré-definidas,
sem lógica complexa ou validação de comportamento. É utilizado quando
o foco do teste não está na dependência externa, mas apenas no valor retornado.

**Spy** é um objeto que envolve uma implementação real, permitindo observar
e, se necessário, sobrescrever parcialmente o comportamento,
sendo útil quando se deseja validar chamadas mantendo parte da lógica original.


### 6. Boas práticas para manutenção de testes automatizados em longo prazo

Para garantir a manutenção e evolução saudável da automação de testes ao longo
do tempo, é fundamental aplicar boas práticas como:

- Utilizar padrões de projeto como Page Object Model para reduzir acoplamento.
- Manter testes pequenos, independentes e com responsabilidade única.
- Evitar dependência excessiva da interface gráfica, priorizando testes de API.
- Centralizar dados de teste e configurações externas.
- Garantir que testes sejam legíveis, com nomes claros e asserts objetivos.
- Revisar e refatorar testes continuamente, assim como código de produção.

Essas práticas reduzem flakiness, facilitam manutenção e aumentam a confiabilidade
da suíte de testes.



---



# Parte A – Análise e Planejamento de Testes

## Contexto
Os cenários abaixo consideram um sistema de autenticação com os perfis:
-> ADMIN
-> USER
-> VISITOR

Como o desafio não fornece URLs, credenciais ou sistema real,
os cenários foram elaborados de forma conceitual, com foco em cobertura,
estratégia e boas práticas de QA.

---

## Cenário 1 – Login válido com usuário comum (USER)

**Objetivo**  
Validar que um usuário com credenciais válidas consegue realizar login com sucesso.

**Tipo de teste**  
Funcional / Positivo

**Camada**  
UI e API

**Pré-condições**  
- Usuário do tipo USER previamente cadastrado
- Sistema disponível

**Passos**
1. Acessar a tela de login
2. Informar e-mail válido
3. Informar senha válida
4. Clicar no botão “Entrar”

**Resultado esperado (BDD)**  
- **Dado que** o usuário foi autenticado com sucesso  
- **E** é redirecionado para a página inicial (Home/Dashboard)  
- **Então** a sessão/tokens de autenticação são válidos

**Observações**
Este cenário pode ser automatizado prioritariamente via API (RestAssured)
e complementado via UI (Selenium) para validar o fluxo completo.


---

## Cenário 2 – Tentativa de login com senha inválida

**Objetivo**  
Validar que o sistema bloqueia o acesso quando uma senha inválida é informada.

**Tipo de teste**  
Funcional / Negativo

**Camada**  
UI e API

**Pré-condições**  
- Usuário do tipo USER previamente cadastrado
- Sistema disponível

**Passos**
1. Acessar a tela de login
2. Informar e-mail válido
3. Informar senha inválida
4. Clicar no botão “Entrar”

**Resultado esperado (BDD)**
- **Dado que** a senha informada é inválida  
- **Então** o usuário não deve ser autenticado  
- **E** uma mensagem de erro clara deve ser exibida  
- **E** nenhuma sessão/token deve ser gerado

**Observações**
Este cenário é essencial para validação de segurança
e deve garantir que informações sensíveis não sejam expostas.


---

## Cenário 3 – Bloqueio de login após múltiplas tentativas inválidas

**Objetivo**  
Validar que o sistema protege contra ataques de força bruta,
bloqueando tentativas excessivas de login com credenciais inválidas.

**Tipo de teste**  
Segurança / Negativo

**Camada**  
API (prioritário) e UI

**Pré-condições**  
- Usuário do tipo USER previamente cadastrado
- Sistema disponível
- Política de segurança configurada (ex: 5 tentativas inválidas)

**Passos**
1. Acessar a tela de login
2. Informar e-mail válido
3. Informar senha inválida repetidamente até exceder o limite permitido
4. Tentar realizar login novamente

**Resultado esperado (BDD)**
- **Dado que** o número máximo de tentativas inválidas foi excedido  
- **Então** o usuário deve ser temporariamente bloqueado  
- **E** uma mensagem informativa deve ser exibida  
- **E** nenhuma autenticação deve ser permitida durante o período de bloqueio

**Observações**
Este cenário é crítico para segurança e deve validar também
logs de auditoria e políticas de rate limiting quando aplicável.


---

## Cenário 4 – Login válido com usuário ADMIN

**Objetivo**  
Validar que um usuário com perfil ADMIN consegue realizar login
e acessar funcionalidades administrativas.

**Tipo de teste**  
Funcional / Positivo

**Camada**  
UI e API

**Pré-condições**  
- Usuário com perfil ADMIN previamente cadastrado
- Sistema disponível

**Passos**
1. Acessar a tela de login
2. Informar e-mail válido de um usuário ADMIN
3. Informar senha válida
4. Clicar no botão “Entrar”

**Resultado esperado (BDD)**
- **Dado que** o usuário ADMIN foi autenticado com sucesso  
- **Então** deve ser redirecionado para a área administrativa  
- **E** permissões elevadas devem estar disponíveis

**Observações**
Este cenário valida controle de acesso baseado em perfil (RBAC).



---

## Cenário 5 – Acesso como VISITOR sem autenticação

**Objetivo**  
Validar que usuários não autenticados (VISITOR) possuem acesso restrito
e são impedidos de acessar áreas protegidas do sistema.

**Tipo de teste**  
Funcional / Segurança

**Camada**  
UI e API

**Pré-condições**  
- Usuário não autenticado
- Sistema disponível

**Passos**
1. Acessar diretamente uma URL protegida
2. Tentar navegar por funcionalidades restritas

**Resultado esperado (BDD)**
- **Dado que** o usuário não está autenticado  
- **Então** o acesso às áreas protegidas deve ser negado  
- **E** o usuário deve ser redirecionado para a tela de login  
- **E** nenhuma informação sensível deve ser exibida

**Observações**
Este cenário valida controle de acesso e proteção de rotas sensíveis.



---

## Cenário 6 – Performance do login com tempo de resposta aceitável

**Objetivo**  
Validar que o processo de autenticação responde dentro do tempo esperado,
garantindo boa experiência ao usuário.

**Tipo de teste**  
Não funcional / Performance

**Camada**  
API (prioritário)

**Pré-condições**  
- Usuário do tipo USER previamente cadastrado
- Sistema disponível

**Passos**
1. Realizar requisição de login com credenciais válidas
2. Medir o tempo de resposta da autenticação

**Resultado esperado (BDD)**
- **Dado que** a requisição de login é válida  
- **Então** o tempo de resposta deve ser inferior a 5 segundos  
- **E** o login deve ser concluído com sucesso

**Observações**
Este cenário pode ser automatizado com RestAssured,
incluindo assert de tempo de resposta.



---

## Cenário 7 – Expiração de sessão do usuário

**Objetivo**  
Validar que o sistema encerra corretamente a sessão do usuário
após o tempo configurado de inatividade.

**Tipo de teste**  
Funcional / Segurança

**Camada**  
API e UI

**Pré-condições**  
- Usuário autenticado com sessão ativa
- Política de expiração de sessão configurada

**Passos**
1. Realizar login com credenciais válidas
2. Permanecer inativo até ultrapassar o tempo limite de sessão
3. Tentar acessar uma funcionalidade protegida

**Resultado esperado (BDD)**
- **Dado que** a sessão do usuário expirou  
- **Então** o acesso às funcionalidades protegidas deve ser negado  
- **E** o usuário deve ser redirecionado para a tela de login  
- **E** uma nova autenticação deve ser exigida

**Observações**
Este cenário é importante para segurança e prevenção
de uso indevido de sessões abandonadas.



---

## Cenário 8 – Logout do sistema

**Objetivo**  
Validar que o usuário consegue encerrar a sessão manualmente
e que o sistema invalida corretamente a autenticação.

**Tipo de teste**  
Funcional / Positivo

**Camada**  
UI e API

**Pré-condições**  
- Usuário autenticado com sessão ativa
- Sistema disponível

**Passos**
1. Acessar o sistema com usuário autenticado
2. Clicar na opção “Logout”
3. Tentar acessar uma funcionalidade protegida

**Resultado esperado (BDD)**
- **Dado que** o usuário realizou logout com sucesso  
- **Então** a sessão/token deve ser invalidado  
- **E** o acesso às áreas protegidas deve ser bloqueado  
- **E** o usuário deve ser redirecionado para a tela de login

**Observações**
Este cenário garante o correto encerramento da sessão
e evita reutilização indevida de credenciais.

---

## Parte D – Estratégia de Esperas e Sincronização

### Objetivo
Definir a estratégia utilizada para lidar com comportamentos assíncronos
em testes automatizados, garantindo estabilidade, confiabilidade
e redução de flakiness.

---

### Estratégia adotada

Em aplicações modernas, ações do usuário dependem frequentemente de
requisições assíncronas, carregamento dinâmico de dados
e renderização progressiva da interface.

Para lidar com esse cenário, a estratégia adotada prioriza
o uso de **esperas explícitas**, evitando esperas fixas
e não determinísticas.

---

### Esperas em testes de UI (Selenium)

Nos testes de interface, devem ser utilizadas **esperas explícitas**
(`WebDriverWait`) associadas a condições específicas, como:

- Elemento visível
- Elemento clicável
- Texto esperado presente
- URL alterada após navegação

Essa abordagem garante que o teste aguarde apenas o tempo necessário,
tornando a execução mais rápida e menos suscetível a falhas intermitentes.

> O uso de `Thread.sleep` deve ser evitado, pois torna os testes frágeis
> e aumenta o tempo de execução.

---

### Esperas em testes de API

Em testes de API, a sincronização está relacionada ao
tempo de resposta e à consistência dos dados.

A estratégia inclui:
- Assert de tempo máximo de resposta (ex: ≤ 5 segundos)
- Validação de status code e payload
- Polling controlado quando operações assíncronas forem utilizadas

---

### Integração entre UI e API

Quando há dependência entre UI e API, a validação deve priorizar:
- Verificação de estado via API antes da UI
- Evitar dependência exclusiva de loaders visuais
- Sincronização baseada em estado, não em tempo fixo

---

### Conclusão

O uso de esperas explícitas e sincronização baseada em estado
é essencial para garantir testes automatizados estáveis,
performáticos e confiáveis em pipelines de CI/CD.

---

## Parte E – SQL

### Objetivo
Responder às questões de SQL propostas no desafio,
demonstrando conhecimento em consultas, análise de dados
e diagnóstico de problemas.

---

### 1. Consulta para listar usuários ativos

```sql
SELECT *
FROM usuarios
WHERE ativo = true;

SELECT email, COUNT(*) AS quantidade
FROM usuarios
GROUP BY email
HAVING COUNT(*) > 1;
Explicação
Identifica e-mails duplicados utilizando agregação (GROUP BY)
e filtro com HAVING.



SELECT p.id,
       p.usuario_id,
       SUM(i.valor) AS valor_total
FROM pedidos p
JOIN itens_pedido i ON i.pedido_id = p.id
GROUP BY p.id, p.usuario_id
HAVING SUM(i.valor) > 500;

Explicação
Soma o valor dos itens por pedido e retorna apenas
aqueles cujo total ultrapassa R$ 500.

4. Diagnóstico de lentidão em consultas SQL

Para diagnosticar lentidão em consultas SQL, é importante analisar:

Uso adequado de índices

Consultas com JOIN sem chaves indexadas

Uso excessivo de SELECT *

Falta de filtros (WHERE)

Alto volume de dados sem paginação

Ferramentas como EXPLAIN e EXPLAIN ANALYZE
auxiliam na identificação de gargalos de performance.

5. Estratégias para garantir integridade dos dados

Boas práticas para garantir integridade incluem:

Uso de chaves primárias e estrangeiras

Constraints como NOT NULL e UNIQUE

Uso de transações (BEGIN, COMMIT, ROLLBACK)

Validação de dados antes da persistência






>>>>>>> 278dea49c6e201ac7d690bf6e6ddb814be5d2487
