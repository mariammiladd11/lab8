/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONObject;

public class Certificate {

    private String studentId;
    private String courseId;
    private String certificateId;
    private String issueDate;
    private String pdfPath;

    
    public Certificate(String studentId, String courseId, String pdfOutputDir) 
            throws IOException, DocumentException {

        this.studentId = studentId;
        this.courseId = courseId;
        this.certificateId = "CERT-" + studentId + "-" + courseId;
        this.issueDate = java.time.Instant.now().toString();

        
        File dir = new File(pdfOutputDir);
        if (!dir.exists()) dir.mkdirs();

        
        this.pdfPath = pdfOutputDir + File.separator + certificateId + ".pdf";

        
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(this.pdfPath));

        document.open();
        document.add(new Paragraph("Certificate of Completion"));
        document.add(new Paragraph("Student ID: " + studentId));
        document.add(new Paragraph("Course ID: " + courseId));
        document.add(new Paragraph("Issued: " + this.issueDate));
        document.close();
    }

    
    public String getCertificateId() { return certificateId; }
    public String getIssueDate() { return issueDate; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getPdfPath() { return pdfPath; }

    
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("certificateId", certificateId);
        obj.put("studentId", studentId);
        obj.put("courseId", courseId);
        obj.put("issueDate", issueDate);
        obj.put("pdfPath", pdfPath);
        return obj;
    }

    
    public static Certificate fromJson(JSONObject json) {
        Certificate cert = new Certificate(); // uses private empty constructor

        cert.certificateId = json.optString("certificateId");
        cert.studentId = json.optString("studentId");
        cert.courseId = json.optString("courseId");
        cert.issueDate = json.optString("issueDate");
        cert.pdfPath = json.optString("pdfPath");

        return cert;
    }

   
    private Certificate() { }
}
