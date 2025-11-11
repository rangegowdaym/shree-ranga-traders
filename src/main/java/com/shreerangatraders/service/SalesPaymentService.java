package com.shreerangatraders.service;

import com.shreerangatraders.entity.SalesPayment;
import com.shreerangatraders.entity.PaymentHistory;
import com.shreerangatraders.repository.SalesPaymentRepository;
import com.shreerangatraders.repository.PaymentHistoryRepository;
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
public class SalesPaymentService {
    
    private final SalesPaymentRepository salesPaymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    
    public List<SalesPayment> getAllBalances() {
        return salesPaymentRepository.findAll();
    }
    
    public List<PaymentHistory> getAllHistory() {
        return paymentHistoryRepository.findAllByOrderByPaymentDateDescCreatedDateDesc();
    }
    
    public List<PaymentHistory> getHistoryByCustomer(String customerName) {
        return paymentHistoryRepository.findByCustomerNameOrderByPaymentDateDesc(customerName);
    }
    
    @Transactional
    public void recordPayment(String customerName, BigDecimal amount, LocalDate paymentDate, String note) {
        log.info("Recording payment for customer: {}, amount: {}", customerName, amount);
        
        SalesPayment salesPayment = salesPaymentRepository.findByCustomerName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer balance not found"));
        
        BigDecimal newBalance = salesPayment.getBalanceAmount().subtract(amount);
        salesPayment.setBalanceAmount(newBalance);
        salesPaymentRepository.save(salesPayment);
        
        // Create history record
        PaymentHistory history = new PaymentHistory();
        history.setCustomerName(customerName);
        history.setType(PaymentHistory.TransactionType.PAYMENT);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(paymentDate);
        paymentHistoryRepository.save(history);
        
        log.info("Payment recorded. New balance for {}: {}", customerName, newBalance);
    }
    
    @Transactional
    public void recordAdjustment(String customerName, BigDecimal amount, LocalDate adjustmentDate, String note) {
        log.info("Recording adjustment for customer: {}, amount: {}", customerName, amount);
        
        SalesPayment salesPayment = salesPaymentRepository.findByCustomerName(customerName)
                .orElseGet(() -> {
                    SalesPayment newPayment = new SalesPayment();
                    newPayment.setCustomerName(customerName);
                    newPayment.setBalanceAmount(BigDecimal.ZERO);
                    return newPayment;
                });
        
        BigDecimal newBalance = salesPayment.getBalanceAmount().add(amount);
        salesPayment.setBalanceAmount(newBalance);
        salesPaymentRepository.save(salesPayment);
        
        // Create history record
        PaymentHistory history = new PaymentHistory();
        history.setCustomerName(customerName);
        history.setType(PaymentHistory.TransactionType.ADJUSTMENT);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(adjustmentDate);
        paymentHistoryRepository.save(history);
        
        log.info("Adjustment recorded. New balance for {}: {}", customerName, newBalance);
    }
}
