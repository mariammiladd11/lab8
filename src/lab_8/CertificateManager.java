/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author MALAK
 */
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;


public class CertificateManager {

    
    public static void markLessonCompleted(String studentId, String courseId, String lessonId) {
        JSONArray users = JsonDatabaseManager.loadUsers();
        boolean changed = false;

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
                courseProgress.put("completedLessons", new JSONArray());
                progress.put(courseProgress);
            }

            JSONArray completed = courseProgress.getJSONArray("completedLessons");
            if (!completed.toList().contains(lessonId)) {
                completed.put(lessonId);
                changed = true;
            }

            break; 
        }

        if (changed) JsonDatabaseManager.saveUsers(users);
    }

    
    public static boolean isCourseCompleted(String studentId, String courseId) {
        JSONObject course = JsonDatabaseManager.getCourseById(courseId);
        if (course == null) return false;

        JSONArray lessons = course.optJSONArray("lessons");
        if (lessons == null || lessons.length() == 0) return false;

        JSONArray users = JsonDatabaseManager.loadUsers();
        JSONArray completedLessons = new JSONArray();

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (!studentId.equals(user.optString("userId"))) continue;

            JSONArray progress = user.optJSONArray("progress");
            if (progress != null) {
                for (int j = 0; j < progress.length(); j++) {
                    JSONObject p = progress.getJSONObject(j);
                    if (courseId.equals(p.optString("courseId"))) {
                        JSONArray cl = p.optJSONArray("completedLessons");
                        if (cl != null) completedLessons = cl;
                        break;
                    }
                }
            }
            break; 
        }

        
        System.out.println("Course ID: " + courseId);
        System.out.println("Lessons in course:");
        for (int i = 0; i < lessons.length(); i++) {
            System.out.println("  " + lessons.getJSONObject(i).optString("lessonId"));
        }
        System.out.println("Completed lessons for user " + studentId + ":");
        for (int i = 0; i < completedLessons.length(); i++) {
            System.out.println("  " + completedLessons.getString(i));
        }

        for (int i = 0; i < lessons.length(); i++) {
            String lessonId = lessons.getJSONObject(i).optString("lessonId");
            if (!completedLessons.toList().contains(lessonId)) return false;
        }

        return true;
    }

   
    public static JSONObject generateCertificateJson(String studentId, String courseId)
        throws IOException, DocumentException {

    if (!isCourseCompleted(studentId, courseId)) {
        throw new IllegalStateException("Student has not completed the course: " + courseId);
    }

    String pdfOutputDir = "certificates";
    File dir = new File(pdfOutputDir);
    if (!dir.exists()) dir.mkdirs();

    
    JSONArray users = JsonDatabaseManager.loadUsers();
    JSONObject userObj = null;

    for (int i = 0; i < users.length(); i++) {
        JSONObject u = users.getJSONObject(i);
        if (studentId.equals(u.optString("userId"))) {
            userObj = u;
            break;
        }
    }

    if (userObj == null) {
        throw new IllegalStateException("Student not found: " + studentId);
    }

    JSONArray certs = userObj.optJSONArray("certificates");
    if (certs == null) {
        certs = new JSONArray();
        userObj.put("certificates", certs);
    }

    for (int i = 0; i < certs.length(); i++) {
        JSONObject existing = certs.getJSONObject(i);

        if (courseId.equals(existing.optString("courseId")) &&
            studentId.equals(existing.optString("studentId"))) {

            System.out.println("Certificate already exists — returning existing certificate");
            return existing;  
        }
    }

    
    Certificate cert = new Certificate(studentId, courseId, pdfOutputDir);
    JSONObject certJson = cert.toJson();
    certJson.put("courseId", courseId);  

    certs.put(certJson);
    JsonDatabaseManager.saveUsers(users);

   
    JSONArray courses = JsonDatabaseManager.loadCourses();
    for (int i = 0; i < courses.length(); i++) {
        JSONObject c = courses.getJSONObject(i);
        if (courseId.equals(c.optString("courseId"))) {

            JSONArray refs = c.optJSONArray("certificates");
            if (refs == null) {
                refs = new JSONArray();
                c.put("certificates", refs);
            }

            JSONObject ref = new JSONObject();
            ref.put("certificateId", cert.getCertificateId());
            ref.put("studentId", studentId);
            ref.put("issueDate", cert.getIssueDate());
            ref.put("courseId", courseId);

            refs.put(ref);
            break;
        }
    }
    JsonDatabaseManager.saveCourses(courses);

    System.out.println("New certificate generated");
    return certJson;
}
    
    public static ArrayList<Certificate> getCertificatesForUser(String studentId) {
        ArrayList<Certificate> list = new ArrayList<>();
        JSONArray users = JsonDatabaseManager.loadUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!studentId.equals(u.optString("userId"))) continue;

            JSONArray certArr = u.optJSONArray("certificates");
            if (certArr != null) {
                for (int j = 0; j < certArr.length(); j++) {
                    list.add(Certificate.fromJson(certArr.getJSONObject(j)));
                }
            }
            break;
        }
        return list;
    }

    
    public static ArrayList<String> getCertificateIdsForUser(String studentId) {
        ArrayList<String> list = new ArrayList<>();
        JSONArray users = JsonDatabaseManager.loadUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!studentId.equals(u.optString("userId"))) continue;

            JSONArray certArr = u.optJSONArray("certificates");
            if (certArr != null) {
                for (int j = 0; j < certArr.length(); j++) {
                    JSONObject certJson = certArr.getJSONObject(j);
                    list.add(certJson.optString("certificateId"));
                }
            }
            break;
        }
        return list;
    }
}