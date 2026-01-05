package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "payment_event_id")
    private PaymentEvent paymentEvent;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long productId;

    private String productName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public void assignPaymentEvent(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }
}
