package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import jakarta.annotation.Nonnull;
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

    private byte failedCount;

    private LocalDateTime approvedAt;

    public void addPaymentOrder(PaymentOrder paymentOrder) {
        paymentOrder.assignPaymentEvent(this);
        paymentOrders.add(paymentOrder);
    }

    public Long totalAmount() {
        return paymentOrders.stream()
            .mapToLong(it -> it.getAmount().toLong())
            .sum();
    }

    public Integer totalQuantity() {
        return paymentOrders.size();
    }

    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    /**
     * 결제 완료 상태로 변경합니다.
     * @param successInfo 결제 부가 정보
     * @throws InvalidPaymentStatusException 완료되지 않은 결제 주문이 존재하는 경우
     */
    public void done(@Nonnull SuccessExtraInfo successInfo) throws InvalidPaymentStatusException{
        paymentOrders.stream()
            .filter(it -> !it.isPaid())
            .findAny()
            .ifPresent(it -> { throw new InvalidPaymentStatusException(orderId, it.getPaymentStatus()); });

        applySuccessInfo(successInfo);
        this.isPaymentDone = true;
    }

    /**
     * 결제 완료로 인한 부가정보를 기록합니다. <br>
     * - 결제 수단 <br>
     * - PG 결제 승인 시간
     * @param successExtraInfo
     */
    private void applySuccessInfo(@Nonnull SuccessExtraInfo successExtraInfo) {
        if (method != null || approvedAt != null) {
            throw new InvalidPaymentException("이미 결제 부가 정보가 기록되어 있습니다. [orderId = %s]".formatted(orderId.value()));
        }

        this.method = successExtraInfo.paymentMethod();
        this.approvedAt = successExtraInfo.approvedAt();
    }

    public boolean isFail() {
        return !paymentOrders.isEmpty() && paymentOrders.stream().allMatch(PaymentOrder::isFail);
    }

    public byte increaseFailedCount() {
        return failedCount++;
    }

}
