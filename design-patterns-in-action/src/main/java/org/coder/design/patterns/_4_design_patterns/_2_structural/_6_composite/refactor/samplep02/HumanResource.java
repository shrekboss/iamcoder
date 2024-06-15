package org.coder.design.patterns._4_design_patterns._2_structural._6_composite.refactor.samplep02;


public abstract class HumanResource {
    protected long id;
    protected double salary;

    public HumanResource(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract double calculateSalary();
}


