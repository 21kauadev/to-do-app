CREATE TABLE users(
    id SERIAL PRIMARY KEY, -- auto increment
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL
);