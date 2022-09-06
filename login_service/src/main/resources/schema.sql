CREATE TABLE IF NOT EXISTS "activation"
(
    id serial PRIMARY KEY,
    CONSTRAINT user
        FOREIGN KEY(id)
            REFERENCES users(id),
    activation_code VARCHAR(255),
    activation_deadline DATE,
    attempt_counter INTEGER
);

CREATE TABLE IF NOT EXISTS "users"
(
    id serial PRIMARY KEY,
    email VARCHAR(255),
    passsword VARCHAR(255),
    role INTEGER,
    status VARCHAR(255),
    userrname VARCHAR(255)
    );

