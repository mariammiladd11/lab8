/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author CYBER-TECH
 */
public class SignupService {

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean signup(String username, String email, String password, String role) {

        if (!email.contains("@")) {
            return false; 
        }
        if (JsonDatabaseManager.findUserByEmail(email) != null) {
            return false; 
        }

        String userId = "U" + System.currentTimeMillis();
        String passwordHash = hashPassword(password);

        JSONObject userObj = new JSONObject();
        userObj.put("userId", userId);
        userObj.put("role", role);
        userObj.put("username", username);
        userObj.put("email", email);
        userObj.put("passwordHash", passwordHash);

        if (role.equals("student")) userObj.put("enrolledCourses", new JSONArray());
        if (role.equals("instructor")) userObj.put("createdCourses", new JSONArray());

        JSONArray users = JsonDatabaseManager.loadUsers();
        users.put(userObj);

        JsonDatabaseManager.saveUsers(users);

        return true;
    }
}

