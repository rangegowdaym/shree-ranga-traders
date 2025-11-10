package com.shreerangatraders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchasepayment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePaymentHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "shop_name", nullable = false)
    private String shopName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "balance_after", precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceAfter;
    
    @Column(name = "note", length = 500)
    private String note;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    public enum TransactionType {
        PURCHASE, PAYMENT, ADJUSTMENT
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
