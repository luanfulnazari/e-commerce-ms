package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportResource.class)
class ReportResourceMvcTest extends AbstractResourceMvcTest {

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@email.com";
    private final BigDecimal averageTicket = new BigDecimal("150.00");
    private final String yearMonth = YearMonth.of(2025, 7).toString();
    private final BigDecimal totalRevenue = new BigDecimal("50000.00");
    private final BigDecimal totalSpent = new BigDecimal("100000.00");

    @Nested
    class GetTopBuyers {

        @Test
        @DisplayName("Should return 200 and list of top buyers")
        void shouldReturnTopBuyers() throws Exception {

            TopBuyerDTO buyer = new TopBuyerDTO() {

                @Override
                public UUID getUserId() {
                    return userId;
                }

                @Override
                public String getEmail() {
                    return email;
                }

                @Override
                public BigDecimal getTotalSpent() {
                    return totalSpent;
                }
            };

            String expectedJson = objectMapper.writeValueAsString(List.of(buyer));
            when(reportService.getTopBuyers()).thenReturn(List.of(buyer));

            mockMvc.perform(get("/v1/reports/top-buyers"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(reportService).getTopBuyers();
        }
    }

    @Nested
    class GetAverageTicketByUser {

        @Test
        @DisplayName("Should return 200 and paged list of user average tickets")
        void shouldReturnAverageTickets() throws Exception {

            UserAverageTicketDTO avg = new UserAverageTicketDTO() {
                @Override
                public UUID getUserId() {
                    return userId;
                }

                @Override
                public String getEmail() {
                    return email;
                }

                @Override
                public BigDecimal getAverageTicket() {
                    return averageTicket;
                }
            };

            Page<UserAverageTicketDTO> page = new PageImpl<>(List.of(avg));
            String expectedJson = objectMapper.writeValueAsString(page);
            when(reportService.getAverageTicketPerUser(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/v1/reports/average-ticket")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(reportService).getAverageTicketPerUser(any(Pageable.class));
        }
    }

    @Nested
    class GetMonthlyRevenue {

        @Test
        @DisplayName("Should return 200 and monthly revenue")
        void shouldReturnMonthlyRevenue() throws Exception {

            MonthlyRevenueDTO revenue = new MonthlyRevenueDTO() {
                @Override
                public String getRevenueMonth() {
                    return yearMonth;
                }

                @Override
                public BigDecimal getTotalRevenue() {
                    return totalRevenue;
                }
            };

            String expectedJson = objectMapper.writeValueAsString(revenue);
            when(reportService.getMonthlyRevenue(7, 2025)).thenReturn(revenue);

            mockMvc.perform(get("/v1/reports/monthly-revenue")
                            .param("month", "7")
                            .param("year", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(reportService).getMonthlyRevenue(7, 2025);
        }
    }
}
