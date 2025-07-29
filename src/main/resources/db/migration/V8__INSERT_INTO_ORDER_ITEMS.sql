INSERT INTO order_items (id, order_id, product_id, quantity, price)
SELECT UUID_TO_BIN(UUID()), o.id, p.id, 1, p.price
FROM (
    SELECT 'Mouse Gamer Logitech G502 HERO' AS product_name, '%user1%' AS email_pattern
    UNION ALL
    SELECT 'Placa de Vídeo NVIDIA GeForce RTX 3060 12GB', '%user2%'
    UNION ALL
    SELECT 'Notebook Acer Nitro 5 i7 RTX 3050 16GB 512GB SSD', '%user3%'
    UNION ALL
    SELECT 'PC Gamer Intel i5 + RTX 4060 + 16GB + SSD 1TB', '%user4%'
    UNION ALL
    SELECT 'Processador AMD Ryzen 5 5600X', '%user5%'
) AS pu
JOIN users u ON u.email LIKE pu.email_pattern
JOIN products p ON p.name = pu.product_name
JOIN orders o ON o.user_id = u.id AND o.total_price = p.price;