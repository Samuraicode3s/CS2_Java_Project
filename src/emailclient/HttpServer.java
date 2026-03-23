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

        server.createContext("/signup",         new SignupHandler());
        server.createContext("/login",          new LoginHandler());
        server.createContext("/send",           new SendHandler());
        server.createContext("/inbox",          new InboxHandler());
        server.createContext("/sent",           new SentHandler());
        server.createContext("/delete",         new DeleteHandler());
        server.createContext("/createSubEmail", new CreateSubEmailHandler());
        server.createContext("/getSubEmails",   new GetSubEmailsHandler());
        server.createContext("/deleteSubEmail", new DeleteSubEmailHandler());
        server.createContext("/index.html",     new StaticHandler());
        server.createContext("/login.html",     new StaticHandler());
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));

        server.start();
        System.out.println("Server running at http://localhost:8080");
    }

    static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes());
    }

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

    static boolean handleOptions(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            respond(ex, 204, "");
            return true;
        }
        return false;
    }

    // ── /signup ──
    static class SignupHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String name = FileStorage.getValue(body, "name");
            String email = FileStorage.getValue(body, "email");
            String password = FileStorage.getValue(body, "password");

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Missing fields\"}");
                return;
            }

            if (FileStorage.userExists(email)) {
                respond(ex, 400, "{\"error\":\"Email already registered\"}");
                return;
            }

            String id = String.valueOf(System.currentTimeMillis());
            String userJson = "{"
                + "\"id\":" + FileStorage.jsonStr(id) + ","
                + "\"name\":" + FileStorage.jsonStr(name) + ","
                + "\"email\":" + FileStorage.jsonStr(email) + ","
                + "\"password\":" + FileStorage.jsonStr(password)
                + "}";

            FileStorage.appendUser(userJson);
            System.out.println("New user registered: " + email);
            respond(ex, 200, "{\"success\":true,\"name\":" + FileStorage.jsonStr(name) + "}");
        }
    }

    // ── /login ──
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String email = FileStorage.getValue(body, "email");
            String password = FileStorage.getValue(body, "password");
            String userJson = FileStorage.findUser(email);

            if (userJson.isEmpty()) {
                respond(ex, 401, "{\"error\":\"User not found\"}");
                return;
            }

            if (!FileStorage.getValue(userJson, "password").equals(password)) {
                respond(ex, 401, "{\"error\":\"Wrong password\"}");
                return;
            }

            String name = FileStorage.getValue(userJson, "name");
            System.out.println("User logged in: " + email);
            respond(ex, 200, "{\"success\":true,\"name\":" + FileStorage.jsonStr(name)
                    + ",\"email\":" + FileStorage.jsonStr(email) + "}");
        }
    }

    // ── /send ──
    static class SendHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);

            String sender = FileStorage.getValue(body, "sender");
            String recipient = FileStorage.getValue(body, "recipient");
            String subject = FileStorage.getValue(body, "subject");
            String emailBody = FileStorage.getValue(body, "body");

            String attachments = "[]";
            int attIdx = body.indexOf("\"attachments\":");

            if (attIdx != -1) {
                int arrStart = body.indexOf("[", attIdx);

                if (arrStart != -1) {
                    int depth = 0;
                    int arrEnd = arrStart;

                    for (int i = arrStart; i < body.length(); i++) {
                        if (body.charAt(i) == '[') {
                            depth++;
                        } else if (body.charAt(i) == ']') {
                            depth--;
                            if (depth == 0) {
                                arrEnd = i;
                                break;
                            }
                        }
                    }

                    attachments = body.substring(arrStart, arrEnd + 1);
                }
            }

            if (recipient.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Recipient required\"}");
                return;
            }

            String actualRecipient = recipient;
            String subEmailJson = FileStorage.findSubEmail(recipient);

            if (!subEmailJson.isEmpty()) {
                boolean isActive = FileStorage.getValue(subEmailJson, "isActive").equals("true");
                String ownerEmail = FileStorage.getValue(subEmailJson, "ownerEmail");
                String[] allowedSenders = FileStorage.getArrayValues(subEmailJson, "allowedSenders");

                if (!isActive) {
                    respond(ex, 403, "{\"error\":\"SubEmail is inactive\"}");
                    return;
                }

                if (allowedSenders.length > 0) {
                    boolean allowed = false;

                    for (int i = 0; i < allowedSenders.length; i++) {
                        if (allowedSenders[i].equalsIgnoreCase(sender)) {
                            allowed = true;
                            break;
                        }
                    }

                    if (!allowed) {
                        respond(ex, 403, "{\"error\":\"Sender not allowed for this SubEmail\"}");
                        return;
                    }
                }

                actualRecipient = ownerEmail;
            }

            String id = String.valueOf(System.currentTimeMillis());
            String time = new Date().toString();

            String inboxFile = "inbox_" + actualRecipient.replace("@", "_").replace(".", "_") + ".json";
            String emailJson = FileStorage.buildEmailJson(id, sender, recipient, subject, emailBody, time, attachments);
            FileStorage.appendEmail(inboxFile, emailJson);

            String sentFile = "sent_" + sender.replace("@", "_").replace(".", "_") + ".json";
            FileStorage.appendEmail(sentFile, emailJson);

            System.out.println("Email saved: " + sender + " → " + recipient);
            respond(ex, 200, "{\"success\":true}");
        }
    }

    // ── /inbox ──
    static class InboxHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String query = ex.getRequestURI().getQuery();
            String email = (query != null && query.startsWith("email=")) ? query.substring(6) : "";

            if (email.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Email required\"}");
                return;
            }

            String content = FileStorage.readFile("inbox_" + email.replace("@", "_").replace(".", "_") + ".json");
            respond(ex, 200, content.isEmpty() ? "[]" : content);
        }
    }

    // ── /sent ──
    static class SentHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String query = ex.getRequestURI().getQuery();
            String email = (query != null && query.startsWith("email=")) ? query.substring(6) : "";

            if (email.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Email required\"}");
                return;
            }

            String content = FileStorage.readFile("sent_" + email.replace("@", "_").replace(".", "_") + ".json");
            respond(ex, 200, content.isEmpty() ? "[]" : content);
        }
    }

    // ── /delete ──
    static class DeleteHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String email = FileStorage.getValue(body, "email");
            String id = FileStorage.getValue(body, "id");
            String folder = FileStorage.getValue(body, "folder");

            if (email.isEmpty() || id.isEmpty() || folder.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Missing fields\"}");
                return;
            }

            String filename = folder + "_" + email.replace("@", "_").replace(".", "_") + ".json";
            String content = FileStorage.readFile(filename).trim();

            if (!content.isEmpty() && !content.equals("[]")) {
                FileStorage.writeFile(filename, removeById(content, id));
            }

            System.out.println("Email deleted from " + folder + ": " + id);
            respond(ex, 200, "{\"success\":true}");
        }

        private String removeById(String jsonArray, String id) {
            String[] parts = jsonArray.replace("[", "").replace("]", "").split("\\},\\{");
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;

            for (String part : parts) {
                String obj = part.trim();
                if (!obj.startsWith("{")) obj = "{" + obj;
                if (!obj.endsWith("}")) obj = obj + "}";

                if (FileStorage.getValue(obj, "id").equals(id)) {
                    continue;
                }

                if (!first) sb.append(",");
                sb.append(obj);
                first = false;
            }

            sb.append("]");
            return sb.toString();
        }
    }

    // ── /createSubEmail ──
    static class CreateSubEmailHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String ownerEmail = FileStorage.getValue(body, "ownerEmail");
            String subAddress = FileStorage.getValue(body, "subAddress");
            String label = FileStorage.getValue(body, "label");

            if (ownerEmail.isEmpty() || subAddress.isEmpty() || label.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Missing fields\"}");
                return;
            }

            if (FileStorage.subEmailExists(subAddress)) {
                respond(ex, 400, "{\"error\":\"SubEmail already exists\"}");
                return;
            }

            String[] allowedSenders = FileStorage.getArrayValues(body, "allowedSenders");

            SubEmail subEmail = new SubEmail(
                String.valueOf(System.currentTimeMillis()),
                ownerEmail,
                subAddress,
                label,
                allowedSenders,
                true
            );

            FileStorage.appendSubEmail(ownerEmail, subEmail.toJson());

            System.out.println("Created SubEmail: " + subAddress + " for " + ownerEmail);
            respond(ex, 200, "{\"success\":true}");
        }
    }

    // ── /getSubEmails ──
    static class GetSubEmailsHandler implements HttpHandler {
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

            String content = FileStorage.getSubEmails(email);
            respond(ex, 200, content);
        }
    }

    // ── /deleteSubEmail ──
    static class DeleteSubEmailHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (handleOptions(ex)) return;

            String body = readBody(ex);
            String ownerEmail = FileStorage.getValue(body, "ownerEmail");
            String subAddress = FileStorage.getValue(body, "subAddress");

            if (ownerEmail.isEmpty() || subAddress.isEmpty()) {
                respond(ex, 400, "{\"error\":\"Missing fields\"}");
                return;
            }

            String safeOwner = ownerEmail.replace("@", "_").replace(".", "_");
            String filename = "subemails_" + safeOwner + ".json";
            String content = FileStorage.readFile(filename).trim();

            if (content.isEmpty() || content.equals("[]")) {
                respond(ex, 200, "{\"success\":true}");
                return;
            }

            String cleaned = content.substring(1, content.length() - 1);
            String[] parts = cleaned.split("\\},\\{");

            StringBuilder sb = new StringBuilder("[");
            boolean first = true;

            for (String part : parts) {
                String obj = part.trim();

                if (!obj.startsWith("{")) obj = "{" + obj;
                if (!obj.endsWith("}")) obj = obj + "}";

                if (FileStorage.getValue(obj, "subAddress").equalsIgnoreCase(subAddress)) {
                    continue;
                }

                if (!first) sb.append(",");
                sb.append(obj);
                first = false;
            }

            sb.append("]");
            FileStorage.writeFile(filename, sb.toString());

            System.out.println("Deleted SubEmail: " + subAddress);
            respond(ex, 200, "{\"success\":true}");
        }
    }

    // ── serves index.html and login.html ──
    static class StaticHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                respond(ex, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String path = ex.getRequestURI().getPath();

            if (path.equals("/") || path.equals("/index.html")) {
                path = "/index.html";
            } else if (path.equals("/login.html")) {
                path = "/login.html";
            } else {
                respond(ex, 404, "{\"error\":\"Not found\"}");
                return;
            }

            File f = new File("." + path);

            if (!f.exists()) {
                respond(ex, 404, "{\"error\":\"File not found\"}");
                return;
            }

            byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());
            ex.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.getResponseBody().close();
        }
    }
}