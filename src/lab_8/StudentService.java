/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import java.util.ArrayList;
import java.util.List;
import static lab_8.JsonDatabaseManager.loadUsers;
import static lab_8.JsonDatabaseManager.saveUsers;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Linae
 */
public class StudentService {

    
    public Student getStudentById(String studentId) {
        JSONArray arr = JsonDatabaseManager.loadUsers();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject u = arr.getJSONObject(i);

            if (u.getString("userId").equals(studentId)
                    && u.getString("role").equals("student")) {

                Student s = new Student(
                        u.getString("userId"),
                        u.getString("username"),
                        u.getString("email"),
                        u.getString("passwordHash")
                );

                // Load enrolledCourses
                JSONArray enrolled = u.optJSONArray("enrolledCourses");
                if (enrolled != null) {
                    for (int j = 0; j < enrolled.length(); j++) {
                        s.getEnrolledCourses().add(enrolled.getString(j));
                    }
                }

                // Load progress
                JSONObject progObj = u.optJSONObject("progress");
                if (progObj != null) {
                    for (String courseId : progObj.keySet()) {
                        JSONArray lessons = progObj.getJSONArray(courseId);
                        List<String> list = new ArrayList<>();
                        for (int k = 0; k < lessons.length(); k++) {
                            list.add(lessons.getString(k));
                        }
                        s.getProgress().put(courseId, list);
                    }
                }

                return s;
            }
        }
        return null;
    }

  
    private void saveStudent(Student s) {
        JSONArray arr = JsonDatabaseManager.loadUsers();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject u = arr.getJSONObject(i);

            if (u.getString("userId").equals(s.getUserId())) {

                // Update enrolledCourses
                JSONArray newEnrolled = new JSONArray(s.getEnrolledCourses());
                u.put("enrolledCourses", newEnrolled);

                // Update progress
                JSONObject newProgress = new JSONObject();
                for (String courseId : s.getProgress().keySet()) {
                    newProgress.put(courseId,
                            new JSONArray(s.getProgress().get(courseId)));
                }
                u.put("progress", newProgress);

                JsonDatabaseManager.saveUsers(arr);
                return;
            }
        }
    }

   
    public boolean enrollCourse(String studentId, String courseId) {
        Student s = getStudentById(studentId);
        if (s == null) {
            return false;
        }

        s.enrollCourse(courseId);
        saveStudent(s);

        return true;
    }

    
    public List<String> getEnrolledCourseIds(String studentId) {
        Student s = getStudentById(studentId);
        if (s == null) {
            return new ArrayList<>();
        }

        return s.getEnrolledCourses();
    }

    
    public boolean completeLesson(String studentId, String courseId, String lessonId) {
        System.out.println(studentId);
        Student s = getStudentById(studentId);
        if (s == null) {
            return false;
        }

        s.markLessonCompleted(courseId, lessonId);

        saveStudent(s);

        return true;
    }

    public static void completeLessons(String studentId, String courseId, String lessonId) {
        JSONArray users = loadUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);

            if (u.getString("userId").equals(studentId)) {

                JSONObject progress = u.optJSONObject("progress");
                if (progress == null) {
                    progress = new JSONObject();
                }

                JSONArray completed = progress.optJSONArray(courseId);
                if (completed == null) {
                    completed = new JSONArray();
                }

                // Avoid duplicates
                if (!completed.toList().contains(lessonId)) {
                    completed.put(lessonId);
                }

                progress.put(courseId, completed);
                u.put("progress", progress);

                saveUsers(users);
                return;
            }
        }
    }

   
    public List<String> getCompletedLessons(String studentId, String courseId) {
        Student s = getStudentById(studentId);
        if (s == null) {
            return new ArrayList<>();
        }

        return s.getCompletedLessons(courseId);
    }

}
