package com.shreerangatraders.service;

import com.shreerangatraders.entity.Sales;
import com.shreerangatraders.entity.SalesPayment;
import com.shreerangatraders.entity.PaymentHistory;
import com.shreerangatraders.repository.SalesRepository;
import com.shreerangatraders.repository.SalesPaymentRepository;
import com.shreerangatraders.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesPaymentRepository salesPaymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public Sales createSale(Sales sale) {
        log.info("Creating sale for customer: {}", sale.getCustomerName());
        Sales saved = salesRepository.save(sale);

        // Update customer balance if CREDIT
        if (sale.getPaymentType() == Sales.PaymentType.CREDIT) {
            updateCustomerBalance(sale.getCustomerName(), sale.getAmount(), PaymentHistory.TransactionType.SALE, "Sale added - " + saved.getItem());
        }

        return saved;
    }

    @Transactional
    public Sales updateSale(Long id, Sales sale) {
        log.info("Updating sale: {}", id);
        Sales existing = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Calculate balance adjustment
        BigDecimal oldAmount = existing.getPaymentType() == Sales.PaymentType.CREDIT ? existing.getAmount() : BigDecimal.ZERO;
        BigDecimal newAmount = sale.getPaymentType() == Sales.PaymentType.CREDIT ? sale.getAmount() : BigDecimal.ZERO;
        BigDecimal difference = newAmount.subtract(oldAmount);

        existing.setSaleDate(sale.getSaleDate());
        existing.setCustomerName(sale.getCustomerName());
        existing.setItem(sale.getItem());
        existing.setBags(sale.getBags());
        existing.setAmount(sale.getAmount());
        existing.setPaymentType(sale.getPaymentType());

        Sales updated = salesRepository.save(existing);

        // Adjust customer balance
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            updateCustomerBalance(sale.getCustomerName(), difference, PaymentHistory.TransactionType.ADJUSTMENT, "Sale updated - " + sale.getItem());
        }

        return updated;
    }

    @Transactional
    public void deleteSale(Long id) {
        log.info("Deleting sale: {}", id);
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        salesRepository.deleteById(id);

        // Reverse customer balance if CREDIT
        if (sale.getPaymentType() == Sales.PaymentType.CREDIT) {
            updateCustomerBalance(sale.getCustomerName(), sale.getAmount().negate(), PaymentHistory.TransactionType.ADJUSTMENT, "Sale deleted - " + sale.getItem());
        }
    }

    public Sales getSaleById(Long id) {
        return salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    public List<Sales> getRecentSales() {
        return salesRepository.findTop100ByOrderBySaleDateDescIdDesc(PageRequest.of(0, 100));
    }

    public List<Sales> searchSales(LocalDate startDate, LocalDate endDate, String customerName) {
        return salesRepository.searchSales(startDate, endDate, customerName);
    }

    private void updateCustomerBalance(String customerName, BigDecimal amount, PaymentHistory.TransactionType transactionType, String note) {
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
        history.setType(transactionType);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(LocalDate.now());
        paymentHistoryRepository.save(history);

        log.info("Updated customer balance for {}: {}", customerName, newBalance);
    }
}
