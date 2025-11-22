/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author MALAK
 */
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CertificateManager {

    private JSONArray certificates = new JSONArray();

    public void addCertificate(String certificateName) {
        if (certificateName == null || certificateName.isEmpty()) return;
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

        for (int i = 0; i < lessons.length(); i++) {
            JSONObject l = lessons.getJSONObject(i);
            if (!completedList.contains(l.optString("lessonId"))) return false;
        }

        return true;
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

        String fileName = "CERT-" + studentId + "-" + courseId + ".pdf";
        String pdfPath = pdfOutputDir + File.separator + fileName;

        
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new Paragraph("Certificate of Completion"));
    document.add(new Paragraph("Student ID: " + studentId));
    document.add(new Paragraph("Course ID: " + courseId));
    document.add(new Paragraph("Issued: " + Instant.now()));
    document.close();

        // Create certificate object
        Certificate cert = new Certificate(studentId, courseId, pdfPath);

        // Save certificate in users.json
        JSONArray users = JsonDatabaseManager.loadUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);
            if (u.getString("userId").equals(studentId)) {
                JSONArray certArr = u.optJSONArray("certificates");
                if (certArr == null) {
                    certArr = new JSONArray();
                    u.put("certificates", certArr);
                }
                certArr.put(cert.toJson());
                break;
            }
        }
        JsonDatabaseManager.saveUsers(users);

        // Optionally save in courses.json
        JSONArray courses = JsonDatabaseManager.loadCourses();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (c.getString("courseId").equals(courseId)) {
                JSONArray courseCerts = c.optJSONArray("certificates");
                if (courseCerts == null) {
                    courseCerts = new JSONArray();
                    c.put("certificates", courseCerts);
                }
                courseCerts.put(cert.toJson());
                break;
            }
        }
        JsonDatabaseManager.saveCourses(courses);

        return cert;
    }

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