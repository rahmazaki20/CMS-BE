package com.cms.service;

import com.cms.dto.*;
import com.cms.entity.*;
import com.cms.exception.BusinessException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal DEFAULT_VAT_RATE = BigDecimal.valueOf(14);
    private static final BigDecimal DEFAULT_DEDUCTION_1_RATE = BigDecimal.valueOf(1);
    private static final BigDecimal DEFAULT_DEDUCTION_2_RATE = BigDecimal.valueOf(3.6);
    private static final BigDecimal DEFAULT_RETENTION_RATE = BigDecimal.valueOf(5);

    private final ProjectRepository projects;
    private final ItemRepository items;
    private final ProjectItemRepository projectItems;
    private final PaymentRepository payments;
    private final DeliveryRepository deliveryRepository;
    private final ProjectItemMapper pim;
    private final PaymentMapper pm;
    private final DeliveryMapper dm;

    // =========================================================
    // CREATE PROJECT
    // =========================================================

    @Transactional
    public ProjectResponse create(ProjectCreateRequest r) {

        Project project = Project.builder()
                .projectName(r.projectName().trim())
                .clientName(r.clientName())
                .createdDate(
                        r.createdDate() == null
                                ? LocalDate.now()
                                : r.createdDate()
                )
                .status(
                        r.status() == null
                                ? ProjectStatus.IN_PROGRESS
                                : r.status()
                )
                .vatRate(
                        defaultIfNull(
                                r.vatRate(),
                                DEFAULT_VAT_RATE
                        )
                )
                .deduction1Rate(
                        defaultIfNull(
                                r.deduction1Rate(),
                                DEFAULT_DEDUCTION_1_RATE
                        )
                )
                .deduction2Rate(
                        defaultIfNull(
                                r.deduction2Rate(),
                                DEFAULT_DEDUCTION_2_RATE
                        )
                )
                .retentionRate(
                        defaultIfNull(
                                r.retentionRate(),
                                DEFAULT_RETENTION_RATE
                        )
                )
                .build();

        validateRates(project);

        projects.save(project);

        replaceChildren(
                project,
                r.items(),
                r.payments(),
                r.deliveries()
        );

        recalculate(project);

        return response(project);
    }

    // =========================================================
    // GET ALL PROJECTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> all() {

        return projects.findAll()
                .stream()
                .map(project ->
                        new ProjectSummaryResponse(
                                project.getId(),
                                project.getProjectName(),
                                project.getClientName(),
                                project.getCreatedDate(),
                                project.getStatus(),
                                project.getSubtotal(),
                                project.getVatAmount(),
                                project.getDeduction1Amount(),
                                project.getDeduction2Amount(),
                                project.getRetentionAmount(),
                                project.getGrandTotal(),
                                project.getNetPayableAmount(),
                                project.getRemainingBalance()
                        )
                )
                .toList();
    }

    // =========================================================
    // GET PROJECT DETAILS
    // =========================================================

    @Transactional(readOnly = true)
    public ProjectResponse get(Long id) {

        Project project = projects.findDetailedById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        return response(project);
    }

    // =========================================================
    // UPDATE PROJECT
    // =========================================================

    @Transactional
    public ProjectResponse update(
            Long id,
            ProjectCreateRequest r
    ) {

        Project project = projects.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        project.setProjectName(
                r.projectName().trim()
        );

        project.setClientName(
                r.clientName()
        );

        project.setCreatedDate(
                r.createdDate() == null
                        ? project.getCreatedDate()
                        : r.createdDate()
        );

        project.setStatus(
                r.status() == null
                        ? project.getStatus()
                        : r.status()
        );

        project.setVatRate(
                defaultIfNull(
                        r.vatRate(),
                        project.getVatRate()
                )
        );

        project.setDeduction1Rate(
                defaultIfNull(
                        r.deduction1Rate(),
                        project.getDeduction1Rate()
                )
        );

        project.setDeduction2Rate(
                defaultIfNull(
                        r.deduction2Rate(),
                        project.getDeduction2Rate()
                )
        );

        project.setRetentionRate(
                defaultIfNull(
                        r.retentionRate(),
                        project.getRetentionRate()
                )
        );

        validateRates(project);

        project.getProjectItems().clear();
        project.getPayments().clear();
        project.getDeliveries().clear();

        replaceChildren(
                project,
                r.items(),
                r.payments(),
                r.deliveries()
        );

        recalculate(project);

        return response(project);
    }

    // =========================================================
    // DELETE PROJECT
    // =========================================================

    @Transactional
    public void delete(Long id) {

        if (!projects.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Project not found"
            );
        }

        projects.deleteById(id);
    }

    // =========================================================
    // ADD PROJECT ITEM
    // =========================================================

    @Transactional
    public ProjectItemResponse addItem(
            Long id,
            ProjectItemRequest r
    ) {

        Project project = projects.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        ProjectItem projectItem = makeItem(
                project,
                r
        );

        project.getProjectItems().add(projectItem);

        projectItems.save(projectItem);

        recalculate(project);

        return pim.toResponse(projectItem);
    }

    // =========================================================
    // REMOVE PROJECT ITEM
    // =========================================================

    @Transactional
    public void removeItem(
            Long projectId,
            Long itemId
    ) {

        Project project = projects.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        ProjectItem projectItem =
                projectItems.findByIdAndProjectId(
                                itemId,
                                projectId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project item not found"
                                )
                        );

        project.getProjectItems().remove(projectItem);

        projectItems.delete(projectItem);

        recalculate(project);
    }

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @Transactional
    public PaymentResponse addPayment(
            Long id,
            PaymentRequest r
    ) {

        Project project = projects.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        Payment payment = makePayment(
                project,
                r
        );

        project.getPayments().add(payment);

        payments.save(payment);

        recalculate(project);

        return pm.toResponse(payment);
    }

    // =========================================================
    // REMOVE PAYMENT
    // =========================================================

    @Transactional
    public void removePayment(
            Long projectId,
            Long paymentId
    ) {

        Project project = projects.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        Payment payment = payments.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        if (!payment.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Payment not found"
            );
        }

        project.getPayments().remove(payment);

        payments.delete(payment);

        recalculate(project);
    }

    // =========================================================
    // ADD DELIVERY
    // =========================================================

    @Transactional
    public DeliveryResponse addDelivery(
            Long id,
            DeliveryRequest r
    ) {

        Project project = projects.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        Delivery delivery = makeDelivery(
                project,
                r
        );

        project.getDeliveries().add(delivery);

        deliveryRepository.save(delivery);

        return dm.toResponse(delivery);
    }

    // =========================================================
    // REMOVE DELIVERY
    // =========================================================

    @Transactional
    public void removeDelivery(
            Long projectId,
            Long deliveryId
    ) {

        Project project = projects.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        Delivery delivery = project.getDeliveries()
                .stream()
                .filter(d ->
                        d.getId() != null
                                && d.getId().equals(deliveryId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery not found"
                        )
                );

        project.getDeliveries().remove(delivery);

        deliveryRepository.delete(delivery);
    }

    // =========================================================
    // CHILDREN
    // =========================================================

    private void replaceChildren(
            Project project,
            List<ProjectItemRequest> itemRequests,
            List<PaymentRequest> paymentRequests,
            List<DeliveryRequest> deliveryRequests
    ) {

        if (itemRequests != null) {
            for (ProjectItemRequest request : itemRequests) {
                project.getProjectItems().add(
                        makeItem(project, request)
                );
            }
        }

        if (paymentRequests != null) {
            for (PaymentRequest request : paymentRequests) {
                project.getPayments().add(
                        makePayment(project, request)
                );
            }
        }

        if (deliveryRequests != null) {
            for (DeliveryRequest request : deliveryRequests) {
                project.getDeliveries().add(
                        makeDelivery(project, request)
                );
            }
        }
    }

    // =========================================================
    // PROJECT ITEM CREATION
    // =========================================================

    private ProjectItem makeItem(
            Project project,
            ProjectItemRequest request
    ) {

        Item item = items.findById(request.itemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item " + request.itemId() + " not found"
                        )
                );

        BigDecimal price =
                request.actualUnitPrice() == null
                        ? item.getDefaultUnitPrice()
                        : request.actualUnitPrice();

        if (price.signum() < 0) {
            throw new BusinessException(
                    "Unit price cannot be negative"
            );
        }

        BigDecimal total = price.multiply(
                BigDecimal.valueOf(
                        request.quantity().longValue()
                )
        );

        return ProjectItem.builder()
                .project(project)
                .item(item)
                .quantity(request.quantity())
                .actualUnitPrice(price)
                .totalPrice(total)
                .remarks(request.remarks())
                .build();
    }

    // =========================================================
    // PAYMENT CREATION
    // =========================================================

    private Payment makePayment(
            Project project,
            PaymentRequest request
    ) {

        boolean paid = Boolean.TRUE.equals(
                request.isPaid()
        );

        if (paid && request.paidDate() == null) {
            throw new BusinessException(
                    "paidDate is required when payment is marked as paid"
            );
        }

        return Payment.builder()
                .project(project)
                .description(request.description())
                .amount(request.amount())
                .dueDate(request.dueDate())
                .paidDate(request.paidDate())
                .isPaid(paid)
                .build();
    }

    // =========================================================
    // DELIVERY CREATION
    // =========================================================

    private Delivery makeDelivery(
            Project project,
            DeliveryRequest request
    ) {

        return Delivery.builder()
                .project(project)
                .deliveryType(request.deliveryType())
                .quantity(request.quantity())
                .details(request.details())
                .deliveryDate(request.deliveryDate())
                .build();
    }

    // =========================================================
    // FINANCIAL CALCULATION
    // =========================================================

    private void recalculate(Project project) {

        /*
         * Base amount = sum of all project item totals.
         */
        BigDecimal baseAmount = project.getProjectItems()
                .stream()
                .map(ProjectItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        /*
         * All percentages are applied to the base amount.
         */
        BigDecimal vatAmount = percentage(
                baseAmount,
                project.getVatRate()
        );

        BigDecimal deduction1Amount = percentage(
                baseAmount,
                project.getDeduction1Rate()
        );

        BigDecimal deduction2Amount = percentage(
                baseAmount,
                project.getDeduction2Rate()
        );

        BigDecimal retentionAmount = percentage(
                baseAmount,
                project.getRetentionRate()
        );

        /*
         * Base + VAT, before deductions and retention.
         * Kept as grandTotal for backward compatibility.
         */
        BigDecimal grandTotal = baseAmount.add(vatAmount);

        /*
         * Final contractual amount payable after
         * deductions and retention.
         */
        BigDecimal netPayableAmount = baseAmount
                .add(vatAmount)
                .subtract(deduction1Amount)
                .subtract(deduction2Amount)
                .subtract(retentionAmount);

        /*
         * Only paid payments reduce the remaining payable amount.
         */
        BigDecimal paidAmount = project.getPayments()
                .stream()
                .filter(payment ->
                        Boolean.TRUE.equals(
                                payment.getIsPaid()
                        )
                )
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal remainingBalance =
                netPayableAmount.subtract(paidAmount);

        project.setSubtotal(baseAmount);
        project.setVatAmount(vatAmount);
        project.setDeduction1Amount(deduction1Amount);
        project.setDeduction2Amount(deduction2Amount);
        project.setRetentionAmount(retentionAmount);
        project.setGrandTotal(grandTotal);
        project.setNetPayableAmount(netPayableAmount);
        project.setRemainingBalance(remainingBalance);

        projects.save(project);
    }

    private BigDecimal percentage(
            BigDecimal baseAmount,
            BigDecimal rate
    ) {
        return baseAmount
                .multiply(rate)
                .divide(
                        ONE_HUNDRED,
                        4,
                        RoundingMode.HALF_UP
                );
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateRates(Project project) {

        validateRate(
                "VAT",
                project.getVatRate()
        );

        validateRate(
                "Deduction 1",
                project.getDeduction1Rate()
        );

        validateRate(
                "Deduction 2",
                project.getDeduction2Rate()
        );

        validateRate(
                "Retention",
                project.getRetentionRate()
        );
    }

    private void validateRate(
            String name,
            BigDecimal rate
    ) {

        if (rate == null
                || rate.signum() < 0
                || rate.compareTo(ONE_HUNDRED) > 0) {

            throw new BusinessException(
                    name + " rate must be between 0 and 100"
            );
        }
    }

    private BigDecimal defaultIfNull(
            BigDecimal value,
            BigDecimal defaultValue
    ) {
        return value == null ? defaultValue : value;
    }

    // =========================================================
    // RESPONSE
    // =========================================================

    private ProjectResponse response(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getProjectName(),
                project.getClientName(),
                project.getCreatedDate(),
                project.getStatus(),

                project.getSubtotal(),

                project.getVatRate(),
                project.getVatAmount(),

                project.getDeduction1Rate(),
                project.getDeduction1Amount(),

                project.getDeduction2Rate(),
                project.getDeduction2Amount(),

                project.getRetentionRate(),
                project.getRetentionAmount(),

                project.getGrandTotal(),
                project.getNetPayableAmount(),
                project.getRemainingBalance(),

                project.getProjectItems()
                        .stream()
                        .map(pim::toResponse)
                        .toList(),

                project.getPayments()
                        .stream()
                        .map(pm::toResponse)
                        .toList(),

                project.getDeliveries()
                        .stream()
                        .map(dm::toResponse)
                        .toList()
        );
    }
}
