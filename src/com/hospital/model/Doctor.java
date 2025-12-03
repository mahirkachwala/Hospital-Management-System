package com.hospital.model;

public class Doctor extends Person {
    private String specialization;
    private String department;

    public Doctor(String id, String name, String contactNumber, String specialization, String department) {
        super(id, name, contactNumber);
        this.specialization = specialization;
        this.department = department;
    }

    public String getSpecialization() { return specialization; }
    public String getDepartment() { return department; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return super.toString() + ", Specialization: " + specialization + ", Department: " + department;
    }

    @Override
    public String toFileString() {
        return String.join(",", id, name, contactNumber, specialization, department);
    }

    public static Doctor fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length == 5) {
            return new Doctor(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        System.err.println("Error parsing doctor from string: " + fileString);
        return null;
    }
}