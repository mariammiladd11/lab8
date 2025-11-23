/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author MALAK
 */
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CertificateManager {
     private JSONArray certificates = new JSONArray();
     
     public void addCertificate(String certificateName) {
        if (certificateName == null || certificateName.isEmpty()) {
            System.out.println("Invalid certificate name.");
            return;
        }

        JSONObject cert = new JSONObject();
        cert.put("name", certificateName);

        certificates.put(cert);
        System.out.println("Certificate added: " + certificateName);
    }
    
     public void removeCertificate(String certificateName) {
        for (int i = 0; i < certificates.length(); i++) {
            JSONObject cert = certificates.getJSONObject(i);
            if (cert.getString("name").equals(certificateName)) {
                certificates.remove(i);
                System.out.println("Certificate removed: " + certificateName);
                return;
            }
        }
        System.out.println("Certificate not found.");
    }
    
    
    
    public static void markLessonCompleted(String studentId, String courseId, String lessonId) {
        JSONArray users = JsonDatabaseManager.loadUsers();
        boolean changed = false;

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

            break; 
        }

        if (changed) {
            JsonDatabaseManager.saveUsers(users);
        }
    }

    //Check whether a student has completed ALL lessons of a course.
    
    public static boolean isCourseCompleted(String studentId, String courseId) {
        JSONObject course = JsonDatabaseManager.getCourseById(courseId);
        if (course == null) return false;

        JSONArray lessons = course.optJSONArray("lessons");
        if (lessons == null || lessons.length() == 0) return false;

        // collect student's completed lessons for this course
        JSONArray users = JsonDatabaseManager.loadUsers();
        List<String> completedList = new ArrayList<>();

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!u.optString("userId").equals(studentId)) continue;

            JSONArray progress = u.optJSONArray("progress");
            if (progress == null) break;

            for (int j = 0; j < progress.length(); j++) {
                JSONObject p = progress.getJSONObject(j);
                if (courseId.equals(p.optString("courseId"))) {
                    JSONArray cl = p.optJSONArray("completedLessons");
                    if (cl != null) {
                        for (int k = 0; k < cl.length(); k++) {
                            completedList.add(cl.getString(k));
                        }
                    }
                    break;
                }
            }
            break;
        }

        // verify every lessonId is in completedList
        for (int i = 0; i < lessons.length(); i++) {
            JSONObject l = lessons.getJSONObject(i);
            String lessonId = l.optString("lessonId");
            if (!completedList.contains(lessonId)) return false;
        }

        return true;
    }

public static Certificate generateCertificate(String studentId, String courseId) throws IOException, DocumentException {
    String pdfOutputDir = "certificates"; // default folder
    return generateCertificate(studentId, courseId, pdfOutputDir);
}
    public static Certificate generateCertificate(String studentId, String courseId, String pdfOutputDir) throws IOException, DocumentException {
        // Ensure output directory exists
        File dir = new File(pdfOutputDir);
        if (!dir.exists()) {
    // If it doesn't exist, create it (including any parent directories)
    boolean created = dir.mkdirs();
    if (!created) {
        System.out.println("Failed to create directory: " + pdfOutputDir);
        // Optionally throw an exception or handle the error
    }
}


    
     //Generate a JSON certificate for a student who completed a course.
    public static JSONObject generateCertificate(String studentId, String courseId) {
        if (!isCourseCompleted(studentId, courseId)) {
            throw new IllegalStateException("Student has not completed the course");
        }


        Certificate cert = new Certificate(studentId, courseId);
        JSONObject certJson = (JSONObject) cert.toJson();

        // store certificate in user's record
        JSONArray users = JsonDatabaseManager.loadUsers();
        boolean userSaved = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!u.optString("userId").equals(studentId)) continue;

            JSONArray certs = u.optJSONArray("certificates");
            if (certs == null) {
                certs = new JSONArray();
                u.put("certificates", certs);
            }
            certs.put(certJson);
            userSaved = true;
            break;
        }
        if (userSaved) JsonDatabaseManager.saveUsers(users);

        JSONArray courses = JsonDatabaseManager.loadCourses();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (!c.optString("courseId").equals(courseId)) continue;

            JSONArray refs = c.optJSONArray("certificates");
            if (refs == null) {
                refs = new JSONArray();
                c.put("certificates", refs);
            }

            JSONObject ref = new JSONObject();
            ref.put("certificateId", cert.getCertificateId());
            ref.put("studentId", studentId);
            ref.put("issueDate", cert.getIssueDate());

            refs.put(ref);
            break;
        }
        JsonDatabaseManager.saveCourses(courses);

        return certJson;
    }

   //Return list of Certificate objects for a user (reads from users.json) 
    public static ArrayList<Certificate> getCertificatesForUser(String studentId) {
        ArrayList<Certificate> list = new ArrayList<>();
        JSONArray users = JsonDatabaseManager.loadUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (!u.optString("userId").equals(studentId)) continue;

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
    
}

}