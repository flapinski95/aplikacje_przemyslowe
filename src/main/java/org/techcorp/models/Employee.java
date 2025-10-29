package org.techcorp.models;

import org.techcorp.models.enums.Position;
import java.util.Objects;

public class Employee {
    private final String fullName;
    private final String email;
    private final String company;
    private final Position position;
    private final int salary;

    public Employee(String fullName, String email, String company, Position position, int salary) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");
        if (position == null)
            throw new IllegalArgumentException("Position cannot be null");
        if (salary < position.getBaseSalary())
            throw new IllegalArgumentException("Salary cannot be lower than base salary for position: " + position.name());

        this.fullName = fullName;
        this.email = email.trim().toLowerCase();
        this.company = company;
        this.position = position;
        this.salary = salary;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public Position getPosition() {
        return position;
    }

    public int getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee that = (Employee) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) – %s, %s, %d PLN",
                fullName, email, company, position.name(), salary);
    }
}