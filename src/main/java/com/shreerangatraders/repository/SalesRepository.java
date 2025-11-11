package com.shreerangatraders.repository;

import com.shreerangatraders.entity.Sales;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    
    @Query("SELECT s FROM Sales s ORDER BY s.saleDate DESC, s.id DESC")
    List<Sales> findTop100ByOrderBySaleDateDescIdDesc(Pageable pageable);
    
    @Query("SELECT s FROM Sales s WHERE " +
           "(:startDate IS NULL OR s.saleDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.saleDate <= :endDate) AND " +
           "(:customerName IS NULL OR :customerName = '' OR s.customerName LIKE %:customerName%) " +
           "ORDER BY s.saleDate DESC, s.id DESC")
    List<Sales> searchSales(@Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            @Param("customerName") String customerName);
}
