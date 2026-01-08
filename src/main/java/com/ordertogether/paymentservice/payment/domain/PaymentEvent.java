package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
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
@Table(name = "payment_event", uniqueConstraints = {
    @UniqueConstraint(name = "uk_payment_event_order_id", columnNames = "order_id"),
    @UniqueConstraint(name = "uk_payment_event_payment_key", columnNames = "payment_key")
})
public class PaymentEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "payment_event_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @Default
    @OneToMany(mappedBy = "paymentEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentOrder> paymentOrders = new ArrayList<>();

    private String orderName;

    private String paymentKey;

    @Embedded
    @AttributeOverride(
        name = "value",
        column = @Column(name = "order_id", nullable = false, updatable = false)
    )
    private OrderId orderId;

    @Column(nullable = false)
    private boolean isPaymentDone;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private LocalDateTime approvedAt;

    public String getOrderId() {
        return orderId.value();
    }

    public void addPaymentOrder(PaymentOrder paymentOrder) {
        paymentOrder.assignPaymentEvent(this);
        paymentOrders.add(paymentOrder);
    }

    public Long totalAmount() {
        return paymentOrders.stream()
            .mapToLong(it -> it.getAmount().toLong())
            .sum();
    }
}
