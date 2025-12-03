package com.hospital.model;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String id;
    protected String name;
    protected String contactNumber;

    public Person(String id, String name, String contactNumber) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }

    public void setName(String name) { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Contact: " + contactNumber;
    }

    public abstract String toFileString();
}