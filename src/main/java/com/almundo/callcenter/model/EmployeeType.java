package com.almundo.callcenter.model;

/**
 * Model representing Employee Type, as there is a fixed number of  types it's declared as an enum
 */
public enum EmployeeType {
    OPERADOR("Operador"),
    SUPERVISOR("Supervisor"),
    DIRECTOR("Director");

    private String type;

    EmployeeType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
