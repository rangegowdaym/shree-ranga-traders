package com.shreerangatraders.repository;

import com.shreerangatraders.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopName(String shopName);
    boolean existsByShopName(String shopName);
}
