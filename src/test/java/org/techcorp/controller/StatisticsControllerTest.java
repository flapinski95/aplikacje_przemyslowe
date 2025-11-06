package org.techcorp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techcorp.models.CompanyStatistics;
import org.techcorp.models.enums.Position;
import org.techcorp.service.EmployeeService;
import org.techcorp.service.ImportService;
import org.techcorp.service.ApiService;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private ImportService importService;

    @MockBean
    private ApiService apiService;

    @Test
    void average_ok() throws Exception {
        when(employeeService.getAverageSalary(Optional.empty())).thenReturn(10000.0);
        mockMvc.perform(get("/api/statistics/salary/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(10000.0));
    }

    @Test
    void company_stats_ok() throws Exception {
        when(employeeService.getCompanyStatistics())
                .thenReturn(Map.of("TechCorp", new CompanyStatistics(3, 12000.0, "Jan Kowalski")));

        mockMvc.perform(get("/api/statistics/company/TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("TechCorp"));
    }

    @Test
    void positions_ok() throws Exception {
        when(employeeService.countByPosition())
                .thenReturn(Map.of(Position.PROGRAMISTA, 2L));

        mockMvc.perform(get("/api/statistics/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PROGRAMISTA").value(2));
    }
}