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
    
    public PaymentHistory getPaymentById(Long id) {
        return paymentHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment history not found with id: " + id));
    }

    @Transactional
    public void updatePayment(Long id, BigDecimal newAmount, LocalDate newPaymentDate, String newNote) {
        log.info("Updating payment with id: {}", id);

        PaymentHistory payment = paymentHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment history not found with id: " + id));

        // Only allow editing PAYMENT type entries
        if (payment.getType() != PaymentHistory.TransactionType.PAYMENT) {
            throw new RuntimeException("Only PAYMENT type entries can be edited");
        }

        String customerName = payment.getCustomerName();
        BigDecimal oldAmount = payment.getAmount();
        BigDecimal amountDifference = newAmount.subtract(oldAmount);

        // Update customer balance - reverse old amount and apply new amount
        SalesPayment salesPayment = salesPaymentRepository.findByCustomerName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer balance not found"));

        // Reverse the old payment and apply the new payment
        BigDecimal newBalance = salesPayment.getBalanceAmount().add(oldAmount).subtract(newAmount);
        salesPayment.setBalanceAmount(newBalance);
        salesPaymentRepository.save(salesPayment);

        // Update the payment history record
        payment.setAmount(newAmount);
        payment.setBalanceAfter(newBalance);
        payment.setPaymentDate(newPaymentDate);
        payment.setNote(newNote);
        paymentHistoryRepository.save(payment);

        log.info("Payment updated. New balance for {}: {}", customerName, newBalance);
    }

    @Transactional
    public void deletePayment(Long id) {
        log.info("Deleting payment with id: {}", id);

        PaymentHistory payment = paymentHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment history not found with id: " + id));

        // Only allow deleting PAYMENT type entries
        if (payment.getType() != PaymentHistory.TransactionType.PAYMENT) {
            throw new RuntimeException("Only PAYMENT type entries can be deleted");
        }

        String customerName = payment.getCustomerName();
        BigDecimal amount = payment.getAmount();

        // Reverse the payment from customer balance
        SalesPayment salesPayment = salesPaymentRepository.findByCustomerName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer balance not found"));

        BigDecimal newBalance = salesPayment.getBalanceAmount().add(amount);
        salesPayment.setBalanceAmount(newBalance);
        salesPaymentRepository.save(salesPayment);

        // Delete the payment history record
        paymentHistoryRepository.delete(payment);

        log.info("Payment deleted. New balance for {}: {}", customerName, newBalance);
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
