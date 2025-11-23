/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabaseManager {

    private static final String USERS_FILE = "users.json";
    private static final String COURSES_FILE = "courses.json";
    private static final String LESSONS_FILE = "lessons.json";

    public static JSONArray loadUsers() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            return new JSONArray(content);
        } catch (Exception e) {
            return new JSONArray(); // file not found → return empty
        }
    }

    public static void saveUsers(JSONArray users) {
        try (FileWriter file = new FileWriter("users.json")) {
            file.write(users.toString(4));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Users JSON after save: " + users.toString(4));

    }

    public static JSONObject findUserByEmail(String email) {
        JSONArray users = loadUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (u.getString("email").equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public static JSONArray loadCourses() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(COURSES_FILE)));
            return new JSONArray(content);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void saveCourses(JSONArray coursesArray) {
        try (FileWriter fw = new FileWriter(COURSES_FILE)) {
            fw.write(coursesArray.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getCourseById(String courseId) {
        JSONArray courses = loadCourses();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (c.getString("courseId").equals(courseId)) {
                return c;
            }
        }
        return null;
    }

    public static boolean courseIdExists(String courseId) {
        return getCourseById(courseId) != null;
    }

    public static JSONArray loadLessons() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(LESSONS_FILE)));
            return new JSONArray(content);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void saveLessons(JSONArray lessonsArray) {
        try (FileWriter fw = new FileWriter(LESSONS_FILE)) {
            fw.write(lessonsArray.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getLessonsByCourseId(String courseId) {
        JSONArray allLessons = loadLessons();
        JSONArray result = new JSONArray();
        for (int i = 0; i < allLessons.length(); i++) {
            JSONObject lesson = allLessons.getJSONObject(i);
            if (lesson.getString("courseId").equals(courseId)) {
                result.put(lesson);
            }
        }
        return result;
    }

    public static JSONObject getLessonById(String lessonId) {
        JSONArray allLessons = loadLessons();
        for (int i = 0; i < allLessons.length(); i++) {
            JSONObject lesson = allLessons.getJSONObject(i);
            if (lesson.getString("lessonId").equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    public static List<Lesson> getLessons(String courseId) {
        List<Lesson> lessons = new ArrayList<>();
        JSONArray courses = loadCourses();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);

            if (c.getString("courseId").equals(courseId)) {
                JSONArray arr = c.optJSONArray("lessons");
                if (arr == null) {
                    return lessons;
                }

                for (int j = 0; j < arr.length(); j++) {
                    lessons.add(Lesson.fromJson(arr.getJSONObject(j)));
                }
            }
        }
        return lessons;
    }

    public static void saveLessons(String courseId, List<Lesson> lessons) {
        JSONArray courses = loadCourses();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);

            if (c.getString("courseId").equals(courseId)) {

                JSONArray arr = new JSONArray();
                for (Lesson L : lessons) {
                    arr.put(L.toJson());
                }

                c.put("lessons", arr);
                saveCourses(courses);
                return;
            }
        }
    }

    public static void markLessonCompleted(String studentId, String courseId, String lessonId) {
        JSONArray users = JsonDatabaseManager.loadUsers();
        boolean changed = false;

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!u.optString("userId").equals(studentId)) {
                continue;
            }

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

            // Debug print before adding
            System.out.println("Before adding: " + completed);

            boolean already = false;
            for (int k = 0; k < completed.length(); k++) {
                if (lessonId.equals(completed.getString(k))) {
                    already = true;
                    break;
                }
            }

            if (!already) {
                completed.put(lessonId);
                changed = true;
            }

            // Debug print after adding
            System.out.println("After adding: " + completed);

            break;
        }

        if (changed) {
            JsonDatabaseManager.saveUsers(users);
            System.out.println("Progress saved!");
        }
    }

    public static void setCourseStatus(String courseId, String newStatus) {
        JSONArray courses = loadCourses();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (c.getString("courseId").equals(courseId)) {
                c.put("status", newStatus);
                break;
            }
        }
        saveCourses(courses);
    }

    public static void updateCourseStatus(String courseId, String status) {
        JSONArray courses = loadCourses();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (c.getString("courseId").equals(courseId)) {
                c.put("status", status);
                break;
            }
        }
        saveCourses(courses);
    }

    public static void recordStudentLesson(String courseId, String lessonId, String studentId, double score, boolean completed) {
        List<Lesson> lessons = getLessons(courseId);

        for (Lesson l : lessons) {
            if (l.getLessonId().equals(lessonId)) {
                l.recordStudentProgress(studentId, score, completed);
                break;
            }
        }

        saveLessons(courseId, lessons);
        JSONArray users = loadUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (u.getString("userId").equals(studentId)) {
                JSONObject progress = u.optJSONObject("progress");
                if (progress == null) {
                    progress = new JSONObject();
                }

                JSONArray completedArr = progress.optJSONArray(courseId);
                if (completedArr == null) {
                    completedArr = new JSONArray();
                }

                if (!completedArr.toList().contains(lessonId)) {
                    completedArr.put(lessonId);
                }
                progress.put(courseId, completedArr);
                u.put("progress", progress);
                break;
            }
        }
        saveUsers(users);
    }

}
