package com.shreerangatraders.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.shreerangatraders.entity.Sales;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public byte[] generateSalesPdf(List<Sales> salesList, String customerName, LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            Paragraph title = new Paragraph("SHREE RANGA TRADERS")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Sales Report")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(subtitle);

            // Add search criteria
            if (customerName != null && !customerName.isEmpty()) {
                document.add(new Paragraph("Customer: " + customerName)
                        .setFontSize(12)
                        .setBold());
            }

            if (startDate != null || endDate != null) {
                String dateRange = "Date Range: ";
                if (startDate != null && endDate != null) {
                    dateRange += startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER);
                } else if (startDate != null) {
                    dateRange += "From " + startDate.format(DATE_FORMATTER);
                } else {
                    dateRange += "Until " + endDate.format(DATE_FORMATTER);
                }
                document.add(new Paragraph(dateRange)
                        .setFontSize(12)
                        .setBold()
                        .setMarginBottom(15));
            }

            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Create table
            //float[] columnWidths = {15, 20, 20, 10, 15, 20};
            float[] columnWidths = {15, 20, 10, 10, 15};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Add table headers
            //String[] headers = {"Date", "Customer Name", "Item", "Bags", "Amount", "Payment Type"};
            String[] headers = {"Date", "Customer Name", "Item", "Bags", "Amount"};
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Add data rows and calculate totals
            int totalBags = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Sales sale : salesList) {
                table.addCell(new Cell().add(new Paragraph(sale.getSaleDate().format(DATE_FORMATTER)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(sale.getCustomerName())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(sale.getItem())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getBags())))
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", sale.getAmount())))
                        .setTextAlignment(TextAlignment.RIGHT));
                /*table.addCell(new Cell().add(new Paragraph(sale.getPaymentType().toString()))
                        .setTextAlignment(TextAlignment.CENTER));*/

                totalBags += sale.getBags();
                totalAmount = totalAmount.add(sale.getAmount());
            }

            // Add totals row
            table.addCell(new Cell(1, 3)
                    .add(new Paragraph("TOTALS:").setBold())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(totalBags)).setBold())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell()
                    .add(new Paragraph(String.format("%.2f", totalAmount)).setBold())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            /*table.addCell(new Cell()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));*/

            document.add(table);

            // Add summary section
            document.add(new Paragraph("\nSummary:")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20));
            /*document.add(new Paragraph("Total Records: " + salesList.size())
                    .setFontSize(12));*/
            document.add(new Paragraph("Total Bags: " + totalBags)
                    .setFontSize(12));
            document.add(new Paragraph("Total Amount: ₹" + String.format("%.2f", totalAmount))
                    .setFontSize(12));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return baos.toByteArray();
    }
}

