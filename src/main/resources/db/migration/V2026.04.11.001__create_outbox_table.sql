CREATE TABLE payment_outbox
(
    payment_outbox_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    partition_key INT,
    event_type VARCHAR(40),
    payload JSON,
    published TINYINT(1) NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_payment_outbox_idempotency_key UNIQUE (idempotency_key)
);
