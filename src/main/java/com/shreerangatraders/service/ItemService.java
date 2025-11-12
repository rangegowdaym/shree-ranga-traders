package com.shreerangatraders.service;

import com.shreerangatraders.entity.Item;
import com.shreerangatraders.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Item createItem(Item item) {
        if (itemRepository.existsByItemName(item.getItemName())) {
            throw new RuntimeException("Item with name '" + item.getItemName() + "' already exists");
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long id, Item item) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        if (!existing.getItemName().equals(item.getItemName()) &&
            itemRepository.existsByItemName(item.getItemName())) {
            throw new RuntimeException("Item with name '" + item.getItemName() + "' already exists");
        }

        existing.setItemName(item.getItemName());
        existing.setUnit(item.getUnit());
        existing.setDefaultPrice(item.getDefaultPrice());

        return itemRepository.save(existing);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemByName(String itemName) {
        return itemRepository.findByItemName(itemName)
                .orElseThrow(() -> new RuntimeException("Item not found with name: " + itemName));
    }
}

