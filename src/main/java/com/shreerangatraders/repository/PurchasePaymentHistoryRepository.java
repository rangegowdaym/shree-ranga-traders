package com.shreerangatraders.repository;

import com.shreerangatraders.entity.PurchasePaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasePaymentHistoryRepository extends JpaRepository<PurchasePaymentHistory, Long> {
    
    @Query("SELECT pph FROM PurchasePaymentHistory pph WHERE " +
           "(:shopName IS NULL OR :shopName = '' OR pph.shopName = :shopName) " +
           "ORDER BY pph.paymentDate DESC, pph.createdDate DESC")
    List<PurchasePaymentHistory> findByShopNameOrderByPaymentDateDesc(@Param("shopName") String shopName);
    
    List<PurchasePaymentHistory> findAllByOrderByPaymentDateDescCreatedDateDesc();
}
