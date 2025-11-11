package com.shreerangatraders.service;

import com.shreerangatraders.entity.Payment;
import com.shreerangatraders.entity.PurchasePaymentHistory;
import com.shreerangatraders.repository.PaymentRepository;
import com.shreerangatraders.repository.PurchasePaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchasePaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PurchasePaymentHistoryRepository purchasePaymentHistoryRepository;
    
    public List<Payment> getAllBalances() {
        return paymentRepository.findAll();
    }
    
    public List<PurchasePaymentHistory> getAllHistory() {
        return purchasePaymentHistoryRepository.findAllByOrderByPaymentDateDescCreatedDateDesc();
    }
    
    public List<PurchasePaymentHistory> getHistoryByShop(String shopName) {
        return purchasePaymentHistoryRepository.findByShopNameOrderByPaymentDateDesc(shopName);
    }
    
    @Transactional
    public void recordPayment(String shopName, BigDecimal amount, LocalDate paymentDate, String note) {
        log.info("Recording payment for shop: {}, amount: {}", shopName, amount);
        
        Payment payment = paymentRepository.findByShopName(shopName)
                .orElseThrow(() -> new RuntimeException("Shop balance not found"));
        
        BigDecimal newBalance = payment.getBalanceAmount().subtract(amount);
        payment.setBalanceAmount(newBalance);
        paymentRepository.save(payment);
        
        // Create history record
        PurchasePaymentHistory history = new PurchasePaymentHistory();
        history.setShopName(shopName);
        history.setType(PurchasePaymentHistory.TransactionType.PAYMENT);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(paymentDate);
        purchasePaymentHistoryRepository.save(history);
        
        log.info("Payment recorded. New balance for {}: {}", shopName, newBalance);
    }
    
    @Transactional
    public void recordAdjustment(String shopName, BigDecimal amount, LocalDate adjustmentDate, String note) {
        log.info("Recording adjustment for shop: {}, amount: {}", shopName, amount);
        
        Payment payment = paymentRepository.findByShopName(shopName)
                .orElseGet(() -> {
                    Payment newPayment = new Payment();
                    newPayment.setShopName(shopName);
                    newPayment.setBalanceAmount(BigDecimal.ZERO);
                    return newPayment;
                });
        
        BigDecimal newBalance = payment.getBalanceAmount().add(amount);
        payment.setBalanceAmount(newBalance);
        paymentRepository.save(payment);
        
        // Create history record
        PurchasePaymentHistory history = new PurchasePaymentHistory();
        history.setShopName(shopName);
        history.setType(PurchasePaymentHistory.TransactionType.ADJUSTMENT);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(adjustmentDate);
        purchasePaymentHistoryRepository.save(history);
        
        log.info("Adjustment recorded. New balance for {}: {}", shopName, newBalance);
    }
}
