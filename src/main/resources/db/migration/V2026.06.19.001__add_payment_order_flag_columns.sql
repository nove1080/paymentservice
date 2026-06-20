ALTER TABLE payment_order
    ADD COLUMN is_wallet_updated TINYINT(1) DEFAULT FALSE,
    ADD COLUMN is_ledger_updated TINYINT(1) DEFAULT FALSE;
