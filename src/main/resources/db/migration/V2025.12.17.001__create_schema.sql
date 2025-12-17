CREATE TABLE payment_event
(
    payment_event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    payment_key VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    is_payment_done TINYINT(1) NOT NULL DEFAULT FALSE,
    method VARCHAR(255),
    approved_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_payment_event_payment_key UNIQUE (payment_key),
    CONSTRAINT uk_payment_event_order_id UNIQUE (order_id)
);

CREATE TABLE payment_order
(
    payment_order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_event_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_order_payment_event_id FOREIGN KEY (payment_event_id) REFERENCES payment_event (payment_event_id)
);

CREATE TABLE payment_order_history
(
    payment_order_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_order_id BIGINT NOT NULL,
    previous_status VARCHAR(255) NOT NULL,
    current_status VARCHAR(255) NOT NULL,
    reason VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_order_history_payment_order_id FOREIGN KEY (payment_order_id) REFERENCES payment_order (payment_order_id)
);
