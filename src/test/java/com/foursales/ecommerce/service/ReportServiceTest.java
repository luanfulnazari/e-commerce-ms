package com.foursales.ecommerce.service;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReportService reportService;

    private final UUID userId = UUID.randomUUID();

    @Nested
    class GetTopBuyers {

        @Test
        @DisplayName("Should return list of top buyers")
        void shouldReturnTopBuyers() {
            TopBuyerDTO topBuyer = mock(TopBuyerDTO.class);
            when(topBuyer.getUserId()).thenReturn(userId);
            when(topBuyer.getEmail()).thenReturn("buyer@example.com");
            when(topBuyer.getTotalSpent()).thenReturn(BigDecimal.valueOf(1000.00));

            when(orderRepository.findTopBuyers()).thenReturn(List.of(topBuyer));

            List<TopBuyerDTO> result = reportService.getTopBuyers();

            assertEquals(1, result.size());
            assertEquals(topBuyer, result.get(0));
            assertEquals(userId, result.get(0).getUserId());
            assertEquals("buyer@example.com", result.get(0).getEmail());
            assertEquals(BigDecimal.valueOf(1000.00), result.get(0).getTotalSpent());

            verify(orderRepository).findTopBuyers();
            verifyNoMoreInteractions(orderRepository);
        }
    }

    @Nested
    class GetAverageTicketPerUser {

        @Test
        @DisplayName("Should return average ticket per user with pagination")
        void shouldReturnAverageTicketPerUser() {
            UserAverageTicketDTO averageTicketDTO = mock(UserAverageTicketDTO.class);
            when(averageTicketDTO.getUserId()).thenReturn(userId);
            when(averageTicketDTO.getEmail()).thenReturn("user@example.com");
            when(averageTicketDTO.getAverageTicket()).thenReturn(BigDecimal.valueOf(250.00));

            Pageable pageable = PageRequest.of(0, 10);
            Page<UserAverageTicketDTO> page = new PageImpl<>(List.of(averageTicketDTO));
            when(orderRepository.getAverageTicketPerUser(any(Pageable.class))).thenReturn(page);

            Page<UserAverageTicketDTO> result = reportService.getAverageTicketPerUser(pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(averageTicketDTO, result.getContent().get(0));
            assertEquals(userId, result.getContent().get(0).getUserId());
            assertEquals("user@example.com", result.getContent().get(0).getEmail());
            assertEquals(BigDecimal.valueOf(250.00), result.getContent().get(0).getAverageTicket());

            verify(orderRepository).getAverageTicketPerUser(any(Pageable.class));
            verifyNoMoreInteractions(orderRepository);
        }
    }

    @Nested
    class GetMonthlyRevenue {

        @Test
        @DisplayName("Should return current month's revenue")
        void shouldReturnMonthlyRevenue() {
            MonthlyRevenueDTO monthlyRevenueDTO = mock(MonthlyRevenueDTO.class);
            when(monthlyRevenueDTO.getRevenueMonth()).thenReturn("2025-07");
            when(monthlyRevenueDTO.getTotalRevenue()).thenReturn(BigDecimal.valueOf(15000.00));

            when(orderRepository.getRevenueByMonthAndYear(7, 2025)).thenReturn(monthlyRevenueDTO);

            MonthlyRevenueDTO result = reportService.getMonthlyRevenue(7, 2025);

            assertEquals(monthlyRevenueDTO, result);
            assertEquals("2025-07", result.getRevenueMonth());
            assertEquals(BigDecimal.valueOf(15000.00), result.getTotalRevenue());

            verify(orderRepository).getRevenueByMonthAndYear(7, 2025);
            verifyNoMoreInteractions(orderRepository);
        }
    }
}
