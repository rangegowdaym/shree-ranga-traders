package com.shreerangatraders.controller;

import com.shreerangatraders.entity.Item;
import com.shreerangatraders.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item created = itemService.createItem(item);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updated = itemService.updateItem(id, item);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
}

