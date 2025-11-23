/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                            JSONObject lpObj = lessonsObj.getJSONObject(lessonId);

                            LessonProgress lp = new LessonProgress();
                            lp.setAttempts(lpObj.optInt("attempts", 0));
                            lp.setScore(lpObj.optInt("score", 0));
                            lp.setLastScore(lpObj.optDouble("lastScore", 0));
                            lp.setPassed(lpObj.optBoolean("passed", false));

                            lessonMap.put(lessonId, lp);
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
                        lpObj.put("lastScore", lp.getLastScore());
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
        if (passed) {
    // Ensure completedLessons array exists
    JSONArray progressArr = JsonDatabaseManager.loadUsers(); // your current array
    for (int i = 0; i < progressArr.length(); i++) {
        JSONObject u = progressArr.getJSONObject(i);
        if (!u.getString("userId").equals(studentId)) continue;

        JSONArray userProgress = u.optJSONArray("progress");
        if (userProgress == null) continue;

        for (int j = 0; j < userProgress.length(); j++) {
            JSONObject courseProgress = userProgress.getJSONObject(j);
            if (!courseId.equals(courseProgress.optString("courseId"))) continue;

            JSONArray completedLessons = courseProgress.optJSONArray("completedLessons");
            if (completedLessons == null) {
                completedLessons = new JSONArray();
                courseProgress.put("completedLessons", completedLessons);
            }

            // Only add if not already there
            if (!completedLessons.toList().contains(Lesson.getLessonId())) {
                completedLessons.put(lessonId);
            }
        }
    }

    JsonDatabaseManager.saveUsers(progressArr);
}
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
public LessonProgress getLessonProgress(String studentId, String courseId, String lessonId) {
    Student student = getStudentById(studentId);
    if (student == null) return null;

    return student.getLessonProgress(courseId, lessonId);
}

public void submitQuiz(String studentId, String courseId, String lessonId, double score, boolean passed) {
    JSONArray users = JsonDatabaseManager.loadUsers();

    for (int i = 0; i < users.length(); i++) {
        JSONObject u = users.getJSONObject(i);
        if (!studentId.equals(u.optString("userId"))) continue;

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
            courseProgress.put("lessonsProgress", new JSONArray());
            progress.put(courseProgress);
        }

        JSONArray lessonsProgress = courseProgress.optJSONArray("lessonsProgress");
        if (lessonsProgress == null) {
            lessonsProgress = new JSONArray();
            courseProgress.put("lessonsProgress", lessonsProgress);
        }

        JSONObject lp = null;
        for (int k = 0; k < lessonsProgress.length(); k++) {
            JSONObject l = lessonsProgress.getJSONObject(k);
            if (lessonId.equals(l.optString("lessonId"))) {
                lp = l;
                break;
            }
        }
        if (lp == null) {
            lp = new JSONObject();
            lp.put("lessonId", lessonId);
            lp.put("score", 0);
            lp.put("attempts", 0);
            lp.put("passed", false);
            lessonsProgress.put(lp);
        }

        lp.put("score", score);
        lp.put("attempts", lp.optInt("attempts") + 1);
        lp.put("passed", passed);

        // Optionally mark lesson as completed
        if (passed) {
            JSONArray completedLessons = courseProgress.optJSONArray("completedLessons");
            if (completedLessons == null) {
                completedLessons = new JSONArray();
                courseProgress.put("completedLessons", completedLessons);
            }
            if (!completedLessons.toList().contains(lessonId)) {
                completedLessons.put(lessonId);
            }
        }

        break;
    }

    JsonDatabaseManager.saveUsers(users);
}
}
