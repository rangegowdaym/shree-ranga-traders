package com.shreerangatraders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salespayment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_name", unique = true, nullable = false)
    private String customerName;
    
    @Column(name = "balance_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceAmount = BigDecimal.ZERO;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
