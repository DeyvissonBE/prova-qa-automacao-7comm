# =============================================================================
# BLOCO 1: Infraestrutura Portátil (PowerShell)
#
# Este script baixa e configura um ambiente de desenvolvimento Java/Maven
# de forma isolada, sem a necessidade de instalação global.
# =============================================================================

# --- 1. Definição de Variáveis ---
$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot
$toolsDir = Join-Path $projectRoot "tools"
$jdkDir = Join-Path $toolsDir "jdk-17"
$mavenDir = Join-Path $toolsDir "maven-3.9.12"

# URLs para download (versões LTS e estáveis)
$jdkUrl = "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip"
$mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.12/binaries/apache-maven-3.9.12-bin.zip"

# Nomes dos arquivos de download
$jdkZipFile = Join-Path $toolsDir "openjdk.zip"
$mavenZipFile = Join-Path $toolsDir "maven.zip"

# --- 2. Criação da Estrutura e Download ---
Write-Host "Iniciando a configuração do ambiente portátil..."

# Cria a pasta 'tools'
if (-not (Test-Path $toolsDir)) {
    Write-Host "Criando diretório de ferramentas em: $toolsDir"
    New-Item -ItemType Directory -Path $toolsDir | Out-Null
}

# Baixa e extrai o OpenJDK 17
if (-not (Test-Path $jdkDir)) {
    Write-Host "Baixando OpenJDK 17 de $jdkUrl..."
    Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZipFile
    Write-Host "Extraindo OpenJDK 17..."
    Expand-Archive -Path $jdkZipFile -DestinationPath $toolsDir -Force
    # Renomeia a pasta extraída para um nome previsível
    Move-Item -Path (Join-Path $toolsDir "jdk-17.0.2") -Destination $jdkDir
    Remove-Item $jdkZipFile
    Write-Host "OpenJDK 17 configurado em: $jdkDir"
} else {
    Write-Host "OpenJDK 17 já existe. Pulando download."
}

# Baixa e extrai o Maven
if (-not (Test-Path $mavenDir)) {
    Write-Host "Baixando Maven de $mavenUrl..."
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZipFile
    Write-Host "Extraindo Maven..."
    Expand-Archive -Path $mavenZipFile -DestinationPath $toolsDir -Force
    # Renomeia a pasta extraída
    Move-Item -Path (Join-Path $toolsDir "apache-maven-3.9.12") -Destination $mavenDir
    Remove-Item $mavenZipFile
    Write-Host "Maven configurado em: $mavenDir"
} else {
    Write-Host "Maven já existe. Pulando download."
}

# --- 3. Criação da Estrutura do Projeto Maven ---
Write-Host "Criando estrutura de diretórios do projeto Maven..."
$srcTestJava = Join-Path $projectRoot "src/test/java/com/example/automation"
$srcTestResources = Join-Path $projectRoot "src/test/resources"

New-Item -ItemType Directory -Path $srcTestJava -Force | Out-Null
New-Item -ItemType Directory -Path $srcTestResources -Force | Out-Null
Write-Host "Estrutura criada em: src/test/java e src/test/resources"

# --- 4. Geração do Script de Ativação (activate.ps1) ---
Write-Host "Gerando o script de ativação 'activate.ps1'..."
$activateScriptContent = @"
`$ErrorActionPreference = "Stop"

# Configura o JAVA_HOME para o JDK portátil
`$env:JAVA_HOME = "`$PSScriptRoot\tools\jdk-17"
Write-Host "JAVA_HOME definido para: `$env:JAVA_HOME"

# Adiciona o JDK e o Maven portáteis ao PATH da sessão atual
`$env:PATH = "`$PSScriptRoot\tools\jdk-17\bin;`$PSScriptRoot\tools\maven-3.9.6\bin;`$env:PATH"
Write-Host "Java e Maven portáteis adicionados ao PATH."

Write-Host ""
Write-Host "Ambiente ativado! Você pode usar 'java -version' e 'mvn -version' para verificar."
Write-Host "Para desativar, feche este terminal."
"@

Set-Content -Path (Join-Path $projectRoot "activate.ps1") -Value $activateScriptContent
Write-Host "Script 'activate.ps1' criado com sucesso."
Write-Host ""
Write-Host "Setup concluído! Execute '.\activate.ps1' para iniciar o ambiente."