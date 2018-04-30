package com.almundo.callcenter.model;

/**
 * Model representing Employees
 */
public class Employee {

    private String id;
    private EmployeeType type;
    private boolean busy;

    public Employee() {
    }

    public Employee(String id, EmployeeType type) {
        this.id = id;
        this.type = type;
    }

    public Employee(String id, EmployeeType type, boolean busy) {
        this.id = id;
        this.type = type;
        this.busy = busy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType tipo) {
        this.type = tipo;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
