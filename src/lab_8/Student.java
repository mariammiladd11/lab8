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
    private Map<String, Map<String, LessonProgress>> progress = new HashMap<>()

    public Student(String userId, String username, String email, String passwordHash) {
        super(userId, "student", username, email, passwordHash);
    }

    public Map<String, Map<String, LessonProgress>> getProgress() {
        return progress;
    }

    public ArrayList<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void enrollCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            progress.put(courseId, new HashMap<>());
        }
    }
    // NEW: Record a quiz attempt for a lesson
    public void recordQuizAttempt(String courseId, String lessonId, int score, boolean passed) {
        progress.putIfAbsent(courseId, new HashMap<>());
        Map<String, LessonProgress> courseProgress = progress.get(courseId);

        LessonProgress lp = courseProgress.getOrDefault(lessonId, new LessonProgress());
        lp.incrementAttempts();
        lp.setScore(score);
        lp.setPassed(passed);

        courseProgress.put(lessonId, lp);
    }

    // NEW: Check if a lesson is passed
    public boolean isLessonPassed(String courseId, String lessonId) {
        return progress.containsKey(courseId)
            && progress.get(courseId).containsKey(lessonId)
            && progress.get(courseId).get(lessonId).isPassed();
    }
    
    // NEW: Get all completed lessons in a course
    public List<String> getCompletedLessons(String courseId) {
        List<String> completed = new ArrayList<>();
        if (!progress.containsKey(courseId)) return completed;

        for (Map.Entry<String, LessonProgress> entry : progress.get(courseId).entrySet()) {
            if (entry.getValue().isPassed()) completed.add(entry.getKey());
        }
        return completed;
    }

    @Override
    public String toString() {
        return "Student: " + username + " (ID: " + userId + ")";
    }
    public LessonProgress getLessonProgress(String courseId, String lessonId) {
    if (!progress.containsKey(courseId)) return null;
    return progress.get(courseId).get(lessonId); // returns LessonProgress or null
}

}
