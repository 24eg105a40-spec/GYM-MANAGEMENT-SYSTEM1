package com.gymmanagement.repository;

import com.gymmanagement.entity.Payment;
import com.gymmanagement.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionReference(String transactionReference);
    boolean existsByTransactionReference(String transactionReference);
    List<Payment> findByMembershipMemberIdOrderByPaymentDateDesc(Long memberId);
    List<Payment> findByStatusOrderByPaymentDateDesc(PaymentStatus status);

    long countByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'VERIFIED'")
    BigDecimal sumTotalVerifiedRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'VERIFIED' AND p.paymentDate >= :since")
    BigDecimal sumVerifiedRevenueSince(@Param("since") LocalDateTime since);

    List<Payment> findTop10ByOrderByPaymentDateDesc();
}
