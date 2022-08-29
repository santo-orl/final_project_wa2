CREATE TABLE IF NOT EXISTS "tickets"
(
    ticket_id serial PRIMARY KEY,
    ticket_type VARCHAR(255),
    price INTEGER,
    min_age INTEGER,
    max_age INTEGER,
    zid VARCHAR(255),
    valid_from VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "orders"
(
    id serial PRIMARY KEY ,
    user_id VARCHAR(255),
    status VARCHAR(255),
    n_tickets INTEGER,
    ticket_id BIGINT
);

CREATE TABLE IF NOT EXISTS "tickets"
(
    ticket_id serial PRIMARY KEY,
    ticket_type VARCHAR(255),
    price INTEGER,
    min_age INTEGER,
    max_age INTEGER,
    zid VARCHAR(255),
    valid_from VARCHAR(255),
    max_usages INTEGER
    );




