package org.example.emailprojectjavafx.utils.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.FileNotFoundException;
import java.util.List;

public class CreateAppointmentsPDF {

    public static void createPDF(String dest, List<Appointment> appointments) {
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Appointments").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            float[] columnWidths = {2, 2, 6};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Cabecera
            table.addHeaderCell("Date").setBold();
            table.addHeaderCell("Physio").setBold();
            table.addHeaderCell("Diagnosis").setBold();

            for (Appointment appointment : appointments) {
                table.addCell(appointment.getDate().toString());

                ServiceUtils.makePetition(
                        new GenericPetition<>("physios", "", "GET", null, PhysioResponse.class,
                                response ->
                                        table.addCell(response.getPhysio().getName() + " " + response.getPhysio().getSurname()),
                                "Failed to fetch physio")
                );
                table.addCell(appointment.getDiagnosis());
            }

            document.add(table);
            document.close();
            System.out.println("Appointments PDF created at: " + dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
