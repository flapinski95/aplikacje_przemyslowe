package org.techcorp.service;

import org.springframework.stereotype.Service;
import org.techcorp.models.CompanyStatistics;
import org.techcorp.models.Employee;
import org.techcorp.models.EmploymentStatus;
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

    public Optional<Employee> findByEmail(String email) {
        return employees.stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Employee> findByCompany(String company) {
        if (company == null || company.isBlank()) return getAllEmployees();
        return employees.stream()
                .filter(e -> e.getCompany().equalsIgnoreCase(company))
                .collect(Collectors.toList());
    }

    public boolean deleteByEmail(String email) {
        return employees.removeIf(e -> e.getEmail().equalsIgnoreCase(email));
    }

    public Employee replace(String email, Employee newEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmail().equalsIgnoreCase(email)) {
                employees.set(i, newEmployee);
                return newEmployee;
            }
        }
        throw new NoSuchElementException("Employee not found: " + email);
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

    public double getAverageSalary(Optional<String> companyOpt) {
        return (companyOpt.isPresent() ? findByCompany(companyOpt.get()) : employees)
                .stream()
                .mapToInt(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Map<Position, Long> countByPosition() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));
    }

    public Map<EmploymentStatus, Long> statusDistribution() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getStatus, Collectors.counting()));
    }


    public List<Employee> findByStatus(EmploymentStatus status) {
        return employees.stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }


    public void clear() {
        employees.clear();
    }
}