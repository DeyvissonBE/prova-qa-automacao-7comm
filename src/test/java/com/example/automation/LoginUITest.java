package com.example.automation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de UI para a funcionalidade de Login (Parte B).
 */
public class LoginUITest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    @Override
    void setUp() {
        // Inicializa o WebDriver e a Page Object antes de cada teste de UI
        initWebDriver();
        loginPage = new LoginPage(driver, wait);
        loginPage.navegarParaPagina(prop.getProperty("base.url.ui"));
    }

    @Test
    @DisplayName("B.1: Deve realizar login com sucesso para usuário válido")
    void testLoginValido() {
        loginPage.fazerLogin(prop.getProperty("user.valid"), prop.getProperty("user.password"));

        // Aguarda o redirecionamento para o dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verifica se a URL atual contém /dashboard
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/dashboard"), "A URL deveria terminar com '/dashboard' após o login.");
    }

    @Test
    @DisplayName("B.2: Deve exibir 'Acesso Negado' para usuário visitante")
    void testLoginVisitante() {
        loginPage.fazerLogin(prop.getProperty("user.visitor"), prop.getProperty("user.password"));

        // Verifica se a mensagem de erro é exibida
        String mensagemErro = loginPage.getMensagemErro();
        assertEquals("Acesso Negado", mensagemErro, "A mensagem de erro para visitante está incorreta.");

        // Garante que não houve redirecionamento
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"), "O usuário visitante não deveria ser redirecionado.");
    }

    @Test
    @DisplayName("B.3: Deve bloquear usuário após 3 tentativas falhas")
    void testBloqueioUsuario() {
        // Reseta o estado do mock para garantir que o contador de tentativas comece do zero
        try {
            java.net.URL url = new java.net.URL(prop.getProperty("base.url.api") + "/api/reset");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getResponseCode(); // Apenas executa a requisição
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        String usuario = prop.getProperty("user.to_be_blocked");
        
        // Tenta fazer login 3 vezes com senha errada
        for (int i = 0; i < 3; i++) {
            loginPage.fazerLogin(usuario, "senha_errada_" + (i + 1));
            // A cada tentativa, a página pode recarregar. Garantimos que estamos na página certa.
             loginPage.navegarParaPagina(prop.getProperty("base.url.ui"));
        }
        
        // Na quarta tentativa (que seria a falha que causa o bloqueio, ou a primeira após o bloqueio)
        // O sistema deve retornar "Usuário Bloqueado"
        loginPage.fazerLogin(usuario, "qualquer_senha");

        String mensagemErro = loginPage.getMensagemErro();
        assertEquals("Usuário Bloqueado", mensagemErro, "A mensagem de bloqueio de usuário está incorreta.");
    }
}