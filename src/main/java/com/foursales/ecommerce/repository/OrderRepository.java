package com.foursales.ecommerce.repository;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAllByUserId(UUID userId);

    @Query(value = """
                SELECT
                    BIN_TO_UUID(u.id) AS userId,
                    u.email AS email,
                    SUM(o.total_price) AS totalSpent
                FROM orders o
                JOIN users u ON u.id = o.user_id
                WHERE o.status = 'PAID'
                GROUP BY u.id, u.email
                ORDER BY totalSpent DESC
                LIMIT 5
            """, nativeQuery = true)
    List<TopBuyerDTO> findTopBuyers();

    @Query(value = """
        SELECT
            BIN_TO_UUID(u.id) AS userId,
            u.email AS email,
            ROUND(AVG(o.total_price),2) AS averageTicket
        FROM orders o
        JOIN users u ON u.id = o.user_id
        WHERE o.status = 'PAID'
        GROUP BY u.id, u.email
        ORDER BY averageTicket DESC
        """,
            countQuery = """
            SELECT COUNT(DISTINCT u.id)
            FROM orders o
            JOIN users u ON u.id = o.user_id
            WHERE o.status = 'PAID'
        """,
            nativeQuery = true)
    Page<UserAverageTicketDTO> getAverageTicketPerUser(Pageable pageable);

    @Query(value = """
                    SELECT
                        CONCAT(YEAR(o.created_at), '-', LPAD(MONTH(o.created_at), 2, '0')) AS revenue_month,
                        IFNULL(SUM(o.total_price), 0) AS totalRevenue
                    FROM orders o
                    WHERE o.status = 'PAID'
                      AND MONTH(o.created_at) = ?
                      AND YEAR(o.created_at) = ?
                    GROUP BY CONCAT(YEAR(o.created_at), '-', LPAD(MONTH(o.created_at), 2, '0'))
            """, nativeQuery = true)
    MonthlyRevenueDTO getRevenueByMonthAndYear(int month, int year);
}
