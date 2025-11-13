package com.shreerangatraders.controller;

import com.shreerangatraders.entity.Sales;
import com.shreerangatraders.service.PdfExportService;
import com.shreerangatraders.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    
    private final SalesService salesService;
    private final PdfExportService pdfExportService;

    @PostMapping
    public ResponseEntity<Sales> createSale(@RequestBody Sales sale) {
        Sales created = salesService.createSale(sale);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Sales> updateSale(@PathVariable Long id, @RequestBody Sales sale) {
        Sales updated = salesService.updateSale(id, sale);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        salesService.deleteSale(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sales> getSaleById(@PathVariable Long id) {
        Sales sale = salesService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Sales>> getRecentSales() {
        List<Sales> sales = salesService.getRecentSales();
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Sales>> searchSales(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String customerName) {
        List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportSalesToPdf(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String customerName) {

        List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
        byte[] pdfBytes = pdfExportService.generateSalesPdf(sales, customerName, startDate, endDate);

        String filename = "sales_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
