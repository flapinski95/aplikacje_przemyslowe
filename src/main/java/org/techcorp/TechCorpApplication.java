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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class TechCorpApplication implements CommandLineRunner {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ImportService importService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private List<Employee> xmlEmployees;

    public static void main(String[] args) {
        SpringApplication.run(TechCorpApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== TechCorp Employee Management ===\n");

        ImportSummary summary = importService.importFromCsv();
        System.out.println("CSV Import Summary: " + summary);

        employeeService.addAll(xmlEmployees);
        System.out.println("\nAdded XML employees: " + xmlEmployees.size());

        try {
            var apiEmployees = apiService.fetchEmployeesFromApi();
            apiEmployees.forEach(employeeService::addEmployee);
            System.out.println("Fetched " + apiEmployees.size() + " employees from API.");
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
        }

        System.out.println("\n--- Salary consistency issues ---");
        employeeService.validateSalaryConsistency().forEach(System.out::println);

        System.out.println("\n--- Company statistics ---");
        employeeService.getCompanyStatistics()
                .forEach((company, stats) -> System.out.println(company + " -> " + stats));

        System.out.println("\n=== Application finished ===");
    }
}