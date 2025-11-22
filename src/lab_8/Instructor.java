/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Instructor extends User {
           
    
    private ArrayList<String> createdCourses = new ArrayList<>();
public Instructor(String userId, String username, String email, String passwordHash) {
        super(userId, "instructor", username, email, passwordHash);
    } 


    public ArrayList<String> getCreatedCourses() { 
            return createdCourses; 
    }
    public void addCourse(String courseId) {
        if (!createdCourses.contains(courseId)) {
            createdCourses.add(courseId);
        }
    }

    public void removeCourse(String courseId) {
        createdCourses.remove(courseId);
    }
    
    public boolean hasCourse(String courseId) {
        return createdCourses.contains(courseId);
    }
    public void setCreatedCourses(ArrayList<String> courses) {
        this.createdCourses = courses;
    }
}
