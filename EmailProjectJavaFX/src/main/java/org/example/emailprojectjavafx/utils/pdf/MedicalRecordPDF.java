package org.example.emailprojectjavafx.utils.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.ftp.SftpUtils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.File;

public class MedicalRecordPDF {

    private static void makeRecordPdf(String filename, Record record, Patient patient) {
        try {
            PdfWriter writer = new PdfWriter("EmailProjectJavaFX/src/main/resources/records/" + filename + ".pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("MEDICAL RECORD"));
            document.add(new Paragraph("Id: " + record.getId()));
            document.add(new Paragraph("Patient: " + patient.getName() + " " + patient.getSurname()));
            document.add(new Paragraph("Insurance Number: " + patient.getInsuranceNumber()));
            document.add(new Paragraph("Date of birth: " + patient.getBirthDate()));
            document.add(new Paragraph("Medical Record: " + record.getMedicalRecord()));
            document.close();

            //SftpUtils.uploadFTP();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showAlert("Error", "There was an error creating the document", 2);
        }
    }

    public static void printPatientsRecords(){
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "", "GET", null, RecordListResponse.class,
                recordListResponse -> {
                    recordListResponse.getRecords().forEach(record -> {
                        ServiceUtils.makePetition(new GenericPetition<>(
                            "patients", record.getPatient(), "GET", null, PatientResponse.class,
                            patientResponse -> {
                                makeRecordPdf(patientResponse.getPatient().getId(), record, patientResponse.getPatient());
                            }, "Failed to fetch patients"
                        ));
                    });
                }, "Failed to fetch records"
        ));
    }
}
