-- ORDER_ITEMS
CREATE TABLE order_items (
    id BINARY(16) NOT NULL PRIMARY KEY,
    order_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);