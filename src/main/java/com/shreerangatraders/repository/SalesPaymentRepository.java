package com.shreerangatraders.repository;

import com.shreerangatraders.entity.SalesPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalesPaymentRepository extends JpaRepository<SalesPayment, Long> {
    Optional<SalesPayment> findByCustomerName(String customerName);
}
