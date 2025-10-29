package org.techcorp.service;

import org.springframework.stereotype.Service;
import org.techcorp.models.CompanyStatistics;
import org.techcorp.models.Employee;
import org.techcorp.models.enums.Position;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        if (employees.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(employee.getEmail()))) {
            throw new IllegalArgumentException("Employee with this email already exists");
        }
        employees.add(employee);
    }

    public void addAll(Collection<Employee> employeesToAdd) {
        employeesToAdd.forEach(this::addEmployee);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Employee> validateSalaryConsistency() {
        return employees.stream()
                .filter(e -> e.getSalary() < e.getPosition().getBaseSalary())
                .collect(Collectors.toList());
    }

    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getCompany,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            long count = list.size();
                            double avg = list.stream().mapToInt(Employee::getSalary).average().orElse(0);
                            String top = list.stream()
                                    .max(Comparator.comparingInt(Employee::getSalary))
                                    .map(Employee::getFullName)
                                    .orElse("N/A");
                            return new CompanyStatistics(count, avg, top);
                        })));
    }

    public void clear() {
        employees.clear();
    }
}