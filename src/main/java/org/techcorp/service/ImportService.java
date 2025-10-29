package org.techcorp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.techcorp.exception.InvalidDataException;
import org.techcorp.models.Employee;
import org.techcorp.models.ImportSummary;
import org.techcorp.models.enums.Position;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private final EmployeeService employeeService;
    private final String csvFilePath;

    public ImportService(EmployeeService employeeService,
                         @Value("${app.import.csv-file}") String csvFilePath) {
        this.employeeService = employeeService;
        this.csvFilePath = csvFilePath;
    }

    public ImportSummary importFromCsv() {
        List<String> errors = new ArrayList<>();
        int imported = 0;

        try {
            File file = new ClassPathResource(csvFilePath).getFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber == 1 || line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    if (parts.length < 6) {
                        errors.add("Line " + lineNumber + ": Invalid column count");
                        continue;
                    }

                    try {
                        String firstName = parts[0].trim();
                        String lastName = parts[1].trim();
                        String email = parts[2].trim();
                        String company = parts[3].trim();
                        String positionStr = parts[4].trim().toUpperCase();
                        int salary = Integer.parseInt(parts[5].trim());

                        Position position = Position.valueOf(positionStr);

                        if (salary <= 0)
                            throw new InvalidDataException("Salary must be positive");

                        Employee employee = new Employee(
                                firstName + " " + lastName,
                                email, company, position, salary
                        );
                        employeeService.addEmployee(employee);
                        imported++;

                    } catch (IllegalArgumentException | InvalidDataException e) {
                        errors.add("Line " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            errors.add("File error: " + e.getMessage());
        }

        return new ImportSummary(imported, errors);
    }
}