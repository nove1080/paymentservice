package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
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
public class PaymentEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "payment_event_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @Column(unique = true, nullable = false, updatable = false)
    private String paymentKey;

    @Column(unique = true, nullable = false, updatable = false)
    private String orderId;

    @Column(nullable = false)
    private Boolean isPaymentDone;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private LocalDateTime approvedAt;
}
