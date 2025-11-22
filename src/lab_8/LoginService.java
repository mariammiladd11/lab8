/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import org.json.JSONObject;

/**
 *
 * @author CYBER-TECH
 */
import org.json.JSONObject;

import org.json.JSONObject;

public class LoginService {

    public static User login(String email, String password) {
        JSONObject obj = JsonDatabaseManager.findUserByEmail(email);
        if (obj == null) return null;

        String hash = SignupService.hashPassword(password);
        if (!obj.getString("passwordHash").equals(hash)) {
            return null; // wrong password
        }

        String role = obj.getString("role").toLowerCase();

        switch (role) {
            case "student":
                return new Student(
                    obj.getString("userId"),
                    obj.getString("username"),
                    obj.getString("email"),
                    obj.getString("passwordHash")
                );

            case "instructor":
                Instructor instructor = new Instructor(
                    obj.getString("userId"),
                    obj.getString("username"),
                    obj.getString("email"),
                    obj.getString("passwordHash")
                );
                if (obj.has("createdCourses")) {
                    for (Object c : obj.getJSONArray("createdCourses")) {
                        instructor.addCourse(c.toString());
                    }
                }
                return instructor;

            case "admin":
                return new Admin(
                    obj.getString("userId"),
                    obj.getString("username"),
                    obj.getString("email"),
                    obj.getString("passwordHash")
                );

            default:
                return null;
        }
    }
}



