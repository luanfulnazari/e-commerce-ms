package com.foursales.ecommerce.repository;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.entity.Order;
import com.foursales.ecommerce.entity.OrderItem;
import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.OrderStatus;
import com.foursales.ecommerce.enums.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderRepositoryJpaTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DataSource dataSource;

    private User user;
    private Product product;

    @BeforeAll
    void registerFunctions() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE ALIAS IF NOT EXISTS BIN_TO_UUID FOR \"" +
                    "com.foursales.ecommerce.config.H2Functions.binToUuid\"");
        }
    }

    @BeforeEach
    void setup() {
        user = createUser("user@example.com", "secret");
        product = createProduct(new BigDecimal("100"));
        em.flush();
    }

    @Nested
    class FindOrdersTests {

        @Test
        @DisplayName("Should find all orders by user ID with items")
        void shouldFindAllOrdersByUserIdWithItems() {
            Order order = createOrder(user, new BigDecimal("200"), null);
            createOrderItem(order, product);

            em.flush();

            List<Order> orders = orderRepository.findAllByUserId(user.getId());

            assertFalse(orders.isEmpty());
            assertEquals(1, orders.size());
            assertEquals(1, orders.get(0).getItems().size());
            assertNotNull(orders.get(0).getItems().get(0).getProduct());
        }
    }

    @Nested
    class TopBuyersTests {

        @Test
        @DisplayName("Should find top buyers ordered by total spending")
        void shouldFindTopBuyers() {
            createOrder(user, new BigDecimal("150"), null);

            User user2 = createUser("user2@example.com", "secret2");
            createOrder(user2, new BigDecimal("300"), null);

            em.flush();

            List<TopBuyerDTO> topBuyers = orderRepository.findTopBuyers();

            assertFalse(topBuyers.isEmpty());
            assertEquals(2, topBuyers.size());
            assertEquals("user2@example.com", topBuyers.get(0).getEmail());
        }
    }

    @Nested
    class AverageTicketTests {

        @Test
        @DisplayName("Should get average ticket per user")
        void shouldGetAverageTicketPerUser() {
            createOrder(user, new BigDecimal("100"), null);
            createOrder(user, new BigDecimal("300"), null);

            em.flush();

            Page<UserAverageTicketDTO> page = orderRepository.getAverageTicketPerUser(PageRequest.of(0, 10));

            assertFalse(page.isEmpty());
            UserAverageTicketDTO dto = page.getContent().get(0);
            assertEquals(user.getId(), dto.getUserId());
            assertEquals(user.getEmail(), dto.getEmail());
            assertEquals(new BigDecimal("200.00"), dto.getAverageTicket());
        }
    }

    @Nested
    class RevenueTests {

        @Test
        @DisplayName("Should get revenue by month and year")
        void shouldGetRevenueByMonthAndYear() {
            int month = LocalDate.now().getMonthValue();
            int year = LocalDate.now().getYear();

            createOrder(user, new BigDecimal("500"), LocalDateTime.now());

            em.flush();

            MonthlyRevenueDTO revenue = orderRepository.getRevenueByMonthAndYear(month, year);

            assertNotNull(revenue);
            assertEquals(String.format("%d-%02d", year, month), revenue.getRevenueMonth());
            assertEquals(0, revenue.getTotalRevenue().compareTo(BigDecimal.valueOf(500)));
        }
    }

    private User createUser(String email, String password) {
        User userEntity = User.builder()
                .email(email)
                .password(password)
                .role(Role.USER)
                .build();
        em.persist(userEntity);
        return userEntity;
    }

    private Product createProduct(BigDecimal price) {
        Product productEntity = Product.builder()
                .name("name")
                .description("description")
                .price(price)
                .category("category")
                .stockQuantity(10)
                .build();
        em.persist(productEntity);
        return productEntity;
    }

    private Order createOrder(User user, BigDecimal totalPrice, LocalDateTime createdAt) {
        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.PAID)
                .createdAt(createdAt)
                .build();
        em.persist(order);
        return order;
    }

    private OrderItem createOrderItem(Order order, Product product) {
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();
        order.getItems().add(item);
        return item;
    }
}
