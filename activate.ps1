# Este script configura o ambiente e opcionalmente executa um comando.
param(
    [string]$CommandToRun
)

$ErrorActionPreference = "Stop"

# Configura o JAVA_HOME para o JDK portátil
$env:JAVA_HOME = "$PSScriptRoot\tools\jdk-17"
Write-Host "JAVA_HOME definido para: $env:JAVA_HOME"

# Adiciona o JDK e o Maven portáteis ao PATH da sessão atual
$env:PATH = "$PSScriptRoot\tools\jdk-17\bin;$PSScriptRoot\tools\maven-3.9.12\bin;$env:PATH"
Write-Host "Java e Maven portáteis adicionados ao PATH."
Write-Host ""

if (-not [string]::IsNullOrEmpty($CommandToRun)) {
    Write-Host "Executando o comando: $CommandToRun"
    Write-Host "--------------------------------------------------"
    Invoke-Expression $CommandToRun
} else {
    Write-Host "Ambiente ativado! Você pode usar 'java -version' e 'mvn -version' para verificar."
    Write-Host "Para desativar, feche este terminal."
}
