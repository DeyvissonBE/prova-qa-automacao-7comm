package com.example.automation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.StubApp;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Classe base para todos os testes.
 * Responsável por carregar configurações, inicializar e finalizar o WebDriver.
 */
public abstract class BaseTest {

    protected static Properties prop = new Properties();
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static StubApp stubApp;

    @BeforeAll
    static void setup() throws IOException {
        // Inicia o Mock Server
        stubApp = new StubApp();
        stubApp.start();

        // Carrega as propriedades do arquivo de configuração
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);
        
        // Configura o WebDriverManager para baixar o driver do Chrome
        WebDriverManager.chromedriver().setup();
    }

    @AfterAll
    static void tearDownAll() {
        // Para o Mock Server
        if (stubApp != null) {
            stubApp.stop();
        }
    }

    @BeforeEach
    void setUp() {
        // Inicializa o WebDriver antes de cada teste de UI
        // Para testes de API, o driver não será inicializado se não for chamado.
        // A inicialização aqui é focada nos testes de UI.
    }
    
    protected void initWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        // Define a espera explícita padrão de 5 segundos, conforme o requisito
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterEach
    void tearDown() {
        // Fecha o navegador após cada teste de UI
        if (driver != null) {
            driver.quit();
        }
    }
}