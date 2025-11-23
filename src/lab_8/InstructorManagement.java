package lab_8;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sarahkhaled
 */
public class InstructorManagement {
    
    public static String createCourse(String instructorId, String title, String description) {
        if(title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");

        JSONArray courses = JsonDatabaseManager.loadCourses();
        //haye3mel unique course id
        String courseId = "C" + (courses.length() + 1);
        JSONObject newCourse = new JSONObject();
        newCourse.put("courseId", courseId);
        newCourse.put("title", title);
        newCourse.put("description", description);
        newCourse.put("instructorId", instructorId);
        newCourse.put("lessons", new JSONArray());
        newCourse.put("students", new JSONArray());

        courses.put(newCourse);
        JsonDatabaseManager.saveCourses(courses);

        addCourseToInstructor(instructorId, courseId);

        return courseId;
    }
    
   public static void addCourseToInstructor(String instructorId, String courseId) {
    JSONArray users = JsonDatabaseManager.loadUsers();

    for (int i=0; i<users.length(); i++) {
        JSONObject u = users.getJSONObject(i);
        if (u.getString("userId").equals(instructorId)) {
            u.getJSONArray("createdCourses").put(courseId);
            break;
        }
    }

    JsonDatabaseManager.saveUsers(users);
}
 public static void editCourse(String courseId, String newTitle, String newDescription) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c=courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            c.put("title", newTitle);
            c.put("description", newDescription);
            break;
        }
    }
    JsonDatabaseManager.saveCourses(courses);
}
   public static void deleteCourse(String instructorId, String courseId) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        if (courses.getJSONObject(i).getString("courseId").equals(courseId)) {
            courses.remove(i);
            break;
        }
    }

    JsonDatabaseManager.saveCourses(courses);

    removeCourseFromInstructor(instructorId, courseId);
}
private static void removeCourseFromInstructor(String instructorId, String courseId) {
    JSONArray users = JsonDatabaseManager.loadUsers();

    for (int i = 0; i < users.length(); i++) {
        JSONObject u = users.getJSONObject(i);

        if (u.getString("userId").equals(instructorId)) {
            JSONArray arr = u.getJSONArray("createdCourses");

            for (int j = 0; j < arr.length(); j++) {
                if (arr.getString(j).equals(courseId)) {
                    arr.remove(j);
                    break;
                }
            }
        }
    }

    JsonDatabaseManager.saveUsers(users);
}
public static void addLesson(String courseId, String title, String content) {
    if(title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            JSONArray lessons = c.getJSONArray("lessons");

            String lessonId = "L" + (lessons.length() + 1);

            JSONObject newLesson = new JSONObject();
            newLesson.put("lessonId", lessonId);
            newLesson.put("title", title);
            
            newLesson.put("content", content);

            lessons.put(newLesson);
            break;
        }
    }

    JsonDatabaseManager.saveCourses(courses);
}
public static void deleteLesson(String courseId, String lessonId) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            JSONArray lessons = c.getJSONArray("lessons");

            for (int j = 0; j < lessons.length(); j++) {
                if (lessons.getJSONObject(j).getString("lessonId").equals(lessonId)) {
                    lessons.remove(j);
                    break;
                }
            }
        }
    }

    JsonDatabaseManager.saveCourses(courses);
}
public static ArrayList<String> getEnrolledStudents(String courseId) {
    ArrayList<String> list = new ArrayList<>();

    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            JSONArray students = c.getJSONArray("students");

            for (int j = 0; j < students.length(); j++) {
                list.add(students.getString(j));
            }
            break;
        }
    }

    return list;
}
public static void createQuizForLesson(String courseId, String lessonId) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            JSONArray lessons = c.getJSONArray("lessons");

            for (int j = 0; j < lessons.length(); j++) {
                JSONObject l = lessons.getJSONObject(j);

                if (l.getString("lessonId").equals(lessonId)) {
                    if (!l.has("quiz")) {
                        JSONObject quiz = new JSONObject();
                        quiz.put("questions", new JSONArray());
                        l.put("quiz", quiz);
                    }
                    break;
                }
            }
        }
    }

    JsonDatabaseManager.saveCourses(courses);
}
public static void addQuestion(String courseId, String lessonId,
                               String questionText, List<String> options,
                               int correctIndex) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);

        if (c.getString("courseId").equals(courseId)) {
            JSONArray lessons = c.getJSONArray("lessons");

            for (int j = 0; j < lessons.length(); j++) {
                JSONObject l = lessons.getJSONObject(j);

                if (l.getString("lessonId").equals(lessonId)) {

                    JSONObject quiz = l.optJSONObject("quiz");
                    if (quiz == null) {
                        quiz = new JSONObject();
                        quiz.put("questions", new JSONArray());
                        l.put("quiz", quiz);
                    }

                    JSONArray questions = quiz.getJSONArray("questions");

                    JSONObject newQ = new JSONObject();
                    newQ.put("questionId", "Q" + (questions.length() + 1));
                    newQ.put("questionText", questionText);
                    newQ.put("options", new JSONArray(options));
                    newQ.put("correctIndex", correctIndex);

                    questions.put(newQ);
                    break;
                }
            }
        }
    }

    JsonDatabaseManager.saveCourses(courses);
}

}
