package com.hospital.model;

public class Patient extends Person {
    private int age;
    private String gender;
    private String address;

    public Patient(String id, String name, String contactNumber, int age, String gender, String address) {
        super(id, name, contactNumber);
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }

    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return super.toString() + ", Age: " + age + ", Gender: " + gender + ", Address: " + address;
    }

    @Override
    public String toFileString() {
        return String.join(",", id, name, contactNumber, String.valueOf(age), gender, address);
    }

    public static Patient fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length == 6) {
            try {
                return new Patient(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing patient age from string: " + fileString + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Error parsing patient from string due to incorrect parts: " + fileString);
        return null;
    }
}