package com.cms.repository;

import com.cms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.project.id = :projectId
      AND p.isPaid = true
""")
    BigDecimal sumAmountByProjectIdAndIsPaidTrue(
            @Param("projectId") Long projectId
    );}
