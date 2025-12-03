package com.hospital.model;

public class User {
    private String username;
    private String password; // In a real system, this should be a hashed password.
    private Role role;
    private String entityId; // Optional: Link to Doctor ID if role is DOCTOR

    public User(String username, String password, Role role, String entityId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.entityId = entityId; // Can be null for staff
    }

    public User(String username, String password, Role role) {
        this(username, password, role, null);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEntityId() { return entityId; }

    @Override
    public String toString() {
        return "User[Username: " + username + ", Role: " + role +
               (entityId != null && !entityId.equals("null") ? ", EntityID: " + entityId : "") + "]";
    }

    public String toFileString() {
        return String.join(",", username, password, role.name(), entityId == null ? "null" : entityId);
    }

    public static User fromFileString(String fileString) {
        String[] parts = fileString.split(",", -1);
        if (parts.length == 4) {
            try {
                String entityId = parts[3].equals("null") ? null : parts[3];
                return new User(parts[0], parts[1], Role.valueOf(parts[2].toUpperCase()), entityId);
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing user role from string: " + fileString + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Error parsing user from string due to incorrect parts: " + fileString);
        return null;
    }
}