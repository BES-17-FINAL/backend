package com.example.backend.service;

import com.example.backend.entity.Item;
import com.example.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> searchItems(String query) {
        if (query == null || query.isBlank()) {
            return itemRepository.findAll();
        }
        return itemRepository.findByNameContainingIgnoreCase(query);
    }
}
