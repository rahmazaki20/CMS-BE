package com.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    private String description;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    @Column(nullable = false)
    private Boolean isPaid = false;
}
