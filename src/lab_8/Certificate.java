/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author MALAK
 */
public class Certificate {
    private String certificateId;
    private String studentId;
    private String courseId;
    private String issueDate;

    public Certificate(String certificateId, String studentId, String courseId, String issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.issueDate = issueDate;
    }
     public String getCertificateId() { return certificateId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getIssueDate() { return issueDate; }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("certificateId", certificateId);
        obj.put("studentId", studentId);
        obj.put("courseId", courseId);
        obj.put("issueDate", issueDate);
        return obj;
    }

    public static Certificate fromJson(JSONObject obj) {
        return new Certificate(
            obj.getString("certificateId"),
            obj.getString("studentId"),
            obj.getString("courseId"),
            obj.getString("issueDate")
        );
    }
}

