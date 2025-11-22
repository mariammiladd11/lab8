/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
public abstract class User {
    
    public enum Role {
        STUDENT,
        INSTRUCTOR,
        ADMIN
    }

    protected String userId;
    protected Role role;       
    protected String username;
    protected String email;
    protected String passwordHash;

    public User(String userId, String role, String username, String email, String passwordHash) {
        this.userId = userId;
        this.role = Role.valueOf(role.toUpperCase()); 
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

 
    public User(String userId, Role role, String username, String email, String passwordHash) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    
    public String getUserId() { return userId; }
    public Role getRole() { return role; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    // -------- JSON Conversion --------
    public org.json.JSONObject toJson() {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("userId", userId);
        obj.put("role", role.toString()); // save as string
        obj.put("username", username);
        obj.put("email", email);
        obj.put("passwordHash", passwordHash);
        return obj;
    }
}
