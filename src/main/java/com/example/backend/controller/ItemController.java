package com.example.backend.controller;

import com.example.backend.entity.Item;
import com.example.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173") // React 포트에 맞게 변경
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<Item> searchItems(@RequestParam(required = false) String query) {
        return itemService.searchItems(query);
    }
}
