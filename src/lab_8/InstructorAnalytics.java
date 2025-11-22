package lab_8;

import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sarahkhaled
 */
public class InstructorAnalytics {
    
    
    public static double getLessonAverageScore(String courseId, String lessonId) {
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                return l.getAverageScore();
            }
        }
        return 0;
    }
    
    public static double getLessonCompletion(String courseId, String lessonId) {
        List <Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                return l.getCompletionPercentage();
            }
        }
        return 0;
    }
    
    public static double getCourseCompletion(String courseId) {
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        if (lessons.isEmpty()) return 0;

        double totalCompletion = 0;
        for (Lesson l : lessons) {
            totalCompletion += l.getCompletionPercentage();
        }

        return totalCompletion / lessons.size();
    }
    
    public static double getCourseAverageScore(String courseId) {
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        if (lessons.isEmpty()) return 0;

        double totalScore = 0;
        int count = 0;

        for (Lesson l : lessons) {
            totalScore += l.getAverageScore();
            count++;
        }

        return totalScore / count;
    }

}
