package com.shreerangatraders.controller;

import com.shreerangatraders.entity.Payment;
import com.shreerangatraders.entity.PurchasePaymentHistory;
import com.shreerangatraders.service.PurchasePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-payments")
@RequiredArgsConstructor
public class PurchasePaymentController {
    
    private final PurchasePaymentService purchasePaymentService;
    
    @GetMapping("/balances")
    public ResponseEntity<List<Payment>> getAllBalances() {
        List<Payment> balances = purchasePaymentService.getAllBalances();
        return ResponseEntity.ok(balances);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<PurchasePaymentHistory>> getAllHistory() {
        List<PurchasePaymentHistory> history = purchasePaymentService.getAllHistory();
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/{shopName}")
    public ResponseEntity<List<PurchasePaymentHistory>> getHistoryByShop(@PathVariable String shopName) {
        List<PurchasePaymentHistory> history = purchasePaymentService.getHistoryByShop(shopName);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/payment")
    public ResponseEntity<Void> recordPayment(@RequestBody Map<String, Object> paymentData) {
        String shopName = (String) paymentData.get("shopName");
        BigDecimal amount = new BigDecimal(paymentData.get("amount").toString());
        LocalDate paymentDate = LocalDate.parse(paymentData.get("paymentDate").toString());
        String note = (String) paymentData.get("note");
        
        purchasePaymentService.recordPayment(shopName, amount, paymentDate, note);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/adjustment")
    public ResponseEntity<Void> recordAdjustment(@RequestBody Map<String, Object> adjustmentData) {
        String shopName = (String) adjustmentData.get("shopName");
        BigDecimal amount = new BigDecimal(adjustmentData.get("amount").toString());
        LocalDate adjustmentDate = LocalDate.parse(adjustmentData.get("adjustmentDate").toString());
        String note = (String) adjustmentData.get("note");
        
        purchasePaymentService.recordAdjustment(shopName, amount, adjustmentDate, note);
        return ResponseEntity.ok().build();
    }
}
