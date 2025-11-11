package com.shreerangatraders.repository;

import com.shreerangatraders.entity.Purchase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    @Query("SELECT p FROM Purchase p ORDER BY p.purchaseDate DESC, p.id DESC")
    List<Purchase> findTop100ByOrderByPurchaseDateDescIdDesc(Pageable pageable);
    
    @Query("SELECT p FROM Purchase p WHERE " +
           "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.purchaseDate <= :endDate) AND " +
           "(:shopName IS NULL OR :shopName = '' OR p.shopName LIKE %:shopName%) " +
           "ORDER BY p.purchaseDate DESC, p.id DESC")
    List<Purchase> searchPurchases(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("shopName") String shopName);
}
