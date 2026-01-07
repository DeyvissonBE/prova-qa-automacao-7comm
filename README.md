<<<<<<< HEAD
# Prova Técnica de Automação de Testes - Solução Completa

Bem-vindo à solução da prova técnica de automação. Este projeto contém uma suíte de testes completa para UI (Selenium) e API (RestAssured), projetada para ser **totalmente portátil e autocontida**.

Este guia foi escrito pensando em um QA Júnior, detalhando não apenas "o quê" fazer, mas "porquê" cada passo é necessário.

---

## 🏛️ Arquitetura da Solução: Como Tudo Funciona?

Para atender aos requisitos da prova, especialmente o de não instalar nada globalmente, adotamos uma arquitetura inteligente.

### O Problema: Como testar uma aplicação que não existe?
A prova nos pediu para criar testes para um sistema de login, mas não nos forneceu a aplicação em si. Se simplesmente rodássemos os testes, eles falhariam com o erro "Connection Refused", pois não haveria nada rodando em `localhost:8080` para eles se conectarem.

### A Solução: Um Servidor "de Mentira" (Mock Server Embutido)
Para resolver isso, nós criamos a nossa própria aplicação "fake" dentro do projeto de teste.

1.  **Frontend e Backend Simulados:**
    *   Existe uma classe chamada [`StubApp.java`](src/test/java/utils/StubApp.java:1). Pense nela como um **mini-servidor web**.
    *   Quando o teste de UI (Selenium) tenta acessar a página de login, este servidor devolve um HTML simples com os campos de usuário, senha e o botão.
    *   Quando o teste de API (RestAssured) ou o próprio frontend envia os dados de login, este mesmo servidor responde com o JSON apropriado (sucesso, usuário bloqueado, acesso negado, etc.), simulando perfeitamente a lógica de negócio exigida na prova.

2.  **A Mágica da Automação: Tudo Roda Sozinho!**
    *   **Você não precisa iniciar este servidor manualmente.**
    *   Graças à configuração na classe [`BaseTest.java`](src/test/java/com/example/automation/BaseTest.java:1), o servidor `StubApp` é **iniciado automaticamente** antes do primeiro teste começar e é **desligado automaticamente** após o último teste terminar.
    *   Isso garante que, quando os testes forem executados, a aplicação (de mentira) estará no ar para respondê-los.

3.  **Ambiente Portátil e Banco de Dados com Docker:**
    *   Para completar o isolamento, não instalamos Java ou Maven no sistema. O script `setup_portable.ps1` baixa tudo o que é preciso para uma pasta local `tools/`.
    *   O banco de dados PostgreSQL roda em um contêiner **Docker**, garantindo um ambiente limpo e consistente a cada execução.

**Resumo:** O projeto foi desenhado para que você só precise se preocupar com um único comando: o de rodar os testes. Todo o resto (subir servidor, configurar ambiente) é feito de forma automática.

---

## 🗺️ Estrutura de Pastas e Arquivos

```
/
├── pom.xml                     # Coração do projeto Maven. Define dependências e a versão do Java (17).
├── setup_portable.ps1          # Script nº 1: Baixa e extrai o JDK e o Maven na pasta /tools.
├── activate.ps1                # Script nº 2: Ativa o ambiente portátil para usar java e mvn no terminal.
├── docker_run.txt              # Contém o comando Docker para iniciar o banco de dados.
├── init.sql                    # Script SQL com a massa de dados para os testes.
├── cenarios-de-teste.md        # Documentação do planejamento dos testes.
├── respostas-sql.md            # Análise e correção das queries SQL.
└── src/test/
    ├── java/com/example/automation/
    │   ├── BaseTest.java       # Classe pai dos testes. Inicia/para o Mock Server e o WebDriver.
    │   ├── LoginAPITest.java   # Testes de API com RestAssured.
    │   ├── LoginPage.java      # Page Object da tela de login.
    │   ├── LoginUITest.java    # Testes de UI com Selenium.
    │   └── utils/
    │       └── StubApp.java    # O Mock Server que simula o frontend e o backend.
    └── resources/
        └── config.properties   # Arquivo de configuração para URLs e credenciais.
```

---

## 🚀 Guia de Execução Para o Avaliador (e para Você)

Siga estes 3 passos na ordem correta. Os comandos devem ser copiados e colados em um terminal **PowerShell**.

### Pré-requisito
-   **Docker Desktop:** Garanta que ele esteja instalado e em execução na sua máquina.

### Passo 1: Preparar o Ambiente Portátil
Este comando só precisa ser executado **uma vez**. Ele fará o download do JDK e do Maven (pode demorar alguns minutos) e os salvará na pasta `tools/`.

```powershell
# Navegue até a pasta raiz do projeto e execute:
.\setup_portable.ps1
```

### Passo 2: Iniciar o Banco de Dados
Este comando usará o Docker para iniciar um contêiner PostgreSQL em segundo plano e popular o banco com os dados do `init.sql`.

```powershell
# No mesmo terminal, execute:
docker run --rm --name pg_prova_tecnica -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=aut_prova -p 5432:5432 -v "$pwd\init.sql:/docker-entrypoint-initdb.d/init.sql" -d postgres:13
```
> **Dica:** O comando usa `--rm` para que o contêiner seja automaticamente removido ao ser parado, facilitando execuções futuras. Se quiser que ele persista, basta remover `--rm`.

### Passo 3: Rodar a Suíte de Testes (Frontend, Backend e Validações ou um de cada vez) 
Este é o **único comando que você precisa para rodar tudo**. Ele ativa o ambiente, sobe o Mock Server, executa todos os testes e desliga o servidor no final.

```powershell 
# No mesmo terminal, execute:
powershell.exe -ExecutionPolicy Bypass -File .\activate.ps1 -CommandToRun "mvn test"

# Caso queira rodar back e o front separadamente segue comandos para tal ação desejada
Front: powershell.exe -ExecutionPolicy Bypass -File .\activate.ps1 -CommandToRun "mvn -Dtest=LoginUITest test"

Back: powershell.exe -ExecutionPolicy Bypass -File .\activate.ps1 -CommandToRun "mvn -Dtest=LoginAPITest test" 
```

Ao final da execução, você deverá ver a mensagem **`[INFO] BUILD SUCCESS`** no seu terminal, indicando que todos os 7 testes passaram ao rodar o teste em conjunto do front e back. Os relatórios detalhados podem ser encontrados na pasta `target/surefire-reports/`.
=======
# prova-qa-automacao-7comm
>>>>>>> 278dea49c6e201ac7d690bf6e6ddb814be5d2487
