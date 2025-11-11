package com.shreerangatraders.service;

import com.shreerangatraders.entity.Purchase;
import com.shreerangatraders.entity.Payment;
import com.shreerangatraders.entity.PurchasePaymentHistory;
import com.shreerangatraders.repository.PurchaseRepository;
import com.shreerangatraders.repository.PaymentRepository;
import com.shreerangatraders.repository.PurchasePaymentHistoryRepository;
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
public class PurchaseService {
    
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final PurchasePaymentHistoryRepository purchasePaymentHistoryRepository;
    
    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        log.info("Creating purchase for shop: {}", purchase.getShopName());
        Purchase saved = purchaseRepository.save(purchase);
        
        // Update shop balance
        BigDecimal netAmount = purchase.getAmount().subtract(purchase.getDiscount() != null ? purchase.getDiscount() : BigDecimal.ZERO);
        updateShopBalance(purchase.getShopName(), netAmount, "Purchase #" + saved.getId());
        
        return saved;
    }
    
    @Transactional
    public Purchase updatePurchase(Long id, Purchase purchase) {
        log.info("Updating purchase: {}", id);
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        
        BigDecimal oldNetAmount = existing.getAmount().subtract(existing.getDiscount() != null ? existing.getDiscount() : BigDecimal.ZERO);
        BigDecimal newNetAmount = purchase.getAmount().subtract(purchase.getDiscount() != null ? purchase.getDiscount() : BigDecimal.ZERO);
        BigDecimal difference = newNetAmount.subtract(oldNetAmount);
        
        existing.setPurchaseDate(purchase.getPurchaseDate());
        existing.setShopName(purchase.getShopName());
        existing.setBags(purchase.getBags());
        existing.setItems(purchase.getItems());
        existing.setAmount(purchase.getAmount());
        existing.setDiscount(purchase.getDiscount());
        
        Purchase updated = purchaseRepository.save(existing);
        
        // Adjust shop balance
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            updateShopBalance(purchase.getShopName(), difference, "Purchase #" + id + " updated");
        }
        
        return updated;
    }
    
    @Transactional
    public void deletePurchase(Long id) {
        log.info("Deleting purchase: {}", id);
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        
        BigDecimal netAmount = purchase.getAmount().subtract(purchase.getDiscount() != null ? purchase.getDiscount() : BigDecimal.ZERO);
        
        purchaseRepository.deleteById(id);
        
        // Reverse shop balance
        updateShopBalance(purchase.getShopName(), netAmount.negate(), "Purchase #" + id + " deleted");
    }
    
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
    }
    
    public List<Purchase> getRecentPurchases() {
        return purchaseRepository.findTop100ByOrderByPurchaseDateDescIdDesc(PageRequest.of(0, 100));
    }
    
    public List<Purchase> searchPurchases(LocalDate startDate, LocalDate endDate, String shopName) {
        return purchaseRepository.searchPurchases(startDate, endDate, shopName);
    }
    
    private void updateShopBalance(String shopName, BigDecimal amount, String note) {
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
        history.setType(PurchasePaymentHistory.TransactionType.PURCHASE);
        history.setAmount(amount);
        history.setBalanceAfter(newBalance);
        history.setNote(note);
        history.setPaymentDate(LocalDate.now());
        purchasePaymentHistoryRepository.save(history);
        
        log.info("Updated shop balance for {}: {}", shopName, newBalance);
    }
}
