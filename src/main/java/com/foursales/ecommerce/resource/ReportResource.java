package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.dto.MonthlyRevenueDTO;
import com.foursales.ecommerce.dto.TopBuyerDTO;
import com.foursales.ecommerce.dto.UserAverageTicketDTO;
import com.foursales.ecommerce.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportResource {

    private final ReportService reportService;

    @GetMapping("/top-buyers")
    @ResponseStatus(HttpStatus.OK)
    public List<TopBuyerDTO> getTopBuyers() {
        return reportService.getTopBuyers();
    }

    @GetMapping("/average-ticket")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserAverageTicketDTO> getAverageTicketByUser(Pageable pageable) {
        return reportService.getAverageTicketPerUser(pageable);
    }

    @GetMapping("/monthly-revenue")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyRevenueDTO getMonthlyRevenue(
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getMonthlyRevenue(month, year);
    }
}
