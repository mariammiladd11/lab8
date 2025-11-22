/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;

/**
 *
 * @author MALAK
 */
public class TestPDF {
     public static void main(String[] args) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("certificate.pdf"));
            document.open();
            document.add(new Paragraph("Certificate of Completion"));
            document.add(new Paragraph("Student ID: 12345"));
            document.add(new Paragraph("Course ID: 67890"));
            document.add(new Paragraph("Issued: " + java.time.Instant.now()));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
