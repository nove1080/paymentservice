package com.ordertogether.paymentservice.payment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import com.ordertogether.paymentservice.payment.domain.vo.Price;
import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class PaymentOrder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "payment_order_id", updatable = false)
    private Long id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_event_id", nullable = false)
    private PaymentEvent paymentEvent;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long productId;

    private String productName;

    @Embedded
    @AttributeOverride(
        name = "value",
        column = @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    )
    private Price amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private boolean isWalletUpdated;

    private boolean isLedgerUpdated;

    public void assignPaymentEvent(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }

    public void validateTransition(PaymentStatus nextStatus) {
        if (!paymentStatus.canTransitionTo(nextStatus)) {
            throw new InvalidPaymentStatusException(paymentEvent.getOrderId(), paymentStatus, nextStatus);
        }
    }

    public PaymentStatus changePaymentStatus(PaymentStatus nextStatus) {
        validateTransition(nextStatus);
        this.paymentStatus = nextStatus;
        return this.paymentStatus;
    }

    public void completeWalletUpdate() {
        this.isWalletUpdated = true;
    }

    public void completeLedgerUpdate() {
        this.isLedgerUpdated = true;
    }

    public boolean isPostProcessingDone() {
        return isWalletUpdated && isLedgerUpdated;
    }

    public boolean isPaid() {
        return paymentStatus.isSuccess();
    }

    public boolean isFail() {
        return paymentStatus.isFail();
    }
}
