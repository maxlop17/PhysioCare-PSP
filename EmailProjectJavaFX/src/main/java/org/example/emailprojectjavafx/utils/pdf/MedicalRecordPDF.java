package org.example.emailprojectjavafx.utils.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.utils.Utils;

public class MedicalRecordPDF {

    public static void printMedicalRecord(String filename, Record record, Patient patient) {
        try {
            PdfWriter writer = new PdfWriter("src/main/resources/records/" + filename + ".pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("MEDICAL RECORD"));
            document.add(new Paragraph("Id: " + record.getId()));
            document.add(new Paragraph("Patient: " + patient.getName() + " " + patient.getSurname()));
            document.add(new Paragraph("Insurance Number: " + patient.getInsuranceNumber()));
            document.add(new Paragraph("Date of birth: " + patient.getBirthDate()));
            document.add(new Paragraph("Medical Record: " + record.getMedicalRecord()));
            document.close();

            System.out.println("PDF Created");
        } catch (Exception e) {
            Utils.showAlert("Error", "There was an error creating the document", 2);
        }
    }
}
