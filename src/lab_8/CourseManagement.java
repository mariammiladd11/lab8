/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.ArrayList;
import static lab_8.InstructorManagement.addCourseToInstructor;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author MALAK
 */
public  class CourseManagement {
  public static JSONArray browseCourses(){
  return JsonDatabaseManager.loadCourses();
  }
   public static void enrollStudent(String studentId, String courseId) {
        JSONArray courses = JsonDatabaseManager.loadCourses();
        JSONArray users = JsonDatabaseManager.loadUsers();

       
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (u.getString("userId").equals(studentId)) {

                JSONArray enrolled = u.optJSONArray("enrolledCourses");
                if (enrolled == null) {
                    enrolled = new JSONArray();
                    u.put("enrolledCourses", enrolled);
                }

                if (!enrolled.toString().contains(courseId)) {
                    enrolled.put(courseId);
                }
                break;
            }
        }

        JsonDatabaseManager.saveUsers(users);

        
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (c.getString("courseId").equals(courseId)) {

                JSONArray students = c.getJSONArray("students");
                if (!students.toString().contains(studentId)) {
                    students.put(studentId);
                }
                break;
            }
        }

        JsonDatabaseManager.saveCourses(courses);
    } 
   public static JSONArray viewLessons(String courseId) {
        JSONObject c = JsonDatabaseManager.getCourseById(courseId);

        if (c != null) {
            return c.getJSONArray("lessons");
        }
        return new JSONArray();
    }
   public static JSONObject getCourse(String courseId) {
        return JsonDatabaseManager.getCourseById(courseId);
    }
   public static ArrayList<String> getEnrolledStudents(String courseId) {
        ArrayList<String> list = new ArrayList<>();

        JSONArray courses = JsonDatabaseManager.loadCourses();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);

            if (c.getString("courseId").equals(courseId)) {
                JSONArray arr = c.getJSONArray("students");

                for (int j = 0; j < arr.length(); j++) {
                    list.add(arr.getString(j));
                }
                break;
            }
        }

        return list;
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
   
  public static void markStudentLessonCompleted(String studentId, String courseId, String lessonId) {
    CertificateManager.markLessonCompleted(studentId, courseId, lessonId);
}

public static boolean checkStudentCourseComplete(String studentId, String courseId) {
    return CertificateManager.isCourseCompleted(studentId, courseId);
} 
   
 public static JSONObject issueCertificateIfComplete(String studentId, String courseId, String pdfOutputDir) throws IOException, DocumentException {
    if (!CertificateManager.isCourseCompleted(studentId, courseId)) {
        throw new IllegalStateException("Student hasn't completed all lessons.");
    }
    return CertificateManager.generateCertificateJson(studentId, courseId);
}  
 
 public static void setCourseStatus(String courseId, String status) {
    JSONArray courses = JsonDatabaseManager.loadCourses();

    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);
        if (c.getString("courseId").equals(courseId)) {
            c.put("status", status);  // set APPROVED or REJECTED
            break;
        }
    }

    JsonDatabaseManager.saveCourses(courses);
}
public static Lesson getLesson(String courseId, String lessonId) {
    JSONObject course = JsonDatabaseManager.getCourseById(courseId);
    if (course == null) return null;

    JSONArray lessons = course.optJSONArray("lessons");
    if (lessons == null) return null;

    for (int i = 0; i < lessons.length(); i++) {
        JSONObject l = lessons.getJSONObject(i);
        if (l.getString("lessonId").equals(lessonId)) {
            Quiz quiz = null;
            if (l.has("quiz") && !l.isNull("quiz")) {
                quiz = Quiz.fromJson(l.getJSONObject("quiz")); // Make sure you have this method
            }

            return new Lesson(
                l.getString("lessonId"),
                l.getString("title"),
                l.optString("content", ""),
                quiz
            );
        }
    }

    return null;
}


   
   
}