package com.example.automation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de API para o endpoint de Login (Parte C).
 */
public class LoginAPITest extends BaseTest {

    @BeforeAll
    static void setupApi() {
        // Configura a URL base para todas as requisições do RestAssured
        RestAssured.baseURI = prop.getProperty("base.url.api");
    }

    private Map<String, String> createLoginPayload(String username, String password) {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        return payload;
    }

    @Test
    @DisplayName("C.1: Deve retornar 200 e um token para login com sucesso")
    void testApiLoginSucesso() {
        Map<String, String> payload = createLoginPayload(
            prop.getProperty("user.valid"),
            prop.getProperty("user.password")
        );

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    @DisplayName("C.2: Deve retornar 401 para credenciais inválidas")
    void testApiLoginCredenciaisInvalidas() {
        Map<String, String> payload = createLoginPayload(
            prop.getProperty("user.valid"),
            "senha_invalida"
        );

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/login")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("C.3: Deve retornar 403 para usuário visitante sem permissão")
    void testApiLoginVisitanteSemPermissao() {
        Map<String, String> payload = createLoginPayload(
            prop.getProperty("user.visitor"),
            prop.getProperty("user.password")
        );

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/login")
        .then()
            .statusCode(403);
    }

    @Test
    @DisplayName("C.4: Deve retornar 423 para usuário bloqueado")
    void testApiLoginUsuarioBloqueado() {
        Map<String, String> payload = createLoginPayload(
            prop.getProperty("user.blocked"),
            prop.getProperty("user.password")
        );

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/login")
        .then()
            .statusCode(423);
    }
}