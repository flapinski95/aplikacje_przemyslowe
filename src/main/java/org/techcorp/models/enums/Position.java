package org.techcorp.models.enums;
public enum Position {
    PREZES(25000, 1),
    WICEPREZES(18000, 2),
    MANAGER(12000, 3),
    PROGRAMISTA(8000, 4),
    STAZYSTA(3000, 5);

    private final int baseSalary;
    private final int level;

    Position(int baseSalary, int level) {
        this.baseSalary = baseSalary;
        this.level = level;
    }

    public int getBaseSalary() {
        return baseSalary;
    }

    public int getLevel() {
        return level;
    }
}