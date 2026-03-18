package emailclient;

import java.io.*;
import java.nio.file.*;

public class FileStorage {

    // folder where all .json files will be saved
    private static final String DATA_DIR = "data/";

    // makes sure the data/ folder exists when the server starts
    public static void init() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("Created data/ folder.");
        }
    }

    // reads a file and returns its full text content
    // returns empty string if file doesn't exist yet
    public static String readFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(DATA_DIR + filename)));
        } catch (IOException e) {
            return "";
        }
    }

    // writes text content to a file, overwrites if it exists
    public static void writeFile(String filename, String content) {
        try {
            Files.write(Paths.get(DATA_DIR + filename), content.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing file: " + filename);
        }
    }

    // checks if a file exists
    public static boolean fileExists(String filename) {
        return new File(DATA_DIR + filename).exists();
    }

    // -------------------------------------------------------
    // Simple JSON helpers — no external libraries needed
    // -------------------------------------------------------

    // wraps a string value safely for JSON (escapes quotes)
    public static String jsonStr(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    // pulls a value out of a flat JSON string by key
    // e.g. getValue("{\"name\":\"John\"}", "name") → "John"
    public static String getValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx == -1) return "";
        int colon = json.indexOf(":", keyIdx);
        if (colon == -1) return "";
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    // builds a simple email JSON object from parts
    public static String buildEmailJson(String id, String sender, String recipient,
                                         String subject, String body, String time, String attachments) {
        return "{" +
            "\"id\":" + jsonStr(id) + "," +
            "\"sender\":" + jsonStr(sender) + "," +
            "\"recipient\":" + jsonStr(recipient) + "," +
            "\"subject\":" + jsonStr(subject) + "," +
            "\"body\":" + jsonStr(body) + "," +
            "\"time\":" + jsonStr(time) + "," +
            "\"read\":false," +
            "\"star\":false" +
        "}";
    }

    // appends a new email JSON object to an inbox file
    // the file stores a JSON array: [{...},{...}]
    public static void appendEmail(String filename, String emailJson) {
        String existing = readFile(filename).trim();
        String updated;
        if (existing.isEmpty() || existing.equals("[]")) {
            updated = "[" + emailJson + "]";
        } else {
            // strip the closing ] and append
            updated = existing.substring(0, existing.lastIndexOf("]")) + "," + emailJson + "]";
        }
        writeFile(filename, updated);
    }

    // appends a new user JSON object to users.json
    public static void appendUser(String userJson) {
        String existing = readFile("users.json").trim();
        String updated;
        if (existing.isEmpty() || existing.equals("[]")) {
            updated = "[\n  " + userJson + "\n]";
        } else {
            updated = existing.substring(0, existing.lastIndexOf("]")) + ",\n  " + userJson + "\n]";
        }
        writeFile("users.json", updated);
    }

    // checks if an email address is already registered in users.json
    public static boolean userExists(String email) {
        String users = readFile("users.json");
        return users.contains("\"" + email + "\"");
    }

    // finds a user object string by email — returns the {...} block or ""
    public static String findUser(String email) {
        String users = readFile("users.json");
        if (users.isEmpty()) return "";
        // split by "},{" to get individual user objects
        String[] parts = users.replace("[","").replace("]","").replaceAll("\\s","").split("\\},\\{");
        for (String part : parts) {
            String obj = part.trim();
            if (!obj.startsWith("{")) obj = "{" + obj;
            if (!obj.endsWith("}")) obj = obj + "}";
            if (getValue(obj, "email").equals(email)) return obj;
        }
        return "";
    }
}