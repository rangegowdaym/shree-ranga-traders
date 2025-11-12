package com.shreerangatraders.service;

import com.shreerangatraders.entity.Shop;
import com.shreerangatraders.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    @Transactional
    public Shop createShop(Shop shop) {
        if (shopRepository.existsByShopName(shop.getShopName())) {
            throw new RuntimeException("Shop with name '" + shop.getShopName() + "' already exists");
        }
        return shopRepository.save(shop);
    }

    @Transactional
    public Shop updateShop(Long id, Shop shop) {
        Shop existing = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));

        if (!existing.getShopName().equals(shop.getShopName()) &&
            shopRepository.existsByShopName(shop.getShopName())) {
            throw new RuntimeException("Shop with name '" + shop.getShopName() + "' already exists");
        }

        existing.setShopName(shop.getShopName());
        existing.setContact(shop.getContact());
        existing.setAddress(shop.getAddress());

        return shopRepository.save(existing);
    }

    @Transactional
    public void deleteShop(Long id) {
        if (!shopRepository.existsById(id)) {
            throw new RuntimeException("Shop not found with id: " + id);
        }
        shopRepository.deleteById(id);
    }

    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Shop getShopByName(String shopName) {
        return shopRepository.findByShopName(shopName)
                .orElseThrow(() -> new RuntimeException("Shop not found with name: " + shopName));
    }
}

