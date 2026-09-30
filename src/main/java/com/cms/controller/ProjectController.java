package com.cms.controller;

import com.cms.dto.*;
import com.cms.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ProjectResponse create(@Valid @RequestBody ProjectCreateRequest request) { return service.create(request); }

    @GetMapping
    public List<ProjectSummaryResponse> all() { return service.all(); }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) { return service.get(id); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectCreateRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','STOREKEEPER')")
    public ProjectItemResponse addItem(@PathVariable Long id, @Valid @RequestBody ProjectItemRequest request) { return service.addItem(id, request); }

    @DeleteMapping("/{projectId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','STOREKEEPER')")
    public void removeItem(@PathVariable Long projectId, @PathVariable Long itemId) { service.removeItem(projectId, itemId); }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public PaymentResponse addPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) { return service.addPayment(id, request); }

    @DeleteMapping("/{projectId}/payments/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public void removePayment(@PathVariable Long projectId, @PathVariable Long paymentId) { service.removePayment(projectId, paymentId); }

    @PostMapping("/{id}/deliveries")
    @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public DeliveryResponse addDelivery(@PathVariable Long id, @Valid @RequestBody DeliveryRequest request) { return service.addDelivery(id, request); }

    @DeleteMapping("/{projectId}/deliveries/{deliveryId}")
    @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public void removeDelivery(@PathVariable Long projectId, @PathVariable Long deliveryId) { service.removeDelivery(projectId, deliveryId); }
}
