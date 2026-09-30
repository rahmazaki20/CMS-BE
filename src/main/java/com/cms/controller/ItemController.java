package com.cms.controller;

import com.cms.dto.*;
import com.cms.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN','STOREKEEPER')"
    )
    public ItemResponse create(
            @Valid @RequestBody ItemRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public List<ItemResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public ItemResponse getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','STOREKEEPER')"
    )
    public ItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request
    ) {
        return service.update(id, request);
    }

    @PostMapping("/import")
    @PreAuthorize(
            "hasAnyRole('ADMIN','STOREKEEPER')"
    )
    public ItemImportResponse importExcel(
            @RequestParam("file")
            MultipartFile file
    ) {
        return service.importExcel(file);
    }
}