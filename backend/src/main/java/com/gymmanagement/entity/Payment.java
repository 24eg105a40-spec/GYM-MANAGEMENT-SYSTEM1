package com.gymmanagement.entity;

import com.gymmanagement.enums.PaymentMethod;
import com.gymmanagement.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_tx_ref", columnList = "transactionReference")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, unique = true, length = 100)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    private User verifiedBy;

    private LocalDateTime verifiedAt;

    public Payment() {
    }

    public Payment(Membership membership, BigDecimal amount, PaymentMethod paymentMethod, String transactionReference, PaymentStatus status) {
        this.membership = membership;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
