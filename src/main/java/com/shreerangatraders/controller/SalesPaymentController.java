package com.shreerangatraders.controller;

import com.shreerangatraders.entity.SalesPayment;
import com.shreerangatraders.entity.PaymentHistory;
import com.shreerangatraders.service.SalesPaymentService;
import com.shreerangatraders.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales-payments")
@RequiredArgsConstructor
public class SalesPaymentController {
    
    private final SalesPaymentService salesPaymentService;
    private final PdfExportService pdfExportService;

    @GetMapping("/balances")
    public ResponseEntity<List<SalesPayment>> getAllBalances() {
        List<SalesPayment> balances = salesPaymentService.getAllBalances();
        return ResponseEntity.ok(balances);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<PaymentHistory>> getAllHistory() {
        List<PaymentHistory> history = salesPaymentService.getAllHistory();
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/{customerName}")
    public ResponseEntity<List<PaymentHistory>> getHistoryByCustomer(@PathVariable String customerName) {
        List<PaymentHistory> history = salesPaymentService.getHistoryByCustomer(customerName);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/search")
    public ResponseEntity<List<PaymentHistory>> searchHistory(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        PaymentHistory.TransactionType transactionType = null;
        if (type != null && !type.isEmpty()) {
            try {
                transactionType = PaymentHistory.TransactionType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<PaymentHistory> history = salesPaymentService.searchHistory(customerName, transactionType, startDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/payment/{id}")
    public ResponseEntity<PaymentHistory> getPaymentById(@PathVariable Long id) {
        PaymentHistory payment = salesPaymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/payment/{id}")
    public ResponseEntity<Void> updatePayment(@PathVariable Long id, @RequestBody Map<String, Object> paymentData) {
        BigDecimal amount = new BigDecimal(paymentData.get("amount").toString());
        LocalDate paymentDate = LocalDate.parse(paymentData.get("paymentDate").toString());
        String note = (String) paymentData.get("note");

        salesPaymentService.updatePayment(id, amount, paymentDate, note);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/payment/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        salesPaymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> recordPayment(@RequestBody Map<String, Object> paymentData) {
        String customerName = (String) paymentData.get("customerName");
        BigDecimal amount = new BigDecimal(paymentData.get("amount").toString());
        LocalDate paymentDate = LocalDate.parse(paymentData.get("paymentDate").toString());
        String note = (String) paymentData.get("note");
        
        salesPaymentService.recordPayment(customerName, amount, paymentDate, note);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/adjustment")
    public ResponseEntity<Void> recordAdjustment(@RequestBody Map<String, Object> adjustmentData) {
        String customerName = (String) adjustmentData.get("customerName");
        BigDecimal amount = new BigDecimal(adjustmentData.get("amount").toString());
        LocalDate adjustmentDate = LocalDate.parse(adjustmentData.get("adjustmentDate").toString());
        String note = (String) adjustmentData.get("note");
        
        salesPaymentService.recordAdjustment(customerName, amount, adjustmentDate, note);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportSalesPaymentToPdf(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        PaymentHistory.TransactionType transactionType = null;
        if (type != null && !type.isEmpty()) {
            try {
                transactionType = PaymentHistory.TransactionType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<PaymentHistory> history = salesPaymentService.searchHistory(customerName, transactionType, startDate);
        byte[] pdfBytes = pdfExportService.generateSalesPaymentPdf(history, customerName, transactionType);

        String filename = "sales_payment_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
