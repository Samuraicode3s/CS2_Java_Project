package emailclient;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Date;

public class HttpServer {

    public static void start() throws Exception {
        FileStorage.init();

        com.sun.net.httpserver.HttpServer server =
            com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/signup",  new SignupHandler());
        server.createContext("/login",   new LoginHandler());
        server.createContext("/send",    new SendHandler());
        server.createContext("/inbox",   new InboxHandler());
        server.createContext("/sent",    new SentHandler());

        server.start();
        System.out.println("Server running at http://localhost:8080");
    }

    // ── helper: read the full request body as a string ──
    static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes());
    }

    // ── helper: send a JSON response back to the browser ──
    static void respond(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        byte[] bytes = json.getBytes();
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    // ── helper: handle browser preflight OPTIONS requests ──
    static boolean handleOptions(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            respond(ex, 204, "");
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────
    // POST /signup
    // body: {"name":"John","email":"john@mail.com","password":"1234"}
    // ─────────────────────────────────────────
    static class SignupHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String name     = FileStorage.getValue(body, "name");
            String email    = FileStorage.getValue(body, "email");
            String password = FileStorage.getValue(body, "password");

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Missing fields\"}");
                return;
            }

            if (FileStorage.userExists(email)) {
                respond(ex, 400, "{\"error\":\"Email already registered\"}");
                return;
            }

            // give each user a simple incremental ID
            String id = String.valueOf(System.currentTimeMillis());
            String userJson = "{" +
                "\"id\":" + FileStorage.jsonStr(id) + "," +
                "\"name\":" + FileStorage.jsonStr(name) + "," +
                "\"email\":" + FileStorage.jsonStr(email) + "," +
                "\"password\":" + FileStorage.jsonStr(password) +
            "}";

            FileStorage.appendUser(userJson);
            System.out.println("New user registered: " + email);
            respond(ex, 200, "{\"success\":true,\"name\":" + FileStorage.jsonStr(name) + "}");
        }
    }

    // ─────────────────────────────────────────
    // POST /login
    // body: {"email":"john@mail.com","password":"1234"}
    // ─────────────────────────────────────────
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body     = readBody(ex);
            String email    = FileStorage.getValue(body, "email");
            String password = FileStorage.getValue(body, "password");

            String userJson = FileStorage.findUser(email);

            if (userJson.isEmpty()) {
                respond(ex, 401, "{\"error\":\"User not found\"}");
                return;
            }

            String storedPassword = FileStorage.getValue(userJson, "password");
            if (!storedPassword.equals(password)) {
                respond(ex, 401, "{\"error\":\"Wrong password\"}");
                return;
            }

            String name = FileStorage.getValue(userJson, "name");
            System.out.println("User logged in: " + email);
            respond(ex, 200, "{\"success\":true,\"name\":" + FileStorage.jsonStr(name) +
                             ",\"email\":" + FileStorage.jsonStr(email) + "}");
        }
    }

    // ─────────────────────────────────────────
    // POST /send
    // body: {"sender":"john@mail.com","recipient":"james@mail.com",
    //        "subject":"Hello","body":"Hi there"}
    // ─────────────────────────────────────────
    static class SendHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body      = readBody(ex);
            String sender    = FileStorage.getValue(body, "sender");
            String recipient = FileStorage.getValue(body, "recipient");
            String subject   = FileStorage.getValue(body, "subject");
            String emailBody = FileStorage.getValue(body, "body");

            if (recipient.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Recipient required\"}");
                return;
            }

            String id   = String.valueOf(System.currentTimeMillis());
            String time = new Date().toString();

            // save to recipient's inbox
            String inboxFile = "inbox_" + recipient.replace("@", "_").replace(".", "_") + ".json";
            String emailJson = FileStorage.buildEmailJson(id, sender, recipient, subject, emailBody, time);
            FileStorage.appendEmail(inboxFile, emailJson);

            // save to sender's sent folder
            String sentFile = "sent_" + sender.replace("@", "_").replace(".", "_") + ".json";
            FileStorage.appendEmail(sentFile, emailJson);

            System.out.println("Email saved: " + sender + " → " + recipient);
            respond(ex, 200, "{\"success\":true}");
        }
    }

    // ─────────────────────────────────────────
    // GET /inbox?email=john@mail.com
    // returns the full inbox JSON array
    // ─────────────────────────────────────────
    static class InboxHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String query = ex.getRequestURI().getQuery(); // "email=john@mail.com"
            String email = "";
            if (query != null && query.startsWith("email=")) {
                email = query.substring(6);
            }

            if (email.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Email required\"}");
                return;
            }

            String inboxFile = "inbox_" + email.replace("@", "_").replace(".", "_") + ".json";
            String content   = FileStorage.readFile(inboxFile);

            if (content.isEmpty()) content = "[]";
            respond(ex, 200, content);
        }
    }

    // ─────────────────────────────────────────
    // GET /sent?email=john@mail.com
    // ─────────────────────────────────────────
    static class SentHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String query = ex.getRequestURI().getQuery();
            String email = "";
            if (query != null && query.startsWith("email=")) {
                email = query.substring(6);
            }

            if (email.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Email required\"}");
                return;
            }

            String sentFile = "sent_" + email.replace("@", "_").replace(".", "_") + ".json";
            String content  = FileStorage.readFile(sentFile);

            if (content.isEmpty()) content = "[]";
            respond(ex, 200, content);
        }
    }
}