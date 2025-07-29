-- ORDERS
CREATE TABLE orders (
    id BINARY(16) NOT NULL PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);