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
            if (u.getString("userId").equals(studentId) && u.getString("role").equals("student")) {

                Student s = new Student(
                        u.getString("userId"),
                        u.getString("username"),
                        u.getString("email"),
                        u.getString("passwordHash")
                );

                // Load enrolled courses
                JSONArray enrolled = u.optJSONArray("enrolledCourses");
                if (enrolled != null) {
                    for (int j = 0; j < enrolled.length(); j++) {
                        s.getEnrolledCourses().add(enrolled.getString(j));
                    }
                }

                // NEW: Load lesson progress (quiz attempts, score, passed)
                JSONObject progObj = u.optJSONObject("progress");
                if (progObj != null) {
                    for (String courseId : progObj.keySet()) {
                        JSONObject lessonsObj = progObj.getJSONObject(courseId);
                        Map<String, LessonProgress> lessonMap = new HashMap<>();

                        for (String lessonId : lessonsObj.keySet()) {
                            JSONObject lp = lessonsObj.getJSONObject(lessonId);
                            LessonProgress lessonProgress = new LessonProgress();
                            lessonProgress.setScore(lp.optInt("score", 0));
                            lessonProgress.setPassed(lp.optBoolean("passed", false));
                            for (int k = 0; k < lp.optInt("attempts", 0); k++) lessonProgress.incrementAttempts();
                            lessonMap.put(lessonId, lessonProgress);
                        }

                        s.getProgress().put(courseId, lessonMap);
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
                // Save enrolled courses
                u.put("enrolledCourses", new JSONArray(s.getEnrolledCourses()));

                // NEW: Save lesson progress including quiz info
                JSONObject newProgress = new JSONObject();
                for (String courseId : s.getProgress().keySet()) {
                    JSONObject lessonsObj = new JSONObject();
                    for (String lessonId : s.getProgress().get(courseId).keySet()) {
                        LessonProgress lp = s.getProgress().get(courseId).get(lessonId);
                        JSONObject lpObj = new JSONObject();
                        lpObj.put("attempts", lp.getAttempts());
                        lpObj.put("score", lp.getScore());
                        lpObj.put("passed", lp.isPassed());
                        lessonsObj.put(lessonId, lpObj);
                    }
                    newProgress.put(courseId, lessonsObj);
                }
                u.put("progress", newProgress);

                JsonDatabaseManager.saveUsers(arr);
                return;
            }
        }
    }
      public void recordQuizAttempt(String studentId, String courseId, String lessonId, int score, boolean passed) {
        Student s = getStudentById(studentId);
        if (s == null) return;
        s.recordQuizAttempt(courseId, lessonId, score, passed);
        saveStudent(s);
    }
    public boolean canAccessLesson(String studentId, String courseId, String lessonId) {
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(lessonId)) {
                if (i == 0) return true; // first lesson always accessible
                Lesson prev = lessons.get(i - 1);
                Student s = getStudentById(studentId);
                return s != null && s.isLessonPassed(courseId, prev.getLessonId());
            }
        }
        return false;
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

   public List<String> getCompletedLessons(String studentId, String courseId) {
        Student s = getStudentById(studentId);
        if (s == null) {
            return new ArrayList<>();
        }

        return s.getCompletedLessons(courseId);
    }

}
