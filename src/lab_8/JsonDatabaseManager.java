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

    public static void saveUsers(JSONArray usersArray) {
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            fw.write(usersArray.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (arr == null) return lessons;

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
public static void markLessonCompleted(String courseId, String lessonId) {
    List<Lesson> lessons = getLessons(courseId);

    for (Lesson L : lessons) {
        if (L.getLessonId().equals(lessonId)) {
            L.setCompleted(true);
            break;
        }
    }

    saveLessons(courseId, lessons);
}

}

