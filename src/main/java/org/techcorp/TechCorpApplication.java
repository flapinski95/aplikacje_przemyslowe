package org.techcorp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.techcorp.models.ImportSummary;
import org.techcorp.models.Employee;
import org.techcorp.service.ApiService;
import org.techcorp.service.EmployeeService;
import org.techcorp.service.ImportService;

import java.util.List;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class TechCorpApplication implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final ImportService importService;
    private final ApiService apiService;
    private final List<Employee> xmlEmployees;

    // ✅ Wszystko wstrzykiwane przez Spring Boot
    public TechCorpApplication(EmployeeService employeeService,
                               ImportService importService,
                               ApiService apiService,
                               List<Employee> xmlEmployees) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
    }

    public static void main(String[] args) {
        SpringApplication.run(TechCorpApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== TechCorp Employee Management ===\n");

        // 1️⃣ Import z CSV
        ImportSummary summary = importService.importFromCsv();
        System.out.println("CSV Import Summary: " + summary);

        // 2️⃣ Dodanie pracowników z XML
        employeeService.addAll(xmlEmployees);
        System.out.println("\nAdded XML employees: " + xmlEmployees.size());

        // 3️⃣ Pobranie z API
        try {
            var apiEmployees = apiService.fetchEmployeesFromApi();
            apiEmployees.forEach(employeeService::addEmployee);
            System.out.println("Fetched " + apiEmployees.size() + " employees from API.");
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
        }

        // 4️⃣ Walidacja spójności płac
        System.out.println("\n--- Salary consistency issues ---");
        employeeService.validateSalaryConsistency().forEach(System.out::println);

        // 5️⃣ Statystyki firmowe
        System.out.println("\n--- Company statistics ---");
        employeeService.getCompanyStatistics()
                .forEach((company, stats) -> System.out.println(company + " -> " + stats));

        System.out.println("\n=== Application finished ===");
    }
}