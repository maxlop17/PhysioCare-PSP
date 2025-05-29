package org.example.emailprojectjavafx.utils.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.example.emailprojectjavafx.models.Appointment.Appointment;

import java.util.List;

public class CreateTableInPdf {

    public static void createTableInPdf(List<Appointment> appointments, String dest){
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // Add header to document
            document.add(new Paragraph("PhysioCare\n123 Health St., Wellness City, PC 45678")
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));
            // Define column widths; here we create a table with 3 column
            float[] columnWidths = {1, 5}; // Ratio of column widths
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // Headers of the table
            table.addHeaderCell("Number");
            table.addHeaderCell("Appointments");
            for(int i = 0; i < appointments.size(); i++){
                table.addCell(i+1 + "");
                table.addCell(appointments.get(i).toString());
            }
            document.add(table);
            document.close();
            System.out.println("Table PDF created.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}