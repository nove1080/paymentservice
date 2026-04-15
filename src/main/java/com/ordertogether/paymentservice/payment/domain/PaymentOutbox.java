package com.ordertogether.paymentservice.payment.domain;

import com.ordertogether.paymentservice.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@ToString(callSuper = true)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "uk_payment_outbox_idempotency_key", columnNames = "idempotency_key"),
})
@Entity
public class PaymentOutbox extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_outbox_id", updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String idempotencyKey;

    private Integer partitionKey;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String payload;

    @Column(nullable = false)
    private boolean published;

    public void markAsPublished() {
        if (published) {
            throw new IllegalStateException("이미 발행된 Outbox 메시지입니다. [idempotencyKey = %s]".formatted(idempotencyKey));
        }
        this.published = true;
    }
}
