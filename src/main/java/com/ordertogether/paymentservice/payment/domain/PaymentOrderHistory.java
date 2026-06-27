package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class PaymentOrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "payment_order_history_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long paymentOrderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus previousStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus currentStatus;

    @Column(columnDefinition = "TEXT")
    private String reason;

}
