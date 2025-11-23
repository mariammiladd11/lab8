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
    
    
    
    public void submitQuiz(String studentId, String courseId, String lessonId, double score, boolean passed) {
    JSONArray users = JsonDatabaseManager.loadUsers();

    for (int i = 0; i < users.length(); i++) {
        JSONObject u = users.getJSONObject(i);
        if (!studentId.equals(u.optString("userId"))) continue;

        // Get or create 'progress' array
        JSONArray progressArr = u.optJSONArray("progress");
        if (progressArr == null) {
            progressArr = new JSONArray();
            u.put("progress", progressArr);
        }

        // Find courseProgress object
        JSONObject courseProgress = null;
        for (int j = 0; j < progressArr.length(); j++) {
            JSONObject p = progressArr.getJSONObject(j);
            if (courseId.equals(p.optString("courseId"))) {
                courseProgress = p;
                break;
            }
        }

        // If courseProgress does not exist, create it
        if (courseProgress == null) {
            courseProgress = new JSONObject();
            courseProgress.put("courseId", courseId);
            courseProgress.put("lessonsProgress", new JSONArray());
            courseProgress.put("completedLessons", new JSONArray());
            progressArr.put(courseProgress);
        }

        // Ensure lessonsProgress exists
        JSONArray lessonsProgress = courseProgress.optJSONArray("lessonsProgress");
        if (lessonsProgress == null) {
            lessonsProgress = new JSONArray();
            courseProgress.put("lessonsProgress", lessonsProgress);
        }

        // Update or add LessonProgress
        JSONObject lpObj = null;
        for (int k = 0; k < lessonsProgress.length(); k++) {
            JSONObject l = lessonsProgress.getJSONObject(k);
            if (lessonId.equals(l.optString("lessonId"))) {
                lpObj = l;
                break;
            }
        }

        if (lpObj == null) {
            lpObj = new JSONObject();
            lpObj.put("lessonId", lessonId);
            lpObj.put("score", 0);
            lpObj.put("attempts", 0);
            lpObj.put("passed", false);
            lessonsProgress.put(lpObj);
        }

        // Update stats
        lpObj.put("score", score);
        lpObj.put("attempts", lpObj.optInt("attempts") + 1);
        lpObj.put("passed", passed);

        // Ensure completedLessons exists
        JSONArray completedLessons = courseProgress.optJSONArray("completedLessons");
        if (completedLessons == null) {
            completedLessons = new JSONArray();
            courseProgress.put("completedLessons", completedLessons);
        }

        // Mark lesson as completed if passed
        if (passed && !completedLessons.toList().contains(lessonId)) {
            completedLessons.put(lessonId);
        }

        break; // Found the student, no need to loop further
    }

    JsonDatabaseManager.saveUsers(users);
}

      public void recordQuizAttempt(String studentId, String courseId, String lessonId, int score, boolean passed) {
    submitQuiz(studentId, courseId, lessonId, score, passed);
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
    Student s = getStudentById(studentId);
    if (s == null) return null;

    JSONArray progressArr = JsonDatabaseManager.loadUsers(); // find student
    for (int i = 0; i < progressArr.length(); i++) {
        JSONObject u = progressArr.getJSONObject(i);
        if (!u.getString("userId").equals(studentId)) continue;

        JSONArray coursesProgress = u.optJSONArray("progress");
        if (coursesProgress == null) return null;

        for (int j = 0; j < coursesProgress.length(); j++) {
            JSONObject courseP = coursesProgress.getJSONObject(j);
            if (!courseId.equals(courseP.optString("courseId"))) continue;

            JSONArray lessonsProgress = courseP.optJSONArray("lessonsProgress");
            if (lessonsProgress == null) return null;

            for (int k = 0; k < lessonsProgress.length(); k++) {
                JSONObject lp = lessonsProgress.getJSONObject(k);
                if (lessonId.equals(lp.optString("lessonId"))) {
                    LessonProgress lessonProgress = new LessonProgress();
                    lessonProgress.setAttempts(lp.optInt("attempts", 0));
                    lessonProgress.setScore(lp.optInt("score", 0));
                    lessonProgress.setPassed(lp.optBoolean("passed", false));
                    lessonProgress.setLastScore(lp.optDouble("score", 0));
                    return lessonProgress;
                }
            }
        }
    }
    return null;
}



}
