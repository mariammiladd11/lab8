/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
public class Admin extends User {
      public Admin(String userId, String username, String email, String passwordHash) {
        super(userId, "ADMIN", username, email, passwordHash);
    }

    public void approveCourse(String courseId) {
        CourseManagement.setCourseStatus(courseId, "APPROVED");
    }

    public void rejectCourse(String courseId) {
        CourseManagement.setCourseStatus(courseId, "REJECTED");
    }
    
}
