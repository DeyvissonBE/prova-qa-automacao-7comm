package com.example.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object para a página de Login.
 * Encapsula os elementos da UI e as ações que podem ser realizadas nela.
 */
public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Mapeamento dos elementos da página (locators)
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("loginButton");
    private By errorMessage = By.id("errorMessage"); // Supondo que a mensagem de erro tenha este ID

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /**
     * Preenche o campo de usuário.
     * @param username O nome de usuário a ser inserido.
     */
    public void preencherUsuario(String username) {
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        userField.sendKeys(username);
    }

    /**
     * Preenche o campo de senha.
     * @param password A senha a ser inserida.
     */
    public void preencherSenha(String password) {
        WebElement passField = driver.findElement(passwordInput);
        passField.sendKeys(password);
    }

    /**
     * Clica no botão de login.
     */
    public void clicarLogin() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        button.click();
    }

    /**
     * Executa a ação de login completa.
     * @param username O nome de usuário.
     * @param password A senha.
     */
    public void fazerLogin(String username, String password) {
        preencherUsuario(username);
        preencherSenha(password);
        clicarLogin();
    }

    /**
     * Obtém o texto da mensagem de erro exibida na página.
     * A espera explícita garante que o teste aguardará a mensagem aparecer.
     * @return O texto da mensagem de erro.
     */
    public String getMensagemErro() {
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return errorElement.getText();
    }

    /**
     * Navega para a página de login.
     * @param baseUrl A URL base da aplicação.
     */
    public void navegarParaPagina(String baseUrl) {
        driver.get(baseUrl + "/login"); // Supondo que a URL de login seja /login
    }
}