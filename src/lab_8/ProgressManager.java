/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import org.json.JSONObject;
import org.json.JSONArray;

public class ProgressManager {

    // Load progress for 1 lesson
    public static LessonProgress getLessonProgress(String studentId, String lessonId) {
        return ProgressDatabase.getProgress(studentId, lessonId);
    }
public static void markLessonCompleted(String studentId, String courseId, String lessonId) {
    JSONArray users = JsonDatabaseManager.loadUsers();

    for (int i = 0; i < users.length(); i++) {
        JSONObject u = users.getJSONObject(i);
        if (!u.optString("userId").equals(studentId)) continue;

        JSONArray progress = u.optJSONArray("progress");
        if (progress == null) {
            progress = new JSONArray();
            u.put("progress", progress);
        }

        JSONObject courseProgress = null;
        for (int j = 0; j < progress.length(); j++) {
            JSONObject p = progress.getJSONObject(j);
            if (courseId.equals(p.optString("courseId"))) {
                courseProgress = p;
                break;
            }
        }

        if (courseProgress == null) {
            courseProgress = new JSONObject();
            courseProgress.put("courseId", courseId);
            courseProgress.put("completedLessons", new JSONArray());
            progress.put(courseProgress);
        }

        JSONArray completed = courseProgress.getJSONArray("completedLessons");
        if (!completed.toList().contains(lessonId)) {
            completed.put(lessonId);
        }

        break;
    }

    JsonDatabaseManager.saveUsers(users);
}

    // Check if all lessons in a course are completed
    public static boolean hasCompletedAllLessons(String studentId, String courseId) {
        try {
            JSONObject course = CourseManagement.getCourse(courseId);
            if (course == null) return false;

            JSONArray lessons = course.optJSONArray("lessons");
            if (lessons == null || lessons.length() == 0) return false;

            for (int i = 0; i < lessons.length(); i++) {
                JSONObject lesson = lessons.getJSONObject(i);
                String lessonId = lesson.getString("id");

                LessonProgress progress = ProgressDatabase.getProgress(studentId, lessonId);

                if (progress == null || !progress.isPassed()) {
                    return false; // lesson not passed
                }
            }

            return true; // all lessons completed

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
