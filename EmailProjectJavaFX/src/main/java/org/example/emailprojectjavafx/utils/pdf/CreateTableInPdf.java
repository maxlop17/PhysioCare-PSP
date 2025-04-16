package org.example.emailprojectjavafx.utils.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

public class CreateTableInPdf {

    public static void createTableInPdf(){
        String dest = "src/main/resources/tableExample.pdf"; // File path and name
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // Define column widths; here we create a table with 3 column
            float[] columnWidths = {1, 5, 2}; // Ratio of column widths
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // Headers of the table
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Quantity");
            table.addCell("1");
            table.addCell("iPhone");
            table.addCell("10");
            table.addCell("2");
            table.addCell("iPad");
            table.addCell("15");
            table.addCell("3");
            table.addCell("MacBook");
            table.addCell("5");
            document.add(table);
            document.close();
            System.out.println("Table PDF created.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}