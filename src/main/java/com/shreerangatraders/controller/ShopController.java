package com.shreerangatraders.controller;

import com.shreerangatraders.entity.Shop;
import com.shreerangatraders.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
        Shop created = shopService.createShop(shop);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @RequestBody Shop shop) {
        Shop updated = shopService.updateShop(id, shop);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        Shop shop = shopService.getShopById(id);
        return ResponseEntity.ok(shop);
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }
}

