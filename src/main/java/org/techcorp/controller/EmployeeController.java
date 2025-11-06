package org.techcorp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.techcorp.dto.EmployeeDTO;
import org.techcorp.exception.DuplicateEmailException;
import org.techcorp.exception.EmployeeNotFoundException;
import org.techcorp.models.Employee;
import org.techcorp.models.EmploymentStatus;
import org.techcorp.models.enums.Position;
import org.techcorp.service.EmployeeService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll(@RequestParam(required = false) String company) {
        List<EmployeeDTO> body = (company == null ? service.getAllEmployees() : service.findByCompany(company))
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getOne(@PathVariable String email) {
        Employee emp = service.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + email));
        return ResponseEntity.ok(toDTO(emp));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> byStatus(@PathVariable String status) {
        EmploymentStatus st = EmploymentStatus.valueOf(status.toUpperCase());
        List<EmployeeDTO> list = service.findByStatus(st).stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeDTO dto, UriComponentsBuilder uriBuilder) {
        if (service.findByEmail(dto.email).isPresent())
            throw new DuplicateEmailException("Email already exists: " + dto.email);

        Employee emp = fromDTO(dto);
        service.addEmployee(emp);

        URI location = uriBuilder.path("/api/employees/{email}").buildAndExpand(emp.getEmail()).toUri();
        return ResponseEntity.created(location).body(toDTO(emp));
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable String email, @RequestBody EmployeeDTO dto) {
        Employee replacement = new Employee(
                (dto.firstName + " " + dto.lastName).trim(),
                email,
                dto.company,
                Position.valueOf(dto.position.toUpperCase()),
                dto.salary,
                dto.status == null ? null : EmploymentStatus.valueOf(dto.status.toUpperCase())
        );
        try {
            Employee saved = service.replace(email, replacement);
            return ResponseEntity.ok(toDTO(saved));
        } catch (Exception e) {
            throw new EmployeeNotFoundException("Employee not found: " + email);
        }
    }

    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> patchStatus(@PathVariable String email, @RequestBody EmployeeDTO dto) {
        EmploymentStatus st = EmploymentStatus.valueOf(dto.status.toUpperCase());
        Employee existing = service.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + email));

        Employee updated = new Employee(
                existing.getFullName(),
                existing.getEmail(),
                existing.getCompany(),
                existing.getPosition(),
                existing.getSalary(),
                st
        );
        service.replace(email, updated);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email) {
        boolean removed = service.deleteByEmail(email);
        if (!removed) throw new EmployeeNotFoundException("Employee not found: " + email);
        return ResponseEntity.noContent().build();
    }

    private EmployeeDTO toDTO(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        String[] parts = e.getFullName().split(" ", 2);
        dto.firstName = parts.length > 0 ? parts[0] : "";
        dto.lastName  = parts.length > 1 ? parts[1] : "";
        dto.email = e.getEmail();
        dto.company = e.getCompany();
        dto.position = e.getPosition().name();
        dto.salary = e.getSalary();
        dto.status = e.getStatus().name();
        return dto;
    }

    private Employee fromDTO(EmployeeDTO dto) {
        String fullName = ((dto.firstName == null ? "" : dto.firstName) + " " + (dto.lastName == null ? "" : dto.lastName)).trim();
        Position pos = Position.valueOf(dto.position.toUpperCase());
        EmploymentStatus st = dto.status == null ? null : EmploymentStatus.valueOf(dto.status.toUpperCase());
        return new Employee(fullName, dto.email, dto.company, pos, dto.salary, st);
    }
}