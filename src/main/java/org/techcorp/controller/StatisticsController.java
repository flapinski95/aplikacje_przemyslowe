package org.techcorp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.techcorp.dto.CompanyStatisticsDTO;
import org.techcorp.models.CompanyStatistics;
import org.techcorp.models.enums.Position;
import org.techcorp.models.EmploymentStatus;
import org.techcorp.service.EmployeeService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EmployeeService service;
    public StatisticsController(EmployeeService service) { this.service = service; }

    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> average(@RequestParam(required = false) String company) {
        double avg = service.getAverageSalary(Optional.ofNullable(company));
        return ResponseEntity.ok(Collections.singletonMap("averageSalary", avg));
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> companyStats(@PathVariable String companyName) {
        Map<String, CompanyStatistics> all = service.getCompanyStatistics();
        CompanyStatistics stats = all.get(companyName);
        if (stats == null) return ResponseEntity.notFound().build();

        CompanyStatisticsDTO dto = new CompanyStatisticsDTO();
        dto.companyName = companyName;
        dto.employeeCount = stats.getEmployeeCount();
        dto.averageSalary = stats.getAverageSalary();
        dto.topEarnerName = stats.getTopEarner();
        dto.highestSalary =  (int) Math.round(
                service.findByCompany(companyName).stream().mapToInt(e -> e.getSalary()).max().orElse(0)
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Integer>> positions() {
        Map<String, Integer> body = service.countByPosition().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue().intValue()));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> status() {
        Map<String, Integer> body = service.statusDistribution().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue().intValue()));
        return ResponseEntity.ok(body);
    }
}