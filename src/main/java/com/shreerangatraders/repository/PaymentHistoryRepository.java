package com.shreerangatraders.repository;

import com.shreerangatraders.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    
    @Query("SELECT ph FROM PaymentHistory ph WHERE " +
           "(:customerName IS NULL OR :customerName = '' OR ph.customerName = :customerName) " +
           "ORDER BY ph.paymentDate DESC, ph.createdDate DESC")
    List<PaymentHistory> findByCustomerNameOrderByPaymentDateDesc(@Param("customerName") String customerName);
    
    List<PaymentHistory> findAllByOrderByPaymentDateDescCreatedDateDesc();
}
