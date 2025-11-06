package org.techcorp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.techcorp.models.Employee;
import org.techcorp.models.EmploymentStatus;
import org.techcorp.models.enums.Position;
import org.techcorp.service.ApiService;
import org.techcorp.service.EmployeeService;
import org.techcorp.service.ImportService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerTest {

    @Autowired MockMvc mvc;
    @MockBean private EmployeeService service;
    @MockBean private ImportService importService;
    @MockBean  private ApiService apiService;

    @Test
    void getAll_ok() throws Exception {
        when(service.getAllEmployees()).thenReturn(List.of(new Employee("Jan Kowalski","jan@x.pl","TechCorp",Position.PROGRAMISTA,8000,EmploymentStatus.ACTIVE)));

        mvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jan@x.pl"));
    }

    @Test
    void getOne_notFound() throws Exception {
        when(service.findByEmail("no@x.pl")).thenReturn(Optional.empty());
        mvc.perform(get("/api/employees/no@x.pl"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOne_found() throws Exception {
        when(service.findByEmail("jan@x.pl")).thenReturn(Optional.of(
                new Employee("Jan Kowalski","jan@x.pl","TechCorp", Position.PROGRAMISTA,8000, EmploymentStatus.ACTIVE)
        ));
        mvc.perform(get("/api/employees/jan@x.pl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("TechCorp"));
    }


    @Test
    void post_created() throws Exception {
        when(service.findByEmail("jan@x.pl")).thenReturn(Optional.empty());
        doAnswer(inv -> null).when(service).addEmployee(any());
        mvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jan\",\"lastName\":\"Kowalski\",\"email\":\"jan@x.pl\",\"company\":\"TechCorp\",\"position\":\"PROGRAMISTA\",\"salary\":8000,\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.email").value("jan@x.pl"));
    }

    @Test
    void delete_noContent() throws Exception {
        when(service.deleteByEmail("jan@x.pl")).thenReturn(true);
        mvc.perform(delete("/api/employees/jan@x.pl"))
                .andExpect(status().isNoContent());
    }

    @Test
    void patch_status_ok() throws Exception {
        when(service.findByEmail("jan@x.pl")).thenReturn(Optional.of(
                new Employee("Jan Kowalski","jan@x.pl","TechCorp", Position.PROGRAMISTA,8000, EmploymentStatus.ACTIVE)
        ));
        doAnswer(inv -> null).when(service).replace(eq("jan@x.pl"), any(Employee.class));
        mvc.perform(patch("/api/employees/jan@x.pl/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ON_LEAVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));
    }
}