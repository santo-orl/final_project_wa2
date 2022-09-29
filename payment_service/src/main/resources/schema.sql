CREATE TABLE IF NOT EXISTS "transactions"
(
    transaction_id serial PRIMARY KEY,
    order_id BIGINT,
    user_id VARCHAR(255),
    order_status VARCHAR(255),
    total_cost FLOAT,
    date VARCHAR(255)
);
