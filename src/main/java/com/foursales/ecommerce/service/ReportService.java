package com.foursales.ecommerce.service;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<TopBuyerDTO> getTopBuyers() {
        return orderRepository.findTopBuyers();
    }

    @Transactional(readOnly = true)
    public Page<UserAverageTicketDTO> getAverageTicketPerUser(Pageable pageable) {
        return orderRepository.getAverageTicketPerUser(pageable);
    }

    @Transactional(readOnly = true)
    public MonthlyRevenueDTO getMonthlyRevenue(int month, int year) {
        return orderRepository.getRevenueByMonthAndYear(month, year);
    }
}
