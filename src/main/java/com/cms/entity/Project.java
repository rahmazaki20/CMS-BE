package com.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    private String clientName;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    /** Base amount: current value of project work before VAT/deductions. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    /** VAT percentage, default 14%. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal vatRate = BigDecimal.valueOf(14);

    /** Calculated VAT amount. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal vatAmount = BigDecimal.ZERO;

    /** First statutory deduction percentage, default 1%. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal deduction1Rate = BigDecimal.valueOf(1);

    /** Calculated first deduction amount. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal deduction1Amount = BigDecimal.ZERO;

    /** Second statutory deduction percentage, default 3.6%. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal deduction2Rate = BigDecimal.valueOf(3.6);

    /** Calculated second deduction amount. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal deduction2Amount = BigDecimal.ZERO;

    /** Retention percentage, default 5%. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal retentionRate = BigDecimal.valueOf(5);

    /** Calculated retained amount. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal retentionAmount = BigDecimal.ZERO;

    /** Base amount plus VAT, before statutory deductions and retention. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal grandTotal = BigDecimal.ZERO;

    /** Final amount payable after VAT, deductions and retention. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal netPayableAmount = BigDecimal.ZERO;

    /** Final payable amount still outstanding after paid payments. */
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal remainingBalance = BigDecimal.ZERO;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProjectItem> projectItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Delivery> deliveries = new ArrayList<>();
}
