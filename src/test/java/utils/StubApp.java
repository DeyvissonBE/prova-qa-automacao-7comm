package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class StubApp {

    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new LoginFrontendHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/api/login", new LoginApiHandler());
        server.createContext("/api/reset", new ResetHandler()); // Agora vai funcionar
        server.setExecutor(null);
        server.start();
        System.out.println("StubApp (Mock Server) iniciado na porta 8080...");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("StubApp (Mock Server) parado.");
        }
    }

    // Handler para a página de login (frontend)
    static class LoginFrontendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String html = "<html><head><title>Login</title></head>" +
                          "<body>" +
                          "<h1>Página de Login</h1>" +
                          "<input type='text' id='username' placeholder='Usuário'><br>" +
                          "<input type='password' id='password' placeholder='Senha'><br>" +
                          "<button id='loginButton'>Entrar</button>" +
                          "<div id='errorMessage' style='color:red; margin-top:10px;'></div>" +
                          "<script>" +
                          "document.getElementById('loginButton').onclick = function() {" +
                          "  const user = document.getElementById('username').value;" +
                          "  const pass = document.getElementById('password').value;" +
                          "  fetch('/api/login', {" +
                          "    method: 'POST'," +
                          "    headers: {'Content-Type': 'application/json'}," +
                          "    body: JSON.stringify({ username: user, password: pass })" +
                          "  }).then(response => {" +
                          "    if (response.status === 200) {" +
                          "      window.location.href = '/dashboard';" +
                          "    } else {" +
                          "      response.json().then(data => {" +
                          "        let msg = data.message || data.error || 'Erro desconhecido';" +
                          "        if (response.status === 423) { msg = 'Usuário Bloqueado'; }" +
                          "        if (response.status === 403) { msg = 'Acesso Negado'; }" +
                          "        document.getElementById('errorMessage').innerText = msg;" +
                          "      });" +
                          "    }" +
                          "  });" +
                          "};" +
                          "</script>" +
                          "</body></html>";
            sendResponse(t, 200, html, "text/html");
        }
    }

    // Handler para a página de dashboard
   static class DashboardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            // Simula carregamento assíncrono de até 5 segundos
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String html = "<html><body><h1 id='welcome'>Bem vindo</h1></body></html>";
        sendResponse(t, 200, html, "text/html");

    }
}


    // Handler para a API de login (backend)
    static class LoginApiHandler implements HttpHandler {
        private static int failedLoginAttempts = 0;

        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                InputStream is = t.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                if (body.contains("\"user_valido\"") && body.contains("\"123456\"")) {
                    String jsonResponse = "{ \"token\": \"fake-jwt\", \"profile\": \"USER\" }";
                    sendResponse(t, 200, jsonResponse, "application/json");
                } else if (body.contains("\"user_visitor\"")) {
                    String jsonResponse = "{ \"message\": \"Acesso negado\" }";
                    sendResponse(t, 403, jsonResponse, "application/json");
                } else if (body.contains("\"user_blocked\"")) {
                    String jsonResponse = "{ \"error\": \"Usuário bloqueado\" }";
                    sendResponse(t, 423, jsonResponse, "application/json");
                } else if (body.contains("\"user_to_be_blocked\"")) {
                    if (failedLoginAttempts >= 3) {
                        String jsonResponse = "{ \"error\": \"Usuário bloqueado\" }";
                        sendResponse(t, 423, jsonResponse, "application/json");
                    } else {
                        failedLoginAttempts++;
                        String jsonResponse = "{ \"error\": \"Credenciais inválidas\" }";
                        sendResponse(t, 401, jsonResponse, "application/json");
                    }
                } else {
                    String jsonResponse = "{ \"error\": \"Credenciais inválidas\" }";
                    sendResponse(t, 401, jsonResponse, "application/json");
                }
            } else {
                sendResponse(t, 405, "Method Not Allowed", "text/plain");
            }
        }
    }

    // Handler para resetar o estado do mock (o contador de tentativas)
    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("GET".equals(t.getRequestMethod())) {
                LoginApiHandler.failedLoginAttempts = 0; // Zera o contador
                String response = "State reset successfully.";
                sendResponse(t, 200, response, "text/plain");
            } else {
                sendResponse(t, 405, "Method Not Allowed", "text/plain");
            }
        }
    }

    private static void sendResponse(HttpExchange t, int statusCode, String response, String contentType) throws IOException {
        t.getResponseHeaders().set("Content-Type", contentType);
        t.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}