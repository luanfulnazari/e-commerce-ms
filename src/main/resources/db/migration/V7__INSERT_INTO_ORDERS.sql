INSERT INTO orders (id, user_id, status, total_price, created_at)
SELECT UUID_TO_BIN(UUID()), u.id, 'PAID', p.price, NOW()
FROM users u
JOIN (
  SELECT 'Mouse Gamer Logitech G502 HERO' AS product_name, '%user1%' AS user_email_pattern
  UNION ALL
  SELECT 'Placa de Vídeo NVIDIA GeForce RTX 3060 12GB', '%user2%'
  UNION ALL
  SELECT 'Notebook Acer Nitro 5 i7 RTX 3050 16GB 512GB SSD', '%user3%'
  UNION ALL
  SELECT 'PC Gamer Intel i5 + RTX 4060 + 16GB + SSD 1TB', '%user4%'
  UNION ALL
  SELECT 'Processador AMD Ryzen 5 5600X', '%user5%'
) AS prod_user ON u.email LIKE prod_user.user_email_pattern
JOIN products p ON p.name = prod_user.product_name;