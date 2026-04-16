package com.example.BillingApplication.controller;

import com.example.BillingApplication.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/summary")
    public Map<String, Object> getSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        return dashboardService.getSummary(start, end);
    }

    @GetMapping("/top-customers")
public List<Map<String, Object>> topCustomers() {
    return dashboardService.getTopCustomers();
}
    @GetMapping("/monthly-revenue")
public List<Map<String, Object>> monthlyRevenue(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {

    LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
    LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

    return dashboardService.getMonthlyRevenue(start, end);
}
    @GetMapping("/overdue")
public List<Map<String, Object>> getOverdueInvoices(
        @RequestParam(required = false) Long customerId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {

    LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
    LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

    return dashboardService.getOverdueInvoices(customerId, start, end);
}

}