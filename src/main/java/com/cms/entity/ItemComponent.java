package com.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "item_components",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_item_component_name",
                        columnNames = {"item_id", "name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit;

    @Column(
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal unitPrice;

    @Column(
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal totalPrice;
}