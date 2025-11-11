package com.shreerangatraders.controller;

import com.shreerangatraders.entity.Sales;
import com.shreerangatraders.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    
    private final SalesService salesService;
    
    @PostMapping
    public ResponseEntity<Sales> createSale(@RequestBody Sales sale) {
        Sales created = salesService.createSale(sale);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Sales> updateSale(@PathVariable Long id, @RequestBody Sales sale) {
        Sales updated = salesService.updateSale(id, sale);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        salesService.deleteSale(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sales> getSaleById(@PathVariable Long id) {
        Sales sale = salesService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Sales>> getRecentSales() {
        List<Sales> sales = salesService.getRecentSales();
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Sales>> searchSales(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String customerName) {
        List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
        return ResponseEntity.ok(sales);
    }
}
