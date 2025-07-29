package com.foursales.ecommerce.service;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    public List<TopBuyerDTO> getTopBuyers() {
        return orderRepository.findTopBuyers();
    }

    public Page<UserAverageTicketDTO> getAverageTicketPerUser(Pageable pageable) {
        return orderRepository.getAverageTicketPerUser(pageable);
    }

    public MonthlyRevenueDTO getMonthlyRevenue(int month, int year) {
        return orderRepository.getRevenueByMonthAndYear(month, year);
    }
}
