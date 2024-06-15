package org.coder.design.patterns._4_design_patterns._2_structural._6_composite.refactor.samplep02;

public class Employee extends HumanResource {
    public Employee(long id, double salary) {
        super(id);
        this.salary = salary;
    }

    @Override
    public double calculateSalary() {
        return salary;
    }
}
