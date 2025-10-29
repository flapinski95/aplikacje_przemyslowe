package org.techcorp.models;

public class CompanyStatistics {
    private final long employeeCount;
    private final double averageSalary;
    private final String topEarner;

    public CompanyStatistics(long employeeCount, double averageSalary, String topEarner) {
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.topEarner = topEarner;
    }

    public long getEmployeeCount() { return employeeCount; }
    public double getAverageSalary() { return averageSalary; }
    public String getTopEarner() { return topEarner; }

    @Override
    public String toString() {
        return "CompanyStatistics{" +
                "employeeCount=" + employeeCount +
                ", averageSalary=" + averageSalary +
                ", topEarner='" + topEarner + '\'' +
                '}';
    }
}