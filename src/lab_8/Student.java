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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    private ArrayList<String> enrolledCourses = new ArrayList<>();
    private Map<String, List<String>> progress = new HashMap<>();

    public Student(String userId, String username, String email, String passwordHash) {
        super(userId, "student", username, email, passwordHash);
    }

    public Map<String, List<String>> getProgress() {
        return progress;
    }

    public ArrayList<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void enrollCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            progress.put(courseId, new ArrayList<>());
        }
    }

    public void markLessonCompleted(String courseId, String lessonId) {
        if (!progress.containsKey(courseId)) {
            progress.put(courseId, new ArrayList<>());
        }

        List<String> completedLessons = progress.get(courseId);
        progress.remove(courseId);

        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
        }
        progress.put(courseId, completedLessons);
        System.out.println(progress.isEmpty());
    }

    public List<String> getCompletedLessons(String courseId) {
        if( progress.get(courseId)!=null)
            return progress.get(courseId);
        else 
            return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Student: " + username + " (ID: " + userId + ")";
    }
}
